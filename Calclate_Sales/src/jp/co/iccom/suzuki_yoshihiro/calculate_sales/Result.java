package jp.co.iccom.suzuki_yoshihiro.calculate_sales;

public class Result implements Comparable<Result> {
	String code, name;
	long amount;

	Result(String code, String name, long amount){
		this.code = code;
		this.name = name;
		this.amount = amount;
	}


	public int compareTo(Result r) {
		return (int) (this.amount - r.amount);
	}
}
