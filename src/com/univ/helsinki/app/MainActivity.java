package com.univ.helsinki.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.univ.helsinki.app.activities.RecentActivity;
import com.univ.helsinki.app.db.RecentActivityDataSource;

public class MainActivity extends Activity {

	private EditText feildTitle;
	private EditText feildContent;
	private Button btnScan;
	private Button btnRecent;

	private RecentActivityDataSource mDatasource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		feildTitle = (EditText) findViewById(R.id.feildTitle);
		feildContent = (EditText) findViewById(R.id.feildContent);
		btnScan = (Button) findViewById(R.id.btnScan);
		btnRecent = (Button) findViewById(R.id.btnRecent);

		btnScan.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				IntentIntegrator scanIntegrator = new IntentIntegrator(
						MainActivity.this);
				scanIntegrator.initiateScan();
			}
		});

		btnRecent.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MainActivity.this,
						RecentActivity.class));
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		// TODO Auto-generated method stub
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		if (scanningResult != null) {

			// we have a result
			String scanContent = scanningResult.getContents();
			String scanFormat = scanningResult.getFormatName();

			if ((scanFormat != null) && scanFormat.trim().length() > 0) {
				feildTitle.setText("FORMAT: " + scanFormat);
				feildContent.setText("CONTENT: " + scanContent);

				mDatasource = new RecentActivityDataSource(this);
				mDatasource.open();

				mDatasource.createFeed(scanFormat, scanContent);
			}else{
				feildTitle.setText("");
				feildContent.setText("");
			}
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"No scan data received!", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	@Override
	protected void onPause() {
		if (mDatasource != null)
			mDatasource.close();
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
