package com.meiqi.app.service;

import java.util.List;

import com.meiqi.app.pojo.Comment;

public interface CommentService {
    List<Comment> getGoodsComments(Comment commentParam);
}
