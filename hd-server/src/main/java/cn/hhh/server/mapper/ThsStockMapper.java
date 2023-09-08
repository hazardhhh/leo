package cn.hhh.server.mapper;

import cn.hhh.server.entity.dto.ThsStockDto;
import cn.hhh.server.entity.res.buyerAttention.BuyerAttentionRes;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description ThsStockMapper
 * @Author HHH
 * @Date 2023/8/29 21:20
 */
public interface ThsStockMapper {

    /**
     * 获取全A股票代码
     *
     * @return
     */
    Set<String> getAllStockCode();

    /**
     * 获取所有结果
     *
     * @return
     */
    List<ThsStockDto> getResult();

    /**
     * 获取证券代码，证券名称，申万行业，公司性质
     *
     * @return
     */
    List<ThsStockDto> getBuyerAttentionResult();

}
