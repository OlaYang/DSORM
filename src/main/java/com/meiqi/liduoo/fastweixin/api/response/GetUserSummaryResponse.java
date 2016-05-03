package com.meiqi.liduoo.fastweixin.api.response;

import java.util.List;

import com.meiqi.liduoo.fastweixin.api.entity.UserSummary;

/**
 * @author peiyu
 */
public class GetUserSummaryResponse extends BaseResponse {

    private List<UserSummary> list;

    public List<UserSummary> getList() {
        return list;
    }

    public void setList(List<UserSummary> list) {
        this.list = list;
    }
}
