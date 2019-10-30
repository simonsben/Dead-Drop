import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class utilities {
    public static int base_bit = ~1;
    public static int byte_mask = 0xFF;

    // Generates mask to isolate the bit at the given index
    public static int get_mask(int index) {
        return 1 << index;
    }

    // Isolates the bit at a given index in the source int
    // TODO Add range check on index
    public static int get_bit(int source, int index) {
        return source & get_mask(index);
    }

    // Inserts bit from given index of source into LSB of the target
    public static int insert_bit(int source, int target, int index) {
        return (target & base_bit) | get_bit(source, index) >> index;
    }

    // Extracts the bit from the LSB of the source and inserts into the given index of the target
    public static int extract_bit(int source, int target, int index) {
        return (get_bit(source, 0) << index) | target;
    }

    // Gets the file type extension (ex. file.txt -> txt)
    public static String get_extension(String filename) {
        return execute_regex("(?<=\\.)\\w+", filename);
    }

    // Removes extension from filename (ex. file.txt -> file)
    public static String remove_extension(String filename) {
        return execute_regex("^[\\w\\d-]+(?=\\.)", filename);
    }

    // Executes a regex function
    public static String execute_regex(String regex_string, String target) {
        Pattern pattern = Pattern.compile(regex_string);
        Matcher matcher = pattern.matcher(target);

        return execute_regex(matcher);
    }

    // Executes regex function
    public static String execute_regex(Matcher matcher) {
        boolean did_find = matcher.find();
        if (!did_find)
            return null;

        return matcher.group(0);
    }

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
            counts[bit][x][y] += get_bit(difference, bit) > 0? 1 : 0;
    }
}
