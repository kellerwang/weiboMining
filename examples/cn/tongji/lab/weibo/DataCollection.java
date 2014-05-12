package cn.tongji.lab.weibo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.tongji.lab.database.ConnectDatabase;
import cn.tongji.lab.log.MyLog;
import weibo4j.Friendships;
import weibo4j.Users;
import weibo4j.model.Paging;
import weibo4j.model.User;
import weibo4j.model.UserWapper;
import weibo4j.model.WeiboException;

public class DataCollection {
	private final static String access_token = "2.002h2uFCD35_EE60aed90890x5QPSC";
	private final static String core_user_id = "1919577123";
	private final static int page_count = 200;
	private static Friendships fs = new Friendships();
	private static Users um = new Users();
	private static List<User> usersList = new ArrayList<User>();
	private static MyLog ml = null;

	public static void init() {
		um.client.setToken(access_token);
		fs.client.setToken(access_token);
		ml = new MyLog();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			init();
			User coreUser;
			coreUser = um.showUserById(core_user_id);
			usersList.add(coreUser);
			int followersCount = coreUser.getFollowersCount();
			int friendsCount = coreUser.getFriendsCount();
			List<User> followersList = new ArrayList<User>();
			List<User> friendsList = new ArrayList<User>();
			int pageCountOfFriends = friendsCount / page_count + 1;
			for (int i = 1; i < pageCountOfFriends + 1; i++) {
				Paging pageOfFriends = new Paging(i, page_count);
				UserWapper uw = fs.getFriendsByID(core_user_id, pageOfFriends);
				friendsList.addAll(uw.getUsers());
			}
			long cursorOfFollowers = 0;
			while (true) {
				UserWapper uw = fs.getFollowersById(core_user_id, page_count,
						cursorOfFollowers);
				followersList.addAll(uw.getUsers());
				cursorOfFollowers = uw.getNextCursor();
				if (cursorOfFollowers == 0) {
					break;
				}
			}
			List<User> tempRetainList = new ArrayList(followersList);
			tempRetainList.retainAll(friendsList);
			List<User> tempRemoveList = new ArrayList(followersList);
			tempRemoveList.removeAll(tempRetainList);
			usersList.addAll(friendsList);
			usersList.addAll(tempRemoveList);
//			FollowingThread ft = new FollowingThread(usersList);
//			ft.start();
			FollowerThread ft = new FollowerThread(usersList);
			ft.start();
			// 
			ConnectDatabase cd = new ConnectDatabase();
			Connection conn = cd.ConnectMysql();
			for (User user : usersList) {
				boolean isSuccess = cd.InsertSql(user);
			}
			cd.CutConnection(conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// 写入到日子文件
			ml.getLoger().error("error", e);
		} catch (WeiboException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			// 写入到日子文件
			ml.getLoger().error("error", e1);
		}
	}

}
