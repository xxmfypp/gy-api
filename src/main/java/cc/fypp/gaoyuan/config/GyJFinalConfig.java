package cc.fypp.gaoyuan.config;

import cc.fypp.gaoyuan.controller.AliPayController;
import cc.fypp.gaoyuan.controller.MateriaController;
import cc.fypp.gaoyuan.controller.MerchantController;
import cc.fypp.gaoyuan.controller.OrderController;
import cc.fypp.gaoyuan.controller.TenderController;
import cc.fypp.gaoyuan.controller.UserController;
import cc.fypp.gaoyuan.handler.DruidStatViewHandler;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.plugin.quartz.QuartzPlugin;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.tx.TxByRegex;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.render.ViewType;


public class GyJFinalConfig extends JFinalConfig {

	@Override
	public void configConstant(Constants arg0) {
		// TODO Auto-generated method stub
		arg0.setDevMode(ConfigFileUtil.getJfinalDebug());
		arg0.setEncoding("UTF-8");
		arg0.setViewType(ViewType.FREE_MARKER);
	}

	@Override
	public void configHandler(Handlers arg0) {
		// TODO Auto-generated method stub
		arg0.add(new DruidStatViewHandler("/druid"));
	}

	@Override
	public void configInterceptor(Interceptors arg0) {
		// TODO Auto-generated method stub
		arg0.add(new TxByRegex(".*delete.*"));
		arg0.add(new TxByRegex(".*add.*"));
		arg0.add(new TxByRegex(".*update.*"));
		arg0.add(new TxByRegex(".*save.*"));
	}

	@Override
	public void configPlugin(Plugins arg0) {
		// TODO Auto-generated method stub
		// 配置Druid数据库连接池插件
		DruidPlugin druidPlugin = new DruidPlugin(ConfigFileUtil.getDbConnectionUrl(), ConfigFileUtil.getDbConnectionUername(), ConfigFileUtil.getDbConnectionPassword());
		druidPlugin.setFilters("wall,stat,mergeStat,config");
		arg0.add(druidPlugin);
		ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
		arg0.add(arp);
		arp.setDialect(new MysqlDialect());
		arp.setShowSql(ConfigFileUtil.getJfinalDebug());
		//配置quartz
		QuartzPlugin quartzPlugin = new QuartzPlugin("job.properties");
        arg0.add(quartzPlugin);
	}

	@Override
	public void configRoute(Routes arg0) {
		// TODO Auto-generated method stub
		arg0.add("user",UserController.class);
		arg0.add("merchant",MerchantController.class);
		arg0.add("materia",MateriaController.class);
		arg0.add("tender",TenderController.class);
		arg0.add("order",OrderController.class);
		arg0.add("alipay",AliPayController.class);
	}


}
