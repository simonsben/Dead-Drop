package core;

import java.awt.image.BufferedImage;

// TODO find better name - maybe tactic, approach, boat
public abstract class technique {
    public abstract void analyze_image(image img);
    public abstract void embed_data(BufferedImage image, byte[] data, int offset);
    public abstract byte[] recover_data(BufferedImage image, int data_size, int offset);

    public void embed_data(BufferedImage image, byte[] data) {
        embed_data(image, data, 0);
    }

    public byte[] recover_data(BufferedImage image, int data_size) {
        return recover_data(image, data_size, 0);
    }
}
