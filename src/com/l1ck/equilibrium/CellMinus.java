package com.l1ck.equilibrium;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import com.l1ck.equilibrium.logic.EQCell;

public class CellMinus extends Cell {

	public CellMinus(Context context, EQCell l) {
		super(context, l);
		bkImage = e.getResources().getDrawable(R.drawable.minus);
		this.setSignColor(Color.BLUE);
	}
	
	@Override
    protected void onDraw(Canvas canvas) {
		bkColor = originalBkColor;
        if (showBorder) {
        	bkColor = Color.argb(Color.alpha(signColor), Color.red(signColor)+50, Color.green(signColor)+50, Color.blue(signColor));
        }
        super.onDraw(canvas);
	}

}
