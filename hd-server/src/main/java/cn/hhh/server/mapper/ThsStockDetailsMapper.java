package cn.hhh.server.mapper;

import java.util.List;
import java.util.Map;

/**
 * @Description ThsStockDetailsMapper
 * @Author HHH
 * @Date 2023/8/29 21:20
 */
public interface ThsStockDetailsMapper {

    /**
     * 前十大持仓公募基金数量（季度）
     *
     * @return
     */
    List<Map<String, Object>> getMutualFundCountByTopTen(String dataTime);

    /**
     * 持仓公募基金数量（半年）
     *
     * @return
     */
    List<Map<String, Object>> getMutualFundCount(String dataTime);

}
