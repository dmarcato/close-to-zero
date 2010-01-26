package com.equilibrium;

import java.util.Vector;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

public class Equilibrium extends Activity implements OnClickListener {
	
	public int lato = 6;						//Numero di caselle per riga
	public Cell[][] amatriciana;				//Matrice delle caselle
	public int selectedRow = 0;					//Indice di riga della casella selezionata
	public int selectedCol = 0;					//Indice di colonna della casella selezionata
	public LinearLayout l;						//Layout principale
	public Cell lastClicked = null;				//Ultima casella cliccata
	public Cell lastMoved = null;				//Ultima casella mossa
	public Vector<Integer> playerRows;		//Righe del giocatore
	public Vector<Integer> playerCols;		//Caselle del giocatore
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        
        //Scelto righe e colonne random
        int totRows = (int) Math.floor(lato / 2);
        int totCols = lato - totRows;
        playerRows = new Vector<Integer>();
        playerCols = new Vector<Integer>();
        while ((totRows > 0) || (totCols > 0)) {
        	int tmp = (int)(lato*Math.random())+1;
        	if ((totRows > 0) && (!playerRows.contains(tmp))) {
        		playerRows.add(tmp);
        		totRows--;
        	} else if (playerCols.contains(tmp) == false) {
        		playerCols.add(tmp);
        		totCols--;
        	}
        }
        
        l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setGravity(Gravity.FILL_VERTICAL);
        
        TableLayout a = new TableLayout(this);
        a.setStretchAllColumns(true);
        
        amatriciana = new Cell[lato+2][];
        boolean isPlayer = false;
        for (int i = 0; i < lato+2; i++) {
        	if (playerRows.contains(i)) {
        		isPlayer = true;
        	} else {
        		isPlayer = false;
        	}
        	amatriciana[i] = new Cell[lato+2];
        	TableRow r = new TableRow(this);
        	for (int j = 0; j < lato+2; j++) {
        		amatriciana[i][j] = new Cell(this, i, j);
        		if ((j < lato+1) && (i < lato+1)) {
	        		String sign;
	        		if ((i+j) % 2 == 0) {
	        			sign = "+";
	        			amatriciana[i][j].setSignColor(0xFF0000AA);
	        		} else {
	        			sign = "-";
	        			amatriciana[i][j].setSignColor(0xFFAA0000);
	        		}
	        		amatriciana[i][j].setSign(sign);
        		} else {
        			amatriciana[i][j].setNumber("0");
        			if ((isPlayer == true) || (playerCols.contains(j) == true)) {
        				amatriciana[i][j].setBkColor(0xFF9999CC, true);
        				amatriciana[i][j].setNumberColor(Color.BLUE);
            		} else {
            			amatriciana[i][j].setBkColor(0xFFCC9999, true);
            			amatriciana[i][j].setNumberColor(Color.RED);
            		}
        		}
        		if ((j != 0) && (i != 0)) {
        			if (((i != lato+1) || (j != lato+1))) {
        				r.addView(amatriciana[i][j]);
        			}
        		}
        	}
        	a.addView(r);
        }
        
        l.addView(a);
        
        setContentView(l);
    }
    
    public void updateSum(int row, int col) {
    	int sumX = 0;
    	int sumY = 0;
    	for (int i = 1; i < lato+1; i++) {
    		if (amatriciana[row][i].getSign() == "-") {
    			sumX -= amatriciana[row][i].getNumber();
    		} else {
    			sumX += amatriciana[row][i].getNumber();
    		}
    		
    		if (amatriciana[i][col].getSign() == "-") {
    			sumY -= amatriciana[i][col].getNumber();
    		} else {
    			sumY += amatriciana[i][col].getNumber();
    		}
    	}
    	amatriciana[row][lato+1].setNumber(Integer.toString(sumX));
    	amatriciana[lato+1][col].setNumber(Integer.toString(sumY));
    }
    
    public void setAround(int row, int col, int number) {
    	amatriciana[row-1][col-1].setAround(number);
    	amatriciana[row-1][col].setAround(number);
    	amatriciana[row-1][col+1].setAround(number);
    	amatriciana[row][col-1].setAround(number);
    	amatriciana[row][col+1].setAround(number);
    	amatriciana[row+1][col-1].setAround(number);
    	amatriciana[row+1][col+1].setAround(number);
    	amatriciana[row+1][col].setAround(number);
    }
    
    public void showNumbers(int row, int col) {
    	TableLayout b = new TableLayout(this);
    	TableRow c = new TableRow(this);
    	Vector<Integer> around = amatriciana[row][col].getAvailableAround();
    	Button btn;
    	for (int i = 0; i < around.size(); i++) {
    		btn = new Button(this);
            btn.setText(Integer.toString(around.elementAt(i)));
            btn.setOnClickListener(this);
            c.addView(btn);
    	}
    	b.addView(c);
    	if (l.getChildCount() > 1) {
    		l.removeViewAt(1);
    	}
    	l.addView(b, 1);
    }
    
    public void hideNumbers() {
    	if (l.getChildCount() > 1) {
    		l.removeViewAt(1);
    	}
    }
    
    public void onClick(View v) {
		Button b = (Button) v;
		amatriciana[selectedRow][selectedCol].setNumber((String) b.getText());
		hideNumbers();
	}
    
    public void drawCross(int row, int col) {
    	for (int i = 1; i <= lato; i++) {
    		amatriciana[row][i].setBkColor(amatriciana[row][lato+1].bkColor);
    		amatriciana[i][col].setBkColor(amatriciana[lato+1][col].bkColor);
    	}
    	amatriciana[row][col].setBkColor(0xFFCC99CC);
    }
    
    public void eraseCross(int row, int col) {
    	for (int i = 1; i <= lato; i++) {
    		amatriciana[row][i].setBkColor(amatriciana[row][i].originalBkColor);
    		amatriciana[i][col].setBkColor(amatriciana[i][col].originalBkColor);
    	}
    }
    
    public boolean onTouchEvent(MotionEvent event) {
    	
    	float x = event.getX();
    	float y = event.getY() - 50;
    	
    	float cellSize = amatriciana[1][1].getSize();
    	float max = cellSize*lato;
    	
    	if (((x > 0) && (y > 0)) && ((x < max) && (y < max))) {
	    	int row = (int) Math.floor(y / cellSize)+1;
	    	int col = (int) Math.floor(x / cellSize)+1;
	    	
	    	if (event.getAction() == MotionEvent.ACTION_DOWN) {
	    		if (lastClicked != null)
	    			lastClicked.setBkColor(lastClicked.originalBkColor);
	    		drawCross(row, col);
	    		lastMoved = amatriciana[row][col];
	    	}
	    	if (event.getAction() == MotionEvent.ACTION_MOVE) {
	    		eraseCross(lastMoved.row, lastMoved.col);
	    		drawCross(row, col);
	    		lastMoved = amatriciana[row][col];
	    	}
	    	if (event.getAction() == MotionEvent.ACTION_UP) {
	    		eraseCross(lastMoved.row, lastMoved.col);
	    		amatriciana[row][col].click();
	    	}
    	}
    	
    	return false;
    }
}