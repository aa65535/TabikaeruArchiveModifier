package com.aa65535.tabikaeruarchivemodifier.model;

import com.aa65535.tabikaeruarchivemodifier.model.DataList.ElementFactory;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Int extends Primitive<Void, Integer> {
    Int(int v) {
        this.value = v;
    }

    Int(RandomAccessFile r) throws IOException {
        super(r, null);
    }

    @Override
    protected void initialize(Void arg) throws IOException {
        this.value = r.readInt();
    }

    @Override
    public boolean save() {
        if (modified) {
            try {
                r.seek(offset());
                r.writeInt(value);
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
            r.writeInt(value);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static class IntElementFactory implements ElementFactory<Int> {
        @Override
        public Int create(RandomAccessFile r) throws IOException {
            return new Int(r);
        }
    }
}
