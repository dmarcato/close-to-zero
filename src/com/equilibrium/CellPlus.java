package com.equilibrium;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import com.equilibrium.logic.EQCell;

public class CellPlus extends Cell {

	public CellPlus(Context context, EQCell l) {
		super(context, l);
		bkImage = e.getResources().getDrawable(com.equilibrium.R.drawable.plus);
		this.setSignColor(Color.RED);
	}
	
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
	}

}
