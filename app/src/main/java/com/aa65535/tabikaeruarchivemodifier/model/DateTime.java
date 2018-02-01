package com.aa65535.tabikaeruarchivemodifier.model;

import com.aa65535.tabikaeruarchivemodifier.model.GameData.Data;
import com.aa65535.tabikaeruarchivemodifier.utils.Util;

import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Locale;

public class DateTime extends Data {
    private Calendar calendar;

    public DateTime(long offset, int year, int month, int date, int hourOfDay, int minute,
                    int second, int millisecond, RandomAccessFile r) {
        super(offset, r);
        calendar = Calendar.getInstance();
        calendar.set(year, month, date, hourOfDay, minute, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void add(int field, int amount) {
        calendar.add(field, amount);
    }

    public String getText() {
        return String.format(Locale.getDefault(),
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL", calendar);
    }

    @Override
    public boolean save() {
        return Util.writeCalendar(r, offset(), calendar);
    }

    @Override
    public int length() {
        return 0x1c;
    }

    @Override
    public String toString() {
        return "DateTime{" +
                "calendar=" + getText() +
                ", offset=" + offset() +
                '}';
    }
}
