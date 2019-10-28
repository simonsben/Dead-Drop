import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.nio.file.Path;
import java.nio.file.Paths;

public class image {
    public BufferedImage image;
    public int total_capacity;
    Path filename;

    public image(String filename) {
        this.filename = Paths.get(filename);
        this.load_image();
    }

    public void load_image() {
        this.image = input.load_image(this.filename);
        this.update_capacity();
    }

    public void save_image(String filename, String file_type) {
        output.save_image(this.image, filename, file_type);
    }

    public void save_image() {
        String raw_filename =  utilities.remove_extension(this.filename.getFileName().toString());
        this.save_image("processed/" + raw_filename + ".png");
    }

    public void save_image(String filename) {
        String extension = utilities.get_extension(filename);
        extension = extension == null ? "png" : extension;

        save_image(filename, extension);
    }

    public void update_capacity() {
        Raster raster = this.image.getRaster();
        this.total_capacity = raster.getWidth() * raster.getHeight() / 8;
    }
}
