package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: Role
 * @Description:角色管理 type 1=匿名用户 2=设计师 3=导购 ...
 * @author 杨永川
 * @date 2015年4月11日 下午2:18:08
 *
 */
public class Role {
    private int    roleId;
    private byte   roleType;
    private String roleName;
    private String actionList;
    private String roleDescribe;



    public Role() {
    }



    public Role(int roleId, byte roleType, String roleName, String actionList, String roleDescribe) {
        super();
        this.roleId = roleId;
        this.roleType = roleType;
        this.roleName = roleName;
        this.actionList = actionList;
        this.roleDescribe = roleDescribe;
    }



    public int getRoleId() {
        return roleId;
    }



    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }



    public byte getRoleType() {
        return roleType;
    }



    public void setRoleType(byte roleType) {
        this.roleType = roleType;
    }



    public String getRoleName() {
        return roleName;
    }



    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }



    public String getActionList() {
        return actionList;
    }



    public void setActionList(String actionList) {
        this.actionList = actionList;
    }



    public String getRoleDescribe() {
        return roleDescribe;
    }



    public void setRoleDescribe(String roleDescribe) {
        this.roleDescribe = roleDescribe;
    }

}