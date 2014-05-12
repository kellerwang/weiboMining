package cn.tongji.lab.weibo;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import cn.tongji.lab.model.WordInfo;
import weibo4j.model.User;

public class TestObject {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StatusThread st = new StatusThread();
		st.start();
		// TrendThread tt = new TrendThread();
		// tt.start();
		// UserTrendThread utt = new UserTrendThread();
		// utt.start();
	}
}
