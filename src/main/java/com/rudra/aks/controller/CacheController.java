package com.rudra.aks.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.rudra.aks.bo.UserBO;
import com.rudra.aks.service.CacheService;

@RestController
@RequestMapping("/cache")
public class CacheController {

	/*
	 * The Logger
	 */
	private static final Logger logger = Logger.getLogger(CacheController.class);
	
	@Autowired
	CacheService	cacheService;
	
	@RequestMapping(value="/get/{userId}", method=RequestMethod.GET)
	public	ResponseEntity<?>	getUser(@PathVariable long userId,@RequestHeader HttpHeaders headers) {
		//logger.info("Begin : " + getClass().getName() + ": getUser()");
		UserBO userBO = null;
		try {
			userBO = cacheService.getUserById(userId);
		} catch (Exception e) {
			logger.error("Exception : " + e);
			return new ResponseEntity<String>("getUser method faild",HttpStatus.BAD_REQUEST);
		}
		//logger.info("End : " + getClass().getName() + ": getUser()");
		return new ResponseEntity<UserBO>(userBO,HttpStatus.OK);
	}
	
	@RequestMapping(value="/getCustom/{userId}/{userName}", method=RequestMethod.GET)
	public	ResponseEntity<?>	getUserCustom(@PathVariable long userId, @PathVariable String userName, @RequestHeader HttpHeaders headers) {
		//logger.info("Begin : " + getClass().getName() + ": getUserCustom()");
		UserBO userBO = null;
		try {
			userBO = cacheService.getUserCustom(userId, userName);
		} catch (Exception e) {
			logger.error("Exception : " + e);
			return new ResponseEntity<String>("getUser method faild",HttpStatus.BAD_REQUEST);
		}
		//logger.info("End : " + getClass().getName() + ": getUserCustom()");
		return new ResponseEntity<UserBO>(userBO,HttpStatus.OK);
	}
	
	@RequestMapping ( value = "/listwhere/{criteria}", method = RequestMethod.GET)
	public	ResponseEntity<?>	listUserWithCriteria(@PathVariable String criteria) {
		List<UserBO> userList = null;
		try {
			userList = cacheService.listUserWithCriteria(criteria);
		} catch (Exception e) {
			logger.error("Exception while listUserCriteria : " + e);
			return new ResponseEntity<String>("Exception occured: " + criteria, HttpStatus.BAD_REQUEST);
		}
		logger.info("No of Users in this criteria: " + userList.size());
		return new ResponseEntity<>(userList.size(), HttpStatus.OK);
	}
	
	@RequestMapping (value = "/listAll", method = RequestMethod.GET)
	public	ResponseEntity<?>	listAllUser() {
		List<UserBO> userList = null;
		try {
			userList = cacheService.listAllUser();
		} catch (Exception e) {
			logger.error("Exception while listUserCriteria : " + e);
			return new ResponseEntity<String>("Exception occured: ", HttpStatus.BAD_REQUEST);
		}
		logger.info("No of Users in this criteria: " + userList.size());
		return new ResponseEntity<>(userList.size(), HttpStatus.OK);
	}

	@RequestMapping (value= "/saveuser", method = RequestMethod.POST)
	public ResponseEntity<?>	saveUser(@RequestBody UserBO userBO, @RequestHeader HttpHeaders headers) {
		long userid = 0;
		try {
			userid = cacheService.saveUser(userBO);
		} catch (Exception e) {
			logger.error("Exception while saving user: " +e);
			return new ResponseEntity<String>("not saved", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(userid, HttpStatus.OK);
	}
	
	@RequestMapping (value = "/update", method = RequestMethod.POST)
	public ResponseEntity<?>	updateUse(@RequestBody UserBO userBO, @RequestHeader HttpHeaders headers) {
		UserBO updatedUserBO = null;
		try {
			updatedUserBO = cacheService.updateUser(userBO);
			if ( updatedUserBO == null)
				throw new Exception("not updated"+ userBO);
		} catch (Exception ex) {
			logger.error("Exception while updating user : " + ex);
			return new ResponseEntity<>("Exception while updating user : ", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(updatedUserBO, HttpStatus.OK);
	}
}
