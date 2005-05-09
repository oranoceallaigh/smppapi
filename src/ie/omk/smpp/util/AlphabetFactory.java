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
 * $Id$
 */

package ie.omk.smpp.util;

import org.apache.commons.logging.LogFactory;

/**
 * Factory class for getting SMS alphabet encoding objects. The API maintains
 * what it considers a 'default alphabet' for the current VM. If nothing else is
 * specified, that alphabet will be an instance of the SMS default alphabet
 * (implemented by the ie.omk.smpp.util.DefaultAlphabetExt class). Changing the
 * API's default alphabet to another is merely a case of setting the
 * "smpp.default_alphabet" System property to the name of another class, which
 * must implement the ie.omk.smpp.util.SMSAlphabet interface.
 */
public final class AlphabetFactory {
    private static AlphabetEncoding defaultAlphabet = null;

    private static final String DEFAULT_ALPHABET_PROPNAME = "smpp.default_alphabet";

    private AlphabetFactory() {
        // AlphabetFactory.Sounds like something off Sesame Street, doesn't it?
        // ;-)
    }

    /**
     * Return the default alphabet for this runtime environment. The default
     * alphabet is usually the SMS Default alphabet
     * (ie.omk.smpp.util.DefaultAlphabetExt). This can be altered by setting the
     * <b>smpp.default_alphabet </b> system property to the name of a concrete
     * sub-class of ie.omk.smpp.util.SMSAlphabet. For example, if you have
     * written an alphabet class called 'it.smpp.MyAlphabet', then when running
     * your smppapi-based application, supply a system property using the -D
     * switch: <br>
     * <code>java -cp .:smppapi.jar -Dsmpp.default_alphabet=ie.smpp.MyAlphabet
     * ...</code>
     */
    public static AlphabetEncoding getDefaultAlphabet() {
        if (defaultAlphabet == null)
            init();

        return (defaultAlphabet);
    }

    private static final void init() {
        String className = "";
        try {
            className = System.getProperty(DEFAULT_ALPHABET_PROPNAME);
            if (className != null) {
                Class alphaClass = Class.forName(className);
                defaultAlphabet = (AlphabetEncoding) alphaClass.newInstance();
            } else {
                defaultAlphabet = DefaultAlphabetEncoding.getInstance();
            }
        } catch (Exception x) {
            // Leave the alphabet as DefaultAlphabet
            LogFactory.getLog(AlphabetFactory.class).warn(
                    "Couldn't load default alphabet " + className, x);
            defaultAlphabet = DefaultAlphabetEncoding.getInstance();
        }
    }

    /**
     * Get the SMSAlphabet needed for encoding messages in a particular
     * language. At the moment this ONLY returns an instance of DefaultAlphabet
     * seeing as that's the only alphabet implemented currently.
     * 
     * @param lang
     *            The ISO code for the language the message is in.
     */
    public static AlphabetEncoding getAlphabet(String lang) {
        // XXX need to come up with a list of mappings here.
        return (defaultAlphabet);
    }
}