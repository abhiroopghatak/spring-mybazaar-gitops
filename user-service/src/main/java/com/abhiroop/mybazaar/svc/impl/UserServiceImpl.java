package com.abhiroop.mybazaar.svc.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abhiroop.mybazaar.dto.UserDto;
import com.abhiroop.mybazaar.pojo.ConfirmationToken;
import com.abhiroop.mybazaar.pojo.Role;
import com.abhiroop.mybazaar.pojo.RoleType;
import com.abhiroop.mybazaar.pojo.User;
import com.abhiroop.mybazaar.repo.RoleDao;
import com.abhiroop.mybazaar.repo.UserDao;
import com.abhiroop.mybazaar.svc.UserService;

import lombok.AllArgsConstructor;

@Transactional
@Service(value = "userService")
@AllArgsConstructor
public class UserServiceImpl implements UserService {

	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserDao userDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private ConfirmationTokenService confirmationTokenService;

	@Autowired
	private JavaMailSender javaMailSender;

	public User loadUserByUsername(String uname) throws RuntimeException {
		User user = userDao.findByUsername(uname);
		if (user == null) {
			log.error("Invalid username or password.");
			throw new RuntimeException("Invalid username or password.");
		}
		List<Role> grantedAuthorities = getAuthorities(user);
		user.setRoles(grantedAuthorities);

		return user;
	}

	private List<Role> getAuthorities(User user) {
		List<Role> roleByUserId = roleDao.findByUserID(user.getId());
		return roleByUserId;
	}

	public List<UserDto> findAll() {
		List<UserDto> users = new ArrayList<>();
		userDao.findAll().iterator().forEachRemaining(user -> users.add(user.toUserDto()));

		return users;
	}

	UserDto convertBean(User u  ,List<Role> roleList){
		
		UserDto udto = new UserDto();
		udto.setId(u.getId());
		udto.setEmail(u.getEmail());
		udto.setFirstName(u.getFirstName());
		udto.setLastName(u.getLastName());
		udto.setUsername(u.getUsername());
		udto.setPassword(u.getPassword());
		udto.setRole(new ArrayList<String>());
		for (Role x : roleList) {

			udto.getRole().add(x.getName().name());
		}

		return udto;
		
	}
	
	@Override
	public void delete(long id) {
		userDao.deleteById(id);
	}

	@Override
	public UserDto save(UserDto userDto, boolean isUpdate) throws RuntimeException {
		User userWithDuplicateUsername = userDao.findByUsername(userDto.getUsername());
		if (!isUpdate) {
			if (userWithDuplicateUsername != null && userDto.getId() != userWithDuplicateUsername.getId()) {
				log.error(String.format("Duplicate username ", userDto.getUsername()));
				throw new RuntimeException("Duplicate username.");
			}
			User userWithDuplicateEmail = userDao.findByEmail(userDto.getEmail());
			if (userWithDuplicateEmail != null && userDto.getId() != userWithDuplicateEmail.getId()) {
				log.error(String.format("Duplicate email %", userDto.getEmail()));
				throw new RuntimeException("Duplicate email.");
			}
		}
		User user = new User();
		user.setId(userDto.getId());
		user.setEmail(userDto.getEmail());
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setUsername(userDto.getUsername());
		user.setPassword(userDto.getPassword());
		user.setActive(userDto.getEnabled());
		
	
		user = userDao.saveAndFlush(user);
		for (String x : userDto.getRole()) {

			Role r = new Role();
			r.setName(RoleType.valueOf(x));
			r.setModifiedOn(new Date());
			r.setDescription(x);
			r.setUserid(user.getId());
			roleDao.save(r);
		}
		userDto.setId(user.getId());
		return userDto;
	}

	
	@Override
	public UserDto signUpUser(UserDto user) {

		if (user.getRole() == null) {
			user.setRole(new ArrayList<String>());
		}
		user.getRole().add("USER");
		final UserDto createdUser = save(user, false);

		final ConfirmationToken confirmationToken = new ConfirmationToken(user);

		confirmationTokenService.saveConfirmationToken(confirmationToken);

		sendConfirmationMail(createdUser.getEmail(),
				"Thank you for registering. Please use this token to activate your account.Token="
						+ confirmationToken.getConfirmationToken());

		return createdUser;
	}

	@Override
	public UserDto confirmUser(String oneTimeToken) throws Exception {

		ConfirmationToken confirmationToken = confirmationTokenService.getConfirmationTokenObjectByToken(oneTimeToken);
		UserDto userObject = findOne(confirmationToken.getUserid());
		if (userObject == null) {
			log.error("Invalid token. Corresponding user not found in system.");
			throw new RuntimeException("Invalid token");
		}
		userObject.setEnabled("TRUE");
		confirmationTokenService.deleteConfirmationToken(confirmationToken.getId());
		int rowsUpdate= userDao.activateUser(userObject.getUsername());

		sendConfirmationMail(userObject.getEmail(),
				" Congrasulations , your account has been registered in the system");
		return userObject;
	}

	void sendConfirmationMail(String userMail, String message) {

		final SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(userMail);
		mailMessage.setSubject("MyBazaar App ---");
		mailMessage.setFrom("no-reply@mybazaar.com");
		mailMessage.setText(message);

		sendEmail(mailMessage);
	}

	@Async
	public void sendEmail(SimpleMailMessage email) {
		javaMailSender.send(email);
	}

	@Override
	public UserDto findOne(long id) {
		List<Role> roleList = roleDao.findByUserID(id);
		User u = userDao.findById(id).get();
		
		return convertBean(u,roleList);
	}

	@Override
	public UserDto findOneByUsername(String username) {
		User u = userDao.findByUsername(username);
		UserDto udto=null;
		if(u !=null ) {
			List<Role> roleList = roleDao.findByUserID(u.getId());
			
			udto = convertBean(u, roleList);
		}
		log.info("findOneByUsername at userserviceimpl responding udto= "+ udto);
		return udto;
	}

	@Override
	public UserDto findOneByEmail(String email) {
		User u = userDao.findByEmail(email);
		UserDto udto=null;
		if(u !=null && u.getId() >0) {
			List<Role> roleList = roleDao.findByUserID(u.getId());
			
			udto = convertBean(u, roleList);
		}
		return null;
	}
}