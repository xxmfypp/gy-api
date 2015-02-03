package cc.fypp.gaoyuan.validate;

import org.apache.commons.lang.StringUtils;

import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;
import cc.fypp.gaoyuan.common.validate.ValidateUtil;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.validate.Validator;

public class UpdateUserPwdValidate extends Validator{

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
		String new_pwd = arg0.getPara("new_pwd");
		String old_pwd = arg0.getPara("old_pwd");
		if(StringUtils.isBlank(login_name)){
			setError(MessageUtil.runtimeErroMsg("登录名不能为空").toString());
			return;
		}
		
		if(!ValidateUtil.isPhoneNum(login_name)){
			setError(MessageUtil.runtimeErroMsg("登陆名应该为合法的手机号码").toString());
			return;
		}
		
		if(StringUtils.isBlank(new_pwd)){
			setError(MessageUtil.runtimeErroMsg("新密码不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(old_pwd)){
			setError(MessageUtil.runtimeErroMsg("原始密码不能为空").toString());
			return;
		}
		Record  record =Db.findFirst("select o.* from user_info o where o.login_name = ? and o.pwd = ?", new Object[]{login_name,old_pwd});
		if(record==null){
			setError(MessageUtil.runtimeErroMsg("原始密码错误").toString());
			return;
		}

	}


}
