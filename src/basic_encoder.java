import java.nio.ByteBuffer;

public class basic_encoder extends image_encoder {
    public basic_encoder(String[] filenames) {
        super(filenames);
        this.header_length = 5;
    }

    public byte[] get_header(int data_length) {
        byte[] header;
        header = new byte[5];

        byte[] tmp = ByteBuffer.allocate(4).putInt(data_length).array();
        System.arraycopy(tmp, 0, header, 1, 4);

        return header;
    }

    public void decode_header(byte[] header) {
        byte[] tmp = new byte[4];
        System.arraycopy(header, 1, tmp, 0, 4);

        this.data_length = ByteBuffer.wrap(tmp).getInt();
    }

    public void encode_data(byte[] data) {
        byte[] header = this.has_capacity(data.length);     // Get encoding data header
        if (header == null)
            return;

        naive.embed_data(this.image_set[0].image, header);                  // Embed header
        naive.embed_data(this.image_set[0].image, data, header.length);     // Embed data
    }

    public byte[] extract_data() {
        byte[] header = naive.recover_data(this.image_set[0].image, this.header_length);    // Recover header
        decode_header(header);                                                              // Read header

        return naive.recover_data(this.image_set[0].image, this.data_length, this.header_length);   // Recover data
    }

//    Code to embed multiple images, king of
//    int image_offset = header.length, data_offset = 0, slice_length;    // Define constants
//    image current_image;
//        for (int index=0;index<this.image_set.length;index++) {             // Add data to images
//            current_image = this.image_set[index];
//            slice_length = Math.min(current_image.total_capacity - image_offset, data.length - data_offset);
//
//            byte[] data_slice = Arrays.copyOfRange(data, data_offset, data_offset + slice_length);
//            naive.embed_data(current_image.image, data_slice, image_offset);
//
//            image_offset = 0;
//            data_offset += slice_length;
//            if (data_offset >= data.length)
//                break;
//        }
}
