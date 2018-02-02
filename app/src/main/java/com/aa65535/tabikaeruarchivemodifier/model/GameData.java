package com.aa65535.tabikaeruarchivemodifier.model;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.aa65535.tabikaeruarchivemodifier.R;
import com.aa65535.tabikaeruarchivemodifier.utils.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public final class GameData {
    private final RandomAccessFile r;
    private final SparseArray<String> nameArray;

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

    private GameData(Context context, File archive) throws FileNotFoundException {
        r = new RandomAccessFile(archive, "rwd");
        Resources resources = context.getResources();
        int[] item_ids = resources.getIntArray(R.array.item_ids);
        String[] item_names = resources.getStringArray(R.array.item_names);
        nameArray = new SparseArray<>(item_ids.length);
        for (int i = 0; i < item_ids.length; i++) {
            nameArray.put(item_ids[i], item_names[i]);
        }
    }

    @Nullable
    public static GameData load(Context context, File archive) {
        try {
            return new GameData(context, archive).load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private GameData load() throws IOException {
        r.seek(0);
        version = r.readInt() / 10000f;
        supportID = r.readInt();

        int c = r.readInt(); // hoten data len
        r.skipBytes(c); // hoten data, skipped

        clover = new Int(r);
        ticket = new Int(r);

        c = r.readInt(); // clover list count
        for (int i = 0; i < c; i++) {
            r.skipBytes(0x39); // clover data, skipped
        }

        lastDateTime = new DateTime(r);

        r.skipBytes(0x04); // next mail id, skipped

        c = r.readInt(); // mail entry count
        mailList = new ArrayList<>(c);
        for (int i = 0; i < c; i++) {
            mailList.add(new Mail(r)); // read mail
        }

        c = r.readInt(); // item entry count
        itemList = new ArrayList<>(c);
        for (int i = 0; i < c; i++) {
            itemList.add(new Item(r).setName(nameArray)); // read item
        }

        c = r.readInt(); // bag item entry count
        bagList = new ArrayList<>(c);
        for (int i = 0; i < c; i++) {
            bagList.add(new Int(r));
        }

        c = r.readInt(); // desk item entry count
        deskList = new ArrayList<>(c);
        for (int i = 0; i < c; i++) {
            deskList.add(new Int(r));
        }

        c = r.readInt(); // bag virtual item entry count
        bagListVirtual = new ArrayList<>(c);
        for (int i = 0; i < c; i++) {
            bagListVirtual.add(new Int(r));
        }

        c = r.readInt(); // desk virtual item entry count
        deskListVirtual = new ArrayList<>(c);
        for (int i = 0; i < c; i++) {
            deskListVirtual.add(new Int(r));
        }

        c = r.readInt(); // collect flags count
        collectFlags = new ArrayList<>(c);
        for (int i = 0; i < c; i++) {
            collectFlags.add(new Bool(r));
        }

        c = r.readInt(); // collect Failed count
        collectFailedCnt = new ArrayList<>(c);
        for (int i = 0; i < c; i++) {
            collectFailedCnt.add(new Int(r));
        }

        c = r.readInt(); // specialty flags count
        specialtyFlags = new ArrayList<>(c);
        for (int i = 0; i < c; i++) {
            specialtyFlags.add(new Bool(r));
        }

        c = r.readInt(); // event timer count
        for (int i = 0; i < c; i++) {
            r.skipBytes(0x01c9); // event timer data, skipped
        }

        c = r.readInt(); // event active count
        for (int i = 0; i < c; i++) {
            r.skipBytes(0x01c9); // event active data, skipped
        }

        tutorialStep = new Int(r);

        c = r.readInt(); // first flags count
        firstFlag = new ArrayList<>(c);
        for (int i = 0; i < c; i++) {
            firstFlag.add(new Bool(r));
        }

        frogName = new Str(r, 0x14);

        frogAchieveId = new Int(r);

        c = r.readInt(); // achieve flags count
        achieveFlags = new ArrayList<>(c);
        for (int i = 0; i < c; i++) {
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

        c = r.readInt(); // game flags count
        gameFlags = new ArrayList<>(c);
        for (int i = 0; i < c; i++) {
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
}
