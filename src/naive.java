import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class naive {
    // TODO add support for multi-channel encoding
    public static void embed_data(BufferedImage image, byte[] data, int offset) {
        WritableRaster image_raster = image.getRaster();

        int num_channels = image_raster.getNumBands();
        int height = image.getHeight(), width = image.getWidth();
        int byte_index = 0, bit_index = 0, source = data[0];

        int[] target_image = new int[num_channels];
        boolean initial_load = true;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (initial_load) {
                    x = (offset * 8 / num_channels) / width;
                    y = (offset * 8 / num_channels) % width;
                }

                image_raster.getPixel(x, y, target_image);          // Get pixel value
                // Insert hidden data into each channel
                for (int channel=0;channel<num_channels;channel++) {
                    if (initial_load) {
                        channel = (offset * 8) % num_channels;
                        initial_load = false;
                    }

                    target_image[channel] = utilities.insert_bit(source, target_image[channel], bit_index);

                    bit_index++;
                    if (bit_index > 7) {
                        bit_index = 0;  // Reset bit index

                        byte_index++;                       // Increment byte index
                        if (byte_index >= data.length) {    // If no more data, stop
                            image_raster.setPixel(x, y, target_image);      // Set pixel value before stopping
                            return;
                        }

                        source = data[byte_index];      // Get next byte to embed
                    }
                }
                image_raster.setPixel(x, y, target_image);          // Set pixel value
            }
        }
    }

    public static void embed_data(BufferedImage image, byte[] data) {
        embed_data(image, data, 0);
    }

    public static byte[] recover_data(BufferedImage image, int data_size, int offset) {
        WritableRaster image_raster = image.getRaster();            // Get image raster
        byte[] data = new byte[data_size];

        int num_channels = image_raster.getNumBands();              // Get number of channels in image
        int height = image.getHeight(), width = image.getWidth();   // Get height and width of image
        int byte_index = 0, bit_index = 0, current_byte = 0;        // Allocate indexes

        int[] target_pixel = new int[num_channels];                 // Allocate byte array to extract image data
        boolean initial_load = true;                                // Set initial loop to true

        for (int x = 0; x < width; x++) {         // For each row
            for (int y = 0; y < height; y++) {    // For each column
                if (initial_load) {         // Set offset before starting
                    x = (offset * 8 / num_channels) / width;
                    y = (offset * 8 / num_channels) % width;
                }

                image_raster.getPixel(x, y, target_pixel);                                          // Get pixel value

                // Insert hidden data into each channel
                for (int channel=0;channel<num_channels;channel++) {
                    if (initial_load) {
                        channel = (offset * 8) % num_channels;
                        initial_load = false;
                    }

                    current_byte = utilities.extract_bit(target_pixel[channel], current_byte, bit_index);     // Get pixel

                    bit_index++;    // Increment bit index
                    if (bit_index > 7) {    // If end of byte
                        bit_index = 0;      // Reset bit index

                        data[byte_index] = (byte) current_byte;    // Add current byte to extracted data array
                        current_byte = 0;                           // Reset current byte value

                        byte_index++;                   // Increment byte index
                        if (byte_index >= data.length)  // If no more data, stop
                            return data;
                    }
                }
            }
        }
        return null;
    }

    public static byte[] recover_data(BufferedImage image, int data_size) {
        return recover_data(image, data_size, 0);
    }
}
