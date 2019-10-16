public class dataset <T extends Number> {
    private T[] data;
    private bool is_raw = False;

    public dataset(T[] array) {
        data = array;
    }
//    @Override
//    public String toString() {
//        char[] cast_data = new char[self.data.length];
//        for (T target : self.data) {
//            cast_data = new String(target);
//        }
//        return new String(cast_data);
//    }
}
