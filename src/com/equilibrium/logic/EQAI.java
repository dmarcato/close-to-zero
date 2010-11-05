package com.equilibrium.logic;

import java.util.Random;
import java.util.Vector;

import com.equilibrium.logic.*;

public final class EQAI extends Thread {
	
	static public EQMoves.EQSingleMove greedyAlg(EQBoard board, EQPlayer player, EQPlayer opp)
	{
		int best = -1000, diff;
		int scoreP1, scoreP2, tmpScorer1, tmpScorer2, tmpScorec1, tmpScorec2;
		EQBoard brd = ((EQBoard)board.clone());
		EQMoves bestMoves = new EQMoves();
		EQCell cell = null;
		for (int i = 0; i < board.getDimension(); i++)
			for (int j = 0; j < board.getDimension(); j++)
			{
				cell = (EQCell) board.get(i, j).clone();
				if (!cell.isSet())
				{
					for (int k = 0; k < cell.getPsb().size(); k++)
					{
						scoreP1 = scoreP2 = 0;
						EQMoves.EQSingleMove move = new EQMoves.EQSingleMove(cell.getPsb().get(k), i, j);
						
						brd.get(i, j).setValue(k);
					
						for (int r = 0; r < brd.getDimension(); r++) {
							tmpScorer1 = tmpScorer2 = tmpScorec1 = tmpScorec2 = 0;
							for (int c = 0; c < brd.getDimension(); c++) {
								if (player.isMineRow(r)) {
									tmpScorer1 += brd.get(r, c).getValue();
								} else {
									tmpScorer2 += brd.get(r, c).getValue();
								}
								if (player.isMineCol(r)) {
									tmpScorec1 += brd.get(c, r).getValue();
								} else {
									tmpScorec2 += brd.get(c, r).getValue();
								}
							}
							scoreP1 += Math.abs(tmpScorer1) + Math.abs(tmpScorec1);
							scoreP2 += Math.abs(tmpScorer2) + Math.abs(tmpScorec2);
						}
							
						diff = scoreP2 - scoreP1;
						if (diff >= best)
						{
							if (diff > best) {
								bestMoves.clear();
							}
							best = diff;
							bestMoves.add(move);
						}
						brd.delete(i, j);
					}
				}
			}
		
		if (bestMoves.size() == 0) {
			return null;
		}

		return bestMoves.get(new Random().nextInt(bestMoves.size()));
	}
}
