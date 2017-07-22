package resource.load;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ResourceLoad {
	private static Map<String, String> content;
	static{
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("lsm.properties");
		Properties pop = new Properties();
		try {
			pop.load(in);
			content = new HashMap<String,String>();
			Field[] fields = Content.class.getDeclaredFields();
			int len = fields.length;
			for(int i = 0 ; i < len ;i ++){
				String key = fields[i].getName();
				content.put(key, pop.getProperty(key));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static Map<String,String> getContent(){
		return content;
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		ResourceLoad load = new ResourceLoad();
	}
}
