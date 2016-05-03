package com.meiqi.liduoo.fastweixin.api.response;

import java.util.List;

import com.meiqi.liduoo.fastweixin.api.entity.UserRead;

/**
 * @author peiyu
 */
public class GetUserReadResponse extends BaseResponse {

    private List<UserRead> list;

    public List<UserRead> getList() {
        return list;
    }

    public void setList(List<UserRead> list) {
        this.list = list;
    }
}
