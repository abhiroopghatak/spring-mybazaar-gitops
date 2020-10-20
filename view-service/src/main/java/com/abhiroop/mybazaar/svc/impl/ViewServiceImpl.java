package com.abhiroop.mybazaar.svc.impl;

import java.net.URI;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.abhiroop.mybazaar.pojo.ApiResponse;
import com.abhiroop.mybazaar.pojo.CartRequest;
import com.abhiroop.mybazaar.pojo.JsProductObject;
import com.abhiroop.mybazaar.pojo.OrderRequestDto;
import com.abhiroop.mybazaar.pojo.Product;
import com.abhiroop.mybazaar.pojo.User;
import com.abhiroop.mybazaar.svc.ViewFacadeService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class ViewServiceImpl implements ViewFacadeService {

	@Value("${service.users.url}")
	private String userSvcUrl;

	public String getUserSvcUrl() {
		return userSvcUrl;
	}

	@Value("${service.inventory.url}")
	private String invSvcUrl;

	@Value("${service.cart.url}")
	private String cartSvcUrl;

	@Value("${service.order.url}")
	private String orderSvcUrl;

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private JavaMailSender javaMailSender;

	public String getOrderSvcUrl() {
		return orderSvcUrl;
	}

	public String getInvSvcUrl() {
		return invSvcUrl;
	}

	public String getCartSvcUrl() {
		return cartSvcUrl;
	}

	private UserDetails userdetails = null;

	private static final Logger log = LoggerFactory.getLogger(ViewServiceImpl.class);

	private UserDetails getUserDetail() {
		if (userdetails != null) {
			return this.userdetails;
		} else {
			if (SecurityContextHolder.getContext().getAuthentication() != null
					&& SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null) {
				this.userdetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			}
			return this.userdetails;
		}
	}

	
	@Override
	@HystrixCommand(fallbackMethod = "findAllProducts_Fallback")
	public List<Product> findAllProducts() throws Exception {
		log.info("request for product list recieved");
		List<Product> prodList = null;
			URI urlForProductList = new URI(getInvSvcUrl() + "/products");
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			log.info("Inventory service called at path => " + urlForProductList.getPath() + " @" + timestamp);
			ResponseEntity<Product[]> responseEntity = restTemplate.getForEntity(urlForProductList, Product[].class);
			prodList = responseEntity.getBody() != null ? Arrays.asList(responseEntity.getBody())
					: Collections.emptyList();
			log.info("Inventory service return data in => "
					+ (new Timestamp(System.currentTimeMillis()).getTime() - timestamp.getTime()) + "seconds");
		log.info("findAllProducts service responded");
		return prodList;
	}
	@SuppressWarnings("unused")
	private List<Product> findAllProducts_Fallback() {
		List<Product> uList = Collections.emptyList();
		System.out.println("Inventory Service is down!!! CIRCUIT BREAKER ENABLED!!! fallback route enabled...");
		return uList;
	}

	static boolean isServiceRunning(RestTemplate restTemplate, String uri) {
		if ("OK".equals(restTemplate.getForObject(uri + "/isok", String.class))) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Product findProductById(String id) throws Exception {
		Product p = null;
		if (isServiceRunning(this.restTemplate, getInvSvcUrl())) {
			Map<String, String> params = new HashMap<>();
			params.put("id", id);
			p = restTemplate.getForObject(getInvSvcUrl() + "/product/{id}", Product.class, params);
			log.info("product Details found ." + p);
		} else {
			log.error("Inventory service not connected.");
		}
		return p;
	}

	@Override
	public Product saveProductToInventory(Product product) {

		if (isServiceRunning(this.restTemplate, getInvSvcUrl())) {

			ResponseEntity<ApiResponse> responseEntity = restTemplate.postForEntity(getInvSvcUrl() + "/products/new",
					product, ApiResponse.class);
			ApiResponse response = responseEntity.getBody();
			if (response.getStatus() == 200) {
				product = (Product) response.getResult();
				log.info("Product is saved in to inventory");
			} else if (response.getStatus() == 422) {
				log.error(
						"The entity send to save can not proceed with as conflict with the data in the destination DB.");
			} else {
				log.error("error in entity save . please connect system admin.");
			}
		} else {
			log.error("Inventory service not connected. Can not proceed with operation. Please connect system admin");
		}
		return product;

	}

	@Override
	public List<User> findAllUsers() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Product> findProductsByCategory(String categ) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addToCart(String productid, int units) throws RuntimeException {
		boolean isItemAdded = false;
		Product p = null;
		if (isServiceRunning(this.restTemplate, getInvSvcUrl())
				&& isServiceRunning(this.restTemplate, getCartSvcUrl())) {
			Map<String, String> params = new HashMap<>();
			params.put("id", productid);
			p = restTemplate.getForObject(getInvSvcUrl() + "/product/{id}", Product.class, params);

			if (p != null) {

				CartRequest cr = new CartRequest();
				cr.setP(p);
				cr.setUnits(units);
				cr.setUid(getUserDetail().getUsername());
				ResponseEntity<ApiResponse> responseEntity = restTemplate.postForEntity(getCartSvcUrl() + "/addtocart",
						cr, ApiResponse.class);
				ApiResponse response = responseEntity.getBody();
				if (response.getStatus() == 200 && (boolean) response.getResult()) {
					isItemAdded = true;
					log.info("Product is saved to cart");
				} else {
					log.error(
							"The entity send to save can not proceed with as some exception occurred in cart service");
				}
			} else {
				log.error("Product id , send to add to cart is INVALID.");
			}

		} else {
			log.error("Inventory or cart service is not responding.");
			throw new RuntimeException("Inventory or cart service is not responding.");
		}

		return isItemAdded;
	}

	@Override
	public boolean emptyCart() {

		boolean result = false;

		if (isServiceRunning(this.restTemplate, getCartSvcUrl()) && getUserDetail() != null) {

			ResponseEntity<ApiResponse> responseEntity = restTemplate.postForEntity(getCartSvcUrl() + "/emptycart",
					getUserDetail().getUsername(), ApiResponse.class);

			ApiResponse response = responseEntity.getBody();
			if (response.getStatus() == 200 && "SUCCESS".equals(response.getMessage())) {
				result = true;
			}
		}
		return result;
	}

	@Override
	public List<JsProductObject> loadCart() {
		List<JsProductObject> pList = Collections.emptyList();
		if (isServiceRunning(this.restTemplate, getCartSvcUrl()) && getUserDetail() != null) {
			Map<String, String> params = new HashMap<>();
			params.put("username", getUserDetail().getUsername());
			ResponseEntity<ApiResponse> responseEntity = restTemplate
					.getForEntity(getCartSvcUrl() + "/loadCartForUser/{username}", ApiResponse.class, params);

			ApiResponse response = responseEntity.getBody();

			if (response.getStatus() == 200) {
				ObjectMapper mapper = new ObjectMapper();

				pList = mapper.convertValue(response.getResult(), new TypeReference<List<JsProductObject>>() {
				});
				if (!CollectionUtils.isEmpty(pList)) {
					Product p = null;
					Iterator<JsProductObject> itr = pList.iterator();
					while (itr.hasNext()) {
						JsProductObject jsProductObject = itr.next();
						try {
							p = findProductById(jsProductObject.getProductId());
							jsProductObject.setCreateTime(p.getCreateTime());
							jsProductObject.setProducer(p.getProducer());
							jsProductObject.setProductName(p.getProductName());
							jsProductObject.setProductDescription(p.getProductDescription());
						} catch (Exception e) {
							log.error("Error retrieving prod details of id " + jsProductObject.getProductId()
									+ " , thus ignoring in cart");
							itr.remove();
						}
					}
				}

				log.info("ProductInOrder list responded");
			} else {
				log.error("Cart service is not working at the  moment");
			}
		}
		return pList;
	}

	@Override
	public boolean removeFromCart(JsProductObject jo) {
		boolean isItemRemoved = false;
		if (isServiceRunning(this.restTemplate, getCartSvcUrl())) {
			Product p = null;
			Map<String, String> params = new HashMap<>();
			params.put("id", jo.getProductId());
			p = restTemplate.getForObject(getInvSvcUrl() + "/product/{id}", Product.class, params);

			if (p != null) {

				CartRequest cr = new CartRequest();
				cr.setP(p);
				cr.setUnits(jo.getUnits());
				cr.setUid(getUserDetail().getUsername());
				ResponseEntity<ApiResponse> responseEntity = restTemplate
						.postForEntity(getCartSvcUrl() + "/removefromcart", cr, ApiResponse.class);
				ApiResponse response = responseEntity.getBody();
				if (response.getStatus() == 200 && (boolean) response.getResult()) {
					isItemRemoved = true;
					log.info("Product is removed from cart");
				} else {
					log.error(
							"The entity send to remove can not proceed with as some exception occurred in cart service");
				}
			} else {
				log.error("Product id " + jo.getProductId() + ", send to remove from cart is INVALID.");
			}
		} else {
			log.error("Cart service is not up . Please try later");
		}
		return isItemRemoved;
	}

	@Override
	public boolean doCartCheckout() {
		boolean result = false;
		if (isServiceRunning(this.restTemplate, getCartSvcUrl()) && getUserDetail() != null) {
			String uname = getUserDetail().getUsername();
			ResponseEntity<ApiResponse> responseEntity = restTemplate.postForEntity(getCartSvcUrl() + "/checkoutcart",
					uname, ApiResponse.class);

			ApiResponse response = responseEntity.getBody();
			if (response.getStatus() == 200 && "SUCCESS".equals(response.getMessage())) {
				result = true;
				log.info("Cart Checkout for user " + uname + " is Successful");
			} else {
				log.info("Cart Checkout for user " + uname + " is Failed");
			}
		}
		return result;
	}

	@Override
	public List<OrderRequestDto> listorders() {
		List<OrderRequestDto> orderlist = new ArrayList<OrderRequestDto>();
		if (isServiceRunning(this.restTemplate, getOrderSvcUrl()) && getUserDetail() != null) {
			Map<String, String> params = new HashMap<>();
			params.put("username", getUserDetail().getUsername());
			ResponseEntity<ApiResponse> responseEntity = restTemplate
					.getForEntity(getOrderSvcUrl() + "/getall/{username}", ApiResponse.class, params);

			ApiResponse response = responseEntity.getBody();

			if (response.getStatus() == 200) {
				ObjectMapper mapper = new ObjectMapper();
				orderlist = mapper.convertValue(response.getResult(), new TypeReference<List<OrderRequestDto>>() {
				});
				if (orderlist == null) {
					orderlist = new ArrayList<OrderRequestDto>();
				}
			}
		}

		return orderlist;
	}

	@Override
	public OrderRequestDto getOrderDetailsById(long oid) {
		OrderRequestDto ordto = null;
		if (isServiceRunning(this.restTemplate, getOrderSvcUrl())) {
			Map<String, Long> params = new HashMap<>();
			params.put("oid", oid);
			ApiResponse response = restTemplate.getForObject(getOrderSvcUrl() + "/getOrderDetails/{oid}",
					ApiResponse.class, params);

			ObjectMapper mapper = new ObjectMapper();

			if (response.getStatus() == 200) {
				ordto = mapper.convertValue(response.getResult(), new TypeReference<OrderRequestDto>() {
				});

				log.info("Order Details found ." + ordto.getOid());
			} else if (response.getStatus() == 204) {
				log.error("No Order details found for provided order id " + oid);
			} else {
				log.error("Order service not connected.");
			}
		}
		return ordto;
	}

	@Override
	public boolean sendOrderToemail(String oid) {
		boolean mailSendStatus = false;
		User u = null;
		String username = getUserDetail().getUsername();
		Map<String, String> params = new HashMap<>();
		params.put("uname", username);
		ApiResponse response = restTemplate.getForObject(getUserSvcUrl() + "/getUserByUsername/{uname}",
				ApiResponse.class, params);
		ObjectMapper mapper = new ObjectMapper();

		if (response.getStatus() == 200) {
			u = mapper.convertValue(response.getResult(), new TypeReference<User>() {
			});
			OrderRequestDto ordto = getOrderDetailsById(Long.parseLong(oid));
			String emailMessage = "Your order has been confirmed with us . Please find below details. \n order Id:"
					+ ordto.getOid() + " , order state:  " + ordto.getOrderstate() + ", totalProducts:"
					+ ordto.getProdList().size();
			final SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setTo(u.getEmail());
			mailMessage.setSubject("MyBazaar App ---Order #" + ordto.getOid() + " Confirmation details.");
			mailMessage.setFrom("no-reply@mybazaar.com");
			mailMessage.setText(emailMessage);

			javaMailSender.send(mailMessage);
			log.info("Order Details Sent to email ." + u.getEmail());
			mailSendStatus = true;
		}
		return mailSendStatus;
	}

	@Async
	public void sendEmail(SimpleMailMessage email) {
		javaMailSender.send(email);
	}
}
