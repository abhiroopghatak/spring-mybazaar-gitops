package com.abhiroop.mybazaar.svc;

import java.util.List;

import com.abhiroop.mybazaar.pojo.JsProductObject;
import com.abhiroop.mybazaar.pojo.OrderRequestDto;
import com.abhiroop.mybazaar.pojo.Product;
import com.abhiroop.mybazaar.pojo.User;

public interface ViewFacadeService {

	List<Product> findAllProducts() throws Exception;
	
	List<Product> findProductsByCategory(String categ) throws Exception;

	Product findProductById(String id) throws Exception;

	List<User> findAllUsers() throws Exception;

	Product saveProductToInventory(Product product);

	boolean addToCart(String productId, int units) ;
	
	boolean removeFromCart(JsProductObject jo) ;

	boolean emptyCart();
	
	List<JsProductObject> loadCart();

	boolean doCartCheckout();

	List<OrderRequestDto> listorders();

	OrderRequestDto getOrderDetailsById(long oid);

	boolean sendOrderToemail(String oid);

	
}
