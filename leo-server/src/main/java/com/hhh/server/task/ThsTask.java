package com.hhh.server.task;

import com.hhh.server.logger.LeoLog;
import com.hhh.server.service.ThsScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

/**
 * @Description ThsTask
 * @Author HHH
 * @Date 2023/6/11 1:35
 */
@Component
public class ThsTask {

    private static final LeoLog log = LeoLog.getInstance();

    @Value("${leo.task.enable:false}")
    private boolean taskEnable;

    @Autowired
    private ThsScoreService thsScoreService;

    /**
     * 新增同花顺预测评级,机构预测明细表
     * 每天晚上 11,18,23点58分开始插入
     *
     */
    @Scheduled(cron = "${leo.task.insertThsMemoir:0 58 11,18,23 * * ?}")
    public void insertThsMemoirTask(){
        if (taskEnable) {
            log.info("ThsTask insertThsMemoirTask");
            thsScoreService.insertThsMemoir(false);
        }
    }

    /**
     * 新增同花顺预测评级,机构预测明细表
     * 每周三周日23点30执行 补全数据
     *
     */
    @Scheduled(cron = "${leo.task.insertThsMemoirByWeek:0 30 23 ? * WED,SUN}")
    public void insertThsMemoirTaskByWeek(){
        if (taskEnable) {
            log.info("ThsTask insertThsMemoirTaskByWeek");
            thsScoreService.insertThsMemoir(true);
        }
    }

    /**
     * 同花顺公司资料,用户行为统计表
     * 每天晚上 23点50分开始插入
     *
     */
    @Scheduled(cron = "${leo.task.insertThsBehavior:0 50 23 * * ?}")
    public void insertThsBehavior(){
        if (taskEnable) {
            log.info("ThsTask insertThsBehavior");
            thsScoreService.insertThsBehavior();
        }
    }

    /**
     * 同花顺公司资料,个股人气表
     * 每天晚上 23点55分开始插入
     *
     */
    @Scheduled(cron = "${leo.task.insertThsHot:0 55 23 * * ?}")
    public void insertThsHot(){
        if (taskEnable) {
            log.info("ThsTask insertThsHot");
            thsScoreService.insertThsHot();
        }
    }

}
