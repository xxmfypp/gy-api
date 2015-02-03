package cc.fypp.gaoyuan.validate;

import org.apache.commons.lang.StringUtils;

import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;
import cc.fypp.gaoyuan.common.validate.ValidateUtil;
import cc.fypp.gaoyuan.config.ConfigFileUtil;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.validate.Validator;

public class FindPwdValidate extends Validator{

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
		String verify_code = arg0.getPara("verify_code");
		String login_name = arg0.getPara("login_name");
		String new_pwd = arg0.getPara("new_pwd");
		if(StringUtils.isBlank(verify_code)){
			setError(MessageUtil.runtimeErroMsg("验证码不能为空").toString());
			return;
		}

		if(StringUtils.isBlank(login_name)){
			setError(MessageUtil.runtimeErroMsg("登陆名不能为空").toString());
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
		
		Record  record =Db.findFirst("select o.* from smscode_info o where o.phone_num=? and o.type=? and o.status=? and o.create_time >=? and o.code_content=?", new Object[]{login_name,Constants.SMS.PWD_CODE,Constants.SMS_STATUS.UNCECK,(System.currentTimeMillis()-ConfigFileUtil.getTimeOut()),verify_code});
		if(record==null){
			setError(error = MessageUtil.runtimeErroMsg("验证码错误或已经失效").toString());
			return;
		}else{
			record.set("status", Constants.SMS_STATUS.CHECK);
			Db.update("smscode_info", record);
		}


	}

}
