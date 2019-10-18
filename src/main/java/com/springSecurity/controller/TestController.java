package com.springSecurity.controller;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.querydsl.core.BooleanBuilder;
import com.springSecurity.config.MyUserDetails;
import com.springSecurity.constant.Constant;
import com.springSecurity.constant.ResponseMessage;
import com.springSecurity.entity.QUser;
import com.springSecurity.entity.User;
import com.springSecurity.entity.User.Role;
import com.springSecurity.entity.UserLogin;
import com.springSecurity.helper.JsonObjectHelper;
import com.springSecurity.helper.NewUserHelper;
import com.springSecurity.repository.UserLoginRepository;
import com.springSecurity.repository.UserRepository;
import com.springSecurity.service.UserDetails;

@RestController
@RequestMapping("/user")
public class TestController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserLoginRepository userLoginRepository;
	
	@Autowired
	private UserDetails userDetails;
	
	private static final Logger LOGGER = Logger.getLogger(TestController.class.getName());
	
	/**
	 * To login user.
	 * 
	 * @return
	 */
	@GetMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
	public MyUserDetails login(HttpServletResponse httpServletResponse) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		MyUserDetails myUserDetails = null;
		if (authentication != null) {
			myUserDetails = (MyUserDetails) authentication.getDetails();
			if (null == myUserDetails.getToken() || myUserDetails.getToken().equals("")) {
				myUserDetails.setToken(httpServletResponse.getHeader("Set-Cookie"));
			}
		}
		return myUserDetails;
	}
	
	/**
	 * To logout the user.
	 * 
	 * @return
	 */
	@GetMapping("/logout")
	public ResponseEntity<JsonObjectHelper> logout(HttpServletResponse httpServletResponse) {
		HttpStatus status = HttpStatus.OK;
		JsonObjectHelper jsonObj = new JsonObjectHelper(false, ResponseMessage.SUCCESS);
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			authentication.setAuthenticated(false);
			SecurityContextHolder.clearContext();
		} catch (Exception e) {
			jsonObj = new JsonObjectHelper(true, e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(jsonObj, status);
	}

	/**
	 * To register user with encrypted password.
	 * 
	 * @param user
	 * @return
	 */
	@PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<JsonObjectHelper> registerUser(@RequestBody NewUserHelper user) {
		HttpStatus status = HttpStatus.OK;
		JsonObjectHelper jsonObj = new JsonObjectHelper(false, ResponseMessage.SUCCESS);
		try {
			User oldUser = userRepository.findByEmail(user.getEmail());
			if (oldUser == null) {
				if (user.getPassword() != null) {
					// creating new user
					User newUser = new User();
					newUser.setAddress(user.getAddress());
					newUser.setBirthDate(user.getBirthDate());
					newUser.setCity(user.getCity());
					newUser.setCountry(user.getCountry());
					newUser.setEmail(user.getEmail());
					newUser.setFirstName(user.getFirstName());
					newUser.setGender(user.getGender());
					newUser.setLastName(user.getLastName());
					newUser.setMobile(user.getMobile());
					newUser.setRole(user.getRole());
					newUser.setZip(user.getZip());
					// saving new user
					newUser = userRepository.save(newUser);

					// creating user login
					UserLogin userLogin = new UserLogin();
					userLogin.setUser(newUser);
					userLogin.setLevel(user.getLevel());
					// encrypting and setting password
					userLogin.setPassword(userRepository.encryptPassword(user.getPassword()));
					userLoginRepository.save(userLogin);
					LOGGER.log(Level.INFO, "User successfully " + Constant.CREATED + " " + newUser);
				} else {
					jsonObj = new JsonObjectHelper(true, ResponseMessage.USER_PASSWORD_REQUIRED);
				}
			} else {
				jsonObj = new JsonObjectHelper(true, ResponseMessage.USER_ALREDY_EXIST);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, null, e);
			jsonObj = new JsonObjectHelper(true, e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(jsonObj, status);
	}
	
	/**
	 * To get user by id.
	 * 
	 * @param id
	 * @return
	 */
	@PreAuthorize("hasAnyRole('Admin')")
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<JsonObjectHelper> getUserById(@PathVariable("id") Long id) {
		HttpStatus status = HttpStatus.OK;
		JsonObjectHelper object = new JsonObjectHelper(true, ResponseMessage.USER_NOT_FOUND);
		Optional<User> user = userRepository.findById(id);
		if (user.isPresent()) {
			object = new JsonObjectHelper(false, ResponseMessage.SUCCESS, user.get());
		}
		return new ResponseEntity<>(object, status);
	}
	
	/**
	 * To get user by email.
	 * 
	 * @param email
	 * @return
	 */
	@PreAuthorize("hasAnyRole('Admin')")
	@GetMapping(value = "/email/{email}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<JsonObjectHelper> getUserByEmail(@PathVariable("email") String email) {
		HttpStatus status = HttpStatus.OK;
		JsonObjectHelper object = new JsonObjectHelper(true, ResponseMessage.USER_NOT_FOUND);
		User user = userRepository.findByEmail(email);
		if (user != null) {
			object = new JsonObjectHelper(false, ResponseMessage.SUCCESS, user);
		}
		return new ResponseEntity<>(object, status);
	}
	
	/**
	 * To delete user by id
	 * 
	 * @param id
	 * @return
	 */
	@Transactional
	@PreAuthorize("hasRole('Admin')")
	@DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<JsonObjectHelper> deleteUserById(@PathVariable("id") Long id) {
		HttpStatus status = HttpStatus.OK;
		JsonObjectHelper object = new JsonObjectHelper(true, ResponseMessage.USER_NOT_FOUND);
		Optional<User> userOptional = userRepository.findById(id);
		try {
			if (userOptional.isPresent()) {
				User user = userOptional.get();
				UserLogin userLogin = userLoginRepository.findByUser(user);
				userLoginRepository.delete(userLogin);
				userRepository.delete(user);
				object = new JsonObjectHelper(false, ResponseMessage.SUCCESS);
				
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, null, e);
			object = new JsonObjectHelper(true, e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(object, status);
	}
	
	
	/**
	 * To save(update) user .
	 * 
	 * @param user
	 * @return
	 */
	@PreAuthorize("hasRole('Admin')")
	@PutMapping(value = "/save", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<JsonObjectHelper> save(@RequestBody User user) {
		HttpStatus status = HttpStatus.OK;
		JsonObjectHelper jsonObj = new JsonObjectHelper(false, ResponseMessage.SUCCESS);
		try {
			if (user.getId() != null) {
				if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
					if (user.getLastName() != null && !user.getLastName().isEmpty()) {
						if (user.getEmail() != null && !user.getEmail().isEmpty()) {
							if (user.getMobile() != null && !user.getMobile().isEmpty()) {
								QUser qUser = QUser.user;
								// checking email existence.
								if (userRepository
										.count(qUser.email.eq(user.getEmail()).and(qUser.id.ne(user.getId()))) == 0) {
									user = userRepository.save(user);
									LOGGER.log(Level.INFO, "User successfully " + Constant.UPDATED + " : " + user);
								} else {
									jsonObj = new JsonObjectHelper(true, ResponseMessage.USER_ALREDY_EXIST);
								}

							} else {
								jsonObj = new JsonObjectHelper(true, ResponseMessage.USER_MOBILE_REQUIRED);
							}
						} else {
							jsonObj = new JsonObjectHelper(true, ResponseMessage.USER_EMAIL_REQUIRED);
						}
					} else {
						jsonObj = new JsonObjectHelper(true, ResponseMessage.USER_LAST_NAME_REQUIRED);
					}
				} else {
					jsonObj = new JsonObjectHelper(true, ResponseMessage.USER_FIRST_NAME_REQUIRED);
				}

			} else {
				jsonObj = new JsonObjectHelper(true, ResponseMessage.CAN_NOT_UPADTE_USER_ID_NOT_EXIST);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, null, e);
			jsonObj = new JsonObjectHelper(true, e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(jsonObj, status);
	}
	
	/**
	 * To update city of login user.
	 * 
	 * @param NewUserHelper
	 * @return
	 */
	@PutMapping(value = "/update/city", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<JsonObjectHelper> updateDeviceId(@RequestBody NewUserHelper newUserHelper) {
		HttpStatus status = HttpStatus.OK;
		JsonObjectHelper jsonObj = new JsonObjectHelper(false, ResponseMessage.SUCCESS);
		try {
			User user = userDetails.getUserDetails().getUser();
			if (user != null) {
				String cityStr = newUserHelper.getCity();
				if (cityStr != null && !cityStr.isEmpty()) {
					user.setCity(cityStr);
					LOGGER.log(Level.INFO, "City  \"" + cityStr + "\" for user \"" + user.getEmail()
					+ "\" successfully " + Constant.UPDATED);
				}
				userRepository.save(user);
			} else {
				jsonObj = new JsonObjectHelper(true, ResponseMessage.LOGIN_USER_NOT_FOUND);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, null, e);
			jsonObj = new JsonObjectHelper(true, e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(jsonObj, status);
	}
	
	
	@GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<JsonObjectHelper> getUserList(@RequestParam("name") Optional<String> name,
			@RequestParam("role") Optional<Role> role, @RequestParam(defaultValue = "0") int pageNo,
			@RequestParam(defaultValue = "25") int limit) {
		HttpStatus status = HttpStatus.OK;
		JsonObjectHelper jsonObj = new JsonObjectHelper(false, ResponseMessage.SUCCESS);
		QUser qUser = QUser.user;
		BooleanBuilder bb = new BooleanBuilder();
		Page<User> page = null;
		// creating filter for user list.
		if (name.isPresent() && !name.get().isEmpty()) {
			bb.and(qUser.firstName.likeIgnoreCase("%" + name.get() + "%"));
		}
		if (role.isPresent()) {
			bb.and(qUser.role.eq(role.get()));
			// bb.and(qUser.roles.contains(role.get()));
		}

		// fetching user from database with filter and limit.
		page = userRepository.findAll(bb, PageRequest.of(pageNo, limit));

		if (page.hasContent()) {
			jsonObj.putData(page.getContent());
		} else {
			jsonObj = new JsonObjectHelper(true, ResponseMessage.USER_NOT_FOUND);
		}
		return new ResponseEntity<>(jsonObj, status);
	}
	
	@PreAuthorize("hasRole('Admin')")
	@GetMapping(value = "/paginateList", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<JsonObjectHelper> getUserListWithPagenation(@RequestParam("name") Optional<String> name,
			@RequestParam("role") Optional<Role> role, @RequestParam(defaultValue = "0") int pageNo,
			@RequestParam(defaultValue = "25") int limit) {
		HttpStatus status = HttpStatus.OK;
		JsonObjectHelper jsonObj = new JsonObjectHelper(false, ResponseMessage.SUCCESS);
		QUser qUser = QUser.user;
		BooleanBuilder bb = new BooleanBuilder();
		Page<User> page = null;
		// creating filter for user list.
		if (name.isPresent() && !name.get().isEmpty()) {
			bb.and(qUser.firstName.likeIgnoreCase("%" + name.get() + "%")
					.or(qUser.lastName.likeIgnoreCase("%" + name.get() + "%")));
		}
		if (role.isPresent()) {
			bb.and(qUser.role.eq(role.get()));
			// bb.and(qUser.roles.contains(role.get()));
		}

		// fetching user from database with filter and limit.
		page = userRepository.findAll(bb, PageRequest.of(pageNo, limit));

		if (page.hasContent()) {
			jsonObj.putData(page);
		} else {
			jsonObj = new JsonObjectHelper(true, ResponseMessage.USER_NOT_FOUND);
		}
		return new ResponseEntity<>(jsonObj, status);
	}

	@PreAuthorize("hasRole('Admin')")
	@GetMapping(value = "/roles", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<JsonObjectHelper> getUserRoles() {
		HttpStatus status = HttpStatus.OK;
		JsonObjectHelper jsonObj = new JsonObjectHelper(false, ResponseMessage.SUCCESS);
		jsonObj.putData(User.Role.values());
		return new ResponseEntity<>(jsonObj, status);
	}
	
	
}
