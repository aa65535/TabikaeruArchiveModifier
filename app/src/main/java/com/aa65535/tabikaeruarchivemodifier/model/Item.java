package com.aa65535.tabikaeruarchivemodifier.model;

import android.util.SparseArray;

import com.aa65535.tabikaeruarchivemodifier.model.GameData.Data;
import com.aa65535.tabikaeruarchivemodifier.utils.Util;

import java.io.RandomAccessFile;

public class Item extends Data {
    private int id;
    private String name;
    private int count;

    public Item(long offset, int id, int count, RandomAccessFile r) {
        super(offset, r);
        this.id = id;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    Item setName(SparseArray<String> array) {
        this.name = array.get(id);
        return this;
    }

    public Item setCount(int count) {
        this.count = count;
        return this;
    }

    @Override
    public boolean save() {
        return Util.writeInt(r, offset(), count);
    }

    @Override
    public int length() {
        return 0x08;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Item item = (Item) o;
        return id == item.id;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name=" + name +
                ", count=" + count +
                ", offset=" + offset() +
                '}';
    }
}
