package com.equilibrium.logic;

import java.util.Random;
import java.util.Vector;

import com.equilibrium.logic.*;

public final class EQAI {
	
	static public EQBoard greedyAlg(EQBoard board, EQPlayer player, EQPlayer opp)
	{
		int best = -1000, diff;
		EQBoard brd = null;
		Vector<EQBoard> bestbrd = new Vector<EQBoard>();
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
						brd.insert(cell.getPsb().get(k), i, j);
					
						diff = opp.getScore(brd) - player.getScore(brd);
						if (diff >= best)
						{
							if (diff > best)
								bestbrd.clear();
							best = diff;
							bestbrd.add((EQBoard) brd.clone());
						}
					}
				}
			}
		
		if (bestbrd.size() == 0)
			return board;

		return bestbrd.get(new Random().nextInt(bestbrd.size()));
	}
}
