package com.guaji.game.util;

public class Tuple2<F, S> implements Tuple {
	public final F first;
	public final S second;

	public Tuple2(F first, S second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public String toString() {
		return "(" + first + "," + second + ")";
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (!(other instanceof Tuple2)) {
			return false;
		}
		Tuple2<?, ?> obj = (Tuple2<?, ?>) other;
		return first.equals(obj.first) && second.equals(obj.second);
	}
}
