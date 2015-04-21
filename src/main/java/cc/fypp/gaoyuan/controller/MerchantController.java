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
				.set("is_recommend", Constants.IS_RECOMMOND.NO)
				.set("level", 3)
				.set("balance", new BigDecimal(0.00));

		String technology_ids = getPara("technology_ids");
		if(technology_ids!=null){
			String[] _technology_ids = technology_ids.split(",");
			for(String technology_id:_technology_ids){
				Record merchant_technology =  new Record().set("merchant_id",  getPara("merchant_id")).set("technology_id",  technology_id);
				Db.save("merchant_technology","merchant_id", merchant_technology);
			}
		}
		String brand_ids = getPara("brand_ids");
		if(brand_ids!=null){
			String[] _brand_ids = brand_ids.split(",");
			for(String brand_id:_brand_ids){
				Record merchant_brandy =  new Record().set("merchant_id",  getPara("merchant_id")).set("brand_id",  brand_id);
				Db.save("merchant_brand","merchant_id", merchant_brandy);
			}
		}

		String service_ids = getPara("service_ids");
		if(service_ids!=null){
			String[] _service_ids = service_ids.split(",");
			for(String service_id:_service_ids){
				Record merchant_service =  new Record().set("merchant_id",  getPara("merchant_id")).set("service_id",  service_id);
				Db.save("merchant_service","merchant_id", merchant_service);
			}
		}

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
	 * 获取服务信息
	 */
	public void getServiceInfo(){
		List<Record> service_infos =  Db.find("select o.* from service_info o");
		renderJson(MessageUtil.successMsg("", service_infos));
	}

	/**
	 * 获取品牌信息
	 */
	public void getBrandInfo(){
		List<Record> brand_infos =  Db.find("select o.* from brand_info o");
		renderJson(MessageUtil.successMsg("", brand_infos));
	}

	/**
	 * 获取工艺信息
	 */
	public void getTechnologyInfo(){
		List<Record> technology_infos =  Db.find("select o.* from technology_info o");
		renderJson(MessageUtil.successMsg("", technology_infos));
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


		String technology_ids = getPara("technology_ids");
		if(technology_ids!=null){
			Db.update("delete from merchant_technology where merchant_id = ?", record.getStr("merchant_id"));
			String[] _technology_ids = technology_ids.split(",");
			for(String technology_id:_technology_ids){
				Record merchant_technology =  new Record().set("merchant_id",  record.getStr("merchant_id")).set("technology_id",  technology_id);
				Db.save("merchant_technology","merchant_id", merchant_technology);
			}
		}
		String brand_ids = getPara("brand_ids");
		if(brand_ids!=null){
			Db.update("delete from merchant_brand where merchant_id = ?", record.getStr("merchant_id"));
			String[] _brand_ids = brand_ids.split(",");
			for(String brand_id:_brand_ids){
				Record merchant_brandy =  new Record().set("merchant_id",  record.getStr("merchant_id")).set("brand_id",  brand_id);
				Db.save("merchant_brand","merchant_id", merchant_brandy);
			}
		}

		String service_ids = getPara("service_ids");
		if(service_ids!=null){
			Db.update("delete from merchant_service where merchant_id = ?", record.getStr("merchant_id"));
			String[] _service_ids = service_ids.split(",");
			for(String service_id:_service_ids){
				Record merchant_service =  new Record().set("merchant_id",  record.getStr("merchant_id")).set("service_id",  service_id);
				Db.save("merchant_service","merchant_id", merchant_service);
			}
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
			record.set("merchant_services", Db.find("select o.* from service_info o where o.id in (select distinct a.service_id from merchant_service a where a.merchant_id = ?)",merchant_id));
			record.set("merchant_brands", Db.find("select o.* from brand_info o where o.id in (select distinct a.brand_id from merchant_brand a where a.merchant_id = ?)",merchant_id));
			record.set("merchant_technologys", Db.find("select o.* from technology_info o where o.id in (select distinct a.technology_id from merchant_technology a where a.merchant_id = ?)",merchant_id));
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
		String[] image_names = getPara("image_name").split(",");
		StringBuilder sql = new StringBuilder();
		for(String image_name:image_names){
			if(sql.length()>0){
				sql.append(",");
			}
			sql.append("'"+image_name+"'");
		}
		List<Record> records  = Db.find(new StringBuilder("select o.* from merchant_externalinfo o where o.image_name in ( ").append(sql).append(" ) ").toString());
		if(records!=null){
			for(Record record:records){
				FileUtil.delete(ConfigFileUtil.getFilePath(), record.getStr("image_name"));
				Db.delete("merchant_externalinfo", "external_id", record);
			}
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
	 * 查询商户列表 v2
	 */
	public void findMerchantListV2(){

		String technology_id = getPara("technology_id");
		String brand_id = getPara("brand_id");
		String service_id = getPara("service_id");
		String area_name = getPara("area_name");
		String quality_order = getPara("quality_order");
		String efficiency_order = getPara("efficiency_order");
		String service_order = getPara("service_order");
		String  sales_volume_order = getPara("sales_volume_order");

		StringBuilder sql = new StringBuilder(" from merchant_info o  where o.status = ? ");
		List<Object> listPara = new ArrayList<Object>();
		listPara.add(Constants.MERCHANT_STATUS.CHECK);
		boolean hasQueryConditions = false;
		if(StringUtils.isNotBlank(technology_id)){
			hasQueryConditions = true;
			sql.append(" and o.merchant_id in (select distinct a.merchant_id from merchant_technology a where a.technology_id = ? ) ");
			listPara.add(technology_id);
			
		}

		if(StringUtils.isNotBlank(brand_id)){
			hasQueryConditions = true;
			sql.append(" and o.merchant_id in (select distinct b.merchant_id from merchant_brand b where b.brand_id = ? ) ");
			listPara.add(brand_id);
		}

		if(StringUtils.isNotBlank(service_id)){
			hasQueryConditions = true;
			sql.append(" and o.merchant_id in (select distinct c.merchant_id from merchant_service c where c.service_id = ? ) ");
			listPara.add(service_id);
		}

		if(StringUtils.isNotBlank(area_name)){
			hasQueryConditions = true;
			sql.append(" and o.city = ? ");
			listPara.add(area_name);
		}
		
		if(!hasQueryConditions){
			sql.append(" and o.is_recommend = ?");
			listPara.add(Constants.IS_RECOMMOND.YES);
		}

		StringBuilder order_sql =  new StringBuilder();

		if(StringUtils.isNotBlank(quality_order)){
			if(order_sql.length()==0){
				order_sql.append(" order by ");
			}else{
				order_sql.append(" , ");
			}
			if(quality_order.equals("1")){
				order_sql.append(" quality_score desc ");
			}else{
				order_sql.append(" quality_score  ");
			}
		}
		
		if(StringUtils.isNotBlank(efficiency_order)){
			if(order_sql.length()==0){
				order_sql.append(" order by ");
			}else{
				order_sql.append(" , ");
			}
			if(efficiency_order.equals("1")){
				order_sql.append(" efficiency_score desc ");
			}else{
				order_sql.append(" efficiency_score  ");
			}
		}
		
		if(StringUtils.isNotBlank(service_order)){
			if(order_sql.length()==0){
				order_sql.append(" order by ");
			}else{
				order_sql.append(" , ");
			}
			if(service_order.equals("1")){
				order_sql.append(" service_score desc ");
			}else{
				order_sql.append(" service_score  ");
			}
		}
		
		if(StringUtils.isNotBlank(sales_volume_order)){
			if(order_sql.length()==0){
				order_sql.append(" order by ");
			}else{
				order_sql.append(" , ");
			}
			if(sales_volume_order.equals("1")){
				order_sql.append(" sales_volume desc ");
			}else{
				order_sql.append(" sales_volume  ");
			}
		}

		List<Record> old_merchantList = Db.find(new StringBuilder("select o.* ").append(sql).append(order_sql).toString(),listPara.toArray(new Object[listPara.size()]));
		
		
		JSONObject json = new JSONObject();
		json.put("area_info", Db.find(new StringBuilder("select distinct o.city ").append(sql).toString(),listPara.toArray(new Object[listPara.size()])));
		json.put("technology_info", Db.find(new StringBuilder("select x.* from technology_info x where x.id in (select distinct y.technology_id from merchant_technology y"
				+ " where y.merchant_id in ( select distinct o.merchant_id ").append(sql).append(order_sql).append("))").toString(),listPara.toArray(new Object[listPara.size()])));
		json.put("brand_info", Db.find(new StringBuilder("select x.* from brand_info x where x.id in (select distinct y.brand_id from merchant_brand y"
				+ " where y.merchant_id in ( select distinct o.merchant_id ").append(sql).append(order_sql).append("))").toString(),listPara.toArray(new Object[listPara.size()])));
		json.put("service_info", Db.find(new StringBuilder("select x.* from service_info x where x.id in (select distinct y.service_id from merchant_service y"
				+ " where y.merchant_id in ( select distinct o.merchant_id ").append(sql).append(order_sql).append("))").toString(),listPara.toArray(new Object[listPara.size()])));
		
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
		
		json.put("list_data", new_merchantList);
		renderJson(MessageUtil.successMsg("", json));


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

	/**
	 * 上传商户标志图
	 */
	@Before({Tx.class})
	public void uploadIconImage(){
		String merchant_id = getPara("merchant_id");
		String imageStr = getPara("image_str");
		String image_name = UUID.randomUUID().toString().replace("-", "");
		Record merchant_info =  Db.findById("merchant_info", "merchant_id", merchant_id,"*");
		if(StringUtils.isNotBlank(merchant_info.getStr("icon_image"))){
			FileUtil.delete(ConfigFileUtil.getFilePath(), merchant_info.getStr("icon_image"));
		}
		merchant_info.set("icon", image_name);
		FileUtil.byte2File(Base64.decodeBase64(imageStr), ConfigFileUtil.getFilePath(), image_name);
		Db.update("merchant_info", "merchant_id",  merchant_info);
		JSONObject json = new JSONObject();
		json.put("icon_image", image_name);
		renderJson(MessageUtil.successMsg("", json));
	}

	/**
	 * 上传商户广告图
	 */
	@Before({Tx.class})
	public void uploadAdImage(){
		String merchant_id = getPara("merchant_id");
		String imageStr = getPara("image_str");
		String image_name = UUID.randomUUID().toString().replace("-", "");
		Record merchant_info =  Db.findById("merchant_info", "merchant_id", merchant_id,"*");
		if(StringUtils.isNotBlank(merchant_info.getStr("ad_image"))){
			FileUtil.delete(ConfigFileUtil.getFilePath(), merchant_info.getStr("ad_image"));
		}
		merchant_info.set("ad_image", image_name);
		FileUtil.byte2File(Base64.decodeBase64(imageStr), ConfigFileUtil.getFilePath(), image_name);
		Db.update("merchant_info", "merchant_id",  merchant_info);
		JSONObject json = new JSONObject();
		json.put("ad_image", image_name);
		renderJson(MessageUtil.successMsg("", json));
	}



	/**
	 * 下载商户标志图片
	 */
	public void downloadIconImage(){
		String image_name = getPara();
		File file = new File(ConfigFileUtil.getFilePath()+File.separator+image_name);
		renderFile(file);
	}

	/**
	 * 下载商户广告图片
	 */
	public void downloadAdImage(){
		String image_name = getPara();
		File file = new File(ConfigFileUtil.getFilePath()+File.separator+image_name);
		renderFile(file);
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
