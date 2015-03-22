package cc.fypp.gaoyuan.validate;

import java.math.BigDecimal;

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
		/*if(StringUtils.isBlank(order_id)){
			setError(MessageUtil.runtimeErroMsg("订单编号不能为空").toString());
			return;
		}*/
		String tender_id = arg0.getPara("tender_id");
		String merchant_id = arg0.getPara("merchant_id");
		String total_amount = arg0.getPara("total_amount");
		if(StringUtils.isBlank(user_id)){
			setError(MessageUtil.runtimeErroMsg("用户编号不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(tender_id)){
			setError(MessageUtil.runtimeErroMsg("招标编号不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(merchant_id)){
			setError(MessageUtil.runtimeErroMsg("商户编号不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(total_amount)){
			setError(MessageUtil.runtimeErroMsg("总金额不能为空").toString());
			return;
		}

	}


}
