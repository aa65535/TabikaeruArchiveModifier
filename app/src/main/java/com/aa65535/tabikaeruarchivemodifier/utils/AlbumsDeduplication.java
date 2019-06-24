package com.aa65535.tabikaeruarchivemodifier.utils;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.aa65535.tabikaeruarchivemodifier.model.AlbumIndex;
import com.aa65535.tabikaeruarchivemodifier.model.Str;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class AlbumsDeduplication {
    private File pictureDir;
    private AlbumIndex albumIndex;

    private OnProgressListener progressListener;
    private final Handler mainHandler;

    public AlbumsDeduplication(File pictureDir, OnProgressListener progressListener) {
        this.pictureDir = pictureDir;
        this.progressListener = progressListener;
        try {
            albumIndex = new AlbumIndex(new File(pictureDir, "index.sav"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public AlbumsDeduplication setProgressListener(OnProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    public boolean isEmpty() {
        return albumIndex.getAlbums().isEmpty();
    }

    public List<File> getAlbumFiles() {
        List<File> files = new ArrayList<>(albumIndex.getAlbums().size());
        for (Str str : albumIndex.getAlbums()) {
            files.add(new File(pictureDir, str.value()));
        }
        return files;
    }

    public void deduplication() {
        if (isEmpty()) {
            if (progressListener != null) {
                progressListener.onEmpty();
            }
        } else {
            if (progressListener != null) {
                progressListener.onBefore(albumIndex.getAlbums().size());
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final int count = deduplicationAlbums();
                    if (progressListener != null) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressListener.onAfter(count);
                            }
                        });
                    }
                }
            }).start();
        }
    }

    private int deduplicationAlbums() {
        Set<String> hashSet = new HashSet<>();
        int i = 0, count = 0;
        final int len = albumIndex.getAlbums().size();
        Iterator<Str> it = albumIndex.getAlbums().iterator();
        while (it.hasNext()) {
            final int progress = ++i;
            if (progressListener != null) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressListener.inProgress(len, progress);
                    }
                });
            }
            File album = new File(pictureDir, it.next().value());
            Bitmap bitmap = Util.readAlbumBitmap(album);
            String hash = ImageHash.calcHash(bitmap);
            if (null != hash) {
                if (hashSet.contains(hash)) {
                    album.delete();
                    it.remove();
                    count++;
                } else {
                    hashSet.add(hash);
                }
            }
        }
        if (count > 0) {
            saveAlbumIndex();
        }
        return count;
    }

    private void saveAlbumIndex() {
        try {
            File tempFile = new File(pictureDir, "index.sav.temp");
            RandomAccessFile r = new RandomAccessFile(tempFile, "rwd");
            albumIndex.write(r);
            File indexFile = new File(pictureDir, "index.sav");
            File backFile = new File(pictureDir, "index.sav.back");
            if (backFile.exists()) {
                backFile.delete();
            }
            indexFile.renameTo(backFile);
            tempFile.renameTo(indexFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public interface OnProgressListener {
        void onBefore(int count);

        void inProgress(int count, int progress);

        void onAfter(int count);

        void onEmpty();
    }
}
