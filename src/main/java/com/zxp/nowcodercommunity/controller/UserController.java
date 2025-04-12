package com.zxp.nowcodercommunity.controller;

import com.zxp.nowcodercommunity.pojo.User;
import com.zxp.nowcodercommunity.result.Result;
import com.zxp.nowcodercommunity.service.LikeService;
import com.zxp.nowcodercommunity.service.UserService;
import com.zxp.nowcodercommunity.util.CommunityUtil;
import com.zxp.nowcodercommunity.util.FileUpload;
import com.zxp.nowcodercommunity.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    // 构造函数注入
    private final FileUpload fileUpload;
    private final UserService userService;
    private final LikeService likeService;
    public UserController(FileUpload fileUpload, UserService userService, LikeService likeService) {
        this.fileUpload = fileUpload;
        this.userService = userService;
        this.likeService = likeService;
    }

    /**
     *  实现文件上传接口
     *  思路大概是前端用户上传完图片后，我们需要获取这个资源的url地址，然后修改数据库里面的url地址即可
     *  难点在于，我们怎么知道是哪个用户上传的头像呢
     *
     */
    @PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) {
        log.info("Uploading file: " + file.getOriginalFilename());
        Map<String, Object> data = new HashMap<>();
        // 文件判断
        if (file.isEmpty()) {
            data.put("errosMsg", "File is empty");
            return Result.error(data, "文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        if (!originalFilename.contains(".")) {
            data.put("errosMsg", "File is not a valid format");
            return Result.error(data, "文件不正确");
        }
        String suffex = originalFilename.substring(originalFilename.lastIndexOf("."));// 得到后缀名
        if (StringUtils.isBlank(suffex)) {
            data.put("errosMsg", "File is empty");
            return Result.error(data, "文件名不能为空");
        }
        if (file.getSize() > 1024 * 1024 * 5) {
            data.put("errosMsg", "文件大小太大了");
            return Result.error(data, "文件大小不能大于5M");
        }

        // 文件的新名称为UUID加前缀
        String newFileName = CommunityUtil.generateUUID() + suffex; // UUID+后缀

        String fileUrl = fileUpload.upload(file, newFileName);

        // 更新用户的url
        Integer rows = userService.updateUserHeaderUrl(fileUrl);
        if (rows < 1) {
            data.put("errosMsg", "上传失败");
            return Result.error(data);
        }

        data.put("fileUrl", fileUrl);
        return Result.success(data);
    }

    /**
     * 查询用户收到的赞
     * @param userId
     * @return
     */
    @GetMapping("/{userId}/like")
    public Result<Object> likeCount(@PathVariable("userId") int userId) {
        Long cnt = likeService.findUserLikeCount(userId);
        return Result.success(cnt);
    }

    /**
     *  查询用户的个人信息
     * @param userId
     * @return
     */
    @GetMapping("/{userId}/info")
    public Result<Object> userInfo(@PathVariable("userId") int userId) {
        User userById = userService.getUserById(userId);
        if (userById == null) {
            return Result.error("用户不存在");
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userById, userVo);
        return Result.success(userVo);
    }




}
