package com.aa65535.tabikaeruarchivemodifier.utils;

import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class AlbumsExporter {
    private File outputDir;

    private List<File> albums;
    private ProgressListener progressListener;
    private final Handler mainHandler;

    public AlbumsExporter(File pictureDir, File outputDir) {
        this.outputDir = outputDir;
        if (!this.outputDir.exists()) {
            this.outputDir.mkdirs();
        }
        this.albums = getAlbums(pictureDir);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public AlbumsExporter setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    public void export() {
        if (progressListener != null) {
            progressListener.onBefore(albums.size());
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                exportAlbums();
                if (progressListener != null) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressListener.onAfter(outputDir.getAbsolutePath());
                        }
                    });
                }
            }
        }).start();
    }

    private void exportAlbums() {
        int i = 0;
        final int len = albums.size();
        Iterator<File> it = albums.iterator();
        while (it.hasNext()) {
            File album = it.next();
            final File out = new File(outputDir, album.getName().replace(".sav", ".png"));
            final int progress = i++;
            if (progressListener != null) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressListener.inProgress(out.getName(), len, progress);
                    }
                });
            }
            byte[] bytes = fileToByteArray(album);
            if (bytes.length > 0) {
                try {
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.length)
                            .compress(CompressFormat.PNG, 100, new FileOutputStream(out));
                    it.remove();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<File> getAlbums(File pictureDir) {
        File[] outFiles = outputDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("album_") && name.endsWith(".png");
            }
        });
        final List<String> fileNames = new ArrayList<>(outFiles.length);
        for (File file : outFiles) {
            fileNames.add(file.getName().replace(".png", ".sav"));
        }
        return new ArrayList<>(Arrays.asList(pictureDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("album_") && name.endsWith(".sav") && !fileNames.contains(name);
            }
        })));
    }

    private static byte[] fileToByteArray(File file) {
        FileChannel fc = null;
        try {
            fc = new RandomAccessFile(file, "r").getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) fc.size() - 4);
            fc.read(byteBuffer, 4);
            return byteBuffer.array();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Util.closeQuietly(fc);
        }
        return new byte[0];
    }

    public interface ProgressListener {
        void onBefore(int count);

        void inProgress(String filename, int count, int progress);

        void onAfter(String path);
    }
}
