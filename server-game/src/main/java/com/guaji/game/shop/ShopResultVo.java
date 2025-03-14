/**
 * 
 */
package com.guaji.game.shop;



/**
 * @author jht
 * @param <>
 * 
 */
public class ShopResultVo<T> {
	public T t;
	public int errorCode;
	public PlayerShopItem romveShopItem;

	public T getT() {
		return t;
	}

	public void setT(T t) {
		this.t = t;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public PlayerShopItem getRomveShopItem() {
		return romveShopItem;
	}

	public void setRomveShopItem(PlayerShopItem romveShopItem) {
		this.romveShopItem = romveShopItem;
	}


}
