

package com.example.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.savedollars.R;


public class ListViewAdapter extends ArrayAdapter<String[]> {
private final Context context;
private final String[][] values;

public ListViewAdapter(Context context, String[][] values) {
    super(context, R.layout.listviewdisplay, values);
    this.context = context;
    this.values = values;
    System.out.println(" ListViewAdapter NEW");
}

@Override
public View getView(int position, View convertView, ViewGroup parent) {
	System.out.println(" BETS getView position:"+position);
	System.out.println(" BETS values[position][0]:"+values[position][0]);
	System.out.println(" BETS values[position][1]:"+values[position][1]);
    LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(R.layout.listviewdisplay, parent, false);
    TextView merchantName = (TextView) rowView.findViewById(R.id.merchantName);
    TextView merchantPrice = (TextView) rowView.findViewById(R.id.merchantPrice);
    merchantName.setText(values[position][0]);
    merchantPrice.setText(values[position][1]);

    return rowView;
}
}