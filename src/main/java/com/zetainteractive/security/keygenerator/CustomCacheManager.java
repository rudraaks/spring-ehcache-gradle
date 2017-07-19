package com.zetainteractive.security.keygenerator;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import net.sf.ehcache.CacheManager;


public class CustomCacheManager extends CacheManager{
	public CustomCacheManager() {
		super();
	}
	
	
/*	public CacheManager cacheManager() {
		return new CacheManager(CustomCacheManager.class.getClassLoader().getResourceAsStream("ehcache.xml"));
		
	}
	
	@Bean
	public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
        EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        cacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        cacheManagerFactoryBean.setShared(true);
        return cacheManagerFactoryBean;
    }*/
}
