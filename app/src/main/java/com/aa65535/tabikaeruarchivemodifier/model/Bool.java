package com.aa65535.tabikaeruarchivemodifier.model;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Bool extends SimpleData<Void, Boolean> {
    Bool(RandomAccessFile r) throws IOException {
        super(r, null);
    }

    @Override
    protected void initialize(Void arg) throws IOException {
        this.value = r.readBoolean();
    }

    @Override
    public boolean write() {
        if (modified) {
            try {
                r.seek(offset());
                r.writeBoolean(value);
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
