package com.equilibrium;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
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
		this.number = "0";
		this.signColor = Color.BLACK;
	}
	
	
	/**
     * @see android.view.View#measure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	setMeasuredDimension(measureWidth(widthMeasureSpec),
                measureHeight(heightMeasureSpec));
    }

    /**
     * Determines the width of this view
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    protected int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text
            //result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
            //        + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }

        size = result;
        return result;
    }

    /**
     * Determines the height of this view
     * @param measureSpec A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
    protected int measureHeight(int measureSpec) {
    	return size;
    }
	
	/**
     * @see android.view.View#measure(int, int)
     */
    /*@Override
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
    }*/
    
    /**
     * Render the text
     * 
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        
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
    	//canvas.drawColor(Color.argb(100, Color.red(bkColor), Color.green(bkColor), Color.blue(bkColor)));
    	//canvas.drawColor(bkColor);
    	
        Paint p = new Paint();
        p.setAntiAlias(true);
        
        p.setColor(bkColor);
        if (row == e.lato) {
        	p.setShader(new LinearGradient(size / 2, 0, size / 2, size, Color.WHITE, bkColor, Shader.TileMode.REPEAT));
        	canvas.drawRect(new Rect(0, 0, size, size / 2), p);
        	canvas.drawArc(new RectF(0, 0, size, size), 0, 180, false, p);
        } else {
        	p.setShader(new LinearGradient(0, size / 2, size, size / 2, Color.WHITE, bkColor, Shader.TileMode.REPEAT));
        	canvas.drawRect(new Rect(0, 0, size / 2, size), p);
        	canvas.drawArc(new RectF(0, 0, size, size), -90, 180, false, p);
        }
        p.setShader(null);
        p.setColor(borderColor);
        p.setAlpha(100);
        if (row != e.lato) {
        	canvas.drawLine(0, 0, getWidth(), 0, p);
        }
        if (col != e.lato) {
        	canvas.drawLine(0, 0, 0, getHeight(), p);
        }
        canvas.drawLine(getWidth(), 0, getWidth(), getHeight(), p);
        canvas.drawLine(getWidth(), getHeight(), 0, getHeight(), p);
        
        //canvas.drawRGB(Color.red(signColor), Color.green(signColor), Color.blue(signColor));
        /*int padding = (int) Math.round(0.17*size);
        bkImage.setBounds(padding, padding, size-padding, size-padding);
        bkImage.draw(canvas);*/
        //canvas.drawARGB(selectedAlpha, 255, 255, 255);
        
        if (CellSum.SHOW_SUM) {
        	p.setAlpha(255);
        	p.setTextSize(size/3);
	        p.setFakeBoldText(true);
	        p.setShadowLayer(currentShadow, 0, 0, signColor);
	        p.setColor(currentNumberColor);
	        canvas.drawText(number, (getWidth()/2)-(p.measureText(number)/2), (5*size/8), p);
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
    		break;
    	}
		return true;
    }

}
