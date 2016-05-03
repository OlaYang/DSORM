package com.meiqi.app.pojo;

import java.util.Date;

/**
 * 
 * @ClassName: Users
 * @Description:用户
 * @author 杨永川
 * @date 2015年4月11日 下午2:16:35
 *
 */
public class Users {
    private long    userId;
    private String  email          = "";
    // 登录用户名
    private String  userName       = "";
    private String  realName       = "";
    private Region  city;
    private Company company;

    private String  password       = "";
    private String  question       = "";
    private String  answer         = "";
    private Integer sex;
    private Date    birthday       = new Date();
    private Double  userMoney      = 0.00;
    private Double  frozenMoney    = 0.00;
    private Integer payPoints      = 0;
    private Integer rankPoints     = 0;
    private Integer addressId      = 0;
    private Integer regTime        = 0;
    private Integer lastLogin      = 0;
    private Date    lastTime       = new Date();
    private String  lastIp         = "";
    private int     visitCount     = 0;
    private Long    userRank       = 0l;
    private Long    isSpecial      = 0l;
    private String  ecSalt         = "";
    private String  salt           = "";
    private Integer parentId       = 0;
    private long    flag           = 0;
    private String  alias          = "";
    private String  msn            = "";
    private String  qq             = "";
    private String  officePhone    = "";
    private String  homePhone      = "";
    private String  phone          = "";
    // 是否生效
    private int     isValidated    = 0;
    private double  creditLine     = 0.00;
    private String  passwdQuestion = "";
    private String  passwdAnswer   = "";
    private String  avatar         = "";
    private Integer roleId         = 1;
    private String  deviceId;
    private Long    shopId         = 0l;
    private Long    companyId      = 0l;
    private Integer from           = 0;
    // 验证码 临时属性
    private String  code;
    // 验证类型 临时属性
    private byte    type;
    private String  oldPassword;
    // 临时属性
    private long    regionId       = 322;
    private String  accessToken;
    
    private String inviteCode;
    
    private InviteCode uCode;
    
    private String roleName; //规则返回
    

    public Users() {
    }



    public long getUserId() {
        return userId;
    }



    public void setUserId(long userId) {
        this.userId = userId;
    }



    public String getEmail() {
        return email;
    }



    public void setEmail(String email) {
        this.email = email;
    }



    public String getUserName() {
        return userName;
    }



    public void setUserName(String userName) {
        this.userName = userName;
    }



    public String getRealName() {
        return realName;
    }



    public void setRealName(String realName) {
        this.realName = realName;
    }



    public Region getCity() {
        return city;
    }



    public void setCity(Region city) {
        this.city = city;
    }



    public Company getCompany() {
        return company;
    }



    public void setCompany(Company company) {
        this.company = company;
    }



    public String getPassword() {
        return password;
    }



    public void setPassword(String password) {
        this.password = password;
    }



    public String getQuestion() {
        return question;
    }



    public void setQuestion(String question) {
        this.question = question;
    }



    public String getAnswer() {
        return answer;
    }



    public void setAnswer(String answer) {
        this.answer = answer;
    }



    public Integer getSex() {
        return sex;
    }



    public void setSex(Integer sex) {
        this.sex = sex;
    }



    public Date getBirthday() {
        return birthday;
    }



    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }



    public Double getUserMoney() {
        return userMoney;
    }



    public void setUserMoney(Double userMoney) {
        this.userMoney = userMoney;
    }



    public Double getFrozenMoney() {
        return frozenMoney;
    }



    public void setFrozenMoney(Double frozenMoney) {
        this.frozenMoney = frozenMoney;
    }



    public Integer getPayPoints() {
        return payPoints;
    }



    public void setPayPoints(Integer payPoints) {
        this.payPoints = payPoints;
    }



    public Integer getRankPoints() {
        return rankPoints;
    }



    public void setRankPoints(Integer rankPoints) {
        this.rankPoints = rankPoints;
    }



    public Integer getAddressId() {
        return addressId;
    }



    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }



    public Integer getRegTime() {
        return regTime;
    }



    public void setRegTime(Integer regTime) {
        this.regTime = regTime;
    }



    public Integer getLastLogin() {
        return lastLogin;
    }



    public void setLastLogin(Integer lastLogin) {
        this.lastLogin = lastLogin;
    }



    public Date getLastTime() {
        return lastTime;
    }



    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }



    public String getLastIp() {
        return lastIp;
    }



    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }



    public int getVisitCount() {
        return visitCount;
    }



    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }



    public Long getUserRank() {
        return userRank;
    }



    public void setUserRank(Long userRank) {
        this.userRank = userRank;
    }



    public Long getIsSpecial() {
        return isSpecial;
    }



    public void setIsSpecial(Long isSpecial) {
        this.isSpecial = isSpecial;
    }



    public String getEcSalt() {
        return ecSalt;
    }



    public void setEcSalt(String ecSalt) {
        this.ecSalt = ecSalt;
    }



    public String getSalt() {
        return salt;
    }



    public void setSalt(String salt) {
        this.salt = salt;
    }



    public Integer getParentId() {
        return parentId;
    }



    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }



    public long getFlag() {
        return flag;
    }



    public void setFlag(long flag) {
        this.flag = flag;
    }



    public String getAlias() {
        return alias;
    }



    public void setAlias(String alias) {
        this.alias = alias;
    }



    public String getMsn() {
        return msn;
    }



    public void setMsn(String msn) {
        this.msn = msn;
    }



    public String getQq() {
        return qq;
    }



    public void setQq(String qq) {
        this.qq = qq;
    }



    public String getOfficePhone() {
        return officePhone;
    }



    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }



    public String getHomePhone() {
        return homePhone;
    }



    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }



    public String getPhone() {
        return phone;
    }



    public void setPhone(String phone) {
        this.phone = phone;
    }



    public int getIsValidated() {
        return isValidated;
    }



    public void setIsValidated(int isValidated) {
        this.isValidated = isValidated;
    }



    public double getCreditLine() {
        return creditLine;
    }



    public void setCreditLine(double creditLine) {
        this.creditLine = creditLine;
    }



    public String getPasswdQuestion() {
        return passwdQuestion;
    }



    public void setPasswdQuestion(String passwdQuestion) {
        this.passwdQuestion = passwdQuestion;
    }



    public String getPasswdAnswer() {
        return passwdAnswer;
    }



    public void setPasswdAnswer(String passwdAnswer) {
        this.passwdAnswer = passwdAnswer;
    }



    public String getAvatar() {
        return avatar;
    }



    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }



    public Integer getRoleId() {
        return roleId;
    }



    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }



    public String getDeviceId() {
        return deviceId;
    }



    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }



    public Long getShopId() {
        return shopId;
    }



    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }



    public Long getCompanyId() {
        return companyId;
    }



    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }



    public String getCode() {
        return code;
    }



    public void setCode(String code) {
        this.code = code;
    }



    public byte getType() {
        return type;
    }



    public void setType(byte type) {
        this.type = type;
    }



    public String getOldPassword() {
        return oldPassword;
    }



    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }



    public long getRegionId() {
        return regionId;
    }



    public void setRegionId(long regionId) {
        this.regionId = regionId;
    }



    public String getAccessToken() {
        return accessToken;
    }



    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }



    public String getInviteCode() {
        return inviteCode;
    }



    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }
    
    public InviteCode getUCode() {
        return uCode;
    }



    public void setUCode(InviteCode uCode) {
        this.uCode = uCode;
    }



    public Integer getFrom() {
        return from;
    }



    public void setFrom(Integer from) {
        this.from = from;
    }



    public String getRoleName() {
        return roleName;
    }



    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

}