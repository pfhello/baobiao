package cn.itcast.baobiao.poi;

import cn.itcast.baobiao.pojo.PoiEntity;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

/**
 * 自定义时间处理器
 * 处理每行数据读取
 *    实现接口
 */
public class SheetHandler implements XSSFSheetXMLHandler.SheetContentsHandler{

    //要封装的实体对象
    private PoiEntity poiEntity;

    /**
     * 开始解析某一行时触发
     *  i:行的索引 从0开始
     * @param i
     */
    @Override
    public void startRow(int i) {
        //实例化对象
        if (i>0){
            poiEntity=new PoiEntity();
        }
    }

    /**
     * 结束解析某一行时触发
     * @param i
     */
    @Override
    public void endRow(int i) {
        //使用对象进行业务操作(保存到数据库等),这里直接输出
        System.out.println(poiEntity);
    }

    /**
     * 对行中的每一个表格进行处理
     * cellReference:单元格名称(ABCD...)
     * value:数据
     * xssfComment:批注
     */
    @Override
    public void cell(String cellReference, String value, XSSFComment xssfComment) {
        //对对象属性赋值
        if (poiEntity!=null){
            String pre = cellReference.substring(0, 1);
            switch (pre){
                case "A":
                    poiEntity.setId(value);
                    break;
                case "B":
                    poiEntity.setBreast(value);
                    break;
                case "C":
                    poiEntity.setAdipocytes(value);
                    break;
                case "D":
                    poiEntity.setNegative(value);
                    break;
                case "E":
                    poiEntity.setStaining(value);
                    break;
                case "F":
                    poiEntity.setSupportive(value);
                    break;
            }
        }
    }
}
