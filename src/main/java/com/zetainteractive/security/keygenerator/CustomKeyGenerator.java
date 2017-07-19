package com.zetainteractive.security.keygenerator;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

@Component (value = "customkey")
public class CustomKeyGenerator implements KeyGenerator {

	/*
	 * Logger 
	 */
	private static final Logger logger = Logger.getLogger(CustomKeyGenerator.class);
	
	@Override
	public Object generate(Object target, Method method, Object... params) {
		logger.info("Generating Custome Key ... ");
		String key = null;
		String methodName = method.getName();
		StringBuilder sb = new StringBuilder();
        if ( "getUserById".equals(methodName) || "updateUser".equals(methodName))
        	key = "key_userid";
        else if ("listAllUser".equals(methodName)) 
			key = "key_list_all";
        else if ( "saveUser".equals(methodName))
        	key = "unique_key_save";
        else {
        	sb.append(method.getName());
	        for (Object param : params) 
	        	key = sb.append(param.toString()).toString();
	    }
        logger.info("Custom Key Generatored : " + key);
        return key;
	}

}
