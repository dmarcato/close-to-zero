package com.equilibrium.logic;

import java.util.Vector;

public class EQCell implements Cloneable {
	private int value = 0;
	private int max = 5;
	private int row = 0, column = 0;
	private Vector<Integer> possibilities = new Vector<Integer>();
	private boolean plus = true;
	private boolean setted = false;
	
	public EQCell(int i, int j) {
		this(5, i, j);
	}
	
	public EQCell(int m, int i, int j) {
		max = m;
		row = i;
		column = j;
		plus = ((i+j)%2 == 0);
		for (int k = 1; k <= max; k++)
			possibilities.add(0);
	}
	
	public int getRow() { return row; }
	public int getCol() { return column; }
	
	public Vector<Integer> getPsb() {
		Vector<Integer> psb = new Vector<Integer>();
		for (int i = 0; i < possibilities.size(); i++)
			if (possibilities.get(i) == 0)
				psb.add(i+1);
		return psb; 
	}
	
	public boolean isSet() { return setted; }
	
	public boolean getSign() { return plus; }
	public int getValue() { return value; }
	
	public boolean setValue(Integer v) { 
		if (setted || (v > 0 && possibilities.get(v-1) > 0)) 
			return false;
		value = v;
		if (!plus) value *= -1;
		setted = true;
		return v != 0;
	}
	
	public void unsetValue() {
		//if (setted && value != 0)
			//possibilities.add(value);
		setted = false;
		value = 0;
	}
	
	public Object clone()
	{
		EQCell cell = new EQCell(max, row, column);
		cell.possibilities = this.possibilities;
		if (setted)
			cell.setValue(value);
		return cell;
	}
	
	public void addPsb(int v)
	{
		possibilities.set(v-1, possibilities.get(v-1)-1);
		if (value == 0 && setted && !getPsb().isEmpty()) {
			unsetValue();
		}
	}
	
	public void removePsb(int v)
	{
		possibilities.set(v-1, possibilities.get(v-1)+1);
	}
	
	public String toString() {
		if (setted) {
			if (plus) 
				return "+"+((Integer)value).toString();
			else 
				return ((Integer)value).toString();
		} else {
			if (plus)
				return "+";
			else	
				return "-";
		}
	}
}
