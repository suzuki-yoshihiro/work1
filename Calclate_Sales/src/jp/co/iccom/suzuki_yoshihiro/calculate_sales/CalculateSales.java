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
	public static void main(String[] args) throws IOException{


		/*
		 * 変数宣言
		 */

		// Fileクラス
		File folder = new File(args[0]);							// フォルダ操作用

		// 文字列型
		String reader;											// ファイルからの情報読み込み用

		// 文字列型配列
		String[] separatedValues;									// ファイルから読み込んだ情報を一時的に保管する

		/*
		 * 支店情報ファイル読み込み
		 */

		HashMap<String, String> branchMapName = new HashMap<String, String>();// 支店コード・支店名
		HashMap<String, Long> branchMapAmount = new HashMap<String, Long>();	// 支店コード・売上高
		ArrayList<String> branchCodeList = new ArrayList<String>();			// 支店コード用
		BufferedReader brBranchReader = new BufferedReader(new FileReader(new File(folder, "branch.lst")));
		try{
			while((reader = brBranchReader.readLine()) != null){
				// カンマ(,)で内容を区切り、一時保存用の配列へ保存
				separatedValues = reader.split(",");

				// フィールド数及び支店番号の判定、3以上の場合、エラーメッセージを表示し強制終了
				if(separatedValues.length >= 3 || separatedValues[0].length() != 3 ||
						separatedValues[0].getBytes().length != separatedValues[0].length()){
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;
				}
				// 支店コードに当たる部分の文字を数字に変換し、エラーを吐くか調べる
				Integer.parseInt(separatedValues[0]);
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
		catch(NumberFormatException e){		// 支店番号の形式が不正だった場合
			System.out.println("支店定義ファイルのフォーマットが不正です");
			return;
		}
		catch(Exception e){		// その他の例外に対する処理
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		finally{
			brBranchReader.close();
		}


		/*
		 * 商品情報ファイル読み込み
		 */

		HashMap<String, String> commodityMapName = new HashMap<String, String>();			// 商品コード・商品名
		HashMap<String, Long> commodityMapAmount = new HashMap<String, Long>();			// 商品コード・売上高
		ArrayList<String> commodityCodeList = new ArrayList<String>();					// 商品コード用
		BufferedReader brCommodityReader = new BufferedReader(new FileReader(new File(folder, "commodity.lst")));
		try{


			while((reader = brCommodityReader.readLine()) != null){
				separatedValues = reader.split(",");
				// フィールド数の及び商品コードの文字数判定、規定以上の場合、エラーメッセージを表示し強制終了
				if(separatedValues.length >= 3 || separatedValues[0].length() != 8 ||
						separatedValues[0].getBytes().length != separatedValues[0].length()){
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
			return;
		}
		finally{
			// BufferedReaderのクローズ処理
			brCommodityReader.close();
		}

		/*
		 * 売上レコードファイル読み込み及び集計処理
		 */

		// 引数で指定したディレクトリ内にある"*.rcd"ファイルの一覧を取得

		ArrayList<String> rcdList = new ArrayList<String>();		// レコードファイルのファイル名格納
		String[] filelist = folder.list();							// カレントディレクトリのファイル一覧
		for(int i = 0; i < filelist.length; i++){
			// 名前にrcdを含む8文字( + 拡張子3文字)のファイルを検索
			if(filelist[i].contains(".rcd") && filelist[i].length() == 12){
				if(filelist[i].substring(8, 12).equals(".rcd")){ // 拡張子がrcdのファイルを検索
					rcdList.add(filelist[i]);
					System.out.println(filelist[i]);
				}
			}
		}

		/*
		 * rcdファイルの名前が連番になっているかどうかを判定、なっていない場合はメッセージを出力し終了
		 * 連番判定はArrayList二格納された文字列の差を求め、その結果が-1以外となっている場合にのみ
		 * エラーを出力するものとする
		 * rcdファイルが1つのみの場合はこの処理をスキップ、0の場合は何もせずプログラムを終了する
		 */
		if(rcdList.size() >= 2){ // rcdファイルの数が2つ以上の場合
			Collections.sort(rcdList); // ファイル名の昇順ソート
			for(int i = 0; i < rcdList.size() - 1; i++){

				if(rcdList.get(i).compareTo(rcdList.get(i + 1)) != -1){
					System.out.println("売上ファイル名が連番になっていません");
					return;
				}
			}
		}else if(rcdList.size() == 0){ // rcdファイルが存在しない場合
				return;
		}

		// rcdファイルの読み込み
		for(int i = 0; i < rcdList.size(); i++){
			File fileRcdIn = new File(folder, rcdList.get(i));
			BufferedReader brRcdReader = new BufferedReader(new FileReader(fileRcdIn));
			try{
				int j = 0;
				separatedValues = new String[3];
				while((reader = brRcdReader.readLine()) != null){
					separatedValues[j] = reader;
					j++;

					// rcdファイルの行数を調べ、4行以上あった場合はエラーメッセージを表示し終了
					if(j >= 4){
						System.out.println("<" + fileRcdIn.getPath() + ">のフォーマットが不正です");
						System.out.println(j);
						return;
					}

				}
				// rcdファイルの行数を調べ、2行以下の場合もエラーメッセージを表示し終了
				if(j <= 2){
					System.out.println("<" + fileRcdIn.getPath() + ">のフォーマットが不正です");
					System.out.println(j);
					return;
				}

				// 支店別の売上集計処理
				boolean bflg = false;
				for(j = 0; j < branchCodeList.size(); j++){
					// あらかじめ読み込んである支店コードと読み込んだrcdファイルの支店コードを比較
					if(separatedValues[0].equals(branchCodeList.get(j))){

						long calcration = branchMapAmount.get(separatedValues[0]);
						calcration += Long.parseLong(separatedValues[2]);
						if(String.valueOf(calcration).length() > 10){		//合計金額の桁数を判定
							System.out.println("合計金額が10桁を超えました");
							return;
						}
						branchMapAmount.put(separatedValues[0], calcration);
						bflg = true;
					}
				}

				// 商品別の売上集計処理
				boolean cflg = false;
				for(j = 0; j < commodityCodeList.size(); j++){

					if(separatedValues[1].equals(commodityCodeList.get(j))){
						long calcration = commodityMapAmount.get(separatedValues[1]);
						calcration += Long.parseLong(separatedValues[2]);
						if(String.valueOf(calcration).length() > 10){		//合計金額の桁数を判定
							System.out.println("合計金額が10桁を超えました");
							return;
						}
						commodityMapAmount.put(separatedValues[1], calcration);
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
			brRcdReader.close();
			}
		}

		/*
		 * 支店別売上集計後ファイル書き出し
		 */

		BufferedWriter bwBranchWriter = new BufferedWriter(new FileWriter(new File(folder, "branch.out")));

		/*
		 * クラス配列の宣言
		 * 支店コードの一覧を格納したArrayListのサイズでインスタンス化する
		 *
		 */
		Branch[] branchArry = new Branch[branchCodeList.size()];
		try{
			// クラス配列への格納
			for(int i = 0; i < branchCodeList.size(); i++){
				branchArry[i] = new Branch(
						branchCodeList.get(i),
						branchMapName.get(branchCodeList.get(i)),
						branchMapAmount.get(branchCodeList.get(i))
						);
			}

			// 単純選択法による金額順の並べ替え
			for(int i = 0; i < branchArry.length -1; i++){
				for(int j = i + 1; j < branchArry.length; j++){
					if(branchArry[i].bAmount < branchArry[j].bAmount){
						Branch branchTmp = new Branch();
						branchTmp = branchArry[i];
						branchArry[i] = branchArry[j];
						branchArry[j] = branchTmp;
					}
				}
			}
			// ファイルへの書き出し
			for(Branch b : branchArry){
				bwBranchWriter.write(b.bCode + "," + b.bName + "," + b.bAmount +
						System.getProperty("line.separator"));
			}
		}
		catch(Exception e){
			System.out.println("予期せぬエラーが発生しました");
		}
		finally{
			bwBranchWriter.close();
		}

		/*
		 * 商品別売上集計後ファイル書き出し
		 */
		BufferedWriter bwCommodityWriter = new BufferedWriter(new FileWriter(new File(folder, "commodity.out")));

		/*
		 * クラス配列の宣言
		 * 商品コードの一覧を格納したArrayListのサイズでインスタンス化する
		 *
		 */
		Commodity[] commodityArry = new Commodity[commodityCodeList.size()];
		try{
			for(int i = 0; i < commodityCodeList.size(); i++){
				// クラス配列への格納
				commodityArry[i] = new Commodity(
						commodityCodeList.get(i),
						commodityMapName.get(commodityCodeList.get(i)),
						commodityMapAmount.get(commodityCodeList.get(i))
						);
			}

			// 単純選択法による金額降順ソート
			for(int i = 0; i < commodityArry.length -1; i++){
				for(int j = i + 1; j < commodityArry.length; j++){
					if(commodityArry[i].cAmount < commodityArry[j].cAmount){
						Commodity commodityTmp = new Commodity();
						commodityTmp = commodityArry[i];
						commodityArry[i] = commodityArry[j];
						commodityArry[j] = commodityTmp;
					}
				}
			}
			// ファイルへの書き出し
			for(Commodity c : commodityArry){
				bwCommodityWriter.write(c.cCode + "," + c.cName + "," + c.cAmount +
						System.getProperty("line.separator"));
			}
		}
		catch(Exception e){
			System.out.println("予期せぬエラーが発生しました");
		}
		finally{
			bwCommodityWriter.close();
		}

	}

}