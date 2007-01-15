/*
 * Java SMPP API
 * Copyright (C) 1998 - 2007 by Oran Kelly
 */

#include <sys/types.h>
#include <sys/socket.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <pthread.h>

#include <netinet/in.h>


#include "stress_recv.h"


pthread_t sender_thread;
pthread_t recv_thread;
pthread_mutex_t write_lock = PTHREAD_MUTEX_INITIALIZER;
int unbinding = 0;

void *deliver_msgs(void *arg);
void *dump_incoming(void *arg);


void make_random_msg(unsigned char *len, char *buf);
void make_random_dest(char *dest);
void *dump_incoming(void *arg);
int read_smpp_packet(int sock, char *buf, int *buf_size);
void make_bind_receiver(char *buf, bind_receiver *r);
void make_deliver_sm(char *buf, deliver_sm *d);
void usage(int argc, char *argv[]);

/*
 * Stucture for arguments to send to the deliver_msgs function.
 */
struct thread_args1
{
	int sock;
	uint32_t msg_count;
};


int main(int argc, char *argv[])
{
	int server_sock = -1, sock = -1;
	uint32_t msg_count = DEFAULT_MSG_COUNT;
	int pak_size = 512, i;
	int listen_port = DEFAULT_LISTEN_PORT;
	char *pak;
	smpp_header *hdr;
	bind_receiver bind_r;
	bind_receiver_resp bind_resp;
	void *thread_return;
	struct thread_args1 targs;

	struct sockaddr_in server_addr;
	struct sockaddr_in remote_addr;
	struct sockaddr *saddr;
	int remote_addr_sz;

	remote_addr_sz = sizeof (remote_addr);


	/*
	 * Parse arguments
	 */
	for (i = 1; i < argc; i++) {
		if (!strncmp(argv[i], "-c", 2))
			msg_count = (uint32_t)strtoul(argv[++i], NULL, 10);
		else if (!strncmp(argv[i], "-p", 2))
			listen_port = (int)strtol(argv[++i], NULL, 10);
		else if (!strncmp(argv[i], "-h", 2)) {
			usage(argc, argv);
			exit (0);
		}
	}


	if (msg_count == 0) {
		fprintf(stderr, "stress_recv: Bad message count.\n");
		exit (1);
	}

	/* Get a buffer for reading packets into.. */
	pak = malloc(512);
	if (pak == NULL) {
		perror("malloc");
		exit (1);
	}


	/* Create a listener socket */
	if ((server_sock = socket(PF_INET, SOCK_STREAM, 0)) == -1) {
		perror("socket");
		exit(1);
	}

	server_addr.sin_family = AF_INET;
	server_addr.sin_addr.s_addr = INADDR_ANY;
	server_addr.sin_port = htons(listen_port);


	saddr = (struct sockaddr *)&server_addr;
	if (bind(server_sock, saddr, sizeof (server_addr)) != 0) {
		perror("bind");
		close(server_sock);
		exit(1);
	}

	if (listen(server_sock, 1) != 0) {
		perror("listen");
		close(server_sock);
		exit(1);
	}

	printf("Server waiting for connection on port %d...\n",
			ntohs(server_addr.sin_port));

	saddr = (struct sockaddr *)&remote_addr;
	if ((sock = accept(server_sock, saddr, &remote_addr_sz)) == -1) {
		perror("accept");
		close(server_sock);
		exit(1);
	}

	printf("Connection from %s:%d\n",
			inet_ntoa(remote_addr.sin_addr.s_addr),
			ntohs(remote_addr.sin_port));

	/* Finished with the server socket... */
	shutdown(server_sock, 2);
	close(server_sock);


	/* Get a (hopefully) bind receiver packet */
	read_smpp_packet(sock, pak, &pak_size);

	hdr = (smpp_header *)pak;
	if (ntohl(hdr->cmd_id) != BIND_RECV) {
		fprintf(stderr, "stress_recv: bind is not a receiver packet\n");
		close(sock);
		exit(1);
	}

	make_bind_receiver(pak, &bind_r);
	free(pak);

	printf("Receiver bound:\n");
	printf("    ID: %s\n    Type: %s\n    if_ver: %x\n",
			bind_r.sys_id,
			bind_r.sys_type,
			bind_r.interface_version);

	bind_resp.cmd_id = htonl(BIND_RECV_RESP);
	bind_resp.cmd_status = 0;
	bind_resp.seq_num = htonl(bind_r.seq_num);
	memcpy(bind_resp.sys_id, SMSC_ID, strlen(SMSC_ID) + 1);
	bind_resp.cmd_len = htonl(16 + strlen(SMSC_ID) + 1);

	/* Send the bind_recevier response */
	if (write(sock, (void *)&bind_resp, ntohl(bind_resp.cmd_len)) == -1) {
		perror("write");
		close(sock);
		exit (1);
	}

	/* Run the sender thread */
	printf("Starting threads...\n");

	targs.sock = sock;
	targs.msg_count = msg_count;
	pthread_create(&sender_thread, NULL, deliver_msgs,
			(void *)&targs);
	pthread_create(&recv_thread, NULL, dump_incoming, (void *)sock);
	 
	pthread_join(sender_thread, &thread_return);
	pthread_join(recv_thread, &thread_return);

	pthread_mutex_destroy(&write_lock);

	printf("Both threads exited normally.\n"
			"Number of deliver_sm responses received: "
			"%u\n", thread_return);

	shutdown(sock, 0);
	sleep(1);

	close(sock);
	return (0);
}


