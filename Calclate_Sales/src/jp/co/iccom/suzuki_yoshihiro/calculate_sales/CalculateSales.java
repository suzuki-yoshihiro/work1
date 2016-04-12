package jp.co.iccom.suzuki_yoshihiro.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class CalculateSales {

	static boolean readBranchFile(File folder, HashMap<String, String> branchMapName, HashMap<String, Long> branchMapAmount, ArrayList<String> branchCodeList) throws IOException{

		BufferedReader br = null;

		try{
			br =  new BufferedReader(new FileReader(new File(folder, "branch.lst")));
			String readLine;

			while((readLine = br.readLine()) != null){
				// カンマ(,)で内容を区切り、一時保存用の配列へ保存
				String[] separatedValues = readLine.split(",");

				if(separatedValues.length != 2 || !separatedValues[0].matches("^\\d{3}$")){
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return false;
				}
				// 支店コードをキーに、支店名を値として保存
				branchMapName.put(separatedValues[0], separatedValues[1]);
				// 支店コードをキーに、売上金額を値(計算前なので0)として保存
				branchMapAmount.put(separatedValues[0], new Long(0));
				// 支店コードをString型のArrayListに追加
				branchCodeList.add(separatedValues[0]);
			}

		}
		// 支店定義ファイルが見つからなかった場合の処理
		catch(FileNotFoundException e){
			System.out.println("支店定義ファイルが存在しません");
			return false;
		}
		catch(Exception e){		// その他の例外に対する処理
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}
		finally{
			if(br != null)	br.close();
		}
		return true;
	}

	static boolean readCommodityFile(File folder, HashMap<String, String> commodityMapName, HashMap<String, Long> commodityMapAmount, ArrayList<String> commodityCodeList) throws IOException{

		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(new File(folder, "commodity.lst")));
			String readLine;
			while((readLine = br.readLine()) != null){
				String[] separatedValues = readLine.split(",");
				// フィールド数の及び商品コードの文字数判定、規定外の場合、エラーメッセージを表示し強制終了
				if(separatedValues.length != 2 || !separatedValues[0].matches("^\\w{8}$")){
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return false;
				}
				commodityMapName.put(separatedValues[0], separatedValues[1]);	// 商品定義用HashMapへ要素の追加
				commodityMapAmount.put(separatedValues[0], new Long(0));
				commodityCodeList.add(separatedValues[0]);
			}
		}
		catch(FileNotFoundException e){
			// 商品定義ファイルが見つからなかった場合の例外処理
			System.out.println("商品定義ファイルが存在しません");
			return false;
		}
		catch(Exception e){
			// その他の例外に対する処理
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}
		finally{
			if(br != null)	br.close();
		}
		return true;
	}


	static int getRcdList(File folder, ArrayList<String> rcdList){

		String[] filelist = folder.list();							// カレントディレクトリのファイル一覧
		for(int i = 0; i < filelist.length; i++){
			// 名前にrcdを含む8文字( + 拡張子3文字)のファイルを検索
			if(filelist[i].length() == 12 && filelist[i].endsWith(".rcd")){
				// 拡張子がrcdのファイルを検索
				rcdList.add(filelist[i]);
			}
		}
		// mainメソッドに読み込んだ件数を返す
		return rcdList.size();
	}

	static boolean isContinus(ArrayList<String> rcdList){
		if(rcdList.size() == 1) return true;
		for(int i = 0; i < rcdList.size() - 1; i++){
			try{
				if((Integer.parseInt(rcdList.get(i).replaceAll(".rcd", "")) - Integer.parseInt(rcdList.get(i + 1).replaceAll(".rcd", "")))!= -1){
					return false;
				}
			}
			catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	static boolean calculateAmount(File folder, HashMap<String, Long> branchMapAmount, HashMap<String, Long> commodityMapAmount, ArrayList<String> rcdList) throws IOException{
		BufferedReader br = null;
		for(int i = 0; i < rcdList.size(); i++){
			try{
				br = new BufferedReader(new FileReader(new File(folder, rcdList.get(i))));
				String readLine;
				ArrayList<String> rcdData = new ArrayList<String>();
				while((readLine = br.readLine()) != null){
					rcdData.add(readLine);
				}

				if(rcdData.size() != 3){
					System.out.println("<" + folder.getPath() + System.getProperty("file.separator") + rcdList.get(i)  + ">のフォーマットが不正です");
					return false;
				}

				// 支店別の売上集計処理

				if(branchMapAmount.containsKey(rcdData.get(0))){
					long calcration = branchMapAmount.get(rcdData.get(0));
					calcration += Long.parseLong(rcdData.get(2));
					if(String.valueOf(calcration).length() > 10){		//合計金額の桁数を判定
						System.out.println("合計金額が10桁を超えました");
						return false;
					}
					branchMapAmount.put(rcdData.get(0), calcration);
				}else{
					System.out.println("<" + folder.getPath() + System.getProperty("file.separator") + rcdList.get(i) + ">の支店コードが不正です");
					return false;
				}

				// 商品別の売上集計処理
				if(commodityMapAmount.containsKey(rcdData.get(1))){
					long calcration = commodityMapAmount.get(rcdData.get(1));
					calcration += Long.parseLong(rcdData.get(2));
					if(String.valueOf(calcration).length() > 10){		//合計金額の桁数を判定
						System.out.println("合計金額が10桁を超えました");
						return false;
					}
					commodityMapAmount.put(rcdData.get(1), calcration);
				}else{
					System.out.println("<" + folder.getPath() + System.getProperty("file.separator") + rcdList.get(i) + ">の商品コードが不正です");
					return false;
				}
			}
			catch(Exception e){
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
			finally{
				if(br != null)	br.close();
			}
		}
		return true;
	}

	static boolean writeBranchFile(File folder, HashMap<String, String> branchMapName, HashMap<String, Long> branchMapAmount, ArrayList<String> branchCodeList) throws IOException{
		ArrayList<Branch> branchList = new ArrayList<Branch>();
		BufferedWriter bw = null;
		try{
			bw = new BufferedWriter(new FileWriter(new File(folder, "branch.out")));
			for(int i = 0; i < branchCodeList.size(); i++){
				branchList.add(new Branch(branchCodeList.get(i),
						branchMapName.get(branchCodeList.get(i)),
						branchMapAmount.get(branchCodeList.get(i))));

			}
			Collections.sort(branchList);
			Collections.reverse(branchList);

			// ファイルへの書き出し
			for(Branch b : branchList){
				bw.write(b.bCode + "," + b.bName + "," + b.bAmount);
				bw.newLine();
			}
		}
		catch(Exception e){
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}
		finally{
			if(bw != null)	bw.close();
		}
		return true;

	}

	static boolean writeCommodityFile(File folder, HashMap<String, String> commodityMapName, HashMap<String, Long> commodityMapAmount, ArrayList<String> commodityCodeList) throws IOException{
		ArrayList<Commodity> commodityList = new ArrayList<Commodity>();
		BufferedWriter bw = null;
		try{
			bw = new BufferedWriter(new FileWriter(new File(folder, "commodity.out")));
			for(int i = 0; i < commodityCodeList.size(); i++){
				commodityList.add(new Commodity(
						commodityCodeList.get(i),
						commodityMapName.get(commodityCodeList.get(i)),
						commodityMapAmount.get(commodityCodeList.get(i))));
			}


			Collections.sort(commodityList);
			Collections.reverse(commodityList);

			// ファイルへの書き出し
			for(Commodity c : commodityList){
				bw.write(c.cCode + "," + c.cName + "," + c.cAmount);
				bw.newLine();
			}
		}
		catch(Exception e){
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}
		finally{
			if(bw != null)	bw.close();
		}
		return true;
	}


	public static void main(String[] args) {

		File folder = new File(args[0]);


		/*
		 * 支店情報ファイル読み込み
		 */

		HashMap<String, String> branchMapName = new HashMap<String, String>();// 支店コード・支店名
		HashMap<String, Long> branchMapAmount = new HashMap<String, Long>();	// 支店コード・売上高
		ArrayList<String> branchCodeList = new ArrayList<String>();			// 支店コード用
		try{
			if(!readBranchFile(folder ,branchMapName, branchMapAmount, branchCodeList))	return;
		}
		catch(Exception e){
			System.out.print("予期せぬエラーが発生しました");
		}

		/*
		 * 商品情報ファイル読み込み
		 */
		HashMap<String, String> commodityMapName = new HashMap<String, String>();
		HashMap<String, Long> commodityMapAmount = new HashMap<String, Long>();
		ArrayList<String> commodityCodeList = new ArrayList<String>();

		try{
			if(!readCommodityFile(folder, commodityMapName, commodityMapAmount, commodityCodeList))	return;
		}
		catch(Exception e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

		/*
		 * 売上レコードファイル読み込み及び集計処理
		 */

		ArrayList<String> rcdList = new ArrayList<String>();

		// 引数で指定したディレクトリ内にある"*.rcd"ファイルの一覧を取得
		// rcdファイルの一覧を読み込み、内容が0件だった場合は何もせずに終了する
		if(getRcdList(folder, rcdList) == 0)	return;

		// rcdファイルのファイル名が連続しているかどうかを判定
		if(!isContinus(rcdList)){
			System.out.println("売上ファイル名が連番になっていません");
			return;
		}

		// rcdファイルの集計作業
		try{
			if(!calculateAmount(folder, branchMapAmount, commodityMapAmount, rcdList))	return;
		}
		catch(Exception e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

		 // 支店別売上集計後ファイル書き出し
		try{
			if(!writeBranchFile(folder, branchMapName, branchMapAmount, branchCodeList))	return;
		}
		catch(Exception e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

		// 商品別売上集計後ファイル書き出し
		try{
			if(!writeCommodityFile(folder, commodityMapName, commodityMapAmount, commodityCodeList))	return;
		}
		catch(Exception e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
	}
}