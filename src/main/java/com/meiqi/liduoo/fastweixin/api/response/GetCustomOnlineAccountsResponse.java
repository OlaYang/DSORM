package com.meiqi.liduoo.fastweixin.api.response;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.meiqi.liduoo.fastweixin.api.entity.CustomOnlineAccount;

/**
 * @author peiyu
 */
public class GetCustomOnlineAccountsResponse extends BaseResponse {

    @JSONField(name = "kf_online_list")
    private List<CustomOnlineAccount> customAccountList;

    public List<CustomOnlineAccount> getCustomAccountList() {
        return customAccountList;
    }

    public void setCustomAccountList(List<CustomOnlineAccount> customAccountList) {
        this.customAccountList = customAccountList;
    }
}
