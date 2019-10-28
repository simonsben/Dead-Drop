import java.awt.image.BufferedImage;
import java.awt.image.Raster;

public class image {
    public BufferedImage image;
    public int total_capacity;

    public image(String filename) {
        this.load_image(filename);
    }

    public void load_image(String filename) {
        this.image = input.load_image(filename);
        this.update_capacity();
    }

    public void save_image(String filename, String file_type) {
        output.save_image(this.image, filename, file_type);
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
