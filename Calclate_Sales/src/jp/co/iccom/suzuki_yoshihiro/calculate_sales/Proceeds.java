package jp.co.iccom.suzuki_yoshihiro.calculate_sales;

public class Proceeds {
	String bCode;
	long amount;
	String cCode;
	Proceeds(String bCode, String cCode, long amount){
		this.bCode = bCode;
		this.cCode = cCode;
		this.amount = amount;
	}
	
}
