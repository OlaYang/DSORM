package com.meiqi.data.engine;

import com.meiqi.data.entity.TService;
import com.meiqi.data.po.TServicePo;

import java.util.Map;

/**
 * User: 
 * Date: 13-8-16
 * Time: 上午10:35
 */
public final class ProcessInfos {
    private static ThreadLocal<InvokeInfo> invokeInfo = new ThreadLocal<InvokeInfo>() {
        @Override
        protected InvokeInfo initialValue() {
            return new InvokeInfo();
        }
    };


    public static void clear() {
        invokeInfo.remove();
    }

    static InvokeNode addLink(TService father, Map<String, Object> fatherParam,
                              TService child, Map<String, Object> childrParam, String attr) throws RengineException {
        return invokeInfo.get().addEdge(father, fatherParam, child, childrParam, attr);
    }

    public static InvokeNode getInvokeNode() {
        return invokeInfo.get().root;
    }
}
