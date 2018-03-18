package com.aa65535.tabikaeruarchivemodifier.model;

import com.aa65535.tabikaeruarchivemodifier.model.DataList.ElementFactory;

import java.io.IOException;
import java.io.RandomAccessFile;

@SuppressWarnings("unused")
public class Item extends Data<Void> {
    public static final int MAX_STOCK = 99;

    private int id;
    private Int stock;

    Item(RandomAccessFile r) throws IOException {
        super(r, null);
    }

    @Override
    protected void initialize(Void arg) throws IOException {
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
    public boolean save() {
        return stock.save();
    }

    @Override
    public boolean write(RandomAccessFile r) {
        try {
            r.writeInt(id);
            return stock.write(r);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
                "offset=" + offset +
                ", length=" + length +
                ", id=" + id +
                ", stock=" + stock +
                ", durable=" + durable() +
                '}';
    }

    public static class ItemElementFactory implements ElementFactory<Item> {
        @Override
        public Item create(RandomAccessFile r) throws IOException {
            return new Item(r);
        }
    }
}
