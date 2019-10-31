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
        int width = base_raster.getWidth(), height = base_raster.getHeight(), num_channels = base_raster.getNumBands();

        // Embed header
        byte[] header = this.get_header(data.length);
        int used_blocks = (int) Math.ceil(header.length / this.block_size);
        BufferedImage header_section = base_image.getSubimage(0, 0, used_blocks, this.block_size);
        naive.embed_data(header_section, header);

        byte[][][] edge_counts;
        for (int channel=0;channel<num_channels;channel++) {
            edge_counts = bpcs.count_edges(base_raster, channel, this.block_size);

            if (channel == 0) {
                // TODO generalize so it can accept longer headers than just one row of blocks
                for (int block=0;block<used_blocks;block++)
                    edge_counts[0][block][0] = 0;
            }



        }
    }
}
