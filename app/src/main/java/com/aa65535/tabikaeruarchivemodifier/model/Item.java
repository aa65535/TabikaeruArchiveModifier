package com.aa65535.tabikaeruarchivemodifier.model;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Item extends Data {
    public static final int MAX_STOCK = 99;

    private int id;
    private Int stock;

    Item(RandomAccessFile r) throws IOException {
        super(r);
        this.id = r.readInt();
        this.stock = new Int(r);
    }

    public int getId() {
        return id;
    }

    public Int stock() {
        return stock;
    }

    public Item stock(int stock) {
        stock = durable() || stock < 1 ? 1 : (stock > MAX_STOCK ? MAX_STOCK : stock);
        this.stock.value(stock);
        return this;
    }

    public boolean durable() {
        return id != -1 && (id == 1001 || id / 1000 == 2);
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
                ", stock=" + stock +
                ", durable=" + durable() +
                '}';
    }
}
