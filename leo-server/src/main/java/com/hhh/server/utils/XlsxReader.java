package com.hhh.server.utils;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XlsxReader {

    public static void main(String[] args) {
        String fileName = "F:\\打分\\04.打分算法\\申万一级企业属性.xlsx";
        String url = "jdbc:mysql://127.0.0.1:3306/hhh?useSSL=false&serverTimezone=GMT";
        String user = "root";
        String password = "111111";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            // 1. 获取数据库连接
            conn = DriverManager.getConnection(url, user, password);
            // 2. 准备 SQL 语句
            String sql = "INSERT INTO ths_score (stock_code, stock_name, sw_first, corp_attr) VALUES (?, ?, ?, ?)";
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
                        if (header.equals("证券代码") || header.equals("证券名称") || header.equals("申万一级") || header.equals("企业属性")) {
                            colIndexList.add(cell.getColumnIndex());
                        }
                    }
                } else { // 读取数据行
                    for (int i = 0; i < colIndexList.size(); i++) {
                        int colIndex = colIndexList.get(i);
                        Cell cell = row.getCell(colIndex);
                        if (cell != null) {
                            String value = cell.getStringCellValue();
                            if (value == null) {
                                break;
                            }
                            stmt.setString(i + 1, value);
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