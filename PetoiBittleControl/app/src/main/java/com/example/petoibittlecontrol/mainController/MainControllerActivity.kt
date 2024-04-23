package com.example.petoibittlecontrol.mainController

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.petoibittlecontrol.R
import com.example.petoibittlecontrol.databinding.ActivityMainControllerBinding
import com.example.petoibittlecontrol.util.isScanPermissionGranted
import com.example.petoibittlecontrol.util.requestScanPermission
import com.polidea.rxandroidble3.LogConstants
import com.polidea.rxandroidble3.LogOptions
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.exceptions.BleScanException
import com.polidea.rxandroidble3.scan.ScanFilter
import com.polidea.rxandroidble3.scan.ScanSettings
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

class MainControllerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainControllerBinding
    private val viewModel : MainControllerViewModel = MainControllerViewModel()

    companion object {
        lateinit var rxBleClient: RxBleClient
            private set
    }

    private var scanDisposable: Disposable? = null

    /*private val resultsAdapter =
        ScanResultsAdapter { startActivity(DeviceActivity.newInstance(this, it.bleDevice.macAddress)) }
*/
    private var hasClickedScan = false

    private val isScanning: Boolean
        get() = scanDisposable != null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initBLEClient()

        binding.scanToggleBtn.setOnClickListener { onScanToggleClick() }
    }

    private fun initBLEClient(){
        rxBleClient = RxBleClient.create(this)
        RxBleClient.updateLogOptions(
            LogOptions.Builder()
            .setLogLevel(LogConstants.INFO)
            .setMacAddressLogSetting(LogConstants.MAC_ADDRESS_FULL)
            .setUuidsLogSetting(LogConstants.UUIDS_FULL)
            .setShouldLogAttributeValues(true)
            .build()
        )
    }



    private fun updateButtonUIState() =
        binding.scanToggleBtn.setText(if (isScanning) R.string.stop_scan else R.string.start_scan)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (isScanPermissionGranted(requestCode, grantResults) && hasClickedScan) {
            hasClickedScan = false
            scanBleDevices()
        }
    }

    private fun initBinding(){
    binding = DataBindingUtil.setContentView(this,R.layout.activity_main_controller)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun onScanToggleClick() {
        if (isScanning) {
            scanDisposable?.dispose()
        } else {
            if (rxBleClient.isScanRuntimePermissionGranted) {
                scanBleDevices()
            } else {
                hasClickedScan = true
                requestScanPermission(rxBleClient)
            }
        }
        updateButtonUIState()
    }

    private fun scanBleDevices() {
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .build()

        val scanFilter = ScanFilter.Builder()
//            .setDeviceAddress("B4:99:4C:34:DC:8B")
            // add custom filters if needed
            .build()

        rxBleClient.scanBleDevices(scanSettings, scanFilter)
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { dispose() }
            .subscribe({
                println("Scan result: $it")
                //resultsAdapter.addScanResult(it)
                    }, { onScanFailure(it) })

            .let { scanDisposable = it }
    }

    private fun onScanFailure(throwable: Throwable) {
        Log.w("ScanActivity", "Scan failed", throwable)
    }



    private fun dispose() {
        scanDisposable = null
        //resultsAdapter.clearScanResults()
        updateButtonUIState()
    }

}