package jp.co.iccom.suzuki_yoshihiro.calculate_sales;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class calculate_sales {
	public static void main(String[] args){
		ArrayList<Branch> branchList = new ArrayList<Branch>();
		ArrayList<Commodity> commodityList = new ArrayList<Commodity>();
		ArrayList<Proceeds> proceedsList = new ArrayList<Proceeds>();
	
		File file;
		FileReader fr;
		BufferedReader br;
		String[] tmp;
		String s = "";
		String ls = System.getProperty("line.separator");
		String fs = System.getProperty("file.separator");

		String[] rcd = new String[100];
		File folder = new File(args[0]);
		String[] filelist = folder.list();


		
		
		try{

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

		int j = 0;
		for(int i = 0; i < filelist.length; i++){
			
			if(filelist[i].contains("rcd")){
				rcd[j] = filelist[i];
				j++;
//				System.out.println(rcd[i]);
			}
		}
		
		try{
			for(int i = 0; i <= j; i++){

				System.out.println(args[0] + fs + rcd[i]);
				file = new File(args[0] + fs + rcd[i]);
				fr = new FileReader(file);
				br = new BufferedReader(fr);
				while((s = br.readLine()) != null){
					tmp = s.split(ls);
					proceedsList.add(new Proceeds(tmp[0], tmp[1], Long.parseLong(tmp[2])));
				}
				br.close();
				for(Proceeds p : proceedsList){
					System.out.println("支店番号：" + p.bCode + "　商品番号：" + p.bCode + "　売上金額：" + p.amount);
				}
			}
		}
		
		catch(Exception e){
			System.out.println(e);
			return;
		}
	}

		
}

