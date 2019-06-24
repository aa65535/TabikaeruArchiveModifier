package com.aa65535.tabikaeruarchivemodifier.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Util {

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    @Nullable
    public static byte[] readAlbumFile(File album) {
        RandomAccessFile r = null;
        try {
            r = new RandomAccessFile(album, "r");
            int len = r.readInt();
            byte[] bytes = new byte[len];
            r.readFully(bytes);
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Util.closeQuietly(r);
        }
        return null;
    }

    @Nullable
    public static Bitmap readAlbumBitmap(File album) {
        byte[] bytes = readAlbumFile(album);
        if (bytes != null) {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return null;
    }
}
