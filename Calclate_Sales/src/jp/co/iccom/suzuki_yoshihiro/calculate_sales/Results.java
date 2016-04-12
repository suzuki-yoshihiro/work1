package jp.co.iccom.suzuki_yoshihiro.calculate_sales;

public class Results implements Comparable<Results> {
	String Code, Name;
	long Amount;

	Results(String code, String name, long amount){
		this.Code = code;
		this.Name = name;
		this.Amount = amount;
	}


	public int compareTo(Results r) {

		return (int) (this.Amount - r.Amount);
	}
}
