package com.exercise.security;

import android.util.Base64;
import android.util.Log;

import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.Provider;
import java.security.Security;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Philip
 * @since 2017-12-07
 */

public class Crypto {

    private static final String PROVIDER_SPONGY_CASTLE = "SC";

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    /**
     * Test function to log the supported providers
     */
    public static void logProviders() {
        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            Log.d("Crypto", "provider: " + provider.getName());
            Set<Provider.Service> services = provider.getServices();
            for (Provider.Service service : services) {
                Log.d("Crypto", "  algorithm: " + service.getAlgorithm());
            }
        }
    }

    /**
     * Make an encrypted copy of a file using AES
     *
     * @param key        as cipher
     * @param inputFile  decrypted file
     * @param outputFile encrypted file
     * @throws Exception on cipher error
     */
    public static void encrypt(String key, File inputFile, File outputFile)
            throws Exception {
        doCrypto(Cipher.ENCRYPT_MODE, key.getBytes("UTF-8"), inputFile, outputFile);
    }

    /**
     * Make a decrypted copy of an encrypted file using AES
     *
     * @param key        as cipher
     * @param inputFile  encrypted file
     * @param outputFile decrypted file
     * @throws Exception on cipher error
     */
    public static void decrypt(String key, File inputFile, File outputFile)
            throws Exception {
        doCrypto(Cipher.DECRYPT_MODE, key.getBytes("UTF-8"), inputFile, outputFile);
    }

    /**
     * Encrypt/Decrypt from file to file using AES
     *
     * @param cipherMode encrypt or decrypt mode of the cipher
     * @param key        as cipher
     * @param inputFile  source file
     * @param outputFile destination file
     * @throws Exception on cipher error
     */
    private static void doCrypto(int cipherMode, byte[] key, File inputFile, File outputFile) throws Exception {
        DESKeySpec keySpec = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES", PROVIDER_SPONGY_CASTLE);
        SecretKey k = keyFactory.generateSecret(keySpec);

        Cipher cipher = Cipher.getInstance("DES", PROVIDER_SPONGY_CASTLE);
        cipher.init(cipherMode, k);

        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length()];
        inputStream.read(inputBytes);

        byte[] outputBytes = cipher.doFinal(inputBytes);

        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(outputBytes);

        inputStream.close();
        outputStream.close();

    }

}
