package cc.fypp.gaoyuan.controller;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cc.fypp.gaoyuan.common.date.DateUtil;
import cc.fypp.gaoyuan.common.file.FileUtil;
import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;
import cc.fypp.gaoyuan.config.ConfigFileUtil;
import cc.fypp.gaoyuan.validate.FindPwdValidate;
import cc.fypp.gaoyuan.validate.MerchantRegisterValidate;
import cc.fypp.gaoyuan.validate.MerchantUploadImageValidate;
import cc.fypp.gaoyuan.validate.SaveMerchantRechargeValidate;
import cc.fypp.gaoyuan.validate.SaveMerchantWithdrawValidate;
import cc.fypp.gaoyuan.validate.UpdateMerchantPwdValidate;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

public class MerchantController extends Controller{

	protected final static Logger logger = Logger.getLogger(MerchantController.class);



	@Before({MerchantRegisterValidate.class,Tx.class})
	/**
	 * 商户注册
	 */
	public void merchantRegister(){
		Record record =  new Record().set("merchant_id",  getPara("merchant_id"))
				.set("login_name", getPara("login_name"))
				.set("pwd",getPara("pwd"))
				.set("company",getPara("company"))
				.set("phone_num",getPara("phone_num"))
				.set("qq",getPara("qq"))
				.set("address",getPara("address"))
				.set("weixin_code",getPara("weixin_code"))
				.set("mail",getPara("mail"))
				.set("merchant_nickname",getPara("merchant_nickname"))
				.set("registration_num",getPara("registration_num"))
				.set("city",getPara("city"))
				.set("legal_name",getPara("legal_name"))
				.set("legal_cardno",getPara("legal_cardno"))
				.set("legal_sex",getPara("legal_sex"))
				.set("zhifubao_code",getPara("zhifubao_code"))
				.set("message",getPara("message"))
				.set("status", Constants.MERCHANT_STATUS.UNCECK)
				.set("create_time", System.currentTimeMillis())
				.set("level", 3)
				.set("balance", new BigDecimal(0.00));
		if(StringUtils.isNotBlank(getPara("lat"))){
			record.set("lat",new BigDecimal(getPara("lat")));
		}
		if(StringUtils.isNotBlank(getPara("lng"))){
			record.set("lng",new BigDecimal(getPara("lng")));
		}
		Db.save("merchant_info","merchant_id", record);
		renderJson(MessageUtil.successMsg("注册成功,请耐心等待审核", ""));
	}

	/**
	 * 修改商户
	 */
	public void updateMerchant(){
		String login_name = getPara("login_name");
		Record record = (Record) Db.findFirst("select o.* from merchant_info o where o.login_name = ? ",login_name);
		if(record==null){
			renderJson(MessageUtil.runtimeErroMsg("商户不存在"));
			return;
		}
		
		if(StringUtils.isNotBlank(getPara("lat"))){
			record.set("lat",new BigDecimal(getPara("lat")));
		}
		
		if(StringUtils.isNotBlank(getPara("lng"))){
			record.set("lng",new BigDecimal(getPara("lng")));
		}
		
		
		if(StringUtils.isNotBlank(getPara("phone_num"))){
			record.set("phone_num",getPara("phone_num"));
		}
		
		if(StringUtils.isNotBlank(getPara("qq"))){
			record.set("qq",getPara("qq"));
		}
		
		if(StringUtils.isNotBlank(getPara("weixin_code"))){
			record.set("weixin_code",getPara("weixin_code"));
		}
		
		if(StringUtils.isNotBlank(getPara("mail"))){
			record.set("mail",getPara("mail"));
		}
		
		if(StringUtils.isNotBlank(getPara("merchant_nickname"))){
			record.set("merchant_nickname",getPara("merchant_nickname"));
		}
		
		if(StringUtils.isNotBlank(getPara("message"))){
			record.set("message",getPara("message"));
		}
		
		Db.update("merchant_info", "merchant_id", record);
		renderJson(MessageUtil.successMsg("商户信息修改成功", record));
	}
	
