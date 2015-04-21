package cc.fypp.gaoyuan.common.msg;

public interface Constants {


	//返回结果
	interface Result{

		public String STATUS = "status";

		public String MESSAGE = "message";

		public String DATA = "data";

	}

	//短信
	interface SMS{
		//注册验证码
		public String REGISTER_CODE = "0";

		//找回密码验证码
		public String PWD_CODE = "1";
	}

	//短信状态
	interface SMS_STATUS{
		public String UNCECK = "0";
		public String CHECK = "1";
	}

	//商户审核状态
	interface MERCHANT_STATUS{
		//0 未审核 
		public String UNCECK = "0";
		//1审核通过 
		public String CHECK = "1";
		//2审核拒绝
		public String REFUSE = "2";
	}
	/**
	 * 查询商户类型
	 * @author xxm
	 *
	 */
	interface QUERY_TYPE{
		public String LOCAL = "0";

		public String REMOTE = "1";

		public String UNIMITED = "2";
	}

	/**
	 * 商户回单状态
	 * @author xxm
	 *
	 */
	interface TENDER_STATUS{
		//未处理
		public String UNTREATED = "0";

		public String REFUSE = "1";

		public String AGREE = "2";

		//过期
		public String TIMEOUT = "3";
		
		public String END = "4";
	}
	
	interface IS_RECOMMOND{
		public String YES = "1";
		public String NO = "0";
	}

	/**
	 * 订单状态
	 * @author xxm
	 *
	 */
	interface ORDER_STATUS{

		public String UNTREATED = "0";

		//开始
		public String START = "1";

		//结束
		public String END = "2";

	}
	

	/**
	 * 付款/充值状态
	 * @author xxm
	 *
	 */
	interface RECHARGE_STATUS{
		//充值提交
		public String RECHARGE = "0";
		
		//等待付款
		public String WAITPAY = "1";
		
		//完成付款
		public String COMPLETE = "2";
		
		//订单完结 ，不可退款
		public String FINISHED = "3";
		//付款失败
		public String FAILURE = "4";

	}

	/**
	 * 提款状态
	 * @author xxm
	 *
	 */
	interface WITHDRAW_STATUS{
		//付款中
		public String WITHDRAW = "0";
		//完成付款
		public String COMPLETE = "1";
		//付款失败
		public String FAILURE = "2";

	}
	
	/**
	 * 交易状态
	 * @author xxm
	 *
	 */
	interface TRANSATION_STATUS{
		//充值
		public String RECHARGE = "0";
		//提款
		public String WITHDRAW = "1";
		//付款
		public String PAYMENT = "2";
		//收款
		public String RECEIVABLES = "3";

	}

	/**
	 * 任务等级
	 * @author xxm
	 *
	 */
	interface TASK_LEVEL{
		public Integer ONE = 1;

		public Integer TWO = 2;

		public Integer THREE = 3;
	}

	interface ORDER_DEL_STATUS{
		public String UN_DELETE="0";
		public String DELETE = "1";
	}
	
	interface TENDER_DEL_STATUS{
		public String UN_DELETE="0";
		public String DELETE = "1";
	}

	/*interface SMS_TYPE{
		public String REGISTER="REGISTER";
	}*/

	enum SMS_TYPE{
		REGISTER("REGISTER"),TASK_NOTE("TASK_NOTE"),RECEIPT("RECEIPT"),TASK_PHASE1("TASK_PHASE1"),TASK_PHASE2("TASK_PHASE2"),TASK_PHASE3("TASK_PHASE3"),FINDPWS("FINDPWS"),PAY_WARN("PAY_WARN"),COLLECT_WARN("COLLECT_WARN");

		private String type;

		SMS_TYPE(String _type){
			this.type=_type;
		}

		@Override
		public String toString() {
			return String.valueOf (this.type);
		}
	}
	
	interface READ_TYPE{
		//已读
		public String READ="1";
		
		//未读
		public String UN_READ="0";
		
	}


	public String ERRORMSG = "errorMsg";

	public String INFOMSG = "infoMsg";

	//////////////////
	public final static char SEPERATOR = ';';  //分割符号
}
