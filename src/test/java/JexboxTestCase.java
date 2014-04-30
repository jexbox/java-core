import java.util.Properties;

import com.jexbox.connector.JexboxConnectorImpl;


public class JexboxTestCase {
	
	public static void testJSON(){
		Properties props = new Properties();

		props.put("appId", "296122b013db574ae2ef2368e1a76bdc1afd96ed");
		props.put("host", "localhost:8086/api/notify");
		
//		props.put("appId", "36bf2f90cfaa8b6542f79a8ef39f7a875b667d8d");
//		props.put("host", "notify.ceco.rushmore.cxm:8086");
		props.put("appVersion", "1.0.1");
		
		RuntimeException re = new RuntimeException("Runtime");
		Exception ex = new Exception("Exception", re);
		JexboxConnectorImpl jexbox = new JexboxConnectorImpl(props);
		
		jexbox.send(ex);
		
//		JsonObject jsonO = jexbox.json(ex);
//		String json = jsonO.toString();
//		System.out.println(json);
		
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
//		props.put("proxyHost", "94.205.181.212");
//		props.put("proxyPort", new Integer(80));
		props.put("useSystemProxy", new Boolean(true));
		
		props.put("background", "false");
		RuntimeException re = new RuntimeException("Runtime");
		Exception ex = new Exception("Exception", re);
		JexboxConnectorImpl jexbox = new JexboxConnectorImpl(props);
		jexbox.send(ex);
	}
	
	public static void main(String[] args) {
		testJSON();
		System.out.println("sent");
	}
}
