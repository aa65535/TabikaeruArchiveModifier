package com.aa65535.tabikaeruarchivemodifier.model;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.aa65535.tabikaeruarchivemodifier.R;
import com.aa65535.tabikaeruarchivemodifier.utils.Util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GameData {
    private final File archive;

    private int clover;
    private long cloverOffset;
    private int tickets;
    private long ticketsOffset;
    private Calendar gameDate;
    private long gameDateOffset;
    private int gameTimes;
    private long gameTimesOffset;
    private List<Mail> mailList;
    private List<Item> itemList;

    public GameData(File archive) {
        this.archive = archive;
    }

    public GameData load(Context context) {
        RandomAccessFile r = null;
        int[] item_ids = context.getResources().getIntArray(R.array.item_ids);
        String[] item_names = context.getResources().getStringArray(R.array.item_names);
        SparseArray<String> array = new SparseArray<>(item_ids.length);
        for (int i = 0; i < item_ids.length; i++) {
            array.put(i, item_names[i]);
        }
        try {
            r = new RandomAccessFile(archive, "r");
            r.seek(0x08);
            int c = r.readInt(); // unkown byte len
            cloverOffset = Util.forwardPointer(r, c);
            r.seek(cloverOffset);
            clover = r.readInt();
            ticketsOffset = r.getFilePointer();
            tickets = r.readInt();

            c = r.readInt(); // unkown byte len
            Util.forwardPointer(r, c); // unkown data passed

            for (int i = 0; i < 19; i++) {
                Util.readCalendar(r); // unkown date passed
                Util.forwardPointer(r, 0x19); // unkown data passed
            }

            Util.readCalendar(r); // unkown date passed
            Util.forwardPointer(r, 0x05); // unkown data passed

            gameDateOffset = r.getFilePointer();
            gameDate = Util.readCalendar(r); // read game date
            gameTimesOffset = r.getFilePointer();
            gameTimes = r.readInt(); // game times

            c = r.readInt(); // mail entry count
            mailList = new ArrayList<>(c);
            for (int i = 0; i < c; i++) {
                mailList.add(Util.readMailbox(r));
            }

            c = r.readInt(); // item entry count
            itemList = new ArrayList<>(c);
            for (int i = 0; i < c; i++) {
                itemList.add(Util.readItem(r).setName(array));
            }

            Util.forwardPointer(r, 0x70); // unkown data passed (bag & table)

            c = r.readInt(); // souvenir date len
            Util.forwardPointer(r, c); // souvenir date passed

            c = r.readInt(); // blank date len
            Util.forwardPointer(r, c * 4); // blank date passed

            c = r.readInt(); // speciality date len
            long offset = Util.forwardPointer(r, c);// speciality date passed
            Log.d("GameData", "offset: " + offset);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Util.closeQuietly(r);
        }
        return this;
    }

    public int getClover() {
        return clover;
    }

    public int getTickets() {
        return tickets;
    }

    public Calendar getGameDate() {
        return gameDate;
    }

    public int getGameTimes() {
        return gameTimes;
    }

    public List<Mail> getMailList() {
        return mailList;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public boolean setClover(int clover) {
        return Util.writeInt(archive, cloverOffset, clover);
    }

    public boolean setTickets(int tickets) {
        return Util.writeInt(archive, ticketsOffset, tickets);
    }

    public boolean setGameDate(Calendar gameDate) {
        return Util.writeCalendar(archive, gameDateOffset + 4, gameDate);
    }

    public boolean setGameTimes(int gameTimes) {
        return Util.writeInt(archive, gameTimesOffset, gameTimes);
    }

    public boolean setItemCount(int id, int count) {
        int index = itemList.indexOf(new Item(id));
        Item item = itemList.get(index);
        return Util.writeInt(archive, item.offset, count);
    }

    static class Data {
        final long offset;

        Data(long offset) {
            this.offset = offset;
        }

        public long getOffset() {
            return offset;
        }
    }
}
