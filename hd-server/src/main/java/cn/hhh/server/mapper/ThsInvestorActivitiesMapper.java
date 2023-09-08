package cn.hhh.server.mapper;

import java.util.List;
import java.util.Map;

/**
 * @Description ThsInvestorActivitiesMapper
 * @Author HHH
 * @Date 2023/8/28 9:07
 */
public interface ThsInvestorActivitiesMapper {

    /**
     * 买方调研次数近一周
     *
     * @return
     */
    List<Map<String, Object>> getResearchedFrequencyByWeek(String dataTime);

    /**
     * 买方调研机构数近一周
     *
     * @return
     */
    List<Map<String, Object>> getResearchedOrgCountByWeek(String dataTime);

    /**
     * 买方调研人数近一周
     *
     * @return
     */
    List<Map<String, Object>> getResearchedPeopleNumByWeek(String dataTime);

    /**
     * 买方调研次数近一个月
     *
     * @return
     */
    List<Map<String, Object>> getResearchedFrequencyByMonth(String dataTime);

    /**
     * 买方调研机构数近一个月
     *
     * @return
     */
    List<Map<String, Object>> getResearchedOrgCountByMonth(String dataTime);

    /**
     * 买方调研人数近一个月
     *
     * @return
     */
    List<Map<String, Object>> getResearchedPeopleNumByMonth(String dataTime);

}
