package com.meiqi.mushroom.entity;

import java.util.List;

/**
 * 服务类实体
 * User: 
 * Date: 13-10-10
 * Time: 上午9:59
 */
public class TMushroomService {
    private Integer sid;
    private String name;
    private String desc;
    private Integer state;
    private List<TMushroomTable> tables;
    private String scope;
    private boolean regLogin;


    public boolean isRegLogin() {
		return regLogin;
	}

	public void setRegLogin(boolean regLogin) {
		this.regLogin = regLogin;
	}

	public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<TMushroomTable> getTables() {
        return tables;
    }

    public void setTables(List<TMushroomTable> tables) {
        this.tables = tables;
    }

    @Override
    public String toString() {
        return "TMushroomService{" +
                "desc='" + desc + '\'' +
                ", sid=" + sid +
                ", name='" + name + '\'' +
                ", state=" + state +
                ", tables=" + tables +
                ", scope='" + scope + '\'' +
                '}';
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
