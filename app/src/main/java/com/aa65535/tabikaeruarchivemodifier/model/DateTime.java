package com.aa65535.tabikaeruarchivemodifier.model;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Locale;

public class DateTime extends Primitive<Void, Calendar> {
    public DateTime(Calendar value) {
        this.value = (Calendar) value.clone();
    }

    DateTime(RandomAccessFile r) throws IOException {
        super(r, null);
    }

    @Override
    protected void initialize(Void arg) throws IOException {
        r.skipBytes(0x04); // date len skipped
        value = Calendar.getInstance();
        value.set(Calendar.YEAR, r.readInt());
        value.set(Calendar.MONTH, r.readInt() - 1);
        value.set(Calendar.DATE, r.readInt());
        value.set(Calendar.HOUR_OF_DAY, r.readInt());
        value.set(Calendar.MINUTE, r.readInt());
        value.set(Calendar.SECOND, r.readInt());
        value.set(Calendar.MILLISECOND, r.readInt());
    }

    @Override
    public DateTime value(Calendar value) {
        return (DateTime) super.value((Calendar) value.clone());
    }

    public DateTime value(int field, int value) {
        this.value.set(field, value);
        modified = true;
        return this;
    }

    public DateTime add(int field, int amount) {
        value.add(field, amount);
        modified = true;
        return this;
    }

    @Override
    public boolean save() {
        if (modified) {
            try {
                r.seek(offset + 0x04); // date len skipped
                r.writeInt(value.get(Calendar.YEAR));
                r.writeInt(value.get(Calendar.MONTH) + 1);
                r.writeInt(value.get(Calendar.DAY_OF_MONTH));
                r.writeInt(value.get(Calendar.HOUR_OF_DAY));
                r.writeInt(value.get(Calendar.MINUTE));
                r.writeInt(value.get(Calendar.SECOND));
                r.writeInt(value.get(Calendar.MILLISECOND));
                modified = false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean write(RandomAccessFile r) {
        try {
            r.writeInt(0x07); // date len
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

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL", value);
    }
}
