import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class output {
    public static void print_array(byte[] array) {
        for (byte target : array) {
            System.out.print((char) target);
        }
    }
    public static void print_array(byte[][] array) {
        for (byte[] target : array) {
            print_array(target);
            System.out.print('\n');
        }
    }

    public static void write_file(Path file_path, byte[] data) {
        FileOutputStream data_file;
        try {
            data_file = new FileOutputStream(file_path.toString());
            data_file.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void write_file(String file_name, byte[] data) {
        Path file_path = Paths.get(file_name);
        write_file(file_path, data);
    }
}
