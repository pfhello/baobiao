package cn.itcast.baobiao.controller;

import cn.itcast.baobiao.poi.SheetHandler;
import cn.itcast.baobiao.pojo.EmUserCompanyPersonal;
import cn.itcast.baobiao.pojo.User;
import cn.itcast.baobiao.service.EmUserCompanyPersonalService;
import cn.itcast.baobiao.util.FileUtil;
import cn.itcast.baobiao.util.QiniuyunUploadUtil;
import cn.itcast.baobiao.vo.UserCount;
import cn.itcast.baobiao.vo.UserVo;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class HelloController {

    @Autowired
    private EmUserCompanyPersonalService emUserCompanyPersonalService;

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello";
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/upload")
    @ResponseBody
    public String upload(@RequestParam("fileName") MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        String ext = filename.substring(filename.lastIndexOf(".") + 1);
        if (!ext.equals("xlsx")) {
            return "文件格式错误";
        }
        XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = wb.getSheetAt(0);
        List<User> list = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            Object[] values = new Object[row.getLastCellNum()];
            int index = 0;
            for (int j = 0; j < row.getLastCellNum(); j++) {
                XSSFCell cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }
                Object value = getCellValue(cell);
                if (value != null) {
                    values[index++] = value;
                }
            }
            User user = new User(values);
            list.add(user);
        }
        System.out.println(list);
        return "导入成功";
    }

    private Object getCellValue(Cell cell) {
        //得到单元格属性类型
        CellType cellType = cell.getCellType();
        //根据单元格数据类型获取数据
        Object value = null;
        switch (cellType) {
            case STRING:
                value = cell.getStringCellValue();
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    //日期格式
                    value = cell.getDateCellValue();
                } else {
                    //数字格式
                    value = cell.getNumericCellValue();
                }
                break;
            case FORMULA: //公式
                value = cell.getCellFormula();
                break;
            default:
                break;
        }
        return value;
    }

    @GetMapping("/export")
    public void export(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<EmUserCompanyPersonal> list = emUserCompanyPersonalService.getEmUserCompanyPersonalList();
        SXSSFWorkbook wb = new SXSSFWorkbook();
        Sheet sheet = wb.createSheet();
        //标题
        Row titl = sheet.createRow(0);
        Cell titlCell = titl.createCell(0);
        titlCell.setCellValue("人员信息表");
        //合并单元格
        CellRangeAddress rangeAddress = new CellRangeAddress(0, 1, 0, 4);
        sheet.addMergedRegion(rangeAddress);
        //标题样式
        CellStyle titleStyle = wb.createCellStyle();
        Font titleFont = wb.createFont();
        titleFont.setFontName("等线");
        titleFont.setFontHeightInPoints((short) 22);
        titleStyle.setFont(titleFont);
        //居中显示
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titlCell.setCellStyle(titleStyle);

        //样式
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("等线");
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        //居中显示
        //水平居中
        style.setAlignment(HorizontalAlignment.CENTER);
        //垂直居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        String[] titles = {"用户Id", "用户姓名", "手机号码", "时间", "部门名称"};
        Row row = sheet.createRow(2);
        //行高
        row.setHeightInPoints(14.25F);
        AtomicInteger headerAi = new AtomicInteger();
        for (String title : titles) {
            //列宽
            sheet.setColumnWidth(headerAi.get(), 14 * 256);
            Cell cell = row.createCell(headerAi.getAndIncrement());
            cell.setCellValue(title);
            //将样式设置进单元格
            cell.setCellStyle(style);
        }
        AtomicInteger dataAi = new AtomicInteger(3);
        Cell cell = null;
        for (int i = 0; i < 10000; i++) {
            for (EmUserCompanyPersonal emUserCompanyPersonal : list) {
                Row dataRow = sheet.createRow(dataAi.getAndIncrement());
                //行高
                dataRow.setHeightInPoints(14.25F);
                cell = dataRow.createCell(0);
                cell.setCellValue(emUserCompanyPersonal.getUserId());
                cell.setCellStyle(style);
                cell = dataRow.createCell(1);
                cell.setCellValue(emUserCompanyPersonal.getUsername());
                cell.setCellStyle(style);
                cell = dataRow.createCell(2);
                cell.setCellValue(emUserCompanyPersonal.getMobile());
                cell.setCellStyle(style);
                cell = dataRow.createCell(3);
                cell.setCellValue(emUserCompanyPersonal.getTimeOfEntry());
                cell.setCellStyle(style);
                cell = dataRow.createCell(4);
                cell.setCellValue(emUserCompanyPersonal.getDepartmentName());
                cell.setCellStyle(style);
            }
        }
        //获取浏览器信息,对文件名进行重新编码
        String fileName = FileUtil.filenameEncoding("人员信息表.xlsx", request);
        //设置信息头
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setHeader("filename", fileName);
        wb.write(response.getOutputStream());
        wb.close();
    }

    @GetMapping("/exportMb")
    public void exportMb(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<EmUserCompanyPersonal> list = emUserCompanyPersonalService.getEmUserCompanyPersonalList();
        //加载模板流数据
        Resource resource = new ClassPathResource("excel-template/hr-demo.xlsx");
        FileInputStream in = new FileInputStream(resource.getFile());
        XSSFWorkbook wb = new XSSFWorkbook(in);
        Sheet sheet = wb.getSheetAt(0);
        Row titleRow = sheet.getRow(0);
        titleRow.getCell(0).setCellValue("人事报表");
        Row row = sheet.getRow(2);
        //取出数据样式
        CellStyle[] styles = new CellStyle[row.getLastCellNum()];
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell == null) {
                continue;
            }
            styles[i] = cell.getCellStyle();
        }
        Cell cell = null;
        AtomicInteger dataAi = new AtomicInteger(2);
        for (int i = 0; i < 10000; i++) {
            for (EmUserCompanyPersonal emUserCompanyPersonal : list) {
                Row dataRow = sheet.createRow(dataAi.getAndIncrement());
                cell = dataRow.createCell(0);
                cell.setCellValue(emUserCompanyPersonal.getUserId());
                cell.setCellStyle(styles[0]);
                cell = dataRow.createCell(1);
                cell.setCellValue(emUserCompanyPersonal.getUsername());
                cell.setCellStyle(styles[1]);
                cell = dataRow.createCell(2);
                cell.setCellValue(emUserCompanyPersonal.getMobile());
                cell.setCellStyle(styles[2]);
                cell = dataRow.createCell(3);
                cell.setCellValue(emUserCompanyPersonal.getTheHighestDegreeOfEducation());
                cell.setCellStyle(styles[3]);
                cell = dataRow.createCell(4);
                cell.setCellValue(emUserCompanyPersonal.getNationalArea());
                cell.setCellStyle(styles[4]);
            }
        }
        //获取浏览器信息,对文件名进行重新编码
        String fileName = FileUtil.filenameEncoding("人员信息表.xlsx", request);
        //设置信息头
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setHeader("filename", fileName);
        wb.write(response.getOutputStream());
        wb.close();
    }

    //百万数据报表的导入
    @PostMapping("/uploadBw")
    @ResponseBody
    public String uploadBw(@RequestParam("fileName") MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        String ext = filename.substring(filename.lastIndexOf(".") + 1);
        if (!ext.equals("xlsx")) {
            return "文件格式错误";
        }
        //1.根据Excel报表获取OPCPackage
        //以只读形式打开
        OPCPackage opcPackage = OPCPackage.open(file.getInputStream());
        //2.创建XSSFReader
        XSSFReader reader = new XSSFReader(opcPackage);
        //3.获取SharedStringsTable对象
        SharedStringsTable table = reader.getSharedStringsTable();
        //4.获取StyleTable对象
        StylesTable stylesTable = reader.getStylesTable();
        //5.创建sax的xmlReader对象
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        //6.注册事件处理器
        XSSFSheetXMLHandler xmlHandler = new XSSFSheetXMLHandler(stylesTable, table, new SheetHandler(), false);
        xmlReader.setContentHandler(xmlHandler);
        //7.逐行读取
        XSSFReader.SheetIterator sheetIterator = (XSSFReader.SheetIterator) reader.getSheetsData();
        while (sheetIterator.hasNext()) {
            //每一个sheet的流数据
            InputStream stream = sheetIterator.next();
            InputSource is = new InputSource(stream);
            xmlReader.parse(is);
        }
        return "导入成功";
    }

    @PostMapping("/image")
    public String image(@RequestParam("fileName") MultipartFile file, Model model) throws IOException {
        //对上传文件进行Base64编码
        String s = Base64.encode(file.getBytes());
        //拼接Data URL数据头
        String dataUrl = new String("data:image/jpg;base64," + s);
        model.addAttribute("dataUrl", dataUrl);
        return "dataUrl";
    }

    @PostMapping("/qiniuUpload")
    @ResponseBody
    public String qiniuUpload(@RequestParam("fileName") MultipartFile file) throws IOException {
//        //构造一个带指定 Region 对象的配置类
//        //Region.region0()指定机房地区,表示华东
//        Configuration cfg = new Configuration(Region.region0());
//        //...其他参数参考类注释
//        UploadManager uploadManager = new UploadManager(cfg);
//        //...生成上传凭证，然后准备上传
//        String accessKey = "JOCA01YeTX0nOuXIuLb2unUyHSEJ5KVBV19fJIYb";
//        String secretKey = "-PF0VN77Wl80-TFy_OnoeSh3nzBJku_fKRrvd0GW";
//        String bucket = "qiniuyun-image";
//        //默认不指定key的情况下，以文件内容的hash值作为文件名
//        //可指定文件名
//        String key = "test2";
//        Auth auth = Auth.create(accessKey, secretKey);
//        //指定覆盖上传
//        String upToken = auth.uploadToken(bucket, key);
//        try {
//            Response response = uploadManager.put(file.getBytes(), key, upToken);
//            //解析上传成功的结果
//            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
//            System.out.println(putRet.key);
//            System.out.println(putRet.hash);
//        } catch (QiniuException ex) {
//            Response r = ex.response;
//            System.err.println(r);
//            try {
//                System.err.println(r.bodyString());
//            } catch (QiniuException ex2) {
//                //ignore
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String upload = QiniuyunUploadUtil.upload("test3", file.getBytes());
//        System.out.println(upload);
        String upload = QiniuyunUploadUtil.breakpointUpload("test4", file.getBytes());
        System.out.println(upload);
        return "上传成功";
    }

    @GetMapping("/jasper")
    public void createPdf(HttpServletRequest request,HttpServletResponse response) throws IOException {
        //1.引入jasper文件
        Resource resource=new ClassPathResource("templates/test3.jasper");
        FileInputStream in=new FileInputStream(resource.getFile());
        ServletOutputStream out = response.getOutputStream();
        //2.创建JasperPrint,向JasperPrint中填充数据
        try {
            /**
             * in:jasper文件输入流
             * new HashMap:向模板中输入的参数
             * JRDataSource:数据源(和数据库数据源不同)
             *     填充模板的数据来源(connection,javaBean,map)
             *     填充空数据来源 JREmptyDataSource
             */
            JasperPrint jasperPrint = JasperFillManager.fillReport(in, new HashMap<>(), new JREmptyDataSource());
            //3.将JasperPrint以PDF形式输出
            JasperExportManager.exportReportToPdfStream(jasperPrint,out);
        } catch (JRException e) {
            e.printStackTrace();
        }finally {
            out.flush();
            out.close();
        }
    }

    @GetMapping("/jasperParam")
    public void createPdfParam(HttpServletRequest request,HttpServletResponse response) throws IOException {
        //1.引入jasper文件
        Resource resource=new ClassPathResource("templates/testParam.jasper");
        FileInputStream in=new FileInputStream(resource.getFile());
        ServletOutputStream out = response.getOutputStream();
        //2.创建JasperPrint,向JasperPrint中填充数据
        try {
            /**
             * in:jasper文件输入流
             * new HashMap:向模板中输入的参数
             * JRDataSource:数据源(和数据库数据源不同)
             *     填充模板的数据来源(connection,javaBean,map)
             *     填充空数据来源 JREmptyDataSource
             */
            Map<String,Object> params=new HashMap<>();
            //设置参数key为模板设计时的parameters
            params.put("username","骡子");
            params.put("mobile","123456");
            params.put("dept","研发部");
            params.put("company","传智播客");
            JasperPrint jasperPrint = JasperFillManager.fillReport(in,params, new JREmptyDataSource());
            //3.将JasperPrint以PDF形式输出
            JasperExportManager.exportReportToPdfStream(jasperPrint,out);
        } catch (JRException e) {
            e.printStackTrace();
        }finally {
            out.flush();
            out.close();
        }
    }

    /**
     * 基于jdbc数据源填充数据,动态sql
     */
    @GetMapping("jasperConn")
    public void createPdfConn(HttpServletRequest request,HttpServletResponse response) throws IOException {
        //1.引入jasper文件
        Resource resource=new ClassPathResource("templates/testConn3.jasper");
        FileInputStream in=new FileInputStream(resource.getFile());
        ServletOutputStream out = response.getOutputStream();
        try {
            Map<String,Object> params=new HashMap<>();
            params.put("user_id","1");
            Connection conn = getConnection();
            JasperPrint jasperPrint = JasperFillManager.fillReport(in, params, conn);
            JasperExportManager.exportReportToPdfStream(jasperPrint,out);
        } catch (JRException e) {
            e.printStackTrace();
        }finally {
            out.flush();
            out.close();
        }

    }

    private Connection getConnection()  {
        Connection connection=null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/excel?serverTimezone=UTC", "root", "1234");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 基于javaBean填充数据
     */
    @GetMapping("jasperBean")
    public void createPdfBean(HttpServletRequest request,HttpServletResponse response) throws IOException {
        //1.引入jasper文件
        Resource resource=new ClassPathResource("templates/testGroup3.jasper");
        FileInputStream in=new FileInputStream(resource.getFile());
        ServletOutputStream out = response.getOutputStream();
        try {
            Map<String,Object> params=new HashMap<>();
            //构建javaBean数据源
            //1.获取对象的list集合
            List<UserVo> list = getUserVoList();
            //2.通过list集合创建javaBean的数据源对象
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
            JasperPrint jasperPrint = JasperFillManager.fillReport(in, params, dataSource);
            response.setContentType("application/pdf");
            JasperExportManager.exportReportToPdfStream(jasperPrint,out);
        } catch (JRException e) {
            e.printStackTrace();
        }finally {
            out.flush();
            out.close();
        }

    }

    private List<UserVo> getUserVoList(){
        List<EmUserCompanyPersonal> list = emUserCompanyPersonalService.getEmUserCompanyPersonalList();
        List<UserVo> userVoList=new ArrayList<>();
        int i=0;
        for (EmUserCompanyPersonal emUserCompanyPersonal:list){
            if (i==40){
                break;
            }
            if (i<=15){
                UserVo userVo=new UserVo();
                userVo.setId("001");
                userVo.setUsername(emUserCompanyPersonal.getUsername());
                userVo.setMobile(emUserCompanyPersonal.getMobile());
                userVo.setDept(emUserCompanyPersonal.getDepartmentName());
                userVoList.add(userVo);
            }else {
                UserVo userVo=new UserVo();
                userVo.setId("002");
                userVo.setUsername(emUserCompanyPersonal.getUsername());
                userVo.setMobile(emUserCompanyPersonal.getMobile());
                userVo.setDept(emUserCompanyPersonal.getDepartmentName());
                userVoList.add(userVo);
            }
            i++;
        }
        return userVoList;
    }

    /**
     * chart表格
     */
    @GetMapping("jasperChart")
    public void createPdfChart(HttpServletRequest request,HttpServletResponse response) throws IOException {
        //1.引入jasper文件
        Resource resource=new ClassPathResource("templates/testChart.jasper");
        FileInputStream in=new FileInputStream(resource.getFile());
        ServletOutputStream out = response.getOutputStream();
        try {
            Map<String,Object> params=new HashMap<>();
            //构建javaBean数据源
            //1.获取对象的list集合
            List<UserCount> list = getUserCountList();
            //2.通过list集合创建javaBean的数据源对象
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
            JasperPrint jasperPrint = JasperFillManager.fillReport(in, params, dataSource);
            //获取浏览器信息,对文件名进行重新编码
            String fileName = FileUtil.filenameEncoding("饼图.pdf", request);
            //设置信息头
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setHeader("filename", fileName);
            JasperExportManager.exportReportToPdfStream(jasperPrint,out);
        } catch (JRException e) {
            e.printStackTrace();
        }finally {
            out.flush();
            out.close();
        }

    }

    private List<UserCount> getUserCountList(){
        List<UserCount> list=new ArrayList<>();
        UserCount u1=new UserCount("001",5L);
        UserCount u2=new UserCount("002",7L);
        UserCount u3=new UserCount("003",10L);
        list.add(u1);
        list.add(u2);
        list.add(u3);
        return list;
    }

}
