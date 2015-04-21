package cc.fypp.gaoyuan.validate;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.validate.Validator;

public class SubmitOrderCompleteValidate extends Validator{

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
		String order_id = arg0.getPara("order_id");
		if(StringUtils.isBlank(order_id)){
			setError(MessageUtil.runtimeErroMsg("订单编号不能为空").toString());
			return;
		}
		
		List<Record> task_lists = Db.find("select o.* from task_info o where o.order_id = ?", order_id);
		if(task_lists.size()==0){
			setError(MessageUtil.runtimeErroMsg("你没有提交任何的任务照片,不能确认完成任务").toString());
			return;
		}
		

	}


}
