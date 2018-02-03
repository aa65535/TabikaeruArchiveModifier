package com.aa65535.tabikaeruarchivemodifier.model;

import com.aa65535.tabikaeruarchivemodifier.model.DataList.ElementFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;

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
        public static final int NONE = -1;
        public static final int GO_TRAVEL = 0;
        public static final int BACK_HOME = 1;
        public static final int PICTURE = 2;
        public static final int DRIFT = 3;
        public static final int RETURN = 4;
        public static final int FRIEND = 5;
        public static final int GIFT = 6;

        Type(RandomAccessFile r) throws IOException {
            super(r);
        }

        @Override
        public String toString() {
            switch (value()) {
                case GO_TRAVEL:
                    return "GO_TRAVEL";
                case BACK_HOME:
                    return "BACK_HOME";
                case PICTURE:
                    return "PICTURE";
                case DRIFT:
                    return "DRIFT";
                case RETURN:
                    return "RETURN";
                case FRIEND:
                    return "FRIEND";
                case GIFT:
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
        this.evtValue = new DataList<>(r, new ElementFactory<Int>() {
            @Override
            public Int create(RandomAccessFile r) throws IOException {
                return new Int(r);
            }
        }, 0x64);
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

    public DateTime triggerTime(DateTime lastTime) {
        return new DateTime().value(lastTime.value()).add(Calendar.SECOND, timeSpanSec.value());
    }

    public Event timeSpanSec(int timeSpanSec) {
        this.timeSpanSec.value(timeSpanSec);
        return this;
    }

    public Event activeTime(int activeTime) {
        this.activeTime.value(activeTime);
        return this;
    }

    public Event evtType(int evtType) {
        this.evtType.value(evtType);
        return this;
    }

    public Event trigger(boolean trigger) {
        this.trigger.value(trigger);
        return this;
    }

    @Override
    public boolean write() {
        return timeSpanSec.write() && activeTime.write() && evtType.write() && trigger.write();
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
}
