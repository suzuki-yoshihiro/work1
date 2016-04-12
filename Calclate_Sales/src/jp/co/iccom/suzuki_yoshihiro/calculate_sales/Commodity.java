package jp.co.iccom.suzuki_yoshihiro.calculate_sales;

public class Commodity implements Comparable<Commodity>{
	String cCode, cName;
	long cAmount;

	Commodity(String code, String name, long amount){
		this.cCode = code;
		this.cName = name;
		this.cAmount = amount;
	}

	Commodity() {
		this.cCode = "";
		this.cName = "";
		this.cAmount = 0;
	}

	public int compareTo(Commodity c) {
		return (int)(this.cAmount - c.cAmount);
	}
}