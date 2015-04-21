package cc.fypp.gaoyuan.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.common.msg.MessageUtil;
import cc.fypp.gaoyuan.common.sms.SmsUtil;

import com.alipay.util.AlipayNotify;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class AliPayController extends Controller{

	private final static Logger logger = Logger.getLogger(AliPayController.class);

	public void notifyUrl(){

		try{
			Map<String,String> params = new HashMap<String,String>();

			Map<String, String[]> requestParams =getParaMap();

			for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i]
							: valueStr + values[i] + ",";
				}
				params.put(name, valueStr);
			}
			//商户订单号
			String out_trade_no = new String(getPara("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
			//支付宝交易号
			String trade_no = new String(getPara("trade_no").getBytes("ISO-8859-1"),"UTF-8");
			//交易状态
			String trade_status = new String(getPara("trade_status").getBytes("ISO-8859-1"),"UTF-8");

			if(AlipayNotify.verify(params)){
				logger.info("交易状态:"+trade_status);
				logger.info("支付宝交易号:"+trade_no);
				logger.info("商户订单号:"+out_trade_no);


				if(trade_status.equals("WAIT_BUYER_PAY")){//等待付款
					rechargeWaitProcess(out_trade_no, trade_no);
				}else if (trade_status.equals("TRADE_SUCCESS")){//交易成功完成
					rechargeSuccessProcess(out_trade_no, trade_no);
				}else if(trade_status.equals("TRADE_FINISHED")){//订单完结不可再退款
					rechargeFinishProcess(out_trade_no, trade_no);
				}else{
					logger.error("支付宝回调状态异常！状态为:"+trade_status);
					renderText("fail");
				}

			}else{
				renderText("fail");
			}

		}catch(Exception e){
			e.printStackTrace();
			logger.error("支付宝回调失败!"+e.getMessage());
			renderText("fail");
		}

	}

	/**
	 * 等待用户付款
	 * @param out_trade_no
	 * @param trade_no
	 */
	private void rechargeWaitProcess(String out_trade_no,String trade_no){
		//用户充值订单号
		if(out_trade_no.startsWith("UR_")){
			Record user_recharge_info = Db.findById("user_recharge_info", out_trade_no);
			if(user_recharge_info==null){
				logger.error("订单号:"+out_trade_no+" 不在系统中");
				renderText("fail");
			}else{
				if(Constants.RECHARGE_STATUS.RECHARGE.equals(user_recharge_info.getStr("status"))){
					user_recharge_info.set("zhifubao_trade_id", trade_no);
					user_recharge_info.set("status", Constants.RECHARGE_STATUS.WAITPAY);
					Db.update("user_recharge_info", user_recharge_info);
					renderText("success");
				}else{
					logger.error("订单号:"+out_trade_no+" 的状态不对，当前状态为:"+user_recharge_info.getStr("status"));
					renderText("fail");
				}
			}
		}else if(out_trade_no.startsWith("MR_")){//商户充值订单号
			Record merchant_recharge_info = Db.findById("merchant_recharge_info", out_trade_no);
			if(merchant_recharge_info==null){
				logger.error("订单号:"+out_trade_no+" 不在系统中");
				renderText("fail");
			}else{
				if(Constants.RECHARGE_STATUS.RECHARGE.equals(merchant_recharge_info.getStr("status"))){
					merchant_recharge_info.set("zhifubao_trade_id", trade_no);
					merchant_recharge_info.set("status", Constants.RECHARGE_STATUS.WAITPAY);
					Db.update("merchant_recharge_info", merchant_recharge_info);
					renderText("success");
				}else{
					logger.error("订单号:"+out_trade_no+" 的状态不对，当前状态为:"+merchant_recharge_info.getStr("status"));
					renderText("fail");
				}
			}
		}else{
			logger.error("订单号:"+out_trade_no+" 不在系统中");
			renderText("fail");
		}
	}

	private void rechargeSuccessProcess(String out_trade_no,String trade_no){
		//用户充值订单号
		if(out_trade_no.startsWith("UR_")){
			Record user_recharge_info = Db.findById("user_recharge_info", out_trade_no);
			if(user_recharge_info==null){
				logger.error("订单号:"+out_trade_no+" 不在系统中");
				renderText("fail");
			}else{
				if(Constants.RECHARGE_STATUS.WAITPAY.equals(user_recharge_info.getStr("status"))||Constants.RECHARGE_STATUS.RECHARGE.equals(user_recharge_info.getStr("status"))){
					user_recharge_info.set("zhifubao_trade_id", trade_no);
					user_recharge_info.set("status", Constants.RECHARGE_STATUS.COMPLETE);
					Db.update("user_recharge_info", user_recharge_info);

					//将余额加之用户账号中
					Record user_info = Db.findById("user_info", "user_id", user_recharge_info.getLong("user_id"), "*");
					user_info.set("balance", user_info.getBigDecimal("balance").add(user_recharge_info.getBigDecimal("amount")));
					Db.update("user_info", "user_id", user_info);
					Record account_transaction_his = new Record()
					.set("login_name", user_info.getStr("login_name"))
					.set("amount",user_recharge_info.getBigDecimal("amount"))
					.set("type", Constants.TRANSATION_STATUS.RECHARGE)
					.set("description", "支付宝充值")
					.set("create_time", System.currentTimeMillis())
					.set("external_id",out_trade_no);
					Db.save("account_transaction_his", account_transaction_his);
					
					//短信通知
					try {
						String collect_template = SmsUtil.getSmsTemplate(Constants.SMS_TYPE.COLLECT_WARN.toString());
						Map<String,String> param = new HashMap<String,String>();
						param.put("name", user_info.getStr("name"));
						param.put("amount", user_recharge_info.getBigDecimal("amount").toString());
						param.put("balance", user_info.getBigDecimal("balance").toString());
						collect_template = MessageUtil.getFormatStringByVeloCity(collect_template, param);
						
						Thread thread = new Thread(new TenderController().new SendSMS(user_info.getStr("login_name"), collect_template));
						thread.start();
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}

					renderText("success");
				}else{
					logger.error("订单号:"+out_trade_no+" 的状态不对，当前状态为:"+user_recharge_info.getStr("status"));
					renderText("fail");
				}
			}
		}else if(out_trade_no.startsWith("MR_")){//商户充值订单号
			Record merchant_recharge_info = Db.findById("merchant_recharge_info", out_trade_no);
			if(merchant_recharge_info==null){
				logger.error("订单号:"+out_trade_no+" 不在系统中");
				renderText("fail");
			}else{
				if(Constants.RECHARGE_STATUS.WAITPAY.equals(merchant_recharge_info.getStr("status"))){
					merchant_recharge_info.set("zhifubao_trade_id", trade_no);
					merchant_recharge_info.set("status", Constants.RECHARGE_STATUS.COMPLETE);
					Db.update("merchant_recharge_info", merchant_recharge_info);

					//将余额加之商户账号中
					Record merchant_info = Db.findById("merchant_info", "merchant_id", merchant_recharge_info.getStr("merchant_id"), "*");
					merchant_info.set("balance", merchant_info.getBigDecimal("balance").add(merchant_recharge_info.getBigDecimal("amount")));
					Db.update("merchant_info", "merchant_id", merchant_info);
					
					Record account_transaction_his = new Record()
					.set("login_name", merchant_info.getStr("login_name"))
					.set("amount",merchant_recharge_info.getBigDecimal("amount"))
					.set("description", "支付宝充值")
					.set("type", Constants.TRANSATION_STATUS.RECHARGE)
					.set("create_time", System.currentTimeMillis())
					.set("external_id",out_trade_no);
					Db.save("account_transaction_his", account_transaction_his);
					
					//短信通知
					try {
						String collect_template = SmsUtil.getSmsTemplate(Constants.SMS_TYPE.COLLECT_WARN.toString());
						Map<String,String> param = new HashMap<String,String>();
						param.put("name", merchant_info.getStr("company"));
						param.put("amount", merchant_recharge_info.getBigDecimal("amount").toString());
						param.put("balance", merchant_info.getBigDecimal("balance").toString());
						collect_template = MessageUtil.getFormatStringByVeloCity(collect_template, param);
						
						Thread thread = new Thread(new TenderController().new SendSMS(merchant_info.getStr("login_name"), collect_template));
						thread.start();
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					
					renderText("success");
				}else{
					logger.error("订单号:"+out_trade_no+" 的状态不对，当前状态为:"+merchant_recharge_info.getStr("status"));
					renderText("fail");
				}
			}
		}else{
			logger.error("订单号:"+out_trade_no+" 不在系统中");
			renderText("fail");
		}

	}
	
	
	/**
	 * 订单完结，不可退款
	 * @param out_trade_no
	 * @param trade_no
	 */
	private void rechargeFinishProcess(String out_trade_no,String trade_no){
		//用户充值订单号
		if(out_trade_no.startsWith("UR_")){
			Record user_recharge_info = Db.findById("user_recharge_info", out_trade_no);
			if(user_recharge_info==null){
				logger.error("订单号:"+out_trade_no+" 不在系统中");
				renderText("fail");
			}else{
				user_recharge_info.set("zhifubao_trade_id", trade_no);
				user_recharge_info.set("status", Constants.RECHARGE_STATUS.FINISHED);
				Db.update("user_recharge_info", user_recharge_info);
				
				if(Constants.RECHARGE_STATUS.RECHARGE.equals(user_recharge_info.getStr("status"))||Constants.RECHARGE_STATUS.WAITPAY.equals(user_recharge_info.getStr("status"))){
					//将余额加之用户账号中
					Record user_info = Db.findById("user_info", "user_id", user_recharge_info.getLong("user_id"), "*");
					user_info.set("balance", user_info.getBigDecimal("balance").add(user_recharge_info.getBigDecimal("amount")));
					Db.update("user_info", "user_id", user_info);
					Record account_transaction_his = new Record()
					.set("login_name", user_info.getStr("login_name"))
					.set("amount",user_info.getBigDecimal("amount"))
					.set("description", "支付宝充值")
					.set("type", Constants.TRANSATION_STATUS.RECHARGE)
					.set("create_time", System.currentTimeMillis())
					.set("external_id",out_trade_no);
					Db.save("account_transaction_his", account_transaction_his);
					
					//短信通知
					try {
						String collect_template = SmsUtil.getSmsTemplate(Constants.SMS_TYPE.COLLECT_WARN.toString());
						Map<String,String> param = new HashMap<String,String>();
						param.put("name", user_info.getStr("name"));
						param.put("amount", user_recharge_info.getBigDecimal("amount").toString());
						param.put("balance", user_info.getBigDecimal("balance").toString());
						collect_template = MessageUtil.getFormatStringByVeloCity(collect_template, param);
						
						Thread thread = new Thread(new TenderController().new SendSMS(user_info.getStr("login_name"), collect_template));
						thread.start();
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					
				}
				renderText("success");
				
			}
		}else if(out_trade_no.startsWith("MR_")){//商户充值订单号
			Record merchant_recharge_info = Db.findById("merchant_recharge_info", out_trade_no);
			if(merchant_recharge_info==null){
				logger.error("订单号:"+out_trade_no+" 不在系统中");
				renderText("fail");
			}else{
				merchant_recharge_info.set("zhifubao_trade_id", trade_no);
				merchant_recharge_info.set("status", Constants.RECHARGE_STATUS.FINISHED);
				Db.update("merchant_recharge_info", merchant_recharge_info);
				if(Constants.RECHARGE_STATUS.RECHARGE.equals(merchant_recharge_info.getStr("status"))||Constants.RECHARGE_STATUS.WAITPAY.equals(merchant_recharge_info.getStr("status"))){
					//将余额加之商户账号中
					Record merchant_info = Db.findById("merchant_info", "merchant_id", merchant_recharge_info.getStr("merchant_id"), "*");
					merchant_info.set("balance", merchant_info.getBigDecimal("balance").add(merchant_recharge_info.getBigDecimal("amount")));
					Db.update("merchant_info", "merchant_id", merchant_info);
					
					Record account_transaction_his = new Record()
					.set("login_name", merchant_recharge_info.getStr("login_name"))
					.set("amount",merchant_recharge_info.getBigDecimal("amount"))
					.set("description", "支付宝充值")
					.set("type", Constants.TRANSATION_STATUS.RECHARGE)
					.set("create_time", System.currentTimeMillis())
					.set("external_id",out_trade_no);
					Db.save("account_transaction_his", account_transaction_his);
					
					//短信通知
					try {
						String collect_template = SmsUtil.getSmsTemplate(Constants.SMS_TYPE.COLLECT_WARN.toString());
						Map<String,String> param = new HashMap<String,String>();
						param.put("name", merchant_info.getStr("company"));
						param.put("amount", merchant_recharge_info.getBigDecimal("amount").toString());
						param.put("balance", merchant_info.getBigDecimal("balance").toString());
						collect_template = MessageUtil.getFormatStringByVeloCity(collect_template, param);
						
						Thread thread = new Thread(new TenderController().new SendSMS(merchant_info.getStr("login_name"), collect_template));
						thread.start();
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					
				}
				renderText("success");
			}
		}else{
			logger.error("订单号:"+out_trade_no+" 不在系统中");
			renderText("fail");
		}
	}


}
