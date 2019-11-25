package utilities;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class encrypter {
    private SecretKeySpec secret_key;
    private byte[] key;
    private byte[] iv;

    public static int iv_length = 16;
    private static String encrypt_type = "AES";
    private static String hash_type = "SHA-1";

    // Takes plaintext key, prepares it, and generates IV
    public void set_key(String plaintext_key) {
        MessageDigest sha;
        try {
            sha = MessageDigest.getInstance(hash_type);     // Initialize hasher
            key = sha.digest(plaintext_key.getBytes());     // Get hash of key
            secret_key = new SecretKeySpec(Arrays.copyOf(key, iv_length), encrypt_type);      // Generate secret key
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        generate_iv();
    }

    public void set_key() {
        System.out.println("----- WARNING: Default key being used for encryption -----");
        set_key("123456");
    }

    // Checks whether the instance has a key
    boolean has_key() {
        return this.secret_key != null;
    }

    // Generates an IV for the instance
    void generate_iv() {
        SecureRandom generator = new SecureRandom();

        this.iv = new byte[iv_length];
        generator.nextBytes(this.iv);
    }

    // Run encrypt/decrypt operation
    byte[] run_crypt_operation(int operation_mode, byte[] data)  {
        if (!this.has_key()) set_key();

        if (operation_mode == Cipher.ENCRYPT_MODE)
            data = data_management.concat_arrays(this.iv, data);
        else if(operation_mode == Cipher.DECRYPT_MODE) {
            byte[] iv = new byte[iv_length], payload = new byte[data.length - iv_length];
            data_management.split_array(data, iv, payload);

            data = payload;
            this.iv = iv;
        }

        Cipher cipher;
        try {
            cipher = Cipher.getInstance(encrypt_type);
            cipher.init(operation_mode, this.secret_key);
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Encrypt data
    public byte[] encrypt_data(byte[] data) {
        return run_crypt_operation(Cipher.ENCRYPT_MODE, data);
    }

    // Decrypt data
    public byte[] decrypt_data(byte[] data) {
        return run_crypt_operation(Cipher.DECRYPT_MODE, data);
    }
}
