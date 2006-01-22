package ie.omk.smpp.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class TestAPIConfig extends TestCase {

    public TestAPIConfig(String name) {
        super(name);
    }

    public void testAPIConfig() throws Exception {
        // The test values should match those in the example properties file in
        // the CVS resources directory.
        APIConfig c = APIConfig.getInstance();

        int propCount = 0;
        Class apiClass = c.getClass();
        Field[] f = apiClass.getFields();
        for (int i = 0; i < f.length; i++) {
            int mod = f[i].getModifiers();
            if (Modifier.isPublic(mod) && Modifier.isStatic(mod)
                    && Modifier.isFinal(mod)
                    && f[i].getType() == String.class) {

                try {
                    String fn = f[i].get(c).toString();
                    c.getProperty(fn);
                    propCount++;
               } catch (PropertyNotFoundException x) {
               }
            }
        }

        if (propCount < 1) {
            // Probably no properties file.
            //pass("No properties were loaded. Maybe no props file
            // found?");
        }
    }
}
