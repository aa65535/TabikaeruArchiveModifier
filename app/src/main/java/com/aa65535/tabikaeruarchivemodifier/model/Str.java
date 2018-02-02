package com.aa65535.tabikaeruarchivemodifier.model;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class Str extends Data {
    private int length;
    private byte[] value;

    Str(RandomAccessFile r, int size) throws IOException {
        super(r);
        length = r.readUnsignedShort();
        value = new byte[size - 0x02];
        r.read(value);
    }

    public String value() {
        return new String(value, 0, length);
    }

    public Str value(String value) {
        byte[] bytes = value.getBytes();
        length = Math.min(bytes.length, this.value.length);
        System.arraycopy(bytes, 0, this.value, 0, length);
        return this;
    }

    @Override
    public boolean write() {
        try {
            r.seek(offset());
            r.writeShort(length);
            r.write(value);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int length() {
        return 0x02 + value.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Str str = (Str) o;
        return length == str.length && Arrays.equals(value, str.value);
    }

    @Override
    public int hashCode() {
        int result = length;
        result = 31 * result + Arrays.hashCode(value);
        return result;
    }

    @Override
    public String toString() {
        return value();
    }
}
