AugmentedApp
============

Android App for Augmented Reality 

Requirements 
==============

JDK 6 or higher version should be install on the linux/windows machine. Android SDK should be configure to fork and make changes to the project. 

Dependency
==============

Android project has a dependency of QR Code scanner. The zing library has been used to correctly scan QR codes and retrieve data from them. More can be read from url:

https://github.com/zxing/zxing/wiki/Scanning-Via-Intent

It is easy to integerate Zing QR scanner. Under your android project {SRC} folder, create a package {com.google.zxing.integration.android}. Add two files under this package:

```
IntentIntegrator.java
IntentResult.java
```

To Launch the QR code scanner, we have to call:

```
IntentIntegrator scanIntegrator = new IntentIntegrator({Application or Activity context});
scanIntegrator.initiateScan();
```

then result of scanning can be retrieved in OnActivityResult call of an Activity:

```
@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) { 
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanningResult != null) {

			// we have a result
			String scanContent = scanningResult.getContents();
			String scanFormat = scanningResult.getFormatName();
 
		} else {
			Toast toast = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

```


QR Code Generator{Sample}
=====================
App testing can be performed using a sample QR code generator

http://www.barcodesinc.com/generator/qr/?chl=QRCodeSample5&chs=200x200&cht=qr&chld=H%7C0

License
==============

AugmentedApp is released under the **MIT license**. Checkout MIT license for more information. 

Contact me
==============

**Maninder Pal Singh**

