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

	static boolean readFile(File folder, HashMap<String, String> MapName, HashMap<String, Long> MapAmount, ArrayList<String> CodeList, ArrayList<String> dataTypeList) throws IOException{

		BufferedReader br = null;

		try{
			br =  new BufferedReader(new FileReader(new File(folder, dataTypeList.get(0) + ".lst")));
			String readLine;
			String pattern;

			// 第5引数の値を基に読み込むファイルの種別にあわせたマッチャーパターンを格納
			if(dataTypeList.get(0).equals("branch")){
				pattern = "^\\d{3}$";
			}else{
				pattern = "^\\w{8}$";
			}
			while((readLine = br.readLine()) != null){
				// カンマ(,)で内容を区切り、一時保存用の配列へ保存
				String[] separatedValues = readLine.split(",");

				if(separatedValues.length != 2 || !separatedValues[0].matches(pattern)){
					System.out.println(dataTypeList.get(1) + "定義ファイルのフォーマットが不正です");
					return false;
				}
				// 支店/商品コードをキーに、支店/商品名を値として保存
				MapName.put(separatedValues[0], separatedValues[1]);
				// 支店/商品コードをキーに、売上金額を値(計算前なので0)として保存
				MapAmount.put(separatedValues[0], new Long(0));
				// 支店/商品コードをString型のArrayListに追加
				CodeList.add(separatedValues[0]);
			}

		}
		// 定義ファイルが見つからなかった場合の処理
		catch(FileNotFoundException e){
			System.out.println(dataTypeList.get(1)  + "定義ファイルが存在しません");
			return false;
		}
		catch(Exception e){
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}
		finally{
			if(br != null)	br.close();
		}
		return true;
	}

	static int getRcdList(File folder, ArrayList<String> rcdList){

		File[] filelist = folder.listFiles();
		for(int i = 0; i < filelist.length; i++){
			// 名前にrcdを含む8文字( + 拡張子3文字)のファイルを検索
			if(filelist[i].length() == 12 && filelist[i].toString().endsWith(".rcd") && filelist[i].isFile()){
				// 拡張子がrcdのファイルを検索
				rcdList.add(filelist[i].toString());
			}
		}
		// mainメソッドに読み込んだ件数を返す
		return rcdList.size();
	}

	static boolean isContinus(ArrayList<String> rcdList){
		if(rcdList.size() == 1) return true;
		for(int i = 0; i < rcdList.size() - 1; i++){
			try{
				if((Integer.parseInt(rcdList.get(i).substring(0, 8)) - Integer.parseInt(rcdList.get(i + 1).substring(0, 8)))!= -1){
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
					System.out.println("<" +rcdList.get(i)  + ">のフォーマットが不正です");
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
					System.out.println("<" + rcdList.get(i) + ">の支店コードが不正です");
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
					System.out.println("<" + rcdList.get(i) + ">の商品コードが不正です");
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

	static boolean writeFile(File folder, HashMap<String, String> MapName, HashMap<String, Long> MapAmount, ArrayList<String> CodeList, String dataType) throws IOException{
		ArrayList<Results> resultsList = new ArrayList<Results>();
		BufferedWriter bw = null;
		try{
			bw = new BufferedWriter(new FileWriter(new File(folder,  dataType + ".out")));
			for(int i = 0; i < CodeList.size(); i++){
				resultsList.add(new Results(CodeList.get(i),
						MapName.get(CodeList.get(i)),
						MapAmount.get(CodeList.get(i))));

			}
			Collections.sort(resultsList);
			Collections.reverse(resultsList);

			// ファイルへの書き出し
			for(Results r : resultsList){
				bw.write(r.Code + "," + r.Name + "," + r.Amount);
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
			ArrayList<String> dataType = new ArrayList<String>();
			dataType.add(0, "branch");
			dataType.add(1, "支店");
			if(!readFile(folder ,branchMapName, branchMapAmount, branchCodeList, dataType))	return;
		}
		catch(Exception e){
			System.out.println("予期せぬエラーが発生しました");
			e.printStackTrace();
		}

		/*
		 * 商品情報ファイル読み込み
		 */
		HashMap<String, String> commodityMapName = new HashMap<String, String>();
		HashMap<String, Long> commodityMapAmount = new HashMap<String, Long>();
		ArrayList<String> commodityCodeList = new ArrayList<String>();

		try{
			ArrayList<String> dataType = new ArrayList<String>();
			dataType.add(0, "commodity");
			dataType.add(1, "商品");
			if(!readFile(folder, commodityMapName, commodityMapAmount, commodityCodeList, dataType))	return;
		}
		catch(Exception e){
			System.out.println("予期せぬエラーが発生しました");
			e.printStackTrace();
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
			if(!writeFile(folder, branchMapName, branchMapAmount, branchCodeList, "branch"))	return;
		}
		catch(Exception e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

		// 商品別売上集計後ファイル書き出し
		try{
			if(!writeFile(folder, commodityMapName, commodityMapAmount, commodityCodeList, "commodity"))	return;
		}
		catch(Exception e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
	}
}