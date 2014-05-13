package cn.tongji.lab.word;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

import cn.tongji.lab.database.ConnectDatabase;

public class FenciTest {
	private static Map<String, Integer> lexiconHM = new HashMap<String, Integer>();

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
		Analyzer analyzer = new PaodingAnalyzer();
		ConnectDatabase cd = new ConnectDatabase();
		Connection conn = cd.ConnectMysql();
		Date dt = new Date();
		String[] tempShuZu = dt.toString().split(" ");
		String temp_str = tempShuZu[0] + " " + tempShuZu[1] + " "
				+ tempShuZu[2] + "%" + tempShuZu[4] + " " + tempShuZu[5];
		String sqlSelectAll = "select * from status";
		// String sqlSelectAll = "select * from status where created_at LIKE '"
		// + temp_str + "';";
		System.out.println(sqlSelectAll);
		ResultSet rs = cd.selectSQL(sqlSelectAll);
		int timeCount = 0;
		try {
			while (rs.next()) {
				String docText = rs.getString("text");
				TokenStream tokenStream = analyzer.tokenStream(docText,
						new StringReader(docText));
				try {
					Token t;
					// System.out.println(docText);
					while ((t = tokenStream.next()) != null) {
						String temp = t.termText();
						if (isChinese(temp) && temp.length() > 1) {
							if (lexiconHM.containsKey(temp)) {
								int tempCount = lexiconHM.get(temp);
								tempCount++;
								lexiconHM.remove(temp);
								lexiconHM.put(temp, tempCount);
							} else {
								lexiconHM.put(temp, 1);
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				timeCount++;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			cd.CutConnection(conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		conn = cd.ConnectMysql();
		try {
			cd.InsertSql(lexiconHM);
			cd.CutConnection(conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
