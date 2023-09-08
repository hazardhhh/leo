package cn.hhh.server.service.impl;

import cn.hhh.server.common.BasicServiceModel;
import cn.hhh.server.constant.ResultConst;
import cn.hhh.server.entity.dto.ThsStockDto;
import cn.hhh.server.entity.res.buyerAttention.BuyerAttentionRes;
import cn.hhh.server.entity.res.buyerAttention.CorpNatureRes;
import cn.hhh.server.entity.res.buyerAttention.IndustryAttributeRes;
import cn.hhh.server.entity.vo.buyerAttention.GetBuyerAttentionVo;
import cn.hhh.server.logger.HdLog;
import cn.hhh.server.mapper.ThsInvestorActivitiesMapper;
import cn.hhh.server.mapper.ThsStockDetailsMapper;
import cn.hhh.server.mapper.ThsStockMapper;
import cn.hhh.server.page.BasicPage;
import cn.hhh.server.page.BasicPageRes;
import cn.hhh.server.redis.CacheFactory;
import cn.hhh.server.service.BuyerAttentionService;
import cn.hhh.server.utils.ThsScoreUtils;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import io.vavr.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Description BuyerAttentionImpl
 * @Author HHH
 * @Date 2023/9/3 4:35
 */
@Service
public class BuyerAttentionImpl implements BuyerAttentionService {

    private static final HdLog log = HdLog.getInstance();

    @Autowired
    private ThsInvestorActivitiesMapper thsInvestorActivitiesMapper;

    @Autowired
    private ThsStockMapper thsStockMapper;

    @Autowired
    private ThsStockDetailsMapper thsStockDetailsMapper;

    /**
     * 获取行业属性列表
     */
    @Override
    public BasicServiceModel getIndustryAttribute() {
        String industryAttributeList = (String) CacheFactory.getCache().get("industry_attribute_list");
        if (industryAttributeList != null) {
            List<IndustryAttributeRes> industryAttributeResList = JSON.parseArray(industryAttributeList, IndustryAttributeRes.class);
            return BasicServiceModel.ok(industryAttributeResList);
        }

        List<ThsStockDto> thsStockDtoList = thsStockMapper.getResult();
        if (thsStockDtoList == null) {
            return BasicServiceModel.ok(new ArrayList<>());
        }

        List<IndustryAttributeRes> industryAttributeResList = new ArrayList<>();

        // 添加全部节点
        IndustryAttributeRes allRes = new IndustryAttributeRes();
        allRes.setValue("全部");
        allRes.setLabel("全部");
        allRes.setChildren(new ArrayList<>());
        industryAttributeResList.add(allRes);

        Map<String, IndustryAttributeRes> firstSwIndustryMap = new LinkedHashMap<>();
        Map<String, IndustryAttributeRes> secondSwIndustryMap = new LinkedHashMap<>();
        Map<String, IndustryAttributeRes> thirdSwIndustryMap = new LinkedHashMap<>();

        for (ThsStockDto thsStockDto : thsStockDtoList) {
            String firstSwIndustry = thsStockDto.getFirstSwIndustry();
            String secondSwIndustry = thsStockDto.getSecondSwIndustry();
            String thirdSwIndustry = thsStockDto.getThirdSwIndustry();

            IndustryAttributeRes firstSwIndustryRes = firstSwIndustryMap.get(firstSwIndustry);
            if (firstSwIndustryRes == null) {
                firstSwIndustryRes = new IndustryAttributeRes();
                firstSwIndustryRes.setValue(firstSwIndustry);
                firstSwIndustryRes.setLabel(firstSwIndustry);
                firstSwIndustryRes.setChildren(new ArrayList<>());
                firstSwIndustryMap.put(firstSwIndustry, firstSwIndustryRes);
                industryAttributeResList.add(firstSwIndustryRes);
            }

            IndustryAttributeRes secondSwIndustryRes = secondSwIndustryMap.get(secondSwIndustry);
            if (secondSwIndustryRes == null) {
                secondSwIndustryRes = new IndustryAttributeRes();
                secondSwIndustryRes.setValue(secondSwIndustry);
                secondSwIndustryRes.setLabel(secondSwIndustry);
                secondSwIndustryRes.setChildren(new ArrayList<>());
                secondSwIndustryMap.put(secondSwIndustry, secondSwIndustryRes);
                firstSwIndustryRes.getChildren().add(secondSwIndustryRes);
            }

            IndustryAttributeRes thirdSwIndustryRes = thirdSwIndustryMap.get(thirdSwIndustry);
            if (thirdSwIndustryRes == null) {
                thirdSwIndustryRes = new IndustryAttributeRes();
                thirdSwIndustryRes.setValue(thirdSwIndustry);
                thirdSwIndustryRes.setLabel(thirdSwIndustry);
                thirdSwIndustryRes.setChildren(new ArrayList<>());
                thirdSwIndustryMap.put(thirdSwIndustry, thirdSwIndustryRes);
                secondSwIndustryRes.getChildren().add(thirdSwIndustryRes);
            }
        }

        // 添加慧得智慧行业节点
        IndustryAttributeRes hdIndustryRes = new IndustryAttributeRes();
        hdIndustryRes.setValue("慧得智慧行业");
        hdIndustryRes.setLabel("慧得智慧行业");
        hdIndustryRes.setChildren(new ArrayList<>());
        industryAttributeResList.add(hdIndustryRes);

        CacheFactory.getCache().set("industry_attribute_list", JSON.toJSONString(industryAttributeResList), 24 * 60 * 60);
        return BasicServiceModel.ok(industryAttributeResList);
    }

