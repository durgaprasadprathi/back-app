package com.appmodz.executionmodule.util;

import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

public class ExcelUtil {

    private XSSFWorkbook xssfWorkbook;

    public ExcelUtil() {
        xssfWorkbook = new XSSFWorkbook();
    }

    public ExcelUtil(InputStream inputStream) throws Exception{
        xssfWorkbook = new XSSFWorkbook(inputStream);
    }

    public XSSFWorkbook createSheet(String sheetName, Object[][] data) {
        XSSFSheet sheet = xssfWorkbook.createSheet(sheetName);
        for(int i=0;i< data.length;i++) {
            Row row = sheet.createRow(i);
            for(int j=0;j<data[i].length;j++) {
                Cell cell = row.createCell(j);
                Object obj = data[i][j];
                if(obj instanceof String)
                    cell.setCellValue((String) obj);
                else if (obj instanceof Long)
                    cell.setCellValue((Long) obj);
                else if(obj instanceof Date) {
                    CellStyle cellStyle = xssfWorkbook.createCellStyle();
                    CreationHelper createHelper = xssfWorkbook.getCreationHelper();
                    cellStyle.setDataFormat(
                            createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
                    cell.setCellValue((Date) obj);
                    cell.setCellStyle(cellStyle);
                }

                else if(obj instanceof Boolean)
                    cell.setCellValue((Boolean) obj);
                else if(obj==null)
                    cell.setBlank();
            }
        }
        return xssfWorkbook;
    }

    public Object[][] readSheet(String sheetName) {
        XSSFSheet sheet = xssfWorkbook.getSheet(sheetName);
        int noOfRows = sheet.getLastRowNum() + 1;
        int noOfColumns = sheet.getRow(0).getLastCellNum();
        Object[][] dataTable = new Object[noOfRows][noOfColumns];
        for (int i = sheet.getFirstRowNum(); i < sheet.getLastRowNum() + 1; i++) {
            Row row = sheet.getRow(i);
            for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                switch(cell.getCellType()) {
                    case NUMERIC:
                        if(DateUtil.isCellDateFormatted(cell))
                            dataTable[i][j] = cell.getDateCellValue();
                        else {
                            Double numericCellValue = cell.getNumericCellValue();
                            if(Math.floor(numericCellValue) == numericCellValue)
                                dataTable[i][j] = numericCellValue.longValue();
                            else
                                dataTable[i][j] = numericCellValue;
                        }
                        break;
                    case BOOLEAN:
                        dataTable[i][j] = cell.getBooleanCellValue();
                        break;
                    case STRING:
                        dataTable[i][j] = cell.getStringCellValue();
                    case BLANK:
                        dataTable[i][j] = null;
                    case _NONE:
                        dataTable[i][j] = null;
                }
            }
        }
        return dataTable;
    }

}
