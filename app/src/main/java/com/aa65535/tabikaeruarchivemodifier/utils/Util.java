package com.aa65535.tabikaeruarchivemodifier.utils;

import android.support.annotation.NonNull;

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

    public static long forwardPointer(RandomAccessFile r, long len) throws IOException {
        r.seek(r.getFilePointer() + len);
        return r.getFilePointer();
    }

    public static Calendar readCalendar(RandomAccessFile r) throws IOException {
        Calendar calendar = Calendar.getInstance();
        r.readInt(); // date len
        calendar.set(
                r.readInt(), // year
                r.readInt() - 1, // month
                r.readInt(), // day of month
                r.readInt(), // hour of day
                r.readInt(), // minute
                r.readInt() // second
        );
        r.readInt(); // millisecond passed
        return calendar;
    }

    @NonNull
    public static Mail readMailbox(RandomAccessFile r) throws IOException {
        long offset = r.getFilePointer();
        int len = r.readUnsignedShort();
        byte[] buffer = new byte[0x56];
        r.read(buffer);
        String title = new String(buffer, 0, len);
        forwardPointer(r, 0x18); // unkown data passed
        Calendar datetime = readCalendar(r);
        forwardPointer(r, 0x02); // blank data passed
        return new Mail(offset, title, datetime);
    }

    @NonNull
    public static Item readItem(RandomAccessFile r) throws IOException {
        long offset = r.getFilePointer();
        int id = r.readInt(); // item id
        int count = r.readInt(); // item count
        return new Item(offset, id, count);
    }

    public static boolean writeInt(File archive, long offset, int value) {
        RandomAccessFile r = null;
        try {
            r = new RandomAccessFile(archive, "rwd");
            r.seek(offset);
            r.writeInt(value);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(r);
        }
        return false;
    }

    public static boolean writeCalendar(File archive, long offset, Calendar value) {
        RandomAccessFile r = null;
        try {
            r = new RandomAccessFile(archive, "rwd");
            r.seek(offset);
            r.writeInt(value.get(Calendar.YEAR));
            r.writeInt(value.get(Calendar.MONTH) + 1);
            r.writeInt(value.get(Calendar.DAY_OF_MONTH));
            r.writeInt(value.get(Calendar.HOUR_OF_DAY));
            r.writeInt(value.get(Calendar.MINUTE));
            r.writeInt(value.get(Calendar.SECOND));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(r);
        }
        return false;
    }
}
