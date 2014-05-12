package cn.tongji.lab.database;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import cn.tongji.lab.model.FollowingShip;
import weibo4j.model.Status;
import weibo4j.model.User;

public class ConnectDatabase {
	private final String drivename = "com.mysql.jdbc.Driver";
	private final String url = "jdbc:mysql://127.0.0.1:3306/weibo";
	private final String user = "root";
	private final String password = "123456";
	private String insql = null;
	private Connection conn = null;
	private PreparedStatement statement = null;
	private final int COMMIT_SIZE = 100;

	public ConnectDatabase() {
		// TODO Auto-generated constructor stub
	}

	public Connection ConnectMysql() {
		try {
			Class.forName(drivename);
			conn = (Connection) DriverManager
					.getConnection(url, user, password);
			if (!conn.isClosed()) {
				System.out.println("Succeeded connecting to the Database!");
			} else {
				System.out.println("Falled connecting to the Database!");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return conn;
	}

	public void CutConnection(Connection conn) throws SQLException {
		try {
			if (conn != null)
				;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

	public ResultSet selectSQL(String sql) {
		ResultSet rs = null;
		try {
			statement = conn.prepareStatement(sql);
			rs = statement.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	public boolean isUserExist(String id) throws SQLException {
		String sql = "select * from user where id= '" + id + "';";
		ResultSet rs = selectSQL(sql);
		if (rs.next()) {
			return true;
		}
		return false;
	}

	public boolean isFollowingShipExist(FollowingShip fs) throws SQLException {
		String sql = "select * from following_relationship where source= '"
				+ fs.getSourceId() + "' and target= '" + fs.getTargetId()
				+ "';";
		ResultSet rs = selectSQL(sql);
		if (rs.next()) {
			return true;
		}
		return false;
	}

	public boolean isStatusExist(Status status) throws SQLException {
		String sql = "select * from status where id= '" + status.getId() + "';";
		ResultSet rs = selectSQL(sql);
		if (rs.next()) {
			return true;
		}
		return false;
	}

	public boolean isLexicon(String text) throws SQLException {
		String sql = "select * from lexicon where text= '" + text + "';";
		ResultSet rs = selectSQL(sql);
		if (rs.next()) {
			return true;
		}
		return false;
	}

	public boolean InsertSql(Status status) throws SQLException {
		if (isStatusExist(status)) {
			return false;
		} else {
			insql = "insert into status(id,created_at,mid,idstr,text,source,favorited,truncated,in_reply_to_status_id,in_reply_to_user_id,in_reply_to_screen_name,thumbnail_pic,bmiddle_pic,original_pic,geo,user_id,retweeted_status_id,reposts_count,comments_count,mlevel,visible) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement ps = conn.prepareStatement(insql);
			ps.setString(1, status.getId());
			ps.setString(2, status.getCreatedAt().toString());
			ps.setString(3, status.getMid());
			ps.setString(4, (new Long(status.getIdstr())).toString());
			ps.setString(5, status.getText());
			ps.setString(6, status.getSource().getName());
			ps.setInt(7, (status.isFavorited() ? 1 : 0));
			ps.setInt(8, (status.isTruncated() ? 1 : 0));
			ps.setString(9,
					(new Long(status.getInReplyToStatusId())).toString());
			ps.setString(10, (new Long(status.getInReplyToUserId())).toString());
			ps.setString(
					11,
					((status.getInReplyToScreenName() != null) ? status
							.getInReplyToScreenName() : null));
			ps.setString(12, status.getThumbnailPic());
			ps.setString(13, status.getBmiddlePic());
			ps.setString(14, status.getOriginalPic());
			ps.setString(15, status.getGeo());
			ps.setString(16, status.getUser().getId());
			ps.setString(17, ((status.getRetweetedStatus() != null) ? status
					.getRetweetedStatus().getId() : null));
			ps.setInt(18, status.getRepostsCount());
			ps.setInt(19, status.getCommentsCount());
			ps.setInt(20, status.getMlevel());
			ps.setInt(21, status.getVisible().getType());
			int result = ps.executeUpdate();
			if (result > 0)
				return true;
			else
				return false;
		}
	}

	public void InsertSql(Map<String, Integer> lexiconHM) throws SQLException {

		conn.setAutoCommit(false);
		PreparedStatement pstmt = conn
				.prepareStatement("load data local infile '' "
						+ "into table lexicon fields terminated by ','");
		StringBuilder sb = new StringBuilder();
		Iterator iter = lexiconHM.entrySet().iterator();
		int i = 0;
		while (iter.hasNext()) {
			Entry<String, Integer> entry = (Entry<String, Integer>) iter.next();
			String keyStr = entry.getKey();
			int valueInt = entry.getValue();
			if (!isLexicon(keyStr)) {
				sb.append(keyStr + "," + valueInt + "\n");
				if (i % COMMIT_SIZE == 0) {
					InputStream is = new ByteArrayInputStream(sb.toString()
							.getBytes());
					((com.mysql.jdbc.Statement) pstmt)
							.setLocalInfileInputStream(is);
					pstmt.execute();
					conn.commit();
					sb.setLength(0);
				}
				i++;
			}
		}
		InputStream is = new ByteArrayInputStream(sb.toString().getBytes());
		((com.mysql.jdbc.Statement) pstmt).setLocalInfileInputStream(is);
		pstmt.execute();
		conn.commit();
	}

	public boolean InsertSql(FollowingShip fs) throws SQLException {
		if (isFollowingShipExist(fs)) {
			return false;
		} else {
			insql = "insert into following_relationship(source,target) values(?,?)";
			PreparedStatement ps = conn.prepareStatement(insql);
			ps.setString(1, fs.getSourceId());
			ps.setString(2, fs.getTargetId());
			int result = ps.executeUpdate();
			if (result > 0)
				return true;
			else
				return false;
		}
	}

	public boolean InsertSql(User user) throws SQLException {
		if (isUserExist(user.getId())) {
			return false;
		} else {
			insql = "insert into user(id,screen_name,name,province,city,location,description,url,profile_image_url,domain,weihao,gender,followers_count,friends_count,statuses_count,favourites_count,created_at,following,allow_all_act_msg,verified,verified_type,remark,status_id,allow_all_comment,avatar_large,verified_reason,follow_me,online_status,bi_followers_count,lang) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement ps = conn.prepareStatement(insql);
			ps.setString(1, user.getId());
			ps.setString(2, user.getScreenName());
			ps.setString(3, user.getName());
			ps.setInt(4, user.getProvince());
			ps.setInt(5, user.getCity());
			ps.setString(6, user.getLocation());
			ps.setString(7, user.getDescription());
			ps.setString(8, user.getUrl());
			ps.setString(9, user.getProfileImageUrl());
			ps.setString(10, user.getUserDomain());
			ps.setString(11, user.getWeihao());
			ps.setString(12, user.getGender());
			ps.setInt(13, user.getFollowersCount());
			ps.setInt(14, user.getFriendsCount());
			ps.setInt(15, user.getStatusesCount());
			ps.setInt(16, user.getFavouritesCount());
			ps.setString(17, user.getCreatedAt().toString());
			ps.setInt(18, (user.isFollowing() ? 1 : 0));
			ps.setInt(19, (user.isAllowAllActMsg() ? 1 : 0));
			ps.setInt(20, (user.isVerified() ? 1 : 0));
			ps.setInt(21, user.getverifiedType());
			ps.setString(22, user.getRemark());
			ps.setString(23, user.getStatusId());
			ps.setInt(24, (user.isAllowAllComment() ? 1 : 0));
			ps.setString(25, user.getAvatarLarge());
			ps.setString(26, user.getVerifiedReason());
			ps.setInt(27, (user.isFollowMe() ? 1 : 0));
			ps.setInt(28, user.getOnlineStatus());
			ps.setInt(29, user.getBiFollowersCount());
			ps.setString(30, user.getLang());
			int result = ps.executeUpdate();
			if (result > 0)
				return true;
			else
				return false;
		}
	}
}
