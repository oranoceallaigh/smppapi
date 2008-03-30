package ie.omk;

public class TestUtils {
    private TestUtils() {
    }

    public static void displayArray(byte[] ar) {
        System.out.print("{");
        for (int i = 0; i < ar.length; i++) {
            System.out.print(" 0x" + Integer.toHexString((int) ar[i] & 0xff)
                    + ",");
        }
        System.out.print("}\n");
    }
}

