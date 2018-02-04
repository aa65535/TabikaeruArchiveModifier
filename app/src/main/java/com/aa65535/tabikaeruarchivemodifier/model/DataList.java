package com.aa65535.tabikaeruarchivemodifier.model;

import com.aa65535.tabikaeruarchivemodifier.model.DataList.ElementFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataList<E extends Data> extends Data<ElementFactory<E>> {
    private Int size;
    private List<E> data;

    DataList(RandomAccessFile r, ElementFactory<E> factory) throws IOException {
        super(r, factory);
    }

    DataList(RandomAccessFile r, ElementFactory<E> factory, int fixed) throws IOException {
        super(r, factory);
        for (int i = 0, len = fixed - size(); i < len; i++) {
            data.add(factory.create(r));
        }
    }

    @Override
    protected void initialize(ElementFactory<E> factory) throws IOException {
        this.size = new Int(r);
        this.data = new ArrayList<>(size());
        for (int i = 0; i < size(); i++) {
            data.add(factory.create(r));
        }
    }

    public int size() {
        return size.value();
    }

    public List<E> data() {
        return Collections.unmodifiableList(data.subList(0, size()));
    }

    public E nextElement() {
        try {
            E e = data.get(size());
            size.value(size() + 1);
            return e;
        } catch (IndexOutOfBoundsException ignored) {
            throw new UnsupportedOperationException();
        }
    }

    public E pop() {
        E e = data.remove(size() - 1);
        size.value(size() - 1);
        return e;
    }

    @Override
    public boolean write() {
        for (E e : data) {
            if (!e.write()) {
                return false;
            }
        }
        return size.write();
    }

    @Override
    public String toString() {
        return data().toString();
    }

    public interface ElementFactory<T extends Data> {
        T create(RandomAccessFile r) throws IOException;
    }
}
