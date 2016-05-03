package com.meiqi.dsmanager.po.rule.preview;


import java.util.HashSet;
import java.util.Set;

import com.meiqi.data.engine.InvokeNode;

/**
 * User: 
 * Date: 13-8-31
 * Time: 上午9:42
 */
public class ProcessInfo {
    private Set<String> serviceNames = new HashSet<String>();
    private Set<String> paramRequired = new HashSet<String>();
    private Set<String> paramROptional = new HashSet<String>();
    private InvokeNode root;


    public Set<String> getParamROptional() {
        return paramROptional;
    }

    public void setParamROptional(Set<String> paramROptional) {
        this.paramROptional = paramROptional;
    }

    @Override
    public String toString() {
        return "ProcessInfo{" +
                ", serviceNames=" + serviceNames +
                ", paramRequired=" + paramRequired +
                ", paramROptional=" + paramROptional +
                ", root=" + root +
                '}';
    }

    public Set<String> getParamRequired() {
        return paramRequired;
    }

    public void setParamRequired(Set<String> paramRequired) {
        this.paramRequired = paramRequired;
    }

    public InvokeNode getRoot() {
        return root;
    }

    public void setRoot(InvokeNode root) {
        this.root = root;
    }

    public Set<String> getServiceNames() {
        return serviceNames;
    }

    public void setServiceNames(Set<String> serviceNames) {
        this.serviceNames = serviceNames;
    }
}
