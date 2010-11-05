package com.equilibrium;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import com.equilibrium.logic.EQCell;

public class CellMinus extends Cell {

	public CellMinus(Context context, EQCell l) {
		super(context, l);
		bkImage = e.getResources().getDrawable(com.equilibrium.R.drawable.minus);
		this.setSignColor(Color.BLUE);
	}
	
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
	}

}
