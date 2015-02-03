package cc.fypp.gaoyuan.validate;

import org.apache.commons.lang.StringUtils;

import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class FindOrderForEenterpriseValidate extends Validator{

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
		if(StringUtils.isBlank(merchant_id)){
			setError(MessageUtil.runtimeErroMsg("商户编号不能为空").toString());
			return;
		}
		
		if(StringUtils.isNotBlank(arg0.getPara("order_id"))){
			if(StringUtils.isBlank(arg0.getPara("is_before"))){
				setError(MessageUtil.runtimeErroMsg("is_before的值不能为空").toString());
				return;
			}
		}
	}


}
