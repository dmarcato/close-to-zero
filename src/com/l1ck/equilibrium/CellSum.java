package com.l1ck.equilibrium;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.MotionEvent;

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
        
        painter.setColor(bkColor);
        int gradColor = Color.argb(150, Color.red(bkColor), Color.green(bkColor), Color.blue(bkColor));
        painter.setShader(new LinearGradient(size / 2, 0, size / 2, size / 2, gradColor, bkColor, Shader.TileMode.MIRROR));
        int padding = (int) Math.round(0.03*size);
        canvas.drawRoundRect(new RectF(padding, padding, size-padding, size-padding), size/7, size/7, painter);
        
        /*if (row == e.lato) {
        	//painter.setShader(new LinearGradient(size / 2, 0, size / 2, size, Color.WHITE, bkColor, Shader.TileMode.REPEAT));
        	canvas.drawRoundRect(new RectF(padding, padding, size-padding, size-padding), size / 2, size / 2, painter);
        } else {
        	//painter.setShader(new LinearGradient(0, size / 2, size, size / 2, Color.WHITE, bkColor, Shader.TileMode.REPEAT));
        	canvas.drawRoundRect(new RectF(-size / 2, 0, size*6/7, size), size / 2, size / 2, painter);
        }*/
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

}
