/*
 * Java implementation of the SMPP v3.3 API
 * Copyright (C) 1998 - 2000 by Oran Kelly
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
 * Java SMPP API author: oran.kelly@ireland.com
 */
package ie.omk.debug;

public class Debug
{
    private static int			debugLevel = 0;
    public static int			DBG_0 = 0;
    public static int			DBG_1 = 1;
    public static int			DBG_2 = 2;
    public static int			DBG_3 = 3;
    public static int			DBG_4 = 4;
    public static int			DBG_5 = 5;
    //	private static String		tabs = "\t\t\t\t\t\t\t\t\t\t\t";
    private static String		tabs = "|---------------------";

    public static boolean		dbg = false;

    private Debug() { }
    public static void setDebugLevel(int l)
    { 
	if(l <= 0) {
	    debugLevel = DBG_0;
	    dbg = false;
	} else if(l > DBG_5) {
	    debugLevel = DBG_5;
	    dbg = true;
	} else {
	    debugLevel = l;
	    dbg = true;
	}

	System.out.println("Debugger@setDebugLevel: Debug level set to "
		+ debugLevel);
    }

    public static void d(Object classt, String method, String s, int level)
    {
	String classFile = null;

	if(debugLevel == 0 || level < debugLevel)
	    return;

	if(classt == null)
	    classFile = "<null>";
	else if (classt instanceof java.lang.Class)
	    classFile = ((java.lang.Class)classt).getName();
	else
	    classFile = classt.getClass().getName();

	System.out.print("#"+debugLevel+tabs.substring(0, debugLevel));
	System.out.println(classFile
		+ "@"
		+ method
		+ ": "
		+ s);
    }
    /*
       public static void d(String classFile, String method, String s, int level)
       {
       if(debugLevel == 0 || level < debugLevel) return;

       System.out.print("#"+debugLevel+tabs.substring(0, debugLevel));
       System.out.println(classFile + "@" + method + ": " +s);
       }
     */
    public static void d(Object cf, String met, boolean b, int level)
    {
	d(cf, met, String.valueOf(b), level);
    }
    public static void d(Object cf, String met, int b, int level)
    {
	d(cf, met, String.valueOf(b), level);
    }
    public static void d(Object cf, String met, double b, int level)
    {
	d(cf, met, String.valueOf(b), level);
    }
}

