/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import org.apache.commons.lang.CharUtils;

/**
 * This class provides a means to easily encrypt an plaintext from an input
 * stream and output the ciphertext in an output stream using AES algorithm and
 * and vice versa.
 *
 * @author Ezeozue Chidube
 */
@SuppressWarnings("CPD-START")
public class AesEncrypter extends com.sf.biocapture.app.BsClazz {

    /**
     * Used for logging within this class. Named after the class
     */
    /**
     * Encryption cipher
     */
    private Cipher ecipher;
    /**
     * Decryption cipher
     */
    private Cipher dcipher;

    private Cipher newDCipher;
    private Cipher newECipher;
    private Base64Coder base64Coder = new Base64Coder();

    /**
     * Creates an AesEncrypter object.
     */
    public AesEncrypter() {
        try {
            //The following is the process for starting up encryption and decryption
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            byte[] seed = "SeamfixBiocapture".getBytes();
            SecureRandom sr = new SecureRandom(seed);
            keygen.init(sr);
            // If you do not initialize the KeyGenerator, each provider supply a default initialization.
            SecretKey aeskey = keygen.generateKey();

            String key = "AES/ECB/PKCS5Padding";
            ecipher = Cipher.getInstance(key);
            dcipher = Cipher.getInstance(key);
            newECipher = Cipher.getInstance(key);
            newDCipher = Cipher.getInstance(key);

            ecipher.init(Cipher.ENCRYPT_MODE, aeskey);
            dcipher.init(Cipher.DECRYPT_MODE, aeskey);
            newECipher.init(Cipher.ENCRYPT_MODE, getDecryptSecureKey());
            newDCipher.init(Cipher.DECRYPT_MODE, getDecryptSecureKey());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            logger.error("", e);
        }
    }

    /**
     * Encrypts plaintext from an input stream and writes out ciphertext to an
     * output stream
     *
     * @param in source of plaintext
     * @param out destination of ciphertext
     */
    public void encrypt(InputStream in, OutputStream out) {
        try {
            // Buffer used to transport the bytes from one stream to another
            byte[] buf = new byte[1024];
            // Bytes written to out will be encrypted
            out = new CipherOutputStream(out, ecipher);

            // Read in the cleartext bytes and write to out to encrypt
            int numRead = 0;
            while ((numRead = in.read(buf)) >= 0) {
                out.write(buf, 0, numRead);
            }
            out.close();
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                logger.error("", e);
            }
            try {
                out.close();
            } catch (IOException e) {
                logger.error("", e);
            }
        }
    }

    /**
     * Decrypts ciphertext from an input stream and writes out plaintext to an
     * output stream
     *
     * @param in source of ciphertext
     * @param out destination of plaintext
     */
    @SuppressWarnings("resource")
    public void decrypt(InputStream in, OutputStream out) {
        try {
            // Buffer used to transport the bytes from one stream to another
            byte[] buf = new byte[1024];
            // Bytes read from in will be decrypted
            in = new CipherInputStream(in, newDCipher);

            // Read in the decrypted bytes and write the cleartext to out
            int numRead = 0;
            while ((numRead = in.read(buf)) >= 0) {
                out.write(buf, 0, numRead);
            }
            out.close();
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                logger.error("", e);
            }
            try {
                out.close();
            } catch (IOException e) {
                logger.error("", e);
            }
        }
    }

    /**
     * @author Temitope Faro
     * @return
     */
    private final SecretKey getDecryptSecureKey() {

        return new SecretKey() {
            /**
             *
             */
            private static final long serialVersionUID = 5035361746769477420L;

            @Override
            public String getFormat() {
                return "RAW";
            }

            @Override
            public byte[] getEncoded() {
                return base64Coder.decode("ZPOU-eSJIV-RUJkNWahuDA==");
            }

            @Override
            public String getAlgorithm() {
                return "AES";
            }
        };
    }

    public String decrypt(String encryptedText) {
        try {
            if (encryptedText == null || encryptedText.equals("")) {
                return encryptedText;
            }
            byte[] cp = base64Coder.decode(encryptedText);
            byte[] plain = newDCipher.doFinal(cp);
            return new String(plain, "UTF8");
        } catch (IllegalArgumentException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
            logger.error("", e);
        }

        return encryptedText;
    }

    public String forceDecrypt(String encryptedText) {
        String plainText = decrypt(encryptedText);
        //a recursive call to ensure proper clean up where recursive encryption may have occurred on a string value
        if (isEncrypted(plainText)) {
            return decrypt(plainText);
        }
        return plainText;
    }

    public String encrypt(String plainText) {
        try {
            byte[] plainTextBytes = plainText.getBytes("UTF8");
            byte[] cipherTextBytes = newECipher.doFinal(plainTextBytes);
            return new String(base64Coder.encode(cipherTextBytes));
        } catch (BadPaddingException | UnsupportedEncodingException | IllegalBlockSizeException e) {
            logger.error("", e);
        }

        return null;
    }

    private boolean isEncrypted(String text) {
        try {
            if (!isAscii(text)) {
                return true;
            }
            base64Coder.decode(text);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static boolean isAscii(String plainText) {
        for (int x = 0; x < plainText.length(); x++) {
            char ch = plainText.charAt(x);
            if (!CharUtils.isAsciiAlphanumeric(ch)) {
                return false;
            }
        }
        return true;
    }
}
