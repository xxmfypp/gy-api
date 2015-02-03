package cc.fypp.gaoyuan.validate;

import org.apache.commons.lang.StringUtils;

import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;
import cc.fypp.gaoyuan.common.validate.ValidateUtil;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class LoginValidate extends Validator{
	
	private String error;
	
	private void setError(String error){
		addError(Constants.ERRORMSG, error);
		this.error=error;
	}

	@Override
	protected void handleError(Controller arg0) {
		// TODO Auto-generated method stub
		arg0.renderJson(error);
	}

	@Override
	protected void validate(Controller arg0) {
		// TODO Auto-generated method stub
		String username = arg0.getPara("login_name");
		String pwd = arg0.getPara("pwd");
		if(StringUtils.isBlank(username)){
			setError(MessageUtil.runtimeErroMsg("用户名不能为空").toString());
			return;
		}
		
		if(!ValidateUtil.isPhoneNum(username)){
			setError(MessageUtil.runtimeErroMsg("用户名应该为合法的手机号码").toString());
			return;
		}
		
		if(StringUtils.isBlank(pwd)){
			setError(MessageUtil.runtimeErroMsg("密码不能为空").toString());
			return;
		}
		
	}
	
	
}
