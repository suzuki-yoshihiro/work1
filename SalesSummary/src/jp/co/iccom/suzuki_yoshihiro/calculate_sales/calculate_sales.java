package jp.co.iccom.suzuki_yoshihiro.calculate_sales;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class calculate_sales {
	public static void main(String[] args){
		ArrayList<Branch> branchList = new ArrayList<Branch>();
		ArrayList<Commodity> commodityList = new ArrayList<Commodity>();
		ArrayList<String> branchData = new ArrayList<String>();
		ArrayList<String> commodityData = new ArrayList<String>();
		
		File file;
		FileReader fr;
		BufferedReader br;
		String[] tmp;
		String s = "";
		int i, j;
		
		try{
			i = 0;
			file = new File(args[0] + "\\branch.lst");
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			while((s = br.readLine()) != null){
				tmp = s.split(",");
				branchList.add(new Branch(Integer.parseInt(tmp[0]), tmp[1]));
				System.out.println(branchList.get(i));
				i++;
			}
			br.close();
		}
		catch(Exception e){
			System.out.println(e);
			return;
		}

		try{
			i = 0;
			file = new File(args[0] + "\\commodity.lst");
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			while((s = br.readLine()) != null){
				commodityData.add(s);
				System.out.println(commodityData.get(i));
				i++;
			}
			br.close();
		}
		catch(Exception e){
			System.out.println(e);
			return;
		}

		}

		
	}

