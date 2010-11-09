package com.equilibrium.logic;

import com.equilibrium.logic.EQCell;

public class EQBoard implements Cloneable{

	private int dimension;
	private EQCell[][] board;
	private int[] rowsSum;
	private int[] columnsSum;
	
	public EQBoard() {
		this(5);
	}
	
	public EQBoard(int d) {
		dimension = d;
		board = new EQCell[dimension][dimension];
		rowsSum = new int[dimension];
		columnsSum = new int[dimension];
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				board[i][j] = new EQCell(dimension, i, j);
			}
		}
	}
	
	public EQCell get(int i, int j)
	{
		if (i < 0 || i > dimension || j < 0 || j > dimension)
			return null;
		return board[i][j];
	}
	
	public int getRowSum(int r) {
		return Math.abs(rowsSum[r]);
	}
	
	public int getColumnSum(int c) {
		return Math.abs(columnsSum[c]);
	}
	
	public int getDimension() { return dimension; }
	
	public void insert(int v, int i, int j) throws EQMoves {
		if (i < 0 || i > dimension || j < 0 || j > dimension)
			return;
		if (board[i][j].setValue(v)) {
			if (j-1 >= 0) {
				board[i][j-1].removePsb(v);
				if (i-1 >= 0)
					board[i-1][j-1].removePsb(v);
				if (i+1 < dimension)
					board[i+1][j-1].removePsb(v);
			}
			if (j+1 < dimension) {
				board[i][j+1].removePsb(v);
				if (i-1 >= 0)
					board[i-1][j+1].removePsb(v);
				if (i+1 < dimension)
					board[i+1][j+1].removePsb(v);
			}
			if (i-1 >= 0)
				board[i-1][j].removePsb(v);
			if (i+1 < dimension)
				board[i+1][j].removePsb(v);
			
			rowsSum[i] += board[i][j].getValue();
			columnsSum[j] += board[i][j].getValue();
			
			EQMoves moves = checkBoard();
			if (moves.size() > 0) {
				throw moves;
			}
		}
	}
	
	public void insert(EQMoves.EQSingleMove mv) throws EQMoves {
		insert(mv.getValue(), mv.getRow(), mv.getCol());
	}
	
	// Manca la propagazione
	public void delete(int i, int j)
	{
		int v = Math.abs(board[i][j].getValue());
		if (v > 0){
			if (j-1 >= 0) {
				board[i][j-1].addPsb(v);
				if (i-1 >= 0)
					board[i-1][j-1].addPsb(v);
				if (i+1 < dimension)
					board[i+1][j-1].addPsb(v);
			}
			if (j+1 < dimension) {
				board[i][j+1].addPsb(v);
				if (i-1 >= 0)
					board[i-1][j+1].addPsb(v);
				if (i+1 < dimension)
					board[i+1][j+1].addPsb(v);
			}
			if (i-1 >= 0)
				board[i-1][j].addPsb(v);
			if (i+1 < dimension)
				board[i+1][j].addPsb(v);
			
			rowsSum[i] -= board[i][j].getValue();
			columnsSum[j] -= board[i][j].getValue();
			
			board[i][j].unsetValue();
		}
	}
	
	private EQMoves checkBoard() {
		EQMoves moves = new EQMoves(); 
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				if (!board[i][j].isSet() && board[i][j].getPsb().size() == 0) {
					moves.add(new EQMoves.EQSingleMove(0, i, j));
					//board[i][j].setValue(0);
				}
			}
		}
		return moves;
	}

	public Object clone() 
	{
		EQBoard newb = new EQBoard(dimension);
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				if (board[i][j].isSet())
					try {
						newb.insert(Math.abs(board[i][j].getValue()), i, j);
					} catch (EQMoves e) {
						try {
							for (int k = 0; k < e.size(); k++) {
								newb.insert(e.get(k));
							}
						} catch (EQMoves m) {}
					}
			}
		}
		return newb;
	}
	
	public String toString() {
		String strBoard = "";
		for (int i = 0; i < dimension; i++) {
			strBoard += "\n";
			for (int j = 0; j < dimension; j++) {
				strBoard += board[i][j].toString() + "   ";
			}
			strBoard += "("+getRowSum(i)+")";
		}
		strBoard += "\n";
		for(int i = 0; i < dimension; i++)
			strBoard += "("+getColumnSum(i)+")  ";
		return strBoard;
	}
}
