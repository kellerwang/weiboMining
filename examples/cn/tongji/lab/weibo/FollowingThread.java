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
import weibo4j.Users;
import weibo4j.model.RateLimitStatus;
import weibo4j.model.User;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONObject;

public class FollowingThread extends Thread {

	private final String access_token = "2.002h2uFCD35_EE60aed90890x5QPSC";
	private Friendships fs = new Friendships();
	private Account am = new Account();
	private List<User> usersList;
	private List<FollowingShip> followingShipList = new ArrayList<FollowingShip>();
	private MyLog ml = null;
	
	public FollowingThread(List<User> usersList) {
		// TODO Auto-generated constructor stub
		this.usersList = new ArrayList(usersList);
		fs.client.setToken(access_token);
		am.client.setToken(access_token);
		ml = new MyLog();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		List<User> deleteList = new ArrayList(usersList);
		for (User cursorUser : usersList) {
			for (User tempUser : deleteList) {
				String sourceId = cursorUser.getId();
				String targetId = tempUser.getId();
				JSONObject joAll = null;
				try {
					joAll = fs.getFriendsRelationship(sourceId, targetId);
				} catch (WeiboException e) {
					// TODO Auto-generated catch block
					if (e.getErrorCode() == 10023) {
						try {
							RateLimitStatus json = am.getAccountRateLimitStatus();
							sleep(json.getResetTimeInSeconds() * 1000);
							joAll = fs.getFriendsRelationship(sourceId, targetId);
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
				JSONObject joTarget = joAll.optJSONObject("target");
				if (joTarget.optString("followed_by").equals("true")) {
					FollowingShip fsTemp = new FollowingShip(targetId, sourceId);
					followingShipList.add(fsTemp);
				}
				if (joTarget.optString("following").equals("true")) {
					FollowingShip fsTemp = new FollowingShip(sourceId, targetId);
					followingShipList.add(fsTemp);
				}
			}
			deleteList.remove(cursorUser);
		}
		for(FollowingShip fs : followingShipList){
			ConnectDatabase cd = new ConnectDatabase();
			Connection conn = cd.ConnectMysql();
			for(User user : usersList){
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
		}
	}
}
