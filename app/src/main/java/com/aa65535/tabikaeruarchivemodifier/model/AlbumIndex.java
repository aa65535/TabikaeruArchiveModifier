package com.aa65535.tabikaeruarchivemodifier.model;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public final class AlbumIndex extends Data<Void> {
    private List<Str> albums;

    public AlbumIndex(File indexFile) throws IOException {
        super(new RandomAccessFile(indexFile, "rwd"), null);
    }

    public List<Str> getAlbums() {
        return albums;
    }

    @Override
    protected void initialize(Void arg) throws IOException {
        r.seek(4);
        int size = r.readInt();
        albums = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            albums.add(new Str(r, r.readInt()));
        }
    }

    @Override
    public boolean save() {
        // not need implement
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean write(RandomAccessFile r) {
        try {
            r.writeInt(albums.size());
            r.writeInt(albums.size());
            for (Str album : albums) {
                r.writeInt(album.length);
                album.write(r);
            }
            r.writeInt(0);
            r.writeInt(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int sizeof() {
        int size = 4 * 4;
        for (Str album : albums) {
            size += 4 + album.length;
        }
        return size;
    }

    public ByteBuffer toByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(sizeof());
        buffer.putInt(albums.size()).putInt(albums.size());
        for (Str album : albums) {
            buffer.putInt(album.length);
            album.write(buffer);
        }
        buffer.putInt(0).putInt(0);
        return buffer;
    }
}
