
public class TestUtils
{
    public static String showBytes(byte[] b)
    {
	java.io.StringWriter sout = new java.io.StringWriter();
	java.io.PrintWriter o = new java.io.PrintWriter(sout);

	o.print("{");
	for (int l = 0; l < b.length; l++) {
	    o.print(Integer.toHexString((int)b[l] & 0x00ff));
	    if (((l + 1) % b.length) != 0)
		o.print(", ");
	}
	o.print("}");
	return (sout.toString());
    }
}
