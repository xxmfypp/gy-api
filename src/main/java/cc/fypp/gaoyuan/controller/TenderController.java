package cc.fypp.gaoyuan.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cc.fypp.gaoyuan.common.file.FileUtil;
import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;
import cc.fypp.gaoyuan.common.sms.SmsUtil;
import cc.fypp.gaoyuan.config.ConfigFileUtil;
import cc.fypp.gaoyuan.validate.DeleteTenderEnterpriseValidate;
import cc.fypp.gaoyuan.validate.FindTenderForEenterpriseValidate;
import cc.fypp.gaoyuan.validate.FindTenderForUserValidate;
import cc.fypp.gaoyuan.validate.MerchantReceiptValidate;
import cc.fypp.gaoyuan.validate.SaveBuyCarEnterpriseValidate;
import cc.fypp.gaoyuan.validate.SaveTenderEnterpriseValidate;
import cc.fypp.gaoyuan.validate.SaveTenderImageValidate;
import cc.fypp.gaoyuan.validate.SaveTenderVectorImageValidate;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;

public class TenderController extends Controller{

	protected final static Logger logger = Logger.getLogger(TenderController.class);


	@Before(SaveTenderImageValidate.class)
	/**
	 * 保存招标图片
	 */
	public void saveTenderImage(){
		String user_id = getPara("user_id");
		String tender_id = getPara("tender_id");
		String imageStr = getPara("image_str");
		String image_name = UUID.randomUUID().toString().replace("-", "");
		FileUtil.byte2File(Base64.decodeBase64(imageStr), ConfigFileUtil.getFilePath(), image_name);
		Record tenderInfo = Db.findById("tender_info", "tender_id", tender_id, "*");
		if(tenderInfo!=null){
			tenderInfo.set("image", image_name);
			Db.update("tender_info", "tender_id", tenderInfo);
		}else{
			tenderInfo = new Record()
			.set("tender_id", tender_id)
			.set("promoter", Integer.valueOf(user_id))
			.set("image", image_name)
			.set("user_del", Constants.TENDER_DEL_STATUS.UN_DELETE)
			.set("create_time", System.currentTimeMillis());
			Db.save("tender_info", "tender_id", tenderInfo);
		}
		renderJson(MessageUtil.successMsg("图片保存成功", ""));
	}

	@Before(SaveTenderVectorImageValidate.class)
	/**
	 * 保存矢量招标图片
	 * @throws FileNotFoundException
	 */
	public void saveVectorImage() throws FileNotFoundException{
		UploadFile uf = getFile("vector_image");
		File file = uf.getFile();
		String user_id = getPara("user_id");
		String tender_id = getPara("tender_id");
		String vectorImage_name = UUID.randomUUID().toString().replace("-", "");
		FileUtil.saveFile(file, ConfigFileUtil.getFilePath(), vectorImage_name);
		Record tenderInfo = Db.findById("tender_info", "tender_id", tender_id, "*");
		if(tenderInfo!=null){
			tenderInfo.set("vector_image", vectorImage_name);
			Db.update("tender_info", "tender_id", tenderInfo);
		}else{
			tenderInfo = new Record()
			.set("tender_id", tender_id)
			.set("promoter", Integer.valueOf(user_id))
			.set("vector_image", vectorImage_name)
			.set("user_del", Constants.TENDER_DEL_STATUS.UN_DELETE)
			.set("create_time", System.currentTimeMillis());
			Db.save("tender_info", "tender_id", tenderInfo);
		}
		renderJson(MessageUtil.successMsg("矢量图片保存成功", ""));
	}
	
	/**
	 * 用户删除招标
	 */
	public void deleteTenderForUser(){
		String tender_id = getPara("tender_id");
		Record tender_info = Db.findFirst("select o.* from tender_info o where o.tender_id = ? ", tender_id);
		if(tender_info!=null){
			tender_info.set("user_del", Constants.ORDER_DEL_STATUS.DELETE);
			Db.update("tender_info", "tender_id", tender_info);
			renderJson(MessageUtil.successMsg("招标信息删除成功", ""));
		}else{
			renderJson(MessageUtil.runtimeErroMsg("招标信息不存在"));
		}
	}
	
