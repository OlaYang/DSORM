package com.meiqi.liduoo.fastweixin.api.response;

import java.util.List;

import com.meiqi.liduoo.fastweixin.api.entity.UpstreamMsgDistMonth;

/**
 * @author peiyu
 */
public class GetUpstreamMsgDistMonthResponse extends BaseResponse {

    private List<UpstreamMsgDistMonth> list;

    public List<UpstreamMsgDistMonth> getList() {
        return list;
    }

    public void setList(List<UpstreamMsgDistMonth> list) {
        this.list = list;
    }
}
