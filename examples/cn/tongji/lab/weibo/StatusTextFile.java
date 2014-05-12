package cn.tongji.lab.weibo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import cn.tongji.lab.database.ConnectDatabase;

public class StatusTextFile {

	public StatusTextFile() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		ConnectDatabase cd = new ConnectDatabase();
		Connection conn = cd.ConnectMysql();
		String sqlSelectAll = "select * from status";
		String fileName = "status.data";
		ResultSet rs = cd.selectSQL(sqlSelectAll);
		try {
			while (rs.next()) {
				String docText = rs.getString("text");
				FileWriter fw;
				try {
					fw = new FileWriter(fileName, true);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(docText);
					bw.newLine();
					bw.close();
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			cd.CutConnection(conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
