package cn.itcast.baobiao;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class PoiTest {

    //创建Excel
    @Test
    public void createExcel() throws IOException {
        //1.创建工作簿
        XSSFWorkbook wb=new XSSFWorkbook();//2007版本
        //2.创建表单sheet
        XSSFSheet sheet = wb.createSheet("test");
        //3.文件流
        OutputStream out=new FileOutputStream("C:\\Users\\pf\\Desktop\\poi\\test.xlsx");
        //4.写入文件
        wb.write(out);
        out.close();
    }

    //创建单元格
    @Test
    public void createCell() throws IOException {
        //1.创建工作簿
        XSSFWorkbook wb=new XSSFWorkbook();
        //2.创建表单sheet
        XSSFSheet sheet = wb.createSheet("test");
        //3.创建行对象 索引从0开始
        XSSFRow row = sheet.createRow(2);//第三行
        //4.创建单元格 索引从0开始
        XSSFCell cell = row.createCell(2);
        //5.向单元格写入内容
        cell.setCellValue("我真帅");
        //6.文件流
        OutputStream out=new FileOutputStream("C:\\Users\\pf\\Desktop\\poi\\test2.xlsx");
        //7.写入文件
        wb.write(out);
        out.close();
    }

    //设置单元格样式
    @Test
    public void setStyle() throws IOException {
        //1.创建工作簿
        XSSFWorkbook wb=new XSSFWorkbook();
        //2.创建表单sheet
        XSSFSheet sheet = wb.createSheet("test");
        //3.创建行对象 索引从0开始
        XSSFRow row = sheet.createRow(2);
        //4.创建单元格 索引从0开始
        XSSFCell cell = row.createCell(2);

        cell.setCellValue("我真帅");

        //样式处理
        //5.创建单元格样式对象
        XSSFCellStyle style = wb.createCellStyle();
        //设置上边框为细线
        style.setBorderTop(BorderStyle.THIN);
        //设置下边框
        style.setBorderBottom(BorderStyle.THIN);
        //设置左边框
        style.setBorderLeft(BorderStyle.THIN);
        //设置右边框
        style.setBorderRight(BorderStyle.THIN);

        //6.创建字体对象
        XSSFFont font = wb.createFont();
        //设置字体
        font.setFontName("华文行楷");
        //设置字号
        font.setFontHeightInPoints((short)18);
        style.setFont(font);

        //设置行高和列宽
        //行高
        row.setHeightInPoints(28);
        //索引从0开始,列宽31
        sheet.setColumnWidth(2,31*256);

        //居中显示
        //水平居中
        style.setAlignment(HorizontalAlignment.CENTER);
        //垂直居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        //将样式设置进单元格
        cell.setCellStyle(style);

        //文件流
        OutputStream out=new FileOutputStream("C:\\Users\\pf\\Desktop\\poi\\test3.xlsx");
        //写入文件
        wb.write(out);
        out.close();

    }

    //插入图片
    @Test
    public void insertPicture() throws IOException {
        //1.创建工作簿
        XSSFWorkbook wb=new XSSFWorkbook();
        //2.创建表单sheet
        XSSFSheet sheet = wb.createSheet("test");
        //3.读取图片流
        FileInputStream in=new FileInputStream("C:\\Users\\pf\\Desktop\\poi\\01.jpg");
        //图片流转化为二进制数组
        byte[] bytes = IOUtils.toByteArray(in);
        in.read(bytes);
        //向poi内存中添加一张图片,返回图片在图片集合中的索引
        //参数1:图片的二进制数组 参数2:图片的类型
        int index = wb.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
        //绘制图片工具类
        XSSFCreationHelper helper = wb.getCreationHelper();
        //创建一个绘图对象
        XSSFDrawing patriarch = sheet.createDrawingPatriarch();
        //创建瞄点,设置图片坐标
        XSSFClientAnchor anchor = helper.createClientAnchor();
        //从第0行开始
        anchor.setRow1(0);
        anchor.setCol1(0);
        //绘制图片
        //参数1:图片位置 参数2:图片在图片集合中的索引
        XSSFPicture picture = patriarch.createPicture(anchor, index);
        //自适应渲染图片
        picture.resize();
        //4.文件流
        OutputStream out=new FileOutputStream("C:\\Users\\pf\\Desktop\\poi\\test4.xlsx");
        //5.写入文件
        wb.write(out);
        out.close();
    }

    //加载Excel并解析
    //sheet.getLastRowNum()得到的是最后一行的索引
    //row.getLastCellNum()得到的是最后一列的号码
    @Test
    public void loadExcel() throws IOException {
        //1.根据Excel文件获取工作簿2007
        XSSFWorkbook wb=new XSSFWorkbook("C:\\Users\\pf\\Desktop\\poi\\demo.xlsx");
        //2.获取sheet
        //索引从0开始
        XSSFSheet sheet = wb.getSheetAt(0);
        //3.获取sheet中的每一行和每一列
        for (int i = 0; i <=sheet.getLastRowNum() ; i++) {
            XSSFRow row = sheet.getRow(i);
            StringBuilder sb=new StringBuilder();
            for (int j = 0; j <row.getLastCellNum() ; j++) {
                XSSFCell cell = row.getCell(j);
                if (cell==null){
                    continue;
                }
                Object value = getCellValue(cell);
                if(value!=null){
                    sb.append(value).append("\t");
                }
            }
            System.out.println(sb.toString());
        }

    }

    private Object getCellValue(Cell cell){
        //得到单元格属性类型
        CellType cellType = cell.getCellType();
        //根据单元格数据类型获取数据
        Object value=null;
        switch (cellType){
            case STRING:
                value=cell.getStringCellValue();
                break;
            case BOOLEAN:
                value=cell.getBooleanCellValue();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)){
                    //日期格式
                    value=cell.getDateCellValue();
                }else {
                    //数字格式
                    value=cell.getNumericCellValue();
                }
                break;
            case FORMULA: //公式
                value=cell.getCellFormula();
                break;
            default:
                break;
        }
        return value;
    }
}
