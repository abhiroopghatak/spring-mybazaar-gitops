package com.abhiroop.mybazaar.svc.impl;

import java.net.URI;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.abhiroop.mybazaar.pojo.ApiResponse;
import com.abhiroop.mybazaar.pojo.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MyUserService implements UserDetailsService {

	private static final Logger log = LoggerFactory.getLogger(MyUserService.class);

	@Value("${service.users.url}")
	private String userSvcUrl;

	public String getUserSvcUrl() {
		return userSvcUrl;
	}

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private PasswordEncoder p;

	// USER SERVICE REST API Consumption impl

	public List<User> findAllUsers() throws Exception {
		log.info("request for user list recieved");

		List<User> uList = null;
		URI uriForUserService = new URI(getUserSvcUrl() + "/getall");

		ResponseEntity<ApiResponse> responseEntity = restTemplate.getForEntity(uriForUserService, ApiResponse.class);

		ApiResponse response = responseEntity.getBody();

		if (response.getStatus() == 200) {
			uList = (List<User>) response.getResult();
			log.info("User list responded");
		} else {
			log.error("User List service is not working at the  moment");
			uList = Collections.emptyList();
		}

		return uList;
	}

	public void deleteUser(Long id) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User u = null;

		if (ViewServiceImpl.isServiceRunning(this.restTemplate, getUserSvcUrl())) {

			Map<String, String> params = new HashMap<>();
			params.put("uname", username);
			ApiResponse response = restTemplate.getForObject(getUserSvcUrl() + "/getUserByUsername/{uname}",
					ApiResponse.class, params);
			ObjectMapper mapper = new ObjectMapper();

			if (response.getStatus() == 200) {
				u = mapper.convertValue(response.getResult(), new TypeReference<User>() {
				});

				log.info(String.format("At loadUserByUsername of View Svc. User recieved of uname  %s", u.getUsername()));

				org.springframework.security.core.userdetails.User springUser = new org.springframework.security.core.userdetails.User(
						u.getUsername(), u.getPassword(), new ArrayList<>());
				log.info("user loaded =>" + springUser);
				return springUser;
			} else {

				log.error("NO Data found for the id provided.");
			}

		} else {

			log.error("User Service is not Ready to Authenticate...Please connect Admin");
		}

		return null;

		// TODO Auto-generated method stub
	}

	public String activateUser(String token) throws Exception {
		String result = "FAILED";
		if (ViewServiceImpl.isServiceRunning(this.restTemplate, getUserSvcUrl())) {

			ResponseEntity<ApiResponse> responseEntity = restTemplate.postForEntity(getUserSvcUrl() + "/activation",
					token, ApiResponse.class);
			ApiResponse response = responseEntity.getBody();
			if (response.getStatus() == 200) {
				result = "SUCCESS";
				log.info("User has been activated in system .");
			} else if (response.getStatus() == 422) {
				log.error(
						"The entity send to save can not proceed with as conflict with the data in the destination DB.");
				throw new RuntimeException("Conflict Data");
			} else {
				log.error("error in user activation. please connect system admin.");
				throw new RuntimeException("error in user activation . please connect system admin.");
			}
		} else {
			log.error("User service not connected. Can not proceed with operation. Please connect system admin");
			throw new RuntimeException("User service not connected.");
		}

		return result;
	}

	public User registerUser(User user) throws Exception {
		if (user != null && ViewServiceImpl.isServiceRunning(this.restTemplate, getUserSvcUrl())) {

			// encode password using BCryptPasswordEncoder
			user.setPassword(p.encode(user.getPassword()));
			ResponseEntity<ApiResponse> responseEntity = restTemplate.postForEntity(getUserSvcUrl() + "/adduser", user,
					ApiResponse.class);
			ApiResponse response = responseEntity.getBody();
			if (response.getStatus() == 200) {
				user = new ObjectMapper().convertValue(response.getResult(), new TypeReference<User>() {
				});
				log.info("User is saved in to repository");
			} else if (response.getStatus() == 422) {
				log.error(
						"The entity send to save can not proceed with as conflict with the data in the destination DB.");
				throw new RuntimeException("Conflict Data");
			} else {
				log.error("error in entity save . please connect system admin.");
				throw new RuntimeException("error in entity save . please connect system admin.");
			}
		} else {
			log.error("User service not connected. Can not proceed with operation. Please connect system admin");
			throw new RuntimeException("User service not connected.");
		}
		return user;
	}

	public User userDetails(Long id) throws Exception {
		log.info("userDetails request for userid {0}  recieved", id);
		User u = null;

		if (ViewServiceImpl.isServiceRunning(this.restTemplate, getUserSvcUrl())) {

			Map<String, Long> params = new HashMap<>();
			params.put("id", id);
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			log.info("User service called at path => " + getUserSvcUrl() + " @ " + timestamp);

			ApiResponse response = restTemplate.getForObject(getUserSvcUrl() + "/getUser/{id}", ApiResponse.class,
					params);

//			System.out.println(response.toString());
			ObjectMapper mapper = new ObjectMapper();

			log.info("User service return data in => "
					+ (new Timestamp(System.currentTimeMillis()).getTime() - timestamp.getTime()) + " m-seconds");

			if (response.getStatus() == 200) {
				u = mapper.convertValue(response.getResult(), new TypeReference<User>() {
				});
			} else {
				log.error("NO Data found for the id provided.");
			}
		} else {
			log.error("User service not connected.");
		}
		log.info("User Data responded");

		return u;
	}

	public User getUserByUsername(String uname) {

		log.info("userDetails request for uname {0}  recieved", uname);
		User u = null;

		if (ViewServiceImpl.isServiceRunning(this.restTemplate, getUserSvcUrl())) {

			Map<String, String> params = new HashMap<>();
			params.put("uname", uname);
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			log.info("User service called at path => " + getUserSvcUrl() + " @ " + timestamp);

			ApiResponse response = restTemplate.getForObject(getUserSvcUrl() + "/getUserByUsername/{uname}", ApiResponse.class,
					params);

//			System.out.println(response.toString());
			ObjectMapper mapper = new ObjectMapper();

			log.info("User service return data in => "
					+ (new Timestamp(System.currentTimeMillis()).getTime() - timestamp.getTime()) + " m-seconds");

			if (response.getStatus() == 200) {
				u = mapper.convertValue(response.getResult(), new TypeReference<User>() {
				});
			} else {
				log.error("NO Data found for the username=>"+uname);
			}
		} else {
			log.error("User service not connected.");
		}
		log.info("User Data responded");

		return u;
	
	}

}
