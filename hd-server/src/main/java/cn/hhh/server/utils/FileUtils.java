package cn.hhh.server.utils;

import cn.hutool.core.io.FileUtil;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description FileUtils
 * @Author HHH
 * @Date 2023/7/19 17:35
 */
public class FileUtils {

    private FileUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final List<String> EXCEL_SUFFIX_ARRAY = Arrays.asList("xls", "xlsx");

    /**
     * 判断文件名是否是Excel扩展名
     * @param fileName
     * @return {@link boolean}
     */
    public static boolean isExcel(String fileName){
        if(StringUtils.isEmpty(fileName)){
            return Boolean.FALSE;
        }

        final String suffix = FileUtil.getSuffix(fileName);
        return EXCEL_SUFFIX_ARRAY.contains(suffix);
    }

    /**
     * 生成导出Excel文件名
     * 拼上时间戳
     * @param fileNamePrefix
     * @return {@link String}
     */
    public static String generateExcelFileName(String fileNamePrefix){
        return fileNamePrefix + new Date().getTime() + ".xlsx";
    }

}
