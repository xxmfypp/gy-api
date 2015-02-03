package cc.fypp.gaoyuan.validate;

import org.apache.commons.lang.StringUtils;

import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.validate.Validator;

public class MerchantReceiptValidate extends Validator{
	
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
		String tender_enterprise_id = arg0.getPara("id");
		String status = arg0.getPara("status");
		
		if(StringUtils.isBlank(tender_enterprise_id)){
			setError(MessageUtil.runtimeErroMsg("回执编号不能为空").toString());
			return;
		}
		
		if(StringUtils.isBlank(status)){
			setError(MessageUtil.runtimeErroMsg("回执状态不能为空").toString());
			return;
		}
		Record record = Db.findById("tender_enterprise", Integer.valueOf(tender_enterprise_id));
		
		if(record==null){
			setError(MessageUtil.runtimeErroMsg("回执信息不存在").toString());
			return;
		}else if(!record.getStr("status").equals(Constants.TENDER_STATUS.UNTREATED)){
			setError(MessageUtil.runtimeErroMsg("回执状态已经无法修改").toString());
			return;
		}
	}
	
	
}
