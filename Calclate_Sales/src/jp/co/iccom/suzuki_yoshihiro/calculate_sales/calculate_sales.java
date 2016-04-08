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
import java.util.Iterator;


public class calculate_sales {
	public static void main(String[] args) throws IOException{


		/*
		 * 変数宣言
		 */


		Branch[] bArry;
		Branch bTmp = new Branch();
		Commodity[] cArry;
		Commodity cTmp = new Commodity();

		// HashMap・ArrayListの宣言

		HashMap<String, String> branchMapIn = new HashMap<String, String>();	// 支店情報
		HashMap<String, String> commodityMapIn = new HashMap<String, String>();	// 商品情報
		HashMap<String, Long> branchMapOut = new HashMap<String, Long>();		// 支店情報(売上集計処理後)
		HashMap<String, Long> commodityMapOut = new HashMap<String, Long>();	// 商品情報(売上集計処理後)
		ArrayList<String> bCodeList = new ArrayList<String>();					// 支店コード用
		ArrayList<String> cCodeList = new ArrayList<String>();					// 商品コード用


		// Fileクラス

		File file;																// ファイル操作用
		File folder = new File(args[0]);										// フォルダ操作用

		// ファイル読み込み

		FileReader fr;
		BufferedReader br = null;

		// ファイル書き込み

		FileWriter fw;
		BufferedWriter bw;

		// 文字列型

		String s = "";												// ファイルからの情報読み込み用
		String ls = System.getProperty("line.separator");			// 改行コードの取得
		String fs = System.getProperty("file.separator");			// ディレクトリ・ファイルパスの区切りの取得
		String errmsg = "予期せぬエラーが発生しました";

		// 文字列型配列

		String[] tmp;												// ファイルから読み込んだ情報を一時的に保管する
		String[] rcd = new String[32768];							// レコードファイルのファイル名格納
		String[] filelist = folder.list();

		// 整数型

		int i, j, k;


		/*
		 * 支店情報ファイル読み込み
		 */

		try{

			file = new File(args[0] + fs + "branch.lst");
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			while((s = br.readLine()) != null){
				tmp = s.split(",");

				// フィールド数及び支店番号の判定、3以上の場合、エラーメッセージを表示し強制終了
				if(tmp.length >= 3 || tmp[0].length() != 3){
					System.out.println("支店定義ファイルのフォーマットが不正です");
					br.close();
					return ;
				}
				int code = Integer.parseInt(tmp[0]);		// 支店番号が数字のみで定義されているかどうかを判定
				branchMapIn.put(tmp[0], tmp[1]);
				branchMapOut.put(tmp[0], new Long(0));
				bCodeList.add(tmp[0]);

			}
			br.close();
		}
		catch(FileNotFoundException e){		// 支店定義ファイルが見つからなかった場合の処理
			System.out.println("支店定義ファイルが存在しません");
			return ;
		}
		catch(NumberFormatException e){		// 支店番号の形式が不正だった場合
			System.out.println("支店定義ファイルのフォーマットが不正です");
			return ;
		}
		catch(Exception e){		// その他の例外に対する処理
			System.out.println(errmsg);
			System.out.println(e.getMessage());
			return ;
		}


		/*
		 * 商品情報ファイル読み込み
		 */

		try{

			file = new File(args[0] + fs + "commodity.lst");
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			while((s = br.readLine()) != null){
				tmp = s.split(",");
				// フィールド数の及び商品コードの文字数判定、規定以上の場合、エラーメッセージを表示し強制終了
				if(tmp.length >= 3 || tmp[0].length() != 8){
					System.out.println("商品定義ファイルのフォーマットが不正です");
					br.close();
					return ;
				}
				commodityMapIn.put(tmp[0], tmp[1]);	// 商品定義用HashMapへ要素の追加
				commodityMapOut.put(tmp[0], new Long(0));
				cCodeList.add(tmp[0]);
			}
			br.close();
		}
		catch(FileNotFoundException e){		// 商品定義ファイルが見つからなかった場合の例外処理
			System.out.println("商品定義ファイルが存在しません");
			return;
		}
		catch(Exception e){		// その他の例外に対する処理
			System.out.println(errmsg);
			return ;
		}


		/*
		 * 売上レコードファイル読み込み及び集計処理
		 */

		// 引数で指定したディレクトリ内にある"*.rcd"ファイルの一覧を取得

		j= 0;
		for(i = 0; i < filelist.length; i++){

			if(filelist[i].contains("rcd")){
				rcd[j] = filelist[i];
				j++;
			}
		}

		// rcdファイルの名前が連番になっているかどうかを判定、なっていない場合はメッセージを出力し終了
		for(i = 0; i < j - 1; i++){
			if(Math.abs(rcd[i].compareTo(rcd[i + 1])) != 1){
				System.out.println("売上ファイル名が連番になっていません");
				return ;
			}
		}
		// rcdファイルの読み込み
		try{
			for(i = 0; i < j; i++){

				k = 0;
				tmp = new String[3];
				file = new File(args[0] + fs + rcd[i]);
				fr = new FileReader(file);
				br = new BufferedReader(fr);

				while((s = br.readLine()) != null){

					// rcdファイルの行数を調べ、4行以上あった場合はエラーメッセージを表示し終了

					if(k > 4){
						System.out.println("<" + args[0] + fs + rcd[i] + ">のフォーマットが不正です");
						br.close();
						return ;
					}
					tmp[k] = s;
					k++;

				}
				br.close();

				boolean bflg = false;
				boolean cflg = false;

				// 支店別の売上集計処理

				for(int l = 0; l < bCodeList.size(); l++){
					if(tmp[0].equals(bCodeList.get(l))){
						long cal = branchMapOut.get(tmp[0]);
						cal += Long.parseLong(tmp[2]);
						if(String.valueOf(cal).length() >= 10){		//合計金額の桁数を判定
							System.out.println("合計金額が10桁を超えました");
							System.out.println(String.valueOf(cal));
							return ;
						}
						branchMapOut.put(tmp[0], cal);
						System.out.println(branchMapOut.entrySet());
						bflg = true;
					}
				}
				for(int l = 0; l < cCodeList.size(); l++){
					if(tmp[1].equals(cCodeList.get(l))){
						long cal = commodityMapOut.get(tmp[1]);
						cal += Long.parseLong(tmp[2]);
						if(String.valueOf(cal).length() >= 10){		//合計金額の桁数を判定
							System.out.println("合計金額が10桁を超えました");
							System.out.println(String.valueOf(cal));
							return ;
						}
						commodityMapOut.put(tmp[1], cal);
						System.out.println(commodityMapOut.entrySet());
						cflg = true;
					}
				}

				/*
				 * 以下は、当該データが見当たらなかった場合に行う
				 * エラーメッセージ出力処理
				 */

				if(!bflg){
					System.out.println("<" + args[0] + fs + rcd[i] + ">の支店コードが不正です");
					return ;
				}
				if(!cflg){
					System.out.println("<" + args[0] + fs + rcd[i] + ">の商品コードが不正です");
					return ;
				}


			}

		}
		catch(Exception e){
			System.out.println(errmsg);
			return;
		}


		/*
		 * 支店別売上集計後ファイル書き出し
		 */

		file = new File(args[0] + fs + "branch.out");
		fw = new FileWriter(file);
		bw = new BufferedWriter(fw);
		bArry = new Branch[bCodeList.size()];
		System.out.println(bArry.length);
		try{
			Iterator<String> itIn = branchMapIn.keySet().iterator();
			i = 0;
			while(itIn.hasNext()){
				Object obj = itIn.next();
				bArry[i] = new Branch(
						obj.toString(),
						branchMapIn.get(obj.toString()),
						branchMapOut.get(obj.toString()));

				i++;
			}

			for(i = 0; i < bArry.length -1; i++){
				for(j = i + 1; j < bArry.length; j++){
					if(bArry[i].bAmount < bArry[j].bAmount){
						bTmp = bArry[i];
						bArry[i] = bArry[j];
						bArry[j] = bTmp;
					}
				}
			}
			for(Branch b : bArry){
				System.out.println("支店コード：" + b.bCode + "　支店名：" + b.bName + "　売上：" + b.bAmount);
				bw.write(b.bCode + "," + b.bName + "," + b.bAmount + ls);
			}
			bw.close();
		}
		catch(Exception e){
			System.out.println(errmsg);
		}

		/*
		 * 商品別売上集計後ファイル書き出し
		 */


		file = new File(args[0] + fs + "commodity.out");
		fw = new FileWriter(file);
		bw = new BufferedWriter(fw);
		cArry = new Commodity[cCodeList.size()];

		try{
			Iterator<String> itIn = commodityMapIn.keySet().iterator();
			i = 0;
			while(itIn.hasNext()){
				Object obj = itIn.next();

				cArry[i] = new Commodity(
						obj.toString(),
						commodityMapIn.get(obj.toString()),
						commodityMapOut.get(obj.toString()));
				i++;
			}
			for(i = 0; i < cArry.length -1; i++){
				for(j = i + 1; j < cArry.length; j++){
					if(cArry[i].cAmount < cArry[j].cAmount){
						cTmp = cArry[i];
						cArry[i] = cArry[j];
						cArry[j] = cTmp;
					}
				}
			}
			for(Commodity c : cArry){
				System.out.println("支店コード：" + c.cCode + "　支店名：" + c.cName + "　売上：" + c.cAmount);
				bw.write(c.cCode + "," + c.cName + "," + c.cAmount + ls);
			}
			bw.close();
		}
		catch(Exception e){
			System.out.println(errmsg + ls + e);
		}

	}

}