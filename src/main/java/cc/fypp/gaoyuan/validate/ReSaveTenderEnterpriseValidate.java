package cc.fypp.gaoyuan.validate;

import org.apache.commons.lang.StringUtils;

import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;
import cc.fypp.gaoyuan.common.sms.SmsUtil;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.validate.Validator;

public class ReSaveTenderEnterpriseValidate extends Validator{

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
		String tender_id = arg0.getPara("tender_id");
		String merchant_id = arg0.getPara("merchant_id");
		if(StringUtils.isBlank(tender_id)){
			setError(MessageUtil.runtimeErroMsg("招标编号不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(merchant_id)){
			setError(MessageUtil.runtimeErroMsg("商户编号不能为空").toString());
			return;
		}
		
		String sms_template = SmsUtil.getSmsTemplate(Constants.SMS_TYPE.TASK_NOTE.toString());
		if(StringUtils.isBlank(sms_template)){
			setError(MessageUtil.runtimeErroMsg("短信模板为空"));
			return;
		}
		
		Record tender_enterprise = Db.findFirst("select * from tender_enterprise where tender_id=? and merchant_id = ?", new Object[]{tender_id,merchant_id});
		if(tender_enterprise==null){
			setError(MessageUtil.runtimeErroMsg("对应商户的招标信息不存在"));
			return;
		}
		
		if(!tender_enterprise.getStr("status").equals(Constants.TENDER_STATUS.TIMEOUT)){
			setError(MessageUtil.runtimeErroMsg("当前状态不允许重新发送招标"));
			return;
		}
		

	}


}
