package cc.fypp.gaoyuan.validate;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.validate.Validator;

public class SaveUserWithdrawValidate extends Validator{

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
		String user_id = arg0.getPara("user_id");
		String amount = arg0.getPara("amount");
		String zhifubao_code = arg0.getPara("zhifubao_code");
		if(StringUtils.isBlank(user_id)){
			setError(MessageUtil.runtimeErroMsg("用户编号不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(amount)){
			setError(MessageUtil.runtimeErroMsg("提款金额不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(zhifubao_code)){
			setError(MessageUtil.runtimeErroMsg("支付宝账号不能为空").toString());
			return;
		}
		Record user_info =  Db.findById("user_info", "user_id", Integer.valueOf(user_id),"*");
		if(user_info.getBigDecimal("balance").compareTo(new BigDecimal(amount))==-1){
			setError(MessageUtil.runtimeErroMsg("提款金额不能大约余额").toString());
			return;
		}

	}


}
