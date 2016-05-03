package com.meiqi.liduoo.fastweixin.api.response;

import java.util.List;

import com.meiqi.liduoo.fastweixin.api.entity.CardCardInfo;

/**
 * @author peiyu
 */
public class GetCardCardInfoResponse extends BaseResponse {

	private List<CardCardInfo> list;

	public List<CardCardInfo> getList() {
		return list;
	}

	public void setList(List<CardCardInfo> list) {
		this.list = list;
	}
}
