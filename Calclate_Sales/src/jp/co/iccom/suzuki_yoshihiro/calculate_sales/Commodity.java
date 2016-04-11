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
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public int compareTo(Commodity c) {
		System.out.println(this.cAmount + c.cAmount + (this.cAmount - c.cAmount));
		return (int)(this.cAmount - c.cAmount);
	}
}