public class Kuznechik {
    private final static byte[][] constants = new byte[32][16];
    private static final byte[][] keys = new byte[10][16];

    public static byte[] X(byte[] A, byte[] B) {
        byte[] c = new byte[16];
        for (int i = 0; i < 16; i++)
            c[i] = (byte) (A[i] ^ B[i]);
        return c;
    }

    public static byte[] S(byte[] A) {
        byte[] B = new byte[A.length];
        for (int i = 0; i < 16; i++) {
            B[i] = Const.PI[A[i] < 0 ? A[i] + 256 : A[i]];
        }
        return B;
    }

    public static byte[] SReverse(byte[] A) {
        byte[] B = new byte[A.length];
        for (int i = 0; i < 16; i++) {
            B[i] = Const.REVERSE_PI[A[i] < 0 ? A[i] + 256 : A[i]];
        }
        return B;
    }

    public static byte LMul(byte a, byte b) {
        byte result = 0;
        for (int i = 0; i < 8; i++) {
            if ((b & 1) == 1)
                result ^= a;
            byte hiBit = (byte) (a & 0x80);
            a <<= 1;
            if (hiBit < 0)
                a ^= (byte) 0xc3;
            b >>= 1;
        }

        return result;
    }

    public static byte[] R(byte[] A) {
        byte a = 0;
        byte[] B = new byte[16];
        for (int i = 15; i >= 0; i--) {
            a ^= LMul(B[i == 0 ? 15 : i - 1] = A[i], Const.L_VEC[i]);
        }
        B[15] = a;
        return B;
    }

    public static byte[] RReverse(byte[] A) {
        byte[] B = new byte[16];
        for (int i = 1; i < 16; i++) {
            A[15] ^= LMul(B[i] = A[i - 1], Const.L_VEC[i]);
        }
        B[0] = A[15];
        return B;
    }

    public static byte[] L(byte[] A) {
        for (int i = 0; i < 16; i++) {
            A = R(A);
        }
        return A;
    }


    public static byte[] LReverse(byte[] A) {
        for (int i = 0; i < 16; i++)
            A = RReverse(A);
        return A;
    }

    private static void genConst() {
        for (int i = 0; i < 32; i++) {
            byte[] tmp = new byte[16];
            tmp[0] = (byte) (i + 1);
            constants[i] = L(tmp);
        }
    }

    private static byte[][] F(byte[] A, byte[] B, byte[] cons) {
        return new byte[][]{X(L(S(X(A, cons))), B), A};
    }

    public static void generateKeys(byte[] K1, byte[] K2) {
        genConst();
        byte[][] A = {K1, K2};
        keys[0] = K1;
        keys[1] = K2;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j <= 6; j += 2) {
                byte[][] B = F(A[0], A[1], constants[j + 8 * i]);
                A = F(B[0], B[1], constants[j + 1 + 8 * i]);
            }
            keys[2 * i + 2] = A[0];
            keys[2 * i + 3] = A[1];
        }
    }


    public static byte[] encrypt(byte[] msg) {
        byte[] output = msg;
        for (int i = 0; i < 9; i++) {
            output = L(S(X(keys[i], output)));
        }
        return X(output, keys[9]);
    }

    public static byte[] decrypt(byte[] msg) {
        byte[] output = msg;
        output = X(keys[9], output);
        for (int i = 8; i >= 0; i--) {
            output = X(keys[i], SReverse(LReverse(output)));
        }
        return output;
    }
}
