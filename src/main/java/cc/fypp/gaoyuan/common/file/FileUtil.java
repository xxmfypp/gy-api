/**
 * 
 */
package cc.fypp.gaoyuan.common.file;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;


@SuppressWarnings("restriction")
public class FileUtil {
	private static final Logger logger = Logger.getLogger(FileUtil.class);

	public static void byte2File(byte[] buf, String filePath, String fileName){  
		BufferedOutputStream bos = null;  
		FileOutputStream fos = null;  
		File file = null;  
		try{  
			File dir = new File(filePath);  
			if (!dir.exists()){  
				dir.mkdirs();  
			}  
			file = new File(filePath + File.separator + fileName);  
			fos = new FileOutputStream(file);  
			bos = new BufferedOutputStream(fos);  
			bos.write(buf);  
		}catch (Exception e){  
			e.printStackTrace();  
		}  
		finally  {  
			if (bos != null)  {  
				try  {  
					bos.close();  
				}catch (IOException e){  
//					e.printStackTrace();  
					logger.info(e.getMessage());
				}  
			}  
			if (fos != null)  {  
				try  {  
					fos.close();  
				}catch (IOException e){  
//					e.printStackTrace();  
					logger.info(e.getMessage());
				}  
			}  
		}  
	} 
	
	public static void saveFile(File file, String filePath, String fileName) throws FileNotFoundException{
		InputStream inStream = null;
		FileOutputStream fs = null;
		try{  
			File dir = new File(filePath);  
			if (!dir.exists()){  
				dir.mkdirs();  
			}  
			 int bytesum = 0; 
	         int byteread = 0; 
			  inStream = new FileInputStream(file); //读入原文件 
              fs = new FileOutputStream(filePath + File.separator + fileName); 
             byte[] buffer = new byte[1024]; 
             while ( (byteread = inStream.read(buffer)) != -1) { 
                 bytesum += byteread; //字节数 文件大小 
                 fs.write(buffer, 0, byteread); 
             } 
             inStream.close(); 
			
		}catch (Exception e){  
			e.printStackTrace();  
		}finally  {  
			if (fs != null)  {  
				try  {  
					fs.close();  
				}catch (IOException e){  
//					e.printStackTrace();  
					logger.info(e.getMessage());
				}  
			} 
			if(inStream!=null){
				try {
					inStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
			
		}  
	}

	public static void delete( String filePath, String fileName){
		File file = new File(filePath + File.separator + fileName);  
		if(file.exists()){
			file.delete();
		}
	}

	public static byte[] File2byte(String filePath){  
		byte[] buffer = null;  
		try  {  
			File file = new File(filePath);  
			FileInputStream fis = new FileInputStream(file);  
			ByteArrayOutputStream bos = new ByteArrayOutputStream();  
			byte[] b = new byte[1024];  
			int n;  
			while ((n = fis.read(b)) != -1)  {  
				bos.write(b, 0, n);  
			}  
			fis.close();  
			bos.close();  
			buffer = bos.toByteArray();  
		} catch (FileNotFoundException e)  {  
			logger.info(e.getMessage());
		}  catch (IOException e)  {  
			logger.info(e.getMessage());
		}  
		return buffer;  
	}  
	
	
	
	public static void createThumbnail(String filePath,String fileName){
	  try{
	   File srcfile = new File(filePath+File.separator+fileName);
	   if(!srcfile.exists()){
	    System.out.println("文件不存在");
	    return;
	   }
	   
	  BufferedImage image = ImageIO.read(srcfile);
	  
	   //获得缩放的比例
	   double ratio = 1.0;
	   if(image.getHeight()>250 || image.getWidth()>250)
	   {
	    if(image.getHeight() > image.getWidth())
	    {
	     ratio = 250.0 / image.getHeight();
	    }
	    else
	    {
	     ratio = 250.0 / image.getWidth();
	    }
	   }
	   //计算新的图面宽度和高度
	   int newWidth =(int)(image.getWidth()*ratio);
	   int newHeight =(int)(image.getHeight()*ratio);
	   
	   BufferedImage bfImage= new BufferedImage(newWidth,newHeight,BufferedImage.TYPE_INT_RGB);
	   bfImage.getGraphics().drawImage(image.getScaledInstance(newWidth, newHeight,Image.SCALE_SMOOTH),0,0,null);
	   
	   FileOutputStream os = new FileOutputStream(filePath+File.separator+fileName+"_tmp");
	   JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
	   encoder.encode(bfImage);
	   os.close();
	   System.out.println("创建缩略图成功");
	  }
	  catch(Exception e)
	  {
	   System.out.println("创建缩略图发生异常"+e.getMessage());
	  }
	 }
	

}
