package cn.tongji.lab.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class WordInfo {
	private StringBuffer right = null;
	private StringBuffer lift = null;
	private int count = 0;
	private boolean single;
	private double combinationDegree;
	private double comentropy;

	public double getComentropy() {
		if (right == null || lift == null) {
			return 0;
		}
		if (right.length() == 0 || lift.length() == 0) {
			return 0;
		} else {
			double rightTempComentropy = getTempComentropy(right);
			double liftTempComentropy = getTempComentropy(lift);
			comentropy = rightTempComentropy < liftTempComentropy ? rightTempComentropy
					: liftTempComentropy;
			return comentropy;
		}
	}

	public double getTempComentropy(StringBuffer temp) {
		double Length = temp.length();
		Map<String, Integer> charHM = new HashMap<String, Integer>();
		for (int i = 0; i < Length; i++) {
			String tempKey = String.valueOf(temp.charAt(i));
			if (charHM.containsKey(tempKey)) {
				int tempVaule = charHM.get(tempKey) + 1;
				charHM.remove(tempKey);
				charHM.put(tempKey, tempVaule);
			} else {
				charHM.put(tempKey, 1);
			}
		}
		double comentropyTemp = 0;
		Iterator iter = charHM.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Integer> entry = (Entry<String, Integer>) iter.next();
			String key = entry.getKey();
			double value = entry.getValue();
			comentropyTemp -= ((value / Length) * Math.log(value / Length));
		}
		return comentropyTemp;
	}

	public void setComentropy(double comentropy) {
		this.comentropy = comentropy;
	}

	public double getCombinationDegree() {
		return combinationDegree;
	}

	public void setCombinationDegree(double combinationDegree) {
		this.combinationDegree = combinationDegree;
	}

	public boolean isSingle() {
		return single;
	}

	public void setSingle(boolean single) {
		this.single = single;
	}

	public void init() {
		this.right = new StringBuffer();
		this.lift = new StringBuffer();
	}

	public void addRightList(char newChar) {
		this.right.append(newChar);
	}

	public void addLiftList(char newChar) {
		this.lift.append(newChar);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public WordInfo() {
		// TODO Auto-generated constructor stub

	}

	public WordInfo(boolean single, int count) {
		// TODO Auto-generated constructor stub
		this.single = single;
		this.count = count;
		if (!this.single) {
			init();
		}
	}

}
