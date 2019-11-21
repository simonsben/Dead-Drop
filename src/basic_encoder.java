import core.header;
import core.image;

public class basic_encoder extends image_encoder {
    image base_image;

    public basic_encoder(String[] filenames) {
        super(filenames);
        this.header_length = 5;
        base_image = this.image_set[0];
    }

    public byte[] get_header(int data_length) {
        if (base_image.data_capacity < data_length)
            return null;

        base_image.encode_mode = 1;
        base_image.data_size = data_length;
        return header.generate_mode_one(base_image);
    }

    public void encode_data(byte[] data) {
        has_capacity(data.length);

        tech.embed_data(base_image, get_header(data.length));      // Embed header
        tech.embed_data(base_image, data, this.header_length);     // Embed data
    }

    public byte[] decode_data() {
        return tech.recover_data(base_image, base_image.data_size, this.header_length);   // Recover data
    }
}
