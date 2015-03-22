package cc.fypp.gaoyuan.config;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationFactory;
import org.apache.log4j.Logger;

import java.io.File;

public class ConfigFileUtil {

	protected final static Logger logger = Logger.getLogger(ConfigFileUtil.class);

	private static Configuration config = null;

	private static long lastModifyTime = 0l;

	private static String configHome = System.getenv("gy_home");

	private static String configFilePath = configHome + File.separator + "conf" + File.separator + "gy-api1.properties";

	public static void getConfig() {
		try {
			if (config == null) {
				lastModifyTime = new File(configFilePath).lastModified();
				loadConfig(configFilePath);
			} else {
				if (getReload()) {
					long nowLastModifyTime = new File(configFilePath).lastModified();
					if (nowLastModifyTime != lastModifyTime) {
						logger.info("检测到配置文件有变动,重新加载配置文件");
						loadConfig(configFilePath);
						lastModifyTime = nowLastModifyTime;
					}
				}
			}
		} catch (ConfigurationException e) {

		}
	}

	private static void loadConfig(String configFilePath) throws ConfigurationException {
		System.setProperty("config.file", configFilePath);
		ConfigurationFactory factory = new ConfigurationFactory("gy-api1.xml");
		config = factory.getConfiguration();
	}

	/**
	 * 获取是否动态家在配置文件
	 *
	 * @return
	 */
	public static Boolean getReload() {
		if (config == null) {
			return true;
		} else {
			return config.getBoolean("config.reload", true);
		}
	}

	/**
	 * 获取数据库连接地址
	 *
	 * @return
	 */
	public static String getDbConnectionUrl() {
		getConfig();
		return config.getString("db.connection.url");
	}

	/**
	 * 获取数据库用户名
	 *
	 * @return
	 */
	public static String getDbConnectionUername() {
		getConfig();
		return config.getString("db.connection.username");
	}

	/**
	 * 获取数据库密码
	 *
	 * @return
	 */
	public static String getDbConnectionPassword() {
		getConfig();
		return config.getString("db.connection.password");
	}



	/**
	 * 获取数据库驱动
	 *
	 * @return
	 */
	public static String getDbConnectionDriver() {
		getConfig();
		return config.getString("db.connection.driver", "com.mysql.jdbc.Driver");
	}


	/**
	 * 系统是否为测试状态
	 *
	 * @return
	 */
	public static Boolean getJfinalDebug() {
		getConfig();
		return config.getBoolean("jfinal.debug", true);
	}

	public static String getSmsName() {
		getConfig();
		return config.getString("sms.name", "gaoyuanapp");
	}

	public static String getSmsPwd() {
		getConfig();
		return config.getString("sms.pwd", "gy20141118");
	}

	public static String getFilePath() {
		getConfig();
		return config.getString("file.path", "D:/upload");
	}

	public static Integer getTimeOut(){
		getConfig();
		return config.getInteger("time.out", 1200000);
	}

	public static Integer getTenderTimeout(){
		getConfig();
		return config.getInteger("tender.time.out", 1000*60*30);
	}

	public static String getAliPayPartner(){
		getConfig();
		return config.getString("alipay.partner");
	}
	
	public static String getAliPayPrivate_key(){
		getConfig();
		return config.getString("alipay.private_key");
	}
	
	public static String getAliPayPublic_key(){
		getConfig();
		return config.getString("alipay.public_key");
	}
	
	public static String getAliPayLog_path(){
		getConfig();
		return config.getString("alipay.log_path");
	}


}
