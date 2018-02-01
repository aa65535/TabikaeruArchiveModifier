package com.aa65535.tabikaeruarchivemodifier.utils;

import android.support.annotation.NonNull;

import com.aa65535.tabikaeruarchivemodifier.model.DateTime;
import com.aa65535.tabikaeruarchivemodifier.model.Item;
import com.aa65535.tabikaeruarchivemodifier.model.Mail;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Calendar;

public class Util {
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    @NonNull
    public static byte[] fileToByteArray(File file, long position) {
        FileChannel fc = null;
        try {
            fc = new RandomAccessFile(file, "r").getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) (fc.size() - position));
            fc.read(byteBuffer, position);
            return byteBuffer.array();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(fc);
        }
        return new byte[0];
    }

    @NonNull
    public static DateTime readDateTime(RandomAccessFile r) throws IOException {
        r.skipBytes(0x04); // date len = 7
        return new DateTime(r.getFilePointer(),
                r.readInt(), // year
                r.readInt() - 1, // month
                r.readInt(), // day of month
                r.readInt(), // hour of day
                r.readInt(), // minute
                r.readInt(),
                r.readInt(), // millisecond passed
                r);
    }

    @NonNull
    public static String readString(RandomAccessFile r, int bs, int len) throws IOException {
        byte[] buffer = new byte[bs];
        r.read(buffer);
        return new String(buffer, 0, len);
    }

    @NonNull
    public static Mail readMail(RandomAccessFile r) throws IOException {
        long offset = r.getFilePointer();
        int len = r.readUnsignedShort();
        String title = readString(r, 0x56, len);
        int type = r.readInt();
        int clover = r.readInt();
        r.skipBytes(0x04); // blank data passed
        Item item = readItem(r);
        int id = r.readInt();
        DateTime datetime = readDateTime(r);
        r.skipBytes(0x02); // blank data passed
        return new Mail(offset, id, type, title, clover, item, datetime, r);
    }

    @NonNull
    public static Item readItem(RandomAccessFile r) throws IOException {
        long offset = r.getFilePointer();
        int id = r.readInt(); // item id
        int count = r.readInt(); // item count
        return new Item(offset, id, count, r);
    }

    public static boolean writeInt(RandomAccessFile r, long offset, int value) {
        try {
            r.seek(offset);
            r.writeInt(value);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean writeCalendar(RandomAccessFile r, long offset, Calendar value) {
        try {
            r.seek(offset);
            r.writeInt(value.get(Calendar.YEAR));
            r.writeInt(value.get(Calendar.MONTH) + 1);
            r.writeInt(value.get(Calendar.DAY_OF_MONTH));
            r.writeInt(value.get(Calendar.HOUR_OF_DAY));
            r.writeInt(value.get(Calendar.MINUTE));
            r.writeInt(value.get(Calendar.SECOND));
            r.writeInt(value.get(Calendar.MILLISECOND));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean removeData(RandomAccessFile r, long offset, int length) {
        try {
            long len = r.length();
            long off = offset + length;
            r.seek(off);
            byte[] buf = new byte[(int) (len - off)];
            r.readFully(buf);
            r.seek(offset);
            r.write(buf);
            r.setLength(len - length);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
