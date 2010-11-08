package com.equilibrium;

import java.util.Iterator;
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
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Equilibrium extends Activity implements OnClickListener {
	
	/**
	 * Thread per l'esecuzione di mosse da parte dell'AI
	 * @author dario
	 */
	private class AIThread extends Thread {
		
		private int gameType = 0;						//Indica il tipo di algoritmo da usare
		
		//Indica i tipi di algoritmi disponibili
		public static final int EASY = 1;
		public static final int STANDARD = 2;
		public static final int HARD = 3;
		
		public AIThread(int gt) {
			this.gameType = gt;
		}
		
		public void run() {
			EQMoves.EQSingleMove move = null;
			switch (this.gameType) {
			case AIThread.EASY:
				move = EQAI.simpleAlg(board, players.get(), players.getOther());
				break;
			case AIThread.STANDARD:
				move = EQAI.greedyAlg(board, players.get(), players.getOther());
				break;
			case AIThread.HARD:
				move = EQAI.greedyAlg(board, players.get(), players.getOther());
				break;
			}
			Message m = new Message();
            m.what = Equilibrium.AIMOVE;
            m.obj = move;
            Equilibrium.this.threadHandler.sendMessage(m);
		}
	}
	
	/**
	 * Riceve i messaggi dai thread
	 */
	Handler threadHandler = new Handler(){
		// @Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Equilibrium.AIMOVE:
				stopLoading();
				EQMoves.EQSingleMove move = (EQMoves.EQSingleMove) msg.obj;
				addMove(move);
				if (lastClicked != null) {
					lastClicked.normal();
					eraseCross(lastClicked.logic.getRow(), lastClicked.logic.getCol());
				}
				drawCross(move.getRow(), move.getCol());
				lastClicked = amatriciana.get(move.getRow()).get(move.getCol());
				lastClicked.select();
				//loadingDialog.dismiss();
				break;
			}
			super.handleMessage(msg);
		}
	};
	
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
	public boolean p1Cpu = false;
	public boolean p2Cpu = false;
	public String p1Color = "green";
	public String p2Color = "magenta";
	public int cpuLevel = AIThread.EASY;
	private LinearLayout scoreLayout;
	private TableLayout numbersLayout;
	protected ProgressDialog loadingDialog;
	private boolean firstRun = true;
	private boolean blockInteraction = false;
	
	private EQBoard board = null;
	public Players players = null;
	
	public static final int MENU_NEW_GAME = 424;
	public static final int MENU_SETTINGS = 548;
	public static final int MENU_HELP = 362;
	public static final int MENU_QUIT = 189;
	
	public static final int DIALOG_HELP = 165;
	
	public static final int AIMOVE = 875;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        
        l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setGravity(Gravity.FILL_VERTICAL);
        setContentView(l);
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	
    	if (getPrefs() || firstRun) {
    		firstRun = false;
    		start();
    	}
    	if (scoreLayout != null) {
			scoreLayout.invalidate();
		}
    }
    
    private boolean getPrefs() {
    	boolean ris = false;
        // Get the xml/preferences.xml preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        
        boolean newP1Cpu = (prefs.getString("p1Cpu", "human").equals("human")) ? false : true;
        if (newP1Cpu != p1Cpu) {
        	p1Cpu = newP1Cpu;
        	ris = true;
        }
        
        boolean newP2Cpu = (prefs.getString("p2Cpu", "human").equals("human")) ? false : true;
        if (newP2Cpu != p2Cpu) {
        	p2Cpu = newP2Cpu;
        	ris = true;
        }
        
        int newCpuLevel = Integer.parseInt(prefs.getString("cpuLevel", String.valueOf(AIThread.EASY)));
        if (newCpuLevel != cpuLevel) {
        	cpuLevel = newCpuLevel;
        	ris = true;
        }
        
        p1Color = prefs.getString("p1Color", "green");
        p2Color = prefs.getString("p2Color", "yellow");
        if (players != null) {
        	players.get(1).setColor(Color.parseColor(p1Color));
        	players.get(2).setColor(Color.parseColor(p2Color));
        }
        
        showPartialSum = prefs.getBoolean("partialSum", true);
        CellSum.SHOW_SUM = showPartialSum;
        int newLato = Integer.parseInt(prefs.getString("size", "6"));
        if (newLato != lato) {
        	lato = newLato;
        	ris = true;
        }
        return ris;
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
    		dialog = new Dialog(this);

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
    	
    	l.removeAllViews();
    	
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
        EQPlayer p1 = new EQPlayer(pRows, pCols, p1Cpu);
        p1.setColor(Color.parseColor(p1Color));
        for (int i = 0; i < lato; i++) {
        	pRows.set(i, !pRows.get(i));
        	pCols.set(i, !pCols.get(i));
        }
    	EQPlayer p2 = new EQPlayer(pRows, pCols, p2Cpu);
    	p2.setColor(Color.parseColor(p2Color));
    	
    	players = new Players(p1, p2);
    	
        TableLayout a = new TableLayout(this);
        a.setStretchAllColumns(true);
        
        amatriciana = new Vector<Vector<Cell>>();
        for (int i = 0; i <= lato; i++) {
        	TableRow r = new TableRow(this);
        	amatriciana.add(new Vector<Cell>());
        	for (int j = 0; j <= lato; j++) {
        		Cell c = null;
        		if (i == lato || j == lato) {
        			if (i != j) {
        				c = new CellSum(this, i, j);
        			}
        		} else {
	        		EQCell eqc = board.get(i, j);
	        		if (eqc.getSign()) {
	        			c = new CellPlus(this, eqc);
	        		} else {
	        			c = new CellMinus(this, eqc);
	        		}
        		}
        		if (c != null) {
        			amatriciana.get(i).add(c);
        			r.addView(c);
        		}
        	}
        	a.addView(r);
        }        
        if (l.getChildCount() > 0) {
        	l.removeViewAt(0);
        }
        
        scoreLayout = new LinearLayout(this);
        scoreLayout.setHorizontalGravity(Gravity.CENTER);
        scoreLayout.setPadding(2, 2, 2, 2);
    	updateScore();
    	
    	numbersLayout = new TableLayout(this);
    	
        //Aggiungo gli elementi alla finestra
    	l.addView(scoreLayout);
        l.addView(a);
        l.addView(numbersLayout);
        
        if (players.get().isBot()) {
        	doAIMove();
        }
    }
    
    public void doAIMove() {
    	//loadingDialog = ProgressDialog.show(this, "", getResources().getText(R.string.loading), true);
    	startLoading();
    	new AIThread(cpuLevel).start();
    }
    
    public void nextPlayer() {
    	players.next();
    	if (players.get().isBot()) {
    		doAIMove();
    	}
    }
    
    public void startLoading() {
    	this.blockInteraction = true;
    	numbersLayout.removeAllViews();
    	TextView t = new TextView(this);
    	t.setTextColor(players.get().getColor());
    	t.setTextSize(20);
    	t.setTypeface(Typeface.DEFAULT_BOLD);
    	t.setText(R.string.thinking);
    	t.setGravity(Gravity.CENTER);
    	t.setPadding(5, 5, 5, 5);
    	numbersLayout.addView(t);
    }
    
    public void stopLoading() {
    	this.blockInteraction = false;
    	numbersLayout.removeAllViews();
    }
    
    public void showNumbers(int row, int col) {
    	if (blockInteraction) {
    		return;
    	}
    	numbersLayout.removeAllViews();
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
    	numbersLayout.addView(c);
    }
    
    public void hideNumbers() {
    	if (blockInteraction) {
    		return;
    	}
    	numbersLayout.removeAllViews();
    }
    
    public void finishGame() {
    	int p1Score = players.get(1).getScore(board);
    	int p2Score = players.get(2).getScore(board);
    	int winnerText;
    	if (p1Score < p2Score) {
    		winnerText = R.string.player1_win;
    	} else if (p1Score > p2Score) {
    		winnerText = R.string.player2_win;
    	} else {
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
    	alert.show();
    }
    
    public void onClick(View v) {
		Button b = (Button) v;
		EQMoves.EQSingleMove mv = new EQMoves.EQSingleMove(Integer.parseInt((String)b.getText()), selectedRow, selectedCol);
		addMove(mv);
		hideNumbers();
	}
    
    public void addMove(EQMoves.EQSingleMove move) {
    	try {
    		amatriciana.get(move.getRow()).get(move.getCol()).select();
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
    	updateRowSum(move.getRow());
    	updateColSum(move.getCol());
    	if (turnLeft == 0) {
    		updateScore();
			finishGame();
		} else {
			nextPlayer();
			updateScore();
		}
    }
    
    public void updateRowSum(int row) {
    	EQPlayer tmp = null;
    	if (players.get().isMineRow(row)) {
    		tmp = players.get();
    	} else {
    		tmp = players.getOther();
    	}
    	amatriciana.get(row).get(lato).setNumber(tmp.getRowSum(board, row));
    }
    
    public void updateColSum(int col) {
    	EQPlayer tmp = null;
    	if (players.get().isMineCol(col)) {
    		tmp = players.get();
    	} else {
    		tmp = players.getOther();
    	}
    	amatriciana.get(lato).get(col).setNumber(tmp.getColumnSum(board, col));
    }
    
    public void updateScore() {
    	scoreLayout.removeAllViews();
    	for (int i = 1; i <= 2; i++) {
    		String out = "";
	    	TextView t = new TextView(this);
	    	t.setTextColor(players.get(i).getColor());
	    	t.setTypeface(Typeface.DEFAULT_BOLD);
	    	if (players.get(i) == players.get()) {
    			out += "» ";
	    	}
	    	out += ((players.get(i).isBot()) ? getResources().getString(R.string.cpu) : getResources().getString(R.string.human));
	    	out += ": ";
	    	out += players.get(i).getScore(board);
	    	t.setText(out);
	    	scoreLayout.addView(t);
	    	if (i != 2) {
	    		t = new TextView(this);
	    		t.setText("     ");
	    		scoreLayout.addView(t);
	    	}
    	}
    }
    
    public void drawCross(int row, int col) {
    	for (int i = 0; i < lato; i++) {
    		if (row != lato) {
    			amatriciana.get(row).get(i).highlight();
    		}
    		if (col != lato) {
    			amatriciana.get(i).get(col).highlight();
    		}
    	}
    }
    
    public void eraseCross(int row, int col) {
    	for (int i = 0; i < lato; i++) {
    		if (row != lato) {
    			amatriciana.get(row).get(i).normal();
    		}
    		if (col != lato) {
    			amatriciana.get(i).get(col).normal();
    		}
    	}
    }
    
    /*public boolean onTouchEvent(MotionEvent event) {
    	int x = (int) event.getX();
    	int y = (int) event.getY();
    	Cell theCell = null;
    	for (int i = 0; i < lato; i++) {
    		for (int j = 0; j < lato; j++) {
    			Rect r = new Rect();
    			Cell c = amatriciana.get(i).get(j);
    			r.left = c.getLeft();
    			r.top = c.getTop();
    			r.right = c.getRight();
    			r.bottom = c.getBottom();
    			if (r.contains(x, y)) {
    				theCell = amatriciana.get(i).get(j);
    			}
    		}
    	}
    	if (theCell != null) {
			switch (event.getAction()) {
	    	case MotionEvent.ACTION_DOWN:
	    		if (lastClicked != null) {
					lastClicked.normal();
					eraseCross(lastClicked.row, lastClicked.col);
	    		}
				lastMoved = theCell;
	    		break;
	    	case MotionEvent.ACTION_UP:
	    		if ((lastClicked != null) && (lastClicked != theCell)) {
	    			lastClicked.normal();
	    		}
	    		lastClicked = theCell;
	    		drawCross(theCell.logic.getRow(), theCell.logic.getCol());
	    		break;
	    	}
    	}
		return true;
    }*/
}