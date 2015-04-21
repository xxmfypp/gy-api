package cc.fypp.gaoyuan.controller;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cc.fypp.gaoyuan.common.date.DateUtil;
import cc.fypp.gaoyuan.common.file.FileUtil;
import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;
import cc.fypp.gaoyuan.common.sms.SmsUtil;
import cc.fypp.gaoyuan.config.ConfigFileUtil;
import cc.fypp.gaoyuan.validate.FindPwdValidate;
import cc.fypp.gaoyuan.validate.GetCodeValidate;
import cc.fypp.gaoyuan.validate.LoginValidate;
import cc.fypp.gaoyuan.validate.SaveCommentReplyValidate;
import cc.fypp.gaoyuan.validate.SaveUserRechargeValidate;
import cc.fypp.gaoyuan.validate.SaveUserWithdrawValidate;
import cc.fypp.gaoyuan.validate.UpdateUserPwdValidate;
import cc.fypp.gaoyuan.validate.UpdateUserValidate;
import cc.fypp.gaoyuan.validate.UserRegisterValidate;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

public class UserController extends Controller{

	protected final static Logger logger = Logger.getLogger(UserController.class);


	@Before(LoginValidate.class)
	/**
	 * 用户登陆
	 */
	public void login(){
		
		String login_name = getPara("login_name");
		String pwd = getPara("pwd");
		JSONObject json ;

		Record record = (Record) Db.findFirst("select o.* from user_info o where o.login_name = ? and o.pwd = ?",new Object[]{login_name,pwd});
		if(record!=null){
			json =  new JSONObject();
			json.put("type", "user");
			record.set("pwd", "");
			json.put("detail", record);
			renderJson(MessageUtil.successMsg("登陆成功", json));
			return;
		}
		record = (Record) Db.findFirst("select o.* from merchant_info o where o.login_name = ? and o.pwd = ?",new Object[]{login_name,pwd});
		if(record!=null){
			if(record.getStr("status").equals(Constants.MERCHANT_STATUS.UNCECK)){
				renderJson(MessageUtil.runtimeErroMsg("当前账户未审核，无法登陆"));
				return;
			}
			if(record.getStr("status").equals(Constants.MERCHANT_STATUS.REFUSE)){
				renderJson(MessageUtil.runtimeErroMsg("当前账户审核通过，无法登陆"));
				return;
			}
			json =  new JSONObject();
			json.put("type", "merchant");
			json.put("detail", record);
			renderJson(MessageUtil.successMsg("登陆成功", json));
			return;
		}

		renderJson(MessageUtil.runtimeErroMsg("用户名密码错误"));
	}
	
	
	@Before({GetCodeValidate.class,Tx.class})
	/**
	 * 获取注册码
	 */
	public void getRegisterCode(){
		String login_name = getPara("login_name");
		Long count = Db.queryBigDecimal("select sum(count) from (select count(*) as count from user_info a where a.login_name=? union select count(*) as count from merchant_info b where b.login_name = ?) count", login_name,login_name).longValue();
		if(count>0){
			renderJson(MessageUtil.runtimeErroMsg("此手机号已经被注册"));
			return;
		}
		
		
		String sms_template = SmsUtil.getSmsTemplate(Constants.SMS_TYPE.REGISTER.toString());
		if(StringUtils.isBlank(sms_template)){
			renderJson(MessageUtil.runtimeErroMsg("短信模板不存在").toString());
			return;
		}
		
		String verify_code = getCode();
		Map<String,String> param =  new HashMap<String,String>();
		param.put("verifyCode", verify_code);
		if(SmsUtil.sendMessage(login_name, MessageUtil.getFormatStringByVeloCity(sms_template, param))){
			Record record = Db.findFirst("select o.* from smscode_info o where o.phone_num=? and o.type=?",new Object[]{login_name,Constants.SMS.REGISTER_CODE});
			if(record!=null){
				record.set("create_time", System.currentTimeMillis())
				.set("code_content", verify_code)
				.set("status", Constants.SMS_STATUS.UNCECK);
				Db.update("smscode_info", record);
			}else{
				record = new Record().set("phone_num", login_name)
						.set("create_time", System.currentTimeMillis())
						.set("code_content", verify_code)
						.set("type", Constants.SMS.REGISTER_CODE)
						.set("status", Constants.SMS_STATUS.UNCECK);
				Db.save("smscode_info", record);
			}
			renderJson(MessageUtil.successMsg("获取验证码成功", ""));
		}else{
			renderJson(MessageUtil.runtimeErroMsg("短信发送异常"));
		}
	}

