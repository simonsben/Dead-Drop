public class main {
    public static void main(String[] args) {
        byte[] file_data = input.load_file("test_file.txt");

        output.print_array(file_data);
    }
}
