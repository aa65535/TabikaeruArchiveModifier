package com.aa65535.tabikaeruarchivemodifier.model;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.aa65535.tabikaeruarchivemodifier.R;
import com.aa65535.tabikaeruarchivemodifier.utils.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public final class GameData {
    private final RandomAccessFile r;
    private final SparseArray<String> nameArray;

    private int clover;
    private long cloverOffset;
    private int tickets;
    private long ticketsOffset;
    private int travelTimes;
    private long travelTimesOffset;
    private DateTime lastGameTime;
    private long mailEntryCountOffset;
    private List<Mail> mailList;
    private long itemEntryCountOffset;
    private List<Item> itemList;

    private GameData(Context context, File archive) throws FileNotFoundException {
        r = new RandomAccessFile(archive, "rwd");
        Resources resources = context.getResources();
        int[] item_ids = resources.getIntArray(R.array.item_ids);
        String[] item_names = resources.getStringArray(R.array.item_names);
        nameArray = new SparseArray<>(item_ids.length);
        for (int i = 0; i < item_ids.length; i++) {
            nameArray.put(item_ids[i], item_names[i]);
        }
    }

    private GameData load() throws IOException {
        r.seek(0x08);
        int c = r.readInt(); // unkown byte len
        r.skipBytes(c); // unkown data passed

        cloverOffset = r.getFilePointer();
        clover = r.readInt();
        ticketsOffset = r.getFilePointer();
        tickets = r.readInt();

        c = r.readInt(); // unkown byte len
        r.skipBytes(c); // unkown data passed

        for (int i = 0; i < 19; i++) {
            Util.readDateTime(r); // unkown date passed
            r.skipBytes(0x19); // unkown data passed
        }

        Util.readDateTime(r); // unkown date passed
        r.skipBytes(0x05); // unkown data passed

        lastGameTime = Util.readDateTime(r); // read game date
        travelTimesOffset = r.getFilePointer();
        travelTimes = r.readInt(); // travel times

        mailEntryCountOffset = r.getFilePointer();
        c = r.readInt(); // mail entry count
        mailList = new ArrayList<>(c);
        for (int i = 0; i < c; i++) {
            mailList.add(Util.readMail(r)); // read mail
        }

        itemEntryCountOffset = r.getFilePointer();
        c = r.readInt(); // item entry count
        itemList = new ArrayList<>(c);
        for (int i = 0; i < c; i++) {
            itemList.add(Util.readItem(r).setName(nameArray)); // read item
        }

        if (false) {
            r.skipBytes(0x70); // unkown data passed (? bag & table)

            c = r.readInt(); // souvenir data len, c = 64
            r.skipBytes(c); // souvenir data passed

            c = r.readInt(); // blank int data len, c = 64
            r.skipBytes(c * 4); // blank int data passed

            c = r.readInt(); // speciality data len, c = 64
            r.skipBytes(c); // speciality data passed
            //3d02
            r.skipBytes(0x01ac); // unkown data passed
            //3eae
            Util.readDateTime(r); // unkown date passed
            //3ece
            r.skipBytes(0x01a9); // unkown data passed
            //4077
            Util.readDateTime(r); // unkown date passed
            //4097
            r.skipBytes(0x01a9); // unkown data passed
            //4240
            Util.readDateTime(r); // unkown date passed
            //4260
            r.skipBytes(0x01a9); // unkown data passed
            //4409
            Util.readDateTime(r); // unkown date passed
            //4429
            r.skipBytes(0x01a9); // unkown data passed
            //4502
            Util.readDateTime(r); // unkown date passed
            //45f2
            r.skipBytes(0x01a9); // unkown data passed
            //479b
            Util.readDateTime(r); // unkown date passed
            //47bb
            r.skipBytes(0x01a9); // unkown data passed
            //4964
            Util.readDateTime(r); // unkown date passed
            //4984
            r.skipBytes(0x01a9); // unkown data passed
            //4b2d
            Util.readDateTime(r); // unkown date passed
            //4b4d
            r.skipBytes(0x01a9); // unkown data passed
            //4cf6
            Util.readDateTime(r); // unkown date passed
            //4d16
            r.skipBytes(0x0b); // unkown data passed

            c = r.readUnsignedShort(); // unkown data len, c = 7
            r.skipBytes(c); // unkown data passed

            c = r.readUnsignedShort(); // name data len
            Util.readString(r, 0x16, c); // name data

            c = r.readInt(); // unkown data len, c = 64
            r.skipBytes(c); // unkown data passed (? honour)
        }
        return this;
    }

    public void reload() {
        try {
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public static GameData load(Context context, File archive) {
        try {
            return new GameData(context, archive).load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getClover() {
        return clover;
    }

    public int getTickets() {
        return tickets;
    }

    public int getTravelTimes() {
        return travelTimes;
    }

    public DateTime getLastGameTime() {
        return lastGameTime;
    }

    public List<Mail> getMailList() {
        return mailList;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public boolean setClover(int clover) {
        this.clover = clover;
        return Util.writeInt(r, cloverOffset, clover);
    }

    public boolean setTickets(int tickets) {
        this.tickets = tickets;
        return Util.writeInt(r, ticketsOffset, tickets);
    }

    public boolean setTravelTimes(int travelTimes) {
        this.travelTimes = travelTimes;
        return Util.writeInt(r, travelTimesOffset, travelTimes);
    }

    public void destroy() {
        Util.closeQuietly(r);
    }

    @Override
    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }

    @Override
    public String toString() {
        return "GameData{" +
                "clover=" + clover +
                ", cloverOffset=" + cloverOffset +
                ", tickets=" + tickets +
                ", ticketsOffset=" + ticketsOffset +
                ", travelTimes=" + travelTimes +
                ", travelTimesOffset=" + travelTimesOffset +
                ", lastGameTime=" + lastGameTime +
                '}';
    }

    static abstract class Data {
        long offset;
        final RandomAccessFile r;

        Data(long offset, RandomAccessFile r) {
            this.offset = offset;
            this.r = r;
        }

        public long offset() {
            return offset;
        }

        final boolean remove() {
            return Util.removeData(r, offset, length());
        }

        public abstract boolean save();

        public abstract int length();
    }
}
