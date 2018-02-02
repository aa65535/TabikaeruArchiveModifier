package com.aa65535.tabikaeruarchivemodifier.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AlbumsExporter {
    private File pictureDir;
    private File outputDir;
    private Set<File> albums;

    private ProgressListener progressListener;
    private final Handler mainHandler;

    public AlbumsExporter(File pictureDir, File outputDir) {
        this.pictureDir = pictureDir;
        this.outputDir = outputDir;
        if (!this.outputDir.exists()) {
            this.outputDir.mkdirs();
        }
        this.albums = new HashSet<>(getAlbumFileList());
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public AlbumsExporter setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    public void refresh() {
        albums.addAll(getAlbumFileList());
    }

    public void export() {
        if (albums.isEmpty()) {
            if (progressListener != null) {
                progressListener.isEmpty();
            }
        } else {
            if (progressListener != null) {
                progressListener.onBefore(albums.size());
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final int count = exportAlbums();
                    if (progressListener != null) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressListener.onAfter(outputDir.getAbsolutePath(), count);
                            }
                        });
                    }
                }
            }).start();
        }
    }

    private int exportAlbums() {
        int i = 0, count = 0;
        final int len = albums.size();
        Iterator<File> it = albums.iterator();
        while (it.hasNext()) {
            File album = it.next();
            final File out = new File(outputDir, album.getName().replace(".sav", ".png"));
            final int progress = ++i;
            if (progressListener != null) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressListener.inProgress(out.getName(), len, progress);
                    }
                });
            }
            byte[] bytes = readAlbumFile(album);
            if (bytes != null) {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(out);
                    fos.write(bytes);
                    fos.flush();
                    it.remove();
                    count++;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Util.closeQuietly(fos);
                }
            }
        }
        return count;
    }

    @NonNull
    private List<File> getAlbumFileList() {
        return Arrays.asList(pictureDir.listFiles(new MyFilenameFilter(outputDir)));
    }

    @Nullable
    private static byte[] readAlbumFile(File album) {
        RandomAccessFile r = null;
        try {
            r = new RandomAccessFile(album, "r");
            int len = r.readInt();
            byte[] bytes = new byte[len];
            r.readFully(bytes);
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Util.closeQuietly(r);
        }
        return null;
    }

    private static class MyFilenameFilter implements FilenameFilter {
        private Set<String> outFileNames;

        public MyFilenameFilter(File outputDir) {
            String[] names = outputDir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".png") && name.startsWith("album_");
                }
            });
            outFileNames = new HashSet<>(names.length);
            for (String name : names) {
                outFileNames.add(name.replace(".png", ".sav"));
            }
        }

        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".sav") && name.startsWith("album_") && !outFileNames.contains(name);
        }
    }

    public interface ProgressListener {
        void onBefore(int count);

        void inProgress(String filename, int count, int progress);

        void onAfter(String path, int count);

        void isEmpty();
    }
}
