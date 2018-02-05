package com.aa65535.tabikaeruarchivemodifier.model;

import android.support.annotation.Nullable;

import com.aa65535.tabikaeruarchivemodifier.model.DataList.ElementFactory;
import com.aa65535.tabikaeruarchivemodifier.model.GameData.OnLoadedListener;
import com.aa65535.tabikaeruarchivemodifier.utils.Util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

@SuppressWarnings("unused")
public final class GameData extends Data<OnLoadedListener> {
    private int version;
    private int versionStart;
    private int supportID;

    private Int clover;
    private Int ticket;
    private DateTime lastDateTime;
    private DataList<Mail> mailList;
    private DataList<Item> itemList;
    private DataList<Int> bagList;
    private DataList<Int> deskList;
    private DataList<Int> bagListVirtual;
    private DataList<Int> deskListVirtual;
    private DataList<Bool> collectFlags;
    private DataList<Int> collectFailedCnt;
    private DataList<Bool> specialtyFlags;
    private DataList<Event> eventTimerList;
    private DataList<Event> eventActiveList;
    private Int tutorialStep;
    private DataList<Bool> firstFlag;
    private Str frogName;
    private Int frogAchieveId;
    private DataList<Bool> achieveFlags;
    private Int frogMotion;
    private Bool home;
    private Bool drift;
    private Int restTime;
    private Int lastTravelTime;
    private Bool standby;
    private Int standbyWait;
    private Int bgmVolume;
    private Int seVolume;
    private Bool noticeFlag;
    private DataList<Int> gameFlags;
    private Int tmpRaffleResult;

    private GameData(File archive, OnLoadedListener listener) throws IOException {
        super(new RandomAccessFile(archive, "rwd"), listener);
    }

    @Override
    protected void initialize(OnLoadedListener listener) throws IOException {
        r.seek(offset());
        int v = r.readInt();

        if (v < 10400) {
            throw new UnsupportedOperationException("Unsupported Game Archive File");
        }

        version = v;
        supportID = r.readInt();

        v = r.readInt(); // hoten data len
        r.skipBytes(v); // hoten data, skipped

        clover = new Int(r);
        ticket = new Int(r);

        v = r.readInt(); // clover list count
        r.skipBytes(v * 0x39); // clover data, skipped

        lastDateTime = new DateTime(r);

        r.skipBytes(0x04); // next mail id, skipped

        mailList = new DataList<>(r, new ElementFactory<Mail>() {
            @Override
            public Mail create(RandomAccessFile r) throws IOException {
                return new Mail(r);
            }
        });

        itemList = new DataList<>(r, new ElementFactory<Item>() {
            @Override
            public Item create(RandomAccessFile r) throws IOException {
                return new Item(r);
            }
        });

        bagList = new DataList<>(r, new ElementFactory<Int>() {
            @Override
            public Int create(RandomAccessFile r) throws IOException {
                return new Int(r);
            }
        });

        deskList = new DataList<>(r, new ElementFactory<Int>() {
            @Override
            public Int create(RandomAccessFile r) throws IOException {
                return new Int(r);
            }
        });

        bagListVirtual = new DataList<>(r, new ElementFactory<Int>() {
            @Override
            public Int create(RandomAccessFile r) throws IOException {
                return new Int(r);
            }
        });

        deskListVirtual = new DataList<>(r, new ElementFactory<Int>() {
            @Override
            public Int create(RandomAccessFile r) throws IOException {
                return new Int(r);
            }
        });

        collectFlags = new DataList<>(r, new ElementFactory<Bool>() {
            @Override
            public Bool create(RandomAccessFile r) throws IOException {
                return new Bool(r);
            }
        });

        collectFailedCnt = new DataList<>(r, new ElementFactory<Int>() {
            @Override
            public Int create(RandomAccessFile r) throws IOException {
                return new Int(r);
            }
        });

        specialtyFlags = new DataList<>(r, new ElementFactory<Bool>() {
            @Override
            public Bool create(RandomAccessFile r) throws IOException {
                return new Bool(r);
            }
        });

        eventTimerList = new DataList<>(r, new ElementFactory<Event>() {
            @Override
            public Event create(RandomAccessFile r) throws IOException {
                return new Event(r);
            }
        });

        eventActiveList = new DataList<>(r, new ElementFactory<Event>() {
            @Override
            public Event create(RandomAccessFile r) throws IOException {
                return new Event(r);
            }
        });

        tutorialStep = new Int(r);

        firstFlag = new DataList<>(r, new ElementFactory<Bool>() {
            @Override
            public Bool create(RandomAccessFile r) throws IOException {
                return new Bool(r);
            }
        });

        frogName = new Str(r, 0x14);

        frogAchieveId = new Int(r);

        achieveFlags = new DataList<>(r, new ElementFactory<Bool>() {
            @Override
            public Bool create(RandomAccessFile r) throws IOException {
                return new Bool(r);
            }
        });

        frogMotion = new Int(r);
        home = new Bool(r);
        drift = new Bool(r);
        restTime = new Int(r);
        lastTravelTime = new Int(r);
        standby = new Bool(r);
        standbyWait = new Int(r);
        bgmVolume = new Int(r);
        seVolume = new Int(r);
        noticeFlag = new Bool(r);

        gameFlags = new DataList<>(r, new ElementFactory<Int>() {
            @Override
            public Int create(RandomAccessFile r) throws IOException {
                return new Int(r);
            }
        });

        tmpRaffleResult = new Int(r);
        versionStart = r.readInt();
        if (listener != null) {
            listener.onLoaded(this);
        }
    }

