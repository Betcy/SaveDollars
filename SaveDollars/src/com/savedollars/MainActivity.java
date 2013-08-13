/**************************************************************************************
SaveDollars – An open source Android application that helps users to compare prices 
of a product across different ecommerce sites and make a decision about purchase.

Copyright (C) 2013 Smita Kundargi and Jeanne Betcy Victor

This program is free software: you can redistribute it and/or modify it under 
the terms of the GNU General Public License as published by the Free Software Foundation, 
either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. 
If not, see http://www.gnu.org/licenses/.

Following is the link for the repository: https://github.com/SmitaBetcy/SaveDollars

Please, see the file license in this distribution for license terms. Link is
https://github.com/SmitaBetcy/SaveDollars/blob/master/License

References:
https://developers.google.com/shopping-search/v1/reference-response-format
https://developers.google.com/shopping-search/v1/getting_started
https://code.google.com/p/zxing/wiki/ScanningViaIntent
http://stackoverflow.com/questions/8632529/listview-with-multiple-strings
http://sweetclipart.com/


Author - Smita Kundargi and Jeanne Betcy Victor
email: ksmita@pdx.edu and jbv3@pdx.edu

 ******************************************************************************************/

package com.savedollars;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.savedollars.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


/*******************************************************************************************************
** MainActivity is the main screen that provides users with options to either enter the barcode or 
** scan the barcode to retrieve product information.
*********************************************************************************************************/
public class MainActivity extends Activity implements OnClickListener {

	private EditText queryEditText;
	private Button searchButton;
	private ImageButton scanImageButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		queryEditText = (EditText) findViewById(R.id.queryEditText);
		searchButton = (Button) findViewById(R.id.searchButton);
		scanImageButton = (ImageButton) findViewById(R.id.scanImageButton);

		searchButton.setOnClickListener(this);
		scanImageButton.setOnClickListener(this);

	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		IntentResult scanResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		if (scanResult != null) {
			// we have a result
			String scanContent = scanResult.getContents();
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
				searchIntent.putExtra("barcodeNumber",
						(queryEditText.getText()).toString());
				startActivity(searchIntent);
			} else {
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
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			scanIntegrator.initiateScan();
			break;

		}
	}
}
