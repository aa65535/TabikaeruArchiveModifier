package com.aa65535.tabikaeruarchivemodifier;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.aa65535.tabikaeruarchivemodifier.model.DateTime;
import com.aa65535.tabikaeruarchivemodifier.model.Event;
import com.aa65535.tabikaeruarchivemodifier.model.GameData;
import com.aa65535.tabikaeruarchivemodifier.model.GameData.OnLoadedListener;
import com.aa65535.tabikaeruarchivemodifier.model.Int;
import com.aa65535.tabikaeruarchivemodifier.model.Mail;
import com.aa65535.tabikaeruarchivemodifier.model.Mail.Type;
import com.aa65535.tabikaeruarchivemodifier.model.SimpleData;
import com.aa65535.tabikaeruarchivemodifier.utils.AlbumsExporter;
import com.aa65535.tabikaeruarchivemodifier.utils.AlbumsExporter.OnProgressListener;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements OnLoadedListener {
    private static final int REQUEST_CODE_REQUEST_PERMISSIONS = 0x1784;

    private File archive;
    private boolean loaded;
    private GameData gameData;
    private boolean flagsChecked;
    private AlbumsExporter exporter;
    private SparseArray<MenuItem> menuItemList;
    private SparseArray<TableRowData> rowDataList;
    private final Context context = this;
    private final Handler handler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File cacheDir = getExternalCacheDir();
        if (cacheDir == null) {
            Toasty.error(context, "shared storage is not currently available.").show();
            throw new RuntimeException("shared storage is not currently available.");
        }
        File dataDir = cacheDir.getParentFile().getParentFile();
        archive = new File(dataDir, "jp.co.hit_point.tabikaeru/files/Tabikaeru.sav");
        initView();
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
        rowDataList = new SparseArray<>();
        TableLayout parent = findViewById(R.id.parentLayout);
        EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        rowDataList.put(R.id.clover_stock, new TableRowData(R.string.clover_stock, editText, parent));
        editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        rowDataList.put(R.id.ticket_stock, new TableRowData(R.string.ticket_stock, editText, parent));
        editText = new EditText(this);
        editText.setEnabled(false);
        rowDataList.put(R.id.last_game_time, new TableRowData(R.string.last_game_time, editText, parent));
        editText = new EditText(this);
        editText.setEnabled(false);
        rowDataList.put(R.id.next_go_travel_time, new TableRowData(R.string.next_go_travel_time, editText, parent));
        editText = new EditText(this);
        editText.setEnabled(false);
        rowDataList.put(R.id.next_back_home_time, new TableRowData(R.string.next_back_home_time, editText, parent));
    }

    private void initData() {
        loaded = false;
        if (archive.canWrite()) {
            try {
                if (gameData == null) {
                    gameData = GameData.load(archive, this);
                } else {
                    gameData.reload(this);
                }
            } catch (UnsupportedOperationException e) {
                Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toasty.error(context, getString(R.string.archive_permission_denied)).show();
        }
    }

    @Override
    public void onLoaded(GameData gameData) {
        loaded = true;
        flagsChecked = false;
        this.gameData = gameData;
        DateTime lastDateTime = gameData.lastDateTime();
        rowDataList.get(R.id.clover_stock).setValue(gameData.clover(), 9);
        rowDataList.get(R.id.ticket_stock).setValue(gameData.ticket(), 3);
        rowDataList.get(R.id.last_game_time).setValue(lastDateTime, -1);
        if (atHome()) {
            Event goTravel = getTimerEventByType(Event.Type.GO_TRAVEL);
            //noinspection ConstantConditions
            rowDataList.get(R.id.next_go_travel_time)
                    .setValue(goTravel.triggerTime(lastDateTime), -1)
                    .setVisibility(View.VISIBLE);
        } else {
            rowDataList.get(R.id.next_go_travel_time).setVisibility(View.GONE);
        }
        Event backHome = getTimerEventByType(Event.Type.BACK_HOME);
        if (backHome != null) {
            rowDataList.get(R.id.next_back_home_time)
                    .setValue(backHome.triggerTime(lastDateTime), -1)
                    .setVisibility(View.VISIBLE);
        } else {
            rowDataList.get(R.id.next_back_home_time).setVisibility(View.GONE);
        }
        if (exporter == null) {
            exporter = initAlbumsExporter();
        } else {
            exporter.refresh();
        }
        getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
    }

    private AlbumsExporter initAlbumsExporter() {
        File picture = new File(archive.getParentFile(), "Picture");
        File filesDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Tabikaeru");
        return new AlbumsExporter(picture, filesDir).setOnProgressListener(new OnProgressListener() {
            View view = View.inflate(MainActivity.this, R.layout.progress, null);
            ProgressBar progressBar = view.findViewById(R.id.progress_bar);
            TextView tips = view.findViewById(R.id.progress_tips);
            AlertDialog dialog = new Builder(MainActivity.this)
                    .setTitle(R.string.export_albums)
                    .setView(view)
                    .setCancelable(false)
                    .create();

            @Override
            public void onBefore(int count) {
                progressBar.setMax(count);
                dialog.show();
            }

            @Override
            public void inProgress(String filename, int count, int progress) {
                progressBar.setProgress(progress);
                tips.setText(getString(R.string.progress_tips, filename, progress, count));
            }

            @Override
            public void onAfter(String path, int count) {
                dialog.dismiss();
                Toasty.success(context, getString(R.string.export_albums_msg, count)).show();
            }

            @Override
            public void isEmpty() {
                Toasty.info(context, getString(R.string.no_albums_export)).show();
            }
        });
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
        menuItemList.put(R.id.action_get_all_achieve, menu.findItem(R.id.action_get_all_achieve));
        menuItemList.put(R.id.action_get_all_collect, menu.findItem(R.id.action_get_all_collect));
        menuItemList.put(R.id.action_get_all_specialty, menu.findItem(R.id.action_get_all_specialty));
        menuItemList.put(R.id.action_set_all_item_stock, menu.findItem(R.id.action_set_all_item_stock));
        menuItemList.put(R.id.action_change_frog_state, menu.findItem(R.id.action_change_frog_state));
        menuItemList.put(R.id.action_set_leaflet_to_gift, menu.findItem(R.id.action_set_leaflet_to_gift));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < menuItemList.size(); i++) {
            menuItemList.valueAt(i).setEnabled(loaded);
        }
        if (loaded) {
            menuItemList.get(R.id.action_change_frog_state).setTitle(atHome() ? R.string.call_frog_go_travel : R.string.call_frog_back_home);
            menuItemList.get(R.id.action_get_all_achieve).setEnabled(!gameData.haveAllAchieve());
            menuItemList.get(R.id.action_get_all_collect).setEnabled(!gameData.haveAllCollect());
            menuItemList.get(R.id.action_get_all_specialty).setEnabled(!gameData.haveAllSpecialty());
        }
        menu.findItem(R.id.action_export_albums).setEnabled(exporter != null);
        flagsChecked = true;
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveGameData();
                return true;
            case R.id.action_export_albums:
                exporter.export();
                return true;
            case R.id.action_get_all_achieve:
            case R.id.action_get_all_collect:
            case R.id.action_get_all_specialty:
            case R.id.action_set_all_item_stock:
            case R.id.action_change_frog_state:
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
            boolean r1 = rowDataList.get(R.id.clover_stock).saveData();
            boolean r2 = rowDataList.get(R.id.ticket_stock).saveData();
            if (r1 && r2) {
                Toasty.success(this, getString(R.string.success_message)).show();
            } else {
                Toasty.error(this, getString(R.string.failure_message)).show();
            }
        } catch (NumberFormatException e) {
            Toasty.error(context, getString(R.string.number_format_error_message)).show();
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
        if (gameData.getAllAchieve()) {
            Toasty.success(this, getString(R.string.success_message)).show();
        } else {
            Toasty.success(this, getString(R.string.success_message)).show();
        }
    }

    private void getAllCollect() {
        if (gameData.getAllCollect()) {
            Toasty.success(this, getString(R.string.success_message)).show();
        } else {
            Toasty.success(this, getString(R.string.success_message)).show();
        }
    }

    private void getAllSpecialty() {
        if (gameData.getAllSpecialty()) {
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

    private void changeFrogState() {
        triggerEvent(atHome() ? Event.Type.GO_TRAVEL : Event.Type.BACK_HOME);
    }

    private void setLeafletToGift() {
        for (Mail mail : gameData.mailList()) {
            if (mail.type().value() == Type.LEAFLET) {
                mail.type(Type.GIFT).clover(99).save();
            }
        }
        Toasty.success(this, getString(R.string.success_message)).show();
    }

    @Nullable
    private Event getTimerEventByType(int evtType) {
        for (Event event : gameData.eventTimerList()) {
            if (event.evtType().value() == evtType) {
                return event;
            }
        }
        return null;
    }

    private void triggerEvent(int evtType) {
        Event event = getTimerEventByType(evtType);
        if (event != null) {
            Calendar to = Calendar.getInstance();
            to.add(Calendar.SECOND, -event.timeSpanSec().value());
            Calendar from = gameData.lastDateTime().value();
            int amount = (int) (to.getTimeInMillis() - from.getTimeInMillis());
            gameData.lastDateTime().add(Calendar.MILLISECOND, amount);
            SimpleData value = rowDataList.get(R.id.next_go_travel_time).getValue();
            if (value != null) {
                ((DateTime) value).add(Calendar.MILLISECOND, amount);
            }
            value = rowDataList.get(R.id.next_back_home_time).getValue();
            if (value != null) {
                ((DateTime) value).add(Calendar.MILLISECOND, amount);
            }
            writeCalendar();
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

    private void writeCalendar() {
        if (gameData.lastDateTime().save()) {
            rowDataList.get(R.id.last_game_time).update();
            rowDataList.get(R.id.next_go_travel_time).update();
            rowDataList.get(R.id.next_back_home_time).update();
            Toasty.success(context, getString(R.string.success_message)).show();
        } else {
            Toasty.error(context, getString(R.string.failure_message)).show();
        }
    }

    private static class TableRowData {
        private SimpleData value;
        private EditText valueView;
        private final TableRow tableRow;

        public TableRowData(@StringRes int name, EditText valueView, TableLayout parentView) {
            this.valueView = valueView;
            TextView nameView = new TextView(valueView.getContext());
            tableRow = new TableRow(valueView.getContext());
            nameView.setText(name);
            nameView.setGravity(Gravity.END);
            tableRow.addView(nameView);
            tableRow.addView(valueView);
            parentView.addView(tableRow);
        }

        public SimpleData getValue() {
            return value;
        }

        public TableRowData setValue(SimpleData value, int maxlength) {
            this.value = value;
            valueView.setText(value.toString());
            if (maxlength >= 0) {
                valueView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxlength)});
            }
            return this;
        }

        public TableRowData update() {
            if (value != null) {
                valueView.setText(value.toString());
            }
            return this;
        }

        public boolean saveData() {
            if (value instanceof Int) {
                int v = Integer.parseInt(valueView.getText().toString());
                return ((Int) value).value(v).save();
            }
            return false;
        }

        public TableRowData setVisibility(int visibility) {
            tableRow.setVisibility(visibility);
            return this;
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
                case R.id.action_change_frog_state:
                    activity.changeFrogState();
                    break;
            }
        }
    }
}
