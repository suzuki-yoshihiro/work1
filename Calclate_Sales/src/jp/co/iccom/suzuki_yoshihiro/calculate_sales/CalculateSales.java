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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CalculateSales {
	public static void main(String[] args) throws IOException {

		File folder = new File(args[0]);
		BufferedReader br = null;
		BufferedWriter bw = null;

		/*
		 * 支店情報ファイル読み込み
		 */

		HashMap<String, String> branchMapName = new HashMap<String, String>();// 支店コード・支店名
		HashMap<String, Long> branchMapAmount = new HashMap<String, Long>();	// 支店コード・売上高
		ArrayList<String> branchCodeList = new ArrayList<String>();			// 支店コード用

		try{
			br =  new BufferedReader(new FileReader(new File(folder, "branch.lst")));
			String readLine;

			while((readLine = br.readLine()) != null){
				// カンマ(,)で内容を区切り、一時保存用の配列へ保存
				String[] separatedValues = readLine.split(",");
				Pattern p = Pattern.compile("^\\d{3}$");
				Matcher m = p.matcher(separatedValues[0]);
				if(separatedValues.length != 2 || !m.find()){
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;
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
			return;
		}
		catch(Exception e){		// その他の例外に対する処理
			System.out.println("予期せぬエラーが発生しました");
			e.printStackTrace();
			return;
		}
		finally{
			br.close();
		}


		/*
		 * 商品情報ファイル読み込み
		 */
		HashMap<String, String> commodityMapName = new HashMap<String, String>();
		HashMap<String, Long> commodityMapAmount = new HashMap<String, Long>();
		ArrayList<String> commodityCodeList = new ArrayList<String>();
		try{
			br = new BufferedReader(new FileReader(new File(folder, "commodity.lst")));
			String readLine;
			while((readLine = br.readLine()) != null){
				String[] separatedValues = readLine.split(",");
				Pattern p = Pattern.compile("^\\w{8}$");
				Matcher m = p.matcher(separatedValues[0]);
				// フィールド数の及び商品コードの文字数判定、規定外の場合、エラーメッセージを表示し強制終了
				if(separatedValues.length != 2 || !m.find()){
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return;
				}
				commodityMapName.put(separatedValues[0], separatedValues[1]);	// 商品定義用HashMapへ要素の追加
				commodityMapAmount.put(separatedValues[0], new Long(0));
				commodityCodeList.add(separatedValues[0]);
			}
		}
		catch(FileNotFoundException e){
			// 商品定義ファイルが見つからなかった場合の例外処理
			System.out.println("商品定義ファイルが存在しません");
			return;
		}
		catch(Exception e){
			// その他の例外に対する処理
			System.out.println("予期せぬエラーが発生しました");
			e.printStackTrace();
			return;
		}
		finally{
			br.close();
		}

		/*
		 * 売上レコードファイル読み込み及び集計処理
		 */

		// 引数で指定したディレクトリ内にある"*.rcd"ファイルの一覧を取得

		ArrayList<String> rcdList = new ArrayList<String>();		// レコードファイルのファイル名格納
		String[] filelist = folder.list();							// カレントディレクトリのファイル一覧
		for(int i = 0; i < filelist.length; i++){
			// 名前にrcdを含む8文字( + 拡張子3文字)のファイルを検索
			if(filelist[i].length() == 12 && filelist[i].endsWith(".rcd")){
				// 拡張子がrcdのファイルを検索
				rcdList.add(filelist[i]);
			}
		}

		/*
		 * rcdファイルの名前が連番になっているかどうかを判定、なっていない場合はメッセージを出力し終了
		 * 連番判定はArrayList二格納された文字列の差を求め、その結果が-1以外となっている場合にのみ
		 * エラーを出力するものとする
		 * rcdファイルが1つのみの場合はこの処理をスキップ、0の場合は何もせずプログラムを終了する
		 */
		if(rcdList.size() == 0){
			return;
		}
		if(rcdList.size() >= 2){
			Collections.sort(rcdList);
			for(int i = 0; i < rcdList.size() - 1; i++){
//				if(rcdList.get(i).compareTo(rcdList.get(i + 1)) != -1){
//					System.out.println("売上ファイル名が連番になっていません");
//					return;
//				}
			}
		}
		// rcdファイルの読み込み
		for(int i = 0; i < rcdList.size(); i++){
			try{
				br = new BufferedReader(new FileReader(new File(folder, rcdList.get(i))));
				String readLine;
				ArrayList<String> rcdData = new ArrayList<String>();
				while((readLine = br.readLine()) != null){
					rcdData.add(readLine);
				}
				for(String s : rcdData){
					System.out.println(s);
				}
				if(rcdData.size() != 3){
					System.out.println("<" + folder.getPath() + rcdList.get(i)  + ">のフォーマットが不正です");
					return;
				}

				// 支店別の売上集計処理

					if(branchMapAmount.containsKey(rcdData.get(0))){
						long calcration = branchMapAmount.get(rcdData.get(0));
						calcration += Long.parseLong(rcdData.get(2));
						if(String.valueOf(calcration).length() > 10){		//合計金額の桁数を判定
							System.out.println("合計金額が10桁を超えました");
							return;
						}
						branchMapAmount.put(rcdData.get(0), calcration);
					}else{
						System.out.println("<" + folder.getPath() + rcdList.get(i) + ">の支店コードが不正です");
						return;
					}

				// 商品別の売上集計処理
				if(commodityMapAmount.containsKey(rcdData.get(1))){
					long calcration = commodityMapAmount.get(rcdData.get(1));
					calcration += Long.parseLong(rcdData.get(2));
					if(String.valueOf(calcration).length() > 10){		//合計金額の桁数を判定
						System.out.println("合計金額が10桁を超えました");
						return;
					}
					System.out.println(calcration);
					commodityMapAmount.put(rcdData.get(0), calcration);
				}else{
					System.out.println("<" + folder.getPath() + rcdList.get(i) + ">の商品コードが不正です");
					return;
				}


			}
			catch(Exception e){
				System.out.println("予期せぬエラーが発生しました");
				e.printStackTrace();
				return;
			}
			finally{
			br.close();
			}
		}

		/*
		 * 支店別売上集計後ファイル書き出し
		 */

		ArrayList<Branch> branchList = new ArrayList<Branch>();
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
			e.printStackTrace();
			return;
		}
		finally{
			bw.close();
		}

		/*
		 * 商品別売上集計後ファイル書き出し
		 */

		ArrayList<Commodity> commodityList = new ArrayList<Commodity>();
		try{
			bw = new BufferedWriter(new FileWriter(new File(folder, "commodity.out")));
			for(int i = 0; i < commodityCodeList.size(); i++){
				commodityList.add(new Commodity(
						commodityCodeList.get(i),
						commodityMapName.get(commodityCodeList.get(i)),
						commodityMapAmount.get(commodityCodeList.get(i))));
			}
			for(Commodity c : commodityList){
				System.out.println(c.cCode + "," + c.cName + "," + c.cAmount);
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
			e.printStackTrace();
			return;
		}
		finally{
			bw.close();
		}

	}


}