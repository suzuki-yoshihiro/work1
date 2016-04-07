package jp.co.iccom.suzuki_yoshihiro.calculate_sales;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class calculate_sales {
	public static void main(String[] args) throws IOException{
		
		/*
		 * 変数宣言
		 */
		
	
		ArrayList<Branch> branchList = new ArrayList<Branch>();
		ArrayList<Commodity> commodityList = new ArrayList<Commodity>();
		ArrayList<Proceeds> proceedsList = new ArrayList<Proceeds>();
	
		File file;
		FileReader fr;
		BufferedReader br = null;
		String[] tmp;	// ファイルから読み込んだ情報を一時的に保管する
		String s = "";	// ファイルからの情報読み込み用
		String ls = System.getProperty("line.separator");	// 改行コードの取得
		String fs = System.getProperty("file.separator");	// ディレクトリ・ファイルパスの区切りの取得

		String[] rcd = new String[100];		// レコードファイルのファイル名格納
		File folder = new File(args[0]);	// フォルダ情報の格納
		String[] filelist = folder.list();	
		String errmsg = "予期せぬエラーが発生しました";
		
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
				// フィールド数の判定、3以上の場合、エラーメッセージを表示し強制終了
				if(tmp.length >= 3 || tmp[0].length() != 3){		
					System.out.println("支店定義ファイルのフォーマットが不正です");
//					return ;
				}

				branchList.add(new Branch(tmp[0], tmp[1]));

			}

			for(Branch b : branchList){
				System.out.println("支店番号：" + b.bCode + "　支店名：" + b.bName);
			}

		}
		catch(FileNotFoundException e){		// 支店定義ファイルが見つからなかった場合の処理
			System.out.println("支店定義ファイルが存在しません");
			System.out.println(e);
			return;
		}
		catch(NumberFormatException e){
			System.out.println("支店定義ファイルのフォーマットが不正です");
			System.out.println(e);
			return ;
		}
		catch(Exception e){		// その他の例外に対する処理
			System.out.println(errmsg);
			return ;
		}
		finally{
			br.close();
		}
		System.out.println("================================");

		
		/*
		 * 商品情報ファイル読み込み
		 */
		
		try{

			file = new File(args[0] + fs + "commodity.lst");
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			while((s = br.readLine()) != null){
				tmp = s.split(",");
				if(tmp.length >= 3 || tmp[0].length() != 8){	// フィールド数の判定、3以上の場合、エラーメッセージを表示し強制終了
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return ;
				}
				commodityList.add(new Commodity(tmp[0], tmp[1]));	// 商品定義用ArrayListへ要素の追加
			}
			
			
			for(Commodity c : commodityList){
				System.out.println("商品番号：" + c.cCode + "　商品名：" + c.cName);
			}
		}
		catch(FileNotFoundException e){		// 商品定義ファイルが見つからなかった場合の例外処理
			System.out.println("商品定義ファイルが存在しません");	
			return;
		}
		catch(Exception e){		// その他の例外に対する処理
			System.out.println(errmsg);
			return ;
		}
		finally{
			br.close();
		}
		
		
		/*
		 * 売上レコードファイル読み込み
		 */
		
		j= 0;
		for(i = 0; i < filelist.length; i++){
			
			if(filelist[i].contains("rcd")){
				rcd[j] = filelist[i];
				j++;
			}
		}
		for(i = 0; i < j - 1; i++){
			if(Math.abs(rcd[i].compareTo(rcd[i + 1])) != 1){
				System.out.println("売上ファイル名が連番になっていません");
				return ;
			}
		}
		try{
			for(i = 0; i < j; i++){
				k = 0;
				tmp = new String[3];
				file = new File(args[0] + fs + rcd[i]);
				fr = new FileReader(file);
				br = new BufferedReader(fr);
				while((s = br.readLine()) != null){
					if(k >= 3){
						System.out.println("<" + args[0] + fs + rcd[i] + ">のフォーマットが不正です");
						return ;
					}
					tmp[k] = s;
					k++;
				}
				proceedsList.add(new Proceeds(tmp[0], tmp[1], Long.parseLong(tmp[2])));

			}
		}		
		catch(Exception e){
			System.out.println(e);
			return;
		}
		finally{
			br.close();	
		}
		for(Proceeds p : proceedsList){
			System.out.println("支店番号：" + p.bCode + "　商品番号：" + p.cCode + "　売上金額：" + p.amount);
		}

	}

		
}