	@Before({UserRegisterValidate.class,Tx.class})
	/**
	 * 用户注册
	 */
	public void userRegister(){
		Record user_info =  new Record().set("login_name",  getPara("login_name"))
				.set("pwd", getPara("pwd"))
				.set("create_time", System.currentTimeMillis())
				.set("phone_num",getPara("login_name"))
				.set("balance", new BigDecimal(0.00))
				.set("city", getPara("city"))
				.set("level", 3);
		Db.save("user_info", user_info);
		renderJson(MessageUtil.successMsg("注册成功", ""));
	}
	
	
	@Before({Tx.class})
	public void yseUser() throws Exception{
		Record user_info =  new Record().set("login_name",  "111111")
				.set("pwd", "111111")
				.set("create_time", System.currentTimeMillis())
				.set("phone_num","111111")
				.set("balance", new BigDecimal(0.00))
				.set("city","111111")
				.set("level", 3);
		Db.save("user_info", user_info);
		user_info.getLong("user_id");
		renderJson(MessageUtil.successMsg("注册成功", user_info));
		//throw new Exception("sdfsdfsd");
		/*System.out.println(user_info.getLong("user_id"));
		user_info.set("level", 100);
		Db.update("user_info","user_id", user_info);
		renderJson(MessageUtil.successMsg("注册成功", user_info));*/
	}

	@Before(UpdateUserValidate.class)
	/**
	 * 修改用户
	 */
	public void updateUser(){
		String login_name = getPara("login_name");
		Record record = (Record) Db.findFirst("select o.* from user_info o where o.login_name = ? ",login_name);
		if(StringUtils.isNotBlank(getPara("name"))){
			record.set("name", getPara("name"));
		}
		
		if(StringUtils.isNotBlank(getPara("city"))){
			record.set("city", getPara("city"));
		}
		
		if(StringUtils.isNotBlank(getPara("address"))){
			record.set("address", getPara("address"));
		}
		Db.update("user_info", "user_id", record);
		renderJson(MessageUtil.successMsg("用户信息修改成功", ""));
	}

	@Before(UpdateUserPwdValidate.class)
	/**
	 * 修改用户密码
	 */
	public void updateUserPwd(){
		String login_name = getPara("login_name");
		String new_pwd = getPara("new_pwd");
		Record record = (Record) Db.findFirst("select o.* from user_info o where o.login_name = ? ",login_name);
		if(record==null){
			renderJson(MessageUtil.runtimeErroMsg("用户不存在"));
			return;
		}
		record.set("pwd", new_pwd);
		Db.update("user_info", "user_id", record);
		renderJson(MessageUtil.successMsg("用户密码修改成功", ""));
	}


	@Before({GetCodeValidate.class,Tx.class})
	/**
	 * 获取找回密码验证码
	 */
	public void getFindPwdCode(){
		String login_name = getPara("login_name");
		
		
		
		String sms_template = SmsUtil.getSmsTemplate(Constants.SMS_TYPE.FINDPWS.toString());
		if(StringUtils.isBlank(sms_template)){
			renderJson(MessageUtil.runtimeErroMsg("短信模板不存在").toString());
			return;
		}
		
		String verify_code = getCode();
		Map<String,String> param =  new HashMap<String,String>();
		param.put("verifyCode", verify_code);
		
		if(SmsUtil.sendMessage(login_name, MessageUtil.getFormatStringByVeloCity(sms_template, param))){
			Record record = Db.findFirst("select o.* from smscode_info o where o.phone_num=? and o.type=?",new Object[]{login_name,Constants.SMS.PWD_CODE});
			if(record!=null){
				record.set("create_time", System.currentTimeMillis())
				.set("code_content", verify_code)
				.set("status", Constants.SMS_STATUS.UNCECK);
				Db.update("smscode_info", record);
			}else{
				record = new Record().set("phone_num", login_name)
						.set("create_time", System.currentTimeMillis())
						.set("code_content", verify_code)
						.set("type", Constants.SMS.PWD_CODE)
						.set("status", Constants.SMS_STATUS.UNCECK);
				Db.save("smscode_info", record);
			}
			renderJson(MessageUtil.successMsg("获取验证码成功", ""));
		}else{
			renderJson(MessageUtil.runtimeErroMsg("短信发送异常"));
		}
	}

