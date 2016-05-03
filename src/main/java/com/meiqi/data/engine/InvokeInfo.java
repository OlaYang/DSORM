package com.meiqi.data.engine;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.entity.TService;
import com.meiqi.data.po.TServicePo;
import com.meiqi.data.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * User: 
 * Date: 13-8-15
 * Time: 下午7:15
 */
public class InvokeInfo {
    static class __key {
        final String serviceName;
        final Map<String, Object> param;

        __key(Map<String, Object> param, String serviceName) {
            this.param = param;
            this.serviceName = serviceName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof __key)) return false;

            __key key = (__key) o;

            if (param != null ? !param.equals(key.param) : key.param != null)
                return false;
            if (serviceName != null ? !serviceName.equals(key.serviceName) : key.serviceName != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = serviceName != null ? serviceName.hashCode() : 0;
            result = 31 * result + (param != null ? param.hashCode() : 0);
            return result;
        }
    }


    Map<__key, InvokeNode> nodes = new HashMap<__key, InvokeNode>();
    InvokeNode root = null;

    InvokeNode addEdge(TService father, Map<String, Object> fatherParam,
                       TService child, Map<String, Object> childrParam, String attr) throws RengineException {
        __key childKey = new __key(childrParam, child.getName());
        InvokeNode childNode = new InvokeNode(attr, childrParam, child);

        if (father == null) {
            nodes.put(childKey, childNode);
            root = childNode;
        } else {
            InvokeNode oldChildNode = nodes.get(childKey);

            if (oldChildNode != null) { //已经开始计算
                D2Data d2Data = Cache4D2Data.peek(child.getName(), childrParam);
                if (d2Data == null) { // 却没有算完
                    throw new RengineException(child.getName(), "数据源存在循环调用, " + father.getName() + JSON.toJSONString(fatherParam) +
                            " 调用 " + child.getName() + JSON.toJSONString(childrParam));
                }

                return null;
            } else {
                InvokeNode fatherNode = nodes.get(new __key(fatherParam, father.getName()));
                if (fatherNode == null) {
                    throw new RengineException(child.getName(), "!!!严重错误，调用方数据源不存在, " + father.getName());
                }

                nodes.put(childKey, childNode);
                fatherNode.childs.add(childNode);
            }
        }

        return childNode;
    }
}