	/**
	 * 商户删除招标
	 */
	public void deleteTenderForEnterPrise(){
		String tender_id = getPara("tender_id");
		String merchant_id = getPara("merchant_id");
		Record tender_enterprise = Db.findFirst("select o.* from tender_enterprise o where o.tender_id = ? and o.merchant_id = ?", tender_id,merchant_id);
		if(tender_enterprise!=null){
			tender_enterprise.set("merchant_del", Constants.ORDER_DEL_STATUS.DELETE);
			Db.update("tender_enterprise",tender_enterprise);
			renderJson(MessageUtil.successMsg("招标信息删除成功", ""));
		}else{
			renderJson(MessageUtil.runtimeErroMsg("招标信息不存在"));
		}
	}
	
	/**
	 * 保存或修改招标信息
	 */
	public void saveOrUpdateTender(){
		String user_id = getPara("user_id");
		String tender_id = getPara("tender_id");
		Record tenderInfo = Db.findById("tender_info", "tender_id", tender_id, "*");
		boolean isSave = false;
		if(tenderInfo==null){
			tenderInfo = new Record()
			.set("tender_id", tender_id)
			.set("promoter", Integer.valueOf(user_id))
			.set("user_del", Constants.TENDER_DEL_STATUS.UN_DELETE)
			.set("create_time", System.currentTimeMillis());
			isSave=true;
		}
		if(StringUtils.isNotBlank(getPara("product_id"))){
			tenderInfo.set("product_id", Integer.valueOf(getPara("product_id")));
		}
		
		if(StringUtils.isNotBlank(getPara("size"))){
			tenderInfo.set("size", getPara("size"));
		}
		
		if(StringUtils.isNotBlank(getPara("light_level"))){
			tenderInfo.set("light_level", Integer.valueOf(getPara("light_level")));
		}
		if(StringUtils.isNotBlank(getPara("strength_level"))){
			tenderInfo.set("strength_level", Integer.valueOf(getPara("strength_level")));
		}
		if(StringUtils.isNotBlank(getPara("process_level"))){
			tenderInfo.set("process_level", Integer.valueOf(getPara("process_level")));
		}
		if(StringUtils.isNotBlank(getPara("time"))){
			tenderInfo.set("time", getPara("time"));
		}
		if(StringUtils.isNotBlank(getPara("quantity"))){
			tenderInfo.set("quantity", Integer.valueOf(getPara("quantity")));
		}
		if(StringUtils.isNotBlank(getPara("distance"))){
			tenderInfo.set("distance", getPara("distance"));
		}
		if(StringUtils.isNotBlank(getPara("pickup_type"))){
			tenderInfo.set("pickup_type", getPara("pickup_type"));
		}
		if(StringUtils.isNotBlank(getPara("mark"))){
			tenderInfo.set("mark", getPara("mark"));
		}
		
		if(StringUtils.isNotBlank(getPara("area"))){
			tenderInfo.set("area", BigDecimal.valueOf(Double.valueOf(getPara("area"))));
		}
		
		if(StringUtils.isNotBlank(getPara("perimeter"))){
			tenderInfo.set("perimeter", BigDecimal.valueOf(Double.valueOf(getPara("perimeter"))));
		}
		
		if(StringUtils.isNotBlank(getPara("price"))){
			tenderInfo.set("price", BigDecimal.valueOf(Double.valueOf(getPara("price"))));
		}
		if(isSave){
			Db.save("tender_info","tender_id", tenderInfo);
		}else{
			Db.update("tender_info","tender_id", tenderInfo);
		}
		renderJson(MessageUtil.successMsg("保存招标信息成功", ""));
	}
	
	
	@Before(FindTenderForEenterpriseValidate.class)
	public void findTenderForEenterprise(){
		String merchant_id  = getPara("merchant_id");
		String status = getPara("status");
		String tender_id = getPara("tender_id"); 
		Boolean is_before = getParaToBoolean("is_before");
		Integer page_size = 10;
		if(StringUtils.isNotBlank(getPara("page_size"))){
			page_size = getParaToInt("page_size");
		}
		List<Object> listPara = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder(" select o.*,c.*,a.* from tender_info o inner join tender_enterprise a on a.tender_id=o.tender_id inner join merchant_info c on a.merchant_id = c.merchant_id where a.merchant_id = ? and a.status = ? and a.merchant_del = ?");
		listPara.add(merchant_id);
		listPara.add(status);
		listPara.add(Constants.TENDER_DEL_STATUS.UN_DELETE);
		if(StringUtils.isNotBlank(tender_id)){
			if(is_before){
				sql.append(" and o.create_time < (select t.create_time from tender_info t where t.tender_id=?) ");
			}else{
				sql.append(" and o.create_time > (select t.create_time from tender_info t where t.tender_id=?) ");
			}
			listPara.add(tender_id);
		}
		
		sql.append(" order by o.create_time desc limit ?,?");
		listPara.add(0);
		listPara.add(page_size);
		List<Record> listResult = Db.find(sql.toString(),listPara.toArray(new Object[listPara.size()]));
		
		renderJson(MessageUtil.successMsg("", listResult));
		
	}
	
