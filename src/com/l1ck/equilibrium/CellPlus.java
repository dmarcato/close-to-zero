package com.l1ck.equilibrium;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import com.l1ck.closetozero.R;
import com.l1ck.equilibrium.logic.EQCell;

public class CellPlus extends Cell {

	public CellPlus(Context context, EQCell l) {
		super(context, l);
		bkImage = e.getResources().getDrawable(R.drawable.plus);
		this.setSignColor(Color.RED);
	}
	
	@Override
    protected void onDraw(Canvas canvas) {
		bkColor = originalBkColor;
        if (showBorder) {
        	bkColor = Color.argb(Color.alpha(signColor), Color.red(signColor), Color.green(signColor)+100, Color.blue(signColor)+100);
        }
        super.onDraw(canvas);
	}

}
