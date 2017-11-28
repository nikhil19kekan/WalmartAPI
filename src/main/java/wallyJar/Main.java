package wallyJar;

import java.util.List;

import org.json.simple.parser.ParseException;

public class Main {

	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		List<Category> categories =ManageCategory.getCategoriesCatalogue();
		int productId=0;//enter productId here
		Product p = ManageProduct.getProduct(productId);
	}

}
