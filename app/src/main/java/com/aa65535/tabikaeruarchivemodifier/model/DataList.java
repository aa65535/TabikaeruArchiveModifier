package com.aa65535.tabikaeruarchivemodifier.model;

import android.support.annotation.NonNull;

import com.aa65535.tabikaeruarchivemodifier.model.DataList.ElementFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

@SuppressWarnings("unused")
public class DataList<E extends Data> extends Data<ElementFactory<E>> implements Iterable<E> {
    private Int size;
    private List<E> data;

    DataList(RandomAccessFile r, ElementFactory<E> factory) throws IOException {
        super(r, factory);
    }

    DataList(RandomAccessFile r, ElementFactory<E> factory, int fixed) throws IOException {
        super(r, factory);
        int len = fixed - size();
        if (len < 0) {
            throw new IllegalArgumentException("data size > " + fixed);
        }
        for (int i = 0; i < len; i++) {
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

    public E get(int index) {
        return data().get(index);
    }

    public List<E> data() {
        return Collections.unmodifiableList(data.subList(0, size()));
    }

    @Override
    public boolean save() {
        for (E e : this) {
            if (!e.save()) {
                return false;
            }
        }
        return size.save();
    }

    @Override
    public boolean write(RandomAccessFile r) {
        for (E e : data) {
            if (!e.write(r)) {
                return false;
            }
        }
        return size.write(r);
    }

    @Override
    public String toString() {
        return data().toString();
    }

    @NonNull
    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    private class Itr implements Iterator<E> {
        int cursor = 0;

        @Override
        public boolean hasNext() {
            return cursor < size();
        }

        @Override
        public E next() {
            try {
                int i = cursor;
                E next = data.get(i);
                cursor = i + 1;
                return next;
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }
    }

    public interface ElementFactory<T extends Data> {
        T create(RandomAccessFile r) throws IOException;
    }
}
