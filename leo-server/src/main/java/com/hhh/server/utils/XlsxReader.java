package com.hhh.server.utils;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XlsxReader {

    public static void main1(String[] args) {
        String fileName = "F:\\打分\\04.打分算法\\申万二级企业属性.xlsx";
        String url = "jdbc:mysql://127.0.0.1:3306/hhh?useSSL=false&serverTimezone=GMT";
        String user = "root";
        String password = "111111";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            // 1. 获取数据库连接
            conn = DriverManager.getConnection(url, user, password);
            // 2. 准备 SQL 语句
            String sql = "UPDATE ths_score SET sw_second = ? WHERE stock_code = ?";
            stmt = conn.prepareStatement(sql);
            // 3. 读取 xlsx 文件
            System.out.println("start");
            FileInputStream fis = new FileInputStream(fileName);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            List<Integer> colIndexList = new ArrayList<Integer>();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() == 0) { // 第一行为表头
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        String header = cell.getStringCellValue().trim();
                        if (header.equals("证券代码") || header.equals("申万二级")) {
                            colIndexList.add(cell.getColumnIndex());
                        }
                    }
//                    System.out.println(colIndexList);
                } else {
                    // 读取数据行
                    String stockCode = null;
//                    String dividend_2022 = null;
                    String swSecond = null;
                    for (int i = 0; i < colIndexList.size(); i++) {
                        int colIndex = colIndexList.get(i);
                        Cell cell = row.getCell(colIndex);
                        if (cell != null) {
                            if (cell.getCellType() == CellType.NUMERIC) {
                                double value = cell.getNumericCellValue();
                                DecimalFormat df = new DecimalFormat("0.0000");
                                String formattedValue = df.format(value);
//                                System.out.println(formattedValue);
//                                if (i == 1) {
//                                    dividend_2022 = formattedValue;
//                                }
                                stmt.setString(i + 1, formattedValue);
                            } else {
                                String value = cell.getStringCellValue();
//                                System.out.println(value);
                                if (i == 0) {
                                    stockCode = value;
                                }
                                if (i == 1) {
                                    swSecond = value;
                                }
                                if (value == null) {
                                    break;
                                }
                                stmt.setString(i + 1, value);
                            }
                        } else {
                            stmt.setNull(i + 1, java.sql.Types.VARCHAR);
                        }
                    }
                    System.out.println(stockCode + " " + swSecond);
                    if (stockCode != null && swSecond != null) {
                        stmt.setString(1, swSecond);
                        stmt.setString(2, stockCode);
                        stmt.executeUpdate();
                    }
                }
            }
            // 4. 关闭资源
            workbook.close();
            fis.close();
            stmt.close();
            conn.close();
            System.out.println("end");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String fileName = "F:\\国信金工\\【国信金工】6月券商金股统计_50家.xlsx";
        String url = "jdbc:mysql://127.0.0.1:3306/hhh?useSSL=false&serverTimezone=GMT";
        String user = "root";
        String password = "111111";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            // 1. 获取数据库连接
            conn = DriverManager.getConnection(url, user, password);
            // 2. 准备 SQL 语句
            String sql = "INSERT INTO gxjg (date, securities_name, stock_code, stock_name, release_date, specific_time, data_source) VALUES (?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            // 3. 读取 xlsx 文件
            System.out.println("start");
            FileInputStream fis = new FileInputStream(fileName);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            List<Integer> colIndexList = new ArrayList<Integer>();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() == 0) { // 第一行为表头
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        String header = cell.getStringCellValue().trim();
                        if (header.equals("日期") || header.equals("券商名称") || header.equals("股票代码") || header.equals("股票名称")
                                || header.equals("发布日期") || header.equals("具体时间") || header.equals("数据来源")) {
                            colIndexList.add(cell.getColumnIndex());
                        }
                    }
//                    System.out.println(colIndexList);
                } else {
                    // 读取数据行
                    for (int i = 0; i < colIndexList.size(); i++) {
                        int colIndex = colIndexList.get(i);
                        Cell cell = row.getCell(colIndex);
                        if (cell != null) {
                            if (cell.getCellType() == CellType.NUMERIC) {
                                double value = cell.getNumericCellValue();
//                                DecimalFormat df = new DecimalFormat("0.0000");
//                                String formattedValue = df.format(value);
//                                System.out.println(formattedValue);
//                                stmt.setString(i + 1, formattedValue);
                                // 国信金工时间格式转化
                                String formattedValue = "";
                                if (value <= 1) {
                                    int hours = (int) (value * 24);  // 计算时间小时数
                                    int minutes = (int) ((value * 24 - hours) * 60);  // 计算时间分钟数
                                    formattedValue = String.format("%02d:%02d", hours, minutes);
                                } else {
                                    formattedValue = String.format("%08.0f", value);
                                }
                                stmt.setString(i + 1, formattedValue);
                            } else {
                                String value = cell.getStringCellValue();
//                                System.out.println(value);
                                if (value == null) {
                                    break;
                                }
                                stmt.setString(i + 1, value);
                            }
                        } else {
                            stmt.setNull(i + 1, java.sql.Types.VARCHAR);
                        }
                    }
                    stmt.executeUpdate();
                }
            }
            // 4. 关闭资源
            workbook.close();
            fis.close();
            stmt.close();
            conn.close();
            System.out.println("end");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}