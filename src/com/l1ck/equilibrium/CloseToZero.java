package com.l1ck.equilibrium;

import java.util.Vector;

import com.admob.android.ads.AdManager;
import com.admob.android.ads.AdView;
import com.l1ck.equilibrium.logic.EQAI;
import com.l1ck.equilibrium.logic.EQBoard;
import com.l1ck.equilibrium.logic.EQCell;
import com.l1ck.equilibrium.logic.EQMoves;
import com.l1ck.equilibrium.logic.EQPlayer;
import com.l1ck.equilibrium.logic.EQSudokuBoard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class CloseToZero extends Activity implements OnClickListener {
	
	/**
	 * Thread per l'esecuzione di mosse da parte dell'AI
	 * @author dario
	 */
	private class AIThread extends Thread {
		
		private int gameType = 0;						//Indica il tipo di algoritmo da usare
		private boolean suggest = false;				//Mossa di suggerimento
		
		//Indica i tipi di algoritmi disponibili
		public static final int EASY = 1;
		public static final int STANDARD = 2;
		public static final int HARD = 3;
		
		public AIThread(int gt) {
			this(gt, false);
		}
		
		public AIThread(int gt, boolean suggestMove) {
			this.gameType = gt;
			this.suggest = suggestMove;
		}
		
		public void run() {
			EQMoves.EQSingleMove move = null;
			switch (this.gameType) {
			case AIThread.EASY:
				move = EQAI.simpleAlg(board, players.get(), players.getOther());
				break;
			case AIThread.STANDARD:
				//move = EQAI.greedyAlg(board, players.get(), players.getOther());
				move = EQAI.extendedGreedyAlg(board, players.get(), players.getOther());
				break;
			case AIThread.HARD:
				move = EQAI.smartAlg(board, players.get(), players.getOther(), true);
				break;
			}
			Message m = new Message();
            m.what = (this.suggest) ? CloseToZero.AISUGGEST : CloseToZero.AIMOVE;
            m.obj = move;
            CloseToZero.this.threadHandler.sendMessage(m);
		}
	}
	
	/**
	 * Riceve i messaggi dai thread
	 */
	Handler threadHandler = new Handler(){
		// @Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case CloseToZero.AISUGGEST:
				stopLoading();
				if (msg.obj == null) {
					Toast tmp = Toast.makeText(CloseToZero.this, CloseToZero.this.getApplicationContext().getResources().getString(R.string.no_moves_available), Toast.LENGTH_LONG);
					tmp.show();
				} else {
					doVibration();
					EQMoves.EQSingleMove move = (EQMoves.EQSingleMove) msg.obj;
					if (lastClicked != null) {
						lastClicked.normal();
						eraseCross(lastClicked.getRow(), lastClicked.getCol());
					}
					selectedRow = move.getRow();
					selectedCol = move.getCol();
					drawCross(selectedRow, selectedCol);
					lastClicked = amatriciana.get(selectedRow).get(selectedCol);
					lastClicked.select();
					showNumbers(selectedRow, selectedCol);
				}
				break;
			case CloseToZero.AIMOVE:
				if (pause) {
					return;
				}
				stopLoading();
				if (msg.obj == null) {
					Toast tmp = Toast.makeText(CloseToZero.this, CloseToZero.this.getApplicationContext().getResources().getString(R.string.no_moves_available), Toast.LENGTH_LONG);
					tmp.show();
				} else {
					doVibration();
					EQMoves.EQSingleMove move = (EQMoves.EQSingleMove) msg.obj;
					addMove(move);
					if (lastClicked != null) {
						lastClicked.normal();
						eraseCross(lastClicked.getRow(), lastClicked.getCol());
					}
					drawCross(move.getRow(), move.getCol());
					lastClicked = amatriciana.get(move.getRow()).get(move.getCol());
					lastClicked.select();
				}
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	public int lato = 5;						//Numero di caselle per riga
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
	public String p1Color = "cyan";
	public String p2Color = "gray";
	public int cpuLevel = AIThread.EASY;
	public int gameMode = GAME_MODE_NORMAL;
	private LinearLayout scoreLayout;
	private HorizontalScrollView numbersLayout;
	protected ProgressDialog loadingDialog;
	private boolean blockInteraction = false;
	private boolean canVibrate = true;
	private boolean firstRun = true;
	private Vibrator vibro;
	private boolean pause = false;
	private AIThread thinkingAI = null;
	private DisplayMetrics displayMetrics = null;
	private EQMoves moveHistory = null;
	
	private EQBoard board = null;
	public Players players = null;
	
	public static Typeface NUMBER_FONT = null;
	public static Typeface TEXT_FONT = null;
	public static Typeface EQ_FONT = null;
	public static Typeface CZ_FONT = null;
	
	public static final int TEXT_SIZE = 15;
	
	public static final int GAME_MODE_NORMAL = 100;
	public static final int GAME_MODE_SUDOKU = 200;
	
	public static final int MENU_NEW_GAME = 424;
	public static final int MENU_UNDO = 215;
	public static final int MENU_SETTINGS = 548;
	public static final int MENU_HELP = 362;
	public static final int MENU_ABOUT = 654;
	public static final int MENU_SUGGEST = 345;
	public static final int MENU_QUIT = 189;
	
	public static final int DIALOG_HELP = 165;
	public static final int DIALOG_ABOUT = 245;
	
	public static final int AIMOVE = 875;
	public static final int AISUGGEST = 556;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.main);
        
        vibro = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        
        CloseToZero.NUMBER_FONT = Typeface.createFromAsset(getAssets(), "fonts/danielbd.ttf");
        CloseToZero.TEXT_FONT = Typeface.createFromAsset(getAssets(), "fonts/HappyKiller.ttf");
        CloseToZero.EQ_FONT = Typeface.createFromAsset(getAssets(), "fonts/BINREGUL.TTF");
        CloseToZero.CZ_FONT = Typeface.createFromAsset(getAssets(), "fonts/Visitor-TT2-BRK-.ttf");
        
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        
        l = (LinearLayout)this.findViewById(R.id.l);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setGravity(Gravity.FILL_VERTICAL);
        
        AdView adView = null;
        if (displayMetrics.heightPixels > 320) {
	        AdManager.setTestDevices(new String[] {
	        		AdManager.TEST_EMULATOR,	// Android emulator
	        		//"54C99084BAD7C6DA232F0DD4215BA36D", // Tattoo
	        });
	        adView = (AdView)this.findViewById(R.id.ad);
	        adView.requestFreshAd();
	        adView.setRequestInterval(60);
        }
        
        if (adView != null) {
        	RelativeLayout tmp = (RelativeLayout)this.findViewById(R.id.adLayout);
        	tmp.setVisibility(RelativeLayout.VISIBLE);
        }
        
        getPrefs();
        
        startActivityForResult(new Intent(getBaseContext(), Startup.class), 0);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch (requestCode) {
    	case 0:
    		switch (resultCode) {
    		case RESULT_CANCELED:
    			this.finish();
    			break;
			default:
				getPrefs();
				start();
	    		updateScore();
				break;
    		}
    		break;
    	}
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	startActivityForResult(new Intent(getBaseContext(), Startup.class), 0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	
    	if (getPrefs() || firstRun) {
    		firstRun = false;
    		start();
    	}
    	updateScore();
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
        
        boolean newP2Cpu = (prefs.getString("p2Cpu", "cpu").equals("human")) ? false : true;
        if (newP2Cpu != p2Cpu) {
        	p2Cpu = newP2Cpu;
        	ris = true;
        }
        
        int newCpuLevel = Integer.parseInt(prefs.getString("cpuLevel", String.valueOf(AIThread.EASY)));
        if (newCpuLevel != cpuLevel) {
        	cpuLevel = newCpuLevel;
        	ris = true;
        }
        
        int newGameMode = Integer.parseInt(prefs.getString("gameMode", String.valueOf(GAME_MODE_NORMAL)));
        if (newGameMode != gameMode) {
        	gameMode = newGameMode;
        	ris = true;
        }
        
        p1Color = prefs.getString("p1Color", "cyan");
        p2Color = prefs.getString("p2Color", "gray");
        if (players != null) {
        	players.get(1).setColor(Color.parseColor(p1Color));
        	players.get(2).setColor(Color.parseColor(p2Color));
        }
        
        showPartialSum = prefs.getBoolean("partialSum", true);
        CellSum.SHOW_SUM = showPartialSum;
        
        canVibrate = prefs.getBoolean("canVibrate", true);
        
        int newLato = Integer.parseInt(prefs.getString("size", "5"));
        if (newLato != lato) {
        	lato = newLato;
        	ris = true;
        }
        
        return ris;
	}
    
    /* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_NEW_GAME, 0, R.string.menu_new).setIcon(android.R.drawable.ic_menu_add);
        menu.add(0, MENU_UNDO, 1, R.string.menu_undo).setIcon(android.R.drawable.ic_menu_revert);
        menu.add(0, MENU_SUGGEST, 2, R.string.menu_suggest).setIcon(android.R.drawable.ic_menu_search);
        menu.add(0, MENU_HELP, 3, R.string.menu_help).setIcon(android.R.drawable.ic_menu_help);
        menu.add(0, MENU_SETTINGS, 4, R.string.menu_settings).setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(0, MENU_ABOUT, 5, R.string.menu_about).setIcon(android.R.drawable.ic_menu_info_details);
        menu.add(0, MENU_QUIT, 6, R.string.menu_exit).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        return true;
    }

    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_NEW_GAME:
            start();
            return true;
        case MENU_UNDO:
        	if (!blockInteraction) {
        		undoLastMove(true);
        	}
            return true;
        case MENU_SETTINGS:
        	pauseAI();
        	if (displayMetrics.heightPixels <= 320) {
        		startActivity(new Intent(getBaseContext(), SettingsTab.class));
        	} else {
        		startActivity(new Intent(getBaseContext(), Settings.class));
        	}
        	return true;
        case MENU_HELP:
        	this.showDialog(DIALOG_HELP);
        	return true;
        case MENU_ABOUT:
        	this.showDialog(DIALOG_ABOUT);
        	return true;
        case MENU_SUGGEST:
        	if (!blockInteraction) {
	        	startLoading();
	        	thinkingAI = new AIThread(cpuLevel, true);
	        	thinkingAI.start();
        	}
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
        	break;
    	case DIALOG_ABOUT:
    		dialog = new Dialog(this);

        	dialog.setContentView(R.layout.about);
        	dialog.setTitle(R.string.about);
        	break;
    	}
    	return dialog;
    }
    
    public void start() {
    	
    	l.removeAllViews();
    	
    	//Creo la board
    	switch (this.gameMode) {
    	case GAME_MODE_NORMAL:
    		board = new EQBoard(lato);
    		break;
    	case GAME_MODE_SUDOKU:
    		board = new EQSudokuBoard(lato);
    		break;
    	}
    	moveHistory = new EQMoves();
    	
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
        			} else {
        				c = new CellAIControl(this);
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
    	
    	numbersLayout = new HorizontalScrollView(this);
    	numbersLayout.setHorizontalFadingEdgeEnabled(true);
    	numbersLayout.setFadingEdgeLength(30);
    	numbersLayout.setFillViewport(true);
    	numbersLayout.setSmoothScrollingEnabled(true);
    	
        //Aggiungo gli elementi alla finestra
    	l.addView(scoreLayout);
        l.addView(a);
        l.addView(numbersLayout);
        
        pauseAI();
        if (players.get().isBot()) {
        	if (!players.getOther().isBot()) {
        		doAIMove();
        	}
        }
    }
    
    public void pauseAI() {
    	if (players.isBothBot()) {
	    	this.pause = true;
	    	this.blockInteraction = true;
	    	if (thinkingAI != null) {
	    		thinkingAI.interrupt();
	    	}
	    	stopLoading();
    	}
    }
    
    public void resumeAI() {
    	this.pause = false;
    	doAIMove();
	}
    
    public boolean isPaused() {
    	return this.pause;
    }
    
    public void doAIMove() {
    	if (pause) {
    		return;
    	}
    	startLoading();
    	thinkingAI = new AIThread(cpuLevel);
    	thinkingAI.start();
    }
    
    public void undoLastMove(boolean rec) {
    	if (moveHistory.size() > 0) {
    		EQMoves.EQSingleMove lastMove = null;
    		do {
    			lastMove = moveHistory.pop();
    			this.board.delete(lastMove);
    			this.turnLeft++;
    			this.updateRowSum(lastMove.getRow());
        		this.updateColSum(lastMove.getCol());
    		} while (moveHistory.size() > 0 && lastMove.getValue() == 0);
    		if (players.getOther().isBot()) {
    			if (rec) {
    				undoLastMove(false);
    			}
    		} else {
    			nextPlayer();
    		}
    		if (this.lastClicked != null) {
    			this.lastClicked.normal();
    		}
    		l.invalidate();
    	} else {
    		Toast tmp = Toast.makeText(this, getResources().getString(R.string.undo_error), Toast.LENGTH_LONG);
    		tmp.show();
    	}
	}
    
    public void nextPlayer() {
    	players.next();
    	updateScore();
    	if (players.get().isBot()) {
    		doAIMove();
    	}
    }
    
    public void startLoading() {
    	this.blockInteraction = true;
    	numbersLayout.removeAllViews();
    	TextView t = new TextView(this);
    	t.setTypeface(TEXT_FONT);
    	t.setTextSize(TEXT_SIZE+5);
    	t.setTextColor(players.get().getColor());
    	t.setText(R.string.thinking);
    	t.setGravity(Gravity.CENTER);
    	t.setPadding(5, 5, 5, 5);
    	numbersLayout.addView(t);
    }
    
    public void stopLoading() {
    	this.blockInteraction = false;
    	this.thinkingAI = null;
    	numbersLayout.removeAllViews();
    }
    
    public void showNumbers(int row, int col) {
    	if (blockInteraction) {
    		return;
    	}
    	numbersLayout.removeAllViews();
    	TableLayout table = new TableLayout(this);
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
    	table.addView(c);
    	numbersLayout.addView(table);
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
    	                CloseToZero.this.start();
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
		doVibration();
		hideNumbers();
	}
    
    public void doVibration() {
    	if (this.canVibrate) {
    		vibro.vibrate(100);
    	}
    }
    
    public void addMove(EQMoves.EQSingleMove move) {
    	try {
    		amatriciana.get(move.getRow()).get(move.getCol()).select();
    		turnLeft--;
    		moveHistory.add(move);
    		board.insert(move);
    	} catch (EQMoves zeroMove) {
    		try {
				for (int i = 0; i < zeroMove.size(); i++) {
					turnLeft--;
					moveHistory.add(zeroMove.get(i));
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
    	((CellSum)amatriciana.get(row).get(lato)).setNumber(tmp.getRowSum(board, row));
    }
    
    public void updateColSum(int col) {
    	EQPlayer tmp = null;
    	if (players.get().isMineCol(col)) {
    		tmp = players.get();
    	} else {
    		tmp = players.getOther();
    	}
    	((CellSum)amatriciana.get(lato).get(col)).setNumber(tmp.getColumnSum(board, col));
    }
    
    public void updateScore() {
    	scoreLayout.removeAllViews();
    	TableLayout table = new TableLayout(this);
    	table.setStretchAllColumns(true);
    	TableRow r = new TableRow(this);
    	for (int i = 1; i <= 2; i++) {
    		String out = "";
	    	TextView t = new TextView(this);
	    	t.setGravity(Gravity.CENTER_HORIZONTAL);
	    	t.setTypeface(TEXT_FONT);
	    	t.setTextSize(TEXT_SIZE);
	    	t.setTextColor(players.get(i).getColor());
	    	if (players.get(i) == players.get()) {
    			out += "» ";
	    	}
	    	out += ((players.get(i).isBot()) ? getResources().getString(R.string.cpu) : getResources().getString(R.string.human));
	    	out += ": ";
	    	out += players.get(i).getScore(board);
	    	t.setText(out);
	    	r.addView(t);
    	}
    	table.addView(r);
    	scoreLayout.addView(table);
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
    
}