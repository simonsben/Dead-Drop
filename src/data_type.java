public class data_type<T extends Number> {
    public static void main(String[] args) {
        int[] a = new int[]{1, 2, 3, 4};
        Class<?> target_class = ((Object) a[0]).getClass();

        if(target_class == Integer.class)
            System.out.println("int");
        else
            System.out.println("Nothing");
    }
}
