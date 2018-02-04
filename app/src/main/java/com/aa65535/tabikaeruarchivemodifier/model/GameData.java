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
    private float version;
    private float versionStart;
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

        version = v / 10000f;
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
        versionStart = r.readFloat() / 10000f;
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
    public boolean write() {
        // not need implementation
        throw new UnsupportedOperationException();
    }

    public interface OnLoadedListener {
        void onLoaded(GameData data);
    }
}
