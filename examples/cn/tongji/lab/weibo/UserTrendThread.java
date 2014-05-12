package cn.tongji.lab.weibo;

import java.util.List;

import weibo4j.Account;
import weibo4j.model.UserTrend;
import weibo4j.model.WeiboException;

public class UserTrendThread extends Thread {
	private final String access_token = "2.002h2uFCD35_EE60aed90890x5QPSC";
	private final String core_user_id = "1919577123";
	private weibo4j.Trend wt = new weibo4j.Trend();
	private Account am = new Account();
	private MyLog ml = null;

	public UserTrendThread() {
		// TODO Auto-generated constructor stub
		wt.client.setToken(access_token);
		am.client.setToken(access_token);
		ml = new MyLog();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			List<UserTrend> utList = wt.getTrends(core_user_id);
			System.out.println(utList.size());
			for(UserTrend ut : utList){
				System.out.println(ut.getHotword() + ut.getNum() + ut.gettrendId());
			}
		} catch (WeiboException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
