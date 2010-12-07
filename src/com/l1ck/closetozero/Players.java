package com.l1ck.closetozero;

import com.l1ck.closetozero.logic.EQPlayer;

public class Players {

	private EQPlayer p1 = null;
	private EQPlayer p2 = null;
	private EQPlayer current = null;
	
	public Players(EQPlayer p, EQPlayer q) {
		this.p1 = p;
		this.p2 = q;
		this.current = p1;
	}
	
	public void setP1(EQPlayer p) {
		this.p1 = p;
	}
	
	public void setP2(EQPlayer p) {
		this.p2 = p;
	}

	public void next() {
		if (this.current.equals(this.p1)) {
			this.current = this.p2;
		} else {
			this.current = this.p1;
		}
	}
	
	public EQPlayer get() {
		return this.current;
	}
	
	public EQPlayer get(int i) {
		switch (i) {
		default:
		case 1:
			return this.p1;
		case 2:
			return this.p2;
		}
	}
	
	public EQPlayer getOther() {
		if (this.current.equals(this.p1)) {
			return this.p2;
		} else {
			return this.p1;
		}
	}
	
	public boolean isBothBot() {
		return this.p1.isBot() && this.p2.isBot();
	}

}
