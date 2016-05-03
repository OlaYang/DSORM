package com.meiqi.liduoo.fastweixin.api.response;

import java.util.List;

import com.meiqi.liduoo.fastweixin.api.entity.UserShareHour;

/**
 * @author peiyu
 */
public class GetUserShareHourResponse extends BaseResponse {

    private List<UserShareHour> list;

    public List<UserShareHour> getList() {
        return list;
    }

    public void setList(List<UserShareHour> list) {
        this.list = list;
    }
}
