package com.aa65535.tabikaeruarchivemodifier.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SuppressWarnings("unused")
public final class ImageHash {

    public static String calcHash(Bitmap img) {
        if (img != null) {
            int i = 0;
            int w = 492;
            int h = 342;
            byte[] bytes = new byte[30492];
            for (int x = 8; x < w; x += 4) {
                for (int y = 8; y < h; y += 4) {
                    int pixel = img.getPixel(x, y);
                    bytes[i++] = (byte) Color.red(pixel);
                    bytes[i++] = (byte) Color.green(pixel);
                    bytes[i++] = (byte) Color.blue(pixel);
                }
            }
            return sha256(bytes);
        }
        return null;
    }

    @Nullable
    public static String sha256(byte[] input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(input);
            byte[] output = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : output) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
