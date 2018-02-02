package com.aa65535.tabikaeruarchivemodifier.utils;

import android.support.annotation.NonNull;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

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

    @NonNull
    public static byte[] fileToByteArray(File file, long position) {
        FileChannel fc = null;
        try {
            fc = new RandomAccessFile(file, "r").getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) (fc.size() - position));
            fc.read(byteBuffer, position);
            return byteBuffer.array();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(fc);
        }
        return new byte[0];
    }
}
