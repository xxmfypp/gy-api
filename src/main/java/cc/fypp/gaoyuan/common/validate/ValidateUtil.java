package cc.fypp.gaoyuan.common.validate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class ValidateUtil {

	public static boolean isPhoneNum(String phoneNum){
		if(StringUtils.isBlank(phoneNum)){
			return false;
		}
		Pattern p = Pattern.compile("^((13[0-9])|(17[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");  
		Matcher m = p.matcher(phoneNum);  
		return m.matches();
	}

}
