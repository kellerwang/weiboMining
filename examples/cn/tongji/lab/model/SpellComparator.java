package cn.tongji.lab.model;

import java.util.Comparator;

public class SpellComparator implements Comparator {

	public SpellComparator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compare(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		try {
			// 取得比较对象的汉字编码，并将其转换成字符串
			String s1 = new String(arg0.toString());
			String s2 = new String(arg1.toString());
			// 运用String类的 compareTo（）方法对两对象进行比较
			return s1.compareTo(s2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
