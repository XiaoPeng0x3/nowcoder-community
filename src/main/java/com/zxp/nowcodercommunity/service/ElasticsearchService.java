package com.zxp.nowcodercommunity.service;

import com.zxp.nowcodercommunity.pojo.DiscussPost;
import org.springframework.data.domain.Page;

public interface ElasticsearchService {
    /**
     * 保存
     * @param post
     */
    void saveDiscussPost(DiscussPost post);

    /**
     * 根据id删除帖子
     * @param id
     */
    void deleteDiscussPost(int id);

    /**
     * 根据关键词进行分页搜索
     * @param keyword
     * @param currentPage
     * @param pageSize
     * @return
     */
    Page<DiscussPost> searchDiscussPost(String keyword, int currentPage, int pageSize);
}
