package com.aa65535.tabikaeruarchivemodifier.model;

import com.aa65535.tabikaeruarchivemodifier.model.DataList.ElementFactory;

import java.io.IOException;
import java.io.RandomAccessFile;

@SuppressWarnings("unused")
public class Goal extends Data<Void> {
    private int id;
    private Int cnt;

    public Goal(RandomAccessFile r) throws IOException {
        super(r, null);
    }

    @Override
    protected void initialize(Void arg) throws IOException {
        this.id = r.readInt();
        this.cnt = new Int(r);
    }

    public int id() {
        return id;
    }

    public Int cnt() {
        return cnt;
    }

    @Override
    public boolean save() {
        return cnt.save();
    }

    @Override
    public boolean write(RandomAccessFile r) {
        try {
            r.writeInt(id);
            return cnt.write(r);
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

        Goal goal = (Goal) o;

        if (id != goal.id)
            return false;
        return cnt != null ? cnt.equals(goal.cnt) : goal.cnt == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (cnt != null ? cnt.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Goal{" +
                "id=" + id +
                ", cnt=" + cnt +
                ", offset=" + offset +
                ", length=" + length +
                '}';
    }

    public static class GoalElementFactory implements ElementFactory<Goal> {
        @Override
        public Goal create(RandomAccessFile r) throws IOException {
            return new Goal(r);
        }
    }
}
