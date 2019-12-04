package core;

import utilities.Encrypter;


// TODO find better name - maybe tactic, approach, boat
public abstract class technique {
    Encrypter encrypt_manager;

    public technique(String plaintext) {
        set_encryption_key(plaintext);
    }

    public technique() {
        encrypt_manager = new Encrypter();
    }

    public abstract void analyze_image(Image img);
    public abstract int embed_data(Image img, byte[] data, int offset);
    public abstract byte[] recover_data(Image img, int data_size, int offset);

    public int embed_data(Image image, byte[] data) {
        return embed_data(image, data, 0);
    }

    public byte[] recover_data(Image image, int data_size) {
        return recover_data(image, data_size, 0);
    }

    public void set_encryption_key(String plaintext) {
        if (encrypt_manager == null) encrypt_manager = new Encrypter();
        encrypt_manager.set_key(plaintext);
    }
}
