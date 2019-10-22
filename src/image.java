import java.awt.image.BufferedImage;

public class image {
    public BufferedImage image;
    public int num_channels;

    public image(String filename) {
        this.load_image(filename);
        this.update_channels();
    }

    private void update_channels() {
        this.num_channels = this.image.getRaster().getNumBands();
    }

    public final void load_image(String filename) {
        this.image = input.load_image(filename);
    }

    public final void save_image(String filename, String file_type) {
        output.save_image(this.image, filename, file_type);
    }

    public final void save_image(String filename) {
        String extension = utilities.get_extension(filename);
        extension = extension == null ? "jpg" : extension;

        save_image(filename, extension);
    }
}
