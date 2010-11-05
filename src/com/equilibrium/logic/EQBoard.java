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
	
	public void insert(int v, int i, int j) {
		if (i < 0 || i > dimension || j < 0 || j > dimension)
			return;
		if (board[i][j].setValue(v)) {
			if (j-1 >= 0) {
				board[i][j-1].getPsb().removeElement(v);
				if (i-1 >= 0)
					board[i-1][j-1].getPsb().removeElement(v);
				if (i+1 < dimension)
					board[i+1][j-1].getPsb().removeElement(v);
			}
			if (j+1 < dimension) {
				board[i][j+1].getPsb().removeElement(v);
				if (i-1 >= 0)
					board[i-1][j+1].getPsb().removeElement(v);
				if (i+1 < dimension)
					board[i+1][j+1].getPsb().removeElement(v);
			}
			if (i-1 >= 0)
				board[i-1][j].getPsb().removeElement(v);
			if (i+1 < dimension)
				board[i+1][j].getPsb().removeElement(v);
			
			rowsSum[i] += board[i][j].getValue();
			columnsSum[j] += board[i][j].getValue();
			
			checkBoard();
		}
	}
	
	// Manca la propagazione
	public void delete(int i, int j)
	{
		board[i][j].unsetValue();
	}
	
	private void checkBoard() {
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				if (!board[i][j].isSet() && board[i][j].getPsb().size() == 0) {
					board[i][j].setValue(0);
				}
			}
		}
	}

	public Object clone() 
	{
		EQBoard newb = new EQBoard(dimension);
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				if (board[i][j].isSet())
					newb.insert(Math.abs(board[i][j].getValue()), i, j);
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
