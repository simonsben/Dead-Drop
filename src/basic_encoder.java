import core.header;
import core.image;
import core.naive;

import java.awt.image.Raster;

public class basic_encoder extends image_encoder {
    image base_image;

    public basic_encoder(String[] filenames) {
        super(filenames);
        this.header_length = 5;
        base_image = this.image_set[0];
    }

    public void analyze_image(image img) {
        Raster raster = img.image.getRaster();
        img.data_capacity = raster.getWidth() * raster.getHeight() * img.num_channels / 8;
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

        naive.embed_data(this.image_set[0].image, get_header(data.length));      // Embed header
        naive.embed_data(this.image_set[0].image, data, this.header_length);     // Embed data
    }

    public byte[] decode_data() {
        return naive.recover_data(base_image.image, base_image.data_size, this.header_length);   // Recover data
    }
}
