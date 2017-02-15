package io.github.mylittleswift.sampleapp;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.youhong.oldhealthcare.blesdk.RFLampDevice;
import com.youhong.oldhealthcare.blesdk.Tools;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import no.nordicsemi.android.dfu.DfuBaseService;

public class OtaActivity extends Activity implements OnClickListener, LoaderCallbacks<Cursor> {
    private static final String EXTRA_URI = "uri";
    private BluetoothAdapter mAdapter;
    private String mFilePath;
    private Uri mFileStreamUri;
    private int mFileType;
    private int mFileTypeTmp = DfuBaseService.TYPE_APPLICATION;
    private Button bt_download;
    private Handler handler = new Handler();
    private Button bt_ota;
    private ProgressBar mProgressBar;
    private TextView mTextPercentage;
    private ProgressDialog dialog;
    private URL url;
    private Intent serviceIntent;

    private TextView mTvFile;
    private RFLampDevice device;

    protected String version;

    private List<String> mTaskList;

    /**
     * Constants
     **/
    private static final String MASTER_OTA_NAME = "j1622_";
    private static final String SLAVE_OTA_NAME = "j1622ui_";

    private static final String MASTER_OTA_BROADCAST_NAME = "ota_mcu";
    private static final String SLAVE_OTA_BROADCAST_NAME = "ota_ui";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
       /* setContentView(R.layout.ota);
        BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mAdapter = manager.getAdapter();
        IntentFilter filter = new IntentFilter();
        filter.addAction(DfuBaseService.BROADCAST_PROGRESS);
        filter.addAction(DfuBaseService.BROADCAST_ERROR);
        filter.addAction("onDescriptorWrite");
        filter.addAction(LightBLEService.ACTION_GATT_SERVICES_DISCOVERED);
        filter.addAction(LightBLEService.ACTION_GATT_DISCONNECTED);
        filter.addAction(LightBLEService.ACTION_DATA_AVAILABLE);
        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(mDfuUpdateReceiver, filter);
        registerReceiver(mDfuUpdateReceiver, filter);
        initView();
        if (!TextUtils.isEmpty(Tools.device.getName()) && Tools.device.getName().toLowerCase().contains("ota")) {
            bt_ota.setEnabled(true);
        } else {
            device = new RFLampDevice(this, Tools.device);
        }
*/
    }

    private void initView() {

     /*   dialog = new ProgressDialog(this);
        dialog.setTitle("Please wait.....");
        dialog.setCanceledOnTouchOutside(false);
        bt_ota = (Button) findViewById(R.id.bt_ota);
        bt_ota.setOnClickListener(this);
        bt_download = (Button) findViewById(R.id.bt_download);
        bt_download.setOnClickListener(this);
        if (Tools.OTAtype == 0) {
            bt_download.setVisibility(View.VISIBLE);
        } else {
            bt_download.setVisibility(View.GONE);
        }
        mTextPercentage = (TextView) findViewById(R.id.textviewProgress);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar_file);
        mTvFile = (TextView) findViewById(R.id.tv_selected);
        // if(device.deviceName.equals("DfuTarg")){
        // sendHex(device.deviceName,device.deviceMac);
        // }*/
    }

    @Override
    public void onClick(View arg0) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case SELECT_FILE_REQ:
                // clear previous data
                mFileType = mFileTypeTmp;
                mFilePath = null;
                mFileStreamUri = null;
                // and read new one
                final Uri uri = data.getData();
            /*
             * The URI returned from application may be in 'file' or 'content'
			 * schema. 'File' schema allows us to create a File object and read
			 * details from if directly. Data from 'Content' schema must be read
			 * by Content Provider. To do that we are using a Loader.
			 */

                if (uri.getScheme().equals("file")) {
                    // the direct path to the file has been returned

                    mFileStreamUri = uri;
                    final String path = uri.getPath();

                    mFilePath = path;
                    mTvFile.setText(path);


                    // updateFileInfo(file.getName(), file.length(), mFileType);
                } else if (uri.getScheme().equals("content")) {
                    // an Uri has been returned
                    mFileStreamUri = uri;
                    // if application return an Uri for streaming, let's use it. Does
                    // it works?
                    // FIXME both Uris works with Google Drive app. Why both? What's
                    // the difference? How about other apps like DropBox?
                    final Bundle extras = data.getExtras();
                    if (extras != null && extras.containsKey(Intent.EXTRA_STREAM))
                        mFileStreamUri = extras.getParcelable(Intent.EXTRA_STREAM);

                    // file name and size must be obtained from Content Provider
                    final Bundle bundle = new Bundle();
                    bundle.putParcelable(EXTRA_URI, uri);
                    getLoaderManager().restartLoader(0, bundle, this);
                }
                break;
            default:
                break;
        }
    }

    private static final int SELECT_FILE_REQ = 1;
    static final int REQUEST_ENABLE_BT = 2;

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.putExtra("explorer_title", getString(R.string.dialog_read_from_dir));
        intent.setDataAndType(Uri.fromFile(new File("/sdcard")), "*/*");
        intent.putExtra("filter_suffix", ".zip");
        /**
         * Testing
         *
         */
        Log.e("Testing!~~~~~~~~~~~", Uri.fromFile(new File("/sdcard")).toString());

        intent.setClass(this, FilterExDialog.class);
        startActivityForResult(intent, SELECT_FILE_REQ);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        final LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.unregisterReceiver(mDfuUpdateReceiver);
        unregisterReceiver(mDfuUpdateReceiver);
        if (device == null)
            return;
        device.disconnectedDevice();
    }

    private void startUpdate() {

        if (device == null)
            return;
        if (!TextUtils.isEmpty(Tools.device.getName()) && device.deviceName.toLowerCase().contains("dfu")) {
            sendHex(device.deviceName, device.deviceMac);
        } else {
            // device.sendUpdate();
        }
    }

    private final BroadcastReceiver mDfuUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            // DFU is in progress or an error occurred
           /* final String action = intent.getAction();

            if (DfuBaseService.BROADCAST_PROGRESS.equals(action)) {

                final int progress = intent.getIntExtra(DfuBaseService.EXTRA_DATA, 0);
                final int currentPart = intent.getIntExtra(DfuBaseService.EXTRA_PART_CURRENT, 1);
                final int totalParts = intent.getIntExtra(DfuBaseService.EXTRA_PARTS_TOTAL, 1);
                updateProgressBar(progress, currentPart, totalParts, false);

                if (progress == DfuBaseService.PROGRESS_COMPLETED) {
                    stopService(serviceIntent);
                    startScan(true, reconnect_callback);
                }

            } else if (DfuBaseService.BROADCAST_ERROR.equals(action)) {
                final int error = intent.getIntExtra(DfuBaseService.EXTRA_DATA, 0);
                bt_ota.setEnabled(true);
                Toast.makeText(OtaActivity.this, "error", Toast.LENGTH_SHORT).show();
                dialog.cancel();
                updateProgressBar(error, 0, 0, true);

                // We have to wait a bit before canceling notification. This is
                // called before DfuService creates the last notification.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // if this activity is still open and upload process was
                        // completed, cancel the notification
                        final NotificationManager manager = (NotificationManager) getSystemService(
                                Context.NOTIFICATION_SERVICE);
                        manager.cancel(DfuBaseService.NOTIFICATION_ID);
                    }
                }, 200);
            } else if ("onDescriptorWrite".equals(action)) {

                // checkUpDate();
            } else if (LightBLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                //startScan(true);

            } else if (LightBLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                TaskCheck();
                            }
                        }, 500);
                    }
                });

            } else if (LightBLEService.ACTION_DATA_AVAILABLE.equals(action)) {

            }*/
        }
    };

  /*  private void TaskCheck() {

        if (mTaskList == null || mTaskList.size() == 0) {
            bt_ota.setEnabled(true);
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
        final String path = mFileStreamUri.getPath();
        final String dirPath = path.substring(0, path.lastIndexOf("/"));
        if (mTaskList.get(0).contains(MASTER_OTA_NAME)) {


            UpdateMaster(dirPath);

        } else {

            UpdateSlave(dirPath);

        }

    }*/


    boolean isUpgrade = false;

    @Override
    public void onBackPressed() {

        if (isUpgrade) {


        } else {
            super.onBackPressed();

        }

    }


    private void updateProgressBar(final int progress, final int part, final int total, final boolean error) {
        switch (progress) {
            case DfuBaseService.PROGRESS_CONNECTING:
                dialog.cancel();
                mProgressBar.setVisibility(View.VISIBLE);
                mTextPercentage.setVisibility(View.VISIBLE);
                mProgressBar.setIndeterminate(true);
                mTextPercentage.setText(R.string.dfu_status_connecting);
                break;
            case DfuBaseService.PROGRESS_STARTING:
                mProgressBar.setIndeterminate(true);
                mTextPercentage.setText(R.string.dfu_status_starting);
                break;
            case DfuBaseService.PROGRESS_VALIDATING:
                mProgressBar.setIndeterminate(true);
                mTextPercentage.setText(R.string.dfu_status_validating);
                break;
            case DfuBaseService.PROGRESS_DISCONNECTING:
                mProgressBar.setIndeterminate(true);
                mTextPercentage.setText(R.string.dfu_status_disconnecting);
                break;
            case DfuBaseService.PROGRESS_COMPLETED:
                mTextPercentage.setText(R.string.dfu_status_completed);
                // let's wait a bit until we cancel the notification. When canceled
                // immediately it will be recreated by service again.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onTransferCompleted();

                        // if this activity is still open and upload process was
                        // completed, cancel the notification
                        final NotificationManager manager = (NotificationManager) getSystemService(
                                Context.NOTIFICATION_SERVICE);
                        manager.cancel(DfuBaseService.NOTIFICATION_ID);
                    }
                }, 200);
                break;
            case DfuBaseService.PROGRESS_ABORTED:
                mTextPercentage.setText(R.string.dfu_status_aborted);
                // let's wait a bit until we cancel the notification. When canceled
                // immediately it will be recreated by service again.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // if this activity is still open and upload process was
                        // completed, cancel the notification
                        final NotificationManager manager = (NotificationManager) getSystemService(
                                Context.NOTIFICATION_SERVICE);
                        manager.cancel(DfuBaseService.NOTIFICATION_ID);
                    }
                }, 200);
                break;
            default:
                mProgressBar.setIndeterminate(false);
                if (error) {
                    showErrorMessage(progress);
                } else {
                    mProgressBar.setProgress(progress);
                    mTextPercentage.setText(getString(R.string.progress, progress));
                    // if (total > 1)
                    // mTextUploading.setText(getString(R.string.dfu_status_uploading_part,
                    // part, total));
                    // else
                    // mTextUploading.setText(R.string.dfu_status_uploading);
                }
                break;
        }
    }

    private void showErrorMessage(int code) {
        // TODO Auto-generated method stub
  /*      showToast("Upload failed: " + GattError.parse(code) + " ("
                + (code & ~(DddService.ERROR_MASK | DddService.ERROR_REMOTE_MASK)) + ")");*/
    }

    private void showToast(final String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void checkUpDate() {
        // TODO Auto-generated method stub
        device.checkUpdate();
    }

    protected void onTransferCompleted() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mTextPercentage.setVisibility(View.INVISIBLE);
        bt_ota.setEnabled(true);
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
    }

    String mOtaName = null;
    String mZipFilePath = null;
    private LeScanCallback callback = new LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int arg1, byte[] arg2) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    String name = device.getName();
                    String address = device.getAddress();
                    if (name != null && name.toLowerCase().contains(mOtaName)) {
                        startScan(false, callback);
                        sendHex(name, address);
                    }
                }

            });
        }
    };


    private LeScanCallback reconnect_callback = new LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int arg1, byte[] arg2) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    String name = device.getName();
                    String address = device.getAddress();
                    if (address.equals(OtaActivity.this.device.deviceMac)) {
                        startScan(false, reconnect_callback);
                        OtaActivity.this.device = new RFLampDevice(OtaActivity.this, device);

                    }
                }

            });
        }
    };

    private void sendHex(String name, String address) {
        String file = mZipFilePath;
        String type = "";
   /*     if (Tools.OTAtype == 1) {
            mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "J038_0315_2.hex";
            // file = mFilePath;
        } else {
            // file = mZipFilePath;
        }*/
        type = file.substring(file.length() - 3);
        // mFilePath =
        // Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
        // "v0_6_4_b018_jstyle_limited.hex";
        //    serviceIntent = new Intent(this, DddService.class);
        // if(!new File(mFilePath).exists()){
        // Toast.makeText(this, "�����ļ�δ�ҵ�", Toast.LENGTH_SHORT).show();
        // }
        // findHexPath();

        serviceIntent.putExtra(DfuBaseService.EXTRA_DEVICE_NAME, name);
        serviceIntent.putExtra(DfuBaseService.EXTRA_DEVICE_ADDRESS, address);
        if (type.equals("zip")) {
            serviceIntent.putExtra(DfuBaseService.EXTRA_FILE_MIME_TYPE, DfuBaseService.MIME_TYPE_ZIP);
        } else {
            serviceIntent.putExtra(DfuBaseService.EXTRA_FILE_MIME_TYPE, DfuBaseService.MIME_TYPE_OCTET_STREAM);
        }

        serviceIntent.putExtra(DfuBaseService.EXTRA_FILE_PATH, file);
        serviceIntent.putExtra(DfuBaseService.EXTRA_FILE_TYPE, type);
        try {
            serviceIntent.putExtra(DfuBaseService.EXTRA_FILE_URI, new URI(file));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        startService(serviceIntent);
    }

    protected void startScan(boolean b, final LeScanCallback callback) {
        if (b) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() { // TODO Auto-generated method stub
                    mAdapter.stopLeScan(callback);
                }
            }, 20000);
            mAdapter.startLeScan(callback);
        } else {
            mAdapter.stopLeScan(callback);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        final Uri uri = arg1.getParcelable(EXTRA_URI);
        /*
         * Some apps, f.e. Google Drive allow to select file that is not on the
		 * device. There is no "_data" column handled by that provider. Let's
		 * try to obtain all columns and than check which columns are present.
		 */
        // final String[] projection = new String[] {
        // MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.SIZE,
        // MediaStore.MediaColumns.DATA };
        return new CursorLoader(this, uri,
                null /* all columns, instead of projection */, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
        if (data != null && data.moveToNext()) {
            /*
             * Here we have to check the column indexes by name as we have
			 * requested for all. The order may be different.
			 */
            final String fileName = data.getString(
                    data.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)/* 0 DISPLAY_NAME */);
            final int fileSize = data
                    .getInt(data.getColumnIndex(MediaStore.MediaColumns.SIZE) /* 1 SIZE */);
            String filePath = null;
            final int dataIndex = data.getColumnIndex(MediaStore.MediaColumns.DATA);
            if (dataIndex != -1)
                filePath = data.getString(dataIndex /* 2 DATA */);
            if (!TextUtils.isEmpty(filePath)) {
                mFilePath = filePath;
                mTvFile.setText(fileName);
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub

    }
}
