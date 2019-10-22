import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

public class naive {
    // TODO add support for multi-channel encoding
    public static void embed_data(BufferedImage image, byte[] data, int offset) {
        WritableRaster image_raster = image.getRaster();

        int num_channels = image_raster.getNumBands();
        int height = image.getHeight(), width = image.getWidth();
        int byte_index = 0, bit_index = 0, source = data[0];

        int[] target_image = new int[num_channels];
        boolean last = false, initial_load = true;

        for (int x=0;x<width;x++) {
            for (int y=0;y<height;y++) {
                if (initial_load) {
                    x = offset / width;
                    y = offset % width;
                    initial_load = false;
                }

                image_raster.getPixel(x, y, target_image);                                // Get pixel value
                target_image[0] = utilities.insert_bit(source, target_image[0], bit_index);     // Insert hidden data
                image_raster.setPixel(x, y, target_image);                                // Set pixel value

                bit_index++;
                if (bit_index > 7) {
                    if (!last) bit_index = 0;
                    else return;

                    byte_index++;
                    if (byte_index >= data.length)
                        last = true;

                    source = (last ? '\0' : data[byte_index]);
                }
            }
        }
    }

    public static void embed_data(BufferedImage image, String embed_string, int offset) {
        embed_data(image, embed_string.getBytes(), offset);
    }

    public void embed_data(BufferedImage image, byte[] data) {
        embed_data(image, data, 0);
    }

    public static byte[] recover_data(BufferedImage image) {
        if (image == null)
            return null;
        int num_channels = image.getRaster().getNumBands();

        Raster image_raster = image.getRaster();
        int[] source = new int[num_channels];
        int height = image.getHeight(), width = image.getWidth();
        int bit_index = 0;
        ArrayList<Byte> recovered = new ArrayList<Byte>();
        int last = 0;

        for (int x=0;x<width;x++) {
            for (int y=0;y<height;y++) {
                image_raster.getPixel(x, y, source);                                // Get pixel value
                last = utilities.extract_bit(source[0], last, bit_index);     // Insert hidden data

                bit_index++;
                if (bit_index > 7) {
                    if ((char) last != '\0') bit_index = 0;
                    else {
                        byte[] result = new byte[recovered.size()];
                        for (int index=0;index<recovered.size();index++)
                            result[index] = recovered.get(index);
                        return result;
                    }

                    recovered.add((byte) last);
                    last = 0;
                }
            }
        }
        return null;
    }
}
