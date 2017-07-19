package com.zetainteractive.security;

import java.lang.reflect.Method;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;


@Configuration
@EnableCaching
public class EhCacheConfiguration {

	@Primary
    @Bean
    public EhCacheCacheManager ehCacheCacheManager() {
		/*EhCacheCacheManager cacheManager = new EhCacheCacheManager();
		cacheManager.setCacheManager(ehCacheManagerFactoryBean().getObject());
        return cacheManager;*/
		return new EhCacheCacheManager(ehCacheManagerFactoryBean().getObject());
    }


    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
        EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        cacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        cacheManagerFactoryBean.setShared(true);
        return cacheManagerFactoryBean;
    }

    /*
     * As using CustomKeyGenerator.java to get customized key at method call 
     */
   /* @Bean(name="customkey")
    public KeyGenerator keyGenerator() {
      return new KeyGenerator() {
       	@Override
		public Object generate(Object obj, Method method, Object... params) {
			StringBuilder sb = new StringBuilder();
	          sb.append(obj.getClass().getName());
	          sb.append(method.getName());
	          
	          for (Object param : params) {
	            sb.append(param.toString());
	          }
	          return sb.toString();
		}
      };
    }*/
}
