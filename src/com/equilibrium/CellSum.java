package com.equilibrium;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;

import com.equilibrium.logic.EQCell;

public class CellSum extends Cell {

	public static boolean SHOW_SUM = false;
	
	public CellSum(Context context, int r, int c) {
		super(context, null);
		this.row = r;
		this.col = c;
		currentAlpha = selectedAlpha;
		if (col == e.lato && row != e.lato) {
			if (e.p1.isMineRow(row)) {
				this.bkColor = e.p1.getColor();
			} else {
				this.bkColor = e.p2.getColor();
			}
		} else if (row == e.lato && col != e.lato) {
			if (e.p1.isMineCol(col)) {
				this.bkColor = e.p1.getColor();
			} else {
				this.bkColor = e.p2.getColor();
			}
		}
	}
	
	/**
     * @see android.view.View#measure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	int w = measureWidth(widthMeasureSpec);
    	int h = measureHeight(heightMeasureSpec);
    	if (!CellSum.SHOW_SUM) {
    		if (row == e.lato && w != 0) {
    			h = 10;
    		} else if (col == e.lato && h != 0) {
    			w = 10;
    		}
    	}
    	Log.i("Equilibrium", String.valueOf(row)+":"+String.valueOf(col)+" "+String.valueOf(w)+" - "+String.valueOf(h));
    	setMeasuredDimension(w, h);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		return true;
    }

}
