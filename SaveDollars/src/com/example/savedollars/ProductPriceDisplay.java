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

import java.util.ArrayList;
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

public class ProductPriceDisplay extends ListActivity {
	
	private String JSONData = "";
	private Map merchantMap = new HashMap();
	private Map<Object,Object> sortedMap = new LinkedHashMap<Object,Object>();
	private int totalCount = 0;	
	private String[][] PDT_INFO ;
	public String pdtName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectDiskReads()
        .detectDiskWrites()
        .detectNetwork()
        .penaltyLog()
        .build());
       
	    JSONData = (getIntent().getStringExtra("JsonData")); 
	    System.out.println("Json data in price page: " + JSONData);
	    if(JSONData != null)
		{
			parseJsonData(JSONData);
		}
		super.onCreate(savedInstanceState);
		System.out.println("after on create");

		setContentView(R.layout.pdtpriceview);
		

		//Setting Product Name
		TextView productName = (TextView) findViewById(R.id.pdtNameTextView);
		productName.setText(pdtName);
		
		Iterator objMapIterator = sortedMap.entrySet().iterator();
		int i = 0;
		System.out.println("3 Updating INFO totalCount:"+totalCount);

		PDT_INFO = new String[totalCount][2];
		while (objMapIterator.hasNext()) {
			Map.Entry keyValuePairs = (Map.Entry) objMapIterator.next();
			String key = ProductTotalPriceDisplay.merchantNames[i];
			
			
			PDT_INFO[i][0] = key;
			PDT_INFO[i][1] = "$" + sortedMap.get(key);
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

	/*
	 * Method : parseJsonData
	 * Argument : String
	 * Description: parses JSON data for shipping price of product
	 * Returns : void
	 */
	
	private void parseJsonData(String data)
	{
		   //Convert to JSON object for parsing
       try
       {
		   JSONObject jsonResponse = new JSONObject(data);
		   //Get the names 
		   JSONArray arr = jsonResponse.names();
		   
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
					   String productPrice = price.getString("price");
					   float finalPrice = Float.parseFloat(productPrice);					   
					   merchantMap.put(merchantName, finalPrice);
					   
				   }
			   
				pdtName = objPrice.getString("title");
				System.out.println("<BETS> Pdt NAME :"+pdtName);
		   }
		   
		   System.out.println("out of for loop");
		   sortMerchantPrices();
		   System.out.println("Sorting done");

		   
       }
       catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/*
	 * method : sortMerchantPrices
	 * arguments : none
	 * description: Sorts a hashmap containing merchant names and prices
	 *              by values ( prices ). The idea is to convert map
	 *              to list and then sort before converting back to map
	 *              again.
	 * returns : void 
	 */
	private void sortMerchantPrices() {
		
		List objList = new LinkedList(merchantMap.entrySet());
		
		Collections.sort(objList, new Comparator() {
			public int compare(Object obj1, Object obj2) {
				return ((Comparable) ((Map.Entry) (obj1)).getValue())
                                       .compareTo(((Map.Entry) (obj2)).getValue());
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
	
	public void pdttotalpriceview(View v)
	{
		System.out.println("At Total Price button");
		Intent searchIntent = new Intent(ProductPriceDisplay.this,
				ProductTotalPriceDisplay.class);
		searchIntent.putExtra("JsonData", JSONData);
		startActivity(searchIntent);
	}
	
	public void pdtpriceview(View v)
	{
		System.out.println("At price button");
		Intent searchIntent = new Intent(ProductPriceDisplay.this,
				ProductPriceDisplay.class);
		searchIntent.putExtra("JsonData", JSONData);
		startActivity(searchIntent);
	}

	public void pdtshippingpriceview(View v)
	{
		System.out.println("At shipping button");
		Intent searchIntent = new Intent(ProductPriceDisplay.this,
				ProductShippingPriceDisplay.class);
		searchIntent.putExtra("JsonData", JSONData);
		startActivity(searchIntent);
	}
	
	public void pdtstockview(View v)
	{
		System.out.println("At stock button");
		Intent searchIntent = new Intent(ProductPriceDisplay.this,
				ProductStockDisplay.class);
		searchIntent.putExtra("JsonData", JSONData);
		startActivity(searchIntent);
	}
	
	public void activity_main(View v){
		System.out.println("At main menu button");
		Intent searchIntent = new Intent(ProductPriceDisplay.this,
				MainActivity.class);
		startActivity(searchIntent);
		}
	




}