package cn.hhh.server.entity.res.ths;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description TestRes
 * @Author HHH
 * @Date 2023/7/16 0:45
 */
@Data
@ApiModel("测试")
@AllArgsConstructor
@NoArgsConstructor
public class TestRes {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "author")
    private String author;

}
