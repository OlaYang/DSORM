package com.meiqi.openservice.action.javabin.ip.util;

public class IPLocation {
	private String country;
	private String area;

	public IPLocation() {
		country = area = "";
	}

	public IPLocation getCopy() {
		IPLocation ret = new IPLocation();
		ret.country = country;
		ret.area = area;
		return ret;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		// ���Ϊ��������IP��ַ��ĵ������ʾCZ88.NET,�������ȥ��
		if (area.trim().equals("CZ88.NET")) {
			this.area = "���������";
		} else {
			this.area = area;
		}
	}
}
