package cc.fypp.gaoyuan.common.msg;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 * 消息工具类
 * 
 * @author xuman.xu
 * 
 */
public class MessageUtil {

	/**
	 * 运行时异常信息
	 * 
	 * @param message
	 *            异常信息
	 * @return 异常信息
	 */
	public static String runtimeErroMsg(String message) {
		JSONObject json = new JSONObject();
		json.put(Constants.Result.STATUS, "1");
		json.put(Constants.Result.MESSAGE, message);
		return json.toString();
	}

	public static String jsonExceptionMsg(int status, String message) {
		JSONObject json = new JSONObject();
		try {
			json.put(Constants.Result.STATUS, String.valueOf(status));
			if (StringUtils.isBlank(message)) {
				json.put(Constants.Result.MESSAGE, "");
			} else {
				json.put(Constants.Result.MESSAGE, message);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	public static String jsonMsg(String status, String message, Object data) {
		JSONObject json = new JSONObject();
		try {
			json.put(Constants.Result.STATUS, String.valueOf(status));
			if (StringUtils.isBlank(message)) {
				json.put(Constants.Result.MESSAGE, "");
			} else {
				json.put(Constants.Result.MESSAGE, message);
			}
			if (data == null) {
				json.put(Constants.Result.DATA, "");
			} else {
				json.put(Constants.Result.DATA, data);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	/**
	 * 运行成功信息
	 *
	 * @param message
	 *            成功信息
	 * @param data
	 *            返回Object对象
	 * @return 成功信息
	 */
	public static String successMsg(String message, Object data) {
		JSONObject json = new JSONObject();
		json.put(Constants.Result.STATUS, "0");
		if (StringUtils.isBlank(message)) {
			json.put(Constants.Result.MESSAGE, "");
		} else {
			json.put(Constants.Result.MESSAGE, message);
		}
		if (data == null) {
			json.put(Constants.Result.DATA, "");
		} else {
			json.put(Constants.Result.DATA, data);
		}
		return json.toString();
	}
	
	
	public static String getFormatStringByVeloCity(String template, Map<String, String> templateParam) {
        VelocityEngine ve = new VelocityEngine();
        StringWriter writer = new StringWriter();
        VelocityContext context = new VelocityContext(templateParam);
        ve.evaluate(context, writer, "", template);
        String content = writer.toString();
        try {
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return content;
    }


}
