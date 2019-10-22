import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class utilities {
    public static int base_bit = ~1;

    public static int get_mask(int index) {
        return 1 << index;
    }

    // TODO Add range check on index
    public static int get_bit(int source, int index) {
        return source & get_mask(index);
    }

    public static int insert_bit(int source, int target, int index) {
        return (target & base_bit) | get_bit(source, index) >> index;
    }

    public static int extract_bit(int source, int target, int index) {
        return (get_bit(source, 0) << index) | target;
    }

    public static String get_extension(String filename) {
        Pattern pat = Pattern.compile("(?<=\\.)\\w+");
        Matcher matcher = pat.matcher(filename);

        boolean did_find = matcher.find();
        if (!did_find)
            return null;

        return matcher.group(0);
    }
}
