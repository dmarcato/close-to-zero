package com.l1ck.closetozero.logic;
import java.util.Vector;

//import EQBoard.Coord;


public class EQSudokuBoard extends EQBoard {

	public EQSudokuBoard() {
		this(5);
	}
	
	public EQSudokuBoard(int d) {
		super(d);
	}
	
	public void insert(int v, int i, int j) throws EQMoves {
		if (i < 0 || i > dimension || j < 0 || j > dimension)
			return;
		if (board[i][j].setValue(v)) {
			
			for (int r = 0; r < dimension; r++) {
				int psbSize = board[r][j].getPsb().size();
				if (board[r][j].removePsb(v)) {
					maxGain.put(v, maxGain.get(v) - 1);
				}
				int newPsbSize = board[r][j].getPsb().size();
				if (psbSize > 0) {
					psbList.get(psbSize).remove(Coord.get(r, j));
				}
				if (!board[r][j].isSet() && newPsbSize > 0) {
					psbList.get(newPsbSize).put(Coord.get(r, j), board[r][j]);
				}
			}
				
			for (int c = 0; c < dimension; c++) {
				if (c == j) continue;
				int psbSize = board[i][c].getPsb().size();
				if (board[i][c].removePsb(v)) {
					maxGain.put(v, maxGain.get(v) - 1);
				}
				int newPsbSize = board[i][c].getPsb().size();
				if (psbSize > 0) {
					psbList.get(psbSize).remove(Coord.get(i, c));
				}
				if (!board[i][c].isSet() && newPsbSize > 0) {
					psbList.get(newPsbSize).put(Coord.get(i, c), board[i][c]);
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
			
			for (int r = 0; r < dimension; r++) {
				int psbSize = board[r][j].getPsb().size();
				if (board[r][j].addPsb(v)) {
					maxGain.put(v, maxGain.get(v) + 1);
				}
				int newPsbSize = board[r][j].getPsb().size();
				if (psbSize > 0) {
					psbList.get(psbSize).remove(Coord.get(r, j));
				}
				if ((r == i || !board[r][j].isSet()) && newPsbSize > 0) {
					psbList.get(newPsbSize).put(Coord.get(r, j), board[r][j]);
				}
			}
				
			for (int c = 0; c < dimension; c++) {
				if (c == j) continue;
				int psbSize = board[i][c].getPsb().size();
				if (board[i][c].addPsb(v)) {
					maxGain.put(v, maxGain.get(v) + 1);
				}
				int newPsbSize = board[i][c].getPsb().size();
				if (psbSize > 0) {
					psbList.get(psbSize).remove(Coord.get(i, c));
				}
				if (!board[i][c].isSet() && newPsbSize > 0) {
					psbList.get(newPsbSize).put(Coord.get(i, c), board[i][c]);
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

}
