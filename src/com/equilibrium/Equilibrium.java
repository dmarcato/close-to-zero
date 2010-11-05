package com.equilibrium;

import java.util.Vector;

import com.equilibrium.logic.EQAI;
import com.equilibrium.logic.EQBoard;
import com.equilibrium.logic.EQCell;
import com.equilibrium.logic.EQMoves;
import com.equilibrium.logic.EQPlayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Equilibrium extends Activity implements OnClickListener {
	
	private class AIThread extends Thread {
		
		private int gameType = 0;
		
		public static final int STANDARD = 1;
		
		public AIThread(int gt) {
			this.gameType = gt;
		}
		
		public void run() {
			EQMoves.EQSingleMove move = null;
			switch (this.gameType) {
			case AIThread.STANDARD:
				move = EQAI.greedyAlg(board, p2, p1);
				break;
			}
			addMove(move);
			loadingDialog.dismiss();
		}
	}
	
	public int lato = 6;						//Numero di caselle per riga
	public Vector<Vector<Cell>> amatriciana;	//Matrice delle caselle
	public int selectedRow = 0;					//Indice di riga della casella selezionata
	public int selectedCol = 0;					//Indice di colonna della casella selezionata
	public LinearLayout l;						//Layout principale
	public Cell lastClicked = null;				//Ultima casella cliccata
	public Cell lastMoved = null;				//Ultima casella mossa
	public Vector<Integer> playerRows;			//Righe del giocatore
	public Vector<Integer> playerCols;			//Caselle del giocatore
	public boolean turn = true;
	public int turnLeft = 0;
	public boolean showPartialSum = true;
	public boolean cpu = false;
	private LinearLayout scoreLayout;
	protected ProgressDialog loadingDialog;
	
	private EQBoard board = null;
	public EQPlayer p1 = null;
	public EQPlayer p2 = null;
	public EQPlayer currentPlayer = null;
	
	public static final int MENU_NEW_GAME = 424;
	public static final int MENU_SETTINGS = 548;
	public static final int MENU_HELP = 362;
	public static final int MENU_QUIT = 189;
	
	public static final int DIALOG_HELP = 165;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        
        l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setGravity(Gravity.FILL_VERTICAL);
        setContentView(l);
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	
    	getPrefs();
    	start();
    }
    
    private void getPrefs() {
        // Get the xml/preferences.xml preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        cpu = prefs.getBoolean("vsCpu", false);
        showPartialSum = prefs.getBoolean("partialSum", true);
        lato = Integer.parseInt(prefs.getString("size", "6"));
	}
    
    /* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_NEW_GAME, 0, R.string.menu_new).setIcon(android.R.drawable.ic_menu_add);
        menu.add(0, MENU_SETTINGS, 1, R.string.menu_settings).setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(0, MENU_HELP, 2, R.string.menu_help).setIcon(android.R.drawable.ic_menu_help);
        menu.add(0, MENU_QUIT, 3, R.string.menu_exit).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        return true;
    }

    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_NEW_GAME:
            start();
            return true;
        case MENU_SETTINGS:
        	startActivity(new Intent(getBaseContext(), Settings.class));
        	return true;
        case MENU_HELP:
        	this.showDialog(DIALOG_HELP); //No funsia :/
        	return true;
        case MENU_QUIT:
            finish();
            return true;
        }
        return false;
    }
    
    protected Dialog onCreateDialog(int id) {
    	Dialog dialog = null;
    	switch (id) {
    	case DIALOG_HELP:
    		dialog = new Dialog(this.getApplicationContext());

        	dialog.setContentView(R.layout.help);
        	dialog.setTitle(R.string.help);
        	/*TextView text = (TextView) dialog.findViewById(R.id.text);
        	text.setText("Hello, this is a custom dialog!");
        	ImageView image = (ImageView) dialog.findViewById(R.id.image);
        	image.setImageResource(R.drawable.android);*/
        	break;
    	}
    	return dialog;
    }
    
    public void start() {
    	
    	//Creo la board
    	board = new EQBoard(lato);
    	
    	//Turni da giocare
    	turnLeft = lato*lato;
        
        //Scelto righe e colonne random
        int totRows = (int) Math.floor(lato / 2);
        int totCols = lato - totRows;
        Vector<Boolean> pRows = new Vector<Boolean>();
        Vector<Boolean> pCols = new Vector<Boolean>();
        for (int i = 0; i < lato; i++) {
        	pRows.add(false);
        	pCols.add(false);
        }
        Vector<Integer> pos = new Vector<Integer>();
        while (pos.size() < totRows) {
        	int tmp = (int)(lato*Math.random());
        	if (!pos.contains(tmp)) {
        		pos.add(tmp);
        		pRows.set(tmp, true);
        	}
        }
        pos.clear();
        while (pos.size() < totCols) {
        	int tmp = (int)(lato*Math.random());
        	if (!pos.contains(tmp)) {
        		pos.add(tmp);
        		pCols.set(tmp, true);
        	}
        }
        p1 = new EQPlayer(pRows, pCols, false);
        for (int i = 0; i < lato; i++) {
        	pRows.set(i, !pRows.get(i));
        	pCols.set(i, !pCols.get(i));
        }
    	p2 = new EQPlayer(pRows, pCols, cpu);
    	
    	currentPlayer = p1;
        
        TableLayout a = new TableLayout(this);
        a.setStretchAllColumns(true);
        
        amatriciana = new Vector<Vector<Cell>>();
        for (int i = 0; i < lato; i++) {
        	TableRow r = new TableRow(this);
        	amatriciana.add(new Vector<Cell>());
        	for (int j = 0; j < lato; j++) {
        		EQCell eqc = board.get(i, j);
        		Cell c = null;
        		if (eqc.getSign()) {
        			c = new CellPlus(this, eqc);
        		} else {
        			c = new CellMinus(this, eqc);
        		}
        		amatriciana.get(i).add(c);
        		r.addView(c);
        	}
        	a.addView(r);
        }
        
        if (l.getChildCount() > 0) {
        	l.removeViewAt(0);
        }
        l.addView(a, 0);
        
        scoreLayout = new LinearLayout(this);
        l.addView(scoreLayout, 1);
        
        updateScore();
        
        if (currentPlayer.isBot()) {
        	doAIMove();
        }
    }
    
    public void doAIMove() {
    	loadingDialog = ProgressDialog.show(this, "", getResources().getText(R.string.loading), true);
    	new AIThread(AIThread.STANDARD).start();
    }
    
    public void nextPlayer() {
    	if (currentPlayer.equals(p1)) {
    		currentPlayer = p2;
    	} else {
    		currentPlayer = p1;
    	}
    	if (currentPlayer.isBot()) {
    		doAIMove();
    	}
    }
    
    public void showNumbers(int row, int col) {
    	TableLayout b = new TableLayout(this);
    	TableRow c = new TableRow(this);
    	EQCell cell = board.get(row, col);
    	Vector<Integer> around = cell.getPsb();
    	Button btn;
    	for (int i = 0; i < around.size(); i++) {
    		btn = new Button(this);
    		btn.setHeight(amatriciana.get(row).get(col).getSize());
    		btn.setWidth(amatriciana.get(row).get(col).getSize());
            btn.setText(Integer.toString(around.elementAt(i)));
            btn.setOnClickListener(this);
            c.addView(btn);
    	}
    	b.addView(c);
    	if (l.getChildCount() > 2) {
    		l.removeViewAt(2);
    	}
    	l.addView(b, 2);
    }
    
    public void hideNumbers() {
    	if (l.getChildCount() > 2) {
    		l.removeViewAt(2);
    	}
    }
    
    public void finishGame() {
    	/*int p1Score = p1.getScore(board);
    	int p2Score = p2.getScore(board);
    	int winnerText;
    	if (Math.abs(totPlayer) < Math.abs(totOther)) {
    		turn = true;
    		amatriciana[lato+1][lato+1].setNumber(Integer.toString(totPlayer));
    		amatriciana[lato+1][lato+1].setNumberColor(Color.BLUE);
    		winnerText = R.string.blue_player_win;
    	} else if (Math.abs(totPlayer) > Math.abs(totOther)) {
    		turn = false;
    		amatriciana[lato+1][lato+1].setNumber(Integer.toString(totOther));
    		amatriciana[lato+1][lato+1].setNumberColor(Color.RED);
    		winnerText = R.string.red_player_win;
    	} else {
    		amatriciana[lato+1][lato+1].setNumber(Integer.toString(totOther));
    		amatriciana[lato+1][lato+1].setNumberColor(Color.BLACK);
    		winnerText = R.string.noone_win;
    	}
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(winnerText)
    	       .setCancelable(false)
    	       .setPositiveButton(R.string.new_game, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                Equilibrium.this.start();
    	                dialog.dismiss();
    	           }
    	       })
    	       .setNegativeButton(R.string.close_dialog, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
    	AlertDialog alert = builder.create();
    	alert.show();*/
    }
    
    public void onClick(View v) {
		Button b = (Button) v;
		EQMoves.EQSingleMove mv = new EQMoves.EQSingleMove(Integer.parseInt((String)b.getText()), selectedRow, selectedCol);
		addMove(mv);
		hideNumbers();
	}
    
    public void addMove(EQMoves.EQSingleMove move) {
    	try {
    		amatriciana.get(move.getRow()).get(move.getCol()).setNumber(move.getValue());
    		turnLeft--;
    		board.insert(move);
    	} catch (EQMoves zeroMove) {
    		try {
				for (int i = 0; i < zeroMove.size(); i++) {
					amatriciana.get(zeroMove.get(i).getRow()).get(zeroMove.get(i).getCol()).setNumber(zeroMove.get(i).getValue());
					turnLeft--;
					board.insert(zeroMove.get(i));
				}
			} catch (EQMoves m) {}
    	}
    	updateScore();
    	if (turnLeft == 0) {
			finishGame();
		} else {
			nextPlayer();
		}
    }
    
    public void updateScore() {
    	scoreLayout.removeAllViewsInLayout();
        TextView tw = new TextView(this);
        tw.setText("Player 1: "+p1.getScore(board));
        scoreLayout.addView(tw);
        tw = new TextView(this);
        tw.setText("Player 2: "+p2.getScore(board));
        scoreLayout.addView(tw);
    }
    
    public void drawCross(int row, int col) {
    	for (int i = 0; i < lato; i++) {
    		amatriciana.get(row).get(i).highlight();
    		amatriciana.get(i).get(col).highlight();
    	}
    }
    
    public void eraseCross(int row, int col) {
    	for (int i = 0; i < lato; i++) {
    		amatriciana.get(row).get(i).normal();
    		amatriciana.get(i).get(col).normal();
    	}
    }
}