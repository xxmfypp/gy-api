package cc.fypp.gaoyuan.validate;

import org.apache.commons.lang.StringUtils;

import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class UserPaymentValidate extends Validator{

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
//		String order_id = arg0.getPara("order_id");
		String user_id = arg0.getPara("user_id");
		String amount = arg0.getPara("amount");
		/*if(StringUtils.isBlank(order_id)){
			setError(MessageUtil.runtimeErroMsg("订单编号不能为空").toString());
			return;
		}*/
		if(StringUtils.isBlank(user_id)){
			setError(MessageUtil.runtimeErroMsg("用户编号不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(amount)){
			setError(MessageUtil.runtimeErroMsg("付款金额不能为空").toString());
			return;
		}

	}


}
