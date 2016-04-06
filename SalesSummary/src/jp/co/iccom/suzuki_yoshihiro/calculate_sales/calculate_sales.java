package jp.co.iccom.suzuki_yoshihiro.calculate_sales;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class calculate_sales {
	public static void main(String[] args){
		ArrayList<Branch> branchList = new ArrayList<Branch>();
		ArrayList<Commodity> commodityList = new ArrayList<Commodity>();
//		ArrayList<String> branchData = new ArrayList<String>();
		ArrayList<String> commodityData = new ArrayList<String>();
		
		File file;
		FileReader fr;
		BufferedReader br;
		String[] tmp;
		String s = "";
		int i;
		
		try{
			i = 0;
			file = new File(args[0] + "\\branch.lst");
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			while((s = br.readLine()) != null){
				tmp = s.split(",");
				branchList.add(new Branch(tmp[0], tmp[1]));

			}
			br.close();
			for(Branch b : branchList){
				System.out.println("支店番号：" + b.bCode + "　支店名：" + b.bName);
			}

		}
		catch(Exception e){
			System.out.println(e);
			return;
		}
		
		System.out.println("================================");

		try{
			i = 0;
			file = new File(args[0] + "\\commodity.lst");
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			while((s = br.readLine()) != null){
				tmp = s.split(",");
				commodityList.add(new Commodity(tmp[0], tmp[1]));
			}
			br.close();
			
			for(Commodity c : commodityList){
				System.out.println("商品番号：" + c.cCode + "　商品名：" + c.cName);
			}
		}
		catch(Exception e){
			System.out.println(e);
			return;
		}

		}

		
	}

