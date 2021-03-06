package com.aa65535.tabikaeruarchivemodifier;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.aa65535.tabikaeruarchivemodifier.model.Clover;
import com.aa65535.tabikaeruarchivemodifier.model.Event;
import com.aa65535.tabikaeruarchivemodifier.model.GameData;
import com.aa65535.tabikaeruarchivemodifier.model.GameData.OnLoadedListener;
import com.aa65535.tabikaeruarchivemodifier.model.Item;
import com.aa65535.tabikaeruarchivemodifier.model.Mail;
import com.aa65535.tabikaeruarchivemodifier.utils.AlbumsDeduplication;
import com.aa65535.tabikaeruarchivemodifier.utils.AlbumsExporter;
import com.aa65535.tabikaeruarchivemodifier.utils.Constants;
import com.daimajia.numberprogressbar.NumberProgressBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements Constants, OnLoadedListener, OnSeekBarChangeListener {
    private static final int REQUEST_CODE_REQUEST_PERMISSIONS = 0x1784;

    private File archive;
    private boolean loaded;
    private GameData gameData;
    private AlbumsExporter exporter;
    private AlbumsDeduplication deduplication;
    private SparseArray<MenuItem> menuItemList;
    private SparseArray<DataBinder> dataBinderList;
    private final Context context = this;
    private final Handler handler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File cacheDir = getExternalCacheDir();
        if (cacheDir == null) {
            Toasty.error(context, "Shared storage is not currently available.").show();
            finish();
            return;
        }
        try {
            archive = findArchive(cacheDir.getParentFile().getParentFile());
            initView();
        } catch (FileNotFoundException e) {
            Toasty.error(this, e.getMessage()).show();
            finish();
        }
    }

    private File findArchive(File dataDir) throws FileNotFoundException {
        List<PackageInfo> installedPackages = getPackageManager().getInstalledPackages(0);
        for (PackageInfo packageInfo : installedPackages) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String packageName = packageInfo.packageName;
                if (packageName.startsWith("jp.co.hit_point")) {
                    File file = new File(dataDir, packageName + "/files/Tabikaeru.sav");
                    if (file.exists()) {
                        return file;
                    }
                }
            }
        }
        throw new FileNotFoundException("archive file not found.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        verifyStoragePermissions(this);
    }

    @Override
    protected void onDestroy() {
        if (gameData != null) {
            gameData.destroy();
        }
        super.onDestroy();
    }

    private void initView() {
        dataBinderList = new SparseArray<>();
        dataBinderList.put(R.id.clover_stock, new DataBinder(this, R.id.clover_stock) {
            @Override
            public String getValue() {
                return gameData.clover().toString();
            }
        });
        dataBinderList.put(R.id.ticket_stock, new DataBinder(this, R.id.ticket_stock) {
            @Override
            public String getValue() {
                return gameData.ticket().toString();
            }
        });
        dataBinderList.put(R.id.coupon_stock, new DataBinder(this, R.id.coupon_stock) {
            @Override
            public String getValue() {
                return gameData.coupon().toString();
            }
        });
        dataBinderList.put(R.id.last_game_time, new DataBinder(this, R.id.last_game_time) {
            @Override
            public String getValue() {
                return gameData.lastDateTime().toString();
            }
        });
        dataBinderList.put(R.id.next_go_travel_time, new DataBinder(this, R.id.next_go_travel_time) {
            @Override
            public String getValue() {
                Event type = getTimerEventByType(Event.Type.GO_TRAVEL);
                return type != null ? type.triggerTime(gameData.lastDateTime()).toString() : null;
            }
        });
        dataBinderList.put(R.id.next_back_home_time, new DataBinder(this, R.id.next_back_home_time) {
            @Override
            public String getValue() {
                Event type = getTimerEventByType(Event.Type.BACK_HOME);
                return type != null ? type.triggerTime(gameData.lastDateTime()).toString() : null;
            }
        });
        dataBinderList.put(R.id.bgm_volume, new DataBinder(this, R.id.bgm_volume) {
            @Override
            public String getValue() {
                return gameData.bgmVolume().toString();
            }
        });
        dataBinderList.put(R.id.se_volume, new DataBinder(this, R.id.se_volume) {
            @Override
            public String getValue() {
                return gameData.seVolume().toString();
            }
        });
        SeekBar bgmVolume = dataBinderList.get(R.id.bgm_volume).getView();
        bgmVolume.setTag(findViewById(R.id.bgm_volume_v));
        bgmVolume.setOnSeekBarChangeListener(this);
        SeekBar seVolume = dataBinderList.get(R.id.se_volume).getView();
        seVolume.setTag(findViewById(R.id.se_volume_v));
        seVolume.setOnSeekBarChangeListener(this);
    }

    private void initData() {
        loaded = false;
        if (archive.canWrite()) {
            if (exporter == null) {
                exporter = initAlbumsExporter();
            } else {
                exporter.refresh();
            }
            if (deduplication == null) {
                deduplication = initAlbumsDeduplication();
            }
            try {
                if (gameData == null) {
                    gameData = GameData.load(archive, this);
                } else {
                    gameData.reload(this);
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                Toasty.error(this, e.getMessage()).show();
                finish();
            }
        } else {
            Toasty.error(context, getString(R.string.archive_permission_denied)).show();
            finish();
        }
    }

    @Override
    public void onLoaded(GameData gameData) {
        loaded = true;
        this.gameData = gameData;
        for (int i = 0; i < dataBinderList.size(); i++) {
            dataBinderList.valueAt(i).setValue();
        }
        invalidateOptionsMenu();
    }

    @NonNull
    private AlbumsExporter initAlbumsExporter() {
        File pictureDir = new File(archive.getParentFile(), "Picture");
        File outputDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Tabikaeru");
        return new AlbumsExporter(pictureDir, outputDir, new AlbumsExportProgressListener(this));
    }

    private AlbumsDeduplication initAlbumsDeduplication() {
        File pictureDir = new File(archive.getParentFile(), "Picture");
        return new AlbumsDeduplication(pictureDir, new AlbumsDeduplicationProgressListener(this));
    }

    private void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE"
            }, REQUEST_CODE_REQUEST_PERMISSIONS);
        } else {
            initData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_REQUEST_PERMISSIONS) {
            initData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        menuItemList = new SparseArray<>();
        menuItemList.put(R.id.action_save, menu.findItem(R.id.action_save));
        menuItemList.put(R.id.action_album_deduplication, menu.findItem(R.id.action_album_deduplication));
        menuItemList.put(R.id.action_add_album_page, menu.findItem(R.id.action_add_album_page));
        menuItemList.put(R.id.action_reset_request_count, menu.findItem(R.id.action_reset_request_count));
        menuItemList.put(R.id.action_grow_all_clover, menu.findItem(R.id.action_grow_all_clover));
        menuItemList.put(R.id.action_get_all_achieve, menu.findItem(R.id.action_get_all_achieve));
        menuItemList.put(R.id.action_get_all_collect, menu.findItem(R.id.action_get_all_collect));
        menuItemList.put(R.id.action_get_all_specialty, menu.findItem(R.id.action_get_all_specialty));
        menuItemList.put(R.id.action_set_all_item_stock, menu.findItem(R.id.action_set_all_item_stock));
        menuItemList.put(R.id.action_call_frog_back_home, menu.findItem(R.id.action_call_frog_back_home));
        menuItemList.put(R.id.action_call_frog_go_travel, menu.findItem(R.id.action_call_frog_go_travel));
        menuItemList.put(R.id.action_set_leaflet_to_gift, menu.findItem(R.id.action_set_leaflet_to_gift));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < menuItemList.size(); i++) {
            menuItemList.valueAt(i).setEnabled(loaded);
        }
        if (loaded) {
            boolean atHome = atHome();
            menuItemList.get(R.id.action_call_frog_back_home).setVisible(!atHome);
            menuItemList.get(R.id.action_call_frog_go_travel).setVisible(atHome);
            if (gameData.version() >= VERSION_121) {
                menuItemList.get(R.id.action_add_album_page).setEnabled(gameData.addAlbumPage().value() < MAX_ALBUM_PAGE);
            } else {
                menuItemList.get(R.id.action_add_album_page).setVisible(false);
            }
            menuItemList.get(R.id.action_reset_request_count).setEnabled(gameData.requestCount().value() < 3);
            menuItemList.get(R.id.action_get_all_achieve).setEnabled(!gameData.haveAllAchieve());
            menuItemList.get(R.id.action_get_all_collect).setEnabled(!gameData.haveAllCollect());
            menuItemList.get(R.id.action_get_all_specialty).setEnabled(!gameData.haveAllSpecialty());
        }
        menu.findItem(R.id.action_export_albums).setEnabled(exporter != null && !exporter.isEmpty());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveGameData();
                return true;
            case R.id.action_export_albums:
                item.setEnabled(false);
                exporter.export();
                return true;
            case R.id.action_album_deduplication:
                deduplicationAlbums();
                return true;
            case R.id.action_add_album_page:
                addAlbumPage();
                return true;
            case R.id.action_reset_request_count:
                resetRequestCount();
                return true;
            case R.id.action_grow_all_clover:
                growClover();
                return true;
            case R.id.action_get_all_achieve:
            case R.id.action_get_all_collect:
            case R.id.action_get_all_specialty:
            case R.id.action_set_all_item_stock:
            case R.id.action_call_frog_back_home:
            case R.id.action_call_frog_go_travel:
                confirm(item.getItemId());
                return true;
            case R.id.action_set_leaflet_to_gift:
                setLeafletToGift();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveGameData() {
        try {
            EditText cloverStock = dataBinderList.get(R.id.clover_stock).getView();
            EditText ticketStock = dataBinderList.get(R.id.ticket_stock).getView();
            EditText couponStock = dataBinderList.get(R.id.coupon_stock).getView();
            SeekBar bgmVolume = dataBinderList.get(R.id.bgm_volume).getView();
            SeekBar seVolume = dataBinderList.get(R.id.se_volume).getView();
            boolean r1 = gameData.clover().value(Integer.parseInt(cloverStock.getText().toString())).save();
            boolean r2 = gameData.ticket().value(Integer.parseInt(ticketStock.getText().toString())).save();
            boolean r5 = gameData.coupon().value(Integer.parseInt(couponStock.getText().toString())).save();
            boolean r3 = gameData.bgmVolume().value(bgmVolume.getProgress()).save();
            boolean r4 = gameData.seVolume().value(seVolume.getProgress()).save();
            if (r1 && r2 && r3 && r4 && r5) {
                Toasty.success(this, getString(R.string.success_message)).show();
            } else {
                Toasty.error(this, getString(R.string.failure_message)).show();
            }
        } catch (NumberFormatException e) {
            Toasty.error(context, getString(R.string.number_format_error_message)).show();
        }
    }

    private void deduplicationAlbums() {
        deduplication.deduplication();
    }

    private void addAlbumPage() {
        if (gameData.addAlbumPage().value(MAX_ALBUM_PAGE).save()) {
            Toasty.success(this, getString(R.string.success_message)).show();
        } else {
            Toasty.error(this, getString(R.string.failure_message)).show();
        }
    }

    private void growClover() {
        for (Clover clover : gameData.cloverList()) {
            if (!clover.timeSpanSec().value(-127).save()) {
                Toasty.error(this, getString(R.string.failure_message)).show();
                return;
            }
        }
        Toasty.success(this, getString(R.string.success_message)).show();
    }

    private void resetRequestCount() {
        if (gameData.requestCount().value(3).save()) {
            Toasty.success(this, getString(R.string.success_message)).show();
        } else {
            Toasty.error(this, getString(R.string.failure_message)).show();
        }
    }

    private void confirm(final int actionId) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.operation_title)
                .setMessage(R.string.operation_warning)
                .setCancelable(false)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.sendEmptyMessage(actionId);
                    }
                })
                .create()
                .show();
    }

    private void getAllAchieve() {
        if (gameData.getAllAchieve(this)) {
            Toasty.success(this, getString(R.string.success_message)).show();
        } else {
            Toasty.success(this, getString(R.string.success_message)).show();
        }
    }

    private void getAllCollect() {
        if (gameData.getAllCollect(this)) {
            Toasty.success(this, getString(R.string.success_message)).show();
        } else {
            Toasty.success(this, getString(R.string.success_message)).show();
        }
    }

    private void getAllSpecialty() {
        if (gameData.getAllSpecialty(this)) {
            Toasty.success(this, getString(R.string.success_message)).show();
        } else {
            Toasty.success(this, getString(R.string.success_message)).show();
        }
    }

    private void setAllItemStock() {
        if (gameData.getAllItem(this)) {
            Toasty.success(this, getString(R.string.success_message)).show();
        } else {
            Toasty.error(this, getString(R.string.failure_message)).show();
        }
    }

    private void setLeafletToGift() {
        for (Mail mail : gameData.mailList()) {
            if (Mail.Type.LEAFLET.equals(mail.type())) {
                mail.type(Mail.Type.GIFT).clover(Item.MAX_STOCK).save();
            }
        }
        Toasty.success(this, getString(R.string.success_message)).show();
    }

    @Nullable
    private Event getTimerEventByType(Event.Type evtType) {
        for (Event event : gameData.eventTimerList()) {
            if (event.evtType().equals(evtType)) {
                return event;
            }
        }
        return null;
    }

    private void triggerEvent(Event.Type evtType) {
        Event event = getTimerEventByType(evtType);
        int timeSpanSec = 0;
        if (event != null) {
            timeSpanSec = -event.timeSpanSec().value();
        } else if (evtType == Event.Type.BACK_HOME) {
            timeSpanSec = -1800;
        }
        if (timeSpanSec != 0) {
            Calendar to = Calendar.getInstance();
            to.add(Calendar.SECOND, timeSpanSec);
            Calendar from = gameData.lastDateTime().value();
            int amount = (int) (to.getTimeInMillis() - from.getTimeInMillis());
            if (gameData.lastDateTime().add(Calendar.MILLISECOND, amount).save()) {
                dataBinderList.get(R.id.last_game_time).setValue();
                dataBinderList.get(R.id.next_go_travel_time).setValue();
                dataBinderList.get(R.id.next_back_home_time).setValue();
                Toasty.success(context, getString(R.string.success_message)).show();
            } else {
                Toasty.error(context, getString(R.string.failure_message)).show();
            }
        }
    }

    private boolean atHome() {
        Event goTravelEvent = getTimerEventByType(Event.Type.GO_TRAVEL);
        Event backHomeEvent = getTimerEventByType(Event.Type.BACK_HOME);
        if (goTravelEvent == null || backHomeEvent == null) {
            return false;
        }
        long goTravelTimeInMillis = gameData.lastDateTime().value().getTimeInMillis() +
                goTravelEvent.timeSpanSec().value() * 1000;
        return Calendar.getInstance().getTimeInMillis() < goTravelTimeInMillis &&
                goTravelEvent.timeSpanSec().value() < backHomeEvent.timeSpanSec().value();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        ((TextView) seekBar.getTag()).setText(String.format(Locale.getDefault(), "%d%%", progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    private static class AlbumsExportProgressListener implements AlbumsExporter.OnProgressListener {
        private TextView tip;
        private NumberProgressBar progressBar;
        private MainActivity activity;
        private AlertDialog dialog;

        public AlbumsExportProgressListener(MainActivity activity) {
            this.activity = activity;
            View view = View.inflate(activity, R.layout.progress, null);
            this.dialog = new Builder(activity)
                    .setTitle(activity.getString(R.string.export_albums))
                    .setCancelable(false)
                    .setView(view)
                    .create();
            this.tip = view.findViewById(R.id.tip);
            this.progressBar = view.findViewById(R.id.progressBar);
        }

        @Override
        public void onBefore(int count) {
            dialog.show();
            progressBar.setProgress(0);
            progressBar.setMax(count);
        }

        @Override
        public void inProgress(String filename, int count, int progress) {
            progressBar.setProgress(progress);
            tip.setText(String.format(Locale.getDefault(), "%1$d/%2$d", progress, count));
        }

        @Override
        public void onAfter(String path, int count) {
            dialog.dismiss();
            Toasty.success(activity, activity.getString(R.string.export_albums_msg, count)).show();
        }

        @Override
        public void onEmpty() {
            Toasty.info(activity, activity.getString(R.string.no_albums_export)).show();
        }
    }

    private static class AlbumsDeduplicationProgressListener implements AlbumsDeduplication.OnProgressListener {
        private TextView tip;
        private NumberProgressBar progressBar;
        private MainActivity activity;
        private AlertDialog dialog;

        public AlbumsDeduplicationProgressListener(MainActivity activity) {
            this.activity = activity;
            View view = View.inflate(activity, R.layout.progress, null);
            this.dialog = new Builder(activity)
                    .setTitle(activity.getString(R.string.album_deduplication))
                    .setCancelable(false)
                    .setView(view)
                    .create();
            this.tip = view.findViewById(R.id.tip);
            this.progressBar = view.findViewById(R.id.progressBar);
        }

        @Override
        public void onBefore(int count) {
            dialog.show();
            progressBar.setProgress(0);
            progressBar.setMax(count);
        }

        @Override
        public void inProgress(int count, int progress) {
            progressBar.setProgress(progress);
            tip.setText(String.format(Locale.getDefault(), "%1$d/%2$d", progress, count));
        }

        @Override
        public void onAfter(int count) {
            dialog.dismiss();
            if (count > 0) {
                Toasty.success(activity, activity.getString(R.string.deduplication_albums_msg, count)).show();
            } else {
                Toasty.success(activity, activity.getString(R.string.albums_not_duplication)).show();
            }
        }

        @Override
        public void onEmpty() {
            Toasty.info(activity, activity.getString(R.string.albums_empty)).show();
        }
    }

    private abstract static class DataBinder {
        View view;

        public DataBinder(Activity activity, int id) {
            this.view = activity.findViewById(id);
        }

        public void setValue() {
            String value = getValue();
            ((ViewGroup) view.getParent()).setVisibility(value != null ? View.VISIBLE : View.GONE);
            if (value != null) {
                if (view instanceof TextView) {
                    ((TextView) view).setText(value);
                } else if (view instanceof SeekBar) {
                    ((SeekBar) view).setProgress(Integer.parseInt(value));
                }
            }
        }

        public abstract String getValue();

        @SuppressWarnings("unchecked")
        public <T extends View> T getView() {
            return (T) view;
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> reference;

        private MyHandler(MainActivity reference) {
            this.reference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = reference.get();
            if (null == activity) {
                return;
            }
            switch (msg.what) {
                case R.id.action_get_all_achieve:
                    activity.getAllAchieve();
                    break;
                case R.id.action_get_all_collect:
                    activity.getAllCollect();
                    break;
                case R.id.action_get_all_specialty:
                    activity.getAllSpecialty();
                    break;
                case R.id.action_set_all_item_stock:
                    activity.setAllItemStock();
                    break;
                case R.id.action_call_frog_back_home:
                    activity.triggerEvent(Event.Type.BACK_HOME);
                    break;
                case R.id.action_call_frog_go_travel:
                    activity.triggerEvent(Event.Type.GO_TRAVEL);
                    break;
            }
        }
    }
}
