package com.rudra.aks.service;

import java.util.List;

import com.rudra.aks.bo.UserBO;

public interface CacheService {
	UserBO	getUserById(long userId);
	List<UserBO>	listUserWithCriteria(String criteria);
	UserBO getUserCustom(long userId, String userName);
	List<UserBO> listAllUser();
	long saveUser(UserBO userBO);
	UserBO updateUser(UserBO userBO);
}
