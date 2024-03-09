import java.util.Arrays;
import java.util.HexFormat;
import java.util.function.Function;

public class Tests {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static void main(String[] args) {
        S_Test();
        SReverse_Test();
        R_Test();
        RReverse_Test();
        L_Test();
        LReverse_Test();
        KEY_Test();
        ENC_DEC_Test();
    }

    public static void S_Test() {
        System.out.println("RUNNING S TESTS\n");
        String[] test = {"ffeeddccbbaa99881122334455667700", "b66cd8887d38e8d77765aeea0c9a7efc",
                "559d8dd7bd06cbfe7e7b262523280d39", "0c3322fed531e4630d80ef5c5a81c50b",
                "23ae65633f842d29c5df529c13f5acda"};
        check_ans(test, Kuznechik::S, "S");
    }

    public static void SReverse_Test() {
        byte[] a = {
                (byte) 0xFF, (byte) 0xEE, (byte) 0xDD, (byte) 0xCC, (byte) 0xBB, (byte) 0xAA, (byte) 0x99, (byte) 0x88,
                (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77, (byte) 0x00
        };
        helpFunc(a, Kuznechik::S, Kuznechik::SReverse, "Test S (ГОСТ Р 34.12─2015 Приложение А.1.1)");
    }

    public static void R_Test() {
        System.out.println("\nRUNNING R TESTS\n");
        String[] test = {"00000000000000000000000000000100", "94000000000000000000000000000001",
                "a5940000000000000000000000000000", "64a59400000000000000000000000000",
                "0d64a594000000000000000000000000"};
        check_ans(test, Kuznechik::R, "R");
    }

    public static void RReverse_Test() {
        byte[] a = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x01, 0};
        helpFunc(a, Kuznechik::RReverse, Kuznechik::R, "Test R (ГОСТ Р 34.12─2015 Приложение А.1.2)");
    }

    public static void L_Test() {
        System.out.println("\nRUNNING L TESTS\n");
        String[] test = {"64a59400000000000000000000000000", "d456584dd0e3e84cc3166e4b7fa2890d",
                "79d26221b87b584cd42fbc4ffea5de9a", "0e93691a0cfc60408b7b68f66b513c13",
                "e6a8094fee0aa204fd97bcb0b44b8580"};
        check_ans(test, Kuznechik::L, "L");
    }

