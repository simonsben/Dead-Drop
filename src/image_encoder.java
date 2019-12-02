import core.image;
import core.Naive;
import core.BPCS;
import core.technique;

import java.security.InvalidParameterException;

public abstract class image_encoder {
    image[] image_set;
    static int header_length;
    int data_capacity;
    technique tech = new Naive();
    boolean will_encrypt = false, assigned_key = false;


    public image_encoder(String[] filenames, String technique_name) {
        set_technique(technique_name);

        image_set = new image[filenames.length];
        for (int index=0;index<filenames.length;index++)
            image_set[index] = new image(filenames[index], tech);

        analyze_images();
    }

    public image_encoder(image[] images, technique _tech) {
        image_set = images;
        tech = _tech;

        analyze_images();
    }

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

    public void analyze_images() {
        for (image img : image_set) {
            tech.analyze_image(img);
            this.data_capacity += img.data_capacity;
        }
        System.out.printf("Initialized encoder with capacity %dK\n", data_capacity / 1024);
    }

    public void has_capacity(int data_length) {
        if (data_length > data_capacity)
            throw new InvalidParameterException("Data provided exceeds capacity of image.");
    }

    public void save_images() {
        for (image target_image : image_set) {
            if (target_image.was_used)
                target_image.save_image();
        }
    }

    public void set_encryption_key(String plaintext) {
        tech.set_encryption_key(plaintext);
        assigned_key = true;
    }

    public abstract void encode_data(byte[] data);
    public abstract byte[] decode_data();

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("Advanced encoder with images:\n");

        for (image img : image_set) {
            output.append(img);
            output.append('\n');
        }

        return output.toString();
    }
}
