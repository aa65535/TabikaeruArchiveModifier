package com.aa65535.tabikaeruarchivemodifier.model;

import com.aa65535.tabikaeruarchivemodifier.model.DataList.ElementFactory;

import java.io.IOException;
import java.io.RandomAccessFile;

@SuppressWarnings("unused")
public class Clover extends Data<Void> {
    private int x;
    private int y;
    private int element;
    private int spriteNum;
    private int point;
    private DateTime lastHarvest;
    private Int timeSpanSec;
    private Bool newFlag;

    public Clover(RandomAccessFile r) throws IOException {
        super(r, null);
    }

    @Override
    protected void initialize(Void arg) throws IOException {
        this.x = r.readInt();
        this.y = r.readInt();
        this.element = r.readInt();
        this.spriteNum = r.readInt();
        this.point = r.readInt();
        this.lastHarvest = new DateTime(r);
        this.timeSpanSec = new Int(r);
        this.newFlag = new Bool(r);
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int element() {
        return element;
    }

    public int spriteNum() {
        return spriteNum;
    }

    public int point() {
        return point;
    }

    public DateTime lastHarvest() {
        return lastHarvest;
    }

    public Int timeSpanSec() {
        return timeSpanSec;
    }

    public Bool newFlag() {
        return newFlag;
    }

    public Clover timeSpanSec(int timeSpanSec) {
        this.timeSpanSec.value(timeSpanSec);
        return this;
    }

    public Clover newFlag(boolean newFlag) {
        this.newFlag.value(newFlag);
        return this;
    }

    @Override
    public boolean save() {
        return lastHarvest.save() && timeSpanSec.save() && newFlag.save();
    }

    @Override
    public boolean write(RandomAccessFile r) {
        try {
            r.writeInt(x);
            r.writeInt(y);
            r.writeInt(element);
            r.writeInt(spriteNum);
            r.writeInt(point);
            if (lastHarvest.write(r)) {
                if (timeSpanSec.write(r)) {
                    return newFlag.write(r);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String toString() {
        return "Clover{" +
                "offset=" + offset +
                ", length=" + length +
                ", x=" + x +
                ", y=" + y +
                ", element=" + element +
                ", spriteNum=" + spriteNum +
                ", point=" + point +
                ", lastHarvest=" + lastHarvest +
                ", timeSpanSec=" + timeSpanSec +
                ", newFlag=" + newFlag +
                '}';
    }

    public static class CloverElementFactory implements ElementFactory<Clover> {
        @Override
        public Clover create(RandomAccessFile r) throws IOException {
            return new Clover(r);
        }
    }
}
