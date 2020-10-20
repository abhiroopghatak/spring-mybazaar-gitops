package com.abhiroop.mybazaar.util;

public final class Mappings {
    //Constants
    public static final String DEFAULT = "/";
    
    public static final String REDIRECT = "redirect:/home";
    public static final String HOME = "/home";
    public static final String LOGOUT = "/logout";
    
    //Product screen REST MAPPING
    
    public static final String ADD_PRODUCT = "/product";
    public static final String PRODUCT_SAVE = "/product/save";
    public static final String PRODUCT_DETAIL = "/productInfo";
	public static final String HEALTH = "/health";
	public static final String PROD_LIST = "/prodlist";
	
	
	public static final String ADD_TO_CART = "/addToCart";
	public static final String DISPLAY_CART = "/cartDetails";
	
	//USER SCREEN REST MAPPING
	public static final String VIEW_USERS = "/view-users";
	public static final String USER_DETAIL = "/userInfo";
	public static final String USER_DETAIL_BY_NAME = "/user-profile";
	public static final String LOGIN = "/login";
	public static final String USER_SIGN_UP = "/user/register";
	public static final String USER_SIGN_UP_FORM = "/register-form";
	public static final String USER_ACTIVATION = "/user/activation";

	
	//CART SCREEN REST MAPPING
	public static final String LOAD_CART = "/loadcart";
	public static final String DO_CART_EMPTY = "/removeallfromcart";
	public static final String REMOVE_FROM_CART = "/removethisfromcart";

	public static final String DO_CART_CHECKOUT = "/docheckout";

	
	//Order REST MAPPING
	public static final String ORDERS = "/orders";
	public static final String LOAD_ORDER_BY_ID = "/loadOrderById";
	public static final String ORDER_TO_EMAIL = "/sendOrderToemail";
	

    private Mappings() {}

}
