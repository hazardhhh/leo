package com.hhh.server.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hhh
 * @since 2023-05-18
 */
@Data
@ApiModel("更新密码实体类")
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePwdReq {

    @ApiModelProperty(value = "adminId")
    private Integer adminId;

    @ApiModelProperty(value = "旧密码")
    private String oldPass;

    @ApiModelProperty(value = "新密码")
    private String pass;

}
