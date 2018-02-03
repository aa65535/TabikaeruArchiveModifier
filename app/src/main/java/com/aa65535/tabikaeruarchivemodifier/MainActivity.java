package com.aa65535.tabikaeruarchivemodifier;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aa65535.tabikaeruarchivemodifier.model.Bool;
import com.aa65535.tabikaeruarchivemodifier.model.Event;
import com.aa65535.tabikaeruarchivemodifier.model.GameData;
import com.aa65535.tabikaeruarchivemodifier.model.Int;
import com.aa65535.tabikaeruarchivemodifier.model.Item;
import com.aa65535.tabikaeruarchivemodifier.model.Mail;
import com.aa65535.tabikaeruarchivemodifier.model.Mail.Type;
import com.aa65535.tabikaeruarchivemodifier.utils.AlbumsExporter;
import com.aa65535.tabikaeruarchivemodifier.utils.AlbumsExporter.ProgressListener;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    private static final int REQUEST_CODE_FILE_PICKER = 0x1233;
    private static final int REQUEST_CODE_REQUEST_PERMISSIONS = 0x1784;

    private static final int WHAT_WRITE_CALENDAR = 0x334;

    private EditText cloverInput;
    private EditText ticketsInput;
    private EditText dateInput;
    private Button cloverButton;
    private Button ticketsButton;
    private List<View> viewList = new ArrayList<>();

    private File archive;
    private GameData gameData;
    private AlbumsExporter exporter;
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

    private void pickArchive() {
        File parentFile = archive.getParentFile();
        while (!parentFile.exists()) {
            parentFile = parentFile.getParentFile();
        }
        new LFilePicker()
                .withActivity(MainActivity.this)
                .withRequestCode(REQUEST_CODE_FILE_PICKER)
                .withTitle(getString(R.string.archive_pick))
                .withBackgroundColor("#3F51B5")
                .withFileFilter(new String[]{"sav"})
                .withMutilyMode(false)
                .withChooseMode(true)
                .withStartPath(parentFile.getAbsolutePath())
                .start();
    }

    private void initView() {
        viewList.add(cloverInput = findViewById(R.id.et_clover));
        viewList.add(ticketsInput = findViewById(R.id.et_tickets));
        dateInput = findViewById(R.id.et_date);
        cloverButton = findViewById(R.id.save_clover);
        ticketsButton = findViewById(R.id.save_tickets);
        viewList.add(findViewById(R.id.advance_date));
        viewList.get(viewList.size() - 1).setOnClickListener(this);
        cloverButton.setOnClickListener(this);
        ticketsButton.setOnClickListener(this);
        cloverInput.addTextChangedListener(new MyTextWatcher(cloverButton));
        ticketsInput.addTextChangedListener(new MyTextWatcher(ticketsButton));
    }

    private void initData() {
        if (archive.exists()) {
            if (archive.canWrite()) {
                try {
                    if (gameData == null) {
                        gameData = GameData.load(archive);
                    } else {
                        gameData.reload();
                    }
                } catch (UnsupportedOperationException e) {
                    for (View view : viewList) {
                        view.setEnabled(false);
                    }
                    Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                for (View view : viewList) {
                    view.setEnabled(gameData.loaded());
                }
                for (Event event : gameData.eventActiveList()) {
                    Log.d("Active", event.toString());
                }
                for (Event event : gameData.eventTimerList()) {
                    Log.d("Timer", event.toString());
                }
                String cloverData = gameData.clover().toString();
                String ticketsData = gameData.ticket().toString();
                cloverButton.setTag(cloverData);
                ticketsButton.setTag(ticketsData);
                cloverInput.setText(cloverData);
                ticketsInput.setText(ticketsData);
                dateInput.setText(gameData.lastDateTime().getText());
                if (exporter == null) {
                    exporter = initAlbumsExporter();
                } else {
                    exporter.refresh();
                }
            } else {
                Toasty.error(context, getString(R.string.archive_permission_denied)).show();
            }
        } else {
            pickArchive();
        }
    }

    private AlbumsExporter initAlbumsExporter() {
        File picture = new File(archive.getParentFile(), "Picture");
        File filesDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Tabikaeru");
        return new AlbumsExporter(picture, filesDir).setProgressListener(new ProgressListener() {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_FILE_PICKER && data != null) {
            ArrayList<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);
            archive = new File(list.get(0));
            initData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_export_albums:
                if (exporter != null && gameData.loaded()) {
                    exporter.export();
                }
                return true;
            case R.id.action_archive_pick:
                pickArchive();
                return true;
            case R.id.action_get_all_achieve:
            case R.id.action_get_all_collect:
            case R.id.action_get_all_specialty:
            case R.id.action_set_all_item_stock:
            case R.id.action_call_frog_back_home:
                if (gameData.loaded()) {
                    confirm(item.getItemId());
                }
                return true;
            case R.id.action_set_leaflet_to_gift:
                if (gameData.loaded()) {
                    setLeafletToGift();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
        setFlags(gameData.achieveFlags(), 0x1fcf, 13);
    }

    private void getAllCollect() {
        setFlags(gameData.collectFlags(), 0x61ea6, 19);
    }

    private void getAllSpecialty() {
        setFlags(gameData.specialtyFlags(), 0x7ffff61ea6L, 39);
    }

    private void setFlags(List<Bool> flags, long flagBits, int len) {
        for (int i = 0; i < len; i++) {
            if (!flags.get(i).value(((flagBits >>> i) & 1) == 1).write()) {
                Toasty.error(this, getString(R.string.failure_message)).show();
                return;
            }
        }
        Toasty.success(this, getString(R.string.success_message)).show();
    }

    private void setAllItemStock() {
        for (Item item : gameData.itemList()) {
            if (!item.stock(Item.MAX_STOCK).write()) {
                Toasty.error(this, getString(R.string.failure_message)).show();
                return;
            }
        }
        Toasty.success(this, getString(R.string.success_message)).show();
    }

    private void setLeafletToGift() {
        for (Mail mail : gameData.mailList()) {
            if (mail.type().value() == Type.LEAFLET) {
                mail.type(Type.GIFT).clover(99).write();
            }
        }
        Toasty.success(this, getString(R.string.success_message)).show();
    }

    private void triggerEvent(int evtType) {
        for (Event event : gameData.eventTimerList()) {
            if (event.evtType().value() == evtType) {
                int sec = event.timeSpanSec().value();
                gameData.lastDateTime().set(Calendar.getInstance()).add(Calendar.SECOND, -sec);
                break;
            }
        }
        writeCalendar();
    }

    private void checkFrogState() {
        if (needGoTravel()) {
            new Builder(this)
                    .setTitle(R.string.operation_title)
                    .setMessage(R.string.go_travel_confirm)
                    .setCancelable(false)
                    .setNegativeButton(R.string.back_home, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            triggerEvent(Event.Type.BACK_HOME);
                        }
                    })
                    .setPositiveButton(R.string.go_travel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            triggerEvent(Event.Type.GO_TRAVEL);
                        }
                    })
                    .create()
                    .show();
        } else {
            triggerEvent(Event.Type.BACK_HOME);
        } 
    }

    private boolean needGoTravel() {
        if (!gameData.home().value()) {
            return false;
        }
        Event goTravel = null, backHome = null;
        for (Event event : gameData.eventTimerList()) {
            switch (event.evtType().value()) {
                case Event.Type.GO_TRAVEL:
                    goTravel = event;
                    break;
                case Event.Type.BACK_HOME:
                    backHome = event;
                    break;
            }
        }
        if (goTravel == null || backHome == null) {
            return false;
        }
        long t1 = gameData.lastDateTime().value().getTimeInMillis() + goTravel.timeSpanSec().value() * 1000;
        return Calendar.getInstance().getTimeInMillis() <= t1
                && goTravel.timeSpanSec().value() < backHome.timeSpanSec().value();
    }

    private void writeCalendar() {
        if (gameData.lastDateTime().write()) {
            dateInput.setText(gameData.lastDateTime().getText());
            Toasty.success(context, getString(R.string.success_message)).show();
        } else {
            Toasty.error(context, getString(R.string.failure_message)).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_clover:
                writeInt(v, gameData.clover(), cloverInput.getText().toString());
                break;
            case R.id.save_tickets:
                writeInt(v, gameData.ticket(), ticketsInput.getText().toString());
                break;
            case R.id.advance_date:
                gameData.lastDateTime().add(Calendar.HOUR_OF_DAY, -3);
                handler.removeMessages(WHAT_WRITE_CALENDAR);
                handler.sendEmptyMessageDelayed(WHAT_WRITE_CALENDAR, 500);
                break;
        }
    }

    private void writeInt(View v, Int val, String s) {
        try {
            boolean ret = val.value(Integer.parseInt(s)).write();
            v.setTag(s);
            v.setEnabled(!ret);
            if (ret) {
                Toasty.success(context, getString(R.string.success_message)).show();
            } else {
                Toasty.error(context, getString(R.string.failure_message)).show();
            }
        } catch (NumberFormatException e) {
            Toasty.error(context, getString(R.string.number_format_error_message)).show();
        }
    }

    private static class MyTextWatcher implements TextWatcher {
        private Button button;

        MyTextWatcher(Button button) {
            this.button = button;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            button.setEnabled(s.length() > 0 && !s.toString().equals(button.getTag()));
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
                case WHAT_WRITE_CALENDAR:
                    activity.writeCalendar();
                    break;
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
                    activity.checkFrogState();
                    break;
            }
        }
    }
}
