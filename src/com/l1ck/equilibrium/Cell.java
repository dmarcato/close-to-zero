package com.l1ck.equilibrium;

import com.l1ck.equilibrium.logic.EQCell;

import android.view.MotionEvent;
import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

public class Cell extends View {

	protected CloseToZero e;
	protected EQCell logic;
	protected int size;
	public int row;
	public int col;
	public int originalBkColor = Color.LTGRAY;//0xFFEEEEEE;
	public int bkColor = Color.LTGRAY;//0xFFEEEEEE;
	protected int borderColor = Color.DKGRAY;
	protected int signColor = Color.RED;
	protected int numberColor = Color.WHITE;
	protected int highlightNumberColor = 0;
	protected int currentNumberColor = 0;
	protected Drawable bkImage = null;
	protected int originalAlpha = 255-220;
	protected int selectedAlpha = 255-30;
	protected int currentAlpha = 0;
	protected float originalShadow = 2;
	protected float highlightShadow = 0.5f;
	protected float currentShadow = 0;
	protected boolean showBorder = false;
	
	protected Paint painter = null;
	
	 /**
     * Constructor.  This version is only needed if you will be instantiating
     * the object manually (not from a layout XML file).
     * @param context
     */
    public Cell(Context context, EQCell l) {
        super(context);
        e = (CloseToZero) getContext();
        logic = l;
        if (logic != null) {
        	row = logic.getRow();
            col = logic.getCol();
        }
        bkImage = e.getResources().getDrawable(R.drawable.trans);
        bkImage.setAlpha(255);
        currentAlpha = originalAlpha;
        currentShadow = originalShadow;
        currentNumberColor = numberColor;
        
        painter = new Paint();
        painter.setAntiAlias(true);
        painter.setTypeface(CloseToZero.NUMBER_FONT);
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
     * Render the text
     * 
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        painter.setColor(bkColor);
        painter.setShader(new LinearGradient(size / 2, 0, size / 2, size / 2, bkColor, Color.WHITE, Shader.TileMode.MIRROR));
        int padding = (int) Math.round(0.03*size);
        canvas.drawRoundRect(new RectF(padding, padding, size-padding, size-padding), size/7, size/7, painter);
        painter.setShader(null);
        
        padding = (int) Math.round(0.17*size);
        bkImage.setBounds(padding, padding, size-padding, size-padding);
        bkImage.setAlpha(currentAlpha);
        bkImage.draw(canvas);
        
        painter.setTextSize(size*2/3);
        painter.setFakeBoldText(true);
        painter.setShadowLayer(currentShadow, 0, 0, signColor);
        painter.setColor(currentNumberColor);
        String n = (this.isSetted()) ? String.valueOf(getNumber()) : "";
        canvas.drawText(n, (getWidth()/2)-(painter.measureText(n)/2), (3*size/4), painter);
        painter.setShadowLayer(0, 0, 0, 0);
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
    		e.selectedRow = row;
    		e.selectedCol = col;
    		if (!this.isSetted()) {
    			e.showNumbers(row, col);
    			select();
    		} else {
    			e.hideNumbers();
    		}
    		if ((e.lastClicked != null) && (e.lastClicked != this)) {
    			e.lastClicked.normal();
    		}
    		e.lastClicked = this;
    		e.drawCross(row, col);
    		break;
    	}
		return true;
    }
    
    public boolean isSetted() {
		return this.logic.isSet();
	}

	public void setSignColor(int color) {
    	signColor = color;
    	invalidate();
    }
    
    public void setNumberColor(int color) {
    	numberColor = color;
    	invalidate();
    }
    
    public int getNumber() {
    	return Math.abs(this.logic.getValue());
    }
    
    public int getSize() {
    	return size;
    }
    
    public void highlight() {
    	currentShadow = highlightShadow;
    	currentNumberColor = Color.argb(80, Color.red(signColor), Color.green(signColor), Color.blue(signColor));
    	invalidate();
    }
    
    public void select() {
    	currentAlpha = selectedAlpha;
    	currentNumberColor = numberColor;
    	showBorder = true;
    	invalidate();
    }
    
    public void normal() {
    	currentShadow = originalShadow;
    	currentAlpha = originalAlpha;
    	currentNumberColor = numberColor;
    	showBorder = false;
    	invalidate();
    }
    
    public int getRow() {
    	return this.logic.getRow();
    }
    
    public int getCol() {
    	return this.logic.getCol();
    }
}
