package com.zxp.nowcodercommunity.controller;

import com.zxp.nowcodercommunity.pojo.DiscussPost;
import com.zxp.nowcodercommunity.pojo.Page;
import com.zxp.nowcodercommunity.pojo.User;
import com.zxp.nowcodercommunity.result.Result;
import com.zxp.nowcodercommunity.service.HomeService;
import com.zxp.nowcodercommunity.service.UserService;
import com.zxp.nowcodercommunity.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/index")
public class HomeController {

    private final HomeService homeService;

    private final UserService userService;

    public HomeController(HomeService homeService, UserService userService) {
        this.homeService = homeService;
        this.userService = userService;
    }

    @GetMapping
    public Result<List<Map<String, Object>>> getDiscussPosts(@RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "5") int limit, @RequestParam(defaultValue = "0") int orderMode) {
        // 构造 Page 对象并初始化属性
        Page page = new Page(current, limit, homeService.getTotalDiscussPosts(0));

        // 获取帖子列表
        List<DiscussPost> list = homeService.getDiscussPosts(0, page.getCurrent(), page.getLimit(), 0);

        // 使用 Stream API 简化数据封装
        List<Map<String, Object>> data = list.stream().map(post -> {
            Map<String, Object> map = new HashMap<>();
            map.put("post", post);

            // 获取用户信息并拷贝属性
            User user = userService.getUserById(post.getUserId());
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            map.put("user", userVo);

            return map;
        }).collect(Collectors.toList());

        return Result.success(data);
    }


    @GetMapping("/count")
    public Result<Integer> countDiscussPosts() {
        return Result.success(homeService.getTotalDiscussPosts(0));
    }
}
