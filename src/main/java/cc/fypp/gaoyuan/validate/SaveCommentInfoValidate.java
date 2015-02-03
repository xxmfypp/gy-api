package cc.fypp.gaoyuan.validate;

import org.apache.commons.lang.StringUtils;

import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.validate.Validator;

public class SaveCommentInfoValidate extends Validator{

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
		String order_id  = arg0.getPara("order_id");
		String merchant_id = arg0.getPara("merchant_id");
		String user_id = arg0.getPara("user_id");
		String quality = arg0.getPara("quality");
		String efficiency = arg0.getPara("efficiency");
		String service = arg0.getPara("service");
		String comment = arg0.getPara("comment");
		if(StringUtils.isBlank(order_id)){
			setError(MessageUtil.runtimeErroMsg("订单编号不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(merchant_id)){
			setError(MessageUtil.runtimeErroMsg("商户编号不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(user_id)){
			setError(MessageUtil.runtimeErroMsg("商户编号不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(quality)){
			setError(MessageUtil.runtimeErroMsg("质量等级不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(efficiency)){
			setError(MessageUtil.runtimeErroMsg("效率等级密码不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(service)){
			setError(MessageUtil.runtimeErroMsg("服务等级密码不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(comment)){
			setError(MessageUtil.runtimeErroMsg("评论不能为空").toString());
			return;
		}

		Record order_info =  Db.findById("order_info", "order_id", Integer.valueOf(order_id), "*");

		if(order_info==null){
			setError(MessageUtil.runtimeErroMsg("订单信息不存在").toString());
			return;
		}else{
			if(!order_info.getStr("status").equals(Constants.ORDER_STATUS.END)){
				setError(MessageUtil.runtimeErroMsg("此订单当前状态不允许评论").toString());
				return;
			}
		}
		
		Record comment_info = Db.findFirst("select o.* from comment_info o where o.merchant_id = ? and o.order_id = ? and o.user_id = ?", merchant_id,Long.valueOf(order_id),Long.valueOf(user_id));
		if(comment_info!=null){
			setError(MessageUtil.runtimeErroMsg("此订单已经评论，不能重复评论").toString());
			return;
		}

	}


}
