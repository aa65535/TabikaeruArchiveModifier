package com.aa65535.tabikaeruarchivemodifier.model;

import com.aa65535.tabikaeruarchivemodifier.utils.Util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
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
        r.seek(0);
        int size = r.readInt();
        r.readInt();
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

    private int sizeof() {
        if (albums.size() > 0) {
            return albums.get(0).length;
        }
        return 0;
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
            Util.closeQuietly(r);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
