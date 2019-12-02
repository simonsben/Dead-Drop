package utilities;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

import static utilities.data_management.concat_arrays;
import static utilities.data_management.split_array;
import static utilities.strings.get_algorithm;

public class encrypter {
    private SecretKeySpec secret_key;
    private IvParameterSpec iv;

    public static int key_length = 16;
    private static String encrypt_type = "AES/CFB/NoPadding";
    private static String hash_type = "SHA-1";

    // Takes plaintext key, prepares it, and generates IV
    public void set_key(String plaintext_key) {
        try {
            MessageDigest hasher = MessageDigest.getInstance(hash_type);
            byte[] hashed_key = hasher.digest(plaintext_key.getBytes());

            secret_key = new SecretKeySpec(hashed_key, 0, key_length, get_algorithm(encrypt_type));
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
    public boolean has_key() {
        return this.secret_key != null;
    }

    // Generates an IV for the instance
    private void generate_iv() {
        if (iv != null) return;

        SecureRandom generator = new SecureRandom();
        byte[] raw_iv = new byte[key_length];

        generator.nextBytes(raw_iv);
        iv = new IvParameterSpec(raw_iv);
    }

    // Extracts IV from encoded byte stream
    private byte[] extract_iv(byte[] raw_data) {
        byte[] raw_iv = new byte[key_length];
        byte[] data = new byte[raw_data.length - key_length];
        split_array(raw_data, raw_iv, data);

        iv = new IvParameterSpec(raw_iv);
        return data;
    }

    // Run encrypt/decrypt operation
    private byte[] run_crypt_operation(int operation_mode, byte[] data)  {
        if (operation_mode == Cipher.DECRYPT_MODE) data = extract_iv(data);
        if (!this.has_key()) set_key();

        Cipher cipher;
        byte[] output = null;

        try {
            cipher = Cipher.getInstance(encrypt_type);
            cipher.init(operation_mode, secret_key, iv);
            output = cipher.doFinal(data);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        if (output != null && operation_mode != Cipher.DECRYPT_MODE)
            output = concat_arrays(iv.getIV(), output);

        return output;
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
