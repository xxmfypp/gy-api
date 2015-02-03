package cc.fypp.gaoyuan.validate;

import org.apache.commons.lang.StringUtils;

import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class GetProductListValidate extends Validator{
	
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
		if(StringUtils.isBlank(arg0.getPara("materia_id"))){
			setError(MessageUtil.runtimeErroMsg("材质编号不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(arg0.getPara("area"))){
			setError(MessageUtil.runtimeErroMsg("面积不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(arg0.getPara("perimeter"))){
			setError(MessageUtil.runtimeErroMsg("周长不能为空").toString());
			return;
		}
		
		if(StringUtils.isBlank(arg0.getPara("scene_id"))){
			setError(MessageUtil.runtimeErroMsg("场景编号不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(arg0.getPara("light_level"))){
			setError(MessageUtil.runtimeErroMsg("亮度等级不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(arg0.getPara("strength_level"))){
			setError(MessageUtil.runtimeErroMsg("强度等级不能为空").toString());
			return;
		}
		if(StringUtils.isBlank(arg0.getPara("process_level"))){
			setError(MessageUtil.runtimeErroMsg("工艺等级不能为空").toString());
			return;
		}
		
	}
	
	
}
