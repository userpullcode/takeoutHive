package com.hive.takeout.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ToString
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)//插入的时候自动填充
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)//插入和更新的时候自动填充
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)//插入的时候自动填充
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)//插入和更新的时候自动填充
    private Long updateUser;

}
