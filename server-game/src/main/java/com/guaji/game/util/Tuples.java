package com.guaji.game.util;

public class Tuples {

	public static <F, S> Tuple2<F, S> tuple(F first, S second) {
		return new Tuple2<F, S>(first, second);
	}

	public static <F, S, T> Tuple3<F, S, T> tuple(F first, S second, T third) {
		return new Tuple3<F, S, T>(first, second, third);
	}

}
