package cc.fypp.gaoyuan.validate;

import org.apache.commons.lang.StringUtils;

import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class MerchantUploadImageValidate extends Validator{

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
		String type = arg0.getPara("type");
		String imageStr = arg0.getPara("image_str");
		if(StringUtils.isBlank(merchant_id)){
			setError(MessageUtil.runtimeErroMsg("商户编号不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(type)){
			setError(MessageUtil.runtimeErroMsg("图片类型不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(imageStr)){
			setError(MessageUtil.runtimeErroMsg("图片内容不能为空").toString());
			return;
		}

	}


}
