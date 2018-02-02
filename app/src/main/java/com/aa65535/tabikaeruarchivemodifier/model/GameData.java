package com.aa65535.tabikaeruarchivemodifier.model;

import android.support.annotation.Nullable;

import com.aa65535.tabikaeruarchivemodifier.utils.Util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public final class GameData extends Data {
    private float version;
    private float versionStart;
    private int supportID;

    private Int clover;
    private Int ticket;
    private DateTime lastDateTime;
    private List<Mail> mailList;
    private List<Item> itemList;
    private List<Int> bagList;
    private List<Int> deskList;
    private List<Int> bagListVirtual;
    private List<Int> deskListVirtual;
    private List<Bool> collectFlags;
    private List<Int> collectFailedCnt;
    private List<Bool> specialtyFlags;
    private List<Event> eventTimerList;
    private List<Event> eventActiveList;
    private Int tutorialStep;
    private List<Bool> firstFlag;
    private Str frogName;
    private Int frogAchieveId;
    private List<Bool> achieveFlags;
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
    private List<Int> gameFlags;
    private Int tmpRaffleResult;

    private boolean loaded;

    private GameData(File archive) throws IOException {
        super(new RandomAccessFile(archive, "rwd"), -1);
    }

    @Override
    protected void initialize(int size) throws IOException {
        loaded = false;
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
        for (int i = 0; i < v; i++) {
            r.skipBytes(0x39); // clover data, skipped
        }

        lastDateTime = new DateTime(r);

        r.skipBytes(0x04); // next mail id, skipped

        v = r.readInt(); // mail entry count
        mailList = new ArrayList<>(v);
        for (int i = 0; i < v; i++) {
            mailList.add(new Mail(r)); // read mail
        }

        v = r.readInt(); // item entry count
        itemList = new ArrayList<>(v);
        for (int i = 0; i < v; i++) {
            itemList.add(new Item(r)); // read item
        }

        v = r.readInt(); // bag item entry count
        bagList = new ArrayList<>(v);
        for (int i = 0; i < v; i++) {
            bagList.add(new Int(r));
        }

        v = r.readInt(); // desk item entry count
        deskList = new ArrayList<>(v);
        for (int i = 0; i < v; i++) {
            deskList.add(new Int(r));
        }

        v = r.readInt(); // bag virtual item entry count
        bagListVirtual = new ArrayList<>(v);
        for (int i = 0; i < v; i++) {
            bagListVirtual.add(new Int(r));
        }

        v = r.readInt(); // desk virtual item entry count
        deskListVirtual = new ArrayList<>(v);
        for (int i = 0; i < v; i++) {
            deskListVirtual.add(new Int(r));
        }

        v = r.readInt(); // collect flags count
        collectFlags = new ArrayList<>(v);
        for (int i = 0; i < v; i++) {
            collectFlags.add(new Bool(r));
        }

        v = r.readInt(); // collect Failed count
        collectFailedCnt = new ArrayList<>(v);
        for (int i = 0; i < v; i++) {
            collectFailedCnt.add(new Int(r));
        }

        v = r.readInt(); // specialty flags count
        specialtyFlags = new ArrayList<>(v);
        for (int i = 0; i < v; i++) {
            specialtyFlags.add(new Bool(r));
        }

        v = r.readInt(); // event timer count
        eventTimerList = new ArrayList<>(v);
        for (int i = 0; i < v; i++) {
            eventTimerList.add(new Event(r));
        }

        v = r.readInt(); // event active count
        eventActiveList = new ArrayList<>(v);
        for (int i = 0; i < v; i++) {
            eventActiveList.add(new Event(r));
        }

        tutorialStep = new Int(r);

        v = r.readInt(); // first flags count
        firstFlag = new ArrayList<>(v);
        for (int i = 0; i < v; i++) {
            firstFlag.add(new Bool(r));
        }

        frogName = new Str(r, 0x14);

        frogAchieveId = new Int(r);

        v = r.readInt(); // achieve flags count
        achieveFlags = new ArrayList<>(v);
        for (int i = 0; i < v; i++) {
            achieveFlags.add(new Bool(r));
        }

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

        v = r.readInt(); // game flags count
        gameFlags = new ArrayList<>(v);
        for (int i = 0; i < v; i++) {
            gameFlags.add(new Int(r));
        }

        tmpRaffleResult = new Int(r);
        versionStart = r.readFloat() / 10000f;
        loaded = true;
    }

    @Nullable
    public static GameData load(File archive) {
        try {
            return new GameData(archive);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void reload() {
        try {
            initialize(-1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean loaded() {
        return loaded;
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
        return mailList;
    }

    public List<Item> itemList() {
        return itemList;
    }

    public List<Int> bagList() {
        return bagList;
    }

    public List<Int> deskList() {
        return deskList;
    }

    public List<Int> bagListVirtual() {
        return bagListVirtual;
    }

    public List<Int> deskListVirtual() {
        return deskListVirtual;
    }

    public List<Bool> collectFlags() {
        return collectFlags;
    }

    public List<Int> collectFailedCnt() {
        return collectFailedCnt;
    }

    public List<Bool> specialtyFlags() {
        return specialtyFlags;
    }

    public List<Event> eventTimerList() {
        return eventTimerList;
    }

    public List<Event> eventActiveList() {
        return eventActiveList;
    }

    public Int tutorialStep() {
        return tutorialStep;
    }

    public List<Bool> firstFlag() {
        return firstFlag;
    }

    public Str frogName() {
        return frogName;
    }

    public Int frogAchieveId() {
        return frogAchieveId;
    }

    public List<Bool> achieveFlags() {
        return achieveFlags;
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
        return gameFlags;
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
}
