package com.example.petoibittlecontrol.util

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.polidea.rxandroidble3.RxBleClient

private const val REQUEST_PERMISSION_BLE_SCAN = 101

internal fun Activity.requestScanPermission(client: RxBleClient) =
    ActivityCompat.requestPermissions(
        this,
        /*
         * the below would cause a ArrayIndexOutOfBoundsException on API < 23. Yet it should not be called then as runtime
         * permissions are not needed and RxBleClient.isScanRuntimePermissionGranted() returns `true`
         */
        arrayOf(client.recommendedScanRuntimePermissions[0]),
        REQUEST_PERMISSION_BLE_SCAN
    )

internal fun isScanPermissionGranted(requestCode: Int, grantResults: IntArray) =
    requestCode == REQUEST_PERMISSION_BLE_SCAN && grantResults[0] == PackageManager.PERMISSION_GRANTED
