package com.aa65535.tabikaeruarchivemodifier.model;

import android.support.annotation.NonNull;

import com.aa65535.tabikaeruarchivemodifier.model.Bool.BoolElementFactory;
import com.aa65535.tabikaeruarchivemodifier.model.Clover.CloverElementFactory;
import com.aa65535.tabikaeruarchivemodifier.model.Event.EventElementFactory;
import com.aa65535.tabikaeruarchivemodifier.model.GameData.OnLoadedListener;
import com.aa65535.tabikaeruarchivemodifier.model.Goal.GoalElementFactory;
import com.aa65535.tabikaeruarchivemodifier.model.Int.IntElementFactory;
import com.aa65535.tabikaeruarchivemodifier.model.Item.ItemElementFactory;
import com.aa65535.tabikaeruarchivemodifier.model.Mail.MailElementFactory;
import com.aa65535.tabikaeruarchivemodifier.model.Pay.PayElementFactory;
import com.aa65535.tabikaeruarchivemodifier.utils.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

@SuppressWarnings("unused")
public final class GameData extends Data<OnLoadedListener> {
    private int version;
    private int versionStart;
    private int supportID;
    private byte[] unknownData;

    private DataList<Bool> hoten;
    private Int clover;
    private Int ticket;
    private DataList<Clover> cloverList;
    private DateTime lastDateTime;
    private Int mailListNextId;
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
    // VersionAdded: 1.05
    private Int iapCallBackCnt;
    // VersionAdded: 1.06
    private Pay applicationData;
    // VersionAdded: 1.06
    private DataList<Int> applicationItemId;
    // VersionAdded: 1.06
    private DataList<Pay> payData;
    // VersionAdded: 1.20
    private Int coupon;
    // VersionAdded: 1.20
    private DataList<Int> lastActPromo;
    // VersionAdded: 1.20
    private DataList<Int> pkgList;
    // VersionAdded: 1.20
    private DataList<Int> pkgCollectFlagsId;
    // VersionAdded: 1.20
    private DataList<Int> pkgSpecialtyFlagsId;
    // VersionAdded: 1.20
    private Int requestCount;
    // VersionAdded: 1.20
    private Int requestId;
    // VersionAdded: 1.20
    private Int requestTimer;
    // VersionAdded: 1.20
    private Bool requestAutoPlay;
    // VersionAdded: 1.21
    private Int addAlbumPage;
    // VersionAdded: 1.40
    private DataList<Int> pkgAchieveFlagsId;
    // VersionAdded: 1.40
    private Int langId;
    // VersionAdded: 1.40
    private DataList<Goal> goalList;
    // VersionAdded: 1.40
    private DataList<Goal> pkgGoalList;
    // VersionAdded: 1.50
    private DateTime lastServerDateTime;

    private GameData(File archive, OnLoadedListener listener) throws IOException {
        super(new RandomAccessFile(archive, "rwd"), listener);
    }