	@Before(FindTenderForUserValidate.class)
	/**
	 * 用户分页查询招标信息
	 */
	public void findTenderForUser(){
		Long user_id  = getParaToLong("user_id");
		String status = getPara("status");
		String tender_id = getPara("tender_id"); 
		Boolean is_before = getParaToBoolean("is_before");
		Integer page_size = 10;
		if(StringUtils.isNotBlank(getPara("page_size"))){
			page_size = getParaToInt("page_size");
		}
		List<Object> listPara = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder(" select o.*,c.*,a.* from tender_info o inner join tender_enterprise a on a.tender_id = o.tender_id inner join merchant_info c on c.merchant_id = a.merchant_id where o.promoter = ?  and o.user_del = ?");
		listPara.add(user_id);
		listPara.add(Constants.TENDER_DEL_STATUS.UN_DELETE);
		if(StringUtils.isNotBlank(status)){
			sql.append(" and (select count(*) from tender_enterprise a where a.tender_id = o.tender_id and a.status = ? ) > 0 "); 
			listPara.add(status);
		}
		if(StringUtils.isNotBlank(tender_id)){
			if(is_before){
				sql.append(" and o.create_time < (select t.create_time from tender_info t where t.tender_id=?) ");
			}else{
				sql.append(" and o.create_time > (select t.create_time from tender_info t where t.tender_id=?) ");
			}
			listPara.add(tender_id);
		}
		
		sql.append(" order by o.create_time desc limit ?,?");
		listPara.add(0);
		listPara.add(page_size);
		
		List<Record> listResults = Db.find(sql.toString(),listPara.toArray(new Object[listPara.size()]));
		
		

		List<Record> new_listResult = new ArrayList<Record>();
		
		for(Record listResult:listResults){
			listResult.set("images0", Db.find("select o.* from merchant_externalinfo o where o.type=? and o.merchant_id = ?", "0",listResult.getStr("merchant_id")));
			listResult.set("images1", Db.find("select o.* from merchant_externalinfo o where o.type=? and o.merchant_id = ?", "1",listResult.getStr("merchant_id")));
			listResult.set("images2", Db.find("select o.* from merchant_externalinfo o where o.type=? and o.merchant_id = ?", "2",listResult.getStr("merchant_id")));
			listResult.set("images3", Db.find("select o.* from merchant_externalinfo o where o.type=? and o.merchant_id = ?", "3",listResult.getStr("merchant_id")));
			listResult.set("images4", Db.find("select o.* from merchant_externalinfo o where o.type=? and o.merchant_id = ?", "4",listResult.getStr("merchant_id")));
			listResult.set("images5", Db.find("select o.* from merchant_externalinfo o where o.type=? and o.merchant_id = ?", "5",listResult.getStr("merchant_id")));
			new_listResult.add(listResult);
		}
		renderJson(MessageUtil.successMsg("", new_listResult));
		
	}
	
	
	/**
	 * 批量保存招标企业信息
	 */
	@Before(SaveTenderEnterpriseValidate.class)
	public void saveTenderEnterprise(){
		String tender_id = getPara("tender_id");
		String[] merchant_ids = getPara("merchant_id").split(",");
		String sms_template = SmsUtil.getSmsTemplate(Constants.SMS_TYPE.TASK_NOTE.toString());
		if(StringUtils.isBlank(sms_template)){
			renderJson(MessageUtil.runtimeErroMsg("短信模板为空"));
			return;
		}
		for(String merchant_id:merchant_ids){
			Record record = new Record()
			.set("tender_id", tender_id)
			.set("merchant_id", merchant_id)
			.set("status", Constants.TENDER_STATUS.UNTREATED)
			.set("merchant_del", Constants.TENDER_DEL_STATUS.UN_DELETE)
			.set("create_time", System.currentTimeMillis());
			Db.save("tender_enterprise", record);
			/**
			 * TODO
			 * 短信通知商户
			 */
			Record merchant = Db.findById("merchant_info", "merchant_id", merchant_id, "*");
			Thread thread = new Thread(new SendSMS(merchant.getStr("login_name"), sms_template));
			thread.start();
		}
		renderJson(MessageUtil.successMsg("保存招标企业信息成功", ""));
	}
	
