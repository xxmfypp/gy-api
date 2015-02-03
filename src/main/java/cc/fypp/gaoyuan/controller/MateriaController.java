package cc.fypp.gaoyuan.controller;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cc.fypp.gaoyuan.common.msg.MessageUtil;
import cc.fypp.gaoyuan.config.ConfigFileUtil;
import cc.fypp.gaoyuan.validate.GetProductListValidate;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

public class MateriaController extends Controller{

	protected final static Logger logger = Logger.getLogger(MateriaController.class);


	/**
	 * 获取材料列表
	 */
	public void getMaterialList(){
		List<Record> materialList = Db.find("select o.* from material_info o ");
		renderJson(MessageUtil.successMsg("", materialList));
	}

	/**
	 * 获取场景列表
	 */
	public void getSceneList(){
		List<Record> sceneList = Db.find("select o.* from scene_info o ");
		renderJson(MessageUtil.successMsg("", sceneList));
	}

	@Before({GetProductListValidate.class,Tx.class})
	/**
	 * 获取产品信息(带价格)
	 */
	public void getProductList(){
		Long materia_id = getParaToLong("materia_id");
		BigDecimal area = new BigDecimal(getPara("area"));
		BigDecimal perimeter = new BigDecimal(getPara("perimeter"));
		Integer scene_id = Integer.valueOf(getPara("scene_id"));
		Integer light_level = Integer.valueOf(getPara("light_level"));
		Integer strength_level = Integer.valueOf(getPara("strength_level"));
		Integer process_level = Integer.valueOf(getPara("process_level"));
		List<Record> productList = Db.find("select o.* from product_info o inner join product_material_info b on o.id = b.product_id inner join product_scene_info a on o.id = a.product_id where  b.meterial_id = ? and a.scene_id = ?",new Object[]{materia_id,scene_id});
		if(productList!=null&&!productList.isEmpty()){
			for(Record product:productList){
				List<Record> attributeList = Db.find("select o.* from attribute_info o where o.id in (select a.attribute_id from product_attribute_info a where a.product_id = ?)", product.getLong("id"));
				BigDecimal totalPrice =  new BigDecimal(0);
				if(attributeList!=null&&!attributeList.isEmpty()){
					for(Record attribute:attributeList){
						BigDecimal attributeTotalPrice =  new BigDecimal(0);
						List<Record> priceList = Db.find("select o.* from price_info o where o.attribute_id = ? and ((o.level_type=? and o.level = ?) or (o.level_type=? and o.level = ?) or (o.level_type=? and o.level = ?))", new Object[]{attribute.getLong("id"),1,light_level,2,strength_level,3,process_level});
						if(priceList!=null&&!priceList.isEmpty()){
							for(Record price:priceList){
								//乘以面积
								if(price.getInt("charging_type")==1){
									attributeTotalPrice = attributeTotalPrice.add(price.getBigDecimal("price").multiply(area));
								}else if(price.getInt("charging_type")==2){//乘以周长
									attributeTotalPrice = attributeTotalPrice.add(price.getBigDecimal("price").multiply(perimeter));
								}

							}
						}
						totalPrice = totalPrice.add(attributeTotalPrice);
					}
				}
				product.set("total_price", totalPrice.multiply(new BigDecimal(2)));
			}
		}
		renderJson(MessageUtil.successMsg("", productList));
	}

	/**
	 * 产品图片下载
	 */
	public void downloadProductImage(){
		String product_id = getPara();
		Record record = (Record) Db.findById("product_info", Integer.valueOf(product_id));
		if(record!=null){
			File file = new File(ConfigFileUtil.getFilePath()+"/.."+record.getStr("image_url"));
			renderFile(file);
		}
	}
	
	public void testPrice(){
		Long product_id = getParaToLong("product_id");
		BigDecimal area = new BigDecimal(getPara("area"));
		BigDecimal perimeter = new BigDecimal(getPara("perimeter"));
		Integer light_level = Integer.valueOf(getPara("light_level"));
		Integer strength_level = Integer.valueOf(getPara("strength_level"));
		Integer process_level = Integer.valueOf(getPara("process_level"));


		Record product = Db.findById("product_info", product_id);
		List<Record> attributeList = Db.find("select o.* from attribute_info o where o.id in (select a.attribute_id from product_attribute_info a where a.product_id = ?)", product.getLong("id"));
		BigDecimal totalPrice =  new BigDecimal(0);
		if(attributeList!=null&&!attributeList.isEmpty()){
			for(Record attribute:attributeList){
				BigDecimal attributeTotalPrice =  new BigDecimal(0);
				List<Record> priceList = Db.find("select o.* from price_info o where o.attribute_id = ? and ((o.level_type=? and o.level = ?) or (o.level_type=? and o.level = ?) or (o.level_type=? and o.level = ?))", new Object[]{attribute.getLong("id"),1,light_level,2,strength_level,3,process_level});
				if(priceList!=null&&!priceList.isEmpty()){
					for(Record price:priceList){
						//乘以面积
						if(price.getInt("charging_type")==1){
							attributeTotalPrice = attributeTotalPrice.add(price.getBigDecimal("price").multiply(area));
						}else if(price.getInt("charging_type")==2){//乘以周长
							attributeTotalPrice = attributeTotalPrice.add(price.getBigDecimal("price").multiply(perimeter));
						}

					}
				}
				totalPrice = totalPrice.add(attributeTotalPrice);
			}
		}
		product.set("total_price", totalPrice.multiply(new BigDecimal(2)));
		renderJson(MessageUtil.successMsg("", product));
	}
	
	/**
	 * 获取产品参考价格
	 */
	public void consultPrice(){
		Long product_id = getParaToLong("product_id");
		BigDecimal area = new BigDecimal(getPara("area"));
		BigDecimal perimeter = new BigDecimal(getPara("perimeter"));
		Integer light_level = Integer.valueOf(getPara("light_level"));
		Integer strength_level = Integer.valueOf(getPara("strength_level"));
		Integer process_level = Integer.valueOf(getPara("process_level"));
		Record product = Db.findById("product_info", product_id);
		
		List<Record> attributeList = Db.find("select o.* from attribute_info o where o.id in (select a.attribute_id from product_attribute_info a where a.product_id = ?)", product.getLong("id"));
		List<Record> new_attributeList = new ArrayList<Record>();
		
		if(attributeList!=null&&!attributeList.isEmpty()){
			for(Record attribute:attributeList){
				
				List<Record> priceList = Db.find("select o.* from price_info o where o.attribute_id = ? and ((o.level_type=? and o.level = ?) or (o.level_type=? and o.level = ?) or (o.level_type=? and o.level = ?))", new Object[]{attribute.getLong("id"),1,light_level,2,strength_level,3,process_level});
				List<Record> new_priceList =  new ArrayList<Record>();
				
				if(priceList!=null&&!priceList.isEmpty()){
					for(Record price:priceList){
						//乘以面积
						if(price.getInt("charging_type")==1){
							price.set("total_price", price.getBigDecimal("price").multiply(area));
						}else if(price.getInt("charging_type")==2){//乘以周长
							price.set("total_price", price.getBigDecimal("price").multiply(perimeter));
						}
						new_priceList.add(price);
					}
				}
				attribute.set("price_list", new_priceList);
				new_attributeList.add(attribute);
			}
		}
		renderJson(MessageUtil.successMsg("", new_attributeList));
	}


}
