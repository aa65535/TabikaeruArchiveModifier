package com.aa65535.tabikaeruarchivemodifier.model;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class Str extends Data {
    private int len;
    private byte[] value;
    private boolean modified;

    Str(RandomAccessFile r, int size) throws IOException {
        super(r, size);
    }

    @Override
    protected void initialize(int size) throws IOException {
        len = r.readUnsignedShort();
        value = new byte[size - 0x02];
        r.read(value);
    }

    public String value() {
        return new String(value, 0, len);
    }

    public Str value(String value) {
        modified = !value().equals(value);
        if (modified) {
            byte[] bytes = value.getBytes();
            len = Math.min(bytes.length, this.value.length);
            System.arraycopy(bytes, 0, this.value, 0, len);
        }
        return this;
    }

    @Override
    public boolean write() {
        if (modified) {
            try {
                r.seek(offset());
                r.writeShort(len);
                r.write(value);
                modified = false;
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Str str = (Str) o;
        return len == str.len && Arrays.equals(value, str.value);
    }

    @Override
    public int hashCode() {
        int result = len;
        result = 31 * result + Arrays.hashCode(value);
        return result;
    }

    @Override
    public String toString() {
        return value();
    }
}
