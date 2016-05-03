package com.meiqi.liduoo.fastweixin.api.response;

import java.util.List;

/**
 * @author Nottyjay
 */
public class GetUserInfoListResponse extends  BaseResponse{
	private static final long serialVersionUID = 8896984957210600472L;
	private List<GetUserInfoResponse> user_info_list;

    public List<GetUserInfoResponse> getUser_info_list() {
        return user_info_list;
    }

    public void setUser_info_list(List<GetUserInfoResponse> user_info_list) {
        this.user_info_list = user_info_list;
    }
}
