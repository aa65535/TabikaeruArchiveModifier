package com.aa65535.tabikaeruarchivemodifier.model;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Locale;

public class DateTime extends Data {
    private Calendar calendar;

    DateTime(RandomAccessFile r) throws IOException {
        super(r);
        r.skipBytes(0x04); // date len skipped
        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, r.readInt());
        calendar.set(Calendar.MONTH, r.readInt() - 1);
        calendar.set(Calendar.DATE, r.readInt());
        calendar.set(Calendar.HOUR_OF_DAY, r.readInt());
        calendar.set(Calendar.MINUTE, r.readInt());
        calendar.set(Calendar.SECOND, r.readInt());
        calendar.set(Calendar.MILLISECOND, r.readInt());
    }

    public Calendar value() {
        return calendar;
    }

    public DateTime set(int field, int value) {
        calendar.set(field, value);
        return this;
    }

    public void add(int field, int amount) {
        calendar.add(field, amount);
    }

    public String getText() {
        return String.format(Locale.getDefault(),
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL", calendar);
    }

    @Override
    public boolean write() {
        try {
            r.seek(offset + 0x04);
            r.writeInt(calendar.get(Calendar.YEAR));
            r.writeInt(calendar.get(Calendar.MONTH) + 1);
            r.writeInt(calendar.get(Calendar.DAY_OF_MONTH));
            r.writeInt(calendar.get(Calendar.HOUR_OF_DAY));
            r.writeInt(calendar.get(Calendar.MINUTE));
            r.writeInt(calendar.get(Calendar.SECOND));
            r.writeInt(calendar.get(Calendar.MILLISECOND));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int length() {
        return 0x1c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DateTime dateTime = (DateTime) o;

        return calendar.equals(dateTime.calendar);
    }

    @Override
    public int hashCode() {
        return calendar.hashCode();
    }

    @Override
    public String toString() {
        return "DateTime{" +
                "offset=" + offset() +
                ", datetime=" + getText() +
                '}';
    }
}
