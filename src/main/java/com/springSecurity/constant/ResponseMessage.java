package com.springSecurity.constant;

public class ResponseMessage {
	private ResponseMessage() {
	}

	//common message
	public static final String SUCCESS = "Success";
	public static final String LOGIN_USER_NOT_FOUND = "Login user not found";
	public static final String OWNER_NOT_FOUND = "owner not found";
	

	//user API's message
	public static final String USER_ALREDY_EXIST = "User already exist with this email";
	public static final String USER_NOT_FOUND = "User not found";
	public static final String CAN_NOT_UPADTE_USER_ID_NOT_EXIST = "Can't update user, user id doesn't exist";
	public static final String USER_PASSWORD_REQUIRED = "User password is required";
	public static final String USER_FIRST_NAME_REQUIRED = "User first name is required";
	public static final String USER_LAST_NAME_REQUIRED = "User last name is required";
	public static final String USER_MOBILE_REQUIRED = "User mobile no is required";
	public static final String USER_EMAIL_REQUIRED = "User email is required";
	
	//country API's message
	public static final String COUNTRY_NOT_FOUND = "Counrty not found";

}
