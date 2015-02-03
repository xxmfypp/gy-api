package cc.fypp.gaoyuan.validate;

import org.apache.commons.lang.StringUtils;

import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;

import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;
import com.jfinal.validate.Validator;

public class SaveTenderVectorImageValidate extends Validator{

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
		UploadFile uf = arg0.getFile("vector_image");
		String user_id = arg0.getPara("user_id");
		String tender_id = arg0.getPara("tender_id");
		if(StringUtils.isBlank(user_id)){
			setError(MessageUtil.runtimeErroMsg("用户编号不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(tender_id)){
			setError(MessageUtil.runtimeErroMsg("招标编号不能为空").toString());
			return;
		}
		if(uf.getFile()==null){
			setError(MessageUtil.runtimeErroMsg("矢量图片不能为空").toString());
			return;
		}

	}


}
