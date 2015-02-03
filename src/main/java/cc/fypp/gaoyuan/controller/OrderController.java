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

import cc.fypp.gaoyuan.common.file.FileUtil;
import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;
import cc.fypp.gaoyuan.common.sms.SmsUtil;
import cc.fypp.gaoyuan.config.ConfigFileUtil;
import cc.fypp.gaoyuan.validate.FindOrderForEenterpriseValidate;
import cc.fypp.gaoyuan.validate.FindOrderForUserValidate;
import cc.fypp.gaoyuan.validate.SaveCommentInfoValidate;
import cc.fypp.gaoyuan.validate.SaveTaskInfoValidate;
import cc.fypp.gaoyuan.validate.UserPaymentValidate;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

public class OrderController extends Controller{

	protected final static Logger logger = Logger.getLogger(OrderController.class);

	/**
	 * 删除订单
	 */
	public void deleteOrder(){
		Long order_id = getParaToLong("order_id");
		Boolean is_user = getParaToBoolean("is_user");
		Record order_info = Db.findFirst("select o.* from order_info o where o.order_id = ? and o.status= ?", order_id,Constants.ORDER_STATUS.END);
		if(order_info!=null){
			if(is_user){
				order_info.set("user_del", Constants.ORDER_DEL_STATUS.DELETE);
			}else{
				order_info.set("merchant_del", Constants.ORDER_DEL_STATUS.DELETE);
			}
			Db.update("order_info", "order_id", order_info);
			renderJson(MessageUtil.successMsg("订单删除成功", ""));
		}else{
			renderJson(MessageUtil.runtimeErroMsg("订单不能删除或订单不存在"));
		}
	}


	/**
	 * 查询订单详情
	 */
	public void findOrderDetailById(){
		Long order_id = getParaToLong("order_id");
		Record order_info = Db.findById("order_info", "order_id",order_id,"*");
		if(order_info!=null){
			Record tender_info = Db.findById("tender_info", "tender_id", order_info.getStr("tender_id"), "*");
			Record product_info = Db.findById("product_info", tender_info.getLong("product_id"));
			Record user_info = Db.findById("user_info", "user_id", order_info.getLong("user_id"), "*");
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
			order_info.set("tender_info", tender_info);
			order_info.set("product_info", product_info);
			order_info.set("user_info", user_info);
			order_info.set("has_recharge", Db.queryBigDecimal("select sum(o.amount) from task_recharge o where o.order_id = ?",order_id));
		}
		renderJson(MessageUtil.successMsg("", order_info));
	}

	

