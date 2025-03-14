package com.guaji.game.util;

public class Tuple3<F, S, T> extends Tuple2<F, S> {

	public final T third;

	public Tuple3(F first, S second, T third) {
		super(first, second);
		this.third = third;
	}

	@Override
	public String toString() {
		return "(" + first + "," + second + "," + third + ")";
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (!(other instanceof Tuple3)) {
			return false;
		}
		Tuple3<?, ?, ?> obj = (Tuple3<?, ?, ?>) other;
		return super.equals(other) && third.equals(obj.third);
	}
}