/*
 * Delivery thread function.
 */
void *deliver_msgs(void *arg)
{
	struct thread_args1 *targs = (struct thread_args1 *)arg;
	char *dm, *dest, *msg;
	unsigned char *sm_len;
	smpp_header *hdr, unbind;
	int len;
	uint32_t seq = 1;
	time_t start, end;

	/* 37 + message length. Addresses MUST be 11 chars + nul. */

	dm = malloc(224);
	if (dm == NULL) {
		perror("deliver_msgs");
		pthread_exit((void *)-1);
	}

	memset(dm, 0, 200);

	/* Set some handy pointers.. */
	hdr = (smpp_header *)dm;
	sm_len = (unsigned char *)(dm + 56);
	dest = dm + 32;
	msg = dm + 57;

	/* Set the header */
	hdr->cmd_id = htonl(DELIVER_SM);

	/* set the source address.. */
	memcpy(dm + 19, "353861234567", 13);


	printf("[send] Attempting to send %d messages.\n",
			targs->msg_count);

	start = time(NULL);

	/*
	 * The loop to dump a shit load of messages down the network..
	 */
	pthread_mutex_lock(&write_lock);
	for (seq = 1; seq <= targs->msg_count; seq++) {
		if (unbinding)
			break;

		hdr->seq_num = htonl(seq);
		make_random_msg(sm_len, msg);
		make_random_dest(dest);
		len = 57 + (int)*sm_len;
		hdr->cmd_len = htonl(len);

		if (write(targs->sock, dm, len) == -1) {
			perror("[send],write");
			break;
		}

		if ((seq % 1000) == 0)
			printf("%u\n", seq);
	}
	pthread_mutex_unlock(&write_lock);

	end = time(NULL);
	printf("[send] Sent %d deliver_sm's.\n"
			"    Start time: %lu\n"
			"    End time: %lu\n"
			"    Total elapsed: %lu seconds\n",
			seq - 1, start, end, (end - start));

	free(dm);

	/* Send an unbind, if needed */
	if (!unbinding) {
		unbind.cmd_len = htonl(16);
		unbind.cmd_id = htonl(UNBIND);
		unbind.cmd_status = 0;
		unbind.seq_num = htonl(seq);

		if (write(targs->sock, &unbind, 16) == -1)
			fprintf(stderr, "[send] deliver_msgs: error sending "
					"unbind!");
	}


	pthread_exit((void *)0);
}


/*
 * Make a message of random length. The message will always be between 1 and 160
 * bytes, the actual length returned in len.
 */
void make_random_msg(unsigned char *len, char *buf)
{
	int size, i, r;

	size = (random() % 130) + 1;
	*len = (unsigned char)size;
	for (i = 0; i <= size; i++) {
		r = random() % 57;
		if (r > 25 && r < 32) {
			if (r < 29)
				r = 25;
			else
				r = 32;
		}

		*(buf++) = (char)(r + 65);
	}
}


/*
 * Make a random destination address. The first 2 bytes are the ton and npi.
 * This function will always generate a 12-byte dest address followed by 1 null
 * byte.
 */
void make_random_dest(char *dest)
{
	int i, r;

	/* Not going to bother with changing ton and npi for now! */
	dest += 2;
	for (i = 0; i < 12; i++) {
		r = random() % 9;
		*(dest++) = (char)(r + 48);
	}
	*(dest + 12) = '\0';
}


/*
 * We don't do anything in particular with the incoming packets except monitor
 * for unbinding and count the number of deliver_sm responses.
 */
