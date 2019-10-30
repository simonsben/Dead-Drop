import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class bpcs {
    public static byte[][][] count_edges(Raster image, int channel, int block_size) {
        int width = image.getWidth(), height = image.getHeight(), num_channels = image.getNumBands();
        int x_block, y_block, x_index, difference;
        byte[][][] edge_counts = new byte[8][width][height];
        int[] primary_row = new int[width * num_channels], secondary_row = new int[width * num_channels], tmp;

        if (channel >= num_channels)
            return null;

        for (int y=0;y<height-1;y++) {
            image.getPixels(0, y, width, 1, primary_row);
            tmp = secondary_row;
            secondary_row = primary_row;
            primary_row = tmp;

            y_block = y / block_size;
            for (int x=0;x<width-1;x++) {
                x_block = x / block_size;
                x_index = x * num_channels + channel;

                if (x % block_size >= num_channels - 2)
                    continue;
                // X primary
                difference = primary_row[x_index] ^ primary_row[x_index + num_channels];
                utilities.offload_differences(edge_counts, x_block, y_block, difference);

                // X secondary
                difference = secondary_row[x_index] ^ secondary_row[x_index + num_channels];
                utilities.offload_differences(edge_counts, x_block, (y + 1) / block_size, difference);

                if (y % block_size >= num_channels - 1)
                    continue;

                // Y primary
                difference = primary_row[x_index] ^ secondary_row[x_index];
                utilities.offload_differences(edge_counts, x_block, y_block, difference);

                if (x + 1 == width) {
                    difference = primary_row[x_index + num_channels] ^ secondary_row[x_index + num_channels];
                    utilities.offload_differences(edge_counts, (x + 1) / block_size, y_block, difference);
                }
            }
        }
        return edge_counts;
    }

    public static void add_square_mask(WritableRaster image, int block_size) {
        int width = image.getWidth(), height = image.getHeight(), num_channels = image.getNumBands();
        int[] row = new int[width * num_channels];

        int x_index, y_index;

        for (int y=0;y<height;y++) {
            image.getPixels(0, y, width, 1, row);
            y_index = y / block_size;

            for (int x=0;x<width;x++) {
                x_index = x / block_size;

                if ((x_index % 2 == 0) ^ (y_index % 2 == 0)) {
                    row[x * 3] = 255;
                }
            }
            image.setPixels(0, y, width, 1, row);
        }
    }

    public static void add_square_mask(WritableRaster image) {
        add_square_mask(image, 8);
    }
}
