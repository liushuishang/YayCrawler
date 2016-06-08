package yaycrawler.common.utils;

import org.apache.poi.xssf.usermodel.*;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/8.
 */
public class ExcelUtils {

    /**
     * 导出
     *
     * @param file     Excel文件(路径+文件名)，Excel文件不存在会自动创建
     * @param dataList 数据
     * @return
     */
    public static boolean exportCsv(File file,List<Map> dataList, ServletOutputStream outputStream) {
        boolean isSucess = false;
        // 创建一个workbook 对应一个excel应用文件
        XSSFWorkbook workBook = new XSSFWorkbook();
        // 在workbook中添加一个sheet,对应Excel文件中的sheet
        XSSFSheet sheet = workBook.createSheet("导出excel例子");
        ExportUtil exportUtil = new ExportUtil(workBook, sheet);
        XSSFCellStyle headStyle = exportUtil.getHeadStyle();
        XSSFCellStyle bodyStyle = exportUtil.getBodyStyle();
        // 构建表头
        XSSFRow headRow = sheet.createRow(0);
        XSSFCell cell = null;
        // 构建表体数据
        if (dataList != null && dataList.size() > 0) {
            for (int j = 0; j < dataList.size(); j++) {
                XSSFRow bodyRow = sheet.createRow(j + 1);
                Map<String, String> data = dataList.get(j);
                int i = 0;
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    if (j == 0) {
                        cell = headRow.createCell(i);
                        cell.setCellStyle(headStyle);
                        cell.setCellValue(entry.getKey());
                    }
                    cell = bodyRow.createCell(i);
                    cell.setCellStyle(bodyStyle);
                    cell.setCellValue(entry.getValue());
                    i++;
                }
            }
        }
        try {
            workBook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return isSucess;
        }
    }
    /**
     * 导入
     *
     * @param inputStream Excel文件(路径+文件)
     * @return
     */
    public static List<Map> importExcel(InputStream inputStream) {
        List<Map> dataList = new ArrayList<Map>();
        try {
            XSSFWorkbook workBook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workBook.getSheetAt(0);
            if (sheet != null) {
                XSSFRow keys = sheet.getRow(0);
                for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                    Map data = new HashMap();
                    XSSFRow rows = sheet.getRow(i);
                    for (int j = 0;j<keys.getPhysicalNumberOfCells();j++) {
                        String key = keys.getCell(j).toString().replaceAll("[\\s\\u00A0]+$", "");
                        String value = rows.getCell(j).toString().replaceAll("[\\s\\u00A0]+$", "");
                        data.put(key,value);
                    }
                    dataList.add(data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }
}

