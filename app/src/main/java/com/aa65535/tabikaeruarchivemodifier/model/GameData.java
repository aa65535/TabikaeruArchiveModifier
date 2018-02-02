package com.aa65535.tabikaeruarchivemodifier.model;

import android.support.annotation.Nullable;

import com.aa65535.tabikaeruarchivemodifier.utils.Util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public final class GameData extends Data {
    public float version;
    public float versionStart;
    public int supportID;

    public Int clover;
    public Int ticket;
    public DateTime lastDateTime;
    public List<Mail> mailList;
    public List<Item> itemList;
    public List<Int> bagList;
    public List<Int> deskList;
    public List<Int> bagListVirtual;
    public List<Int> deskListVirtual;
    public List<Bool> collectFlags;
    public List<Int> collectFailedCnt;
    public List<Bool> specialtyFlags;
    public Int tutorialStep;
    public List<Bool> firstFlag;
    public Str frogName;
    public Int frogAchieveId;
    public List<Bool> achieveFlags;
    public Int frogMotion;
    public Bool home;
    public Bool drift;
    public Int restTime;
    public Int lastTravelTime;
    public Bool standby;
    public Int standbyWait;
    public Int bgmVolume;
    public Int seVolume;
    public Bool noticeFlag;
    public List<Int> gameFlags;
    public Int tmpRaffleResult;

    private GameData(File archive) throws IOException {
        super(new RandomAccessFile(archive, "rwd"));
    }

    @Nullable
    public static GameData load(File archive) {
        try {
            return new GameData(archive).load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private GameData load() throws IOException {
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
        for (int i = 0; i < v; i++) {
            r.skipBytes(0x01c9); // event timer data, skipped
        }

        v = r.readInt(); // event active count
        for (int i = 0; i < v; i++) {
            r.skipBytes(0x01c9); // event active data, skipped
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
        return this;
    }

    public void reload() {
        try {
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        Util.closeQuietly(r);
    }

    public float getVersion() {
        return version;
    }

    public int getSupportID() {
        return supportID;
    }

    public Int getClover() {
        return clover;
    }

    public Int getTicket() {
        return ticket;
    }

    public DateTime getLastDateTime() {
        return lastDateTime;
    }

    public List<Mail> getMailList() {
        return mailList;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public List<Int> getBagList() {
        return bagList;
    }

    public List<Int> getDeskList() {
        return deskList;
    }

    public List<Int> getBagListVirtual() {
        return bagListVirtual;
    }

    public List<Int> getDeskListVirtual() {
        return deskListVirtual;
    }

    public List<Bool> getCollectFlags() {
        return collectFlags;
    }

    public List<Int> getCollectFailedCnt() {
        return collectFailedCnt;
    }

    public List<Bool> getSpecialtyFlags() {
        return specialtyFlags;
    }

    public Int getTutorialStep() {
        return tutorialStep;
    }

    public List<Bool> getFirstFlag() {
        return firstFlag;
    }

    public Str getFrogName() {
        return frogName;
    }

    public Int getFrogAchieveId() {
        return frogAchieveId;
    }

    public List<Bool> getAchieveFlags() {
        return achieveFlags;
    }

    public Int getFrogMotion() {
        return frogMotion;
    }

    public Bool getHome() {
        return home;
    }

    public Bool getDrift() {
        return drift;
    }

    public Int getRestTime() {
        return restTime;
    }

    public Int getLastTravelTime() {
        return lastTravelTime;
    }

    public Bool getStandby() {
        return standby;
    }

    public Int getStandbyWait() {
        return standbyWait;
    }

    public Int getBgmVolume() {
        return bgmVolume;
    }

    public Int getSeVolume() {
        return seVolume;
    }

    public Bool getNoticeFlag() {
        return noticeFlag;
    }

    public List<Int> getGameFlags() {
        return gameFlags;
    }

    public Int getTmpRaffleResult() {
        return tmpRaffleResult;
    }

    public float getVersionStart() {
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
                "version=" + version +
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
        // TODO: 2018-02-02 need implementation
        return false;
    }

    @Override
    public int length() {
        // TODO: 2018-02-02 need implementation
        return 0;
    }
}
