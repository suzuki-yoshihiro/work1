package jp.co.iccom.suzuki_yoshihiro.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;


public class CalculateSales {

	static boolean readFile(File folder, HashMap<String, String> mapName, HashMap<String, Long> mapAmount, String[] dataType) throws IOException{

		BufferedReader br = null;

		try{
			br =  new BufferedReader(new FileReader(new File(folder, dataType[0] + ".lst")));
			String readLine;
			String pattern;

			// 第4引数の値を基に読み込むファイルの種別にあわせたマッチャーパターンを格納
			if(dataType[0].equals("branch")){
				// パターン：3桁の数字
				pattern = "^\\d{3}$";
			}else{
				// パターン：8桁の英数字
				pattern = "^\\w{8}$";
			}
			while((readLine = br.readLine()) != null){
				String[] separatedValues = readLine.split(",");
				if(separatedValues.length != 2 || !separatedValues[0].matches(pattern)){
					System.out.println(dataType[1] + "定義ファイルのフォーマットが不正です");
					return false;
				}
				// 支店/商品コードをキーに、支店/商品名を値として保存
				mapName.put(separatedValues[0], separatedValues[1]);
				// 支店/商品コードをキーに、売上金額を値(計算前なので0)として保存
				mapAmount.put(separatedValues[0], new Long(0));
			}
		}
		// 定義ファイルが見つからなかった場合の処理
		catch(FileNotFoundException e){
			System.out.println(dataType[1] + "定義ファイルが存在しません");
			return false;
		}
		// その他未定義のエラーの場合
		catch(Exception e){
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}
		finally{
			if(br != null){
				br.close();
			}
		}
		return true;
	}

	static void getRcdList(File folder, ArrayList<String> rcdList){

		File[] folderList = folder.listFiles();

		for(int i = 0; i < folderList.length; i++){
			// 拡張子がrcdのファイルを検索
			if(folderList[i].getName().length() == 12 && folderList[i].getName().endsWith(".rcd") && folderList[i].isFile()){
				rcdList.add(folderList[i].getName());
			}
		}
	}

	static boolean isContinuous(ArrayList<String> rcdList){

		if(rcdList.size() == 1){
			return true;
		}
		Collections.sort(rcdList);
		for(int i = 0; i < rcdList.size() - 1; i++){
			try{
				if((Integer.parseInt(rcdList.get(i).substring(0, 8)) - Integer.parseInt(rcdList.get(i + 1).substring(0, 8))) != -1){
					return false;
				}
			}
			catch(Exception e){
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
					System.out.println(rcdList.get(i)  + "のフォーマットが不正です");
					return false;
				}

				// 支店別の売上集計処理

				if(!branchMapAmount.containsKey(rcdData.get(0))){
					System.out.println(rcdList.get(i) + "の支店コードが不正です");
					return false;
				}
				// HashMapに格納された合計金額にrcdファイルから読み込んだ売上金額を加算
				long calcration = branchMapAmount.get(rcdData.get(0)) + Long.parseLong(rcdData.get(2));
				//合計金額の桁数を判定
				if(String.valueOf(calcration).length() > 10){
					System.out.println("合計金額が10桁を超えました");
					return false;
				}
				branchMapAmount.put(rcdData.get(0), calcration);

				// 商品別の売上集計処理(処理内容は支店別と同様)

				if(!commodityMapAmount.containsKey(rcdData.get(1))){
					System.out.println(rcdList.get(i) + "の商品コードが不正です");
					return false;
				}
				calcration = commodityMapAmount.get(rcdData.get(1)) + Long.parseLong(rcdData.get(2));
				if(String.valueOf(calcration).length() > 10){
					System.out.println("合計金額が10桁を超えました");
					return false;
				}
				commodityMapAmount.put(rcdData.get(1), calcration);
			}
			catch(Exception e){
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
			finally{
				if(br != null){
					br.close();
				}
			}
		}
		return true;
	}

	static boolean writeFile(File folder, HashMap<String, String> mapName, HashMap<String, Long> mapAmount, String dataType) throws IOException{

		ArrayList<Result> resultList = new ArrayList<Result>();
		String[] codeArray = mapAmount.keySet().toArray(new String[mapAmount.size()]);
		Arrays.sort(codeArray);
		BufferedWriter bw = null;
		try{
			bw = new BufferedWriter(new FileWriter(new File(folder, dataType + ".out")));
			for(String code : codeArray){
				resultList.add(new Result(code,
							  mapName.get(code),
							mapAmount.get(code)));
			}
			Collections.sort(resultList);
			Collections.reverse(resultList);

			// ファイルへの書き出し
			for(Result r : resultList){
				bw.write(r.code + "," + r.name + "," + r.amount);
				bw.newLine();
			}
		}
		catch(Exception e){
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}
		finally{
			if(bw != null){
				bw.close();
			}
		}
		return true;
	}

	public static void main(String[] args) {

		if(args.length == 0){
			System.out.println("正しくディレクトリを指定してください");
			return;
		}

		File folder = new File(args[0]);

		// 支店情報の読み込み
		HashMap<String, String> branchMapName = new HashMap<String, String>();
		HashMap<String, Long> branchMapAmount = new HashMap<String, Long>();
		try{
			String[] type = {"branch", "支店"};

			if(!readFile(folder, branchMapName, branchMapAmount, type)){
				return;
			}
		}
		catch(Exception e){
			System.out.println("予期せぬエラーが発生しました");
		}

		// 商品情報の読み込み
		HashMap<String, String> commodityMapName = new HashMap<String, String>();
		HashMap<String, Long> commodityMapAmount = new HashMap<String, Long>();
		try{
			String[] type = {"commodity", "商品"};
			if(!readFile(folder, commodityMapName, commodityMapAmount, type)){
				return;
			}
		}
		catch(Exception e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

		// 売上ファイルの読み込み・前処理と計算
		ArrayList<String> rcdList = new ArrayList<String>();

		// 引数で指定したディレクトリ内にある"*.rcd"ファイルの一覧を取得
		// rcdファイルの一覧を読み込み、内容が0件だった場合は何もせずに終了する
		getRcdList(folder, rcdList);
		if(rcdList.size() == 0){
			return;
		}

		// rcdファイルのファイル名が連続しているかどうかを判定
		if(!isContinuous(rcdList)){
			System.out.println("売上ファイル名が連番になっていません");
			return;
		}

		// rcdファイルの集計作業
		try{
			if(!calculateAmount(folder, branchMapAmount, commodityMapAmount, rcdList)){
				return;
			}
		}
		catch(Exception e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

		 // 支店別売上集計後ファイル書き出し
		try{
			if(!writeFile(folder, branchMapName, branchMapAmount, "branch")){
				return;
			}
			if(!writeFile(folder, commodityMapName, commodityMapAmount, "commodity")){
				return;
			}
		}
		catch(Exception e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
	}
}