	@Before(UpdateMerchantPwdValidate.class)
	/**
	 * 修改商户密码
	 */
	public void updateMerchantPwd(){
		String login_name = getPara("login_name");
		String new_pwd = getPara("new_pwd");
		Record record = (Record) Db.findFirst("select o.* from merchant_info o where o.login_name = ? ",login_name);
		if(record==null){
			renderJson(MessageUtil.runtimeErroMsg("商户不存在"));
			return;
		}
		record.set("pwd", new_pwd);
		Db.update("merchant_info", "merchant_id", record);
		renderJson(MessageUtil.successMsg("商户密码修改成功", ""));
	}
	
	
	@Before({FindPwdValidate.class,Tx.class})
	/**
	 * 商户找回密码
	 */
	public void findMerchantPwd(){
		String login_name = getPara("login_name");
		String new_pwd = getPara("new_pwd");
		Record record = (Record) Db.findFirst("select o.* from merchant_info o where o.login_name = ? ",login_name);
		if(record==null){
			renderJson(MessageUtil.runtimeErroMsg("商户不存在"));
			return;
		}
		record.set("pwd", new_pwd);
		Db.update("merchant_info", "merchant_id", record);
		renderJson(MessageUtil.successMsg("密码找回成功", ""));
	}
	
	/**
	 * 查询商户信息
	 */
	public void findMerchantInfo(){
		String merchant_id = getPara("merchant_id");
		Record record = (Record) Db.findById("merchant_info", "merchant_id", merchant_id,"*");
		if(record==null){
			renderJson(MessageUtil.runtimeErroMsg("商户不存在"));
			return;
		}else{
			record.set("pwd", "");
			record.set("images0", Db.find("select o.* from merchant_externalinfo o where o.type=? and o.merchant_id = ?", "0",merchant_id));
			record.set("images1", Db.find("select o.* from merchant_externalinfo o where o.type=? and o.merchant_id = ?", "1",merchant_id));
			record.set("images2", Db.find("select o.* from merchant_externalinfo o where o.type=? and o.merchant_id = ?", "2",merchant_id));
			record.set("images3", Db.find("select o.* from merchant_externalinfo o where o.type=? and o.merchant_id = ?", "3",merchant_id));
			record.set("images4", Db.find("select o.* from merchant_externalinfo o where o.type=? and o.merchant_id = ?", "4",merchant_id));
			record.set("images5", Db.find("select o.* from merchant_externalinfo o where o.type=? and o.merchant_id = ?", "5",merchant_id));
			renderJson(MessageUtil.successMsg("", record));
		}
	}
	
	/**
	 * 查询商户图片列表
	 */
	public void findMerchantImage(){
		String merchant_id = getPara("merchant_id");
		String type = getPara("type");
		Record record = (Record) Db.findById("merchant_info", "merchant_id", merchant_id,"*");
		if(record==null){
			renderJson(MessageUtil.runtimeErroMsg("商户不存在"));
			return;
		}
		List<Record> imageList = new ArrayList<Record>();
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql =  new StringBuilder("select o.* from merchant_externalinfo o where o.merchant_id =? ");
		params.add(merchant_id);
		if(StringUtils.isNotBlank(type)){
			sql.append(" o.type=? ");
			params.add(sql);
		}
		imageList = Db.find(sql.toString(), params.toArray(new Object[params.size()]));
		renderJson(MessageUtil.successMsg("", imageList));
	}
	
	/**
	 * 文件下载
	 */
	public void downloadImage(){
		String external_id = getPara();
		Record record = (Record) Db.findById("merchant_externalinfo", "external_id", Integer.valueOf(external_id),"*");
		if(record!=null){
			File file = new File(ConfigFileUtil.getFilePath()+File.separator+record.getStr("image_name"));
			renderFile(file);
		}
	}
	
	@Before({MerchantUploadImageValidate.class,Tx.class})
	/**
	 * 图片上传
	 */
	public void uploadImage(){
		String merchant_id = getPara("merchant_id");
		String type = getPara("type");
		String imageStr = getPara("image_str");
		String image_name = UUID.randomUUID().toString().replace("-", "");
		FileUtil.byte2File(Base64.decodeBase64(imageStr), ConfigFileUtil.getFilePath(), image_name);
		Record record = new Record().set("merchant_id", merchant_id)
				.set("type", type)
				.set("image_name", image_name)
				.set("create_time", System.currentTimeMillis());
		Db.save("merchant_externalinfo", "external_id", record);
		JSONObject json = new JSONObject();
		json.put("image_name", image_name);
		renderJson(MessageUtil.successMsg("", json));
	}
	
