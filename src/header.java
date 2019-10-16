public class header {
    public static boolean is_jfif(byte[] data) {
        if (!has_jpg_signature(data)) {
            return false;
        } else if(
                data[6] != 'J' || data[7] != 'F' ||
                data[8] != 'I' || data[9] != 'F' ||
                data[10] != '\0'
        )   // Check for JFIF identifier
            return false;

        return true;
    }

    public static boolean has_jpg_signature(byte[] data) {
        return data.length > 22 &&    // At least as long as header + tail
            data[0] == (byte) 0xFF &&   // Has SOI marker
            data[1] == (byte) 0xD8 &&
            data[2] == (byte) 0xFF &&   // Has correct application use marker
            data[3] == (byte) 0xE0 &&
            data[data.length - 2] == (byte) 0xFF &&     // Has correct tail
            data[data.length - 1] == (byte) 0xD9;
    }

    public static void print_header(byte[] data) {
        System.out.println("Image header:");
        output.print_hex(data, 0, 20);
    }
}
