package com.cs.inje.of;

import java.io.*;
    import org.apache.poi.ss.usermodel.Workbook;
    import org.apache.poi.xssf.usermodel.XSSFCell;
    import org.apache.poi.xssf.usermodel.XSSFRow;
    import org.apache.poi.xssf.usermodel.XSSFSheet;
    import org.apache.poi.xssf.usermodel.XSSFWorkbook;
    import java.util.ArrayList;

    public class JavaExcel
    {
		String[] strings;
        public ArrayList<String[]> readExcel(String filePath) throws IOException
        {
            ArrayList<String[]> returnArrayList = new ArrayList<String[]>();
            FileInputStream fis=new FileInputStream(filePath);
            XSSFWorkbook workbook=new XSSFWorkbook(fis);
            int rowindex=0;
            int columnindex=0;

            XSSFSheet sheet=workbook.getSheetAt(0);

            int rows=sheet.getPhysicalNumberOfRows();
            for(rowindex=0;rowindex<rows;rowindex++)
            {

                XSSFRow row=sheet.getRow(rowindex);
                if(row !=null)
                {
                    //���� ��
                    int cells=row.getPhysicalNumberOfCells();
                    strings = new String[cells];
                    for(columnindex=0;columnindex<=cells;columnindex++)
                    {

                        XSSFCell cell=row.getCell(columnindex);
                        String value=new String("");

                        //���� ���ϰ�츦 ���� ��üũ
                        if(cell==null)
                        {
                            continue;
                        }
                        else
                        {

                            switch (cell.getCellType()){
                            case XSSFCell.CELL_TYPE_FORMULA:
                                value=cell.getCellFormula();
                                break;
                            case XSSFCell.CELL_TYPE_NUMERIC:
                                value=cell.getNumericCellValue()+"";
                                break;
                            case XSSFCell.CELL_TYPE_STRING:
                                value=cell.getStringCellValue()+"";
                                break;
                            case XSSFCell.CELL_TYPE_BLANK:
                                value=cell.getBooleanCellValue()+"";
                                break;
                            case XSSFCell.CELL_TYPE_ERROR:
                                value=cell.getErrorCellValue()+"";
                                break;
                            }
                        }
                        strings[columnindex] = value;

                    }
                }
                returnArrayList.add(strings);
            }

            return returnArrayList;
        }
    }



