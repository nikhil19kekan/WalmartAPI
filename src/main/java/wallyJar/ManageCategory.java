package wallyJar;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/*
 * this method has only one public method exposed
 * getCategories -> fetchJsonData -> cleanAndFetch -> fetchCategories
 */
public class ManageCategory {
	static String data=null;
	static List<Category> categories=null;
	public List<Category> getCategories() {
		fetchJSONData();
		return categories;
	}
	/*
	 * this method traverse the category tree recursively to traverse through all the categories that we have.
	 */
	private static void fetchCategories(JSONArray arr){
		try {
			for(int i=0;i<arr.size();i++) {
				Object obj = arr.get(i);
				Category c = new Category();
				JSONObject jObj = (JSONObject)obj;
				c.setId((String)jObj.get("id"));
				c.setName((String)jObj.get("name"));
				c.setPath(cleanPath((String)jObj.get("path")));
				categories.add(c);
				if(jObj.get("children") != null) {
					fetchCategories((JSONArray)jObj.get("children"));
				}
			}
		}catch(Exception e) {
			System.out.println("Exception occurred while fetchCategories");
			System.out.println(e.getMessage());
		}
	}
	/*
	 * This method removes un-necessary slash and hyphens 
	 * and puts -> to indicate sub category in path
	 */
	private static String cleanPath(String path){
		StringBuilder strBuilder = new StringBuilder();
		try {
			for(int i=0;i<path.length();i++) {
				if(path.charAt(i) == '/') {
					strBuilder.append("->");
				}else {
					if(((i+1)<path.length()) && path.charAt(i+1) == '/' && path.charAt(i) == '\\') {
						strBuilder.append("->");
						i++;
					}else {
						strBuilder.append(path.charAt(i));
					}
				}
			}
		}catch(Exception e) {
			System.out.println("exception occurred at cleanPath");
			System.out.println(e.getMessage());
		}
		return strBuilder.toString();
	}
	/*
	 * returns value part for key "categories"
	 * this part consists array of categories along with subcategories nested within each category.
	 */
	private static void cleanAndFetch(){
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(data);
			JSONObject jObject = (JSONObject)obj;
			JSONArray jArray =  (JSONArray) jObject.get("categories");
			fetchCategories(jArray);
		}catch(Exception e) {
			System.out.println("exception occurred while cleanAndFetch");
			System.out.println(e.getMessage());
		}
	}
	
	public void printALL() {
		for(int i=0;i<categories.size();i++) {
			System.out.println("ID\t"+categories.get(i).getId() + "\tNAME\t" +categories.get(i).getName() + "\tPATH\t"+categories.get(i).getPath());
		}
	}
	/*
	 * This Method fetches JSON string from walmart TAXONOMY API into static data variable 
	 * Then it takes out categories from json string calling cleanAndFetch method.
	 */
	private static void fetchJSONData() {
		try {
			URL walmartUrl = new URL("http://api.walmartlabs.com/v1/taxonomy?apiKey=mbqtt78en6jgfpzmuyj6ab5s&format=json"); 
			HttpURLConnection con = (HttpURLConnection)walmartUrl.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Accept", "application/json");
			if(con.getResponseCode() == 200) {
				InputStream is = con.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader buff = new BufferedReader(isr);
				String str=null;
				StringBuilder stringBuilder = new StringBuilder();
				while((str=buff.readLine()) != null) {
					stringBuilder.append(str);
				}
				data=stringBuilder.toString();
			}else {
				throw new RuntimeException("Failed to connect to walmart REST service"+con.getResponseCode()+con.getResponseMessage());
			}
			cleanAndFetch();
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static List<Category> getCategoriesCatalogue(){
		fetchJSONData();
		return categories;
	}
}
