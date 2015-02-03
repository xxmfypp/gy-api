package cc.fypp.gaoyuan.validate;

import org.apache.commons.lang.StringUtils;

import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class SaveTaskInfoValidate extends Validator{

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

		String task_id = arg0.getPara("task_id");
		String order_id = arg0.getPara("order_id");
		if(StringUtils.isBlank(task_id)){
			setError(MessageUtil.runtimeErroMsg("任务编号不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(order_id)){
			setError(MessageUtil.runtimeErroMsg("订单编号不能为空").toString());
			return;
		}

	}


}
