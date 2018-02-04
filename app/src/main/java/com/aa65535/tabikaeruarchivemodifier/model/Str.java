package com.aa65535.tabikaeruarchivemodifier.model;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Str extends SimpleData<Integer, String> {
    private int size;

    Str(RandomAccessFile r, int size) throws IOException {
        super(r, size);
    }

    @Override
    protected void initialize(Integer size) throws IOException {
        this.size = (size -= 0x02);
        int len = r.readUnsignedShort();
        byte[] buffer = new byte[size];
        r.readFully(buffer);
        value = new String(buffer, 0, len);
    }

    @Override
    public boolean save() {
        if (modified) {
            try {
                byte[] bytes = value.getBytes();
                int len = Math.min(size, bytes.length);
                r.seek(offset());
                r.writeShort(len);
                r.write(bytes, 0, len);
                modified = false;
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }
}
