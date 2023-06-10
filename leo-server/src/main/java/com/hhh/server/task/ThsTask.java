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

    @Value("${ths.task.enalbe:false}")
    private boolean taskEnable;

    @Autowired
    private ThsScoreService thsScoreService;

    /**
     * 新增同花顺预测评级,机构预测明细表
     * 每天晚上 11,18,23点58分开始插入
     *
     */
    @Scheduled(cron = "${ths.task.insertThsMemoir:0 58 11,18,23 * * ?}")
    public void insertThsMemoirTask(){
        if (taskEnable) {
            log.info("ThsTask insertThsMemoirTask");
            thsScoreService.insertThsMemoir();
        }
    }

}
