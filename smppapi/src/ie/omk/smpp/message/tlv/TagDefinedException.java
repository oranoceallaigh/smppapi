/*
 * Java SMPP API
 * Copyright (C) 1998 - 2002 by Oran Kelly
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
 *
 * $Id$
 */
package ie.omk.smpp.message.tlv;

/**
 * Tag has already been defined. This exception is thrown by
 * {@link Tag#defineTag}if an attempt is made to redefine a tag which already
 * has a definition.
 * 
 * @author Oran Kelly &lt;orank@users.sf.net&gt;
 */
public class TagDefinedException extends java.lang.RuntimeException {
    /**
     * The value of the tag that was attempted to be redefined.
     */
    private int tagValue = -1;

    /**
     * Create a new TagDefinedException.
     * 
     * @param tagValue
     *            The tag for which an attempt as made to redefine.
     */
    public TagDefinedException(int tagValue) {
        this.tagValue = tagValue;
    }

    /**
     * Create a new TagDefinedException.
     * 
     * @param tagValue
     *            The tag for which an attempt as made to redefine.
     * @param msg
     *            The exception message.
     */
    public TagDefinedException(int tagValue, String msg) {
        super(msg);
        this.tagValue = tagValue;
    }

    /**
     * Get the tag for which an attempt as made to redefine.
     * 
     * @return The tag for which an attempt as made to redefine.
     */
    public int getTagValue() {
        return (tagValue);
    }
}