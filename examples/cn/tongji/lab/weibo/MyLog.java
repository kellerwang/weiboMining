package cn.tongji.lab.weibo;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class MyLog {
	private Logger loger;
	
	public Logger getLoger() {
		return loger;
	}

	public MyLog() {
		// TODO Auto-generated constructor stub
		// 获得当前目录路径
		String filePath = this.getClass().getResource("/").getPath();
		// 找到log4j.properties配置文件所在的目录(已经创建好)
		filePath = filePath.substring(1).replace("bin", "examples");
		// 获得日志类loger的实例
		loger = Logger.getLogger(this.getClass());
		// loger所需的配置文件路径
		PropertyConfigurator.configure("/" + filePath + "log4j.properties");
	}

}
