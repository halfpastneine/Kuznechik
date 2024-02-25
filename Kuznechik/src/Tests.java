import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Tests {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static int count = 0;


    public static void main(String[] args) {
        System.out.println("RUN GOST TESTS\n\n");
        sTest();
        lTest();
        rTest();
        encAndDecTests();
        System.out.printf("%n%n");
        System.out.println("RUN RANDOM TESTS\n\n");
        randomTests();
        System.out.printf("%s Tests passed : %d/14", ANSI_GREEN, count);
    }


    // GOST TESTS

    public static void sTest() {
        byte[] a = {
                (byte) 0xFF, (byte) 0xEE, (byte) 0xDD, (byte) 0xCC, (byte) 0xBB, (byte) 0xAA, (byte) 0x99, (byte) 0x88,
                (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77, (byte) 0x00
        };

        System.out.println("Test S (ГОСТ Р 34.12─2015 Приложение А.1.1)");
        System.out.println("\tInput:     " + Arrays.toString(a));

        byte[] b = Kuznechik.S(Kuznechik.S(Kuznechik.S(Kuznechik.S(a))));
        System.out.println("\tEncrypted: " + Arrays.toString(b));

        b = Kuznechik.SReverse(Kuznechik.SReverse(Kuznechik.SReverse(Kuznechik.SReverse(b))));
        System.out.println("\tDecrypted: " + Arrays.toString(b));

        checkTest(a, b, "Test S (ГОСТ Р 34.12─2015 Приложение А.1.1): ");

        System.out.println("\n");
    }

    public static void lTest() {
        byte[] a = {(byte) 0x64, (byte) 0xA5, (byte) 0x94, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        System.out.println("Test L (ГОСТ Р 34.12─2015 Приложение А.1.3)");
        System.out.println("\tInput:     " + Arrays.toString(a));

        byte[] b = Kuznechik.L(Kuznechik.L(Kuznechik.L(Kuznechik.L(a))));
        System.out.println("\tEncrypted: " + Arrays.toString(b));

        b = Kuznechik.LReverse(Kuznechik.LReverse(Kuznechik.LReverse(Kuznechik.LReverse(b))));
        System.out.println("\tDecrypted: " + Arrays.toString(b));
        checkTest(a, b, "Test L (ГОСТ Р 34.12─2015 Приложение А.1.3): ");

        System.out.println("\n");
    }


    public static void rTest() {
        byte[] a = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x01, 0};

        System.out.println("Test R (ГОСТ Р 34.12─2015 Приложение А.1.2)");
        System.out.println("\tInput:     " + Arrays.toString(a));

        byte[] b = Kuznechik.R(Kuznechik.R(Kuznechik.R(Kuznechik.R(a))));
        System.out.println("\tEncrypted: " + Arrays.toString(b));

        b = Kuznechik.RReverse(Kuznechik.RReverse(Kuznechik.RReverse(Kuznechik.RReverse(b))));
        System.out.println("\tDecrypted: " + Arrays.toString(b));

        checkTest(a, b, "Test R (ГОСТ Р 34.12─2015 Приложение А.1.2): ");

        System.out.println("\n");
    }


    public static void encAndDecTests() {
        byte[][] key = {
                {
                        (byte) 0x88, (byte) 0x99, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, (byte) 0xFF,
                        (byte) 0x00, (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77
                },
                {
                        (byte) 0xFE, (byte) 0xDC, (byte) 0xBA, (byte) 0x98, (byte) 0x76, (byte) 0x54, (byte) 0x32, (byte) 0x10,
                        (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF
                }
        };

        byte[] plainText = {
                (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77, (byte) 0x00,
                (byte) 0xFF, (byte) 0xEE, (byte) 0xDD, (byte) 0xCC, (byte) 0xBB, (byte) 0xAA, (byte) 0x99, (byte) 0x88
        };
        byte[] test = plainText;
        Kuznechik.generateKeys(key[0], key[1]);

        System.out.println("Test encryption/decryption with no mode (ГОСТ Р 34.12─2015 Приложение А.1.5 и А.1.6): ");
        System.out.println("\tInput:     " + Arrays.toString(plainText));

        plainText = Kuznechik.encrypt(plainText);
        System.out.println("\tEncrypted: " + Arrays.toString(plainText));

        plainText = Kuznechik.decrypt(plainText);
        System.out.println("\tDecrypted: " + Arrays.toString(plainText));

        checkTest(test, plainText, "Test encryption/decryption with no mode (ГОСТ Р 34.12─2015 Приложение А.1.5 и А.1.6): ");

        System.out.println("\n");
    }

    // RANDOM TESTS

    public static void randomTests() {
        String[] tests = new String[]{
                "AD7A770D7AA8E3FE",
                "7A13051B788D568E",
                "409CD0A0BB3EE6BD",
                "ACD23AB545DC177B",
                "60AEF616C3A7DBC2",
                "00DD1C869B8F81BD",
                "4713B140449C4064",
                "9C0C624D45CEFCA5",
                "B8CAF7BF72B938B8",
                "65AC8EAC25CFD541",
        };
        int i = 1;
        for (String test : tests) {
            byte[][] key = generateRandomKeys();
            byte[] a = test.getBytes();
            byte[] b = a;
            System.out.printf("Input: %s%n", test);
            System.out.println("\tInput in bytes:     " + Arrays.toString(a));
            Kuznechik.generateKeys(key[0], key[1]);

            a = Kuznechik.encrypt(a);
            System.out.println("\tEncrypted:          " + Arrays.toString(a));
            byte[] c = a;
            a = Kuznechik.decrypt(a);
            System.out.println("\tDecrypted:          " + Arrays.toString(a));

            checkTest(a, b, String.format("Test %d/10: ", i));
            System.out.println();
            speedTest(key[0], key[1], a, c);
            System.out.println();


            i++;
        }
    }

    private static byte[][] generateRandomKeys() {
        Random random = ThreadLocalRandom.current();
        byte[][] key = new byte[2][16];
        random.nextBytes(key[0]);
        random.nextBytes(key[1]);
        return key;
    }


    private static void speedTest(byte[] K1, byte[] K2, byte[] A, byte[] B) {
        Kuznechik.generateKeys(K1, K2);
        int numIterations = 150;
        long startTime = System.currentTimeMillis();
        for (int j = 0; j < numIterations; j++) {
            for (int i = 0; i < 0x100; i += 4)
                Kuznechik.encrypt(A);
        }
        long endTime = System.currentTimeMillis();
        double durationSeconds = (endTime - startTime) / 1000.0;
        double throughputEncrypt = ((double) numIterations * 0x100 * 4) / (durationSeconds * 1024);
        System.out.printf("kuz_encrypt_block(): %.3f kB/s (n=%dkB,t=%.3fs)\n", throughputEncrypt, numIterations, durationSeconds);
        startTime = System.currentTimeMillis();
        for (int j = 0; j < numIterations; j++) {
            for (int i = 0; i < 0x100; i += 4)
                Kuznechik.decrypt(B);
        }
        endTime = System.currentTimeMillis();
        durationSeconds = (endTime - startTime) / 1000.0;
        double throughputDecrypt = ((double) numIterations * 0x100 * 4) / (durationSeconds * 1024);
        System.out.printf("kuz_decrypt_block(): %.3f kB/s (n=%dkB,t=%.3fs)\n", throughputDecrypt, numIterations, durationSeconds);

    }

    public static void checkTest(byte[] a, byte[] b, String s) {
        if (Arrays.equals(a, b)) {
            System.out.printf("%s %s %s %s", s, ANSI_GREEN, "PASSED", ANSI_RESET);
            count++;
        } else {
            System.out.printf("%s %s %s %s", s, ANSI_RED, "FAILED", ANSI_RESET);
        }
    }
}