	/**
	 * 删除招标企业信息
	 */
	@Before(DeleteTenderEnterpriseValidate.class)
	public void deleteTenderEnterprise(){
		String tender_enterprise_id = getPara("id");
		Db.deleteById("tender_enterprise", Integer.valueOf(tender_enterprise_id));
		renderJson(MessageUtil.successMsg("删除招标企业信息成功", ""));
	}
	
	/**
	 * 根据招标编号查询英标的企业
	 */
	public void findTenderEnterpriseList(){
		String tender_id = getPara("tender_id");
		
		List<Record> tenderEnterprises = Db.find("select o.*,a.*,o.status as t_e_status from tender_enterprise o inner join merchant_info a on o.merchant_id = a.merchant_id where o.tender_id = ?", tender_id);
		
		renderJson(MessageUtil.successMsg("", tenderEnterprises));
	}
	
	/**
	 * 商户回执
	 */
	@Before({MerchantReceiptValidate.class,Tx.class})
	public void merchantReceipt(){
		String tender_enterprise_id = getPara("id");
		String status = getPara("status");
		Record record = Db.findById("tender_enterprise", tender_enterprise_id);
		
		String sms_template = SmsUtil.getSmsTemplate(Constants.SMS_TYPE.RECEIPT.toString());
		if(StringUtils.isBlank(sms_template)){
			renderJson(MessageUtil.runtimeErroMsg("短信模板为空"));
			return;
		}
		
		
		if(Constants.TENDER_STATUS.REFUSE.equals(status)){
			record.set("status", Constants.TENDER_STATUS.REFUSE);
		}
		if(Constants.TENDER_STATUS.AGREE.equals(status)){
			record.set("status", Constants.TENDER_STATUS.AGREE);
			record.set("new_price",BigDecimal.valueOf(Double.valueOf(getPara("price"))));
			record.set("new_time",getPara("time"));
			/**
			 * TODO
			 * 短信通知用户
			 */
			Record user = Db.findFirst("select o.login_name from user_info o inner join tender_info a on o.user_id = a.promoter where a.tender_id = ?", record.getStr("tender_id"));
			Thread thread = new Thread(new SendSMS(user.getStr("login_name"), sms_template));
			thread.start();
		}
		if(StringUtils.isNotBlank(getPara("mark"))){
			record.set("mark", getPara("mark"));
		}
		Db.update("tender_enterprise", record);
		renderJson(MessageUtil.successMsg("回执成功", ""));
	}
	
	
	/**
	 * 查询招标信息详情
	 */
	public void findTenderDetailById(){
		String tender_id = getPara("tender_id");
		Record tender_info = Db.findById("tender_info", "tender_id", tender_id, "*");
		if(tender_info!=null){
			Record product_info = Db.findById("product_info", tender_info.getLong("product_id"));
			Record user_info = Db.findById("user_info", "user_id", tender_info.getLong("promoter"), "*");
			user_info.set("pwd", "");
			StringBuilder sb = new StringBuilder();
			if(product_info!=null){
				List<Record> meterial_infos = Db.find(" select a.meterial from product_material_info o inner join material_info a on o.meterial_id = a.id where o.product_id = ?" ,product_info.getLong("id"));
				if(meterial_infos!=null&&!meterial_infos.isEmpty()){
					for(Record meterial:meterial_infos){
						if(sb.length()>0){
							sb.append(" ");
						}
						sb.append(meterial.getStr("meterial"));
					}
				}
				product_info.set("materials", sb.toString());
			}
			tender_info.set("product_info", product_info);
			tender_info.set("user_info", user_info);
		}
		renderJson(MessageUtil.successMsg("", tender_info));
	}
	
	
	/**
	 * 下载招标图片
	 */
	public void downloadTenderImage(){
		String tender_id = getPara(); 
		Record record = (Record) Db.findById("tender_info", "tender_id", tender_id,"*");
		if(record!=null){
			File file = new File(ConfigFileUtil.getFilePath()+File.separator+record.getStr("image"));
			renderFile(file);
		}
	}
	
