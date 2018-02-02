package com.aa65535.tabikaeruarchivemodifier.model;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Int extends Data {
    private int value;

    Int(RandomAccessFile r) throws IOException {
        super(r);
        this.value = r.readInt();
    }

    public int value() {
        return value;
    }

    public Int value(int value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean write() {
        try {
            r.seek(offset());
            r.writeInt(value);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int length() {
        return 0x04;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Int anInt = (Int) o;
        return value == anInt.value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return "" + value;
    }
}
