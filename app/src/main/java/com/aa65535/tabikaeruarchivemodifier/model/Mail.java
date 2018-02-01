package com.aa65535.tabikaeruarchivemodifier.model;

import com.aa65535.tabikaeruarchivemodifier.model.GameData.Data;
import com.aa65535.tabikaeruarchivemodifier.utils.Util;

import java.io.RandomAccessFile;

public class Mail extends Data {
    private int id;
    private int type;
    private String title;
    private int clover;
    private Item item;
    private DateTime datetime;

    public Mail(long offset, int id, int type, String title, int clover, Item item,
                DateTime datetime, RandomAccessFile r) {
        super(offset, r);
        this.id = id;
        this.type = type;
        this.title = title;
        this.clover = clover;
        this.item = item;
        this.datetime = datetime;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public int getClover() {
        return clover;
    }

    public Item getItem() {
        return item;
    }

    public DateTime getDatetime() {
        return datetime;
    }

    public Mail setType(int type) {
        this.type = type;
        return this;
    }

    public Mail setClover(int clover) {
        this.clover = clover;
        return this;
    }

    @Override
    public boolean save() {
        return Util.writeInt(r, offset() + 0x58, type)
                && Util.writeInt(r, offset() + 0x5a, clover);
    }

    @Override
    public int length() {
        return 0x92;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Mail mail = (Mail) o;
        return id == mail.id;
    }

    @Override
    public String toString() {
        return "Mail{" +
                "id=" + id +
                ", type=" + type +
                ", title=" + title +
                ", clover=" + clover +
                ", item=" + item +
                ", datetime=" + datetime +
                ", offset=" + offset() +
                '}';
    }
}
