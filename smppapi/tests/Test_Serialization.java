
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import java.util.Date;

import ie.omk.smpp.MessageDetails;
import ie.omk.smpp.message.MsgFlags;
import ie.omk.smpp.message.SmeAddress;
import ie.omk.smpp.util.GSMConstants;
import ie.omk.smpp.util.SMPPDate;

public class Test_Serialization
{
    public static void main(String[] args)
    {
	try {
	    SMPPDate d1 = new SMPPDate(new Date());
	    ByteArrayOutputStream bo = new ByteArrayOutputStream();
	    ObjectOutputStream out = new ObjectOutputStream(bo);
	    out.writeObject(d1);

	    ByteArrayInputStream bi = new
		ByteArrayInputStream(bo.toByteArray());
	    ObjectInputStream in = new ObjectInputStream(bi);
	    SMPPDate d2 = (SMPPDate)in.readObject();

	    System.out.println("Date 1: " + d1.toString());
	    System.out.println("Date 2: " + d2.toString());


	    MsgFlags mf1 = new MsgFlags();
	    mf1.priority = true;
	    mf1.data_coding = 561;
	    mf1.default_msg = 45;
	    bo = new ByteArrayOutputStream();
	    out = new ObjectOutputStream(bo);
	    out.writeObject(mf1);

	    bi = new ByteArrayInputStream(bo.toByteArray());
	    in = new ObjectInputStream(bi);
	    MsgFlags mf2 = (MsgFlags)in.readObject();

	    System.out.println("\n\nMsgFlags 1: " + showFlags(mf1));
	    System.out.println("MsgFlags 2: " + showFlags(mf2));

	    SmeAddress sme1 = new SmeAddress(
		    GSMConstants.GSM_TON_INTERNATIONAL,
		    GSMConstants.GSM_NPI_UNKNOWN,
		    "8912883802395");
	    bo = new ByteArrayOutputStream();
	    out = new ObjectOutputStream(bo);
	    out.writeObject(sme1);

	    bi = new ByteArrayInputStream(bo.toByteArray());
	    in = new ObjectInputStream(bi);
	    SmeAddress sme2 = (SmeAddress)in.readObject();

	    System.out.println("\n\nSmeAddress 1: " + sme1.toString());
	    System.out.println("SmeAddress 2: " + sme2.toString());

	    sme1 = new SmeAddress("distList");
	    bo = new ByteArrayOutputStream();
	    out = new ObjectOutputStream(bo);
	    out.writeObject(sme1);

	    bi = new ByteArrayInputStream(bo.toByteArray());
	    in = new ObjectInputStream(bi);
	    sme2 = (SmeAddress)in.readObject();

	    System.out.println("\n\nDistList 1: " + sme1.toString());
	    System.out.println("DistList 2: " + sme2.toString());
	} catch (Exception x) {
	    x.printStackTrace(System.err);
	}
    }


    private static String showFlags(MsgFlags mf)
    {
	StringBuffer s = new StringBuffer("Flags(");
	if (mf.priority)
	    s.append("priority, ");
	if (mf.registered)
	    s.append("registered, ");
	if (mf.replace_if_present)
	    s.append("replace, ");
	s.append("esmClass = " + mf.esm_class + ", ");
	s.append("data_coding = " + mf.data_coding + ", ");
	s.append("default msg = " + mf.default_msg);
	return (s.toString());
    }
}
