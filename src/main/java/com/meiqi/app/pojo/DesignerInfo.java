package com.meiqi.app.pojo;

/**
 * 
 * @Description:用于设计师个人信息页面
 * 
 * @author:luzicong
 * 
 * @time:2015年7月8日 上午11:20:27
 */
public class DesignerInfo {
	private String imagv;
	
	private String name; //真实姓名
	
	private String company;
	
	private Commission commission;
	
	// '设计师入驻来源 0未知 1 IPAD 2 IOS APP（优家购）3 安卓 APP（优家购）4 微信入驻（会员卡）5 PC端-优家购 6 PC端-爱有窝 7 后台添加',
	private int settleSource = 0;

	public String getImagv() {
		return imagv;
	}

	public void setImagv(String imagv) {
		this.imagv = imagv;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Commission getCommission() {
		return commission;
	}

	public void setCommission(Commission commission) {
		this.commission = commission;
	}

    public int getSettleSource() {
        return settleSource;
    }

    public void setSettleSource(int settleSource) {
        this.settleSource = settleSource;
    }

}
