package cn.tongji.lab.weibo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import weibo4j.Account;
import weibo4j.Friendships;
import weibo4j.Timeline;
import weibo4j.model.Paging;
import weibo4j.model.RateLimitStatus;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;

public class StatusThread extends Thread {

	private final String access_token = "2.002h2uFCD35_EE60aed90890x5QPSC";
	private Timeline tl = new Timeline();
	private Account am = new Account();
	private MyLog ml = null;

	public StatusThread() {
		// TODO Auto-generated constructor stub
		tl.client.setToken(access_token);
		am.client.setToken(access_token);
		ml = new MyLog();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			ConnectDatabase cd = new ConnectDatabase();
			Connection conn = cd.ConnectMysql();
			int pageNume = 1;
			while (true) {
				Paging page = new Paging(pageNume, 200);
				StatusWapper sw = null;
				try {
					sw = tl.getFriendsTimeline(0, 0, page);
				} catch (WeiboException e) {
					// TODO Auto-generated catch block
					if (e.getErrorCode() == 10023) {
						try {
							RateLimitStatus json = am.getAccountRateLimitStatus();
							sleep(json.getResetTimeInSeconds() * 1000);
							sw = tl.getFriendsTimeline(0, 0, page);
						} catch (WeiboException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							ml.getLoger().error("error", e1);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							ml.getLoger().error("error", e1);
						}
					} else {
						e.printStackTrace();
						ml.getLoger().error("error", e);
					}
				}
				List<Status> statusList = sw.getStatuses();
				for (Status status : statusList) {
					boolean isSuccess = cd.InsertSql(status);
					System.out.println(status.getCreatedAt());
				}
				pageNume++;
				if(sw.getNextCursor() == 0){
					cd.CutConnection(conn);
					sleep(12*60*60*1000);
					pageNume = 1;
					break;
				}
			}
//			cd.CutConnection(conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
