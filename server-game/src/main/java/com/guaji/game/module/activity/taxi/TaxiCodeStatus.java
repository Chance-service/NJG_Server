package com.guaji.game.module.activity.taxi;

public class TaxiCodeStatus {
	private String myTaxiCode;
	private boolean isExchange;

	public TaxiCodeStatus(){
		myTaxiCode = "";
		isExchange = false;
	}

	public String getMyTaxiCode() {
		return myTaxiCode;
	}

	public void setMyTaxiCode(String myTaxiCode) {
		if(!myTaxiCode.equals("")){
			this.myTaxiCode = myTaxiCode;
			this.isExchange = true;
		}
	}

	public boolean isExchange() {
		return isExchange;
	}
}
