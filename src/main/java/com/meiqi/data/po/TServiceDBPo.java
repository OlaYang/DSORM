package com.meiqi.data.po;

/**
 * User: 
 * Date: 13-7-2
 * Time: 上午10:39
 */
public class TServiceDBPo {
    private String dbID;
    private String driver;
    private String url;
    private String user;
    private String password;
    private String pool;  // 对应业务netty线程池

    @Override
    public String toString() {
        return "TServiceDBPo{" +
                "dbID=" + dbID +
                ", driver='" + driver + '\'' +
                ", url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }


    public String getPool() {
        return pool;
    }

    public void setPool(String pool) {
        this.pool = pool;
    }

    public String getDbID() {
        return dbID;
    }

    public void setDbID(String dbID) {
        this.dbID = dbID;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
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
        if (!(o instanceof TServiceDBPo)) return false;

        TServiceDBPo dbPo = (TServiceDBPo) o;

        if (dbID != null ? !dbID.equals(dbPo.dbID) : dbPo.dbID != null)
            return false;
        if (driver != null ? !driver.equals(dbPo.driver) : dbPo.driver != null)
            return false;
        if (password != null ? !password.equals(dbPo.password) : dbPo.password != null)
            return false;
        if (url != null ? !url.equals(dbPo.url) : dbPo.url != null)
            return false;
        if (user != null ? !user.equals(dbPo.user) : dbPo.user != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dbID != null ? dbID.hashCode() : 0;
        return result;
    }
}
