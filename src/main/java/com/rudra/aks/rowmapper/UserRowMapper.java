package com.rudra.aks.rowmapper;


import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rudra.aks.bo.MFAUserImage;
import com.rudra.aks.bo.MFAUserOTP;
import com.rudra.aks.bo.MFAUserQuestionnaire;
import com.rudra.aks.bo.UserBO;
import com.rudra.aks.bo.UserInfo;
import com.rudra.aks.bo.UserNotification;

public class UserRowMapper implements RowMapper<UserBO> {

	
	private static final Logger logger = Logger.getLogger(UserRowMapper.class);

	@Override
	public UserBO mapRow(ResultSet rs, int rowNum) throws SQLException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		UserBO userBO = new UserBO();
		userBO.setUserID(rs.getInt("USERID"));
		userBO.setUserName(rs.getString("USERNAME"));
		userBO.setFirstName(rs.getString("FIRSTNAME"));
		userBO.setMiddleName(rs.getString("MIDDLENAME"));
		userBO.setLastName(rs.getString("LASTNAME"));
		userBO.setEmailID(rs.getString("EMAILID"));
		userBO.setPassword(rs.getString("PASSWORD"));
		//userBO.setDepartmentID(rs.getInt("DEPARTMENTID"));
		
		if(rs.getString("TYPE") != null && rs.getString("TYPE").trim().length() > 0){
			userBO.setType(rs.getString("TYPE").charAt(0));
		}
		if(rs.getString("STATUS") != null && rs.getString("STATUS").trim().length() > 0){
			userBO.setStatus(rs.getString("STATUS").charAt(0));
		}
		userBO.setCustomerID(rs.getInt("CUSTOMERID"));
		userBO.setTimeZone(rs.getString("TIMEZONE"));
		//userBO.setLastLogin(rs.getTimestamp("LASTLOGIN"));
		try {
			if(StringUtils.isNotBlank(rs.getString("USERINFO")))
				userBO.setUserInfo(mapper.readValue(rs.getString("USERINFO"), UserInfo.class));
			if(StringUtils.isNotBlank(rs.getString("USERNOTIFICATION")))
				userBO.setUserNotification(mapper.readValue(rs.getString("USERNOTIFICATION"),new TypeReference<UserNotification>() {}));
			if(StringUtils.isNotBlank(rs.getString("MFAUSERIMAGE")))
				userBO.setMfaUserImage(mapper.readValue(rs.getString("MFAUSERIMAGE"),new TypeReference<List<MFAUserImage>>() {}));
			if(StringUtils.isNotBlank(rs.getString("MFAUSEROTP")))
				userBO.setMfaUserOTP(mapper.readValue(rs.getString("MFAUSEROTP"),MFAUserOTP.class));
			if(StringUtils.isNotBlank(rs.getString("MFAUSERQUESTIONNAIRE")))
				userBO.setMfaUserQuestionnaire(mapper.readValue(rs.getString("MFAUSERQUESTIONNAIRE"),new TypeReference<List<MFAUserQuestionnaire>>() {}));
		} catch (IOException e) {
			logger.error("Failed in userRowMapper :: ", e);
			throw new SQLException("Unable to convert JSON Format, invalid JSON String :: "+e, e);
		}
		if(rs.getString("createdby") != null)
		userBO.setCreatedBy(rs.getString("createdby"));
		if(rs.getString("updatedby") != null)
			userBO.setUpdatedBy(rs.getString("updatedby"));
		
		/** Converting timestamp from server timezone to local timezone *//*
		java.sql.Timestamp createDate = null, updateDate = null;
		try {
			TimeZone timeZone = TimeZone.getTimeZone(ZetaUtil.getHelper().getUser().getTimeZone());
			createDate = rs.getTimestamp("createdate")==null?null:CommonUtil.toLocalTime(rs.getTimestamp("createdate").getTime(), timeZone);
			updateDate = rs.getTimestamp("updatedate")==null?null:CommonUtil.toLocalTime(rs.getTimestamp("updatedate").getTime(), timeZone);	
		} catch (Exception e1) {
			logger.error("Error occured while converting timestamp to user timezone: "+e1.getMessage());
		}
		if(rs.getString("createdate") != null)
			userBO.setCreateDate(createDate);
		
		if(rs.getString("updatedate") != null)
			userBO.setUpdateDate(updateDate);
		*/
		return userBO;
	}
	
	
}

