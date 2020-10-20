package com.abhiroop.mybazaar.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abhiroop.mybazaar.dto.ApiResponse;
import com.abhiroop.mybazaar.dto.UserDto;
import com.abhiroop.mybazaar.svc.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	public static final String METHODEXCEPTION = "Exception occurred";
	public static final String INVALID = "invalid";
	public static final String SUCCESS = "success";
	public static final String NOT_FOUND = "Not Found";
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_USER = "ROLE_USER";

	@Autowired
	private UserService userService;

	@GetMapping("/health")
	public String health() {
		String r = "User Svc is " + ((userService != null) ? "Up and Running" : "Not Ready");
		System.out.println(r);
		return r;
	}

	@GetMapping("/isok")
	public String isok() {
		System.out.println("liveness probe called.");
		return "OK";
	}

	@GetMapping("/getall")
	public ApiResponse listUser() {
		log.info(String.format("received request to list user "));
		List<UserDto> ulist = null;
		ApiResponse ar = null;
		try {
			ulist = userService.findAll();
			ar = new ApiResponse(HttpStatus.OK, SUCCESS, ulist);
		} catch (Exception e) {
			e.printStackTrace();
			ar = new ApiResponse(HttpStatus.NO_CONTENT, METHODEXCEPTION, ulist);
		}
		return ar;
	}

	@PostMapping("/adduser")
	public ApiResponse create(@RequestBody UserDto user) {
		log.info(String.format("received request to create user %s", user));
		ApiResponse ar = null;
		try {
			user = userService.signUpUser(user);
			ar = new ApiResponse(HttpStatus.OK, SUCCESS, user);
		} catch (RuntimeException re) {
			ar = new ApiResponse(HttpStatus.CONFLICT, INVALID, user);
			re.printStackTrace();
		}
		return ar;
	}

	@PostMapping("/activation")
	public ApiResponse activation(@RequestBody String usertoken) {
		log.info(String.format("received request to activate user with token %s", usertoken));
		ApiResponse ar = null;
		UserDto user = null;
		try {
			user = userService.confirmUser(usertoken);
			ar = new ApiResponse(HttpStatus.OK, SUCCESS, user);
		} catch (Exception re) {
			ar = new ApiResponse(HttpStatus.CONFLICT, INVALID, user);
			re.printStackTrace();
		}
		return ar;
	}

	@GetMapping(value = "/getUser/{id}")
	public ApiResponse getUser(@PathVariable long id) {
		log.info(String.format("received request to get user id %s", id));
		UserDto userdto = null;
		ApiResponse ar = null;
		try {
			userdto = userService.findOne(id);
			ar = new ApiResponse(HttpStatus.OK, SUCCESS, userdto);
		} catch (RuntimeException re) {
			ar = new ApiResponse(HttpStatus.CONFLICT, INVALID, userdto);
			re.printStackTrace();
		}
		return ar;
	}

	@GetMapping(value = "/getUserByUsername/{uname}")
	public ApiResponse getUser(@PathVariable String uname) {
		log.info(String.format("received request to get user of username %s", uname));
		UserDto userdto = null;
		ApiResponse ar = null;
		try {
			userdto = userService.findOneByUsername(uname);
			if (userdto != null) {
				ar = new ApiResponse(HttpStatus.OK, SUCCESS, userdto);
				log.info(String.format("Userdto recieved of id %s", userdto.getId()));
			} else {
				ar = new ApiResponse(HttpStatus.OK, NOT_FOUND, userdto);
			}
		} catch (RuntimeException re) {
			ar = new ApiResponse(HttpStatus.CONFLICT, INVALID, userdto);
			re.printStackTrace();
		}
		return ar;
	}

	@DeleteMapping(value = "del/{id}")
	public void delete(@PathVariable(value = "id") Long id) {
		log.info(String.format("received request to delete user of id= %s", id));
		userService.delete(id);
	}

}