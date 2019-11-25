package core;

import utilities.input;
import utilities.output;
import utilities.strings;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;

public class image {
    public BufferedImage image;
    public int data_capacity, data_size, num_channels;
    short encoding_id;
    byte image_index;
    public byte encode_mode = -1;
    Path filename;

    public image(String filename, technique tech) {
        this.filename = Paths.get(filename);
        load_image(tech);
    }

    public image() {}

    public void load_image(technique tech) {
        image = input.load_image(this.filename);
        num_channels = image.getRaster().getNumBands();

        header.decode_header(this, tech);
    }

    public void save_image(String filename, String file_type) {
        output.save_image(this.image, filename, file_type);
    }

    public void save_image() {
        String raw_filename =  strings.remove_extension(this.filename.getFileName().toString());
        save_image("processed/" + raw_filename + ".png");
    }

    public void save_image(String filename) {
        String extension = strings.get_extension(filename);
        extension = extension == null ? "png" : extension;

        save_image(filename, extension);
    }
}
