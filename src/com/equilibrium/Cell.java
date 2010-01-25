package com.equilibrium;

import java.util.Vector;

import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;

public class Cell extends View implements OnClickListener {

	private Equilibrium e;
	private String sign;
	private String number;
	private int size;
	private int row;
	private int col;
	private boolean numberSetted = false;
	private int bkColor = Color.DKGRAY;
	private int borderColor = Color.BLACK;
	private int signColor = Color.RED;
	private int numberColor = Color.YELLOW;
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
        setOnClickListener(this);
        sign = "";
        number = "";
        row = r;
        col = c;
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
        
        p.setTextSize(10);
        p.setColor(signColor);
        canvas.drawText(sign, 2, 10, p);
        
        p.setTextSize(12);
        p.setColor(numberColor);
        canvas.drawText(number, (getWidth()/2)-(p.measureText(number)/2), (getHeight()/2)+4, p);
    }
    
    public void setBkColor(int color) {
    	bkColor = color;
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
    
    public void setNumber(String txt) {
    	number = txt;
    	numberSetted = true;
    	//Aggiorno il punteggio totale
    	if ((row != e.lato+1) && (col != e.lato+1)) {
    		e.updateSum(row, col);
    		e.setAround(row, col, Integer.parseInt(number));
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
    	around[num] = true;
    	if ((--possibilities) == 0) {
    		number = "0";
    		e.showNumbers(row, col);
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
    
	public void onClick(View v) {
		//Controllo di non essere nei bordi delle somme o di non aver cliccato la stessa casella
		if ((row != e.lato+1) && (col != e.lato+1) && (e.lastClicked != this)) {
			e.selectedRow = row;
			e.selectedCol = col;
			if (numberSetted == false) {
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
				e.showNumbers(row, col);
			} else {
				e.hideNumbers();
			}
			bkColor = Color.GRAY;
			if (e.lastClicked != null) {
				e.lastClicked.setBkColor(Color.DKGRAY);
			}
			e.lastClicked = this;
			invalidate();
		}
	}
}