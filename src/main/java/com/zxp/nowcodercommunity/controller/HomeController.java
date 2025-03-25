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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/community/index")
public class HomeController {

    @Autowired
    private HomeService homeService;

    @Autowired
    private UserService userService;

    @GetMapping
    public Result<List<Map<String, Object>>> getDiscussPosts(@RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "5")int limit, @RequestParam(defaultValue = "0")int orderMode) {

        // 构造page
        Page page = new Page();
        page.setCurrent(current);
        page.setLimit(limit);
        page.setRows(homeService.getTotalDiscussPosts(0));
        List<DiscussPost> list = homeService.getDiscussPosts(0, page.getCurrent(), page.getLimit(), 0);
        List<Map<String,Object>> data = new ArrayList();
        for (DiscussPost post : list) {
            Map<String,Object> map = new HashMap<>();
            map.put("post", post);
            // 获取User对象
            User user = userService.getUserById(post.getUserId());
            // 将uer对象拷贝到vo里面
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            // 添加到data里面
            map.put("user", userVo);
            data.add(map);
        }
        return Result.success(data);
    }

    @GetMapping("/count")
    public Result<Integer> countDiscussPosts() {
        return Result.success(homeService.getTotalDiscussPosts(0));
    }
}
