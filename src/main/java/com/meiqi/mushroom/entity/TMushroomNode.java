package com.meiqi.mushroom.entity;

/**
 * 数据库节点实体
 * User: 
 * Date: 13-10-10
 * Time: 上午9:58
 */
public class TMushroomNode {
    private Integer nid;
    private String name;
    private String url;
    private String user;
    private String password;


    public Integer getNid() {
        return nid;
    }

    public void setNid(Integer nid) {
        this.nid = nid;
    }

    @Override
    public String toString() {
        return "TMushroomNode{" +
                "name='" + name + '\'' +
                ", nid=" + nid +
                ", url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TMushroomNode)) return false;

        TMushroomNode that = (TMushroomNode) o;

        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;
        if (nid != null ? !nid.equals(that.nid) : that.nid != null)
            return false;
        if (password != null ? !password.equals(that.password) : that.password != null)
            return false;
        if (url != null ? !url.equals(that.url) : that.url != null)
            return false;
        if (user != null ? !user.equals(that.user) : that.user != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = nid != null ? nid.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
