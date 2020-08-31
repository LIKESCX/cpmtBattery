package com.cpit.cpmt.biz.utils.exchange;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class NumberUtils {
    private static final Integer MILLION = 10000;
    private static final Integer BILLION = 100000000;
    private static final String MILLION_UNIT = "万";
    private static final String BILLION_UNIT = "亿";

    /**
     * 将数字转换成以万为单位或者以亿为单位，因为在前端数字太大显示有问题
     *
     * @author
     * @version 1.00.00
     *
     * @date 2018年1月18日
     * @param amount 报销金额
     * @return
     */
    public static String amountConversion(double amount){
        //最终返回的结果值
        String result = String.valueOf(amount);
        //商
        double tempValue = 0;
        //余数
        double remainder = 0;
        //余数除万的商
        double tempValue2 = 0;
        //余数除万的余数
        double remainder2 = 0;

        //金额大于1万小于1亿
        if(amount > MILLION && amount < BILLION){
            tempValue = amount/MILLION;
            remainder = amount%MILLION;
            result = new Double(tempValue).intValue()+ MILLION_UNIT+formatNumber(remainder);
        }
        //金额大于1亿
        else if(amount > BILLION){
            tempValue = amount/BILLION;
            remainder = amount%BILLION;
            tempValue2 = remainder/MILLION;
            remainder2 = remainder%MILLION;
            result = new Double(tempValue).intValue() + BILLION_UNIT+new Double(tempValue2).intValue()+MILLION_UNIT+formatNumber(remainder2);

        }else{
            result = formatNumber(amount);
        }
        return result;
    }


    /**
     * 对数字进行四舍五入，保留2位小数
     *
     * @author
     * @version 1.00.00
     *
     * @date 2018年1月18日
     * @param number 要四舍五入的数字
     * @return
     */
    public static String formatNumber(double number){
        DecimalFormat df = new DecimalFormat("0.00");
        String s = df.format(number);
        return s;
    }

    /**
     * 对数据进行补0显示，即显示.00
     *
     * @author
     * @version 1.00.00
     *
     * @date 2018年1月23日
     * @return
     */
    public static String zeroFill(double number){
        String value = String.valueOf(number);

        if(value.indexOf(".")<0){
            value = value + ".00";
        }else{
            String decimalValue = value.substring(value.indexOf(".")+1);

            if(decimalValue.length()<2){
                value = value + "0";
            }
        }
        return value;
    }

    /**
     * 测试方法入口
     *
     * @author
     * @version 1.00.00
     *
     * @date 2018年1月18日
     * @param args
     */
    public static void main(String[] args) throws Exception{
        System.out.println(amountConversion((double)3.2));
        amountConversion((double)1222222222/60);
//        amountConversion(18166.235);
//        amountConversion(1222188.43434);
//        amountConversion(129887783.32);
    }

}