    /**
     * 获取企业属性列表
     */
    @Override
    public BasicServiceModel getCorpNature() {
        String corpNatureList = (String) CacheFactory.getCache().get("corp_nature_list");
        if (corpNatureList != null) {
            List<CorpNatureRes> corpNatureResList = JSON.parseArray(corpNatureList, CorpNatureRes.class);
            return BasicServiceModel.ok(corpNatureResList);
        }

        List<ThsStockDto> thsStockDtoList = thsStockMapper.getResult();
        if (thsStockDtoList == null) {
            return BasicServiceModel.ok(new ArrayList<>());
        }
        List<CorpNatureRes> corpNatureResList = new ArrayList<>();
        Map<String, CorpNatureRes> corpNatureMap = new LinkedHashMap<>();
        for (ThsStockDto thsStockDto : thsStockDtoList) {
            String corpNature = thsStockDto.getCorpNature();
            CorpNatureRes corpNatureRes = corpNatureMap.get(corpNature);
            if (corpNatureRes == null) {
                corpNatureRes = new CorpNatureRes();
                corpNatureRes.setValue(corpNature);
                corpNatureRes.setLabel(corpNature);
                corpNatureMap.put(corpNature, corpNatureRes);
                corpNatureResList.add(corpNatureRes);
            }
        }
        CacheFactory.getCache().set("corp_nature_list", JSON.toJSONString(corpNatureResList), 24 * 60 * 60);
        return BasicServiceModel.ok(corpNatureResList);
    }

