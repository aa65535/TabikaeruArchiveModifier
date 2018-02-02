package com.aa65535.tabikaeruarchivemodifier.model;

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
     */
    final boolean remove() {
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

    public abstract boolean write();

    public abstract int length();
}
