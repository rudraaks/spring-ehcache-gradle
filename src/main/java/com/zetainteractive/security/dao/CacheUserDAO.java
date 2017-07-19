package com.zetainteractive.security.dao;

import java.util.List;

import com.zetainteractive.security.bo.UserBO;

public interface CacheUserDAO {
	long	saveUser(UserBO userBO);
	UserBO	getUserById(long userId);
	int 	saveCustomer();
	UserBO 	updateUser(UserBO userBO);
	long 	saveUserWithTemplate(UserBO user);
	UserBO 	getUserCustom(long userId, String userName);
	List<UserBO> listAllUser();
	List<UserBO>	listUserWithCriteria(String criteria);

}
