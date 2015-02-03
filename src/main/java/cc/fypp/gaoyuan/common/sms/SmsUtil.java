package cc.fypp.gaoyuan.common.sms;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import cc.fypp.gaoyuan.config.ConfigFileUtil;


public class SmsUtil {

	/**
	 * 发送短信
	 * @param to
	 * @param msg
	 * @return
	 */
	public static boolean sendMessage(String to,String msg){
		Sender sender = new Sender(ConfigFileUtil.getSmsName(),ConfigFileUtil.getSmsPwd());
		return sender.massSend(to, msg, "0", "").contains("success");
	}
	
	/**
	 * 获取短信模板
	 * @param type 短信模板类型
	 * @return 短信内容
	 */
	public static String getSmsTemplate(String type){
		Record sms_template = Db.findFirst("select o.* from sms_template o where o.type = ?", type);
		if(sms_template!=null){
			return sms_template.getStr("content");
		}else{
			return null;
		}
	}

}

