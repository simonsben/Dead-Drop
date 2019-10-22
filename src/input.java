import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;


public class input {
    // Import file as byte stream
    public static byte[] load_file(Path file_path) {
        FileInputStream data_stream = null;
        byte[] byte_array = null;

        try {
            File data_file = new File(file_path.toString());    // Initialize file
            if (!data_file.exists()) {                          // Check if file exists
                throw new FileNotFoundException("Given path does not exist.");
            }

            byte_array = new byte[(int) data_file.length()];    // Initialize byte array

            data_stream = new FileInputStream(data_file);       // Initialize data stream
            data_stream.read(byte_array);                       // Load file into byte array
        } catch (IOException e) {   // Catch IO errors
            e.printStackTrace();
        } finally {
            // If byte stream was opened, try to close it
            if (data_stream != null) {
                try {
                    data_stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return byte_array;
    }

    public static byte[] load_file(String filename) {
        Path file_path = Paths.get(filename);
        return load_file(file_path);
    }

    public static BufferedImage load_image(String filename) {
        BufferedImage image;
        try {
            image = ImageIO.read(new File(filename));
            System.out.println("Image loaded");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return image;
    }
}