	@Before(Tx.class)
	/**
	 * 图片删除
	 */
	public void deleteImage(){
		String image_name = getPara("image_name");
		Record record  = Db.findFirst("select o.* from merchant_externalinfo o where o.image_name = ?", image_name);
		if(record!=null){
			Db.delete("merchant_externalinfo", "external_id", record);
			FileUtil.delete(ConfigFileUtil.getFilePath(), image_name);
			renderJson(MessageUtil.successMsg("图片删除成功", ""));
		}else{
			renderJson(MessageUtil.runtimeErroMsg("图片不存在"));
		}
	}
	
	/**
	 * 根据区域查询企业信息里列表
	 */
	public void findMerchantList(){
		String query_type = getPara("query_type");
		StringBuilder sql = new StringBuilder("select o.* from merchant_info o  where o.status = '"+Constants.MERCHANT_STATUS.CHECK+"'");
		if(Constants.QUERY_TYPE.LOCAL.equals(query_type)){
			sql.append(" and o.city = '"+getPara("area_name")+"' ");
		}else if(Constants.QUERY_TYPE.REMOTE.equals(query_type)){
			sql.append(" and o.city <> '"+getPara("area_name")+"' ");
		}
		sql.append(" order by o.level desc,o.create_time desc");
		
		List<Record> old_merchantList = Db.find(sql.toString());
		
		
		List<Record> new_merchantList = new ArrayList<Record>();
		
		for(Record merchant_info:old_merchantList){
			merchant_info.set("images0", Db.find("select o.* from merchant_externalinfo o where o.type=? and o.merchant_id = ?", "0",merchant_info.getStr("merchant_id")));
			merchant_info.set("images1", Db.find("select o.* from merchant_externalinfo o where o.type=? and o.merchant_id = ?", "1",merchant_info.getStr("merchant_id")));
			merchant_info.set("images2", Db.find("select o.* from merchant_externalinfo o where o.type=? and o.merchant_id = ?", "2",merchant_info.getStr("merchant_id")));
			merchant_info.set("images3", Db.find("select o.* from merchant_externalinfo o where o.type=? and o.merchant_id = ?", "3",merchant_info.getStr("merchant_id")));
			merchant_info.set("images4", Db.find("select o.* from merchant_externalinfo o where o.type=? and o.merchant_id = ?", "4",merchant_info.getStr("merchant_id")));
			merchant_info.set("images5", Db.find("select o.* from merchant_externalinfo o where o.type=? and o.merchant_id = ?", "5",merchant_info.getStr("merchant_id")));
			new_merchantList.add(merchant_info);
		}
		renderJson(MessageUtil.successMsg("", new_merchantList));
	}
	
	
	/**
	 * 商户创建充值记录
	 */
	@Before(SaveMerchantRechargeValidate.class)
	public void saveMerchantRecharge(){
		Record merchant_recharge_info =  new Record();
		String id = getMerchantRechargeId();
		merchant_recharge_info.set("id", id)
		.set("amount", new BigDecimal(getPara("amount")))
		.set("mark", getPara("mark"))
		.set("recharge_time",System.currentTimeMillis())
		.set("status", Constants.RECHARGE_STATUS.RECHARGE)
		.set("merchant_id", getPara("merchant_id"));
		Db.save("merchant_recharge_info", merchant_recharge_info);
		
		JSONObject json = new JSONObject();
		json.put("amount", getPara("amount"));
		json.put("id", id);
		renderJson(MessageUtil.successMsg("创建充值记录成功", json));

	}
	
	/**
	 * 创建提款记录
	 */
	@Before(SaveMerchantWithdrawValidate.class)
	public void saveMerchantWithdraw(){
		String merchant_id = getPara("merchant_id");
		BigDecimal amount =  new BigDecimal(getPara("amount"));
		Record merchant_withdraw_info =  new Record();
		String id = getMerchantWithdrawId();
		merchant_withdraw_info.set("id", id)
		.set("amount", amount)
		.set("mark", getPara("mark"))
		.set("recharge_time",System.currentTimeMillis())
		.set("status", Constants.WITHDRAW_STATUS.WITHDRAW)
		.set("merchant_id", merchant_id);
		
		Db.save("merchant_withdraw_info", merchant_withdraw_info);
		
		
		Record merchant_info =  Db.findById("merchant_info", "merchant_id", merchant_id,"*");
		merchant_info.set("balance", merchant_info.getBigDecimal("balance").subtract(amount));
		Db.update("merchant_info", "merchant_id", merchant_info);
		
		JSONObject json = new JSONObject();
		json.put("amount", getPara("amount"));
		json.put("id", id);
		renderJson(MessageUtil.successMsg("提款申请成功", json));

	}
	
