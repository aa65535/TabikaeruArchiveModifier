package com.aa65535.tabikaeruarchivemodifier.model;

import com.aa65535.tabikaeruarchivemodifier.model.DataList.ElementFactory;
import com.aa65535.tabikaeruarchivemodifier.model.Int.IntElementFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;

@SuppressWarnings("unused")
public class Event extends Data<Void> {
    private int id;
    private int evtId;
    private Int timeSpanSec;
    private Int activeTime;
    private Type evtType;
    private DataList<Int> evtValue;
    private DateTime addTime;
    private Bool trigger;

    public static class Type extends Int {
        public static final Type NONE = new Type(-1);
        public static final Type GO_TRAVEL = new Type(0);
        public static final Type BACK_HOME = new Type(1);
        public static final Type PICTURE = new Type(2);
        public static final Type DRIFT = new Type(3);
        public static final Type RETURN = new Type(4);
        public static final Type FRIEND = new Type(5);
        public static final Type GIFT = new Type(6);

        Type(int v) {
            super(v);
        }

        Type(RandomAccessFile r) throws IOException {
            super(r);
        }

        @Override
        public String toString() {
            switch (value()) {
                case 0:
                    return "GO_TRAVEL";
                case 1:
                    return "BACK_HOME";
                case 2:
                    return "PICTURE";
                case 3:
                    return "DRIFT";
                case 4:
                    return "RETURN";
                case 5:
                    return "FRIEND";
                case 6:
                    return "GIFT";
            }
            return "NONE";
        }
    }

    Event(RandomAccessFile r) throws IOException {
        super(r, null);
    }

    @Override
    protected void initialize(Void arg) throws IOException {
        this.id = r.readInt();
        this.timeSpanSec = new Int(r);
        this.activeTime = new Int(r);
        this.evtType = new Type(r);
        this.evtId = r.readInt();
        this.evtValue = new DataList<>(r, new IntElementFactory(), 0x64);
        this.addTime = new DateTime(r);
        this.trigger = new Bool(r);
    }

    public int id() {
        return id;
    }

    public int evtId() {
        return evtId;
    }

    public Int timeSpanSec() {
        return timeSpanSec;
    }

    public Int activeTime() {
        return activeTime;
    }

    public Type evtType() {
        return evtType;
    }

    public DataList<Int> evtValue() {
        return evtValue;
    }

    public DateTime addTime() {
        return addTime;
    }

    public Bool trigger() {
        return trigger;
    }

    public DateTime triggerTime(DateTime lastDateTime) {
        return new DateTime(lastDateTime.value()).add(Calendar.SECOND, timeSpanSec.value());
    }

    public Event timeSpanSec(int timeSpanSec) {
        this.timeSpanSec.value(timeSpanSec);
        return this;
    }

    public Event activeTime(int activeTime) {
        this.activeTime.value(activeTime);
        return this;
    }

    public Event evtType(Type evtType) {
        this.evtType.value(evtType.value());
        return this;
    }

    public Event trigger(boolean trigger) {
        this.trigger.value(trigger);
        return this;
    }

    @Override
    public boolean save() {
        return timeSpanSec.save() && activeTime.save() && evtType.save() && trigger.save();
    }

    @Override
    public boolean write(RandomAccessFile r) {
        try {
            r.writeInt(id);
            if (timeSpanSec.write(r)) {
                if (activeTime.write(r)) {
                    if (evtType.write(r)) {
                        r.writeInt(evtId);
                        if (evtValue.write(r)) {
                            if (addTime.write(r)) {
                                return trigger.write(r);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Event event = (Event) o;
        return id == event.id && evtId == event.evtId &&
                (evtType != null ? evtType.equals(event.evtType) : event.evtType == null);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + evtId;
        result = 31 * result + (evtType != null ? evtType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
                "offset=" + offset +
                ", length=" + length +
                ", id=" + id +
                ", evtId=" + evtId +
                ", timeSpanSec=" + timeSpanSec +
                ", activeTime=" + activeTime +
                ", evtType=" + evtType +
                ", evtValue=" + evtValue +
                ", addTime=" + addTime +
                ", trigger=" + trigger +
                '}';
    }

    public static class EventElementFactory implements ElementFactory<Event> {
        @Override
        public Event create(RandomAccessFile r) throws IOException {
            return new Event(r);
        }
    }
}
