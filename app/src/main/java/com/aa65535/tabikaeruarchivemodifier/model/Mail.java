package com.aa65535.tabikaeruarchivemodifier.model;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Mail extends Data {
    private int id;
    private Type type;
    private Str title;
    private Str message;
    private Int clover;
    private Int ticket;
    private Item item;
    private DateTime datetime;
    private Bool opened;
    private Bool protect;

    public static class Type extends Int {
        public static final int NONE = -1;
        public static final int MESSAGE = 0;
        public static final int PICTURE = 1;
        public static final int GIFT = 2;
        public static final int MANAGEMENT = 3;
        public static final int LEAFLET = 4;

        Type(RandomAccessFile r) throws IOException {
            super(r);
        }

        @Override
        public String toString() {
            switch (value()) {
                case MESSAGE:
                    return "MESSAGE";
                case PICTURE:
                    return "PICTURE";
                case GIFT:
                    return "GIFT";
                case MANAGEMENT:
                    return "MANAGEMENT";
                case LEAFLET:
                    return "LEAFLET";
            }
            return "NONE";
        }
    }

    Mail(RandomAccessFile r) throws IOException {
        super(r);
        this.title = new Str(r, 0x28);
        this.message = new Str(r, 0x28);
        r.skipBytes(0x04); // id, skipped
        r.skipBytes(0x04); // sender chara id, skipped
        this.type = new Type(r);
        this.clover = new Int(r);
        this.ticket = new Int(r);
        this.item = new Item(r);
        this.id = r.readInt();
        this.datetime = new DateTime(r);
        this.opened = new Bool(r);
        this.protect = new Bool(r);
    }

    public int getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public Str getTitle() {
        return title;
    }

    public Str getMessage() {
        return message;
    }

    public Int getClover() {
        return clover;
    }

    public Int getTicket() {
        return ticket;
    }

    public Item getItem() {
        return item;
    }

    public DateTime getDatetime() {
        return datetime;
    }

    public Bool getOpened() {
        return opened;
    }

    public Bool getProtect() {
        return protect;
    }

    public Mail setType(int type) {
        this.type.value(type);
        return this;
    }

    public Mail setClover(int clover) {
        this.clover.value(clover);
        return this;
    }

    @Override
    public boolean write() {
        return type.write() && title.write() && message.write()
                && clover.write() && ticket.write() && item.write()
                && datetime.write() && opened.write() && protect.write();
    }

    @Override
    public int length() { // 0x92
        return title.length() + message.length() + 0x04 + 0x04 + type.length()
                + clover.length() + ticket.length() + item.length() + 0x04
                + datetime.length() + opened.length() + protect.length();
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
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Mail{" +
                "offset=" + offset +
                ", id=" + id +
                ", type=" + type +
                ", title=" + title +
                ", message=" + message +
                ", clover=" + clover +
                ", ticket=" + ticket +
                ", item=" + item +
                ", datetime=" + datetime +
                ", opened=" + opened +
                ", protect=" + protect +
                '}';
    }
}
