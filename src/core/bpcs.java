package core;

import utilities.data_management;
import utilities.low_level;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

// TODO List of functions to implement
/*
    -encode_block(raster, x_index, y_index, data)
    -encode_data(raster, data

 */

public class bpcs {
    public static byte threshold = 8;



    // Counts the bit-plane edges block-wise for a given channel
    public static byte[][][] count_edges(Raster image, int channel, int block_size) {
        int width = image.getWidth(), height = image.getHeight(), num_channels = image.getNumBands();
        int x_block, y_block, x_index, difference;
        byte[][][] edge_counts = new byte[8][width][height];
        int[] primary_row = new int[width * num_channels], secondary_row = new int[width * num_channels], tmp;

        if (channel >= num_channels)
            throw new IllegalArgumentException("Selected channel must exist.");

        image.getPixels(0, 0, width, 1, secondary_row);
        for (int y=0;y<height-1;y++) {
            image.getPixels(0, y + 1, width, 1, primary_row);
            tmp = secondary_row;
            secondary_row = primary_row;
            primary_row = tmp;

            y_block = y / block_size;
            for (int x=0;x<width-1;x++) {
                x_block = x / block_size;
                x_index = x * num_channels + channel;

                if (x % block_size >= num_channels - 1)
                    continue;
                // X primary
                difference = primary_row[x_index] ^ primary_row[x_index + num_channels];
                data_management.offload_differences(edge_counts, x_block, y_block, difference);

                // X secondary
                difference = secondary_row[x_index] ^ secondary_row[x_index + num_channels];
                data_management.offload_differences(edge_counts, x_block, (y + 1) / block_size, difference);

                if (y % block_size >= num_channels - 1)
                    continue;

                // Y primary
                difference = primary_row[x_index] ^ secondary_row[x_index];
                data_management.offload_differences(edge_counts, x_block, y_block, difference);

                if (y + 1 == height) {
                    difference = primary_row[x_index + num_channels] ^ secondary_row[x_index + num_channels];
                    data_management.offload_differences(edge_counts, (x + 1) / block_size, y_block, difference);
                }
            }
        }
        return edge_counts;
    }

    public static void embed_data(BufferedImage image, byte[][][] edge_counts, byte[] data, int block_size, int channel, int block_offset) {
        BufferedImage sub_image;
        int data_offset = 0, block_capacity = block_size * block_size;
        byte[] data_subset = new byte[block_capacity];

        for (int x_index=0;x_index<edge_counts[0].length;x_index++){
            for (int y_index=0;y_index<edge_counts[0][0].length;y_index++) {
                sub_image = image.getSubimage(x_index * block_size, y_index * block_size, block_size, block_size);

                for (int bit=0;bit<8;bit++) {
                    if (block_offset-- > 0 || edge_counts[bit][x_index][y_index] < threshold)
                        continue;

                    System.arraycopy(data, data_offset, data_subset, 0, Math.min(block_capacity, block_capacity - data_offset));
                    data_offset += block_capacity;

                    embed_block(sub_image.getRaster(), data_subset, block_size, channel, bit);
                    if (data_offset >= data.length)
                        return;
                }
            }
        }
    }

    public static byte[] recover_data(BufferedImage image, byte[][][] edge_counts, int data_length, int block_size, int channel) {
        BufferedImage sub_image;
        byte[] data = new byte[data_length];
        int data_offset = 0, block_capacity = block_size * block_size;
        byte[] data_subset = new byte[block_capacity];

        for (int x_index=0;x_index<edge_counts[0].length;x_index++){
            for (int y_index=0;y_index<edge_counts[0][0].length;y_index++) {
                sub_image = image.getSubimage(x_index * block_size, y_index * block_size, block_size, block_size);

                for (int bit=0;bit<8;bit++) {
                    if (edge_counts[bit][x_index][y_index] < threshold)
                        continue;

                    recover_block(sub_image.getRaster(), data_subset, block_size, channel, bit);
                    System.arraycopy(data_subset, 0, data, data_offset, Math.min(block_capacity, block_capacity - data_offset));
                    data_offset += block_capacity;

                    if (data_offset >= data_length)
                        return data;
                }
            }
        }

        return data;
    }

    public static void embed_block(WritableRaster image, byte[] data, int block_size, int channel, int bit) {
        int bit_index = 0, byte_index = 0, source = data[0];
        int[] image_data = new int[image.getNumBands()];

        for (int x=0;x<block_size;x++) {
            for (int y=0;y<block_size;y++) {
                image.getPixel(x, y, image_data);
                image_data[channel] = low_level.place_bit(source, image_data[channel], bit_index, bit);
                image.setPixel(x, y, image_data);

                bit_index++;
                if (bit_index > 7) {
                    bit_index = 0;  // Reset bit index

                    byte_index++;                       // Increment byte index
                    if (byte_index >= data.length)      // If no more data, stop
                        return;
                    source = data[byte_index];      // Get next byte to embed
                }
            }
        }
    }

    public static void recover_block(Raster image, byte[] data, int block_size, int channel, int bit) {
        int bit_index = 0, byte_index = 0, current_byte = 0;
        int[] image_data = new int[image.getNumBands()];

        for (int x=0;x<block_size;x++) {
            for (int y=0;y<block_size;y++) {
                image.getPixel(x, y, image_data);

                current_byte = low_level.extract_bit(image_data[channel], current_byte, bit, bit_index);

                bit_index++;
                if (bit_index > 7) {
                    bit_index = 0;  // Reset bit index
                    data[byte_index] = (byte) current_byte;

                    byte_index++;                       // Increment byte index
                    if (byte_index >= data.length)      // If no more space for data, stop
                        return;

                    current_byte = 0;
                }
            }
        }
    }

    public static int compute_channel_capacity(byte[][][] edge_counts) {
        int channel_capacity = 0;

        for (int bit=0;bit<edge_counts[0].length;bit++) {
            for (int x_index=0;x_index<edge_counts[0].length;x_index++) {
                for (int y_index=0;y_index<edge_counts[0][0].length;y_index++)
                    channel_capacity += edge_counts[bit][x_index][y_index];
            }
        }

        return channel_capacity;
    }

    public static void visualize_edges(WritableRaster image, byte[][][] edge_counts, int block_size, int bit) {
        int width = image.getWidth(), height = image.getHeight(), num_channels = image.getNumBands();
        int[] row = new int[width * num_channels];
        int x_index, y_index;

        for (int y=0;y<height;y++) {
            image.getPixels(0, y, width, 1, row);
            y_index = y / block_size;

            for (int x=0;x<width;x++) {
                x_index = x / block_size;

                if (edge_counts[bit][x_index][y_index] >= threshold) {
                    row[x * 3] = 255;
                }
            }
            image.setPixels(0, y, width, 1, row);
        }
    }

    public static void add_square_mask(WritableRaster image, int block_size, int channel) {
        int width = image.getWidth(), height = image.getHeight(), num_channels = image.getNumBands();
        int[] row = new int[width * num_channels];

        int x_index, y_index;

        for (int y=0;y<height;y++) {
            image.getPixels(0, y, width, 1, row);
            y_index = y / block_size;

            for (int x=0;x<width;x++) {
                x_index = x / block_size;

                if ((x_index % 2 == 0) ^ (y_index % 2 == 0)) {
                    row[x * 3 + channel] = 255;
                }
            }
            image.setPixels(0, y, width, 1, row);
        }
    }

    public static void add_square_mask(WritableRaster image, int channel) {
        add_square_mask(image, 8, channel);
    }
}