void *dump_incoming(void *arg)
{
	int sock = (int)arg;
	int buf_sz = 1000;
	unsigned int cmd = 0, responses = 0;
	char *buf;
	smpp_header *hdr;
	smpp_header unbindr;

	buf = malloc(buf_sz);
	if (buf == NULL) {
		fprintf(stderr, "[recv]: failed to allocate "
				" a buffer.\n");
		pthread_exit((void *)-1);
	}

	hdr = (smpp_header *)buf;
	while (1) {
		if (read_smpp_packet(sock, buf, &buf_sz) == -1)
			break;

		cmd = ntohl(hdr->cmd_id);

		if (cmd == UNBIND) {
			unbinding = 1;
			pthread_mutex_lock(&write_lock);
			printf("[recv] Unbind request received. Sending "
					"response.\n");
			unbindr.cmd_len = htonl(16);
			unbindr.cmd_id = htonl(UNBIND_RESP);
			unbindr.cmd_status = 0;
			unbindr.seq_num = hdr->seq_num;
			if (write(sock, &unbindr, 16) == -1) {
				fprintf(stderr, "[recv] Error writing"
						" unbind response.\n");
			}
			pthread_mutex_unlock(&write_lock);
			break;
		} else if (cmd == UNBIND_RESP) {
			printf("[recv] Unbind response received.\n");
			break;
		} else if (cmd == DELIVER_SM_RESP) {
			++responses;
		} else {
			printf("[recv] (SMPP Packet received, ID = 0x%x\n)",
					cmd);
		}
	}

	pthread_exit((void *)responses);
}


/*
 * Read an SMPP packet in it's entirety from the input stream.
 */
int read_smpp_packet(int sock, char *buf, int *buf_size)
{
	uint32_t len;
	int p, c = 0;
	char *newbuf;
	smpp_header *hdr = (smpp_header *)buf;

	for (p = 0; p < 4; p += c) {
		c = read(sock, buf + p, 4 - p);
		if (c == -1)
			goto error;
	}

	len = (uint32_t)ntohl(hdr->cmd_len);

	if (len > *buf_size) {
		/* Realloc the buffer.. */
		fprintf(stderr, "    (reallocing read buffer..)\n");
		newbuf = realloc(buf, len);
		if (newbuf == buf)
			goto error;

		buf = newbuf;
		*buf_size = len;
		hdr = (smpp_header *)buf;
	}

	for (p = 4; p < len; p += c) {
		c = read(sock, buf + p, len - p);
		if (c == -1)
			goto error;
	}

	return (len);

error:
	perror("read_smpp_packet");
	return (-1);
}


void make_bind_receiver(char *buf, bind_receiver *r)
{
	int len = 0;
	smpp_header *hdr = (smpp_header *)buf;

	r->cmd_len = (uint32_t)ntohl(hdr->cmd_len);
	r->cmd_id = (uint32_t)ntohl(hdr->cmd_id);
	r->cmd_status = (uint32_t)ntohl(hdr->cmd_status);
	r->seq_num = (uint32_t)ntohl(hdr->seq_num);

	buf += 16;
	len = strlen(buf) + 1;
	memcpy(r->sys_id, buf, len);

	buf += len;
	len = strlen(buf) + 1;
	memcpy(r->password, buf, len);

	buf += len;
	len = strlen(buf) + 1;
	memcpy(r->sys_type, buf, len);

	buf += len;
	r->interface_version = *(buf++);
	r->addr_ton = *(buf++);
	r->addr_npi = *(buf++);
	
	len = strlen(buf) + 1;
	memcpy(r->addr_range, buf, len);
}

void make_deliver_sm(char *buf, deliver_sm *d)
{
	int len = 0;

	d->cmd_len = (uint32_t)ntohl(*((int *)buf));
	d->cmd_id = (uint32_t)ntohl(*((int *)(buf + 4)));
	d->cmd_status = (uint32_t)ntohl(*((int *)(buf + 8)));
	d->seq_num = (uint32_t)ntohl(*((int *)(buf + 12)));

	buf += 16;
	len = strlen(buf) + 1;
	memcpy(d->service_type, buf, len);

	buf += len;
	d->source_ton = *(buf++);
	d->source_npi = *(buf++);

	len = strlen(buf) + 1;
	memcpy(d->source_addr, buf, len);

	buf += len;
	d->dest_ton = *(buf++);
	d->dest_npi = *(buf++);

	len = strlen(buf) + 1;
	memcpy(d->dest_addr, buf, len);

	buf += len;
	d->esm_class = *(buf++);
	d->protocol_id = *(buf++);
	d->priority = *(buf++);

	len = strlen(buf) + 1;
	memcpy(d->delivery_time, buf, len);

	buf += len;
	len = strlen(buf) + 1;
	memcpy(d->expiry_time, buf, len);

	buf += len;
	d->registered = *(buf++);
	d->replace_if_present = *(buf++);
	d->data_coding = *(buf++);
	d->default_msg_id = *(buf++);
	d->sm_length = *(buf++);
	
	memcpy(d->message, buf, d->sm_length);
}

void usage(int argc, char *argv[])
{
	printf("Usage:\n\
%s: [-c <count>] [-p <port>]\n\
    -c  Send <count> deliver_sm packets to the receiver.\n\
    -p  Listen for connections on TCP port <port>.\n", argv[0]);
}
