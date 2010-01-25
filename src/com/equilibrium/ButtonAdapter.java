package com.equilibrium;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;

public class ButtonAdapter extends BaseAdapter implements OnClickListener {

	private Context cnt;
	private int[] num;
	private Dialog dialog;
	private Cell cell;
	
	public ButtonAdapter(Context c, int[] n, Dialog dia, Cell cl) {
		cnt = c;
		num = n;
		dialog = dia;
		cell = cl;
	}
	
	public int getCount() {
		return num.length;
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Button btn;
		if (convertView == null) {  // if it's not recycled, initialize some attributes
            btn = new Button(cnt);
            btn.setText(Integer.toString(num[position]));
            btn.setOnClickListener(this);
        } else {
            btn = (Button) convertView;
        }
		
		return btn;
	}

	public void onClick(View v) {
		Button b = (Button) v;
		cell.setNumber((String) b.getText());
    	dialog.dismiss();
	}

}
