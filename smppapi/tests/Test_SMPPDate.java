
import java.util.Date;

import ie.omk.smpp.util.SMPPDate;

public class Test_SMPPDate
{
    public static void main(String[] args)
    {
	try {
	    for (int loop = 0; loop < 40; loop++) {
		Date d = new Date();
		SMPPDate d1 = new SMPPDate(d);
		SMPPDate d2 = new SMPPDate(d1.toString());

		System.out.println(
			Long.toString(d.getTime())
			+ ", "
			+ Long.toString(d2.getDate().getTime())
			+ "\n"
			+ d1.toString()
			+ ", "
			+ d2.toString()
			+ "\n--");
	    }
	} catch (Exception x) {
	    x.printStackTrace();
	}
    }
}
