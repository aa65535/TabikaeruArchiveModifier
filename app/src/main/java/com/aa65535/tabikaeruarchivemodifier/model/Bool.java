package com.aa65535.tabikaeruarchivemodifier.model;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Bool extends Data {
    private boolean value;
    private boolean modified;

    Bool(RandomAccessFile r) throws IOException {
        super(r);
        this.value = r.readBoolean();
    }

    public boolean value() {
        return value;
    }

    public Bool value(boolean value) {
        this.modified = this.value != value;
        this.value = value;
        return this;
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

    @Override
    public int length() {
        return 0x1c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Bool bool = (Bool) o;
        return value == bool.value;
    }

    @Override
    public int hashCode() {
        return (value ? 1 : 0);
    }

    @Override
    public String toString() {
        return "" + value;
    }
}