	@Before({UserPaymentValidate.class,Tx.class})
	/**
	 * 用户付款
	 */
	public void userPayment(){
		Long order_id = getParaToLong("order_id");
		Long user_id = getParaToLong("user_id");
		BigDecimal amount = BigDecimal.valueOf(Double.valueOf(getPara("amount")));

		Record user_info = Db.findById("user_info", "user_id", user_id, "*");

		if(user_info.getBigDecimal("balance").compareTo(amount)==-1){
			renderJson(MessageUtil.jsonExceptionMsg(2,"当前余额不足,请尽快充值"));
			return;
		}
		
		Record merchant_info = null;
		
		if(order_id==null){
			String tender_id = getPara("tender_id");
			String merchant_id = getPara("merchant_id");
			BigDecimal total_amount = BigDecimal.valueOf(Double.valueOf(getPara("total_amount")));
			String order_time =  getPara("order_time");
			Record order_info = new Record()
			.set("tender_id", tender_id)
			.set("user_id", user_id)
			.set("merchant_id", merchant_id)
			.set("amount", total_amount)
			.set("create_time", System.currentTimeMillis())
			.set("order_time", order_time)
			.set("user_del", Constants.ORDER_DEL_STATUS.UN_DELETE)
			.set("merchant_del", Constants.ORDER_DEL_STATUS.UN_DELETE);
			
			if(StringUtils.isNotBlank(getPara("customer_name"))){
				order_info.set("customer_name", getPara("customer_name"));
			}

			if(StringUtils.isNotBlank(getPara("customer_address"))){
				order_info.set("customer_address", getPara("customer_address"));
			}

			if(StringUtils.isNotBlank(getPara("customer_phone"))){
				order_info.set("customer_phone", getPara("customer_phone"));
			}
			
			BigDecimal needAmount = total_amount.multiply(new BigDecimal(0.3));
			needAmount = needAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
			if(amount.compareTo(needAmount)!=0){
				renderJson(MessageUtil.runtimeErroMsg("当前应付金额为:"+needAmount.toString()+"元"));
				return;
			}
			
			order_info.set("status", Constants.ORDER_STATUS.START);
			
			Db.save("order_info", "order_id", order_info);
			
			Record tender_enterprise = Db.findFirst("select o.* from tender_enterprise o where o.tender_id = ? and o.merchant_id = ? and o.status = ?", tender_id,merchant_id,Constants.TENDER_STATUS.AGREE);
			if(tender_enterprise!=null){
				tender_enterprise.set("status", Constants.TENDER_STATUS.END);
				Db.update("tender_enterprise",tender_enterprise);
			}else{
				renderJson(MessageUtil.runtimeErroMsg("当前招标已经结束或失效"));
				return;
			}
			order_id = order_info.getLong("order_id");
			merchant_info = Db.findById("merchant_info", "merchant_id", merchant_id, "*");
		}else{
			Record order_info = Db.findById("order_info", "order_id", order_id, "*");
			if(Constants.ORDER_STATUS.END.equals(order_info.getStr("status"))){
				renderJson(MessageUtil.runtimeErroMsg("当前订单已经结束,无法继续付款"));
				return;
			}
			merchant_info = Db.findById("merchant_info", "merchant_id", order_info.getStr("merchant_id"), "*");
			Record task_rechange = Db.findFirst("select o.* from task_recharge o where o.order_id = ? and o.user_id = ?", new Object[]{order_id,user_id});
			if(task_rechange!=null){
				List<Record> task_infos = Db.find("select o.* from task_info o where order_id=?", order_id);
				if(task_infos.size()!=3){
					renderJson(MessageUtil.runtimeErroMsg("当前阶段无法进行付款"));
					return;
				}else{
					BigDecimal hasAmount = task_rechange.getBigDecimal("amount");
					BigDecimal needAmount = order_info.getBigDecimal("amount").subtract(hasAmount);
					if(amount.compareTo(needAmount)!=0){
						renderJson(MessageUtil.runtimeErroMsg("当前应付金额为:"+needAmount.toString()+"元"));
						return;
					}
					order_info.set("status", Constants.ORDER_STATUS.END);
					Db.update("order_info", "order_id", order_info);
				}

			}
		}

		

		//保存付款记录
		Record task_rechange = new Record();
		task_rechange.set("order_id", order_id)
		.set("user_id", user_id)
		.set("amount", amount)
		.set("status", Constants.RECHARGE_STATUS.COMPLETE)
		.set("create_time", System.currentTimeMillis())
		.set("mark", getPara("mark"));
		Db.save("task_recharge", task_rechange);

		//从用户余额中减去付出去的金额
		user_info.set("balance", user_info.getBigDecimal("balance").subtract(amount));
		Db.update("user_info", "user_id", user_info);

		//在商户的余额中加入受到的金额
		merchant_info.set("balance", merchant_info.getBigDecimal("balance").add(amount));
		Db.update("merchant_info", "merchant_id", merchant_info);
		
		try {
			String pay_template = SmsUtil.getSmsTemplate(Constants.SMS_TYPE.PAY_WARN.toString());
			Map<String,String> param = new HashMap<String,String>();
			param.put("name", user_info.getStr("name"));
			param.put("amount", amount.toString());
			param.put("balance", user_info.getBigDecimal("balance").toString());
			pay_template = MessageUtil.getFormatStringByVeloCity(pay_template, param);
			
			Thread thread = new Thread(new TenderController().new SendSMS(user_info.getStr("login_name"), pay_template));
			thread.start();
			
			String collect_template = SmsUtil.getSmsTemplate(Constants.SMS_TYPE.COLLECT_WARN.toString());
			param = new HashMap<String,String>();
			param.put("name", merchant_info.getStr("company"));
			param.put("amount", amount.toString());
			param.put("balance", merchant_info.getBigDecimal("balance").toString());
			collect_template = MessageUtil.getFormatStringByVeloCity(collect_template, param);
			
			Thread thread1 = new Thread(new TenderController().new SendSMS(merchant_info.getStr("login_name"), collect_template));
			thread1.start();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		

		renderJson(MessageUtil.successMsg("付款成功", ""));
	}


	/**上传任务图片
	 * 
	 */
	public void uploadTaskImage(){
		String task_id = getPara("task_id");
		String imageStr = getPara("image_str");
		String image_name = UUID.randomUUID().toString().replace("-", "");
		FileUtil.byte2File(Base64.decodeBase64(imageStr), ConfigFileUtil.getFilePath(), image_name);
		Record record = new Record().set("task_id", task_id)
				.set("image_name", image_name) 
				.set("create_time", System.currentTimeMillis());
		Db.save("task_imageinfo", record);
		JSONObject json = new JSONObject();
		json.put("image_name", image_name);
		renderJson(MessageUtil.successMsg("上传任务图片成功", json));
	}

	/**
	 * 删除任务图片
	 */
	public void deleteTaskImage(){
		String image_name = getPara("image_name");
		Record task_imageinfo = Db.findFirst("select o.* from task_imageinfo o where o.image_name = ?", image_name);
		if(task_imageinfo!=null){
			Db.delete("task_imageinfo", task_imageinfo);
			FileUtil.delete(ConfigFileUtil.getFilePath(), image_name);
			renderJson(MessageUtil.successMsg("任务图片删除成功", ""));
			return;
		}
		renderJson(MessageUtil.runtimeErroMsg("任务图片不存在"));
	}

	/**
	 * 下载任务图片
	 */
	public void downloadTaskImage(){
		Long id = getParaToLong();
		Record record = (Record) Db.findById("task_imageinfo",id);
		if(record!=null){
			File file = new File(ConfigFileUtil.getFilePath()+File.separator+record.getStr("image_name"));
			renderFile(file);
		}
	}

	/**
	 * 获取任务列表
	 */
	public void getTaskList(){
		Long order_id = getParaToLong("order_id");
		List<Record> task_lists = Db.find("select o.* from task_info o where o.order_id = ?", order_id);
		List<Record> new_task_lists = new ArrayList<Record>();
		if(task_lists!=null&&!task_lists.isEmpty()){
			for(Record task:task_lists){
				List<Record> task_images = Db.find("select o.* from task_imageinfo o where o.task_id = ? ", task.getStr("task_id"));
				task.set("images", task_images);
				new_task_lists.add(task);
			}
		}

		renderJson(MessageUtil.successMsg("", new_task_lists));
	}

	/**
	 * 保存评论信息
	 */
	@Before(SaveCommentInfoValidate.class)
	public void saveCommentInfo(){
		Long order_id  = getParaToLong("order_id");
		String merchant_id = getPara("merchant_id");
		Long user_id = getParaToLong("user_id");
		Integer quality = getParaToInt("quality");
		Integer efficiency = getParaToInt("efficiency");
		Integer service = getParaToInt("service");
		String comment = getPara("comment");
		Record comment_info = new Record()
		.set("order_id", order_id)
		.set("merchant_id", merchant_id)
		.set("user_id", user_id)
		.set("quality", quality)
		.set("efficiency", efficiency)
		.set("service", service)
		.set("comment", comment)
		.set("create_time", System.currentTimeMillis());
		Db.save("comment_info", comment_info);
		renderJson(MessageUtil.successMsg("评论成功", ""));
	}

	/**
	 * 根据订单编号查询评论信息
	 */
	public void findCommentByOrderId(){
		Long order_id  = getParaToLong("order_id");
		List<Record> commentInfos = Db.find("select o.* from comment_info o where o.order_id = ?", order_id);

		List<Record> new_commentInfos = new ArrayList<Record>();
		if(commentInfos!=null&&!commentInfos.isEmpty()){
			for(Record comment_info:commentInfos){
				Record user_info =  Db.findById("user_info", "user_id", comment_info.getLong("user_id"), "*");
				comment_info.set("name", user_info.getStr("name"));
				comment_info.set("login_name", user_info.getStr("login_name"));
				comment_info.set("level", user_info.getInt("level"));
				new_commentInfos.add(comment_info);
			}
		}


		renderJson(MessageUtil.successMsg("", new_commentInfos));
	}

	/**
	 * 根据企业编号分页查询评论信息
	 */
	public void findCommentOfPageByMerchantId(){
		String merchant_id  = getPara("merchant_id");
		Long comment_id = getParaToLong("comment_id"); 
		Boolean is_before = getParaToBoolean("is_before");
		Integer page_size = 10;
		if(StringUtils.isNotBlank(getPara("page_size"))){
			page_size = getParaToInt("page_size");
		}
		List<Object> listPara = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder(" select o.* from comment_info o  where o.merchant_id = ? ");
		listPara.add(merchant_id);
		if(StringUtils.isNotBlank(getPara("comment_id"))){
			if(is_before){
				sql.append(" and o.create_time < (select t.create_time from comment_info t where t.id=?) ");
			}else{
				sql.append(" and o.create_time > (select t.create_time from comment_info t where t.id=?) ");
			}
			listPara.add(comment_id);
		}

		sql.append(" order by o.create_time desc  limit ?,?");
		listPara.add(0);
		listPara.add(page_size);

		List<Record> listResult = Db.find(sql.toString(),listPara.toArray(new Object[listPara.size()]));


		List<Record> new_commentInfos = new ArrayList<Record>();
		if(listResult!=null&&!listResult.isEmpty()){
			for(Record comment_info:listResult){
				Record user_info =  Db.findById("user_info", "user_id", comment_info.getLong("user_id"), "*");
				comment_info.set("name", user_info.getStr("name"));
				comment_info.set("login_name", user_info.getStr("login_name"));
				comment_info.set("level", user_info.getInt("level"));
				new_commentInfos.add(comment_info);
			}
		}

		renderJson(MessageUtil.successMsg("", new_commentInfos));
	}

	/**
	 * 获取任务图片列表
	 */
	public void getImageList(){
		String task_id = getPara("task_id");
		List<Record> task_image_lists = Db.find("select o.* from task_imageinfo o where o.task_id = ?", task_id);
		renderJson(MessageUtil.successMsg("", task_image_lists));
	}

	@Before(SaveTaskInfoValidate.class)
	/**
	 * 保存任务信息
	 */
	public void saveTaskInfo(){
		String task_id = getPara("task_id");
		Long order_id =  getParaToLong("order_id");

		String mark =  getPara("mark");
		List<Record> task_lists = Db.find("select o.* from task_info o where o.order_id = ?", order_id);
		int task_level = task_lists.size()+1;
		
		
		Record record =  new Record()
		.set("task_level", task_level)
		.set("task_id", task_id)
		.set("order_id", order_id)
		.set("create_time", System.currentTimeMillis())
		.set("is_read", Constants.READ_TYPE.UN_READ)
		.set("mark", mark);
		
		//发送消息通知订单已经开始
		Record user_info = Db.findFirst("select o.* from user_info o where o.user_id = (select a.user_id from order_info a where a.order_id = ? )", order_id);
		String sms_template = null;
		if(task_level==1){
			sms_template = SmsUtil.getSmsTemplate(Constants.SMS_TYPE.TASK_PHASE1.toString());
			if(StringUtils.isBlank(sms_template)){
				renderJson(MessageUtil.runtimeErroMsg("短信模板为空"));
				return;
			}
		}else if(task_level==2){
			sms_template = SmsUtil.getSmsTemplate(Constants.SMS_TYPE.TASK_PHASE2.toString());
			if(StringUtils.isBlank(sms_template)){
				renderJson(MessageUtil.runtimeErroMsg("短信模板为空"));
				return;
			}
		}else if(task_level==3){
			sms_template = SmsUtil.getSmsTemplate(Constants.SMS_TYPE.TASK_PHASE3.toString());
			if(StringUtils.isBlank(sms_template)){
				renderJson(MessageUtil.runtimeErroMsg("短信模板为空"));
				return;
			}
		}
		Db.save("task_info", "task_id",record);
		
		
		Thread thread = new Thread(new TenderController().new SendSMS(user_info.getStr("login_name"), sms_template));
		thread.start();
		
		renderJson(MessageUtil.successMsg("保存任务信息成功", record));
	}
	
	/**
	 * 设置任务状态为已读
	 */
	public void setTaskReadStauts(){
		String task_id = getPara("task_id");
		Record task_info  = Db.findById("task_info", "task_id", task_id, "*");
		if(task_info==null){
			renderJson(MessageUtil.runtimeErroMsg("任务信息不存在"));
			return;
		}
		task_info.set("is_read", Constants.READ_TYPE.READ);
		Db.update("task_info", "task_id", task_info);
		renderJson(MessageUtil.successMsg("修改任务信息状态成功", null));
	}

	/**
	 * 商户分页查询订单信息
	 */
	@Before(FindOrderForEenterpriseValidate.class)
	public void findOrderForEnterprise(){
		String merchant_id  = getPara("merchant_id");
		String status = getPara("status");
		Long order_id = getParaToLong("order_id"); 
		Boolean is_before = getParaToBoolean("is_before");
		Integer page_size = 10;
		if(StringUtils.isNotBlank(getPara("page_size"))){
			page_size = getParaToInt("page_size");
		}
		List<Object> listPara = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder(" select o.* from order_info o  where o.merchant_id = ? and o.status = ?  and o.merchant_del = ?");
		listPara.add(merchant_id);
		listPara.add(status);
		listPara.add(Constants.ORDER_DEL_STATUS.UN_DELETE);
		if(StringUtils.isNotBlank(getPara("order_id"))){
			if(is_before){
				sql.append(" and o.create_time < (select t.create_time from order_info t where t.order_id=?) ");
			}else{
				sql.append(" and o.create_time > (select t.create_time from order_info t where t.order_id=?) ");
			}
			listPara.add(order_id);
		}

		sql.append(" order by o.create_time desc  limit ?,?");
		listPara.add(0);
		listPara.add(page_size);

		List<Record> listResult = Db.find(sql.toString(),listPara.toArray(new Object[listPara.size()]));

		renderJson(MessageUtil.successMsg("", listResult));
	}

	/**
	 * 用户分页查询订单信息
	 */
	@Before(FindOrderForUserValidate.class)
	public void findOrderForUser(){
		Long user_id  = getParaToLong("user_id");
		String status = getPara("status");
		Long order_id = getParaToLong("order_id"); 
		Boolean is_before = getParaToBoolean("is_before");
		Integer page_size = 10;
		if(StringUtils.isNotBlank(getPara("page_size"))){
			page_size = getParaToInt("page_size");
		}
		List<Object> listPara = new ArrayList<Object>();

		StringBuilder sql = new StringBuilder("select a.*,o.* from order_info  o  inner join merchant_info a on a.merchant_id=o.merchant_id where o.user_id = ? and user_del = ?");
		listPara.add(user_id);
		listPara.add(Constants.ORDER_DEL_STATUS.UN_DELETE);
		if(StringUtils.isNotBlank(status)){
			sql.append(" and o.status = ? ");
			listPara.add(status);
		}

		if(StringUtils.isNotBlank(getPara("order_id"))){
			if(is_before){
				sql.append(" and o.create_time < (select t.create_time from order_info t where t.order_id=?) ");
			}else{
				sql.append(" and o.create_time > (select t.create_time from order_info t where t.order_id=?) ");
			}
			listPara.add(order_id);
		}
		sql.append(" order by o.create_time desc  limit ?,?");
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
			listResult.set("task_info",Db.find("select o.* from task_info o where o.order_id = ? and o.task_level = (select max(a.task_level) from task_info a where a.order_id = ?) ",listResult.getLong("order_id"),listResult.getLong("order_id")));
			new_listResult.add(listResult);
		}
		renderJson(MessageUtil.successMsg("", new_listResult));
	}

}
