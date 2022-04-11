package saka1029.tensuhyo.dictionary;

import java.util.List;

public interface Dict<T> {

	void add(T word);
	List<Found<T>> encode(String s, String... types);
	Acc<T> acc();
	void acc(Acc<T> acc);

}
