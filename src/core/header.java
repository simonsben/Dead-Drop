package core;

import java.nio.ByteBuffer;
import static utilities.data_management.get_array;
import utilities.low_level;

public class header {
    static byte signature = (byte) 0xA8;        // Define default signature as 101010XX
    static byte signature_mask = (byte) 0xFC;

    public static void decode_header(image img, technique tech) {
        byte raw = tech.recover_data(img, 1)[0];     // Get first byte
        int saved_technique = low_level.extract_bit(raw, 0, 1, 0);
        if ((raw & signature_mask) != signature) {  // Check if encoder signature is present
            System.out.println("Skipping image, signature not present.");
            return;
        }
        if ((saved_technique == 0 && (tech instanceof bpcs)) || (saved_technique == 1 && (tech instanceof naive)))
            throw new IllegalCallerException("Encoder technique not equal to encoded format.");

        byte mode = (byte) low_level.get_bit(raw, 0);

        if (mode == 1) decode_mode_one(img, tech);
    }

    // Generate header for encoding mode 1, basic
    public static byte[] generate_mode_one(image img, technique tech) {
        byte[] header = new byte[5];
        header[0] = (byte) (signature | 1);                             // Add signature and encoding mode
        if (tech instanceof bpcs) header[0] = (byte) (header[0] | 2);   // If technique 1, encode in header
        System.arraycopy(get_array(img.data_size), 0, header, 1, 4);    // Add data length

        return header;
    }

    // Decode header when using encoding mode 1, basic
    public static void decode_mode_one(image img, technique tech) {
        img.encode_mode = 1;

        // Get data size
        byte[] tmp = tech.recover_data(img, 4, 1);
        img.data_size = ByteBuffer.wrap(tmp).getInt();
    }
}
