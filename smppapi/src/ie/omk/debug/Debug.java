/*
 * Java SMPP API
 * Copyright (C) 1998 - 2001 by Oran Kelly
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * A copy of the LGPL can be viewed at http://www.gnu.org/copyleft/lesser.html
 * Java SMPP API author: orank@users.sf.net
 * Java SMPP API Homepage: http://smppapi.sourceforge.net/
 */
package ie.omk.debug;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import java.util.Properties;

import ie.omk.smpp.message.SMPPPacket;

/** Debugging helper.
  * By default, this class has everything turned off. It can be configured
  * manually by an application by calling the various methods, or it can be
  * configured at run time using a properties file. To configure using a
  * properties file, set a system property on the command line called
  * 'smppapi.debug' and give it a value of a file to load. For example:<br>
  * <code>java -Dsmppapi.debug=./smppdebug.properties ie.omk.smpp...</code><br>
  * The following properties are read:
  * <table cols="3" border="1" width="60%">
  *   <tr>
  *     <td><b>Property name</b></td>
  *     <td><b>Type</b></td>
  *     <td><b>Description</b></td>
  *   </tr>
  *
  *   <tr>
  *     <td>debug.info</td>
  *     <td>boolean</td>
  *     <td>Turn on information messages</td>
  *   </tr>
  *
  *   <tr>
  *     <td>debug.warn</td>
  *     <td>boolean</td>
  *     <td>Turn on warning messages</td>
  *   </tr>
  *
  *   <tr>
  *     <td>debug.level</td>
  *     <td>integer</td>
  *     <td>Turn on debug messages at this level</td>
  *   </tr>
  *
  *   <tr>
  *     <td>debug.stdout</td>
  *     <td>boolean</td>
  *     <td>By default, messages are send to System.err. Set this to true
  *         to cause messages to be sent to System.out.</td>
  *   </tr>
  * </table>
  */
public class Debug
{
    private static ByteArrayOutputStream inhelp = null;
    private static ByteArrayOutputStream outhelp = null;

    private static PrintStream		out = System.err;

    private static int			debugLevel = 0;

    private static boolean		infoOn = false;
    private static boolean		warning = false;

    // Static initializer...see if there's a properties file!
    static {
	String filename = System.getProperty("smppapi.debug");
	if (filename != null) {
	    try {
		Properties props = new Properties();
		props.load(new FileInputStream(filename));

		infoOn = new Boolean(
			props.getProperty("debug.info")).booleanValue();
		warning = new Boolean(
			props.getProperty("debug.warn")).booleanValue();
		int l = Integer.parseInt(props.getProperty("debug.level"));
		if (new Boolean(
			    props.getProperty("debug.stdout")).booleanValue())
		    out = System.out;

		setDebugLevel(l);
	    } catch (IOException x) {
	    }
	}
    }

    private Debug() { }

    public static void setInfo(boolean info)
    {
	infoOn = info;
    }

    public static void setWarning(boolean warning)
    {
	warning = warning;
    }

    public static void setDebugLevel(int level)
    { 
	debugLevel = level;
	if (debugLevel < 0)
	    debugLevel = 0;

	Debug.info(Debug.class, "setDebugLevel",
		Integer.toString(debugLevel));
    }

    public static void info(Object type, String method, String msg)
    {
	if (infoOn)
	    print(type, method, msg, "Info");
    }

    public static void warn(Object type, String method, String msg)
    {
	if (warning)
	    print(type, method, msg, "Warning");
    }

    public static void d(Object classt, String method, Object s, int level)
    {
	String cn;

	String msg = (s == null) ? "null" : s.toString();
	if (debugLevel >= level)
	    print(classt, method, msg, "Debug" + level);
    }


    public static void d(Object type, String method, boolean p, int level)
    {
	d(type, method, new Boolean(p), level);
    }

    public static void d(Object type, String method, short p, int level)
    {
	d(type, method, new Short(p), level);
    }

    public static void d(Object type, String method, int p, int level)
    {
	d(type, method, new Integer(p), level);
    }

    public static void d(Object type, String method, long p, int level)
    {
	d(type, method, new Long(p), level);
    }

    public static void d(Object type, String method, float p, int level)
    {
	d(type, method, new Float(p), level);
    }

    public static void d(Object type, String method, double p, int level)
    {
	d(type, method, new Double(p), level);
    }


    private static void print(Object type, String method, String msg, String s)
    {
	if (!(type instanceof Class))
	    type = type.getClass();

	String cn = ((Class)type).getName();
	cn = cn.substring(((Class)type).getPackage().getName().length() + 1);

	out.println(s + ":" + cn + "." + method + ":" + msg);
    }
}
