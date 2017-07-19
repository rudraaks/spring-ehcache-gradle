package com.zetainteractive.security.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zetainteractive.security.bo.UserBO;
import com.zetainteractive.security.controller.CacheController;
import com.zetainteractive.security.rowmapper.UserRowMapper;

@Repository
//@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS) 

public class CacheUserDAOImpl implements CacheUserDAO {

	/** The logger. */
	private static final Logger logger = Logger.getLogger(CacheUserDAOImpl.class);
	
	SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	@Autowired
	@Qualifier("jdbcTemplate")
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	@Qualifier("txTemplate")
	TransactionTemplate	transactionTemplate;
	
	
	@Override
	public long saveUserWithTemplate(UserBO user) {
		logger.info("Begin :" + getClass().getName() + " :saveUser()");
		
		/*
		 * using transactionTemplet with overriding doInTransaction method 
		 * 
		 * for query which doesn't return anything use below Callback Interface
		 * 		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
		 *         @Override
		 *         protected void doInTransactionWithoutResult(TransactionStatus	transactionStatus) {
		 */
		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		return transactionTemplate.execute(new TransactionCallback<Long>() {
			public Long doInTransaction(TransactionStatus transactionStatus) {		
			
			try {
				String insertQuery = "INSERT INTO SEC_USER_TEST (USERNAME,FIRSTNAME,MIDDLENAME,LASTNAME,EMAILID,PASSWORD,"
					+ "TYPE,STATUS,CUSTOMERID,TIMEZONE,LASTLOGIN,USERINFO,"
					+ "USERNOTIFICATION,MFAUSERIMAGE,MFAUSEROTP,MFAUSERQUESTIONNAIRE," 
					+ "CREATEDBY,UPDATEDBY,CREATEDATE,UPDATEDATE)"
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,UTC_TIMESTAMP,UTC_TIMESTAMP)";
				ObjectMapper mapper = new ObjectMapper();
				mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
				KeyHolder keyHolder = new GeneratedKeyHolder();
					//PBEncryptionUtil encryptionUtil = new PBEncryptionUtil();
		            String password = user.getPassword(); //encryptionUtil.transform("pack",user.getPassword());
		            
		          //adding current password to previously used passwords
		    		if(user.getUserInfo()!=null && password!=null && password.trim().length()>0){
		    			//logger.info("Setting current password for new user into used passwords");
		    			user.getUserInfo().setOldpasswords("");
		    		}
		    		String userName = "testaks";
		            
					jdbcTemplate.update(new PreparedStatementCreator()
					{
						@Override
						public PreparedStatement createPreparedStatement(Connection con) throws SQLException
						{
							PreparedStatement pstmt = con.prepareStatement(insertQuery,Statement.RETURN_GENERATED_KEYS);
							String userInfo = null;
							String userNotification = null;
							String MFAUserImage = null;
							String MFAUserOTP = null;
							String MFAUserQuestionnaire = null;
							if(user.getCreatedBy() == null)
								user.setCreatedBy("test_aks");
							if(user.getUpdatedBy() == null)
								user.setUpdatedBy("test_aks");
							
							try{
								if(user.getUserInfo() != null)
									userInfo = mapper.writeValueAsString(user.getUserInfo());
								
								if(user.getUserNotification() != null)
									userNotification =mapper.writeValueAsString(user.getUserNotification());
								
								if(user.getMfaUserImage() != null)
									MFAUserImage = mapper.writeValueAsString(user.getMfaUserImage());
								if(user.getMfaUserOTP() != null)
									MFAUserOTP = mapper.writeValueAsString(user.getMfaUserOTP());
								if(user.getMfaUserQuestionnaire() != null)
									MFAUserQuestionnaire = mapper.writeValueAsString(user.getMfaUserQuestionnaire());
								
							} catch (JsonProcessingException e) {
								logger.error("Failed to parse JSON object :: " + e, e);
							}
							
							pstmt.setString(1, user.getUserName());
							pstmt.setString(2,user.getFirstName());
							pstmt.setString(3,user.getMiddleName());
							pstmt.setString(4,user.getLastName());
							pstmt.setString(5, user.getEmailID());
							pstmt.setString(6, password);
							pstmt.setString(7, String.valueOf(user.getType()));
							pstmt.setString(8, String.valueOf(user.getStatus()));
							pstmt.setLong(9, user.getCustomerID());
							pstmt.setString(10, user.getTimeZone());
							pstmt.setTimestamp(11, null);
							pstmt.setString(12, userInfo);
							pstmt.setString(13, userNotification);
							pstmt.setString(14, MFAUserImage);
							pstmt.setString(15, MFAUserOTP);
							pstmt.setString(16, MFAUserQuestionnaire);
							pstmt.setString(17, userName);
							pstmt.setString(18, userName);
							return pstmt;
						}
						
	              },keyHolder);
					
					user.setUserID(keyHolder.getKey().intValue());
					logger.info("End :" + getClass().getName() + " :saveUser()");
					/*if(user.getUserID()>0)
						throw new Exception("temp ex");*/
					return keyHolder.getKey()!=null?keyHolder.getKey().longValue():0L;
				} 
				catch (Exception ex) {
					String exceptionMsg = "Exception in saveUser() : ";
					logger.error("Exception in saveUser()::", ex);
					if(ex.getCause() instanceof SQLIntegrityConstraintViolationException)
						exceptionMsg = "Duplicate entry for user name : " + user.getUserName();
					throw new SecurityException(exceptionMsg + ex.getCause(), ex);
				}
				
				}		
		});						
		
	}
	
	@Override
	public long saveUser(UserBO user) {
		logger.info("Begin :" + getClass().getName() + " :saveUser()");
		
		try {
			String insertQuery = "INSERT INTO SEC_USER_TEST (USERNAME,FIRSTNAME,MIDDLENAME,LASTNAME,EMAILID,PASSWORD,"
				+ "TYPE,STATUS,CUSTOMERID,TIMEZONE,LASTLOGIN,USERINFO,"
				+ "USERNOTIFICATION,MFAUSERIMAGE,MFAUSEROTP,MFAUSERQUESTIONNAIRE," 
				+ "CREATEDBY,UPDATEDBY,CREATEDATE,UPDATEDATE)"
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,UTC_TIMESTAMP,UTC_TIMESTAMP)";
			ObjectMapper mapper = new ObjectMapper();
			mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
			KeyHolder keyHolder = new GeneratedKeyHolder();
				//PBEncryptionUtil encryptionUtil = new PBEncryptionUtil();
	            String password = user.getPassword(); //encryptionUtil.transform("pack",user.getPassword());
	            
	          //adding current password to previously used passwords
	    		if(user.getUserInfo()!=null && password!=null && password.trim().length()>0){
	    			//logger.info("Setting current password for new user into used passwords");
	    			user.getUserInfo().setOldpasswords("");
	    		}
	    		String userName = "testaks";
	            
				jdbcTemplate.update(new PreparedStatementCreator()
				{
					@Override
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException
					{
						PreparedStatement pstmt = con.prepareStatement(insertQuery,Statement.RETURN_GENERATED_KEYS);
						String userInfo = null;
						String userNotification = null;
						String MFAUserImage = null;
						String MFAUserOTP = null;
						String MFAUserQuestionnaire = null;
						if(user.getCreatedBy() == null)
							user.setCreatedBy("test_aks");
						if(user.getUpdatedBy() == null)
							user.setUpdatedBy("test_aks");
						
						try{
							if(user.getUserInfo() != null)
								userInfo = mapper.writeValueAsString(user.getUserInfo());
							
							if(user.getUserNotification() != null)
								userNotification =mapper.writeValueAsString(user.getUserNotification());
							
							if(user.getMfaUserImage() != null)
								MFAUserImage = mapper.writeValueAsString(user.getMfaUserImage());
							if(user.getMfaUserOTP() != null)
								MFAUserOTP = mapper.writeValueAsString(user.getMfaUserOTP());
							if(user.getMfaUserQuestionnaire() != null)
								MFAUserQuestionnaire = mapper.writeValueAsString(user.getMfaUserQuestionnaire());
							
						} catch (JsonProcessingException e) {
							logger.error("Failed to parse JSON object :: " + e, e);
						}
						
						pstmt.setString(1, user.getUserName());
						pstmt.setString(2,user.getFirstName());
						pstmt.setString(3,user.getMiddleName());
						pstmt.setString(4,user.getLastName());
						pstmt.setString(5, user.getEmailID());
						pstmt.setString(6, password);
						//pstmt.setLong(7, user.getDepartmentID());
						pstmt.setString(7, String.valueOf(user.getType()));
						pstmt.setString(8, String.valueOf(user.getStatus()));
						pstmt.setLong(9, user.getCustomerID());
						pstmt.setString(10, user.getTimeZone());
						pstmt.setTimestamp(11, null);
						pstmt.setString(12, userInfo);
						pstmt.setString(13, userNotification);
						pstmt.setString(14, MFAUserImage);
						pstmt.setString(15, MFAUserOTP);
						pstmt.setString(16, MFAUserQuestionnaire);
						pstmt.setString(17, userName);
						pstmt.setString(18, userName);
						return pstmt;
					}
					
	          },keyHolder);
				
				user.setUserID(keyHolder.getKey().intValue());
				logger.info("End :" + getClass().getName() + " :saveUser()");
				return keyHolder.getKey()!=null?keyHolder.getKey().longValue():0L;
		} 
		catch (Exception ex) {
			String exceptionMsg = "Exception in saveUser() : ";
			logger.error("Exception in saveUser()::", ex);
			if(ex.getCause() instanceof SQLIntegrityConstraintViolationException)
				exceptionMsg = "Duplicate entry for user name : " + user.getUserName();
			throw new SecurityException(exceptionMsg + ex.getCause(), ex);
		}
			
	}
	
	public	int	saveCustomer() {
		String customerQuery = "insert into Customer values(10,'aks')";
		int insert = jdbcTemplate.update(customerQuery);
		return insert;
	}

	@Override
	public UserBO getUserById(long userId) {
		logger.info("Begin :" + getClass().getName() + " :getUser()");
		String selectQuery = "SELECT * FROM SEC_USER_TEST WHERE USERID=?";
		try{
			Object[] args = new Object[] { userId };
			logger.info("End :" + getClass().getName() + " :getUser()");
			return jdbcTemplate.queryForObject(selectQuery, args,new UserRowMapper());
		} catch (EmptyResultDataAccessException ex) {
			logger.error("Exception while getUser: "+ new Object[]{userId}, ex);
			throw new SecurityException("User ' "+userId+" ' doesn't exists.", ex);
		} catch (Exception ex) {
			logger.error("Exceptino : "+ new Object[]{userId}, ex);
			throw new SecurityException("Exception getUser() ::"+ex, ex);
		}
	}

	@Override
	public UserBO updateUser(UserBO userBO) {

		logger.debug("Begin :" + getClass().getName() + " :updateUser()");
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		try {
			String userInfo = null;
			String userNotification = null;
			String MFAUserImage = null;
			String MFAUserOTP = null;
			String MFAUserQuestionnaire = null;
			
			if(userBO.getUserInfo() != null)
				userInfo = mapper.writeValueAsString(userBO.getUserInfo());
			if(userBO.getUserNotification() != null)
				userNotification =mapper.writeValueAsString(userBO.getUserNotification());
			if(userBO.getMfaUserImage() != null)
				MFAUserImage = mapper.writeValueAsString(userBO.getMfaUserImage());
			if(userBO.getMfaUserOTP() != null)
				MFAUserOTP = mapper.writeValueAsString(userBO.getMfaUserOTP());
			if(userBO.getMfaUserQuestionnaire() != null)
				MFAUserQuestionnaire = mapper.writeValueAsString(userBO.getMfaUserQuestionnaire());
			if(userBO.getUpdatedBy() == null)
				userBO.setUpdatedBy("test_aks");
						
			String query = "UPDATE SEC_USER_TEST SET USERNAME=?,FIRSTNAME=?,MIDDLENAME=?,LASTNAME=?,EMAILID=?,"
				+ "TYPE=?,STATUS=?,CUSTOMERID=?,TIMEZONE=?,USERINFO=?,"
				+ "USERNOTIFICATION=?,MFAUSERIMAGE=?,MFAUSEROTP=?,MFAUSERQUESTIONNAIRE=?," 
				+ "UPDATEDBY=?,UPDATEDATE=UTC_TIMESTAMP WHERE USERID=?" ;
			
			Object[] params = new Object[] {
					userBO.getUserName(),
					userBO.getFirstName(),
					userBO.getMiddleName(),
					userBO.getLastName(),
					userBO.getEmailID(),
					String.valueOf(userBO.getType()),
					String.valueOf(userBO.getStatus()),
					userBO.getCustomerID(),
					userBO.getTimeZone(),
					userInfo,
					userNotification,
					MFAUserImage,
					MFAUserOTP,
					MFAUserQuestionnaire,
					userBO.getUpdatedBy(),
					userBO.getUserID()
				};
			int result = jdbcTemplate.update(query, params);
			logger.debug("End :" + getClass().getName() + " :updateUser()");
			if(result>0) 
				return userBO;
			else 
				return null;
			
		} catch (Exception ex) {
			logger.error("Exception updateUser() ::",ex);
			throw new SecurityException("Exception in update user: " + ex);
		}
	}

	public List<UserBO>	listUserWithCriteria(String criteria) {
		logger.info("Begin : " + getClass().getName() + " :listUser()");
		String	listQuery = "SELECT * FROM SEC_USER_TEST WHERE CREATEDBY=?";
		try {
			Object []args = new Object[] { criteria };
			return jdbcTemplate.query(listQuery, args, new UserRowMapper());
		} catch (Exception e) {
			logger.error("Exception while listing user: "+ e);
			throw new SecurityException("Exception ::");
		}
	}

	@Override
	public UserBO getUserCustom(long userId, String userName) {
		logger.info("Begin : " + getClass().getName() + " :getUserCustom()");
		String	listQuery = "SELECT * FROM SEC_USER_TEST WHERE USERID = ? AND USERNAME = ?";
		try {
			Object []args = new Object[] { userId, userName };
			return jdbcTemplate.queryForObject(listQuery, args, new UserRowMapper());
		} catch (Exception e) {
			logger.error("Exception while getUserCustom : "+ e);
			throw new SecurityException("Exception ::");
		}
	}

	@Override
	public List<UserBO> listAllUser() {
		logger.info("Begin : " + getClass().getName() + " :listUser()");
		String	listQuery = "SELECT * FROM SEC_USER_TEST";
		try {
			//Object []args = new Object[] { criteria };
			return jdbcTemplate.query(listQuery, new UserRowMapper());
		} catch (Exception e) {
			logger.error("Exception while listing user: "+ e);
			throw new SecurityException("Exception ::");
		}
	}	
	
}
