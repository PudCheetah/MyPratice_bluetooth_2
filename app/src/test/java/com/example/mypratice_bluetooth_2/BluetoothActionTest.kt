package com.example.mypratice_bluetooth_2

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class BluetoothActionTest {
    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var bluetoothAdapter: BluetoothAdapter

    @Mock
    private lateinit var intentLauncher: IntentLauncher

    private lateinit var bluetoothAction: BluetoothAction

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        bluetoothAction = BluetoothAction(context, bluetoothAdapter, intentLauncher)
    }
    @Test
    fun testEnableBluetooth() {
        bluetoothAction.enableBluetooth()
        verify(intentLauncher).activityIntent(any())
    }

    @Test
    fun testDisableBluetooth() {
        `when`(context.checkSelfPermission(anyString())).thenReturn(PackageManager.PERMISSION_GRANTED)
        bluetoothAction.disableBluetooth()
        verify(bluetoothAdapter).disable()
    }

    @Test
    fun testActivityScanning_whenPermissionGrantedAndNotDiscovering() {
        `when`(context.checkSelfPermission(anyString())).thenReturn(PackageManager.PERMISSION_GRANTED)
        `when`(bluetoothAdapter.isDiscovering).thenReturn(false)
        `when`(bluetoothAdapter.startDiscovery()).thenReturn(true)

        bluetoothAction.activityScanning()

        verify(bluetoothAdapter).startDiscovery()
    }

    @Test
    fun testInactivityScanning_whenPermissionGranted() {
        `when`(context.checkSelfPermission(anyString())).thenReturn(PackageManager.PERMISSION_GRANTED)

        bluetoothAction.inactivityScanning()

        verify(bluetoothAdapter).cancelDiscovery()
    }

    @Test
    fun testChangeBluetoothMode_discoverable() {
        bluetoothAction.changeBluetoothMode_discoverable()
        verify(intentLauncher).activityIntent(any())
    }
}