    @Override
    protected void initialize(OnLoadedListener listener) throws IOException {
        r.seek(offset());
        version = r.readInt();

        if (version < VERSION_MIN) {
            throw new UnsupportedOperationException("Unsupported Game Archive File");
        }

        supportID = r.readInt();
        hoten = new DataList<>(r, new BoolElementFactory());
        clover = new Int(r);
        ticket = new Int(r);
        cloverList = new DataList<>(r, new CloverElementFactory());
        lastDateTime = new DateTime(r);
        mailListNextId = new Int(r);
        mailList = new DataList<>(r, new MailElementFactory());
        itemList = new DataList<>(r, new ItemElementFactory());
        bagList = new DataList<>(r, new IntElementFactory());
        deskList = new DataList<>(r, new IntElementFactory());
        bagListVirtual = new DataList<>(r, new IntElementFactory());
        deskListVirtual = new DataList<>(r, new IntElementFactory());
        collectFlags = new DataList<>(r, new BoolElementFactory());
        collectFailedCnt = new DataList<>(r, new IntElementFactory());
        specialtyFlags = new DataList<>(r, new BoolElementFactory());
        eventTimerList = new DataList<>(r, new EventElementFactory());
        eventActiveList = new DataList<>(r, new EventElementFactory());
        tutorialStep = new Int(r);
        firstFlag = new DataList<>(r, new BoolElementFactory());
        frogName = new Str(r, SHORT_STR_LEN);
        frogAchieveId = new Int(r);
        achieveFlags = new DataList<>(r, new BoolElementFactory());
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
        gameFlags = new DataList<>(r, new IntElementFactory());
        tmpRaffleResult = new Int(r);
        versionStart = r.readInt();

        if (version >= VERSION_105) {
            iapCallBackCnt = new Int(r);
        }

        if (version >= VERSION_106) {
            applicationData = new Pay(r);
            applicationItemId = new DataList<>(r, new IntElementFactory());
            payData = new DataList<>(r, new PayElementFactory());
        }

        if (version >= VERSION_120) {
            coupon = new Int(r);
            lastActPromo = new DataList<>(r, new IntElementFactory());
            pkgList = new DataList<>(r, new IntElementFactory());
            pkgCollectFlagsId = new DataList<>(r, new IntElementFactory());
            pkgSpecialtyFlagsId = new DataList<>(r, new IntElementFactory());
            requestCount = new Int(r);
            requestId = new Int(r);
            requestTimer = new Int(r);
            requestAutoPlay = new Bool(r);
        }

        if (version >= VERSION_121) {
            addAlbumPage = new Int(r);
        }

        if (version >= VERSION_140) {
            pkgAchieveFlagsId = new DataList<>(r, new IntElementFactory());
            langId = new Int(r);
            goalList = new DataList<>(r, new GoalElementFactory());
            pkgGoalList = new DataList<>(r, new GoalElementFactory());
        }

        if (version >= VERSION_150) {
            lastServerDateTime = new DateTime(r);
        }

        if (r.getFilePointer() < r.length()) {
            int length = (int) (r.length() - r.getFilePointer());
            unknownData = new byte[length];
            r.read(unknownData);
        }

        if (listener != null) {
            listener.onLoaded(this);
        }
    }

