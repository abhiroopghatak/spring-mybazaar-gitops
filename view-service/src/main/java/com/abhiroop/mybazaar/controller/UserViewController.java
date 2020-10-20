package com.abhiroop.mybazaar.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.abhiroop.mybazaar.pojo.ApiResponse;
import com.abhiroop.mybazaar.pojo.User;
import com.abhiroop.mybazaar.svc.ViewFacadeService;
import com.abhiroop.mybazaar.svc.impl.MyUserService;
import com.abhiroop.mybazaar.util.Mappings;
import com.abhiroop.mybazaar.util.ViewNames;

@RestController
public class UserViewController {
	@Autowired
	private ViewFacadeService viewFacadeService;

	@Autowired
	private MyUserService myUserService;

	private static final Logger log = LoggerFactory.getLogger(UserViewController.class);

//	  ============== USER ADMINISTRATION ===============

	@PostMapping(Mappings.USER_SIGN_UP)
	public ModelAndView register(User user) {
		log.info(Mappings.USER_SIGN_UP + " handler is called .");
		ModelAndView mv = new ModelAndView(ViewNames.USER_REGISTER);

		try {
			user = myUserService.registerUser(user);
			log.info(" user registration is successful.");
			mv.addObject("statusmessage",
					"User Has been registered , please check your email and use token to activate the user.");
		} catch (Exception e) {
			log.info("User registration is failed.");
			e.printStackTrace();
			mv.addObject("statusmessage",
					"There is some exception occurred in backend . Please try later or connect Admin!!!");
		}
		return mv;
	}

	@PostMapping(Mappings.USER_ACTIVATION)
	public ApiResponse activeUserByEmailToken(String token) {
		String result = "FAIL";
		ApiResponse ar = null;
//		ModelAndView mv = new ModelAndView(ViewNames.USER_REGISTER);

		try {
			result = myUserService.activateUser(token);

			if ("SUCCESS".equals(result)) {
				log.info(" user activation is successful.");
//				mv.addObject("statusmessage","Your account has been activated in system . Please do login to proceed.");
				ar = new ApiResponse(HttpStatus.OK, result, result);
			} else {
				log.info(" user activation is Not successful.");
				ar = new ApiResponse(HttpStatus.BAD_REQUEST, result, result);
//				mv.addObject("errormessage", "User has not been activated . Please recheck token.");
			}
		} catch (Exception e) {
			log.info("User registration is failed.");
			e.printStackTrace();
			ar = new ApiResponse(HttpStatus.FORBIDDEN, result, result);
		}

		return ar;
	}

	@GetMapping(Mappings.VIEW_USERS)
	public ModelAndView userLists(Model model) {
		List<User> users = Collections.emptyList();
		ModelAndView mv = new ModelAndView(ViewNames.ALL_USERS);
		try {
			users = myUserService.findAllUsers();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mv.addObject("users", users);
		log.info("/view-user handler is called and user list responded");
		return mv;
	}

	@RequestMapping("/deleteUser")
	public void userDelete(@RequestParam("id") Long id , HttpServletResponse response) {
		try {
			myUserService.deleteUser(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			response.sendRedirect(Mappings.VIEW_USERS);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping(Mappings.USER_DETAIL)
	public ModelAndView userInfo(@RequestParam("id") Long id) {
		User u = null;
		ModelAndView mv = new ModelAndView(ViewNames.UserDETAILS);
		try {
			u = myUserService.userDetails(id);
			mv.addObject("users", u);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mv;
	}
	
	@RequestMapping(Mappings.USER_DETAIL_BY_NAME)
	public ModelAndView loadUserByUserName(@RequestParam("username") String username) {
		User u = null;
		ModelAndView mv = new ModelAndView(ViewNames.UserDETAILS);
		try {
			u = myUserService.getUserByUsername(username);
			mv.addObject("users", u);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mv;
	}
	
}
