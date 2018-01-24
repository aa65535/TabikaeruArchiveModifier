package com.aa65535.tabikaeruarchivemodifier;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    private static final int REQUEST_CODE = 0x1784;

    private EditText cloverInput;
    private EditText ticketsInput;

    private File archive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //noinspection ConstantConditions
        archive = new File(getExternalCacheDir().getParentFile().getParentFile(),
                "jp.co.hit_point.tabikaeru/files/GameData.sav");
        if (archive.exists()) {
            verifyStoragePermissions(this);
        } else {
            showToast(R.string.archive_not_found);
        } 
    }

    private void initView() {
        if (archive.canWrite()) {
            cloverInput = findViewById(R.id.et_clover);
            ticketsInput = findViewById(R.id.et_tickets);
            Button cloverButton = findViewById(R.id.save_clover);
            Button ticketsButton = findViewById(R.id.save_tickets);
            String cloverData = getString(R.string.number, readInt(archive, 0xc70));
            String ticketsData = getString(R.string.number, readInt(archive, 0xc74));
            cloverInput.setText(cloverData);
            ticketsInput.setText(ticketsData);
            cloverButton.setTag(cloverData);
            ticketsButton.setTag(ticketsData);
            cloverButton.setOnClickListener(this);
            ticketsButton.setOnClickListener(this);
            cloverInput.addTextChangedListener(new MyTextWatcher(cloverButton));
            ticketsInput.addTextChangedListener(new MyTextWatcher(ticketsButton));
        } else {
            showToast(R.string.archive_permission_denied);
        }
    }

    public void verifyStoragePermissions(Activity activity) {
        try {
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{
                        "android.permission.READ_EXTERNAL_STORAGE",
                        "android.permission.WRITE_EXTERNAL_STORAGE"
                }, REQUEST_CODE);
            } else {
                initView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showToast(@StringRes int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            initView();
        }
    }

    @Override
    public void onClick(View v) {
        v.setEnabled(false);
        try {
            String s = null;
            boolean ret = false;
            switch (v.getId()) {
                case R.id.save_clover:
                    s = cloverInput.getText().toString();
                    ret = writeInt(archive, 0xc70, Integer.parseInt(s));
                    break;
                case R.id.save_tickets:
                    s = ticketsInput.getText().toString();
                    ret = writeInt(archive, 0xc74, Integer.parseInt(s));
                    break;
            }
            v.setTag(s);
            showToast(ret ? R.string.success_msg : R.string.failure_msg);
        } catch (NumberFormatException e) {
            showToast(R.string.number_err_msg);
        }
    }

    /**
     * Big Endian to Little Endian
     */
    private static int bigToLittle(int v) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[4]);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.asIntBuffer().put(v);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getInt();
    }

    /**
     * Little Endian to Big Endian
     */
    private static int littleToBig(int v) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[4]);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.asIntBuffer().put(v);
        bb.order(ByteOrder.BIG_ENDIAN);
        return bb.getInt();
    }

    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    private static int readInt(File archive, int offset) {
        RandomAccessFile r = null;
        try {
            r = new RandomAccessFile(archive, "r");
            r.seek(offset);
            return littleToBig(r.readInt());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(r);
        }
        return -1;
    }

    private static boolean writeInt(File archive, int offset, int value) {
        RandomAccessFile r = null;
        try {
            r = new RandomAccessFile(archive, "rwd");
            r.seek(offset);
            r.writeInt(bigToLittle(value));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(r);
        }
        return false;
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
            button.setEnabled(!s.toString().equals(button.getTag()));
        }
    }
}