	@Before({FindPwdValidate.class,Tx.class})
	/**
	 * 用户找回密码
	 */
	public void findUserPwd(){
		String login_name = getPara("login_name");
		String new_pwd = getPara("new_pwd");
		Record record = (Record) Db.findFirst("select o.* from user_info o where o.login_name = ? ",login_name);
		if(record==null){
			renderJson(MessageUtil.runtimeErroMsg("用户不存在"));
			return;
		}
		record.set("pwd", new_pwd);
		Db.update("user_info", "user_id", record);
		renderJson(MessageUtil.successMsg("密码找回成功", ""));
	}
	
	/**
	 * 查询商户信息
	 */
	public void findUserInfo(){
		Long user_id = getParaToLong("user_id");
		Record record = (Record) Db.findById("user_info", "user_id", user_id,"*");
		if(record==null){
			renderJson(MessageUtil.runtimeErroMsg("用户不存在"));
			return;
		}else{
			record.set("pwd", "");
			renderJson(MessageUtil.successMsg("", record));
		}
	}
	


	private String getCode(){
		String s = "";
		while(s.length()<6){
			s+=(int)(Math.random()*10);
		}
		return s;
	}

	/**
	 * 用户创建充值记录
	 */
	@Before(SaveUserRechargeValidate.class)
	public void saveUserRecharge(){
		Record user_recharge_info =  new Record();
		String id = getUserRechargeId();
		user_recharge_info.set("id", id)
		.set("amount", new BigDecimal(getPara("amount")))
		.set("mark", getPara("mark"))
		.set("recharge_time",System.currentTimeMillis())
		.set("status", Constants.RECHARGE_STATUS.RECHARGE)
		.set("user_id", getParaToLong("user_id"));
		Db.save("user_recharge_info", user_recharge_info);
		
		JSONObject json = new JSONObject();
		json.put("amount", getPara("amount"));
		json.put("id", id);
		renderJson(MessageUtil.successMsg("创建充值记录成功", json));

	}
	
	/**
	 * 用户创建提款记录
	 */
	@Before(SaveUserWithdrawValidate.class)
	public void saveUserWithdraw(){
		Long user_id = getParaToLong("user_id");
		BigDecimal amount =  new BigDecimal(getPara("amount"));
		Record user_withdraw_info =  new Record();
		String id = getUserWithdrawId();
		user_withdraw_info.set("id", id)
		.set("amount", amount)
		.set("mark", getPara("mark"))
		.set("zhifubao_code", getPara("zhifubao_code"))
		.set("recharge_time",System.currentTimeMillis())
		.set("status", Constants.WITHDRAW_STATUS.WITHDRAW)
		.set("user_id", user_id);
		
		Db.save("user_withdraw_info", user_withdraw_info);
		
		
		Record user_info =  Db.findById("user_info", "user_id", user_id,"*");
		user_info.set("balance", user_info.getBigDecimal("balance").subtract(amount));
		Db.update("user_info", "user_id", user_info);
		
		JSONObject json = new JSONObject();
		json.put("amount", getPara("amount"));
		json.put("id", id);
		renderJson(MessageUtil.successMsg("提款申请成功", json));

	}
	
	/**
	 * 用户分页查询充值信息
	 */
	public void findUserRechargeByPage(){
		Long user_id  = getParaToLong("user_id");
		String status = getPara("status");
		Boolean is_before = getParaToBoolean("is_before");
		Integer page_size = 10;
		if(StringUtils.isNotBlank(getPara("page_size"))){
			page_size = getParaToInt("page_size");
		}
		String user_recharge_id  = getPara("user_recharge_id");
		
		List<Object> listPara = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder(" select o.* from user_recharge_info o where o.user_id = ?");
		listPara.add(user_id);
		if(StringUtils.isNotBlank(status)){
			sql.append(" and o.status = ? ");
			listPara.add(status);
		}
		
		if(StringUtils.isNotBlank(user_recharge_id)){
			if(is_before){
				sql.append(" and o.create_time < (select t.create_time from user_recharge_info t where t.id=?) ");
			}else{
				sql.append(" and o.create_time > (select t.create_time from user_recharge_info t where t.id=?) ");
			}
			listPara.add(user_recharge_id);
		}
		
		sql.append(" order by o.create_time desc limit ?,?");
		listPara.add(0);
		listPara.add(page_size);
		List<Record> listResult = Db.find(sql.toString(),listPara.toArray(new Object[listPara.size()]));
		
		renderJson(MessageUtil.successMsg("", listResult));
	}
	
