package utilities;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class data_management {
    // Concatenate byte arrays
    public static byte[] concat_arrays(byte[] a, byte[] b) {
        int new_length = a.length + b.length;
        byte[] combined = new byte[new_length];

        System.arraycopy(a, 0, combined, 0, a.length);
        System.arraycopy(b, 0, combined, a.length, b.length);

        return combined;
    }

    // Split byte arrays
    public static void split_array(byte[] combined, byte[] a, byte[] b) {
        System.arraycopy(combined, 0, a, 0, a.length);
        System.arraycopy(combined, a.length, b, 0, b.length);
    }

    public static void offload_differences(byte[][][] counts, int x, int y, int difference) {
        for (int bit=0;bit<8;bit++) {
            if (counts[bit][x][y] < Byte.MAX_VALUE)
                counts[bit][x][y] += low_level.get_bit(difference, bit) > 0? 1 : 0;
        }
    }

    public static byte[] get_sub_array(byte[] array, int start, int length) {
        byte[] sub_array = new byte[length];

        for (int index = start; index < start + length; index++)
            sub_array[index - start] = array[index];

        return sub_array;
    }

    // TODO cleanup code - from SO answer
    public static String compute_md5(byte[] data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] messageDigest = md.digest(data);

        BigInteger no = new BigInteger(1, messageDigest);
        String hashtext = no.toString(16);

        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }

    public static byte[] get_array(short value) {
        return ByteBuffer.allocate(2).putShort(value).array();
    }

    public static byte[] get_array(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }

    public static byte[] get_array(long value) {
        return ByteBuffer.allocate(8).putLong(value).array();
    }
}
