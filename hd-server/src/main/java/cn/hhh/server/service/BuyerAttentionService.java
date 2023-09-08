package cn.hhh.server.service;

import cn.hhh.server.common.BasicServiceModel;
import cn.hhh.server.entity.vo.buyerAttention.GetBuyerAttentionVo;
import cn.hhh.server.page.BasicPageRes;

/**
 * @Description BuyerAttentionService
 * @Author HHH
 * @Date 2023/9/3 4:33
 */
public interface BuyerAttentionService {

    /**
     * 获取行业属性列表
     */
    BasicServiceModel<Object> getIndustryAttribute();

    /**
     * 获取企业属性列表
     */
    BasicServiceModel<Object> getCorpNature();

    /**
     * 获取买方关注度指标数据
     */
    BasicServiceModel<BasicPageRes> getData(GetBuyerAttentionVo getBuyerAttentionVo);

}
