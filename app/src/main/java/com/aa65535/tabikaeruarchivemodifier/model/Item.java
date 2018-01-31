package com.aa65535.tabikaeruarchivemodifier.model;

import android.util.SparseArray;

import com.aa65535.tabikaeruarchivemodifier.model.GameData.Data;

public class Item extends Data {
    private final int id;
    private String name;
    private int count;

    Item(int id) {
        super(-1);
        this.id = id;
    }

    public Item(long offset, int id, int count) {
        super(offset);
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Item item = (Item) o;
        return id == item.id;
    }
}
