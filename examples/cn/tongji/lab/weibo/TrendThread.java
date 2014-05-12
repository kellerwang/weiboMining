package cn.tongji.lab.weibo;

import java.util.List;

import weibo4j.Account;
import weibo4j.Timeline;
import weibo4j.model.Trend;
import weibo4j.model.Trends;
import weibo4j.model.WeiboException;

public class TrendThread extends Thread {
	private final String access_token = "2.002h2uFCD35_EE60aed90890x5QPSC";
	private weibo4j.Trend wt = new weibo4j.Trend();
	private Account am = new Account();
	private MyLog ml = null;

	public TrendThread() {
		// TODO Auto-generated constructor stub
		wt.client.setToken(access_token);
		am.client.setToken(access_token);
		ml = new MyLog();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			List<Trends> tList = wt.getTrendsHourly();
			System.out.println(tList.size());
			for (Trends ts : tList) {
				Trend[] trends = ts.getTrends();
				System.out.println(trends.length);
			}

		} catch (WeiboException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
