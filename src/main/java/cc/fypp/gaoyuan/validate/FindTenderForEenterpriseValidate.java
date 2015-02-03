package cc.fypp.gaoyuan.validate;

import org.apache.commons.lang.StringUtils;

import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class FindTenderForEenterpriseValidate extends Validator{

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
		String merchant_id  = arg0.getPara("merchant_id");
		String status = arg0.getPara("status");
		if(StringUtils.isBlank(merchant_id)){
			setError(MessageUtil.runtimeErroMsg("商户编号不能为空").toString());
			return;
		}else if(StringUtils.isBlank(status)){
			setError(MessageUtil.runtimeErroMsg("状态不能为空").toString());
			return;
		}
		
		if(StringUtils.isNotBlank(arg0.getPara("tender_id"))){
			if(StringUtils.isBlank(arg0.getPara("is_before"))){
				setError(MessageUtil.runtimeErroMsg("is_before的值不能为空").toString());
				return;
			}
		}

		if(!Constants.TENDER_STATUS.UNTREATED.equals(status)&&!Constants.TENDER_STATUS.AGREE.equals(status)){
			setError(MessageUtil.runtimeErroMsg("状态值为不在允许的范围内").toString());
			return;
		}

	}


}
