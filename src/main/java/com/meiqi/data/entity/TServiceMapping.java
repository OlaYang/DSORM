package com.meiqi.data.entity;

import java.io.Serializable;

/**
 * User: 
 * Date: 13-7-2
 * Time: 上午10:36
 */
public class TServiceMapping implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String serviceInterface;
    private String serviceImplement;

    @Override
    public String toString() {
        return "TServiceMappingPo{" +
                "id='"+id+'\''+
                ",serviceImplement='" + serviceImplement + '\'' +
                ", serviceInterface='" + serviceInterface + '\'' +
                '}';
    }

    public String getServiceImplement() {
        return serviceImplement;
    }

    public void setServiceImplement(String serviceImplement) {
        this.serviceImplement = serviceImplement;
    }

    public String getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(String serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
    
    
}
