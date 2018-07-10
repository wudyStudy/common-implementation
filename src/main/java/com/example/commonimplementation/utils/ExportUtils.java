package com.example.commonimplementation.utils;

import org.apache.http.client.utils.DateUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 */
public class ExportUtils {

    private static final String datePattern = "yyyy-MM-dd HH:mm:ss";

    public static <T> void exportToWebBook(IExcelSheetExporter<T> sheetExporter, HSSFWorkbook workbook) {

        HSSFSheet sheet = workbook.createSheet(sheetExporter.getSheetName());

        sheet.setDefaultColumnWidth(15);
        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);

        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < sheetExporter.getHeader().length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(sheetExporter.getHeader()[i]);
            cell.setCellValue(text);
        }

        if (null == workbook || null == sheetExporter || CollectionUtils.isEmpty(sheetExporter.getDataSet())) {
            return;
        }

        List<T> dataSet = sheetExporter.getDataSet();
        int index = 0;
        for (T item : dataSet) {
            index++;
            row = sheet.createRow(index);
            int j = 0;
            for (String fieldName : sheetExporter.getFieldNames()) {

                try {
                    Field field = ReflectionUtils.findField(item.getClass(), fieldName);
                    field.setAccessible(true);
                    Object res = field.get(item);
                    String textVal = extractValue(res);
                    HSSFCell cell = row.createCell(j++);
                    cell.setCellStyle(style);
                    HSSFRichTextString text = new HSSFRichTextString(textVal);
                    cell.setCellValue(text);
                } catch (IllegalAccessException e) {
                }

            }
        }


    }

    private static String extractValue(Object target) {
        if (target instanceof BigDecimal) {
            return ((BigDecimal) target).setScale(2).toString();
        } else if (target instanceof Date) {
            return DateUtils.formatDate((Date) target, datePattern);
        } else {
            return null == target ? "" : target.toString();
        }
    }
}
