package cn.tongji.lab.weibo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.tongji.lab.database.ConnectDatabase;
import cn.tongji.lab.log.MyLog;
import cn.tongji.lab.model.FollowingShip;
import weibo4j.Account;
import weibo4j.Friendships;
import weibo4j.model.Paging;
import weibo4j.model.RateLimitStatus;
import weibo4j.model.User;
import weibo4j.model.UserWapper;
import weibo4j.model.WeiboException;

public class FollowerThread extends Thread {
	private final String access_token = "2.002h2uFCD35_EE60aed90890x5QPSC";
	private Friendships fs = new Friendships();
	private Account am = new Account();
	private List<User> usersList;
	private List<FollowingShip> followingShipList = new ArrayList<FollowingShip>();
	private MyLog ml = null;

	public FollowerThread(List<User> usersList) {
		// TODO Auto-generated constructor stub
		this.usersList = new ArrayList(usersList);
		fs.client.setToken(access_token);
		am.client.setToken(access_token);
		ml = new MyLog();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println(usersList.size());
		int invokingTime = 0;
		for (User tempUser : usersList) {
			UserWapper uw = null;
			int pagePage = 1;
			List<User> followerList = new ArrayList<User>();
			while (true) {
				Paging page = new Paging(pagePage, 200);
				try {
					uw = fs.getFriendsChainFollowers(tempUser.getId(), page);
					invokingTime++;
				} catch (WeiboException e) {
					// TODO Auto-generated catch block
					if (e.getErrorCode() == 10023) {
						try {
							RateLimitStatus json = am
									.getAccountRateLimitStatus();
							sleep(json.getResetTimeInSeconds() * 1000);
							uw = fs.getFriendsChainFollowers(tempUser.getId(),
									page);
							invokingTime++;
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
				pagePage++;
				followerList.addAll(uw.getUsers());
				if (uw.getNextCursor() == 0) {
					break;
				}
			}
			System.out.println(followerList.size());
			ConnectDatabase cd = new ConnectDatabase();
			Connection conn = cd.ConnectMysql();
			for (User followerUser : followerList) {
				FollowingShip fs = new FollowingShip(tempUser.getId(),
						followerUser.getId());
				try {
					boolean isSuccess = cd.InsertSql(fs);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					ml.getLoger().error("error", e);
				}
			}
			try {
				cd.CutConnection(conn);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ml.getLoger().error("error", e);
			}
			System.out.println("invokingTime: " + invokingTime);
		}
	}
}
