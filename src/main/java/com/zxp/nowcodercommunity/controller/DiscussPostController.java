package com.zxp.nowcodercommunity.controller;

import com.zxp.nowcodercommunity.annotation.AutoCreateTime;
import com.zxp.nowcodercommunity.pojo.Comment;
import com.zxp.nowcodercommunity.pojo.DiscussPost;
import com.zxp.nowcodercommunity.pojo.Page;
import com.zxp.nowcodercommunity.pojo.User;
import com.zxp.nowcodercommunity.result.Result;
import com.zxp.nowcodercommunity.security.util.SecurityUtil;
import com.zxp.nowcodercommunity.service.CommentService;
import com.zxp.nowcodercommunity.service.DiscussPostService;
import com.zxp.nowcodercommunity.service.UserService;
import com.zxp.nowcodercommunity.vo.UserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.zxp.nowcodercommunity.constant.LoginConstant.ENTITY_TYPE_COMMENT;
import static com.zxp.nowcodercommunity.constant.LoginConstant.ENTITY_TYPE_POST;

/**
 * 与帖子有关的controller
 */
@RestController
public class DiscussPostController {
    private static final Logger log = LoggerFactory.getLogger(DiscussPostController.class);

    // 注入service

    private final DiscussPostService discussPostService; // 帖子的service
    private final UserService userService; // User
    private final CommentService commentService;
    public DiscussPostController(DiscussPostService discussPostService, UserService userService, CommentService commentService) {
        this.discussPostService = discussPostService;
        this.userService = userService;
        this.commentService = commentService;
    }

    @AutoCreateTime // 设置创建时间的字段
    @PostMapping("discuss/add")
     public Result<Object> addDiscussPost(@RequestBody DiscussPost discussPost) throws Exception {
        // 从Security上下文里面拿到useId
        discussPost.setUserId(SecurityUtil.getUserId());
        discussPost.setType((byte) 0);
        discussPost.setStatus((byte) 0);
        discussPost.setCommentCount(0);
        // 初始分数
        discussPost.setScore(0.0);
        // 插入数据库
        Integer rows = discussPostService.addDiscussPost(discussPost);
        if (rows < 1) {
            throw new Exception("插入失败");
        }
        return Result.success("插入成功");
     }

    /**
     *  显示用户帖子的详情状况
     * @param id
     * @return
     */
    @GetMapping("discuss/detail/{id}")
    public Result<Object> getDiscussPost(@PathVariable Integer id) {
         Map<String, Object> data = new HashMap<>();
        // 根据帖子的id查询帖子
         DiscussPost post = discussPostService.findPostById(id);
         log.info("post: {}", post);
         data.put("discussPost", post);
         // 查询对应的用户
         User user = userService.getUserById(post.getUserId());
         UserVo userVo = new UserVo();
         BeanUtils.copyProperties(user, userVo);
         data.put("user", userVo);
         return Result.success(data);
     }

    // 实现帖子的回复功能
    // 回帖也就是将自己的评论显示在被评论用户下面
    // 同时还要将这个功能进行分页查询

    @GetMapping("discuss/detail/{id}/comment")
    public Result<Object> getDiscussPostDetail(@PathVariable("id") int id,
                                               @RequestParam("current") int current,
                                               @RequestParam("limit") int limit) {

        // 获取Page
        Page page = new Page();
        page.setLimit(limit); // 每页最大限制
        page.setCurrent(current); // 当前起始页
        page.setRows(discussPostService.findPostById(id).getCommentCount()); // 得到总的评论数量


        // 获取到所有的comment数据
        // id为帖子的id
        List<Comment> commentList = commentService.findCommentsByEntity(id, ENTITY_TYPE_POST, page.getOffset(),
                page.getLimit());

        // 构造数据
        List<Map<String, Object>> commentVoList = commentList.parallelStream().map(comment -> {
            // 每个List中的数据为comment
            Map<String, Object> commentVo = new HashMap<>();
            // 从这个comment里面得到数据
            // 添加帖子
            // 评论
            commentVo.put("comment", comment);
            // 作者
            User user = userService.getUserById(comment.getUserId());
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            commentVo.put("user", userVo);

            // 回复
            // 评论的评论
            // 得到当前评论的id，然后查询当前评论的评论 replyList
            List<Comment> replyList = commentService
                    .findCommentsByEntity(comment.getId(), ENTITY_TYPE_COMMENT, 0, Integer.MAX_VALUE);

            replyList.parallelStream().map(reply ->{
                Map<String, Object> replyVo = new HashMap<>();
                replyVo.put("reply", reply);
                User replyUser = userService.getUserById(reply.getUserId());
                UserVo replyUserVo = new UserVo();
                BeanUtils.copyProperties(replyUser, replyUserVo);
                replyVo.put("user", replyUserVo);

                // 回复的目标
                User targetUser = userService.getUserById(reply.getTargetId()); // 被回复的信息
                UserVo targetUserVo = new UserVo();
                BeanUtils.copyProperties(targetUser, targetUserVo);
                replyVo.put("target", targetUserVo);
                return replyVo;
            }).collect(Collectors.toList());
            commentVo.put("replys", replyList); // 把某个评论下面的评论封装起来
            // 评论的评论的数量
            Integer replyCount = commentService.findCommentCountByEntity(comment.getId(), ENTITY_TYPE_COMMENT);
            commentVo.put("count", replyCount);
            return commentVo;
        }).collect(Collectors.toList());


        return Result.success(commentVoList);

    }

    /**
     * 根据用户id分页查询
     * @return
     */
    @GetMapping("profile/{id}/post")
    public Result<List<Map<String, Object>>> getDiscussPostsById(@PathVariable("id") int userId,
                                                                 @RequestParam("current") int current,
                                                                 @RequestParam("limit") int limit) {
        // 获取Page
        Page page = new Page();
        page.setLimit(limit); // 每页最大限制
        page.setCurrent(current); // 当前起始页

        // 根据用户id查询他的所有帖子
        Integer countById = discussPostService.findCountById(userId);
        page.setRows(countById);

        // 得到这个用户的所有帖子
        List<DiscussPost> posts = discussPostService.findPostsById(userId, page.getOffset(), page.getLimit(), 0);
        log.info("posts: {}", posts);
        List<Map<String,Object>> postsVo = posts.parallelStream().map(post -> {
            Map<String, Object> postVo = new HashMap<>();
            postVo.put("post", post);
            User user = userService.getUserById(post.getUserId());
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            postVo.put("user", userVo);
            return postVo;
        }).toList();
        log.info("postVo" + postsVo);

        return Result.success(postsVo);
    }
}
