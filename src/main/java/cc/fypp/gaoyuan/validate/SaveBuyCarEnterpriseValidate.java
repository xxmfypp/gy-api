package cc.fypp.gaoyuan.validate;

import org.apache.commons.lang.StringUtils;

import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.validate.Validator;

public class SaveBuyCarEnterpriseValidate extends Validator{

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
		String merchant_id = arg0.getPara("merchant_id");
		if(StringUtils.isBlank(user_id)){
			setError(MessageUtil.runtimeErroMsg("用户编号编号不能为空").toString());
			return;
		}
		long size = Db.queryLong("select count(*) from buy_car o where o.user_id = ?",user_id);
		
		if(size==0){
			setError(MessageUtil.runtimeErroMsg("购物车不能为空").toString());
			return;
		}
		
		if(StringUtils.isBlank(merchant_id)){
			setError(MessageUtil.runtimeErroMsg("商户编号不能为空").toString());
			return;
		}

	}


}
