package com.aa65535.tabikaeruarchivemodifier.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class AlbumsExporter {
    private File pictureDir;
    private File outputDir;
    private Set<File> albums;

    private OnProgressListener progressListener;
    private final Handler mainHandler;

    public AlbumsExporter(File pictureDir, File outputDir, OnProgressListener progressListener) {
        this.pictureDir = pictureDir;
        this.outputDir = outputDir;
        if (!this.outputDir.exists()) {
            this.outputDir.mkdirs();
        }
        this.albums = new HashSet<>(getAlbumFileList());
        this.progressListener = progressListener;
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public AlbumsExporter setOnProgressListener(OnProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    public void refresh() {
        albums.addAll(getAlbumFileList());
    }

    public boolean isEmpty() {
        return albums.isEmpty();
    }

    public void export() {
        if (isEmpty()) {
            if (progressListener != null) {
                progressListener.onEmpty();
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
            byte[] bytes = Util.readAlbumFile(album);
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

    public interface OnProgressListener {
        void onBefore(int count);

        void inProgress(String filename, int count, int progress);

        void onAfter(String path, int count);

        void onEmpty();
    }
}