    @Nullable
    public static GameData load(File archive, OnLoadedListener listener) {
        try {
            return new GameData(archive, listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void reload(OnLoadedListener listener) {
        try {
            initialize(listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        Util.closeQuietly(r);
    }

    public float version() {
        return version;
    }

    public int supportID() {
        return supportID;
    }

    public Int clover() {
        return clover;
    }

    public Int ticket() {
        return ticket;
    }

    public DateTime lastDateTime() {
        return lastDateTime;
    }

    public List<Mail> mailList() {
        return mailList.data();
    }

    public List<Item> itemList() {
        return itemList.data();
    }

    public List<Int> bagList() {
        return bagList.data();
    }

    public List<Int> deskList() {
        return deskList.data();
    }

    public List<Int> bagListVirtual() {
        return bagListVirtual.data();
    }

    public List<Int> deskListVirtual() {
        return deskListVirtual.data();
    }

    public List<Bool> collectFlags() {
        return collectFlags.data();
    }

    public List<Int> collectFailedCnt() {
        return collectFailedCnt.data();
    }

    public List<Bool> specialtyFlags() {
        return specialtyFlags.data();
    }

    public List<Event> eventTimerList() {
        return eventTimerList.data();
    }

    public List<Event> eventActiveList() {
        return eventActiveList.data();
    }

    public Int tutorialStep() {
        return tutorialStep;
    }

    public List<Bool> firstFlag() {
        return firstFlag.data();
    }

    public Str frogName() {
        return frogName;
    }

    public Int frogAchieveId() {
        return frogAchieveId;
    }

    public List<Bool> achieveFlags() {
        return achieveFlags.data();
    }

    public Int frogMotion() {
        return frogMotion;
    }

    public Bool home() {
        return home;
    }

    public Bool drift() {
        return drift;
    }

    public Int restTime() {
        return restTime;
    }

    public Int lastTravelTime() {
        return lastTravelTime;
    }

    public Bool standby() {
        return standby;
    }

    public Int standbyWait() {
        return standbyWait;
    }

    public Int bgmVolume() {
        return bgmVolume;
    }

    public Int seVolume() {
        return seVolume;
    }

    public Bool noticeFlag() {
        return noticeFlag;
    }

    public List<Int> gameFlags() {
        return gameFlags.data();
    }

    public Int tmpRaffleResult() {
        return tmpRaffleResult;
    }

    public float versionStart() {
        return versionStart;
    }

    public boolean getAllItem(OnLoadedListener listener) {
        int len = 0x21c - itemList.length();
        if (len > 0) {
            try {
                expandData(itemList, len);
                writeItems();
                reload(listener);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            for (Item item : itemList) {
                if (!item.stock(Item.MAX_STOCK).save()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void writeItems() throws IOException {
        int[] items = new int[]{0x000063, 0x000163, 0x000263,
                0x000363, 0x000463, 0x000563, 0x000663, 0x000763, 0x000863, 0x000963, 0x000a63,
                0x000b63, 0x000c63, 0x000d63, 0x000e63, 0x03e863, 0x03e901, 0x03ea63, 0x03eb63,
                0x03ec63, 0x03ed63, 0x03ee63, 0x03ef63, 0x03f063, 0x03f163, 0x03f263, 0x07d001,
                0x07d101, 0x07d201, 0x07d301, 0x07d401, 0x07d501, 0x07d601, 0x07d701, 0x07d801,
                0x07d901, 0x07da01, 0x07db01, 0x0bb963, 0x0bba63, 0x0bbd63, 0x0bbf63, 0x0bc163,
                0x0bc263, 0x0bc363, 0x0bc463, 0x0bc963, 0x0bca63, 0x0bcc63, 0x0fa063, 0x0fa163,
                0x0fa263, 0x0fa363, 0x0fa463, 0x0fa563, 0x0fa663, 0x0fa763, 0x0fa863, 0x0fa963,
                0x0faa63, 0x0fab63, 0x0fac63, 0x0fad63, 0x0fae63, 0x0faf63, 0x0fb063, 0x0fb163};
        r.seek(itemList.offset());
        r.writeInt(items.length);
        for (int i : items) {
            r.writeInt((i >>> 8) & 0xffff);
            r.writeInt(i & 0xff);
        }
    }

    private void expandData(Data data, int len) throws IOException {
        int tail = (int) (data.offset() + data.length());
        byte[] buffer = new byte[(int) (r.length() - tail)];
        r.seek(tail);
        r.readFully(buffer);
        r.setLength(r.length() + len);
        r.seek(tail + len);
        r.write(buffer);
    }

    @Override
    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }

    @Override
    public String toString() {
        return "GameData{" +
                "offset=" + offset +
                ", length=" + length +
                ", version=" + version +
                ", versionStart=" + versionStart +
                ", supportID=" + supportID +
                ", clover=" + clover +
                ", ticket=" + ticket +
                ", lastDateTime=" + lastDateTime +
                ", tutorialStep=" + tutorialStep +
                ", frogName='" + frogName + '\'' +
                ", frogAchieveId=" + frogAchieveId +
                ", frogMotion=" + frogMotion +
                ", home=" + home +
                ", drift=" + drift +
                ", restTime=" + restTime +
                ", lastTravelTime=" + lastTravelTime +
                ", standby=" + standby +
                ", standbyWait=" + standbyWait +
                ", bgmVolume=" + bgmVolume +
                ", seVolume=" + seVolume +
                ", noticeFlag=" + noticeFlag +
                ", tmpRaffleResult=" + tmpRaffleResult +
                '}';
    }

    @Override
    public boolean save() {
        // not need implementation
        throw new UnsupportedOperationException();
    }

    public interface OnLoadedListener {
        void onLoaded(GameData data);
    }
}
