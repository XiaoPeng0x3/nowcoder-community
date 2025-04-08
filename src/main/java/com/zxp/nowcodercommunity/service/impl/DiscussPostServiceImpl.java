package com.zxp.nowcodercommunity.service.impl;

import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.zxp.nowcodercommunity.mapper.DiscussPostMapper;
import com.zxp.nowcodercommunity.pojo.DiscussPost;
import com.zxp.nowcodercommunity.service.DiscussPostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    private static final Logger log = LoggerFactory.getLogger(DiscussPostServiceImpl.class);
    // 注入mapper
    private final DiscussPostMapper discussPostMapper;

    public DiscussPostServiceImpl(DiscussPostMapper discussPostMapper) {
        this.discussPostMapper = discussPostMapper;
    }


    @Override
    public Integer addDiscussPost(DiscussPost post) {
        // 参数检查
        if (post == null) {
            return 0; // 更新失败
        }
        // 处理标题和正文中的html标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        // 敏感词过滤
        post.setContent(SensitiveWordHelper.replace(post.getContent(), '*')); // 过滤正文的敏感词
        post.setTitle(SensitiveWordHelper.replace(post.getTitle(), '*')); // 过滤标题的敏感词

        // 将post插入数据库
        Integer rows = discussPostMapper.addDiscussPost(post);
        return rows;
    }

    @Override
    public DiscussPost findPostById(Integer id) {
        // 调用mapper层的接口
        DiscussPost postById = discussPostMapper.findPostById(id);
        return postById;
    }

    @Override
    public Integer findCountById(int userId) {
        return discussPostMapper.findCountById(userId);
    }

    @Override
    public List<DiscussPost> findPostsById(int userId, int current, int limit, int orderMode) {
        List<DiscussPost> ans = discussPostMapper.findPostsById(userId, current, limit, orderMode);
        log.info("ans {}", ans);
        return ans;
    }

    @Override
    public Integer updatePostType(Integer id, Integer type) {
        return discussPostMapper.updatePostType(id, type);
    }

    @Override
    public Integer updateStatus(int id, byte status) {
        return discussPostMapper.updateStatus(id, status);
    }
}
