package com.l1ck.equilibrium.logic;

import java.util.Hashtable;
import java.util.Vector;

import android.util.Log;

import com.l1ck.equilibrium.logic.EQCell;

public class EQBoard implements Cloneable{

	protected static class Coord {
		
		public static String get(int r, int c) {
			return String.valueOf(r)+":"+String.valueOf(c);
		}
	}
	
	protected int dimension;
	protected EQCell[][] board;
	protected int[] rowsSum;
	protected int[] columnsSum;
	protected Hashtable<Integer, Hashtable<String, EQCell>> psbList;
	protected Hashtable<Integer, Integer> maxGain;
	
	public EQBoard() {
		this(5);
	}
	
	public EQBoard(int d) {
		dimension = d;
		board = new EQCell[dimension][dimension];
		rowsSum = new int[dimension];
		columnsSum = new int[dimension];
		psbList = new Hashtable<Integer, Hashtable<String, EQCell>>();
		maxGain = new Hashtable<Integer, Integer>();
		Hashtable<String, EQCell> tmp = new Hashtable<String, EQCell>();
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				board[i][j] = new EQCell(dimension, i, j);
				tmp.put(Coord.get(i, j), board[i][j]);
			}
			psbList.put(i+1, new Hashtable<String, EQCell>());
			maxGain.put(i+1, dimension*dimension);
		}
		psbList.put(dimension, tmp);
	}
	
	public Hashtable<String, EQCell> getPsbCells(int i) {
		return this.psbList.get(i);
	}
	
	public int getMaxGain() {
		int i = this.dimension;
		Log.i("Equilibrium", this.maxGain.toString());
		while (i > 0 && this.maxGain.get(i--) <= 0);
		return (i+1)*2;
	}
	
	public Hashtable<Integer, Integer> getMG() {
		return maxGain;
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
			
			for (int k = -1; k <= 1; k++) {
				for (int l = -1; l <= 1; l++) {	
					if (i+k >= 0 && i+k < dimension && j+l >= 0 && j+l < dimension) {
						int psbSize = board[i+k][j+l].getPsb().size();
						if (board[i+k][j+l].removePsb(v)) {
							maxGain.put(v, maxGain.get(v) - 1);
						}
						int newPsbSize = board[i+k][j+l].getPsb().size();
						if (psbSize > 0) {
							psbList.get(psbSize).remove(Coord.get(i+k, j+l));
						}
						if (!board[i+k][j+l].isSet() && newPsbSize > 0) {
							psbList.get(newPsbSize).put(Coord.get(i+k, j+l), board[i+k][j+l]);
						}
						/*if (newPsbSize != psbSize && psbSize > 0 && psbList.get(psbSize).containsKey(Coord.get(i+k, j+l))) {
							psbList.get(psbSize).remove(Coord.get(i+k, j+l));
							if (!(k == 0 && l == 0) && psbSize > 1) {
								psbList.get(psbSize-1).put(Coord.get(i+k, j+l), board[i+k][j+l]);
							}
						}*/
					}
				}
			}
			
			Vector<Integer> psbij = board[i][j].getPsb();
			for (int k = 1; k <= dimension; k++) {
				if (psbij.contains(k) || k == v) {
					maxGain.put(k, maxGain.get(k) - 1);
				}
			}
			
			rowsSum[i] += board[i][j].getValue();
			columnsSum[j] += board[i][j].getValue();
			
			EQMoves moves = checkBoard();
			if (moves.size() > 0) {
				throw moves;
			}
			
			//Log.i("Equilibrium", maxGain.toString());
		}
		
		if (v == 0) {
			Vector<Integer> psbij = board[i][j].getPsb();
			for (int k = 1; k <= dimension; k++) {
				if (psbij.contains(k) || k == v) {
					maxGain.put(k, maxGain.get(k) - 1);
				}
			}
		}
			
	}
	
	public void insert(EQMoves.EQSingleMove mv) throws EQMoves {
		insert(mv.getValue(), mv.getRow(), mv.getCol());
	}
	
	public void delete(int i, int j)
	{
		int v = Math.abs(board[i][j].getValue());
		if (v > 0){
			
			for (int k = -1; k <= 1; k++) {
				for (int l = -1; l <= 1; l++) {
					if (i+k >= 0 && i+k < dimension && j+l >= 0 && j+l < dimension) {
						int psbSize = board[i+k][j+l].getPsb().size();
						if (board[i+k][j+l].addPsb(v)) {
							maxGain.put(v, maxGain.get(v) + 1);
						}
						int newPsbSize = board[i+k][j+l].getPsb().size();
						// CONTROLLARE!!!
						if (psbSize > 0) {
							psbList.get(psbSize).remove(Coord.get(i+k, j+l));
						}
						if (newPsbSize > 0 && (!board[i+k][j+l].isSet() || (k == 0 && l == 0))) {
							psbList.get(newPsbSize).put(Coord.get(i+k, j+l), board[i+k][j+l]);
						}
					}
				}
			}
			
			Vector<Integer> psbij = board[i][j].getPsb();
			for (int k = 1; k <= dimension; k++) {
				if (psbij.contains(k) || k == v) {
					maxGain.put(k, maxGain.get(k) + 1);
				}
			}
			
			rowsSum[i] -= board[i][j].getValue();
			columnsSum[j] -= board[i][j].getValue();
			
			board[i][j].unsetValue();
			
			//Log.i("Equilibrium", maxGain.toString());
		} else if (v == 0) {
			Vector<Integer> psbij = board[i][j].getPsb();
			for (int k = 1; k <= dimension; k++) {
				if (psbij.contains(k) || k == v) {
					maxGain.put(k, maxGain.get(k) - 1);
				}
			}
		}
	}
	
	public void delete(EQMoves.EQSingleMove mv) {
		delete(mv.getRow(), mv.getCol());
	}
	
	protected EQMoves checkBoard() {
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
		
		//EQBoard newb = new EQBoard(dimension);
		EQBoard newb = null;
		try {
			newb = this.getClass().getConstructor(Integer.TYPE).newInstance(dimension);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
