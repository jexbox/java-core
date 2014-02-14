import java.util.Properties;

import com.google.gson.JsonObject;
import com.jexbox.JexboxImpl;


public class JexboxTestCase {
	
	public static void testJSON(){
		Properties props = new Properties();
		props.put("appId", "4d409621cc1d481903b778edbc0d72503bc1b3ac");
		props.put("host", "notify.ceco.rushmore.cxm:8086");
		props.put("appVersion", "1.0.1");
		
		RuntimeException re = new RuntimeException("Runtime");
		Exception ex = new Exception("Exception", re);
		JexboxImpl jexbox = new JexboxImpl(props);
		JsonObject jsonO = jexbox.json(ex);
		String json = jsonO.toString();
		System.out.println(json);
		
//		Gson parser = new Gson();
//		HashMap data = (HashMap) parser.fromJson(json, HashMap.class);
//		for (Iterator iterator = data.keySet().iterator(); iterator.hasNext();) {
//			String key = (String) iterator.next();
//			Object value = data.get(key);
//			if(value instanceof ArrayList){
//				System.out.println("[");
//				ArrayList arr = (ArrayList) value;
//				for (int i = 0; i < arr.size(); i++) {
//					Object va = arr.get(i);
//					System.out.println(va+",");
//				}
//				
//				System.out.println("]");
//			}else{
//				System.out.println(key+" | "+value);
//			}
//		}
//		
//		System.out.println(data);
	}
	
	public static void send(){
		Properties props = new Properties();
//		props.put("appId", "4d409621cc1d481903b778edbc0d72503bc1b3ac");
//		props.put("host", "notify.ceco.rushmore.cxm:8086");
		
		props.put("appId", "6dd5f3fc15a0aa5ffafe3d119ae83f560d27eea2");
		props.put("appVersion", "1.0.1");
		
		props.put("background", "false");
		RuntimeException re = new RuntimeException("Runtime");
		Exception ex = new Exception("Exception", re);
		JexboxImpl jexbox = new JexboxImpl(props);
		jexbox.send(ex);
	}
	
	public static void main(String[] args) {
		send();
		System.out.println("sent");
	}
}
