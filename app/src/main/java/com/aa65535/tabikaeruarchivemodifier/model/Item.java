package com.aa65535.tabikaeruarchivemodifier.model;

import android.util.SparseArray;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Item extends Data {
    private int id;
    private String name;
    private Int stock;

    Item(RandomAccessFile r) throws IOException {
        super(r);
        this.id = r.readInt();
        this.stock = new Int(r);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Int getStock() {
        return stock;
    }

    Item setName(SparseArray<String> array) {
        this.name = array.get(id);
        return this;
    }

    public Item setStock(int stock) {
        this.stock.value(stock);
        return this;
    }

    @Override
    public boolean write() {
        return stock.write();
    }

    @Override
    public int length() {
        return 0x04 + stock.length();
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
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Item{" +
                "offset=" + offset() +
                ", id=" + id +
                ", name=" + name +
                ", stock=" + stock +
                '}';
    }
}
