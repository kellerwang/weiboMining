package cn.tongji.lab.word;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.tongji.lab.model.WordInfo;

public class HeadWord {
	private static Map<String, WordInfo> headWordHM = new HashMap<String, WordInfo>();
	private static List<String> wordsList = new ArrayList<String>();
	private static long charNum = 0l;

	public HeadWord() {
		// TODO Auto-generated constructor stub
	}

	public static void readFileByChars(String fileName) {
		File file = new File(fileName);
		Reader reader = null;
		try {
			System.out.println("以字符为单位读取文件内容，一次读一个字节：");
			// 一次读一个字符
			reader = new InputStreamReader(new FileInputStream(file));
			String tempStr = new String();
			int tempchar;
			int leftChar = -1;
			Pattern p = Pattern
					.compile("[，＂ ０ １ ２ ３ ４ ５ ６ ７ ８ ９ ＃ ＋ － ． ／ ＜ ＝ ＞ ［ ］ ｀ ｏ ｡ ￣ ％ ……＿  -‘Ξ α ν θ =& * ～ η ~【 ★ 〈 〉 「 」 『 』 〜 — 《 》 】/. • ’ ↓  * # @ ￥ ^ $（ ）。| ？： — ；、 ·  。 ：! “ ！ % ”]");
			while ((tempchar = reader.read()) != -1) {
				if (FenciTest.isChinese((char) tempchar)) {
					charNum++;
				}
				Matcher matcher = p.matcher(String.valueOf((char) tempchar));
				if (matcher.matches() || ((char) tempchar) == '\r'
						|| !FenciTest.isChinese((char) tempchar)
						|| ((char) tempchar) == '　') {
					if (tempStr.isEmpty()) {
						continue;
					} else {
						int count = tempStr.length();
						int leftCharTemp = -1;
						for (int i = 0; i < count; i++) {
							System.out.println(tempStr);
							wordsList.add(tempStr);
							int tempSize = tempStr.length() + 1;
							if (tempStr.length() == (5 + 1)) {
								tempSize = tempStr.length();
							}
							for (int j = 1; j < tempSize; j++) {
								String temp = tempStr.substring(0, j);
								WordInfo wi = null;
								if (headWordHM.containsKey(temp)) {
									wi = headWordHM.get(temp);
									int tempCount = wi.getCount() + 1;
									wi.setCount(tempCount);
									if (j != tempStr.length() && !wi.isSingle()) {
										wi.addRightList(tempStr.charAt(j));
										if (leftCharTemp != -1) {
											wi.addLiftList((char) leftCharTemp);
										} else if (leftChar != -1) {
											wi.addLiftList((char) leftChar);
										}
									}
									headWordHM.remove(temp);
									headWordHM.put(temp, wi);
								} else {
									if (temp.length() == 1) {
										wi = new WordInfo(true, 1);
									} else {
										wi = new WordInfo(false, 1);
										if (j != tempStr.length()) {
											wi.addRightList(tempStr.charAt(j));
											if (leftCharTemp != -1) {
												wi.addLiftList((char) leftCharTemp);
											} else if (leftChar != -1) {
												wi.addLiftList((char) leftChar);
											}
										}
									}
									headWordHM.put(temp, wi);
								}
							}
							leftCharTemp = tempStr.charAt(0);
							tempStr = tempStr.substring(1);
						}
						tempStr = new String();
					}
					leftChar = -1;
				} else {
					if (tempStr.length() == (5 + 1)) {
						System.out.println(tempStr);
						wordsList.add(tempStr);
						for (int j = 1; j < tempStr.length(); j++) {
							String temp = tempStr.substring(0, j);
							WordInfo wi = null;
							if (headWordHM.containsKey(temp)) {
								wi = headWordHM.get(temp);
								int tempCount = wi.getCount() + 1;
								wi.setCount(tempCount);
								if (!wi.isSingle()) {
									wi.addRightList(tempStr.charAt(j));
									if (leftChar != -1) {
										wi.addLiftList((char) leftChar);
									}
								}
								headWordHM.remove(temp);
								headWordHM.put(temp, wi);
							} else {
								if (temp.length() == 1) {
									wi = new WordInfo(true, 1);
								} else {
									wi = new WordInfo(false, 1);
									wi.addRightList(tempStr.charAt(j));
									if (leftChar != -1) {
										wi.addLiftList((char) leftChar);
									}
								}
								headWordHM.put(temp, wi);
							}
						}
						leftChar = tempStr.charAt(0);
						tempStr = tempStr.substring(1);
						tempStr = tempStr + String.valueOf((char) tempchar);
					} else if (tempStr.length() < (5 + 1)) {
						tempStr = tempStr + String.valueOf((char) tempchar);
						leftChar = -1;
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		System.out.println(charNum);
	}

	public static double getCombinationDegree(String strTarget) {
		double max = 0;
		int count = strTarget.length();
		if (count == 1) {
			return 0;
		} else {
			for (int i = 1; i < count; i++) {
				double temp = headWordHM.get(strTarget.substring(0, i))
						.getCount()
						* i
						* headWordHM.get(strTarget.substring(i, count))
								.getCount() * (count - i);
				if (temp > max) {
					max = temp;
				}
			}
			return headWordHM.get(strTarget).getCount() * count / max;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName = "status.data";
		readFileByChars(fileName);
		Iterator iter = headWordHM.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, WordInfo> entry = (Entry<String, WordInfo>) iter
					.next();
			String key = entry.getKey();
			if (key.length() == 1) {
				continue;
			} else {
				double combinationDegree = getCombinationDegree(key);
				entry.getValue().setCombinationDegree(combinationDegree);
				double temp = entry.getValue().getComentropy();
				System.out.println(key + " " + combinationDegree + " " + temp);
			}
		}
	}

}
