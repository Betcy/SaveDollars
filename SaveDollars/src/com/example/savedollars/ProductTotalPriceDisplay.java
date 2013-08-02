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

import java.io.BufferedInputStream;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


import com.example.adapter.ListViewAdapter;

public class ProductTotalPriceDisplay extends ListActivity {

	private List<Float> sortedList = new ArrayList<Float>();
	private Map merchantMap = new HashMap();
	private Map<Object, Object> sortedMap = new LinkedHashMap<Object, Object>();

	private String JSONData = "";
	
	private int totalCount = 0;
	
	private String[][] PDT_INFO ;// = 
 	//private String[][] PDT_INFO = new String{};
//private String[][] PDT_INFO = new String[20][20];            
            
	//smita - 31st july
	public Set<Object> merchantNameKeys;
	public static String [] merchantNames;
	
	public String pdtName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
	
		System.out.println("Inside Product price display class");
		String barcodeNumber = (getIntent().getStringExtra("barcodeNumber")); 
		
		/* If barcode number is scanned from main screen, Google API server
		 * call is made to retrieve the product information. If we are 
		 * returning from existing view ( shipping, stocks) no server call
		 * is made. We display the product information already stored in 
		 * JSONData
		 */
		System.out.println("Barcode number is " + barcodeNumber);
		if(barcodeNumber != null)
		{
		   getProductDetails(barcodeNumber);
		}
		else
		{
			JSONData = (getIntent().getStringExtra("JsonData"));
			System.out.println("Inside product price , JSON Data is " + JSONData);
			parseJsonData(JSONData);
		}

		
		super.onCreate(savedInstanceState);
		System.out.println("Invoking pdttotalpriceview");
		setContentView(R.layout.pdttotalpriceview);
		
		//Setting Product Name
		TextView productName = (TextView) findViewById(R.id.pdtNameTextView);
		productName.setText(pdtName);
		
		Iterator objMapIterator = sortedMap.entrySet().iterator();

		int i = 0;
		System.out.println("3 Updating INFO totalCount:"+totalCount);

		PDT_INFO = new String[totalCount][2];
		while (objMapIterator.hasNext()) {
			Map.Entry keyValuePairs = (Map.Entry) objMapIterator.next();
			System.out.println("i = " + i);
			System.out.println("Save DDollars Merchant Name"
					+ String.valueOf(keyValuePairs.getKey()));
			System.out.println("Save DDollars Merchant Price :"
					+ String.valueOf(keyValuePairs.getValue()));
			PDT_INFO[i][0] = String.valueOf(keyValuePairs.getKey());
			PDT_INFO[i][1] = "$" + String.valueOf(keyValuePairs.getValue());
			System.out.println("While Array Merchant Name:" + PDT_INFO[i][0]
					+ "Array Merchant Price:" + PDT_INFO[i][1]);
			i++;
		}
		System.out.println("Total Updated Rows: " + i + " < totalCount:"
				+ totalCount);

		ListViewAdapter listv = new ListViewAdapter(this, PDT_INFO);

