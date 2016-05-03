package com.meiqi.liduoo.fastweixin.api.response;

import java.util.List;

import com.meiqi.liduoo.fastweixin.api.entity.CardCardInfo;
import com.meiqi.liduoo.fastweixin.api.entity.CardMemberInfo;

/**
 * @author peiyu
 */
public class GetCardMemberInfoResponse extends BaseResponse {

	private List<CardMemberInfo> list;

	public List<CardMemberInfo> getList() {
		return list;
	}

	public void setList(List<CardMemberInfo> list) {
		this.list = list;
	}
}
