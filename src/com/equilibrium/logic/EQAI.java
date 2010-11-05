package com.equilibrium.logic;

import java.util.Random;
import java.util.Vector;

import com.equilibrium.logic.*;

public final class EQAI extends Thread {
	
	static public EQMoves.EQSingleMove greedyAlg(EQBoard board, EQPlayer player, EQPlayer opp)
	{
		int best = -1000, diff;
		EQBoard brd = null;
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
						brd = ((EQBoard)board.clone());
						EQMoves.EQSingleMove move = new EQMoves.EQSingleMove(cell.getPsb().get(k), i, j);
						try {
							brd.insert(move);
						} catch (EQMoves m) {}
					
						diff = opp.getScore(brd) - player.getScore(brd);
						if (diff >= best)
						{
							if (diff > best) {
								bestMoves.clear();
							}
							best = diff;
							bestMoves.add(move);
						}
					}
				}
			}
		
		if (bestMoves.size() == 0) {
			return null;
		}

		return bestMoves.get(new Random().nextInt(bestMoves.size()));
	}
}
