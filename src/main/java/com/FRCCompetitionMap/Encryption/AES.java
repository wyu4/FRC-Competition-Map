package com.FRCCompetitionMap.Encryption;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

public class AES {
    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    public static String addPadding(String unpadded) {
        if (unpadded.length() % 4 != 0) {
            unpadded += "=".repeat(4 - unpadded.length() % 4); // Add padding
        }
        return unpadded;
    }

    public static byte[] encrypt(String input) throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128);
        SecretKey key = generator.generateKey();

        byte[] ivBytes = new byte[16];
        new SecureRandom().nextBytes(ivBytes);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        String encrypted = ENCODER.encodeToString(cipher.doFinal(input.getBytes()));
        String full = encrypted + ":" + ENCODER.encodeToString(key.getEncoded()) + ":" + ENCODER.encodeToString(iv.getIV());
        String prepared = full.replace("=", "");
        return ENCODER.encode(prepared.getBytes());
    }

    public static String decrypt(byte[] encrypted) throws Exception {
        String full = new String(DECODER.decode(encrypted));
        String[] data = full.split(":");
        if (data.length != 3) {
            throw new Exception("Could not decrypt data: incorrect number.");
        }

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(DECODER.decode(addPadding(data[1])), "AES"), new IvParameterSpec(DECODER.decode(addPadding(data[2]))));
        return new String(cipher.doFinal(DECODER.decode(addPadding(data[0]))));
    }
}
