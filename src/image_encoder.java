import core.image;
import core.naive;
import core.bpcs;
import core.technique;
import utilities.encrypter;

import java.security.InvalidParameterException;

public abstract class image_encoder {
    image[] image_set;
    int header_length, data_capacity;
    technique tech = new naive();
    boolean will_encrypt = false, assigned_key = false;


    public image_encoder(String[] filenames, String technique_name) {
        set_technique(technique_name);

        image[] image_set = new image[filenames.length];
        for (int index=0;index<filenames.length;index++)
            image_set[index] = new image(filenames[index], tech);

        this.image_set = image_set;
        analyze_images();
    }

    public void set_technique(String technique_name) {
        if (assigned_key)
            System.out.println("----- WARNING: Discarding previously set encryption key. -----");

        if (technique_name.equals("naive")) {
            tech = new naive();
            will_encrypt = false;
        } else if (technique_name.equals("bpcs")) {
            tech = new bpcs();
            will_encrypt = true;
        }
        else throw new InvalidParameterException("Encoding type not supported.");
        assigned_key = false;
    }

    public void analyze_images() {
        for (image img : image_set) {
             tech.analyze_image(img);

            img.data_capacity -= header_length;     // Subtract header length from image capacity
            if (will_encrypt)                       // If encrypting remove IV length
                this.data_capacity -= encrypter.iv_length * image_set.length;

            this.data_capacity += img.data_capacity;
        }
    }

    public void has_capacity(int data_length) {
        if (data_length > data_capacity)
            throw new InvalidParameterException("Data provided exceeds capacity of image.");
    }

    public void save_images() {
        for (image target_image : image_set)
            target_image.save_image();
    }

    public void set_encryption_key(String plaintext) {
        tech.set_encryption_key(plaintext);
        assigned_key = true;
    }

    public abstract void encode_data(byte[] data);
    public abstract byte[] decode_data();
}
