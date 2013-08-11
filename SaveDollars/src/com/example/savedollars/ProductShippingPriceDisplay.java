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


Author - Smita Kundargi and Jeanne Betcy Victor
email: ksmita@pdx.edu and jbv3@pdx.edu

 ******************************************************************************************/

package com.example.savedollars;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.adapter.ListViewAdapter;

/*******************************************************************************************************
** ProductShippingPriceDisplay is used to parse the JSON Data which is retrieved calling the google 
** shopping API to sort and display the shipping price of the product for different merchants and it 
** allows the user to visit the home page of the merchant by one click.
*********************************************************************************************************/

public class ProductShippingPriceDisplay extends ListActivity {

	private String JSONData = "";
	private Map merchantMap = new HashMap();
	private Map<Object, Object> sortedMap = new LinkedHashMap<Object, Object>();
	private int totalCount = 0;
	private String[][] PDT_INFO;
	public String pdtName;
	public String merchantPage;
	private Map merchantLinkMap = new HashMap();
	private static final String LOG_TAG = "ProductShippingPriceDisplay";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());

		JSONData = (getIntent().getStringExtra("JsonData"));
		if (JSONData != null) {
			parseJsonData(JSONData);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pdtshippingpriceview);

		// Setting Product Name
		TextView productName = (TextView) findViewById(R.id.pdtNameTextView);
		productName.setText(pdtName);

		Iterator objMapIterator = sortedMap.entrySet().iterator();
		int rowIndex = 0;

		PDT_INFO = new String[totalCount][2];
		while (objMapIterator.hasNext()) {
			Map.Entry keyValuePairs = (Map.Entry) objMapIterator.next();
			String key = ProductTotalPriceDisplay.merchantNames[rowIndex];

			PDT_INFO[rowIndex][0] = key;
			PDT_INFO[rowIndex][1] = "$" + String.valueOf(sortedMap.get(key));
			rowIndex++;
		}

		ListViewAdapter listv = new ListViewAdapter(this, PDT_INFO);

		setListAdapter(listv);

		final ListView lv = getListView();

		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				lv.getItemAtPosition(position);
				String pdtKey = PDT_INFO[position][0];
				String merchantLink = (String) merchantLinkMap.get(pdtKey);

				Uri uri = Uri.parse(merchantLink);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);

			}
		});
	}

	/*
	 * Method : parseJsonData Argument : String Description: parses JSON data
	 * for shipping price of product Returns : void
	 */

	private void parseJsonData(String data) {
		// Convert to JSON object for parsing
		try {
			JSONObject jsonResponse = new JSONObject(data);
			// Get the names
			// JSONArray arr = jsonResponse.names();

			if (jsonResponse.has("items")) {
				JSONArray parsedItems = jsonResponse.getJSONArray("items");
				JSONObject inventory = null;

				for (int j = 0; j < parsedItems.length(); j++) {

					inventory = parsedItems.getJSONObject(j);

					JSONObject objPrice = inventory.getJSONObject("product");
					JSONObject merchant = objPrice.getJSONObject("author");
					String merchantName = merchant.getString("name");
					JSONArray invObj = objPrice.getJSONArray("inventories");

					for (int z = 0; z < invObj.length(); z++) {
						JSONObject price = invObj.getJSONObject(z);

						String shipping = "0.0";
						if (price.has("shipping")) {
							shipping = price.getString("shipping");
						}
						float finalPrice = Float.parseFloat(shipping);
						merchantMap.put(merchantName, finalPrice);

					}
					pdtName = objPrice.getString("title");

					// retrieve merchant page
					merchantPage = objPrice.getString("link");

					merchantLinkMap.put(merchantName, merchantPage);
				}

				sortMerchantPrices();

			}
		} catch (JSONException e) {
			Log.e(LOG_TAG, "Cannot process JSON results", e);
			}

	}

	/*
	 * method : sortMerchantPrices arguments : none description: Sorts a hashmap
	 * containing merchant names and prices by values ( prices ). The idea is to
	 * convert map to list and then sort before converting back to map again.
	 * returns : void
	 */
	private void sortMerchantPrices() {

		List objList = new LinkedList(merchantMap.entrySet());

		Collections.sort(objList, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		for (Iterator it = objList.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
			totalCount++;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/* Code for the buttons */

	public void pdttotalpriceview(View v) {

		Intent searchIntent = new Intent(ProductShippingPriceDisplay.this,
				ProductTotalPriceDisplay.class);
		searchIntent.putExtra("JsonData", JSONData);
		startActivity(searchIntent);
	}

	public void pdtpriceview(View v) {
		Intent searchIntent = new Intent(ProductShippingPriceDisplay.this,
				ProductPriceDisplay.class);
		searchIntent.putExtra("JsonData", JSONData);
		startActivity(searchIntent);
	}

	public void pdtshippingpriceview(View v) {
		Intent searchIntent = new Intent(ProductShippingPriceDisplay.this,
				ProductShippingPriceDisplay.class);
		searchIntent.putExtra("JsonData", JSONData);
		startActivity(searchIntent);
	}

	public void pdtstockview(View v) {
		Intent searchIntent = new Intent(ProductShippingPriceDisplay.this,
				ProductStockDisplay.class);
		searchIntent.putExtra("JsonData", JSONData);
		startActivity(searchIntent);
	}

	public void activity_main(View v) {
		Intent searchIntent = new Intent(ProductShippingPriceDisplay.this,
				MainActivity.class);
		startActivity(searchIntent);
	}

}