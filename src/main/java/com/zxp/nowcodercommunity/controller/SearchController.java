package com.zxp.nowcodercommunity.controller;

import com.zxp.nowcodercommunity.pojo.DiscussPost;
import com.zxp.nowcodercommunity.pojo.User;
import com.zxp.nowcodercommunity.result.Result;
import com.zxp.nowcodercommunity.service.ElasticsearchService;
import com.zxp.nowcodercommunity.service.LikeService;
import com.zxp.nowcodercommunity.service.UserService;
import com.zxp.nowcodercommunity.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.zxp.nowcodercommunity.constant.LoginConstant.ENTITY_TYPE_POST;

@RestController
@RequestMapping("search")
public class SearchController {

    //ES服务
    private final ElasticsearchService elasticsearchService;
    // userService服务
    private final UserService userService;
    // 查询帖子的like数目
    private final LikeService likeService;

    public SearchController(ElasticsearchService elasticsearchService, UserService userService, LikeService likeService) {
        this.elasticsearchService = elasticsearchService;
        this.userService = userService;
        this.likeService = likeService;
    }


    /**
     * 根据关键词来实现搜索
     */
    @GetMapping
    public Result<Object> search(@RequestParam(value = "keyword", defaultValue = "") String keyword,
                                 @RequestParam(value = "current", defaultValue = "1") int current,
                                 @RequestParam(value = "limit", defaultValue = "5") int limit) {
        // 根据关键词进行搜索，每个对象都是discussPost类型
        Page<DiscussPost> searchDiscussPost = elasticsearchService.searchDiscussPost(keyword, current, limit);
        // 把信息存储起来返回
        List<Map<String, Object>> discussPosts = searchDiscussPost.stream().map(discussPost -> {
            // 创建vo
            Map<String, Object> discussPostVo = new HashMap<>();
            // 将帖子，帖子作者，帖子点赞数返回
            discussPostVo.put("post", discussPost);
            // 帖子的作者
            UserVo userVo = new UserVo();
            User userById = userService.getUserById(discussPost.getUserId());
            if (userById != null) {
                BeanUtils.copyProperties(userById, userVo);
            }
            // user
            discussPostVo.put("user", userVo);
            // 点赞数
            Long entityLikeCount = likeService.findEntityLikeCount((int) ENTITY_TYPE_POST, discussPost.getId());
            discussPostVo.put("likeCount", entityLikeCount);
            return discussPostVo;
        }).toList();

        Map<String, Object> discussPostPage = new HashMap<>();
        discussPostPage.put("records", discussPosts);
        discussPostPage.put("total", searchDiscussPost.getTotalElements());
        discussPostPage.put("current", current);
        discussPostPage.put("size", limit);


        return Result.success(discussPostPage);
    }
}
