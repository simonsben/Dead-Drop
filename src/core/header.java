package core;

import java.nio.ByteBuffer;
import static utilities.data_management.get_array;
import static utilities.data_management.get_sub_array;
import static utilities.output.print_hex;

import utilities.low_level;

public class header {
    static byte signature = (byte) 0xA8;        // Define default signature as 101010XX
    static byte signature_mask = (byte) 0xFC;

    public static void decode_header(image img, technique tech) {
        byte raw = tech.recover_data(img, 1, 0)[0];     // Get first byte
        byte saved_technique = (byte) low_level.extract_bit(raw, 0, 1, 0);

        if ((raw & signature_mask) != signature)              // Check if encoder signature is present
            return;
        if ((saved_technique == 0 && (tech instanceof BPCS)) || (saved_technique == 1 && (tech instanceof Naive)))
            throw new IllegalCallerException("Encoder technique not equal to encoded format.");

        byte mode = (byte) low_level.get_bit(raw, 0);
        img.was_used = true;
        img.encode_tech = saved_technique;

        if (mode == 0) decode_basic(img, tech);
        if (mode == 1) decode_advanced(img, tech);
    }

    // Generate header for encoding mode 1, basic
    public static byte[] generate_basic(image img, technique tech) {
        byte[] header = new byte[5];

        header[0] = signature;                                          // Add signature and encoding mode (0)
        if (tech instanceof BPCS) header[0] = (byte) (header[0] | 2);   // If technique 1, mark in header
        System.arraycopy(get_array(img.data_size), 0, header, 1, 4);    // Add data length

        return header;
    }

    // Decode header when using encoding mode 1, basic
    public static void decode_basic(image img, technique tech) {
        img.encode_mode = 0;

        // Get data size
        byte[] tmp = tech.recover_data(img, 4, 1);
        img.data_size = ByteBuffer.wrap(tmp).getInt();

        if (img.data_size > img.data_capacity)
            throw new IllegalArgumentException("Data size cannot exceed image capacity.");
    }

    // Generate header for encoding mode 1, advanced
    public static byte[] generate_advanced(image img, technique tech) {
        byte[] header = new byte[8];
        header[0] = (byte) (signature | 1);                             // Add signature and encoding mode
        if (tech instanceof BPCS) header[0] = (byte) (header[0] | 2);   // If technique 1, encode in header

        System.arraycopy(get_array(img.data_size), 0, header, 1, 4);    // Add data length
        header[5] = img.image_index;                                                          // Add image index
        System.arraycopy(get_array(img.encoding_id), 0, header, 6, 2);  // Add encoding index

        return header;
    }

    // Decode header when using encoding mode 2, advanced
    public static void decode_advanced(image img, technique tech) {
        img.encode_mode = 1;

        byte[] raw_header = tech.recover_data(img, 7, 1);

        // Get data size
        img.data_size = ByteBuffer.wrap(get_sub_array(raw_header, 0, 4)).getInt();
        img.image_index = raw_header[4];
        img.encoding_id = ByteBuffer.wrap(get_sub_array(raw_header, 5, 2)).getShort();

        if (tech instanceof BPCS && img.data_size > img.data_capacity)
            throw new IllegalArgumentException("Data size cannot exceed image capacity.\n" + img);
    }
}