    public static void LReverse_Test() {
        byte[] a = {(byte) 0x64, (byte) 0xA5, (byte) 0x94, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        helpFunc(a, Kuznechik::LReverse, Kuznechik::L, "Test L (ГОСТ Р 34.12─2015 Приложение А.1.3)");
    }

    public static void KEY_Test() {
        System.out.println("\nRUNNING KEY TESTS\n");
        String k1 = "8899aabbccddeeff0011223344556677";
        String k2 = "fedcba98765432100123456789abcdef";
        byte[] key1 = HexFormat.of().parseHex(k1);
        byte[] key2 = HexFormat.of().parseHex(k2);
        Kuznechik.generateKeys(key1, key2);
        String[] ans = {
                "8899aabbccddeeff0011223344556677",
                "fedcba98765432100123456789abcdef",
                "db31485315694343228d6aef8cc78c44",
                "3d4553d8e9cfec6815ebadc40a9ffd04",
                "57646468c44a5e28d3e59246f429f1ac",
                "bd079435165c6432b532e82834da581b",
                "51e640757e8745de705727265a0098b1",
                "5a7925017b9fdd3ed72a91a22286f984",
                "bb44e25378c73123a5f32f73cdb6e517",
                "72e9dd7416bcf45b755dbaa88e4a4043",
        };
        int i = 0;
        for (byte[] k : Kuznechik.keys) {
            String a = HexFormat.of().formatHex(k);
            if (a.equals(ans[i])) {
                System.out.printf("\tK%d : %s %s %s %s%n", i, ANSI_GREEN, a, "PASSED", ANSI_RESET);
            } else {
                System.out.printf("\tK%d : %s %s %s %s %s%n", i, ANSI_RED, a, "FAILED, expected", ans[i], ANSI_RESET);
            }
            i++;
        }
    }

    public static void ENC_DEC_Test() {
        System.out.println("\nRUNNING ENC&DEC TESTS\n");
        String input = "1122334455667700ffeeddccbbaa9988";
        System.out.printf("\tInput          : %s%n", input);
        byte[] key1 = HexFormat.of().parseHex("8899aabbccddeeff0011223344556677");
        byte[] key2 = HexFormat.of().parseHex("fedcba98765432100123456789abcdef");
        byte[] msg = HexFormat.of().parseHex(input);
        String encAns = "7f679d90bebc24305a468d42b9d4edcd";
        Kuznechik.generateKeys(key1, key2);
        byte[] tmp = Kuznechik.encrypt(msg);
        String ans = HexFormat.of().formatHex(tmp);
        if (ans.equals(encAns)) {
            System.out.printf("\tEncrypt result : %s %s %s %s%n", ANSI_GREEN, ans, "PASSED", ANSI_RESET);
        } else {
            System.out.printf("\tEncrypt result : %s %s %s %s %s%n", ANSI_RED, ans, "FAILED, expected", encAns, ANSI_RESET);
        }
        ans = HexFormat.of().formatHex(Kuznechik.decrypt(tmp));
        if (ans.equals(input)) {
            System.out.printf("\tDecrypt result : %s %s %s %s%n", ANSI_GREEN, ans, "PASSED", ANSI_RESET);
        } else {
            System.out.printf("\tDecrypt result : %s %s %s %s %s%n", ANSI_RED, ans, "FAILED, expected", encAns, ANSI_RESET);
        }
        speedTest(key1, key2, msg, tmp);
    }

    public static void check_ans(String[] test, Function<byte[], byte[]> function, String s) {
        for (int i = 0; i < test.length - 1; i++) {
            String ans = HexFormat.of().formatHex(function.apply(HexFormat.of().parseHex(test[i])));
            String a;
            if (ans.equals(test[i + 1])) {
                a = String.format("%s PASSED %s", ANSI_GREEN, ANSI_RESET);
            } else {
                a = String.format("%s FAILED, expected %s %s", ANSI_RED, test[i + 1], ANSI_RESET);
            }
            System.out.printf("\t%s(%s) = %s %s%n", s, test[i], ans, a);
        }
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
        System.out.println();
        System.out.printf("\tEncrypt speed  : %.3f kB/s (n=%dkB,t=%.3fs)\n", throughputEncrypt, numIterations, durationSeconds);
        startTime = System.currentTimeMillis();
        for (int j = 0; j < numIterations; j++) {
            for (int i = 0; i < 0x100; i += 4)
                Kuznechik.decrypt(B);
        }
        endTime = System.currentTimeMillis();
        durationSeconds = (endTime - startTime) / 1000.0;
        double throughputDecrypt = ((double) numIterations * 0x100 * 4) / (durationSeconds * 1024);
        System.out.printf("\tDecrypt speed  : %.3f kB/s (n=%dkB,t=%.3fs)\n", throughputDecrypt, numIterations, durationSeconds);

    }

    public static void checkTest(byte[] a, byte[] b, String s) {
        if (Arrays.equals(a, b)) {
            System.out.printf("%s %s %s %s", s, ANSI_GREEN, "PASSED", ANSI_RESET);
        } else {
            System.out.printf("%s %s %s %s", s, ANSI_RED, "FAILED", ANSI_RESET);
        }
    }

    public static void helpFunc(byte[] a, Function<byte[], byte[]> function, Function<byte[], byte[]> functionR, String s) {
        System.out.printf("%n%s%n", s);
        System.out.println("\tInput:     " + Arrays.toString(a));
        byte[] b = function.apply(function.apply(function.apply(function.apply(a))));
        System.out.println("\tEncrypted: " + Arrays.toString(b));
        b = functionR.apply(functionR.apply(functionR.apply(functionR.apply(b))));
        System.out.println("\tDecrypted: " + Arrays.toString(b));
        checkTest(a, b, s);
        System.out.println("\n");
    }
}
