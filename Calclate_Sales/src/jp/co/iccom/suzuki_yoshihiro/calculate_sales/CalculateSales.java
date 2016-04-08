package jp.co.iccom.suzuki_yoshihiro.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class CalculateSales {
	public static void main(String[] args) throws IOException{


		/*
		 * 変数宣言
		 */

		// Fileクラス
		File folder = new File(args[0]);							// フォルダ操作用

		// 文字列型
		String reader = "";											// ファイルからの情報読み込み用

		// 文字列型配列
		String[] separatedValues;									// ファイルから読み込んだ情報を一時的に保管する

		// 整数型

		int i, j, k;

		/*
		 * 支店情報ファイル読み込み
		 */

		HashMap<String, String> branchMapIn = new HashMap<String, String>();// 支店コード・支店名
		HashMap<String, Long> branchMapOut = new HashMap<String, Long>();	// 支店コード・売上高
		ArrayList<String> branchCodeList = new ArrayList<String>();			// 支店コード用
		File fileBranchIn = new File(folder, "branch.lst");								// 第一引数のパスと区切り文字、ファイル名
		BufferedReader brBranchRead = new BufferedReader(new FileReader(fileBranchIn));
		try{
			while((reader = brBranchRead.readLine()) != null){		// カンマ(,)で内容を区切り、一時保存用の配列へ保存
				separatedValues = reader.split(",");

				// フィールド数及び支店番号の判定、3以上の場合、エラーメッセージを表示し強制終了
				if(separatedValues.length >= 3 || separatedValues[0].length() != 3 ||
						separatedValues[0].getBytes().length != separatedValues[0].length()){
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;
				}
				Integer.parseInt(separatedValues[0]);		// 支店コードに当たる部分の文字を数字に変換し、エラーを吐くか調べる
				branchMapIn.put(separatedValues[0], separatedValues[1]);		// 支店コードをキーに、支店名を値として保存
				// 支店コードをキーに、売上金額を値(計算前なので0)として保存
				branchMapOut.put(separatedValues[0], new Long(0));
				branchCodeList.add(separatedValues[0]);		// 支店コードをString型のArrayListに追加
			}

		}
		catch(FileNotFoundException e){		// 支店定義ファイルが見つからなかった場合の処理
			System.out.println("支店定義ファイルが存在しません");
			return;
		}
		catch(NumberFormatException e){		// 支店番号の形式が不正だった場合
			System.out.println("支店定義ファイルのフォーマットが不正です");
			return;
		}
		catch(Exception e){		// その他の例外に対する処理
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		finally{
			brBranchRead.close();
		}


		/*
		 * 商品情報ファイル読み込み
		 */

		HashMap<String, String> commodityMapIn = new HashMap<String, String>();			// 商品コード・商品名
		HashMap<String, Long> commodityMapOut = new HashMap<String, Long>();			// 商品コード・売上高
		ArrayList<String> commodityCodeList = new ArrayList<String>();					// 商品コード用
		File fileCommodityIn = new File(folder, "commodity.lst");
		BufferedReader brCommodityRead = new BufferedReader(new FileReader(fileCommodityIn));
		try{


			while((reader = brCommodityRead.readLine()) != null){
				separatedValues = reader.split(",");
				// フィールド数の及び商品コードの文字数判定、規定以上の場合、エラーメッセージを表示し強制終了
				if(separatedValues.length >= 3 || separatedValues[0].length() != 8 ||
						separatedValues[0].getBytes().length != separatedValues[0].length()){
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return;
				}
				commodityMapIn.put(separatedValues[0], separatedValues[1]);	// 商品定義用HashMapへ要素の追加
				commodityMapOut.put(separatedValues[0], new Long(0));
				commodityCodeList.add(separatedValues[0]);
			}
		}
		catch(FileNotFoundException e){		// 商品定義ファイルが見つからなかった場合の例外処理
			System.out.println("商品定義ファイルが存在しません");
			return;
		}
		catch(Exception e){		// その他の例外に対する処理
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		finally{
			brCommodityRead.close();
		}

		/*
		 * 売上レコードファイル読み込み及び集計処理
		 */

		// 引数で指定したディレクトリ内にある"*.rcd"ファイルの一覧を取得

		ArrayList<String> rcdList = new ArrayList<String>();						// レコードファイルのファイル名格納
		String[] filelist = folder.list();							// カレントディレクトリのファイル一覧
		for(i = 0; i < filelist.length; i++){
			if(filelist[i].contains("rcd")){
				rcdList.add(filelist[i]);
			}
		}

		// rcdファイルの名前が連番になっているかどうかを判定、なっていない場合はメッセージを出力し終了
		for(i = 0; i < rcdList.size() - 1; i++){
			if(Math.abs(rcdList.get(i).compareTo(rcdList.get(i + 1))) != 1){
				System.out.println("売上ファイル名が連番になっていません");
				return;
			}
		}
		// rcdファイルの読み込み
		for(i = 0; i < rcdList.size(); i++){
			File fileRcdIn = new File(folder, rcdList.get(i));
			BufferedReader brRcdRead = new BufferedReader(new FileReader(fileRcdIn));
			try{
				k = 0;
				separatedValues = new String[3];
				while((reader = brRcdRead.readLine()) != null){

					// rcdファイルの行数を調べ、4行以上あった場合はエラーメッセージを表示し終了
					if(k >= 4){
						System.out.println("<" + fileRcdIn.getPath() + ">のフォーマットが不正です");
						return;
					}
					separatedValues[k] = reader;
					k++;
				}
				boolean bflg = false;
				boolean cflg = false;

				// 支店別の売上集計処理
				for(k = 0; k < branchCodeList.size(); k++){
					if(separatedValues[0].equals(branchCodeList.get(k))){
						long calcration = branchMapOut.get(separatedValues[0]);
						calcration += Long.parseLong(separatedValues[2]);
						if(String.valueOf(calcration).length() > 10){		//合計金額の桁数を判定
							System.out.println("合計金額が10桁を超えました");
							return;
						}
						branchMapOut.put(separatedValues[0], calcration);
						bflg = true;
					}
				}
				for(k = 0; k < commodityCodeList.size(); k++){

					if(separatedValues[1].equals(commodityCodeList.get(k))){
						long calcration = commodityMapOut.get(separatedValues[1]);
						calcration += Long.parseLong(separatedValues[2]);
						if(String.valueOf(calcration).length() > 10){		//合計金額の桁数を判定
							System.out.println("合計金額が10桁を超えました");
							return;
						}
						commodityMapOut.put(separatedValues[1], calcration);
						cflg = true;
					}
				}

				/*
				 * 以下は、当該データが見当たらなかった場合に行う
				 * エラーメッセージ出力処理
				 */

				if(!bflg){
					System.out.println("<" + fileRcdIn.getPath() + ">の支店コードが不正です");
					return;
				}
				if(!cflg){
					System.out.println("<" + fileRcdIn.getPath() + ">の商品コードが不正です");
					return;
				}
			}
			catch(Exception e){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
			finally{
			brRcdRead.close();
			}
		}

		/*
		 * 支店別売上集計後ファイル書き出し
		 */

		File fileBranchOut = new File(folder, "branch.out");
		BufferedWriter bwBranchWrite = new BufferedWriter(new FileWriter(fileBranchOut));
		Branch[] branchArry = new Branch[branchCodeList.size()];
		try{
			for(i = 0; i < branchCodeList.size(); i++){
				branchArry[i] = new Branch(
						branchCodeList.get(i),
						branchMapIn.get(branchCodeList.get(i)),
						branchMapOut.get(branchCodeList.get(i))
						);
			}

			// 単純選択法による金額順の並べ替え
			for(i = 0; i < branchArry.length -1; i++){
				for(j = i + 1; j < branchArry.length; j++){
					if(branchArry[i].bAmount < branchArry[j].bAmount){
						Branch branchTmp = new Branch();
						branchTmp = branchArry[i];
						branchArry[i] = branchArry[j];
						branchArry[j] = branchTmp;
					}
				}
			}
			i = 0;
			for(Branch b : branchArry){
				bwBranchWrite.write(b.bCode + "," + b.bName + "," + b.bAmount + System.getProperty("line.separator"));
				System.out.println(b.bCode + "," + b.bName + "," + b.bAmount + System.getProperty("line.separator"));
				i++;
			}
		}
		catch(Exception e){
			System.out.println("予期せぬエラーが発生しました");
			e.printStackTrace();
		}
		finally{
			bwBranchWrite.close();
		}

		/*
		 * 商品別売上集計後ファイル書き出し
		 */
		BufferedWriter bwCommodityWrite = new BufferedWriter(new FileWriter(new File(folder, "commodity.out")));
		Commodity[] commodityArry = new Commodity[commodityCodeList.size()];
		try{
			for(i = 0; i < commodityCodeList.size(); i++){
				commodityArry[i] = new Commodity(
						commodityCodeList.get(i),
						commodityMapIn.get(commodityCodeList.get(i)),
						commodityMapOut.get(commodityCodeList.get(i))
						);
			}

			// 単純選択法による金額降順ソート
			for(i = 0; i < commodityArry.length -1; i++){
				for(j = i + 1; j < commodityArry.length; j++){
					if(commodityArry[i].cAmount < commodityArry[j].cAmount){
						Commodity commodityTmp = new Commodity();
						commodityTmp = commodityArry[i];
						commodityArry[i] = commodityArry[j];
						commodityArry[j] = commodityTmp;
					}
				}
			}
			i = 0;
			for(Commodity c : commodityArry){
				System.out.println("書き込み" + (i + 1) + "行目");
				bwCommodityWrite.write(c.cCode + "," + c.cName + "," + c.cAmount + System.getProperty("line.separator"));
				System.out.println(c.cCode + "," + c.cName + "," + c.cAmount + System.getProperty("line.separator"));
				i++;
			}
		}
		catch(Exception e){
			System.out.println("予期せぬエラーが発生しました");
			e.printStackTrace();
		}
		finally{
			bwCommodityWrite.close();
		}

	}

}