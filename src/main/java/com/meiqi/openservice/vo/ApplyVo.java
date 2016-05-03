package com.meiqi.openservice.vo;

public class ApplyVo
{
    
    private Long id;
    
    private long userId;
    
    private String registerName;
    
    private String phone;
    
    private Integer province;
    
    private Integer city;
    
    private Integer district;
    
    private String budget;
    
    private String decorateDate;
    
    private Integer addDate;
    
    private Integer targetId;
    
    private String formName;
    
    private String verifyCode;
    
    private String codeType;
    
    private String qq;
    
    private String buildingName;
    
    //来源
    private short type;
    
    //来源对象id
    private long sourceId;
    
    public long getUserId()
    {
        return userId;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
    public Integer getTargetId()
    {
        return targetId;
    }
    
    public void setTargetId(Integer targetId)
    {
        this.targetId = targetId;
    }
    
    public Integer getAddDate()
    {
        return addDate;
    }
    
    public void setAddDate(Integer addDate)
    {
        this.addDate = addDate;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public String getRegisterName()
    {
        return registerName;
    }
    
    public void setRegisterName(String registerName)
    {
        this.registerName = registerName;
    }
    
    public String getPhone()
    {
        return phone;
    }
    
    public void setPhone(String phone)
    {
        this.phone = phone;
    }
    
    public String getBudget()
    {
        return budget;
    }
    
    public void setBudget(String budget)
    {
        this.budget = budget;
    }
    
    public String getDecorateDate()
    {
        return decorateDate;
    }
    
    public void setDecorateDate(String decorateDate)
    {
        this.decorateDate = decorateDate;
    }
    
    public Integer getProvince()
    {
        return province;
    }
    
    public void setProvince(Integer province)
    {
        this.province = province;
    }
    
    public Integer getCity()
    {
        return city;
    }
    
    public void setCity(Integer city)
    {
        this.city = city;
    }
    
    public Integer getDistrict()
    {
        return district;
    }
    
    public void setDistrict(Integer district)
    {
        this.district = district;
    }
    
    public String getVerifyCode()
    {
        return verifyCode;
    }
    
    public void setVerifyCode(String verifyCode)
    {
        this.verifyCode = verifyCode;
    }
    
    public String getFormName()
    {
        return formName;
    }
    
    public void setFormName(String formName)
    {
        this.formName = formName;
    }
    
    public String getCodeType()
    {
        return codeType;
    }

    public void setCodeType(String codeType)
    {
        this.codeType = codeType;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public long getSourceId() {
        return sourceId;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    @Override
    public String toString()
    {
        return "RegisterDomain [id=" + id + ", registerName=" + registerName + ", phone=" + phone + ", province="
            + province + ", city=" + city + ", district=" + district + ", budget=" + budget + ", decorateDate="
            + decorateDate + ", addDate=" + addDate + ", targetId=" + targetId + ", verifyCode=" + verifyCode + "]";
    }
    
}
