package com.zxp.nowcodercommunity.controller;

import com.zxp.nowcodercommunity.result.Result;
import com.zxp.nowcodercommunity.service.UserService;
import com.zxp.nowcodercommunity.util.CommunityUtil;
import com.zxp.nowcodercommunity.util.FileUpload;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    public UserController(FileUpload fileUpload, UserService userService) {
        this.fileUpload = fileUpload;
        this.userService = userService;
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


}
