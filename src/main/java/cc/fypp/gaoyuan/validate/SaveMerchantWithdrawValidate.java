package cc.fypp.gaoyuan.validate;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.validate.Validator;

public class SaveMerchantWithdrawValidate extends Validator{

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
		String merchant_id = arg0.getPara("merchant_id");
		String amount = arg0.getPara("amount");
		if(StringUtils.isBlank(merchant_id)){
			setError(MessageUtil.runtimeErroMsg("商户编号不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(amount)){
			setError(MessageUtil.runtimeErroMsg("提款金额不能为空").toString());
			return;
		}
		Record merchant_info =  Db.findById("merchant_info", "merchant_id", merchant_id,"*");
		if(merchant_info.getBigDecimal("balance").compareTo(new BigDecimal(amount))==-1){
			setError(MessageUtil.runtimeErroMsg("提款金额不能大约余额").toString());
			return;
		}

	}


}
