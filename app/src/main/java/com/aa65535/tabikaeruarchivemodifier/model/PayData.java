package com.aa65535.tabikaeruarchivemodifier.model;

import com.aa65535.tabikaeruarchivemodifier.model.DataList.ElementFactory;

import java.io.IOException;
import java.io.RandomAccessFile;

@SuppressWarnings("unused")
public class PayData extends Data<Void> {
    private int uid = -1;
    private Str recceptId;
    private Int itemId;

    public PayData(RandomAccessFile r) throws IOException {
        super(r, null);
    }

    @Override
    protected void initialize(Void arg) throws IOException {
        this.uid = r.readInt();
        this.recceptId = new Str(r, 40);
        this.itemId = new Int(r);
    }

    public int uid() {
        return uid;
    }

    public Str recceptId() {
        return recceptId;
    }

    public Int itemId() {
        return itemId;
    }

    @Override
    public boolean save() {
        return recceptId.save() && itemId.save();
    }

    @Override
    public boolean write(RandomAccessFile r) {
        try {
            r.writeInt(uid);
            if (recceptId.write(r)) {
                return itemId.write(r);
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

        PayData payData = (PayData) o;

        return uid == payData.uid &&
                (recceptId != null ? recceptId.equals(payData.recceptId) : payData.recceptId == null) &&
                (itemId != null ? itemId.equals(payData.itemId) : payData.itemId == null);
    }

    @Override
    public int hashCode() {
        int result = uid;
        result = 31 * result + (recceptId != null ? recceptId.hashCode() : 0);
        result = 31 * result + (itemId != null ? itemId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PayData{" +
                "offset=" + offset +
                ", length=" + length +
                ", uid=" + uid +
                ", recceptId=" + recceptId +
                ", itemId=" + itemId +
                '}';
    }

    public static class PayDataElementFactory implements ElementFactory<PayData> {
        @Override
        public PayData create(RandomAccessFile r) throws IOException {
            return new PayData(r);
        }
    }
}
