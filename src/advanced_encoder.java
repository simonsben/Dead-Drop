import core.header;
import core.image;

import java.util.Random;

import static utilities.data_management.concat_arrays;
import static utilities.data_management.get_sub_array;

public class advanced_encoder extends image_encoder {
    short encoding_id;

    public advanced_encoder(String[] filenames, String technique_name) {
        super(filenames, technique_name);
        this.header_length = 8;
        encoding_id = (short) (new Random()).nextInt(Short.MAX_VALUE + 1);
    }

    public advanced_encoder(String[] filenames) {
        this(filenames, "naive");
    }

    byte[] get_header(image img, int data_length) {
        if (img.data_capacity < data_length + header_length)
            throw new IllegalArgumentException("Target image doesn't have the capacity to embed the given data");

        img.encode_mode = 1;
        img.data_size = data_length;
        img.encoding_id = this.encoding_id;
        return header.generate_mode_two(img, tech);
    }

    public void encode_data(byte[] data) {
        has_capacity(data.length);
        byte[] data_subset;
        int byte_offset = 0, data_size;
        image img;

        for (byte index = 0; index < image_set.length; index++) {
            img = image_set[index];
            img.image_index = index;

            data_size = Math.min(img.data_capacity, data.length - byte_offset); // Get data subset length
            data_subset = get_sub_array(data, byte_offset, data_size);          // Get data subset
            byte_offset += data_size;

            int data_length = tech.embed_data(img, data_subset, header_length);     // Embed data
            tech.embed_data(img, get_header(img, data_length));                     // Embed header
        }
    }

    public byte[] decode_data() {
        byte[] data = new byte[0], image_data;

        for (image img : image_set) {
            if (img.encode_mode == -1)
                throw new IllegalArgumentException("Provided file does not have data encoded in it.");

            image_data = tech.recover_data(img, img.data_size, header_length);
            data = concat_arrays(data, image_data);
        }

        return data;
    }
}
