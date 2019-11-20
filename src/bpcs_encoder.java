import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class bpcs_encoder extends basic_encoder {
    public int block_size = 8;

    public bpcs_encoder(String[] filenames){
        super(filenames);
    }

    public void encode_data(byte[] data) {
        BufferedImage base_image = this.image_set[0].image;
        WritableRaster base_raster = base_image.getRaster();
        int num_channels = base_image.getRaster().getNumBands();

        // Embed header
        byte[] header = this.get_header(data.length), channel_data;
        int used_blocks = (int) Math.ceil(header.length / this.block_size), info_size, data_offset = 0;
        BufferedImage header_section = base_image.getSubimage(0, 0, used_blocks, this.block_size);
        naive.embed_data(header_section, header);

        byte[][][] edge_counts;
        for (int channel=0;channel<num_channels;channel++) {
            edge_counts = bpcs.count_edges(base_raster, channel, this.block_size);

//            if (channel == 0)
//                embed_header(edge_counts, header, base_image);

//            info_size = Math.min(data.length - data_offset, bpcs.compute_channel_capacity(edge_counts));
//            channel_data = new byte[info_size];
//            System.arraycopy(data, data_offset, channel_data, 0, channel_data.length);
        }
    }

//    public byte[] recover_data() {
//        BufferedImage base_image=this.image_set[0].image;
//        byte[][][] edge_counts;
//        int data_offset = 0;
//
//        for (int channel=0;channel<num_channels;channel++) {
//            edge_counts = bpcs.count_edges(base_raster, channel, this.block_size);
//
//            if (channel == 0)
//                recover_header(edge_counts, base_image);
//        }
//    }

    void recover_header(byte[][][] edge_counts, BufferedImage image) {
        byte[] header = new byte[header_length], info_block;
        int data_offset = 0, info_size, block_capacity = block_size * block_size;
        BufferedImage sub_image;

        for (int x_index=0;x_index<edge_counts[0].length;x_index++) {
            for (int y_index = 0; y_index < edge_counts[0][0].length; y_index++) {
                info_size = Math.min(block_capacity, header.length - data_offset);
                info_block = new byte[info_size];

                sub_image = image.getSubimage(x_index * block_size, x_index * block_size, block_size, block_size);
                bpcs.recover_block(sub_image.getRaster(), info_block, block_size, 0, 0);
                System.arraycopy(info_block, 0, header, data_offset, info_block.length);

                data_offset += info_size;
                if (data_offset >= header_length) {
                    this.decode_header(header);
                    return;
                }
            }
        }
    }



    void embed_header(byte[][][] edge_counts, byte[] header, BufferedImage image, boolean write_header) {
        int block_capacity = block_size * block_size, data_offset = 0, info_size;
        byte[] info_block;
        BufferedImage sub_image;

        for (int x_index=0;x_index<edge_counts[0].length;x_index++) {
            for (int y_index=0;y_index<edge_counts[0][0].length;y_index++) {
                edge_counts[0][x_index][y_index] = 0;   // Only zero LSB

                info_size = Math.min(block_capacity, header.length - data_offset);
                if (write_header) {
                    info_block = new byte[info_size];
                    System.arraycopy(header, data_offset, info_block, 0, info_size);

                    sub_image = image.getSubimage(x_index * block_size, x_index * block_size, block_size, block_size);
                    bpcs.embed_block(sub_image.getRaster(), info_block, block_size, 0, 0);
                }

                data_offset += info_size;
                if (data_offset >= header_length)
                    return;
            }
        }

    }
}
