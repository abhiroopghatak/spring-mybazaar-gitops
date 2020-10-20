package com.abhiroop.mybazaar.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.abhiroop.mybazaar.pojo.JwtRequest;
import com.abhiroop.mybazaar.pojo.OrderRequestDto;
import com.abhiroop.mybazaar.pojo.Product;
import com.abhiroop.mybazaar.pojo.User;
import com.abhiroop.mybazaar.svc.ViewFacadeService;
import com.abhiroop.mybazaar.util.Mappings;
import com.abhiroop.mybazaar.util.ViewNames;

@RestController
public class ViewController {

	@Autowired
	private ViewFacadeService viewFacadeService;

	private static final Logger log = LoggerFactory.getLogger(ViewController.class);

	@GetMapping(Mappings.HEALTH)
	public String health() {
		return HttpStatus.OK.toString();
	}

	// Landing page
	@RequestMapping({ Mappings.DEFAULT, Mappings.LOGIN })
	public ModelAndView login() {
		JwtRequest authenticationRequest = new JwtRequest();

		ModelAndView mv = new ModelAndView(ViewNames.LOGIN);
		mv.addObject("authenticationRequest", authenticationRequest);
		return mv;
	}
	
	@RequestMapping(Mappings.DISPLAY_CART)
	public ModelAndView displayCart() {
		
		ModelAndView mv = new ModelAndView(ViewNames.DISPLAY_CART);
		mv.addObject("user", "abhiroop");
		return mv;
	}
	

	// sign-up
	@RequestMapping(Mappings.USER_SIGN_UP_FORM)
	public ModelAndView userRegister() {
		User u = new User();
		ModelAndView mv = new ModelAndView(ViewNames.USER_REGISTER);
		mv.addObject("user", u);
		return mv;
	}

	@RequestMapping(Mappings.HOME)
	public ModelAndView landingPage() {
		UserDetails userdetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User u = new User();
		u.setUsername(userdetails.getUsername());
		ModelAndView mv = new ModelAndView(ViewNames.USER_LANDING);
		mv.addObject("loggedInUser", u);
		return mv;
	}

	// once user logout out
	@RequestMapping(Mappings.LOGOUT)
	public String logout() {
		return "redirect:/login";
	}

	// ============== PRODUCTS ADMINISTRATION ===============

	@GetMapping(Mappings.ADD_PRODUCT)
	public ModelAndView add_product() {
		Product product = new Product();
		ModelAndView mv = new ModelAndView(ViewNames.ADD_PRODUCT);
		mv.addObject("product", product);
		return mv;
	}

	@PostMapping(Mappings.PRODUCT_SAVE)
	public void addProduct(@ModelAttribute("product") Product product, HttpServletRequest request,HttpServletResponse response,
			ModelMap modelMap) {
		if (product.getProductDescription().length() > 5000) {
			log.info("ADDING TOO LONG PRODUCT DESCRIPTION");
		}
		MultipartFile productImage = product.getProductImage();
		product.setProductImage(null);

		try {
			byte[] bytes = productImage.getBytes();
			String imageExt = product.getProductId() + ".jpg";
			BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(new File("src/main/resources/static/product_images/" + imageExt)));
			stream.write(bytes);
			product.setProductIcon(imageExt);
			viewFacadeService.saveProductToInventory(product);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			response.sendRedirect("/view-products");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@GetMapping("/view-products")
	public ModelAndView productLists() {

		log.info("/view-products handler called.");
		List<Product> productList;
		try {
			productList = viewFacadeService.findAllProducts();
			log.info("svc responded with prod list");
		} catch (Exception e) {
			log.error(e.getMessage());
			productList = Collections.emptyList();
		}
		ModelAndView mv = new ModelAndView(ViewNames.PRODUCTLIST);
		mv.addObject("productList", productList);
		return mv;
	}

	@GetMapping(Mappings.PRODUCT_DETAIL)
	public ModelAndView productInfo(@RequestParam("id") String id) {

		Product product = null;
		ModelAndView mv = new ModelAndView(ViewNames.PRODUCTDETAILS);
		try {
			product = viewFacadeService.findProductById(id);
			mv.addObject("product", product);
		} catch (Exception e) {

			e.printStackTrace();
			product = new Product();
		}
		return mv;

	}
	
	@GetMapping(Mappings.ORDERS)
	public ModelAndView getOrdersOfLoggedInUser() {
		List<OrderRequestDto> orderListObject = viewFacadeService.listorders();
		ModelAndView mv = new ModelAndView(ViewNames.ORDERLIST);
		mv.addObject("ordlist", orderListObject);
		return mv;
	}
}