		setListAdapter(listv);
		System.out.println("INFO Updated leter");

	}

	private void getProductDetails(String barcodeNumber) {
		// Smita

		String baseURL = getString(R.string.searchURL);
		String key = getString(R.string.key);
		String country = getString(R.string.country);
		String urlString = baseURL + "&" + key + "&" + country + "&" + "q="
				+ barcodeNumber;

		try {
			URL url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			InputStream in = new BufferedInputStream(
					urlConnection.getInputStream());
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			StringBuilder out = new StringBuilder();
			String line;
			String data;
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
			// JSON data stored as string.
			data = out.toString();
			System.out.println("Bets: JSON DATA :" + data); // product
															// information from
															// google API in
			
			parseJsonData(data);

		} 		
		
		catch (MalformedURLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void parseJsonData(String data)
	{
		   //Convert to JSON object for parsing
		JSONData = data;
       try
       {
		   JSONObject jsonResponse = new JSONObject(data);
		   //Get the names 
		  // JSONArray arr = jsonResponse.names();
		 		   
		   JSONArray parsedItems = jsonResponse.getJSONArray("items");
		   JSONObject inventory = null;
		   //JSONObject inventory = parsedItems.getJSONObject("inventories");
		   
		   
		   for(int j=0;j<parsedItems.length();j++)
		   {
			   
			   inventory = parsedItems.getJSONObject(j);
			      
				JSONObject objPrice = inventory.getJSONObject("product");
				
				JSONObject merchant = objPrice.getJSONObject("author");
				String merchantName = merchant.getString("name");
				System.out.println("MERCHANT NAME = " + merchantName);
				//JSONArray merchantArray = merchant.getJSONArray("name");
				JSONArray invObj = objPrice.getJSONArray("inventories");
				System.out.println("merchant  array length is : " + merchant.toString());
				System.out.println("invObj length is : " + invObj.length());

				
				
				for(int z=0;z<invObj.length();z++)
				   {
					   JSONObject price = invObj.getJSONObject(z);
					   System.out.println(" Json object price is: " + price.toString());
					   String productPrice = price.getString("price");
					   String shipping = price.getString("shipping");
					   float finalPrice = Float.parseFloat(productPrice) + Float.parseFloat(shipping);
					   System.out.println("Final price is " + finalPrice);
					   merchantMap.put(merchantName, finalPrice);
					   sortedList.add(Float.valueOf(finalPrice));					   
				   }
			   
				//Bets Adding
				
				JSONArray imgObj = objPrice.getJSONArray("images");
				
				for (int i = 0; i < imgObj.length(); i++) {
					JSONObject imgLink = imgObj.getJSONObject(i);
					System.out.println("<BETS> Json imgLink is: "
							+ imgLink.toString());
					String img = imgLink.getString("link");

					System.out.println("<BETS> img Link : " + img);
				}
				
				pdtName = objPrice.getString("title");
				System.out.println("<BETS> Pdt NAME :"+pdtName);
		   }
		   
		   Collections.sort(sortedList);
		   
		   System.out.println("Lowest price is " + sortedList.get(0));
		   sortMerchantPrices();
		   merchantNameKeys = sortedMap.keySet();
		   
		   merchantNames = Arrays.copyOf(merchantNameKeys.toArray(), 
					merchantNameKeys.toArray().length, String[].class);
		   
		   
		   
       }
       catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			System.out.println("sortMerchantPrices totalCount:" + totalCount);
		}
		System.out.println("sortMerchantPrices final totalCount:" + totalCount);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	/* Code for the buttons */
	
	public void pdttotalpriceview(View v)
	{
		System.out.println("At Total Price button");
		Intent searchIntent = new Intent(ProductTotalPriceDisplay.this,
				ProductTotalPriceDisplay.class);
		searchIntent.putExtra("JsonData", JSONData);
		startActivity(searchIntent);
	}
	
	public void pdtpriceview(View v)
	{
		System.out.println("At price button");
		Intent searchIntent = new Intent(ProductTotalPriceDisplay.this,
				ProductPriceDisplay.class);
		searchIntent.putExtra("JsonData", JSONData);
		startActivity(searchIntent);
	}

	public void pdtshippingpriceview(View v)
	{
		System.out.println("At shipping button");
		Intent searchIntent = new Intent(ProductTotalPriceDisplay.this,
				ProductShippingPriceDisplay.class);
		searchIntent.putExtra("JsonData", JSONData);
		//searchIntent.putExtra("merchantNames", );
		
		startActivity(searchIntent);
	}
	
	public void pdtstockview(View v)
	{
		System.out.println("At stock button");
		Intent searchIntent = new Intent(ProductTotalPriceDisplay.this,
				ProductStockDisplay.class);
		searchIntent.putExtra("JsonData", JSONData);
		startActivity(searchIntent);
	}
	

	public void activity_main(View v){
		System.out.println("At main menu button");
		Intent searchIntent = new Intent(ProductTotalPriceDisplay.this,
				MainActivity.class);
		startActivity(searchIntent);
		}


	/*
	 * public void onClick(View v) { switch (v.getId()) { case R.id.menuButton:
	 * System.out.println("At main menu button"); Intent mainMenuIntent = new
	 * Intent(ProductPriceDisplay.this, MainActivity.class);
	 * startActivity(mainMenuIntent);
	 * 
	 * break; } }
	 */

}
