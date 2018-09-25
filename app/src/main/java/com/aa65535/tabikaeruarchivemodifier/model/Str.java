package com.aa65535.tabikaeruarchivemodifier.model;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class Str extends SimpleData<Integer, String> {
    private int len;
    private byte[] buffer;

    Str(RandomAccessFile r, int size) throws IOException {
        super(r, size);
    }

    @Override
    protected void initialize(Integer size) throws IOException {
        this.len = r.readUnsignedShort();
        this.buffer = new byte[size - 0x02];
        r.readFully(buffer);
        this.value = new String(buffer, 0, len);
    }

    @Override
    public Str value(String value) {
        if (!this.value.equals(value)) {
            byte[] bytes = value.getBytes();
            if (bytes.length > buffer.length) {
                throw new IllegalArgumentException("value length > " + buffer.length);
            }
            this.len = bytes.length;
            System.arraycopy(bytes, 0, buffer, 0, len);
            Arrays.fill(buffer, len, buffer.length, (byte) 0);
            this.value = new String(buffer, 0, len);
            modified = true;
        }
        return this;
    }

    @Override
    public boolean save() {
        if (modified) {
            try {
                r.seek(offset());
                r.writeShort(len);
                r.write(buffer);
                modified = false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean write(RandomAccessFile r) {
        try {
            r.writeShort(len);
            r.write(buffer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
