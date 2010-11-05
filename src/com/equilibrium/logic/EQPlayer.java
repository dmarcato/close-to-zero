package com.equilibrium.logic;

import java.util.Vector;

public class EQPlayer {
	//private EQBoard board;
	private Vector<Boolean> playerRows;
	private Vector<Boolean> playerColumns;
	
	public EQPlayer(Vector<Boolean> rows, Vector<Boolean> cols) 
	{
		//this.board = board;
		this.playerRows = rows;
		this.playerColumns = cols;
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
		for (int i = 0; i < board.getDimension(); i++)
			if (playerRows.get(i))
				score += getRowSum(board, i);
		for (int i = 0; i < board.getDimension(); i++)
			if (playerColumns.get(i))
			score += getColumnSum(board, i);
		return score;
	}
}