    @NonNull
    public static GameData load(File archive, OnLoadedListener listener) {
        try {
            return new GameData(archive, listener);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reload(OnLoadedListener listener) {
        try {
            initialize(listener);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void destroy() {
        Util.closeQuietly(r);
    }

    // getter start
    public int version() {
        return version;
    }

    public int versionStart() {
        return versionStart;
    }

    public int supportID() {
        return supportID;
    }

    public DataList<Bool> hoten() {
        return hoten;
    }

    public Int clover() {
        return clover;
    }

    public Int ticket() {
        return ticket;
    }

    public DataList<Clover> cloverList() {
        return cloverList;
    }

    public DateTime lastDateTime() {
        return lastDateTime;
    }

    public Int mailListNextId() {
        return mailListNextId;
    }

    public DataList<Mail> mailList() {
        return mailList;
    }

    public DataList<Item> itemList() {
        return itemList;
    }

    public DataList<Int> bagList() {
        return bagList;
    }

    public DataList<Int> deskList() {
        return deskList;
    }

    public DataList<Int> bagListVirtual() {
        return bagListVirtual;
    }

    public DataList<Int> deskListVirtual() {
        return deskListVirtual;
    }

    public DataList<Bool> collectFlags() {
        return collectFlags;
    }

    public DataList<Int> collectFailedCnt() {
        return collectFailedCnt;
    }

    public DataList<Bool> specialtyFlags() {
        return specialtyFlags;
    }

    public DataList<Event> eventTimerList() {
        return eventTimerList;
    }

    public DataList<Event> eventActiveList() {
        return eventActiveList;
    }

    public Int tutorialStep() {
        return tutorialStep;
    }

    public DataList<Bool> firstFlag() {
        return firstFlag;
    }

    public Str frogName() {
        return frogName;
    }

    public Int frogAchieveId() {
        return frogAchieveId;
    }

    public DataList<Bool> achieveFlags() {
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

    public DataList<Int> gameFlags() {
        return gameFlags;
    }

    public Int tmpRaffleResult() {
        return tmpRaffleResult;
    }

    public Int iapCallBackCnt() {
        return iapCallBackCnt;
    }

    public Pay applicationData() {
        return applicationData;
    }

    public DataList<Int> applicationItemId() {
        return applicationItemId;
    }

    public DataList<Pay> payData() {
        return payData;
    }

    public Int coupon() {
        return coupon;
    }

    public DataList<Int> lastActPromo() {
        return lastActPromo;
    }

    public DataList<Int> pkgList() {
        return pkgList;
    }

    public DataList<Int> pkgCollectFlagsId() {
        return pkgCollectFlagsId;
    }

    public DataList<Int> pkgSpecialtyFlagsId() {
        return pkgSpecialtyFlagsId;
    }

    public Int requestCount() {
        return requestCount;
    }

    public Int requestId() {
        return requestId;
    }

    public Int requestTimer() {
        return requestTimer;
    }

    public Bool requestAutoPlay() {
        return requestAutoPlay;
    }

    public Int addAlbumPage() {
        return addAlbumPage;
    }

    public DataList<Int> pkgAchieveFlagsId() {
        return pkgAchieveFlagsId;
    }

    public Int langId() {
        return langId;
    }

    public DataList<Goal> goalList() {
        return goalList;
    }

    public DataList<Goal> pkgGoalList() {
        return pkgGoalList;
    }

    public DateTime lastServerDateTime() {
        return lastServerDateTime;
    }
    // getter end

    public boolean getAllItem(OnLoadedListener listener) {
        try {
            resizeData(itemList, ALL_ITEMS_ARRAY.length * 8 + 4 - itemList.length());
            writeAllItems();
            reload(listener);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void writeAllItems() throws IOException {
        r.seek(itemList.offset());
        r.writeInt(ALL_ITEMS_ARRAY.length);
        for (int i : ALL_ITEMS_ARRAY) {
            r.writeInt(i & 0x7fffffff);
            r.writeInt(((i >>> 31) & 1) == 1 ? 1 : Item.MAX_STOCK);
        }
    }

    private void resizeData(Data data, int len) throws IOException {
        if (len != 0) {
            int tail = (int) (data.offset() + data.length());
            byte[] buffer = new byte[(int) (r.length() - tail)];
            r.seek(tail);
            r.readFully(buffer);
            r.setLength(r.length() + len);
            r.seek(tail + len);
            r.write(buffer);
        }
    }

    public boolean haveAllAchieve() {
        return checkFlagIds(pkgAchieveFlagsId, ACHIEVE_FLAGS_IDS)
                && checkFlags(achieveFlags, ACHIEVE_FLAGS_BITS, ACHIEVE_FLAGS_BITS_LEN);
    }

    public boolean haveAllCollect() {
        return checkFlagIds(pkgCollectFlagsId, COLLECT_FLAGS_IDS)
                && checkFlags(collectFlags, COLLECT_FLAGS_BITS, COLLECT_FLAGS_BITS_LEN);
    }

    public boolean haveAllSpecialty() {
        return checkFlagIds(pkgSpecialtyFlagsId, SPECIALTY_FLAGS_IDS)
                && checkFlags(specialtyFlags, SPECIALTY_FLAGS_BITS, SPECIALTY_FLAGS_BITS_LEN);
    }

    public boolean checkFlagIds(DataList<Int> flagIds, int[] flagIdArray) {
        if (version >= VERSION_140) {
            if (flagIdArray.length != flagIds.size()) {
                return false;
            }
            List<Int> data = flagIds.data();
            for (int id : flagIdArray) {
                if (data.indexOf(new Int(id)) == -1) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkFlags(DataList<Bool> flags, long flagBits, int len) {
        for (int i = 0; i < len; i++) {
            if (((flagBits >>> i) & 1) == 1 && !flags.get(i).value()) {
                return false;
            }
        }
        return true;
    }

    public boolean getAllAchieve(OnLoadedListener listener) {
        return setFlags(achieveFlags, ACHIEVE_FLAGS_BITS, ACHIEVE_FLAGS_BITS_LEN)
                && setFlagIds(pkgAchieveFlagsId, ACHIEVE_FLAGS_IDS, listener);
    }

    public boolean getAllCollect(OnLoadedListener listener) {
        return setFlags(collectFlags, COLLECT_FLAGS_BITS, COLLECT_FLAGS_BITS_LEN)
                && setFlagIds(pkgCollectFlagsId, COLLECT_FLAGS_IDS, listener);
    }

    public boolean getAllSpecialty(OnLoadedListener listener) {
        return setFlags(specialtyFlags, SPECIALTY_FLAGS_BITS, SPECIALTY_FLAGS_BITS_LEN)
                && setFlagIds(pkgSpecialtyFlagsId, SPECIALTY_FLAGS_IDS, listener);
    }

    public boolean setFlagIds(DataList<Int> flagIds, int[] flagIdArray, OnLoadedListener listener) {
        if (version >= VERSION_140) {
            try {
                resizeData(flagIds, flagIdArray.length * 4 + 4 - flagIds.length());
                r.seek(flagIds.offset());
                r.writeInt(flagIdArray.length);
                for (int i : flagIdArray) {
                    r.writeInt(i);
                }
                reload(listener);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public boolean setFlags(DataList<Bool> flags, long flagBits, int len) {
        for (int i = 0; i < len; i++) {
            if (!flags.get(i).value(((flagBits >>> i) & 1) == 1).save()) {
                return false;
            }
        }
        return true;
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
                ", hoten=" + hoten +
                ", clover=" + clover +
                ", ticket=" + ticket +
                ", cloverList=" + cloverList +
                ", lastDateTime=" + lastDateTime +
                ", mailListNextId=" + mailListNextId +
                ", mailList=" + mailList +
                ", itemList=" + itemList +
                ", bagList=" + bagList +
                ", deskList=" + deskList +
                ", bagListVirtual=" + bagListVirtual +
                ", deskListVirtual=" + deskListVirtual +
                ", collectFlags=" + collectFlags +
                ", collectFailedCnt=" + collectFailedCnt +
                ", specialtyFlags=" + specialtyFlags +
                ", eventTimerList=" + eventTimerList +
                ", eventActiveList=" + eventActiveList +
                ", tutorialStep=" + tutorialStep +
                ", firstFlag=" + firstFlag +
                ", frogName=" + frogName +
                ", frogAchieveId=" + frogAchieveId +
                ", achieveFlags=" + achieveFlags +
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
                ", gameFlags=" + gameFlags +
                ", tmpRaffleResult=" + tmpRaffleResult +
                ", iapCallBackCnt=" + iapCallBackCnt +
                ", applicationData=" + applicationData +
                ", applicationItemId=" + applicationItemId +
                ", payData=" + payData +
                ", coupon=" + coupon +
                ", lastActPromo=" + lastActPromo +
                ", pkgList=" + pkgList +
                ", pkgCollectFlagsId=" + pkgCollectFlagsId +
                ", pkgSpecialtyFlagsId=" + pkgSpecialtyFlagsId +
                ", requestCount=" + requestCount +
                ", requestId=" + requestId +
                ", requestTimer=" + requestTimer +
                ", requestAutoPlay=" + requestAutoPlay +
                ", addAlbumPage=" + addAlbumPage +
                ", pkgAchieveFlagsId=" + pkgAchieveFlagsId +
                ", langId=" + langId +
                ", goalList=" + goalList +
                ", pkgGoalList=" + pkgGoalList +
                ", lastServerDateTime=" + lastServerDateTime +
                '}';
    }

    @Override
    public boolean save() {
        // not need implement
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean write(RandomAccessFile r) {
        try {
            r.writeInt(version);
            r.writeInt(supportID);
            if (!hoten.write(r)) {
                return false;
            }
            if (!clover.write(r)) {
                return false;
            }
            if (!ticket.write(r)) {
                return false;
            }
            if (!cloverList.write(r)) {
                return false;
            }
            if (!lastDateTime.write(r)) {
                return false;
            }
            if (!mailListNextId.write(r)) {
                return false;
            }
            if (!mailList.write(r)) {
                return false;
            }
            if (!itemList.write(r)) {
                return false;
            }
            if (!bagList.write(r)) {
                return false;
            }
            if (!deskList.write(r)) {
                return false;
            }
            if (!bagListVirtual.write(r)) {
                return false;
            }
            if (!deskListVirtual.write(r)) {
                return false;
            }
            if (!collectFlags.write(r)) {
                return false;
            }
            if (!collectFailedCnt.write(r)) {
                return false;
            }
            if (!specialtyFlags.write(r)) {
                return false;
            }
            if (!eventTimerList.write(r)) {
                return false;
            }
            if (!eventActiveList.write(r)) {
                return false;
            }
            if (!tutorialStep.write(r)) {
                return false;
            }
            if (!firstFlag.write(r)) {
                return false;
            }
            if (!frogName.write(r)) {
                return false;
            }
            if (!frogAchieveId.write(r)) {
                return false;
            }
            if (!achieveFlags.write(r)) {
                return false;
            }
            if (!frogMotion.write(r)) {
                return false;
            }
            if (!home.write(r)) {
                return false;
            }
            if (!drift.write(r)) {
                return false;
            }
            if (!restTime.write(r)) {
                return false;
            }
            if (!lastTravelTime.write(r)) {
                return false;
            }
            if (!standby.write(r)) {
                return false;
            }
            if (!standbyWait.write(r)) {
                return false;
            }
            if (!bgmVolume.write(r)) {
                return false;
            }
            if (!seVolume.write(r)) {
                return false;
            }
            if (!noticeFlag.write(r)) {
                return false;
            }
            if (!gameFlags.write(r)) {
                return false;
            }
            if (!tmpRaffleResult.write(r)) {
                return false;
            }
            r.writeInt(versionStart);
            if (version >= VERSION_105) {
                if (!iapCallBackCnt.write(r)) {
                    return false;
                }
            }
            if (version >= VERSION_106) {
                if (!applicationData.write(r)) {
                    return false;
                }
                if (!applicationItemId.write(r)) {
                    return false;
                }
                if (!payData.write(r)) {
                    return false;
                }
            }
            if (version >= VERSION_120) {
                if (!coupon.write(r)) {
                    return false;
                }
                if (!lastActPromo.write(r)) {
                    return false;
                }
                if (!pkgList.write(r)) {
                    return false;
                }
                if (!pkgCollectFlagsId.write(r)) {
                    return false;
                }
                if (!pkgSpecialtyFlagsId.write(r)) {
                    return false;
                }
                if (!requestCount.write(r)) {
                    return false;
                }
                if (!requestId.write(r)) {
                    return false;
                }
                if (!requestTimer.write(r)) {
                    return false;
                }
                if (!requestAutoPlay.write(r)) {
                    return false;
                }
            }
            if (version >= VERSION_121) {
                if (!addAlbumPage.write(r)) {
                    return false;
                }
            }
            if (version >= VERSION_140) {
                if (pkgAchieveFlagsId.write(r)) {
                    return false;
                }
                if (langId.write(r)) {
                    return false;
                }
                if (goalList.write(r)) {
                    return false;
                }
                if (pkgGoalList.write(r)) {
                    return false;
                }
            }
            if (version >= VERSION_150) {
                if (lastServerDateTime.write(r)) {
                    return false;
                }
            }
            if (unknownData != null) {
                r.write(unknownData);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean writeToFile(File file) {
        try {
            return write(new RandomAccessFile(file, "rwd"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public interface OnLoadedListener {
        void onLoaded(GameData data);
    }
}