	/**
	 * 用户分页查询提款信息
	 */
	public void findUserWithdrawByPage(){
		Long user_id  = getParaToLong("user_id");
		String status = getPara("status");
		Boolean is_before = getParaToBoolean("is_before");
		Integer page_size = 10;
		if(StringUtils.isNotBlank(getPara("page_size"))){
			page_size = getParaToInt("page_size");
		}
		String user_withdraw_id  = getPara("user_withdraw_id");
		
		List<Object> listPara = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder(" select o.* from user_withdraw_info o where o.user_id = ?");
		listPara.add(user_id);
		if(StringUtils.isNotBlank(status)){
			sql.append(" and o.status = ? ");
			listPara.add(status);
		}
		
		if(StringUtils.isNotBlank(user_withdraw_id)){
			if(is_before){
				sql.append(" and o.create_time < (select t.create_time from user_withdraw_info t where t.id=?) ");
			}else{
				sql.append(" and o.create_time > (select t.create_time from user_withdraw_info t where t.id=?) ");
			}
			listPara.add(user_withdraw_id);
		}
		
		sql.append(" order by o.create_time desc limit ?,?");
		listPara.add(0);
		listPara.add(page_size);
		List<Record> listResult = Db.find(sql.toString(),listPara.toArray(new Object[listPara.size()]));
		
		renderJson(MessageUtil.successMsg("", listResult));
	}
	
	/**
	 * 上传用户头像
	 */
	public void uploadIconImage(){
		Long user_id  = getParaToLong("user_id");
		String imageStr = getPara("image_str");
		String image_name = UUID.randomUUID().toString().replace("-", "");
		Record user_info =  Db.findById("user_info", "user_id", user_id,"*");
		if(StringUtils.isNotBlank(user_info.getStr("user_icon"))){
			FileUtil.delete(ConfigFileUtil.getFilePath(), user_info.getStr("user_icon"));
		}
		user_info.set("user_icon", image_name);
		FileUtil.byte2File(Base64.decodeBase64(imageStr), ConfigFileUtil.getFilePath(), image_name);
		Db.update("user_info", "user_id",  user_info);
		JSONObject json = new JSONObject();
		json.put("user_icon", image_name);
		renderJson(MessageUtil.successMsg("", json));
	}
	
	/**
	 * 下载用户头像
	 */
	public void downloadIconImage(){
		String image_name = getPara();
		File file = new File(ConfigFileUtil.getFilePath()+File.separator+image_name);
		renderFile(file);
	}
	
	/**
	 * 分页获取我的供应商信息
	 */
	@Before({Tx.class})
	public void getMySupplier(){
		Long user_id = getParaToLong("user_id");
		long size = Db.queryLong("select count(*) from order_info o where o.user_id = ?",user_id);
		List<Record> listResult = new ArrayList<Record>();
		if(size==0){
			renderJson(MessageUtil.successMsg("", listResult));
			return;
		}else{
			String merchant_id  = getPara("merchant_id");
			Boolean is_before = getParaToBoolean("is_before");
			Integer page_size = 10;
			if(StringUtils.isNotBlank(getPara("page_size"))){
				page_size = getParaToInt("page_size");
			}
			List<Object> listPara = new ArrayList<Object>();
			
			StringBuilder sql = new StringBuilder(" select o.* from merchant_info o  where o.merchant_id in ( select distinct a.merchant_id from  order_info a where a.user_id = ?)");
			listPara.add(user_id);
			if(StringUtils.isNotBlank(merchant_id)){
				if(is_before){
					sql.append(" and o.create_time < (select t.create_time from merchant_info t where t.merchant_id=?) ");
				}else{
					sql.append(" and o.create_time > (select t.create_time from merchant_info t where t.merchant_id=?) ");
				}
				listPara.add(merchant_id);
			}

			sql.append(" order by o.create_time desc  limit ?,?");
			listPara.add(0);
			listPara.add(page_size);

			listResult = Db.find(sql.toString(),listPara.toArray(new Object[listPara.size()]));

			renderJson(MessageUtil.successMsg("", listResult));
		}
		
	}
	
