package com.example.kumar_000.myapplication_akash;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
//import com.samsung.android.sdk.healthdata.HealthDataObserver;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
//sample comment
    private HealthDataStore mStore;
    private HealthConnectionErrorResult mConnError;
    Set<HealthPermissionManager.PermissionKey> mPermissionkeySet = new HashSet<HealthPermissionManager.PermissionKey>();
    public static final String TAG = "Shyam_App";
    private static MainActivity mInstance = null;
    @Override
    public  void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onConnected at 31");
        super.onCreate(savedInstanceState);
        mInstance = this;
        setContentView(R.layout.activity_main);

        HealthDataService healthDataService = new HealthDataService();
        try {
            healthDataService.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Create a HealthDataStore instance and set its listener
        mStore = new HealthDataStore(this, mConnectionListener);

        /*mPermissionkeySet.add(new HealthPermissionManager.PermissionKey(HealthConstants.BloodPressure.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.READ));
        mPermissionkeySet.add(new HealthPermissionManager.PermissionKey(HealthConstants.BloodPressure.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.WRITE));
        mPermissionkeySet.add(new HealthPermissionManager.PermissionKey(HealthConstants.BloodPressure.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.READ));
        mPermissionkeySet.add(new HealthPermissionManager.PermissionKey(HealthConstants.BloodPressure.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.WRITE));*/

        mPermissionkeySet.add(new HealthPermissionManager.PermissionKey(HealthConstants.FoodIntake.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.READ));
        mPermissionkeySet.add(new HealthPermissionManager.PermissionKey(HealthConstants.FoodIntake.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.WRITE));
        mPermissionkeySet.add(new HealthPermissionManager.PermissionKey(HealthConstants.FoodInfo.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.READ));
        mPermissionkeySet.add(new HealthPermissionManager.PermissionKey(HealthConstants.FoodInfo.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.WRITE));
        // Request the connection to the health data store
        mStore.connectService();
        Log.d(TAG, "onConnected at 51");
    }

    private void showPermissionAlarmDialog() {
        if (isFinishing()) {
            return;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Notice");
        alert.setMessage("All permissions should be acquired");
        alert.setPositiveButton("OK", null);
        alert.show();
    }
    private final HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult> mPermissionListener = new
            HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult>() {

        @Override
        public void onResult(HealthPermissionManager.PermissionResult result) {
            Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = result.getResultMap();
            // Show a permission alarm and initializes the calories if permissions are not acquired
            if (resultMap.values().contains(Boolean.FALSE)) {
                showPermissionAlarmDialog();
            } else {
                Log.d(TAG, "In Else block");
                // Get the calories of Indexed time and display it
                //pavan:mDataHelper.readDailyIntakeCalories(MainActivity.this, mDayStartTime);
                // Register an observer to listen changes of the calories
               // pavan:HealthDataObserver.addObserver(mStore, HealthConstants.FoodIntake.HEALTH_DATA_TYPE, mObserver);
            }
        }
    };
    private void requestPermissions() {
        HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);
        try {
            // Show user permission UI for allowing user to change options
            pmsManager.requestPermissions(mPermissionkeySet, MainActivity.this).setResultListener(mPermissionListener);
        } catch (Exception e) {
            Log.e(TAG, e.getClass().getName() + " - " + e.getMessage());
            Log.e(TAG, "Permission setting fails.");
        }
    }

    private final HealthDataStore.ConnectionListener mConnectionListener = new HealthDataStore.ConnectionListener() {
        @Override
        public void onConnected() {
            Log.d(TAG, "onConnected");
            HealthPermissionManager mPmsManager = new HealthPermissionManager(mStore);
            Map<HealthPermissionManager.PermissionKey, Boolean> mPermissionMap = mPmsManager.isPermissionAcquired(mPermissionkeySet);
            // Check the permissions acquired or not
            if (mPermissionMap.containsValue(Boolean.FALSE)) {
                requestPermissions();
            }
            //mDataHelper.readDailyIntakeCalories(MainActivity.this, mDayStartTime);
        }

        @Override
        public void onConnectionFailed(HealthConnectionErrorResult error) {
            Log.d(TAG, "onConnectionFailed");
            showConnectionFailureDialog(error);
        }

        @Override
        public void onDisconnected() {
            Log.d(TAG, "onDisconnected");
        }
    };

    private void showConnectionFailureDialog(HealthConnectionErrorResult error) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        mConnError = error;
        String message = "Connection with S Health is not available";

        if (mConnError.hasResolution()) {
            switch(error.getErrorCode()) {
                case HealthConnectionErrorResult.PLATFORM_NOT_INSTALLED:
                    message = "Please install S Health";
                    break;
                case HealthConnectionErrorResult.OLD_VERSION_PLATFORM:
                    message = "Please upgrade S Health";
                    break;
                case HealthConnectionErrorResult.PLATFORM_DISABLED:
                    message = "Please enable S Health";
                    break;
                case HealthConnectionErrorResult.USER_AGREEMENT_NEEDED:
                    message = "Please agree with S Health policy";
                    break;
                default:
                    message = "Please make S Health available";
                    break;
            }
        }

        alert.setMessage(message);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (mConnError.hasResolution()) {
                    mConnError.resolve(mInstance);
                }
            }
        });

        if (error.hasResolution()) {
            alert.setNegativeButton("Cancel", null);
        }

        alert.show();
    }

}
