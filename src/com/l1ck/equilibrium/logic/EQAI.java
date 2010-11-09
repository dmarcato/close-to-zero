package com.l1ck.equilibrium.logic;

import java.util.Random;

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
	
	static public EQMoves.EQSingleMove simpleAlg(EQBoard board, EQPlayer player, EQPlayer opp)
	{
		int best = -1000, diff;
		EQBoard brd = ((EQBoard)board.clone());
		EQMoves bestMoves = new EQMoves();
		EQCell cell = null;
		int k = new Random().nextInt(brd.getDimension());
		while (best == -1000) {
			k = k % brd.getDimension() + 1;
			for (int i = 0; i < brd.getDimension(); i++)
				for (int j = 0; j < brd.getDimension(); j++)
				{
					cell = brd.get(i, j);
					if (!cell.isSet() && cell.getPsb().contains(k))
					{
						EQMoves.EQSingleMove move = new EQMoves.EQSingleMove(k, i, j);
						
						try {
							brd.insert(k, i, j);
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
	
	static public EQMoves.EQSingleMove extendedGreedyAlg(EQBoard board, EQPlayer player, EQPlayer opp)
	{
		int best = -1000, diff, plSocreMin = 1000, pltmp;
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
						
						pltmp = player.getScore(brd);
						diff = opp.getScore(brd) - pltmp;
						
						if (diff >= best)
						{
							if (diff > best || pltmp < plSocreMin) {
								bestMoves.clear();
								plSocreMin = pltmp;
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
