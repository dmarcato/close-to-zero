package com.l1ck.equilibrium.logic;

import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import android.util.Log;

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
		int best = -1000, diff, plScoreMin = 1000, pltmp;
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
							brd.insert(move);
						} catch (EQMoves m) {}
						
						pltmp = player.getScore(brd);
						diff = opp.getScore(brd) - pltmp;
						
						if (diff >= best) {
							if (pltmp < plScoreMin || diff > best) {
								bestMoves.clear();
								plScoreMin = pltmp;
							}
							if (!(pltmp > plScoreMin))
								bestMoves.add(move);
							best = diff;
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
	
	static public EQMoves.EQSingleMove smartAlg(EQBoard board, EQPlayer player, EQPlayer opp) {
		int maxGain = board.getMaxGain();
		int lvl = board.getDimension();
		int best = -1000, diff, plScoreMin = 1000, pltmp;
		EQBoard brd = ((EQBoard)board.clone());
		EQMoves bestMoves = new EQMoves();
		EQCell cell = null;
		int initialDiff = opp.getScore(brd) - player.getScore(brd);
		Log.i("Equilibrium", String.valueOf(maxGain));
		Log.i("Equilibrium", String.valueOf(initialDiff));
		for (int i = 1; i <= brd.getDimension(); i++) {
			Log.i("Equilibrium", brd.getPsbCells(i).values().toString());
		}
		while (lvl > 0) {
			Vector<EQCell> psbs = new Vector<EQCell>(brd.getPsbCells(lvl).values());
			for (int i = 0; i < psbs.size(); i++) {
				
				cell = psbs.get(i);
				for (int k = 0; k < cell.getPsb().size(); k++)
				{
					EQMoves.EQSingleMove move = new EQMoves.EQSingleMove(cell.getPsb().get(k), cell.getRow(), cell.getCol());
					
					try {
						brd.insert(move);
					} catch (EQMoves m) {}
					
					pltmp = player.getScore(brd);
					diff = opp.getScore(brd) - pltmp - initialDiff;
					
					if (diff == maxGain) {
						return move;
					} else {
						
						diff = opp.getScore(brd) - pltmp;
						if (diff >= best) {
							if (pltmp < plScoreMin || diff > best) {
								bestMoves.clear();
								plScoreMin = pltmp;
							}
							if (!(pltmp > plScoreMin))
								bestMoves.add(move);
							best = diff;
						}
					
					}
					brd.delete(cell.getRow(), cell.getCol());
				}
				
			}
			
			lvl--;
		}
		
		if (bestMoves.size() == 0) {
			return null;
		}
		
		int min = 1000, tmpmin, imin = 0;
		EQMoves.EQSingleMove mv;
		for (int i = 0; i < bestMoves.size(); i++)
		{
			try {
				brd.insert(bestMoves.get(i));
			} catch (EQMoves m) {}
			
			mv = extendedGreedyAlg(brd, opp, player);
			if (mv != null) {
				try {
					brd.insert(mv);
				} catch (EQMoves m) {}
				
				tmpmin =  player.getScore(brd) - opp.getScore(brd);
				if (tmpmin < min) {
					imin = i;
				}
				
				brd.delete(mv.getRow(), mv.getCol());
			}
			brd.delete(bestMoves.get(i).getRow(), bestMoves.get(i).getCol());
		}
		
		return bestMoves.get(imin);
	}
}
