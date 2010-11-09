package com.l1ck.equilibrium.logic;

import java.util.Vector;

public class EQPlayer {
	//private EQBoard board;
	private Vector<Boolean> playerRows;
	private Vector<Boolean> playerColumns;
	private boolean bot = false;
	private int color = 0;
	
	public EQPlayer(Vector<Boolean> rows, Vector<Boolean> cols, boolean b) 
	{
		//this.board = board;
		this.playerRows = (Vector<Boolean>) rows.clone();
		this.playerColumns = (Vector<Boolean>) cols.clone();
		this.bot = b;
	}
	
	public int getRowSum(EQBoard board, int r)
	{ 
		if (r >= 0 && r < board.getDimension() && playerRows.get(r))
			return board.getRowSum(r);
		else return 0;
	}
	
	public int getColumnSum(EQBoard board, int c)
	{ 
		if (c >= 0 && c < board.getDimension() && playerColumns.get(c))
			return board.getColumnSum(c);
		else return 0;
	}
	
	public int getScore(EQBoard board)
	{
		int score = 0;
		for (int i = 0; i < board.getDimension(); i++) {
			//if (playerRows.get(i)) {
				score += getRowSum(board, i);
			//}
		}
		for (int i = 0; i < board.getDimension(); i++) {
			//if (playerColumns.get(i)) {
				score += getColumnSum(board, i);
			//}
		}
		return score;
	}
	
	public boolean isBot()
	{
		return bot;
	}
	
	public boolean isMineRow(int r)
	{
		return playerRows.get(r);
	}
	
	public boolean isMineCol(int c)
	{
		return playerColumns.get(c);
	}
	
	public boolean isMine(EQCell c)
	{
		if (playerRows.get(c.getRow()) && playerColumns.get(c.getCol())) {
			return true;
		}
		return false;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}
}
