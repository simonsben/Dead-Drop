import core.header;
import core.image;

public class basic_encoder extends image_encoder {
    image base_image;

    public basic_encoder(String[] filenames, String technique_name) {
        super(filenames, technique_name);
        this.header_length = 5;
        base_image = this.image_set[0];
    }

    public basic_encoder(String[] filenames) {
        this(filenames, "naive");
    }

    public byte[] get_header(int data_length) {
        if (base_image.data_capacity < data_length + header_length)
            return null;

        base_image.encode_mode = 1;
        base_image.data_size = data_length;
        return header.generate_mode_one(base_image);
    }

    public void encode_data(byte[] data) {
        has_capacity(data.length);

        int data_length = tech.embed_data(base_image, data, this.header_length);     // Embed data
        tech.embed_data(base_image, get_header(data_length));                        // Embed header
        System.out.printf("Encoded with length %d\n", data_length);

        System.out.println("Data encoded.");
    }

    public byte[] decode_data() {
        System.out.printf("Decode with data length %d\n", base_image.data_size);
        return tech.recover_data(base_image, base_image.data_size, this.header_length);   // Recover data
    }
}
