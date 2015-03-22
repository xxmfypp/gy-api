package cc.fypp.gaoyuan.job;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cc.fypp.gaoyuan.common.msg.Constants;
import cc.fypp.gaoyuan.config.ConfigFileUtil;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class UpdateTenderEnterPriseStatusJob implements Job {
	
	private Logger log = Logger.getLogger(UpdateTenderEnterPriseStatusJob.class);
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		log.info("开始执行定时修改招标状态JOB");
		Db.tx(new IAtom() {

			@Override
			public boolean run() throws SQLException {
				// TODO Auto-generated method stub
				boolean success = true;
				try {
					List<Record> tender_enterprises = Db.find("select o.* from tender_enterprise o where o.status in(?,?)and o.create_time < ?", Constants.TENDER_STATUS.UNTREATED,Constants.TENDER_STATUS.AGREE,System.currentTimeMillis()-ConfigFileUtil.getTenderTimeout());
					if(tender_enterprises!=null&&!tender_enterprises.isEmpty()){
						log.info("有"+tender_enterprises.size()+"条企业招标信息超时");
						for(Record tender_enterprise:tender_enterprises){
							tender_enterprise.set("status", Constants.TENDER_STATUS.TIMEOUT);
							Db.update("tender_enterprise", tender_enterprise);
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					log.error(e.getMessage());
					success=false;
				}
				return success;
			}
			
		});
		
	}

}
