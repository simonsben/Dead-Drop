package utilities;

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
        for (int bit=0;bit<8;bit++)
            counts[bit][x][y] += low_level.get_bit(difference, bit) > 0? 1 : 0;
    }
}
