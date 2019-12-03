import core.Image;
import core.header;
import core.technique;

public class basic_encoder extends image_encoder {
    Image base_image;
    static int header_length = 5;

    public basic_encoder(String[] filenames, String technique_name) {
        super(filenames, technique_name);
        base_image = this.image_set[0];
    }

    public basic_encoder(String[] filenames) {
        this(filenames, "naive");
    }

    public basic_encoder(Image[] images, technique tech) {
        super(images, tech);
        base_image = image_set[0];
    }

    public byte[] get_header(int data_length) {
        if (base_image.data_capacity < data_length)
            return null;

        base_image.encode_mode = 0;
        base_image.data_size = data_length;
        return header.generate_basic(base_image, tech);
    }

    public void encode_data(byte[] data) {
        has_capacity(data.length);

        int data_length = tech.embed_data(base_image, data, header_length);     // Embed data
        tech.embed_data(base_image, get_header(data_length), 0);                        // Embed header
        base_image.was_used = true;
    }

    public byte[] decode_data() {
        if (base_image.encode_mode == -1)
            throw new IllegalArgumentException("Provided file does not have data encoded in it.");

        return tech.recover_data(base_image, base_image.data_size, header_length);   // Recover data
    }
}
