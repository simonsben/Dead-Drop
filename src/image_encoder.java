import core.Image;
import core.Naive;
import core.BPCS;
import core.technique;

import java.security.InvalidParameterException;

public abstract class image_encoder {
    Image[] image_set;
    int data_capacity;
    technique tech = new Naive();
    static int header_length = 8;
    boolean will_encrypt = false, assigned_key = false;

    // Constructor to load images
    public image_encoder(String[] filenames, String technique_name) {
        set_technique(technique_name);

        image_set = new Image[filenames.length];
        for (int index=0;index<filenames.length;index++)
            image_set[index] = new Image(filenames[index], tech);

        analyze_images();
    }

    // Constructor for pre-loaded images
    public image_encoder(Image[] images, technique _tech) {
        image_set = images;
        tech = _tech;

        analyze_images();
    }

    // Get encoder technique
    public void set_technique(String technique_name) {
        if (assigned_key)
            System.out.println("----- WARNING: Discarding previously set encryption key. -----");

        if (technique_name.equals("naive")) {
            tech = new Naive();
            will_encrypt = false;
        } else if (technique_name.equals("bpcs")) {
            tech = new BPCS();
            will_encrypt = true;
        }
        else throw new InvalidParameterException("Encoding type not supported.");
        assigned_key = false;
    }

    // Analyze images before starting operations
    public void analyze_images() {
        for (Image img : image_set) {
            tech.analyze_image(img);
            this.data_capacity += img.data_capacity;
        }
        System.out.printf("Initialized encoder with capacity %dK\n", data_capacity / 1024);
    }

    // Check if encoder has required capacity
    public void has_capacity(int data_length) {
        if (data_length > data_capacity)
            throw new InvalidParameterException("Data provided exceeds capacity of image.");
    }

    // Save used images
    public void save_images() {
        for (Image target_image : image_set) {
            if (target_image.was_used)
                target_image.save_image();
        }
    }

    // Set encryption key for payload
    public void set_encryption_key(String plaintext) {
        tech.set_encryption_key(plaintext);
        assigned_key = true;
    }

    // Require generic encode/decode methods
    public abstract void encode_data(byte[] data);
    public abstract byte[] decode_data();

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("Advanced encoder with images:\n");

        for (Image img : image_set) {
            output.append(img);
            output.append('\n');
        }

        return output.toString();
    }
}
