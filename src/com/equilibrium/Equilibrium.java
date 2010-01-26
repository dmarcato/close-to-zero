package com.equilibrium;

import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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
	public Vector<Integer> playerRows;			//Righe del giocatore
	public Vector<Integer> playerCols;			//Caselle del giocatore
	public boolean turn = true;
	public int turnLeft = 0;
	
	public static final int MENU_NEW_GAME = 424;
	public static final int MENU_SETTINGS = 548;
	public static final int MENU_QUIT = 189;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        
        l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setGravity(Gravity.FILL_VERTICAL);        
        setContentView(l);
        
        start();
    }
    
    /* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_NEW_GAME, 0, "Nuova Partita").setIcon(android.R.drawable.ic_menu_add);
        menu.add(0, MENU_SETTINGS, 1, "Impostazioni").setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(0, MENU_QUIT, 2, "Esci").setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        return true;
    }

    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_NEW_GAME:
            start();
            return true;
        case MENU_QUIT:
            finish();
            return true;
        }
        return false;
    }
    
    public void start() {
    	turnLeft = lato*lato;
        
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
        			if (((i != lato+1) || (j != lato+1))) {
        				amatriciana[i][j].setNumber("0");
        			}
        			if ((isPlayer == true) || (playerCols.contains(j) == true)) {
        				amatriciana[i][j].setBkColor(0xFF9999CC, true);
        				amatriciana[i][j].setNumberColor(Color.BLUE);
            		} else {
            			amatriciana[i][j].setBkColor(0xFFCC9999, true);
            			amatriciana[i][j].setNumberColor(Color.RED);
            		}
        		}

        		if ((j != 0) && (i != 0)) {
        			//if (((i != lato+1) || (j != lato+1))) {
        				r.addView(amatriciana[i][j]);
        			//}
        		}
        	}
        	a.addView(r);
        }
        
        if (l.getChildCount() > 0) {
        	l.removeViewAt(0);
        }
        l.addView(a, 0);
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
    		btn.setHeight(amatriciana[row][col].getSize());
    		btn.setWidth(amatriciana[row][col].getSize());
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
    
    public void finishGame() {
    	int totPlayer = 0;
    	int totOther = 0;
    	for (int i = 1; i <= lato; i++) {
    		if (playerRows.contains(i) == true) {
    			totPlayer += amatriciana[i][lato+1].getNumber();
    		} else {
    			totOther += amatriciana[i][lato+1].getNumber();
    		}
    		if (playerCols.contains(i) == true) {
    			totPlayer += amatriciana[lato+1][i].getNumber();
    		} else {
    			totOther += amatriciana[lato+1][i].getNumber();
    		}
    	}
    	String winnerText = "";
    	if (Math.abs(totPlayer) < Math.abs(totOther)) {
    		turn = true;
    		amatriciana[lato+1][lato+1].setNumber(Integer.toString(totPlayer));
    		amatriciana[lato+1][lato+1].setNumberColor(Color.BLUE);
    		winnerText = "Vince il giocatore BLU!";
    	} else if (Math.abs(totPlayer) > Math.abs(totOther)) {
    		turn = false;
    		amatriciana[lato+1][lato+1].setNumber(Integer.toString(totOther));
    		amatriciana[lato+1][lato+1].setNumberColor(Color.RED);
    		winnerText = "Vince il giocatore ROSSO!";
    	} else {
    		amatriciana[lato+1][lato+1].setNumber(Integer.toString(totOther));
    		amatriciana[lato+1][lato+1].setNumberColor(Color.BLACK);
    		winnerText = "Partita patta!";
    	}
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(winnerText)
    	       .setCancelable(false)
    	       .setPositiveButton("Nuova partita", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                Equilibrium.this.start();
    	                dialog.dismiss();
    	           }
    	       })
    	       .setNegativeButton("Chiudi", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
    	AlertDialog alert = builder.create();
    	alert.show();
    }
    
    public void onClick(View v) {
		Button b = (Button) v;
		amatriciana[selectedRow][selectedCol].setNumber((String) b.getText());
		hideNumbers();
		turn = !turn;
		if (turnLeft == 0) {
			finishGame();
		}
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
    	float y = event.getY();
    	float paddingTop = getWindowManager().getDefaultDisplay().getHeight() - l.getHeight();
    	y -= paddingTop;
    	
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