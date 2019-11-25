import core.image;
import core.naive;
import core.bpcs;
import core.technique;
import java.security.InvalidParameterException;

public abstract class image_encoder {
    image[] image_set;
    int header_length, data_capacity;
    technique tech = new naive();


    public image_encoder(String[] filenames, String technique_name) {
        set_technique(technique_name);

        image[] image_set = new image[filenames.length];
        for (int index=0;index<filenames.length;index++)
            image_set[index] = new image(filenames[index], tech);

        this.image_set = image_set;
        analyze_image();
    }

    public void set_technique(String technique_name) {
        if (technique_name.equals("naive")) tech = new naive();
        else if (technique_name.equals("bpcs")) tech = new bpcs();
        else throw new InvalidParameterException("Encoding type not supported.");
    }

    public void analyze_image() {
        for (image img : image_set) {
             tech.analyze_image(img);
             this.data_capacity += img.data_capacity;
        }
    }

    public void has_capacity(int data_length) {
        if (data_length + header_length > this.data_capacity)
            throw new InvalidParameterException("Data provided exceeds capacity of image.");
    }

    public void save_images() {
        for (image target_image : this.image_set)
            target_image.save_image();
    }

    public abstract byte[] get_header(int data_length);
    public abstract void encode_data(byte[] data);
    public abstract byte[] decode_data();
}
