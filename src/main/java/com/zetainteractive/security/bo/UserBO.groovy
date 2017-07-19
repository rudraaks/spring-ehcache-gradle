package com.zetainteractive.security.bo

import org.springframework.stereotype.Component

import com.fasterxml.jackson.annotation.JsonAutoDetect

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.transform.Sortable

@Sortable(includes=['userName','emailID'])
@CompileStatic
@Canonical
@JsonAutoDetect
class UserBO implements Serializable {
	int userID
	int groupID
	String userName
	String firstName
	String middleName
	String lastName
	String emailID
	String password
	long departmentID
	char type = 'U' //U,S,A
	char status = 'A'//
	long customerID  
	String custCode // for ui specific, not in table
	String timeZone = 'America/Los_Angeles' 
	String listDepts 
	UserInfo userInfo
	UserNotification userNotification
	List<MFAUserImage> mfaUserImage
	MFAUserOTP mfaUserOTP
	List<MFAUserQuestionnaire> mfaUserQuestionnaire
	String createdBy
	String updatedBy
/*	@JsonSerialize//(using = JsonDateTimeSerializer.Class)
	@JsonDeserialize//(using = JsonDateTimeDeserializer.class)
	Date lastLogin
	@JsonSerialize//(using = JsonDateTimeSerializer.class)
	@JsonDeserialize//(using = JsonDateTimeDeserializer.class)
	Date createDate
	@JsonSerialize//(using = JsonDateTimeSerializer.class)
	@JsonDeserialize//(using = JsonDateTimeDeserializer.class)
	Date updateDate*/
}

@CompileStatic
@Canonical
class UserInfo implements Serializable{
	String locale = "en";                /* default - en*/
	//char approvalMandatory = 'N'         /* default - N */  //ZHPE-9770 - COMMENTED
	char sendNotification  = 'N'        /* default - N */
	//char allDepartments   = 'N'         /* default - N */ //ZHPE-9770 - COMMENTED
	String oldpasswords          /* default - 1  */
	Date expiryDate                
	int invalidCounter  =0          /* default - 0  */
	long mobileNumber
	String resetToken  
	String rules            
	//int isForgotPassword = 0         /* default - 0 */
}

@CompileStatic
@Canonical
class UserNotification implements Serializable{
	char listStatus = 'N' 
	char detailedReportStatus= 'N' 
	String  campaignStatus = "Disable" 
	/** Notification Address List */
	String emailAddressList
}


@CompileStatic
@Canonical
class MFAUserImage implements Serializable{
	int imageID            
	String textPhrase    
	char isSelected
}

@CompileStatic
@Canonical
class MFAUserOTP implements Serializable{
	int OTP            
	Date expiryDate
}

@CompileStatic
@Canonical
class MFAUserQuestionnaire implements Serializable{
	int mfaquestionID
	String answer
}
