<b>|</b>&nbsp;<a href='#Why use BLE SDK?'>Why use BLE SDK?</a>
<b>|</b>&nbsp;<a href='#What BLE SDK does?'>What BLE SDK does?</a>
<b>|</b>&nbsp;<a href='#How to use BLE SDK?'>How to use BLE SDK?</a>
<b>|</b>&nbsp;<a href="#Wiki">Wiki</a>
<b>|</b>&nbsp;<a href='#License'>License</a>


<img src="https://img.gadgethacks.com/img/70/27/63574905944081/0/android-basics-connect-bluetooth-device.1280x600.jpg" width="512">


# Android-BLE-SDK

A library to make BLE easier to use in Android. Compatible with Android 4.3(API 18) & above .


<a name="Why use BLE SDK?"/>
## Why use BLE SDK?

1. Using Official APIs are not easy
https://developer.android.com/guide/topics/connectivity/bluetooth-le.html

2. A lot of issues and headaches:
https://github.com/iDevicesInc/SweetBlue/wiki/Android-BLE-Issues


<a name="What BLE SDK does?"/>
## What BLE SDK does?

The basic procedure for setting up a BLE connection is roughly as follows:

1. Scan devices advertising specific services
2. Connect to a device
3. Discover service(s)
4. Discover characteristic(s)
5. Register for characteristic notifications

To connect your central to a peripheral (steps 1 & 2) in order to e.g. read and write data only between the two devices, you start the connection process by scanning for peripherals that provide a specific service (or services) you require. In a viable situation where there are multiple peripherals advertising themselves in the vicinity of a central device, you may want to send customized identifier in the advertisement package (therefore fulfilling the broadcaster role, see above) so that the central can observe the sent identifiers and distinguish a particular peripheral from others.Steps 3 to 5 are covered by GATT, which comes into play when you have actually established a connection with a BLE peripheral. Once connected, you can scan the peripheral’s services and their related characteristics.

A BLE device profile consists of one or more services, each of which can have one or more characteristics. A common case is that there’s one service, which has one characteristic for reading, and one for writing. Services are basically a logical collection of read/write characteristics. Each service and characteristic is identified by a 16/128-bit unique identifier. The 16-bit identifiers are defined by Bluetooth SIG to ensure/encourage interoperability between BLE devices as needed, and 128-bit indentifiers are available for building customized services/characteristics. Each characteristic can also have descriptors that can describe the characteristic’s value, set minimum/maximum limits, or whatever you may need. Dealing with descriptors is normally not necessary, except in one particular case on Android that will be covered later.

You can read a characteristic’s current value, or register to get notified when the value changes. Notifications allow you to get updates whenever they happen, instead of polling the current value repeatedly. When writing to a characteristic, it’s possible to get confirmation of a successful write operation, assuming the characteristic has been configured to support this on the peripheral.



<a name="How to use BLE SDK?"/>
## How to use BLE SDK?

Bluetooth LE on Android is a bit of a wild west, especially when dealing with proprietary Bluetooth stack implementations, but here’s the general idea on how to get started using the public API. First you need to request both BLUETOOTH and BLUETOOTH_ADMIN permissions in your app’s manifest. High-level Bluetooth operations are done through the BluetoothAdapter instance, which is common to all apps on the system. You can get the instance through BluetoothManager‘s `getAdapter()` method.

Scanning peripherals requires that you have implemented the callback interface for getting scan results. In case you need to support API levels 18 to 20, call BluetoothAdapter‘s `startLeScan()` and supply an instance of BluetoothAdapter.LeScanCallback implementation. For API levels 21 onward, first call BluetoothAdapter's `getBluetoothLeScanner()` to get an instance of BluetoothLeScanner, and then call `startScan()` on the instance, supplying a ScanCallback where you can handle scan results. Also note that to get scan results on Lollipop (5.0) and newer you will need to declare `ACCESS_COARSE_LOCATION` or `ACCESS_FINE_LOCATION` permission in the manifest.

In your ScanCallback‘s `onScanResult()` you can get the peripheral from the supplied ScanResult by calling `getDevice()`, which returns a BluetoothDevice object. To initiate connection, call `connectGatt()` on the instance, giving a BluetoothGattCallback instance as argument.

Your BluetoothGattCallback‘s `onConnectionStateChanged()` is called, as the name implies, when the connected to or disconnected from the peripheral. Once you’ve established connection, call `discoverServices()` on the supplied BluetoothGatt instance. This will result in a call to `onServicesDiscovered()`, where you can set up read and write characteristics as needed.

You can get a BluetoothGattService instance by calling `getService()` on the supplied BluetoothGatt object. To register to notifications when a characteristic’s value changes on the peripheral, call `setCharacteristicNotification()` with true as argument on the BluetoothGattCharacteristic object you get by calling `getCharacteristic()` on the service instance. An important thing is to also remember to enable notifications on the Client Characteristic Configuration descriptor by calling `setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)` on the descriptor instance. You can get the descriptor by calling `getDescriptor()` on the BluetoothGattCharacteristic object, supplying the UUID of the descriptor (whose Bluetooth SIG assigned number is 0x2902. Incidentally, on iOS this is done automatically for you.)

When a characteristic’s value is changed on the peripheral and you’ve registered to get notifications for the characteristic, your BluetoothGattCallback‘s `onCharacteristicChanged()` is called by the framework, and you can then get the current value by calling `getValue()` on the supplied BluetoothGattCharacteristic object.

Writing to a writable characteristic is done by first setting the required data to the BluetoothGattCharacteristic object using one of the `setValue()` overloads, and then calling BluetoothLeGatt‘s `writeCharacteristic()` to send the data over to the peripheral. You can get results of the write operation to BluetoothGattCallback‘s `onCharacteristicWrite()` method if you’re interested in them. Bluetooth LE on Android is a bit of a wild west, especially when dealing with proprietary Bluetooth stack implementations, but here’s the general idea on how to get started using the public API. First you need to request both BLUETOOTH and BLUETOOTH_ADMIN permissions in your app’s manifest. High-level Bluetooth operations are done through the BluetoothAdapter instance, which is common to all apps on the system. You can get the instance through BluetoothManager‘s `getAdapter()` method.



<a name="Wiki"/>
## <a href="https://github.com/Mylittleswift/Android-BLE-SDK/wiki">Wiki</a>


<a name="License"/>
## License

    The MIT License (MIT)

    Copyright (c) 2015 LinkMob.cc

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
