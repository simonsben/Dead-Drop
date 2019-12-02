import core.header;
import core.image;
import core.technique;
import utilities.encrypter;
import java.util.Random;
import static utilities.data_management.concat_arrays;
import static utilities.data_management.get_sub_array;

public class advanced_encoder extends image_encoder {
    short encoding_id;
    static int header_length = 8;

    public advanced_encoder(String[] filenames, String technique_name) {
        super(filenames, technique_name);
        encoding_id = (short) (new Random()).nextInt(Short.MAX_VALUE + 1);
    }

    public advanced_encoder(String[] filenames) {
        this(filenames, "naive");
    }

    public advanced_encoder(image[] images, technique tech, short _encoding_id) {
        super(images, tech);
        encoding_id = _encoding_id;
    }

    byte[] get_header(image img, int data_length) {
        img.encode_mode = 1;
        img.data_size = data_length;
        img.encoding_id = this.encoding_id;
        return header.generate_advanced(img, tech);
    }

    public void encode_data(byte[] data) {
        has_capacity(data.length);
        byte[] data_subset;
        int byte_offset = 0, data_size, max_size;
        image img;

        for (byte index = 0; index < image_set.length; index++) {
            img = image_set[index];


            max_size = img.data_capacity - header_length - (will_encrypt? encrypter.key_length : 0);
            data_size = Math.min(max_size, data.length - byte_offset);

            data_subset = get_sub_array(data, byte_offset, data_size);          // Get data subset
            byte_offset += data_size;


            if (data_size == 0) break;

            img.image_index = index;
            img.was_used = true;                                                    // Mark image as used
            int data_length = tech.embed_data(img, data_subset, header_length);     // Embed data
            tech.embed_data(img, get_header(img, data_length), 0);                     // Embed header
        }
    }

    public byte[] decode_data() {
        byte[] data = new byte[0], image_data;

        for (image img : image_set) {
            if (img.was_used) {
                image_data = tech.recover_data(img, img.data_size, header_length);
                data = concat_arrays(data, image_data);
            }
        }

        return data;
    }
}
