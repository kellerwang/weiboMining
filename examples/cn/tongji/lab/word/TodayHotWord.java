package cn.tongji.lab.word;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

import cn.tongji.lab.database.ConnectDatabase;
import cn.tongji.lab.model.Integer2;

public class TodayHotWord {

	private static Map<String, Integer2> todayHM = new HashMap<String, Integer2>();

	public static final boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	public static final boolean isChinese(String strName) {
		char[] ch = strName.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isChinese(c)) {
				return true;
			}
		}
		return false;
	}

	public static final boolean isChineseCharacter(String chineseStr) {
		char[] charArray = chineseStr.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			if ((charArray[i] >= 0x4e00) && (charArray[i] <= 0x9fbb)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @deprecated; 弃用。和方法isChineseCharacter比效率太低。
	 * */
	public static final boolean isChineseCharacter_f2() {
		String str = "！？";
		for (int i = 0; i < str.length(); i++) {
			if (str.substring(i, i + 1).matches("[\\一-\\?]+")) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		try {
			Analyzer analyzer = new PaodingAnalyzer();
			ConnectDatabase cd = new ConnectDatabase();
			Connection conn = cd.ConnectMysql();
			Date dt = new Date();
			String[] tempShuZu = dt.toString().split(" ");
			String tempTodayArray = tempShuZu[0] + " " + tempShuZu[1] + " "
					+ tempShuZu[2] + "%" + tempShuZu[4] + " " + tempShuZu[5];
			String sqlSelectToday = "select * from status where created_at LIKE '"
					+ tempTodayArray + "';";
			System.out.println(sqlSelectToday);
			ResultSet rsToday = cd.selectSQL(sqlSelectToday);
			while (rsToday.next()) {
				String docText = rsToday.getString("text");
				TokenStream tokenStream = analyzer.tokenStream(docText,
						new StringReader(docText));
				try {
					Token t;
					// System.out.println(docText);
					while ((t = tokenStream.next()) != null) {
						String temp = t.termText();
						if (isChinese(temp) && temp.length() > 1) {
							if (todayHM.containsKey(temp)) {
								int tempCount = todayHM.get(temp).getToday();
								tempCount++;
								todayHM.get(temp).setToday(tempCount);
							} else {
								todayHM.put(temp, new Integer2(1, 0));
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Date dy = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
			tempShuZu = dy.toString().split(" ");
			String tempYesterdayArray = tempShuZu[0] + " " + tempShuZu[1] + " "
					+ tempShuZu[2] + "%" + tempShuZu[4] + " " + tempShuZu[5];
			String sqlSelectYesterday = "select * from status where created_at LIKE '"
					+ tempYesterdayArray + "';";
			System.out.println(sqlSelectYesterday);
			ResultSet rsYesterday = cd.selectSQL(sqlSelectYesterday);
			while (rsYesterday.next()) {
				String docText = rsYesterday.getString("text");
				TokenStream tokenStream = analyzer.tokenStream(docText,
						new StringReader(docText));
				try {
					Token t;
					// System.out.println(docText);
					while ((t = tokenStream.next()) != null) {
						String temp = t.termText();
						if (isChinese(temp) && temp.length() > 1) {
							if (todayHM.containsKey(temp)) {
								int tempCount = todayHM.get(temp)
										.getYesterday();
								tempCount++;
								todayHM.get(temp).setYesterday(tempCount);
							} else {
								todayHM.put(temp, new Integer2(0, 1));
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			cd.CutConnection(conn);
			Iterator iter = todayHM.entrySet().iterator();
			double sizeNum = 0;
			double sumSum = 0;
			double sumProportion = 0;
			while (iter.hasNext()) {
				Entry<String, Integer2> entry = (Entry<String, Integer2>) iter
						.next();
				String key = entry.getKey();
				Integer2 val = entry.getValue();
				val.setSum(val.getYesterday() + val.getToday());
				double temp1 = val.getToday();
				double temp2 = val.getSum();
				val.setProportion(temp1 / temp2);

				System.out.println(key + ":" + val.getYesterday() + "/"
						+ val.getToday() + "/" + val.getSum() + "/"
						+ val.getProportion());
				sizeNum++;
				sumSum += val.getSum();
				sumProportion += val.getProportion();
			}
			double averageSum = sumSum / sizeNum;
			double averageProportion = sumProportion / sizeNum;
			System.out.println("sizeNum:" + sizeNum + " averageSum:"
					+ averageSum + " averageProportion:" + averageProportion);
			iter = todayHM.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, Integer2> entry = (Entry<String, Integer2>) iter
						.next();
				String key = entry.getKey();
				Integer2 val = entry.getValue();
				double temp1 = val.getSum();
				double temp = (val.getProportion() * temp1 + averageSum
						* averageProportion)
						/ (averageSum + temp1);
				val.setResult(temp);
				System.out.println(key + ":" + val.getResult());
			}
			ArrayList<Entry<String, Integer2>> list = new ArrayList<Entry<String, Integer2>>(
					todayHM.entrySet());

			Collections.sort(list, new Comparator<Object>() {
				public int compare(Object e1, Object e2) {
					double v1 = ((Entry<String, Integer2>) e1).getValue()
							.getResult();

					double v2 = ((Entry<String, Integer2>) e2).getValue()
							.getResult();
					if (v1 > v2) {
						return -1;
					} else {
						return 1;
					}

				}
			});
			for (Entry<String, Integer2> e : list) {
				System.out.println(e.getKey() + ":" + e.getValue().getResult());
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
