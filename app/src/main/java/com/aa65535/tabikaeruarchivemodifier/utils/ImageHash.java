package com.aa65535.tabikaeruarchivemodifier.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SuppressWarnings("unused")
public final class ImageHash {
    private byte[] bytes = new byte[18144];

    public String calcHash(Bitmap img) {
        if (img != null) {
            int i = 0;
            for (int x = 8; x < 492; x += 3) {
                for (int y = 8; y < 342; y += 3) {
                    int pixel = img.getPixel(x, y);
                    switch (i % 3) {
                        case 0:
                            bytes[i++] = (byte) Color.red(pixel);
                            break;
                        case 1:
                            bytes[i++] = (byte) Color.green(pixel);
                            break;
                        case 2:
                            bytes[i++] = (byte) Color.blue(pixel);
                            break;
                    }
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
