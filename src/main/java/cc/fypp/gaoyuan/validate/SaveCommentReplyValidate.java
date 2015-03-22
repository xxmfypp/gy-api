package cc.fypp.gaoyuan.validate;

import org.apache.commons.lang.StringUtils;

import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class SaveCommentReplyValidate extends Validator{

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
		
		String comment_id = arg0.getPara("comment_id");
		String type = arg0.getPara("type");
		String login_name = arg0.getPara("login_name");
		String content = arg0.getPara("content");
		if(StringUtils.isBlank(comment_id)){
			setError(MessageUtil.runtimeErroMsg("评论编号不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(type)){
			setError(MessageUtil.runtimeErroMsg("用户类型不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(login_name)){
			setError(MessageUtil.runtimeErroMsg("用户登陆名不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(content)){
			setError(MessageUtil.runtimeErroMsg("回复内容不能为空").toString());
			return;
		}

	}


}
