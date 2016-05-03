package com.meiqi.liduoo.fastweixin.api.response;

import java.util.List;

import com.meiqi.liduoo.fastweixin.api.entity.ArticleTotal;

/**
 * @author peiyu
 */
public class GetArticleTotalResponse extends BaseResponse {

    private List<ArticleTotal> list;

    public List<ArticleTotal> getList() {
        return list;
    }

    public void setList(List<ArticleTotal> list) {
        this.list = list;
    }
}
