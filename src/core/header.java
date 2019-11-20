package core;

import java.nio.ByteBuffer;
import static utilities.data_management.get_array;

public class header {
    static byte signature = (byte) 0xA8;    // Define default signature as 10101XXX
    static byte signature_mask = (byte) 0xF8;

    public static void decode_header(image img) {
        byte raw = naive.recover_data(img.image, 1)[0];     // Get first byte
        if ((raw & signature_mask) != signature)    // Check if encoder signature is present
            return;

        byte mode = (byte) (raw & ~signature_mask);

        if (mode == 1) decode_mode_one(img);
    }

    // Generate header for encoding mode 1, basic
    public static byte[] generate_mode_one(image img) {
        byte[] header = new byte[5];
        header[0] = (byte) (signature | img.encode_mode);   // Add signature and encoding mode
        System.arraycopy(get_array(img.data_size), 0, header, 1, 4);    // Add data length

        return header;
    }

    // Decode header when using encoding mode 1, basic
    public static void decode_mode_one(image img) {
        img.encode_mode = 1;

        // Get data size
        byte[] tmp = naive.recover_data(img.image, 4, 1);
        img.data_size = ByteBuffer.wrap(tmp).getInt();
    }
}
