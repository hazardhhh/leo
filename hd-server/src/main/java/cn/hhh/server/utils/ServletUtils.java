package cn.hhh.server.utils;

import cn.hhh.server.handler.CustemExcelhandler;
import com.alibaba.excel.EasyExcelFactory;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import cn.hhh.server.logger.HdLog;
import org.springframework.http.HttpHeaders;

/**
 * @Description ServletUtils
 * @Author HHH
 * @Date 2023/7/19 17:28
 */
public class ServletUtils {

    private static final HdLog log = HdLog.getInstance();

    private ServletUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 输出Excel文件
     * @param response
     * @param rows
     * @param clazz
     * @param fileName
     * @return
     */
    public static void outputExcel(HttpServletResponse response, List<?> rows, Class<?> clazz, String fileName){
        response.setContentType("application/vnd.ms-exce");
        response.setCharacterEncoding("utf-8");
        ServletUtils.setDownloadHeader(response, fileName);
        try (final ServletOutputStream outputStream = response.getOutputStream()) {
            EasyExcelFactory.write(outputStream, clazz).registerWriteHandler(new CustemExcelhandler()).sheet("sheet").doWrite(rows);
        } catch (Exception e){
            log.error("export error", e);
        }
    }

    /**
     * 设置下载文件头
     * @param response
     * @param fileName
     * @return
     */
    public static void setDownloadHeader(HttpServletResponse response, String fileName){
        response.addHeader("Content-Disposition", "filename=" + fileName);
        response.addHeader("Access-Control-Expose-Headers", "content-disposition");
    }

    /**
     * 设置下载文件头
     * @param headers
     * @param fileName
     * @return
     */
    public static void setDownloadHeader(HttpHeaders headers, String fileName){
        headers.add("Content-Disposition", "attachment;filename=" + fileName);
        headers.add("Access-Control-Expose-Headers", "content-disposition");
    }

}
