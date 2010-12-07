package com.l1ck.equilibrium;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.MotionEvent;

public class CellSum extends Cell {

	private String number = "0";
	
	public static boolean SHOW_SUM = false;
	
	public CellSum(Context context, int r, int c) {
		super(context, null);
		this.row = r;
		this.col = c;
		this.number = "0";
		this.signColor = Color.BLACK;
	}
	    
    /**
     * Render the text
     * 
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (col == e.lato && row != e.lato) {
			if (e.players.get().isMineRow(row)) {
				this.bkColor = e.players.get().getColor();
			} else {
				this.bkColor = e.players.getOther().getColor();
			}
		} else if (row == e.lato && col != e.lato) {
			if (e.players.get().isMineCol(col)) {
				this.bkColor = e.players.get().getColor();
			} else {
				this.bkColor = e.players.getOther().getColor();
			}
		}
        
        painter.setColor(bkColor);
        int gradColor = Color.argb(150, Color.red(bkColor), Color.green(bkColor), Color.blue(bkColor));
        painter.setShader(new LinearGradient(size / 2, 0, size / 2, size / 2, gradColor, bkColor, Shader.TileMode.MIRROR));
        int padding = (int) Math.round(0.03*size);
        canvas.drawRoundRect(new RectF(padding, padding, size-padding, size-padding), size/7, size/7, painter);
        painter.setShader(null);
        
        if (CellSum.SHOW_SUM) {
        	painter.setAlpha(255);
        	painter.setTextSize(size/3);
	        painter.setFakeBoldText(true);
	        painter.setShadowLayer(currentShadow, 0, 0, signColor);
	        painter.setColor(currentNumberColor);
	        canvas.drawText(number, (getWidth()/2)-(painter.measureText(number)/2), (5*size/8), painter);
	        painter.setShadowLayer(0, 0, 0, 0);
        }
    }
	
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
    	case MotionEvent.ACTION_DOWN:
    		if (e.lastClicked != null) {
				e.lastClicked.normal();
				e.eraseCross(e.lastClicked.row, e.lastClicked.col);
    		}
			e.lastMoved = this;
    		break;
    	case MotionEvent.ACTION_UP:
    		if ((e.lastClicked != null) && (e.lastClicked != this)) {
    			e.lastClicked.normal();
    		}
    		e.lastClicked = this;
    		e.drawCross(row, col);
    		e.hideNumbers();
    		break;
    	}
		return true;
    }
	
	public int getRow() {
    	return this.row;
    }
    
    public int getCol() {
    	return this.col;
    }
    
    public void setNumber(int n) {
    	this.setNumber(String.valueOf(n));
    }
    
    public void setNumber(String n) {
    	this.number = n;
    }

}
