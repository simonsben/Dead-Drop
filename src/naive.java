import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class naive {
    private BufferedImage image;

    public naive(String filename) {
        this.load_image(filename);
    }

    public final void embed_data(byte[] data) {
        int num_channels = this.image.getRaster().getNumBands();

        WritableRaster image_raster = this.image.getRaster();
        int[] target = new int[num_channels];
        int height = this.image.getHeight(), width = this.image.getWidth();
        int byte_index = 0, bit_index = 0;
        int source = data[0];
        boolean last = false;

        for (int x=0;x<width;x++) {
            for (int y=0;y<height;y++) {
                image_raster.getPixel(x, y, target);                                // Get pixel value
                System.out.printf("%02X -> %02X\n", target[0], utilities.insert_bit(source, target[0], bit_index));
                target[0] = utilities.insert_bit(source, target[0], bit_index);     // Insert hidden data
                image_raster.setPixel(x, y, target);                                // Set pixel value


                bit_index++;
                if (bit_index > 7) {
                    if (!last) bit_index = 0;
                    else return;

                    byte_index++;
                    if (byte_index >= data.length)
                        last = true;

                    source = (last ? '\n' : data[byte_index]);
                }
            }
        }
    }

//    tmp[0] = target[0] & 0xFFFE;


    public final void embed_data(String embed_string) {
        this.embed_data(embed_string.getBytes());
    }

    public final void load_image(String filename) {
        try {
            this.image = ImageIO.read(new File(filename));
            System.out.println("Image loaded");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final void save_image(String filename) {
        try {
            ImageIO.write(this.image, "jpg", new File(filename));
            System.out.println("Image saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Make pretty
    public final int[] get_bytes(int num_bytes) {
        Raster source = this.image.getRaster();
        int[] data = new int[num_bytes];
        int[] tmp = new int[1];

        for (int index=0;index<num_bytes;index++) {
            source.getPixel(0, index, tmp);
            data[index] = tmp[0];
        }

        return data;
    }

}