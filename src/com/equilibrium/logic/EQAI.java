package com.equilibrium.logic;

import java.util.Random;
import java.util.Vector;

import com.equilibrium.logic.*;

public final class EQAI extends Thread {
	
	static public EQMoves.EQSingleMove greedyAlg(EQBoard board, EQPlayer player, EQPlayer opp)
	{
		int best = -1000, diff;
		EQBoard brd = ((EQBoard)board.clone());
		EQMoves bestMoves = new EQMoves();
		EQCell cell = null;
		for (int i = 0; i < brd.getDimension(); i++)
			for (int j = 0; j < brd.getDimension(); j++)
			{
				cell = brd.get(i, j);
				if (!cell.isSet())
				{
					for (int k = 0; k < cell.getPsb().size(); k++)
					{
						EQMoves.EQSingleMove move = new EQMoves.EQSingleMove(cell.getPsb().get(k), i, j);
						
						try {
							brd.insert(k+1, i, j);
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
