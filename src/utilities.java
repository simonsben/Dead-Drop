public class utilities {
    public static int get_mask(int index) {
        return 1 << index;
    }

    // TODO Add range check on index
    public static int get_bit(int source, int index) {
        return source & get_mask(index);
    }

    // TODO Simplify boolean logic
    public static int insert_bit(int source, int target, int index) {
        return (target & ~1) | get_bit(source, index);
    }
}
