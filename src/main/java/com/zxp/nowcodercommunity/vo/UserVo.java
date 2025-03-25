package com.zxp.nowcodercommunity.vo;

// 把用户信息封装到UserVo里面，这样可以方便参数传递

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class UserVo {
    private Integer id;

    private String username;

    private String headerUrl;

    private Byte type;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHeaderUrl() {
        return headerUrl;
    }

    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public UserVo() {
    }

    public UserVo(Integer id, String username, String headerUrl, Byte type, LocalDateTime createTime) {
        this.id = id;
        this.username = username;
        this.headerUrl = headerUrl;
        this.type = type;
        this.createTime = createTime;
    }
}

