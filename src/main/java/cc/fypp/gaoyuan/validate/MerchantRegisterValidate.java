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

public class MerchantRegisterValidate extends Validator{

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
		String merchant_id = arg0.getPara("merchant_id");
		String login_name = arg0.getPara("login_name");
		String pwd = arg0.getPara("pwd");
		String company = arg0.getPara("company");
		String phone_num = arg0.getPara("phone_num");
		String address = arg0.getPara("address");
		String mail = arg0.getPara("mail");
		String registration_num = arg0.getPara("registration_num");
		String city = arg0.getPara("city");
		String legal_name = arg0.getPara("legal_name");
		String legal_cardno = arg0.getPara("legal_cardno");
		String legal_sex = arg0.getPara("legal_sex");
		String zhifubao_code = arg0.getPara("zhifubao_code");
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
		
		if(StringUtils.isBlank(pwd)){
			setError(MessageUtil.runtimeErroMsg("密码不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(company)){
			setError(MessageUtil.runtimeErroMsg("公司名称不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(phone_num)){
			setError(MessageUtil.runtimeErroMsg("公司电话不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(address)){
			setError(MessageUtil.runtimeErroMsg("公司地址不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(mail)){
			setError(MessageUtil.runtimeErroMsg("公司邮箱不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(registration_num)){
			setError(MessageUtil.runtimeErroMsg("企业注册号不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(city)){
			setError(MessageUtil.runtimeErroMsg("所在城市不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(legal_name)){
			setError(MessageUtil.runtimeErroMsg("法人姓名不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(legal_cardno)){
			setError(MessageUtil.runtimeErroMsg("法人身份证不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(legal_sex)){
			setError(MessageUtil.runtimeErroMsg("法人性别不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(merchant_id)){
			setError(MessageUtil.runtimeErroMsg("企业编号").toString());
			return;
		}
		if(StringUtils.isBlank(zhifubao_code)){
			setError(MessageUtil.runtimeErroMsg("支付宝账号不能为空").toString());
			return;
		}

		Record  record =Db.findFirst("select o.* from smscode_info o where o.phone_num=? and o.type=? and o.status=? and o.create_time >=? and o.code_content=?", new Object[]{login_name,Constants.SMS.REGISTER_CODE,Constants.SMS_STATUS.UNCECK,(System.currentTimeMillis()-ConfigFileUtil.getTimeOut()),verify_code});
		if(record==null){
			setError(MessageUtil.runtimeErroMsg("验证码错误或已经失效").toString());
			return;
		}else{
			record.set("status", Constants.SMS_STATUS.CHECK);
			Db.update("smscode_info", record);
		}

	}


}
