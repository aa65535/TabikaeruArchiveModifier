package com.aa65535.tabikaeruarchivemodifier.model;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.RandomAccessFile;

abstract class Data {
    final long offset;
    final RandomAccessFile r;

    Data(RandomAccessFile r) throws IOException {
        this.offset = r.getFilePointer();
        this.r = r;
    }

    public long offset() {
        return offset;
    }

    /**
     * remove data from the save file
     * 
     * @hide
     */
    boolean remove() {
        try {
            long len = r.length();
            long off = offset + length();
            r.seek(off);
            byte[] buf = new byte[(int) (len - off)];
            r.readFully(buf);
            r.seek(offset);
            r.write(buf);
            r.setLength(len - length());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @NonNull
    public static String readString(RandomAccessFile r, int bs) throws IOException {
        int len = r.readUnsignedShort();
        byte[] buffer = new byte[bs];
        r.read(buffer);
        return new String(buffer, 0, len);
    }

    public abstract boolean write();

    public abstract int length();
}
