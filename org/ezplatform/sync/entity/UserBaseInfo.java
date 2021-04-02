package org.ezplatform.sync.entity;

public class UserBaseInfo {
	private static final long serialVersionUID = -1798070786993154676L;

	// "员工工号"
	private String userCode;

	// "人员姓名"
	private String userName;

	// "登录名"
	private String loginName;

	// 密码
	private String password;

	// "性别"
	private String sex;

	// "手机"
	private String telephone;

	// "人事状态"
	private String userStatus;

	// 上级领导id
	private String userLeaders;

	private String email;

	private int userLevel;
	// "身份证号"
	private String cardId;

	private int isDelete;

	// "是否休眠"
	private int isDormant;

	//
	private String orgId;

	// "职务id"
	private String dutyId;
	private String dutyCode;

	// 所属组织
	private String orgName;

	// 所属组织编码
	private String orgCode;

	// 岗位
	private String staName;

	private String stationId;
	private String stationCode;
	private String orgLeaderAccount;
	private String deputyLeaderAccount;
	private String leaderAccount;
	private String corpId;
	private String pwdEncrypt;

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public String getUserLeaders() {
		return userLeaders;
	}

	public void setUserLeaders(String userLeaders) {
		this.userLeaders = userLeaders;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(int userLevel) {
		this.userLevel = userLevel;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public int getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(int isDelete) {
		this.isDelete = isDelete;
	}

	public int getIsDormant() {
		return isDormant;
	}

	public void setIsDormant(int isDormant) {
		this.isDormant = isDormant;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getDutyId() {
		return dutyId;
	}

	public void setDutyId(String dutyId) {
		this.dutyId = dutyId;
	}

	public String getDutyCode() {
		return dutyCode;
	}

	public void setDutyCode(String dutyCode) {
		this.dutyCode = dutyCode;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getStaName() {
		return staName;
	}

	public void setStaName(String staName) {
		this.staName = staName;
	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public String getStationCode() {
		return stationCode;
	}

	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}

	public String getOrgLeaderAccount() {
		return orgLeaderAccount;
	}

	public void setOrgLeaderAccount(String orgLeaderAccount) {
		this.orgLeaderAccount = orgLeaderAccount;
	}

	public String getDeputyLeaderAccount() {
		return deputyLeaderAccount;
	}

	public void setDeputyLeaderAccount(String deputyLeaderAccount) {
		this.deputyLeaderAccount = deputyLeaderAccount;
	}

	public String getLeaderAccount() {
		return leaderAccount;
	}

	public void setLeaderAccount(String leaderAccount) {
		this.leaderAccount = leaderAccount;
	}

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public String getPwdEncrypt() {
		return pwdEncrypt;
	}

	public void setPwdEncrypt(String pwdEncrypt) {
		this.pwdEncrypt = pwdEncrypt;
	}

	
}