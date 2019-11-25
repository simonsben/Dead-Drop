package core;

import utilities.encrypter;


// TODO find better name - maybe tactic, approach, boat
public abstract class technique {
    encrypter encrypt_manager;

    public technique(String plaintext) {
        set_encryption_key(plaintext);
    }

    public technique() {
        encrypt_manager = new encrypter();
    }

    public abstract void analyze_image(image img);
    public abstract int embed_data(image img, byte[] data, int offset);
    public abstract byte[] recover_data(image img, int data_size, int offset);

    public int embed_data(image img, byte[] data) {
        return embed_data(img, data, 0);
    }

    public byte[] recover_data(image img, int data_size) {
        return recover_data(img, data_size, 0);
    }

    public void set_encryption_key(String plaintext) {
        if (encrypt_manager == null) encrypt_manager = new encrypter();
        encrypt_manager.set_key(plaintext);
    }
}