    /**
     * 获取买方关注度指标数据
     */
    @Override
    public BasicServiceModel<BasicPageRes> getData(GetBuyerAttentionVo getBuyerAttentionVo) {
        log.info("BuyerAttentionImpl | getData | getBuyerAttentionVo = {}", getBuyerAttentionVo);
        long startTime1 = System.currentTimeMillis();
        // 买方调研次数近一周
        List<Map<String, Object>> researchedFrequencyByWeekList = thsInvestorActivitiesMapper.getResearchedFrequencyByWeek(getBuyerAttentionVo.getDataTime());
        // 买方调研机构数近一周
        List<Map<String, Object>> researchedOrgCountByWeekList = thsInvestorActivitiesMapper.getResearchedOrgCountByWeek(getBuyerAttentionVo.getDataTime());
        // 买方调研人数近一周
        List<Map<String, Object>> researchedPeopleNumByWeekList = thsInvestorActivitiesMapper.getResearchedPeopleNumByWeek(getBuyerAttentionVo.getDataTime());
        // 买方调研次数近一个月
        List<Map<String, Object>> researchedFrequencyByMonthList = thsInvestorActivitiesMapper.getResearchedFrequencyByMonth(getBuyerAttentionVo.getDataTime());
        // 买方调研机构数近一个月
        List<Map<String, Object>> researchedOrgCountByMonthList = thsInvestorActivitiesMapper.getResearchedOrgCountByMonth(getBuyerAttentionVo.getDataTime());
        // 买方调研人数近一个月
        List<Map<String, Object>> researchedPeopleNumByMonthList = thsInvestorActivitiesMapper.getResearchedPeopleNumByMonth(getBuyerAttentionVo.getDataTime());
        // 前十大持仓公募基金数量（季度）
        List<Map<String, Object>> mutualFundCountByTopTenList = thsStockDetailsMapper.getMutualFundCountByTopTen(ThsScoreUtils
                .getQuarterDate(DateUtil.parse(getBuyerAttentionVo.getDataTime(), "yyyy/MM/dd"), 2));
        // 持仓公募基金数量（半年）
        List<Map<String, Object>> mutualFundCountList = thsStockDetailsMapper.getMutualFundCount(getBuyerAttentionVo.getDataTime());
        // 返回结果list
        List<BuyerAttentionRes> buyerAttentionResResult;
        // 获取全A证券代码，证券名称，申万行业，公司性质
        List<ThsStockDto> thsStockDtoList = thsStockMapper.getBuyerAttentionResult();
        List<BuyerAttentionRes> buyerAttentionResList = new ArrayList<>();
        for (ThsStockDto thsStockDto : thsStockDtoList) {
            BuyerAttentionRes buyerAttentionRes = new BuyerAttentionRes();
            buyerAttentionRes.setStockCode(thsStockDto.getThsStockCode());
            buyerAttentionRes.setStockName(thsStockDto.getStockName());
            buyerAttentionRes.setFirstIndustry(thsStockDto.getFirstSwIndustry());
            buyerAttentionRes.setSecondIndustry(thsStockDto.getSecondSwIndustry());
            buyerAttentionRes.setThirdIndustry(thsStockDto.getThirdSwIndustry());
            buyerAttentionRes.setCorpNature(thsStockDto.getCorpNature());
            buyerAttentionResList.add(buyerAttentionRes);
        }

        for (BuyerAttentionRes buyerAttentionRes : buyerAttentionResList) {
            String stockCode = buyerAttentionRes.getStockCode();

            researchedFrequencyByWeekList.stream()
                    .filter(map -> map.containsKey("stockCode") && map.get("stockCode").equals(stockCode))
                    .findFirst()
                    .ifPresent(map -> buyerAttentionRes.setResearchedFrequencyByWeek((long) map.getOrDefault("researchedFrequencyByWeek", 0L)));

            researchedOrgCountByWeekList.stream()
                    .filter(map -> map.containsKey("stockCode") && map.get("stockCode").equals(stockCode))
                    .findFirst()
                    .ifPresent(map -> buyerAttentionRes.setResearchedOrgCountByWeek((long) map.getOrDefault("researchedOrgCountByWeek", 0L)));

            researchedPeopleNumByWeekList.stream()
                    .filter(map -> map.containsKey("stockCode") && map.get("stockCode").equals(stockCode))
                    .findFirst()
                    .ifPresent(map -> buyerAttentionRes.setResearchedPeopleNumByWeek((long) map.getOrDefault("researchedPeopleNumByWeek", 0L)));

            researchedFrequencyByMonthList.stream()
                    .filter(map -> map.containsKey("stockCode") && map.get("stockCode").equals(stockCode))
                    .findFirst()
                    .ifPresent(map -> buyerAttentionRes.setResearchedFrequencyByMonth((long) map.getOrDefault("researchedFrequencyByMonth", 0L)));

            researchedOrgCountByMonthList.stream()
                    .filter(map -> map.containsKey("stockCode") && map.get("stockCode").equals(stockCode))
                    .findFirst()
                    .ifPresent(map -> buyerAttentionRes.setResearchedOrgCountByMonth((long) map.getOrDefault("researchedOrgCountByMonth", 0L)));

            researchedPeopleNumByMonthList.stream()
                    .filter(map -> map.containsKey("stockCode") && map.get("stockCode").equals(stockCode))
                    .findFirst()
                    .ifPresent(map -> buyerAttentionRes.setResearchedPeopleNumByMonth((long) map.getOrDefault("researchedPeopleNumByMonth", 0L)));

            mutualFundCountByTopTenList.stream()
                    .filter(map -> map.containsKey("stockCode") && map.get("stockCode").equals(stockCode))
                    .findFirst()
                    .ifPresent(map -> buyerAttentionRes.setGetMutualFundCountByTopTen((long) map.getOrDefault("getMutualFundCountByTopTen", 0L)));

            mutualFundCountList.stream()
                    .filter(map -> map.containsKey("stockCode") && map.get("stockCode").equals(stockCode))
                    .findFirst()
                    .ifPresent(map -> buyerAttentionRes.setGetMutualFundCount((long) map.getOrDefault("getMutualFundCount", 0L)));

        }

        buyerAttentionResList
                .forEach(res -> {
                    if (res.getResearchedFrequencyByWeek() == null) {
                        res.setResearchedFrequencyByWeek(0L);
                    }
                    if (res.getResearchedOrgCountByWeek() == null) {
                        res.setResearchedOrgCountByWeek(0L);
                    }
                    if (res.getResearchedPeopleNumByWeek() == null) {
                        res.setResearchedPeopleNumByWeek(0L);
                    }
                    if (res.getResearchedFrequencyByMonth() == null) {
                        res.setResearchedFrequencyByMonth(0L);
                    }
                    if (res.getResearchedOrgCountByMonth() == null) {
                        res.setResearchedOrgCountByMonth(0L);
                    }
                    if (res.getResearchedPeopleNumByMonth() == null) {
                        res.setResearchedPeopleNumByMonth(0L);
                    }
                    if (res.getGetMutualFundCountByTopTen() == null) {
                        res.setGetMutualFundCountByTopTen(0L);
                    }
                    if (res.getGetMutualFundCount() == null) {
                        res.setGetMutualFundCount(0L);
                    }
                });

        long endTime1 = System.currentTimeMillis();
        long executionTime1 = endTime1 - startTime1;

        log.info("方法执行时间1：" + executionTime1 + "毫秒");

        long startTime3 = System.currentTimeMillis();

        //使用Collectors.groupingBy()方法将buyerAttentionResList进行分组
        Map<?, List<BuyerAttentionRes>> groupMap;
        if (getBuyerAttentionVo.getIndustryAttributeType() == 0) {
            groupMap = Collections.singletonMap("all", buyerAttentionResList);
        } else if (getBuyerAttentionVo.getIndustryAttributeType() == 1) {
            groupMap = buyerAttentionResList.stream().collect(Collectors.groupingBy(result -> Tuple.of(result.getFirstIndustry(), result.getCorpNature())));
        } else if (getBuyerAttentionVo.getIndustryAttributeType() == 2) {
            groupMap = buyerAttentionResList.stream().collect(Collectors.groupingBy(result -> Tuple.of(result.getSecondIndustry(), result.getCorpNature())));
        } else if (getBuyerAttentionVo.getIndustryAttributeType() == 3) {
            groupMap = buyerAttentionResList.stream().collect(Collectors.groupingBy(result -> Tuple.of(result.getThirdIndustry(), result.getCorpNature())));
        } else {
            log.info("BuyerAttentionImpl | getData | industryAttribute = {}", getBuyerAttentionVo.getIndustryAttributeType());
            return BasicServiceModel.error(ResultConst.FS_UNKNOWN, ResultConst.FS_UNKNOWN.name());
        }

        for (Map.Entry<?, List<BuyerAttentionRes>> entry : groupMap.entrySet()) {
            List<BuyerAttentionRes> groupList = null;
            groupList = entry.getValue();
            // 被调研次数近一周
            List<Double> researchedFrequencyByWeek = groupList.stream()
                    .map(BuyerAttentionRes::getResearchedFrequencyByWeek)
                    .map(Long::doubleValue)
                    .collect(Collectors.toList());
            // 调研机构个数近一周
            List<Double> researchedOrgCountByWeek = groupList.stream()
                    .map(BuyerAttentionRes::getResearchedOrgCountByWeek)
                    .map(Long::doubleValue)
                    .collect(Collectors.toList());
            // 调研人数近一周
            List<Double> researchedPeopleNumByWeek = groupList.stream()
                    .map(BuyerAttentionRes::getResearchedPeopleNumByWeek)
                    .map(Long::doubleValue)
                    .collect(Collectors.toList());
            // 被调研次数近一个月
            List<Double> researchedFrequencyByMonth = groupList.stream()
                    .map(BuyerAttentionRes::getResearchedFrequencyByMonth)
                    .map(Long::doubleValue)
                    .collect(Collectors.toList());
            // 调研机构个数近一个月
            List<Double> researchedOrgCountByMonth = groupList.stream()
                    .map(BuyerAttentionRes::getResearchedOrgCountByMonth)
                    .map(Long::doubleValue)
                    .collect(Collectors.toList());
            // 调研人数近一个月
            List<Double> researchedPeopleNumByMonth = groupList.stream()
                    .map(BuyerAttentionRes::getResearchedPeopleNumByMonth)
                    .map(Long::doubleValue)
                    .collect(Collectors.toList());
            // 前十大持仓公募基金数量（季度）
            List<Double> getMutualFundCountByTopTen = groupList.stream()
                    .map(BuyerAttentionRes::getGetMutualFundCountByTopTen)
                    .map(Long::doubleValue)
                    .collect(Collectors.toList());
            // 持仓公募基金数量（半年）
            List<Double> getMutualFundCount = groupList.stream()
                    .map(BuyerAttentionRes::getGetMutualFundCount)
                    .map(Long::doubleValue)
                    .collect(Collectors.toList());
            for (BuyerAttentionRes res : buyerAttentionResList) {
                    // 计算排名百分比及得分
                String researchedFrequencyByWeekPCT = ThsScoreUtils.rankPct((double)res.getResearchedFrequencyByWeek(), researchedFrequencyByWeek);
                String researchedFrequencyByWeekScore = null;
                String researchedOrgCountByWeekPCT = ThsScoreUtils.rankPct((double)res.getResearchedOrgCountByWeek(), researchedOrgCountByWeek);
                String researchedOrgCountByWeekScore = null;
                String researchedPeopleNumByWeekPCT = ThsScoreUtils.rankPct((double)res.getResearchedPeopleNumByWeek(), researchedPeopleNumByWeek);
                String researchedPeopleNumByWeekScore = null;
                String researchedFrequencyByMonthPCT = ThsScoreUtils.rankPct((double)res.getResearchedFrequencyByMonth(), researchedFrequencyByMonth);
                String researchedFrequencyByMonthScore = null;
                String researchedOrgCountByMonthPCT = ThsScoreUtils.rankPct((double)res.getResearchedOrgCountByMonth(), researchedOrgCountByMonth);
                String researchedOrgCountByMonthScore = null;
                String researchedPeopleNumByMonthPCT = ThsScoreUtils.rankPct((double)res.getResearchedPeopleNumByMonth(), researchedPeopleNumByMonth);
                String researchedPeopleNumByMonthScore = null;
                String getMutualFundCountByTopTenPCT = ThsScoreUtils.rankPct((double)res.getGetMutualFundCountByTopTen(), getMutualFundCountByTopTen);
                String getMutualFundCountByTopTenScore = null;
                String getMutualFundCountPCT = ThsScoreUtils.rankPct((double)res.getGetMutualFundCount(), getMutualFundCount);
                String getMutualFundCountScore = null;
                if (getBuyerAttentionVo.getRankTactics() == 0) {
                    researchedFrequencyByWeekScore = String.valueOf(ThsScoreUtils.industryRankPct10(Double.valueOf(researchedFrequencyByWeekPCT)));
                    researchedOrgCountByWeekScore = String.valueOf(ThsScoreUtils.industryRankPct10(Double.valueOf(researchedOrgCountByWeekPCT)));
                    researchedPeopleNumByWeekScore = String.valueOf(ThsScoreUtils.industryRankPct10(Double.valueOf(researchedPeopleNumByWeekPCT)));
                    researchedFrequencyByMonthScore = String.valueOf(ThsScoreUtils.industryRankPct10(Double.valueOf(researchedFrequencyByMonthPCT)));
                    researchedOrgCountByMonthScore = String.valueOf(ThsScoreUtils.industryRankPct10(Double.valueOf(researchedOrgCountByMonthPCT)));
                    researchedPeopleNumByMonthScore = String.valueOf(ThsScoreUtils.industryRankPct10(Double.valueOf(researchedPeopleNumByMonthPCT)));
                    getMutualFundCountByTopTenScore = String.valueOf(ThsScoreUtils.industryRankPct10(Double.valueOf(getMutualFundCountByTopTenPCT)));
                    getMutualFundCountScore = String.valueOf(ThsScoreUtils.industryRankPct10(Double.valueOf(getMutualFundCountPCT)));
                } else if (getBuyerAttentionVo.getRankTactics() == 1) {
                    researchedFrequencyByWeekScore = String.valueOf(ThsScoreUtils.industryRankPct20(Double.valueOf(researchedFrequencyByWeekPCT)));
                    researchedOrgCountByWeekScore = String.valueOf(ThsScoreUtils.industryRankPct20(Double.valueOf(researchedOrgCountByWeekPCT)));
                    researchedPeopleNumByWeekScore = String.valueOf(ThsScoreUtils.industryRankPct20(Double.valueOf(researchedPeopleNumByWeekPCT)));
                    researchedFrequencyByMonthScore = String.valueOf(ThsScoreUtils.industryRankPct20(Double.valueOf(researchedFrequencyByMonthPCT)));
                    researchedOrgCountByMonthScore = String.valueOf(ThsScoreUtils.industryRankPct20(Double.valueOf(researchedOrgCountByMonthPCT)));
                    researchedPeopleNumByMonthScore = String.valueOf(ThsScoreUtils.industryRankPct20(Double.valueOf(researchedPeopleNumByMonthPCT)));
                    getMutualFundCountByTopTenScore = String.valueOf(ThsScoreUtils.industryRankPct20(Double.valueOf(getMutualFundCountByTopTenPCT)));
                    getMutualFundCountScore = String.valueOf(ThsScoreUtils.industryRankPct20(Double.valueOf(getMutualFundCountPCT)));
                }
                res.setResearchedFrequencyByWeekScore(researchedFrequencyByWeekScore);
                res.setResearchedOrgCountByWeekScore(researchedOrgCountByWeekScore);
                res.setResearchedPeopleNumByWeekScore(researchedPeopleNumByWeekScore);
                res.setResearchedFrequencyByMonthScore(researchedFrequencyByMonthScore);
                res.setResearchedOrgCountByMonthScore(researchedOrgCountByMonthScore);
                res.setResearchedPeopleNumByMonthScore(researchedPeopleNumByMonthScore);
                res.setGetMutualFundCountByTopTenScore(getMutualFundCountByTopTenScore);
                res.setGetMutualFundCountScore(getMutualFundCountScore);
                // 计算加权总分设置排名
                int score = 0;
                if (getBuyerAttentionVo.getType() == 0) {
                    score = (int) (res.getResearchedFrequencyByWeek() * 3
                            + res.getResearchedOrgCountByWeek() * 2
                            + res.getResearchedPeopleNumByWeek()
                            + res.getResearchedFrequencyByMonth() * 3
                            + res.getResearchedOrgCountByMonth() * 2
                            + res.getResearchedPeopleNumByMonth()
                            + res.getGetMutualFundCountByTopTen() * 2
                            + res.getGetMutualFundCount());
                } else if (getBuyerAttentionVo.getType() == 1) {
                    score = (Integer.parseInt(res.getResearchedFrequencyByWeekScore()) * 3
                            + Integer.parseInt(res.getResearchedOrgCountByWeekScore()) * 2
                            + Integer.parseInt(res.getResearchedPeopleNumByWeekScore())
                            + Integer.parseInt(res.getResearchedFrequencyByMonthScore()) * 3
                            + Integer.parseInt(res.getResearchedOrgCountByMonthScore()) * 2
                            + Integer.parseInt(res.getResearchedPeopleNumByMonthScore())
                            + Integer.parseInt(res.getGetMutualFundCountByTopTenScore()) * 2
                            + Integer.parseInt(res.getGetMutualFundCountScore()));
                }
                res.setRank(score);
            }
        }

        long endTime3 = System.currentTimeMillis();
        long executionTime3 = endTime3 - startTime3;

        log.info("方法执行时间3：" + executionTime3 + "毫秒");

        BasicPageRes res = new BasicPageRes();
        if (buyerAttentionResList.size() == 0) {
            return BasicServiceModel.error(ResultConst.FS_UNKNOWN, ResultConst.FS_UNKNOWN.name());
        }
        BasicPage basicPage = new BasicPage(getBuyerAttentionVo.getPageNo(), getBuyerAttentionVo.getPageSize());
        buyerAttentionResResult = buyerAttentionResList.stream()
                .filter(result -> (result.getStockCode() != null && result.getStockCode().contains(getBuyerAttentionVo.getKeyWord()))
                        || (result.getStockName() != null && result.getStockName().contains(getBuyerAttentionVo.getKeyWord())))
                .filter(result -> {
                    List<String> industryAttributes = getBuyerAttentionVo.getIndustryAttribute();
                    if (industryAttributes.isEmpty()) {
                        return true;
                    }
                    return industryAttributes.stream().anyMatch(attr -> (result.getFirstIndustry() != null && result.getFirstIndustry().contains(attr))
                            || (result.getSecondIndustry() != null && result.getSecondIndustry().contains(attr))
                            || (result.getThirdIndustry() != null && result.getThirdIndustry().contains(attr)));
                })
                .filter(result -> {
                    List<String> corpNature = getBuyerAttentionVo.getCorpNature();
                    if (corpNature.isEmpty()) {
                        return true;
                    }
                    return corpNature.stream().anyMatch(attr -> (result.getCorpNature() != null && result.getCorpNature().contains(attr)));
                })
                .sorted(Comparator.comparingInt(BuyerAttentionRes::getRank).reversed())
                .skip((long) (getBuyerAttentionVo.getPageNo() - 1) * getBuyerAttentionVo.getPageSize())
                .limit(getBuyerAttentionVo.getPageSize())
                .collect(Collectors.toList());
        IntStream.range(0, buyerAttentionResResult.size())
                .forEach(i -> buyerAttentionResResult.get(i).setRank(i + 1));
        int totalCount = (int) buyerAttentionResList.stream()
                .filter(result -> (result.getStockCode() != null && result.getStockCode().contains(getBuyerAttentionVo.getKeyWord()))
                        || (result.getStockName() != null && result.getStockName().contains(getBuyerAttentionVo.getKeyWord())))
                .filter(result -> {
                    List<String> industryAttributes = getBuyerAttentionVo.getIndustryAttribute();
                    if (industryAttributes.isEmpty()) {
                        return true;
                    }
                    return industryAttributes.stream().anyMatch(attr -> (result.getFirstIndustry() != null && result.getFirstIndustry().contains(attr))
                            || (result.getSecondIndustry() != null && result.getSecondIndustry().contains(attr))
                            || (result.getThirdIndustry() != null && result.getThirdIndustry().contains(attr)));
                })
                .filter(result -> {
                    List<String> corpNature = getBuyerAttentionVo.getCorpNature();
                    if (corpNature.isEmpty()) {
                        return true;
                    }
                    return corpNature.stream().anyMatch(attr -> (result.getCorpNature() != null && result.getCorpNature().contains(attr)));
                })
                .count();
        int totalPageSum = (int) Math.ceil((double) totalCount / getBuyerAttentionVo.getPageSize());
        basicPage.setTotalCount((long) totalCount);
        basicPage.setTotalPageNum(totalPageSum);
        res.setPage(basicPage);
        res.setVar(buyerAttentionResResult);
        log.info("BuyerAttentionImpl | getData | buyerAttentionResResult = {}", buyerAttentionResResult);
        return BasicServiceModel.ok(res);
    }

}
