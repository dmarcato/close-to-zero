package com.equilibrium;

import java.util.Vector;

import com.equilibrium.logic.EQCell;

import android.R;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class Cell extends View {

	protected Equilibrium e;
	protected EQCell logic;
	protected String sign;
	protected String number;
	protected int size;
	public int row;
	public int col;
	public boolean numberSetted = false;
	public int originalBkColor = 0xFFEEEEEE;
	public int bkColor = 0xFFEEEEEE;
	protected int borderColor = Color.DKGRAY;
	protected int signColor = Color.RED;
	protected int numberColor = Color.WHITE;
	protected int highlightNumberColor = 0;
	protected int currentNumberColor = 0;
	protected Drawable bkImage = null;
	protected int originalAlpha = 255-30;
	protected int selectedAlpha = 255-150;
	protected int currentAlpha = 0;
	protected float originalShadow = 2;
	protected float highlightShadow = 0.5f;
	protected float currentShadow = 0;
	protected boolean[] around;
	protected int possibilities;
	
	 /**
     * Constructor.  This version is only needed if you will be instantiating
     * the object manually (not from a layout XML file).
     * @param context
     */
    public Cell(Context context, EQCell l) {
        super(context);
        e = (Equilibrium) getContext();
        logic = l;
        bkImage = e.getResources().getDrawable(com.equilibrium.R.drawable.trans);
        bkImage.setAlpha(255);
        sign = number = "";
        row = logic.getRow();
        col = logic.getCol();
        currentAlpha = originalAlpha;
        currentShadow = originalShadow;
        currentNumberColor = numberColor;
        possibilities = e.lato;
        around = new boolean[e.lato+1];
        for (int i = 0; i <= e.lato; i++) {
        	around[i] = false;
        }
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
    private int measureWidth(int measureSpec) {
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
    private int measureHeight(int measureSpec) {
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
        
        canvas.drawColor(bkColor);
        
        Paint p = new Paint();
        p.setAntiAlias(true);
        
        p.setColor(borderColor);
        canvas.drawLine(0, 0, getWidth(), 0, p);
        canvas.drawLine(0, 0, 0, getHeight(), p);
        canvas.drawLine(getWidth(), 0, getWidth(), getHeight(), p);
        canvas.drawLine(getWidth(), getHeight(), 0, getHeight(), p);
        
        //canvas.drawRGB(Color.red(signColor), Color.green(signColor), Color.blue(signColor));
        int padding = (int) Math.round(0.17*size);
        bkImage.setBounds(padding, padding, size-padding, size-padding);
        bkImage.draw(canvas);
        canvas.drawARGB(currentAlpha, 255, 255, 255);
        
        p.setTextSize(size*2/3);
        p.setFakeBoldText(true);
        p.setShadowLayer(currentShadow, 0, 0, signColor);
        p.setColor(currentNumberColor);
        canvas.drawText(number, (getWidth()/2)-(p.measureText(number)/2), (3*size/4), p);
    }
    
    public boolean onTouchEvent(MotionEvent event) {
    	switch (event.getAction()) {
    	case MotionEvent.ACTION_DOWN:
    		if (e.lastClicked != null) {
				e.lastClicked.currentAlpha = originalAlpha;
				e.eraseCross(e.lastClicked.row, e.lastClicked.col);
    		}
			e.lastMoved = this;
    		break;
    	case MotionEvent.ACTION_UP:
    		currentAlpha = selectedAlpha;
        	if ((row != e.lato+1) && (col != e.lato+1)) {
        		e.selectedRow = row;
        		e.selectedCol = col;
        		if (numberSetted == false) {
        			e.showNumbers(row, col);
        		} else {
        			e.hideNumbers();
        		}
        		if ((e.lastClicked != null) && (e.lastClicked != this)) {
        			e.lastClicked.currentAlpha = originalAlpha;
        		}
        		e.lastClicked = this;
        		e.drawCross(row, col);
        	}
    		break;
    	}
		return true;
    }
    
    public void setBkColor(int color) {
    	setBkColor(color, false);
    }
    
    public void setBkColor(int color, boolean original) {
    	bkColor = color;
    	if (original == true) {
    		originalBkColor = color;
    	}
    	invalidate();
    }
    
    public void setSignColor(int color) {
    	signColor = color;
    	invalidate();
    }
    
    public void setSign(String txt) {
    	sign = txt;
    	invalidate();
    }
    
    public String getSign() {
    	return sign;
    }
    
    public void setNumberColor(int color) {
    	numberColor = color;
    	invalidate();
    }
    
    public void setNumber(int num) {
    	setNumber(String.valueOf(num));
    }
    
    public void setNumber(String txt) {
    	number = txt;
    	numberSetted = true;
    	this.normal();
    	invalidate();
    }
    
    public int getNumber() {
    	if (number != "") {
    		return Integer.parseInt(number);
    	} else {
    		return 0;
    	}
    }
    
    public int getSize() {
    	return size;
    }
    
    public void highlight() {
    	currentShadow = highlightShadow;
    	currentNumberColor = Color.argb(80, Color.red(signColor), Color.green(signColor), Color.blue(signColor));
    }
    
    public void normal() {
    	currentShadow = originalShadow;
    	currentAlpha = originalAlpha;
    	currentNumberColor = numberColor;
    }
        
	/* public boolean onTouchEvent(MotionEvent event) {
		//Controllo di non essere nei bordi delle somme o di non aver cliccato la stessa casella
		if ((row != e.lato+1) && (col != e.lato+1) && (e.lastClicked != this)) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
	    		setBkColor(Color.GREEN);
	    	}
	    	if (event.getAction() == MotionEvent.ACTION_UP) {
	    		setBkColor(originalBkColor);
    			e.selectedRow = row;
    			e.selectedCol = col;
    			if (numberSetted == false) {
    				e.showNumbers(row, col);
    			} else {
    				e.hideNumbers();
    			}
    			bkColor = Color.GRAY;
    			if (e.lastClicked != null) {
    				e.lastClicked.setBkColor(e.lastClicked.originalBkColor);
    			}
    			e.lastClicked = this;
    			invalidate();
    		}
    	}
    	return true;
	}*/
}

/*Dialog tmp = new Dialog(getContext());
GridView gridview = new GridView(getContext());

int[] num = new int[e.lato];
for (int i = 0; i < e.lato; i++) {
	//Controllo i lati
	num[i] = i+1;
}

gridview.setNumColumns(GridView.AUTO_FIT);
gridview.setAdapter(new ButtonAdapter(getContext(), num, tmp, this));

tmp.setTitle("Scegli un numero");
tmp.setContentView(gridview);
tmp.show();*/