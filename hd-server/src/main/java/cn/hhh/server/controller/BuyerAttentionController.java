package cn.hhh.server.controller;

import cn.hhh.server.common.BasicServiceModel;
import cn.hhh.server.entity.vo.buyerAttention.GetBuyerAttentionVo;
import cn.hhh.server.page.BasicPageRes;
import cn.hhh.server.service.BuyerAttentionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 买方关注度
 * @Author HHH
 * @Date 2023/9/3 4:31
 */
@Api(tags = "买方关注度")
@RestController
@RequestMapping("/buyerAttention")
public class BuyerAttentionController {

    @Autowired
    private BuyerAttentionService buyerAttentionService;

    @PostMapping("/getIndustryAttribute")
    @ApiOperation(value = "获取行业属性列表")
    @ApiImplicitParam(value = "请求流水id", name = "tid", paramType = "query", dataTypeClass = String.class)
    public BasicServiceModel<Object> getIndustryAttribute() {
        return buyerAttentionService.getIndustryAttribute();
    }

    @PostMapping("/getCorpNature")
    @ApiOperation(value = "获取企业属性列表")
    @ApiImplicitParam(value = "请求流水id", name = "tid", paramType = "query", dataTypeClass = String.class)
    public BasicServiceModel<Object> getCorpNature() {
        return buyerAttentionService.getCorpNature();
    }

    @PostMapping("/getData")
    @ApiOperation(value = "获取买方关注度指标数据")
    @ApiImplicitParam(value = "请求流水id", name = "tid", paramType = "query", dataTypeClass = String.class)
    public BasicServiceModel<BasicPageRes> getData(@RequestBody GetBuyerAttentionVo getBuyerAttentionVo) {
        return buyerAttentionService.getData(getBuyerAttentionVo);
    }

}
