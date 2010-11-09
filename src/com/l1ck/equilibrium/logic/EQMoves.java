package com.l1ck.equilibrium.logic;

import java.util.Vector;

@SuppressWarnings("serial")
public class EQMoves extends Exception {

	public static class EQSingleMove {
		private int row;
		private int col;
		private int value;
		
		public EQSingleMove(int v, int r, int c) {
			this.setRow(r);
			this.setCol(c);
			this.setValue(v);
		}

		public void setRow(int row) {
			this.row = row;
		}

		public int getRow() {
			return row;
		}

		public void setCol(int col) {
			this.col = col;
		}

		public int getCol() {
			return col;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}
	
	private Vector<EQSingleMove> moveList;
	
	public EQMoves() {
		moveList = new Vector<EQSingleMove>();
	}
	
	public EQMoves(EQSingleMove mv) {
		this();
		moveList.add(mv);
	}
	
	public int size() {
		return moveList.size();
	}
	
	public EQSingleMove get(int i) {
		return moveList.get(i);
	}
	
	public void add(EQSingleMove mv) {
		moveList.add(mv);
	}
	
	public void clear() {
		moveList.clear();
	}

}
