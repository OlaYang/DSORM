package com.meiqi.liduoo.fastweixin.api.response;

import java.util.List;

import com.meiqi.liduoo.fastweixin.api.entity.CardBizuinInfo;

/**
 * @author peiyu
 */
public class GetCardBizuinInfoResponse extends BaseResponse {

	private List<CardBizuinInfo> list;

	public List<CardBizuinInfo> getList() {
		return list;
	}

	public void setList(List<CardBizuinInfo> list) {
		this.list = list;
	}
}
