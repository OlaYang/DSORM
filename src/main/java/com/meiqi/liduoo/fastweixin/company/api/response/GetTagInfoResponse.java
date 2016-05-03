package com.meiqi.liduoo.fastweixin.company.api.response;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.annotation.JSONField;
import com.meiqi.liduoo.fastweixin.api.response.BaseResponse;

/**
 *  Response -- 标签信息
 *  ====================================================================
 *  上海聚攒软件开发有限公司
 *  --------------------------------------------------------------------
 *  @author Nottyjay
 *  @version 1.0.beta
 *  @since 1.3.6
 *  ====================================================================
 */
public class GetTagInfoResponse extends BaseResponse {

    @JSONField(name = "userlist")
    private List<Map<String, String>> users;
    @JSONField(name = "partylist")
    private List<Integer> partys;

    public List<Map<String, String>> getUsers() {
        return users;
    }

    public void setUsers(List<Map<String, String>> users) {
        this.users = users;
    }

    public List<Integer> getPartys() {
        return partys;
    }

    public void setPartys(List<Integer> partys) {
        this.partys = partys;
    }
}
