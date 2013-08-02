/**************************************************************************************
Copyright (C) 2013 Smita Kundargi and Jeanne Betcy Victor

This program is free software: you can redistribute it and/or modify it under 
the terms of the GNU General Public License as published by the Free Software Foundation, 
either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. 
If not, see http://www.gnu.org/licenses/.

Author - Smita Kundargi and Jeanne Betcy Victor
email: ksmita@pdx.edu and jbv3@pdx.edu

 ******************************************************************************************/
package com.example.savedollars;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;

import android.content.Intent;
import android.content.SharedPreferences;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import android.app.AlertDialog; //Dialog Box
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

//import com.example.adapter.ListViewAdapter;

public class MainActivity extends Activity implements OnClickListener {

	private SharedPreferences savedSearches;
	private EditText queryEditText;
	private Button searchButton;
	private ImageButton scanImageButton;
	private ImageButton snapImageButton;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//savedSearches = getSharedPreferences("searches", MODE_PRIVATE);

		queryEditText = (EditText) findViewById(R.id.queryEditText);
		searchButton = (Button) findViewById(R.id.searchButton);
		scanImageButton = (ImageButton) findViewById(R.id.scanImageButton);
		//snapImageButton = (ImageButton) findViewById(R.id.snapImageButton);

		searchButton.setOnClickListener(this);
		scanImageButton.setOnClickListener(this);
		//snapImageButton.setOnClickListener(this);

	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		System.out.println("<BETS> onActivityResult ");

		IntentResult scanResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		if (scanResult != null) {
			// we have a result
			String scanContent = scanResult.getContents();
			String scanFormat = scanResult.getFormatName();

			System.out.println(" BARCODE contents :" + scanContent);
			queryEditText.setText(scanContent);
			System.out.println(" BARCODE format :" + scanFormat);
			Intent searchIntent = new Intent(MainActivity.this,
					ProductTotalPriceDisplay.class);
			searchIntent.putExtra("barcodeNumber", (scanContent));
			startActivity(searchIntent);

		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"No scan data received!", Toast.LENGTH_SHORT);
			toast.show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.searchButton:
			if (queryEditText.getText().length() > 0) {
				Intent searchIntent = new Intent(MainActivity.this,
						ProductTotalPriceDisplay.class);

			//	String search = "691464717759";// "9780321673350";//"9780596527679";//691464717759";//remove
												// it after testing

				 searchIntent.putExtra("barcodeNumber", (queryEditText.getText()).toString());//Smita
				//searchIntent.putExtra("barcodeNumber", search);// remove it
																// after testing

				startActivity(searchIntent);
			} else {
				System.out.println("Provid, valid Barcode!");
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						MainActivity.this);
				dialog.setTitle(R.string.missingInputTitle);
				dialog.setPositiveButton(R.string.OK, null);
				dialog.setMessage(R.string.missingInput);
				AlertDialog warningDialog = dialog.create();
				warningDialog.show();
			}
			break;
		case R.id.scanImageButton:
			System.out.println("Scanning Barcode");
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			scanIntegrator.initiateScan();
			break;


		}
	}
}
