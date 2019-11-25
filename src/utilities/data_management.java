package utilities;

import java.nio.ByteBuffer;

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
