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

package com.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.savedollars.R;

/*******************************************************************************************************
** ListViewAdapter is used define the listview with two textviews each accommodating the display of 
** merchant name and merchant(total price,price,stock or ShippingCost).
*********************************************************************************************************/
public class ListViewAdapter extends ArrayAdapter<String[]> {
private final Context context;
private final String[][] values;

public ListViewAdapter(Context context, String[][] values) {
    super(context, R.layout.listviewdisplay, values);
    this.context = context;
    this.values = values;
}

@Override
public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowDisplay = inflater.inflate(R.layout.listviewdisplay, parent, false);
    TextView merchantName = (TextView) rowDisplay.findViewById(R.id.merchantName);
    TextView merchantPrice = (TextView) rowDisplay.findViewById(R.id.merchantPrice);

    
    merchantName.setText(values[position][0]);
    merchantPrice.setText(values[position][1]);

    return rowDisplay;
}
}