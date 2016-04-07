package jp.co.iccom.suzuki_yoshihiro.calculate_sales;

public class Branch {
	String bCode, bName;
	long bAmount;

	Branch(String code, String name, long amount){
		this.bCode = code;
		this.bName = name;
		this.bAmount = amount;
	}
	public int compareTo(Branch b){
		return (int) (bAmount - this.bAmount);

	}
}
