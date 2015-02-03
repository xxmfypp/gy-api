package cc.fypp.gaoyuan.validate;

import org.apache.commons.lang.StringUtils;

import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class UpdateUserValidate extends Validator{
	
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
		String login_name = arg0.getPara("login_name");
		if(StringUtils.isBlank(login_name)){
			setError(MessageUtil.runtimeErroMsg("登录名不能为空").toString());
			return;
		}
	}
	
	
}
