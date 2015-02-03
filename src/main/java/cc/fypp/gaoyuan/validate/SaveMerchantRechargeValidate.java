package cc.fypp.gaoyuan.validate;

import org.apache.commons.lang.StringUtils;

import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class SaveMerchantRechargeValidate extends Validator{

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
		String amount = arg0.getPara("amount");
		String user_id = arg0.getPara("merchant_id");
		if(StringUtils.isBlank(amount)){
			setError(MessageUtil.runtimeErroMsg("充值金额不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(user_id)){
			setError(MessageUtil.runtimeErroMsg("商户编号不能为空").toString());
			return;
		}

	}


}