	/**
	 * 分页获取我的客户信息
	 */
	@Before({Tx.class})
	public void getMyCustomer(){
		String merchant_id = getPara("merchant_id");
		long size = Db.queryLong("select count(*) from order_info o where o.merchant_id = ?",merchant_id);
		List<Record> listResult = new ArrayList<Record>();
		if(size==0){
			renderJson(MessageUtil.successMsg("", listResult));
			return;
		}else{
			String user_id  = getPara("user_id");
			Boolean is_before = getParaToBoolean("is_before");
			Integer page_size = 10;
			if(StringUtils.isNotBlank(getPara("page_size"))){
				page_size = getParaToInt("page_size");
			}
			List<Object> listPara = new ArrayList<Object>();
			
			StringBuilder sql = new StringBuilder(" select o.* from user_info o  where o.user_id in ( select distinct a.user_id from  order_info a where a.merchant_id = ?)");
			listPara.add(merchant_id);
			if(StringUtils.isNotBlank(user_id)){
				if(is_before){
					sql.append(" and o.create_time < (select t.create_time from user_info t where t.user_id=?) ");
				}else{
					sql.append(" and o.create_time > (select t.create_time from user_info t where t.user_id=?) ");
				}
				listPara.add(Long.valueOf(user_id));
			}

			sql.append(" order by o.create_time desc  limit ?,?");
			listPara.add(0);
			listPara.add(page_size);

			listResult = Db.find(sql.toString(),listPara.toArray(new Object[listPara.size()]));

			renderJson(MessageUtil.successMsg("", listResult));
		}
		
	}
	
	
	/**
	 * 获取账号资金变动历史信息
	 */
	@Before({Tx.class})
	public void getAccountTransactionHis(){
		String login_name =  getPara("login_name");
		String account_transaction_id  = getPara("account_transaction_id");
		Boolean is_before = getParaToBoolean("is_before");
		Integer page_size = 10;
		if(StringUtils.isNotBlank(getPara("page_size"))){
			page_size = getParaToInt("page_size");
		}
		List<Object> listPara = new ArrayList<Object>();
		
		StringBuilder sql = new StringBuilder("select o.* from account_transaction_his o where o.login_name = ?");
		listPara.add(login_name);
		if(StringUtils.isNotBlank(account_transaction_id)){
			if(is_before){
				sql.append(" and o.create_time < (select t.create_time from account_transaction_his t where t.id=?) ");
			}else{
				sql.append(" and o.create_time > (select t.create_time from account_transaction_his t where t.id=?) ");
			}
			listPara.add(Long.valueOf(account_transaction_id));
		}

		sql.append(" order by o.create_time desc  limit ?,?");
		listPara.add(0);
		listPara.add(page_size);

		List<Record> listResult = Db.find(sql.toString(),listPara.toArray(new Object[listPara.size()]));

		renderJson(MessageUtil.successMsg("", listResult));
	}
	
	/**
	 * 评论回复
	 */
	@Before(SaveCommentReplyValidate.class)
	public void saveCommentReply(){
		Long comment_id  = getParaToLong("comment_id");
		String type = getPara("type");
		String login_name = getPara("login_name");
		String content =  getPara("content");
		Record comment_reply = new Record()
				.set("comment_id", comment_id)
				.set("type", type)
				.set("content", content)
				.set("login_name", login_name)
				.set("create_time", System.currentTimeMillis());
		Db.save("comment_reply", comment_reply);
		renderJson(MessageUtil.successMsg("","回复成功"));
	}
	
	


	private static String getUserRechargeId(){
		String nowDate = "UR_"+DateUtil.getNowTimeByString(DateUtil.YYYYMMDD);
		Long num = Db.queryLong("select count(*) from user_recharge_info o where o.id like ?",nowDate+"%");
		int intLen = String.valueOf(num+1).length();
		StringBuilder sb = new StringBuilder(nowDate);
		for(int i = intLen;i<5;i++){
			sb.append("0");
		}
		sb.append(String.valueOf(num+1));
		return sb.toString();
	}
	
	private static String getUserWithdrawId(){
		String nowDate = "UW_"+DateUtil.getNowTimeByString(DateUtil.YYYYMMDD);
		Long num = Db.queryLong("select count(*) from user_withdraw_info o where o.id like ?",nowDate+"%");
		int intLen = String.valueOf(num+1).length();
		StringBuilder sb = new StringBuilder(nowDate);
		for(int i = intLen;i<5;i++){
			sb.append("0");
		}
		sb.append(String.valueOf(num+1));
		return sb.toString();
	}
	
	


}