	/**
	 * 商户分页查询充值信息
	 */
	public void findMerchantRechargeByPage(){
		String merchant_id  = getPara("merchant_id");
		String status = getPara("status");
		Boolean is_before = getParaToBoolean("is_before");
		Integer page_size = 10;
		if(StringUtils.isNotBlank(getPara("page_size"))){
			page_size = getParaToInt("page_size");
		}
		String merchant_recharge_id  = getPara("merchant_recharge_id");
		
		List<Object> listPara = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder(" select o.* from merchant_recharge_info o where o.merchant_id = ?");
		listPara.add(merchant_id);
		if(StringUtils.isNotBlank(status)){
			sql.append(" and o.status = ? ");
			listPara.add(status);
		}
		
		if(StringUtils.isNotBlank(merchant_recharge_id)){
			if(is_before){
				sql.append(" and o.create_time < (select t.create_time from merchant_recharge_info t where t.id=?) ");
			}else{
				sql.append(" and o.create_time > (select t.create_time from merchant_recharge_info t where t.id=?) ");
			}
			listPara.add(merchant_recharge_id);
		}
		
		sql.append(" order by o.create_time desc limit ?,?");
		listPara.add(0);
		listPara.add(page_size);
		List<Record> listResult = Db.find(sql.toString(),listPara.toArray(new Object[listPara.size()]));
		
		renderJson(MessageUtil.successMsg("", listResult));
	}
	
	/**
	 * 商户分页查询提款信息
	 */
	public void findMerchantWithdrawByPage(){
		String merchant_id  = getPara("merchant_id");
		String status = getPara("status");
		Boolean is_before = getParaToBoolean("is_before");
		Integer page_size = 10;
		if(StringUtils.isNotBlank(getPara("page_size"))){
			page_size = getParaToInt("page_size");
		}
		String merchant_withdraw_id  = getPara("merchant_withdraw_id");
		
		List<Object> listPara = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder(" select o.* from merchant_withdraw_info o where o.merchant_id = ?");
		listPara.add(merchant_id);
		if(StringUtils.isNotBlank(status)){
			sql.append(" and o.status = ? ");
			listPara.add(status);
		}
		
		if(StringUtils.isNotBlank(merchant_withdraw_id)){
			if(is_before){
				sql.append(" and o.create_time < (select t.create_time from merchant_withdraw_info t where t.id=?) ");
			}else{
				sql.append(" and o.create_time > (select t.create_time from merchant_withdraw_info t where t.id=?) ");
			}
			listPara.add(merchant_withdraw_id);
		}
		
		sql.append(" order by o.create_time desc limit ?,?");
		listPara.add(0);
		listPara.add(page_size);
		List<Record> listResult = Db.find(sql.toString(),listPara.toArray(new Object[listPara.size()]));
		
		renderJson(MessageUtil.successMsg("", listResult));
	}
	


	private static String getMerchantRechargeId(){
		String nowDate = "MR_"+DateUtil.getNowTimeByString(DateUtil.YYYYMMDD);
		Long num = Db.queryLong("select count(*) from merchant_recharge_info o where o.id like ?",nowDate+"%");
		int intLen = String.valueOf(num+1).length();
		StringBuilder sb = new StringBuilder(nowDate);
		for(int i = intLen;i<5;i++){
			sb.append("0");
		}
		sb.append(String.valueOf(num+1));
		return sb.toString();
	}
	
	private static String getMerchantWithdrawId(){
		String nowDate = "MW_"+DateUtil.getNowTimeByString(DateUtil.YYYYMMDD);
		Long num = Db.queryLong("select count(*) from merchant_withdraw_info o where o.id like ?",nowDate+"%");
		int intLen = String.valueOf(num+1).length();
		StringBuilder sb = new StringBuilder(nowDate);
		for(int i = intLen;i<5;i++){
			sb.append("0");
		}
		sb.append(String.valueOf(num+1));
		return sb.toString();
	}


}
