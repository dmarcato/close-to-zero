package com.equilibrium;

import java.util.Vector;

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

	private Equilibrium e;
	private String sign;
	private String number;
	private int size;
	public int row;
	public int col;
	public boolean numberSetted = false;
	public int originalBkColor = 0xFFEEEEEE;
	public int bkColor = 0xFFEEEEEE;
	private int borderColor = Color.DKGRAY;
	private int signColor = Color.RED;
	private int numberColor = Color.WHITE;
	private Drawable bkImage = null;
	private int originalAlpha = 255-30;
	private int selectedAlpha = 255-150;
	private int currentAlpha = 0;
	private int originalShadow = 0;
	private int highlightShadow = 0;
	private int currentShadow = 0;
	private boolean[] around;
	private int possibilities;
	
	 /**
     * Constructor.  This version is only needed if you will be instantiating
     * the object manually (not from a layout XML file).
     * @param context
     */
    public Cell(Context context, int r, int c) {
        super(context);
        e = (Equilibrium) getContext();
        bkImage = e.getResources().getDrawable(com.equilibrium.R.drawable.trans);
        bkImage.setAlpha(255);
        sign = number = "";
        row = r;
        col = c;
        currentAlpha = originalAlpha;
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
        
        if (e.amatriciana[e.lato+1][e.lato+1].equals(this)) {
        	setBkColor(Color.TRANSPARENT);
        	p.setColor((e.turn)? 0xFF9999CC : 0xFFCC9999);
        	canvas.drawCircle(size/2, size/2, size/2-6, p);

        	p.setTextSize(size/3);
            p.setColor(numberColor);
            canvas.drawText(number, (getWidth()/2)-(p.measureText(number)/2), (5*getHeight()/8), p);
        } else {
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
	        p.setShadowLayer(2, 0, 0, currentShadow);
	        p.setColor(numberColor);
	        canvas.drawText(number, (getWidth()/2)-(p.measureText(number)/2), (3*size/4), p);
        }
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
    	originalShadow = Color.argb(100, Color.red(signColor), Color.green(signColor), Color.blue(signColor));
    	highlightShadow = Color.argb(255, Color.red(signColor), Color.green(signColor), Color.blue(signColor));
    	currentShadow = originalShadow;
    	invalidate();
    }
    
    public void setSign(String txt) {
    	sign = txt;
    	if (sign == "+") {
    		bkImage = e.getResources().getDrawable(com.equilibrium.R.drawable.plus);
    	} else if (sign == "-") {
    		bkImage = e.getResources().getDrawable(com.equilibrium.R.drawable.minus);
    	}
    	invalidate();
    }
    
    public String getSign() {
    	return sign;
    }
    
    public void setNumberColor(int color) {
    	numberColor = color;
    	invalidate();
    }
    
    public void setNumber(String txt) {
    	number = txt;
    	numberSetted = true;
    	this.normal();
    	e.eraseCross(row, col);
    	//Aggiorno il punteggio totale
    	if ((row != e.lato+1) && (col != e.lato+1)) {
    		e.updateSum(row, col);
    		e.setAround(row, col, Integer.parseInt(number));
    		e.turnLeft--;
    	}
    	invalidate();
    }
    
    public int getNumber() {
    	if (number != "") {
    		return Integer.parseInt(number);
    	} else {
    		return 0;
    	}
    }
    
    public void setAround(int num) {
    	if ((num != 0) && (around[num] == false)) {
    		--possibilities;
    		around[num] = true;
    	}
    	if ((possibilities == 0) && (numberSetted == false)) {
    		setNumber("0");
    	}
    }
    
    public Vector<Integer> getAvailableAround() {
    	Vector<Integer> a = new Vector<Integer>();
    	for (int i = 1; i <= e.lato; i++) {
    		if (around[i] == false) {
    			a.add(i);
    		}
    	}
    	return a;
    }
    
    public int getSize() {
    	return size;
    }
    
    public void highlight() {
    	currentShadow = highlightShadow;
    }
    
    public void normal() {
    	currentShadow = originalShadow;
    	currentAlpha = originalAlpha;
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