	/**
	 * 下载招标矢量图片
	 */
	public void downloadVectorImage(){
		String tender_id = getPara(); 
		Record record = (Record) Db.findById("tender_info", "tender_id", tender_id,"*");
		if(record!=null){
			File file = new File(ConfigFileUtil.getFilePath()+File.separator+record.getStr("vector_image"));
			renderFile(file);
		}
	}
	
	/**
	 * 下载广告图片图片
	 */
	public void downloadAdImage(){
		Record record = (Record) Db.findFirst("select o.* from sys_info o where o.item_name = ?","share_banner");
		if(record!=null){
			JSONObject json= new  JSONObject();
			json.put("item_value", record.getStr("item_value"));
			renderJson(MessageUtil.successMsg("", json));
		}
	}
	
	/**
	 * 保存至购物车
	 */
	public void addToBuyCar(){
		Long user_id = getParaToLong("user_id");
		String tender_id = getPara("tender_id");
		Record buy_car = new Record().set("user_id", user_id).set("tender_id", tender_id);
		Db.save("buy_car", buy_car);
		renderJson(MessageUtil.successMsg("", "成功加入产品库"));
	}
	
	/**
	 * 获取购物车列表
	 */
	public void findBuyCarList(){
		Long user_id  = getParaToLong("user_id");
		long size = Db.queryLong("select count(*) from buy_car o where o.user_id = ?",user_id);
		if(size==0){
			renderJson(MessageUtil.successMsg("", ""));
		}else{
			List<Record> tender_infos = Db.find("select o.* from tender_info o where o.tender_id in (select distinct a.tender_id from buy_car a where a.user_id = ? )", user_id);
			renderJson(MessageUtil.successMsg("", tender_infos));
		}
	}
	
	/**
	 * 批量保存购物车企业信息
	 */
	@Before(SaveBuyCarEnterpriseValidate.class)
	public void saveBuyCarEnterprise(){
		long user_id = getParaToLong("user_id");
		String[] merchant_ids = getPara("merchant_id").split(",");
		String sms_template = SmsUtil.getSmsTemplate(Constants.SMS_TYPE.TASK_NOTE.toString());
		if(StringUtils.isBlank(sms_template)){
			renderJson(MessageUtil.runtimeErroMsg("短信模板为空"));
			return;
		}
		List<Record> buy_cars = Db.find("select distinct o.tender_id from buy_car o where o.user_id = ?", user_id);
		for(String merchant_id:merchant_ids){
			
			for(Record record:buy_cars){
				Record tender_enterprise = new Record()
				.set("tender_id", record.getStr("tender_id"))
				.set("merchant_id", merchant_id)
				.set("status", Constants.TENDER_STATUS.UNTREATED)
				.set("merchant_del", Constants.TENDER_DEL_STATUS.UN_DELETE)
				.set("create_time", System.currentTimeMillis());
				Db.save("tender_enterprise", tender_enterprise);
			}
			
			/**
			 * TODO
			 * 短信通知商户
			 */
			Record merchant = Db.findById("merchant_info", "merchant_id", merchant_id, "*");
			Thread thread = new Thread(new SendSMS(merchant.getStr("login_name"), sms_template));
			thread.start();
		}
		renderJson(MessageUtil.successMsg("保存招标企业信息成功", ""));
	}
	
	class SendSMS implements Runnable{
		
		private String to;
		
		private String content;
		
		public SendSMS(String to,String content){
			this.to=to;
			this.content=content;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			SmsUtil.sendMessage(to, content);
		}
		
	}
	
	

}
