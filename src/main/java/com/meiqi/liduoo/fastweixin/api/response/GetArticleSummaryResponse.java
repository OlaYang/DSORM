package com.meiqi.liduoo.fastweixin.api.response;

import java.util.List;

import com.meiqi.liduoo.fastweixin.api.entity.ArticleSummary;

/**
 * @author peiyu
 */
public class GetArticleSummaryResponse extends BaseResponse {

    private List<ArticleSummary> list;

    public List<ArticleSummary> getList() {
        return list;
    }

    public void setList(List<ArticleSummary> list) {
        this.list = list;
    }
}
