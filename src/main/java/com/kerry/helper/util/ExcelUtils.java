package com.kerry.helper.util;

import com.kerry.helper.annotation.ExcelReadField;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * **********书山有路勤为径**********
 * excel utils
 * @author k1rry
 * @date 2020/7/11
 * **********学海无涯苦作舟**********
 */
public class ExcelUtils {

    private ExcelUtils() { }

    private static Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    private static final String XLS = "xls";

    private static final String XLSX = "xlsx";

    /**
     * read excel
     * @param filePath file path
     * @param clazz return class type
     * @param <T>
     * @return
     */
    public static <T> List<T> readExcel(String filePath, Class<T> clazz) {
        File file = new File(filePath);
        if (!file.exists()) {
            logger.warn("file not exists:{}", filePath);
            return new ArrayList<>();
        }
        String fileName = file.getName();
        logger.info("fileName:{}", fileName);
        String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
        logger.info("fileType:{}", fileType);
        InputStream inputStream;
        Map<String, String> fieldAnnotation = ClassUtils.fieldAnnotation(clazz, ExcelReadField.class);
        Map<Integer, String> fieldMap = new HashMap<>();
        List<T> list = new ArrayList<>();
        try {
            inputStream = new FileInputStream(file);
            Workbook workbook = getWorkbook(inputStream, fileType);
            if (workbook == null) {
                return new ArrayList<>();
            }
            for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if (sheet == null) {
                    continue;
                }
                int firstRowNum = sheet.getFirstRowNum();
                int lastRowNum = sheet.getLastRowNum();
                int j = firstRowNum;
                for (int i = firstRowNum; i <= lastRowNum; i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }
                    j = i+1;
                    for (int num = row.getFirstCellNum(); num <= row.getLastCellNum(); num++) {
                        Cell cell = row.getCell(num);
                        if (cell == null) {
                            continue;
                        }
                        String s = cell.getStringCellValue();
                        String field = fieldAnnotation.get(cell.getStringCellValue());
                        if (StringUtils.isNotBlank(field)) {
                            fieldMap.put(num, field);
                        }
                    }
                    break;
                }
                for (int i = j; i <= lastRowNum; i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }
                    T t = clazz.newInstance();
                    for (int num = row.getFirstCellNum(); num <= row.getLastCellNum(); num++) {
                        Cell cell = row.getCell(num);
                        if (cell == null) {
                            continue;
                        }
                        String fieldName = fieldMap.get(num);
                        if (StringUtils.isBlank(fieldName)) {
                            continue;
                        }
                        Field field = t.getClass().getDeclaredField(fieldName);
                        field.setAccessible(true);
                        field.set(t, cell.getStringCellValue());
                    }
                    list.add(t);
                }
            }
        } catch (FileNotFoundException e) {
            logger.warn("file not found:{}", filePath);
        } catch (IOException e) {
            logger.error("{}", e.getMessage(), e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static Workbook getWorkbook(final InputStream inputStream, String fileType) throws IOException {
        Workbook workbook = null;
        if (XLS.equals(fileType)) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (XLSX.equals(fileType)) {
            workbook = new XSSFWorkbook(inputStream);
        }
        return workbook;
    }

    public static void outputFile(String filePath) {

    }

}
