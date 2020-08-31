package com.cpit.cpmt.biz.utils.exchange;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TimeUtil {
    public static void main(String[] args) throws Exception {
        Map<String, Object> tenMonthsBefore = getThirtyMinuteBefore();
        String[] monthList = (String[]) tenMonthsBefore.get("list");
        for (String s : monthList) {
            System.out.println(s);
        }
        /*Map<String, Object> hoursBefore = getTenHoursBefore();
        System.out.println((Date)hoursBefore.get("startTime"));
        System.out.println((Date)hoursBefore.get("endTime"));
        String[] hours = (String[]) hoursBefore.get("list");
        for (String hour : hours) {
            System.out.println(hour);
        }*/
    }

    //获取过去30分钟
    public static Map<String, Object> getThirtyMinuteBefore() throws ParseException {
        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Map<String, Object> map = new HashMap<>();
        Calendar date = Calendar.getInstance();
        date.setTime(new Date());
        map.put("endTime",date.getTime());
        date.set(Calendar.MINUTE, date.get(Calendar.MINUTE) - 30);
        map.put("startTime",date.getTime());
        String[] hours = new String[30];

        String format = dateFormat1.format(date.getTime());
        String[] split = format.split("-");

        int year = Integer.parseInt(split[0]);
        int month1 = Integer.parseInt(split[1]);
        int nowDay = Integer.parseInt(split[2]);
        Integer month = getMonth(year, month1);
        Date time = date.getTime();
        int nowHour = time.getHours();
        int nowMinute = time.getMinutes();
        for (int i = 0; i < hours.length; i++) {
            int i3 = year;
            int i2 = month1;
            int i1 = nowDay;
            int i4 = nowHour;
            int i0=  nowMinute++;
            if(nowMinute==59) {
                nowMinute=0;
                i4 = nowHour++;
                if (i4 == 23) {
                    nowHour = 0;
                    i1 = nowDay++;
                    if (i1 == month) {
                        nowDay = 1;
                        i2 = month1++;
                        if (i2 == 12) {
                            month1 = 1;
                            i3 = year++;
                        }
                    }
                }
            }
            hours[i] = sdf.format(sdf.parse(i3 + "-" + i2 + "-" + i1+" "+i4+":"+i0));
        }

        map.put("list",hours);

        return map;
    }

    //获取前10小时(包括当前小时)
    public static Map<String, Object> getTenHoursBefore() throws ParseException {
        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        Map<String, Object> map = new HashMap<>();
        Calendar date = Calendar.getInstance();
        date.setTime(new Date());
        map.put("endTime",date.getTime());
        date.set(Calendar.HOUR, date.get(Calendar.HOUR) - 9);
        map.put("startTime",date.getTime());
        String[] hours = new String[10];

        String format = dateFormat1.format(date.getTime());
        String[] split = format.split("-");

        int year = Integer.parseInt(split[0]);
        int month1 = Integer.parseInt(split[1]);
        int nowDay = Integer.parseInt(split[2]);
        Integer month = getMonth(year, month1);
        int nowHour = date.getTime().getHours();
        for (int i = 0; i < hours.length; i++) {
            int i3 = year;
            int i2 = month1;
            int i1 = nowDay;
            int i0=  nowHour++;
            if(i0==23){
                nowHour=0;
                i1=nowDay++;
                if (i1 == month) {
                    nowDay = 1;
                    i2 = month1++;
                    if (i2 == 12) {
                        month1 = 1;
                        i3 = year++;
                    }
                }
            }
            hours[i] = sdf.format(sdf.parse(i3 + "-" + i2 + "-" + i1+" "+i0));
        }

        map.put("list",hours);

        return map;
    }

    //获取前24小时
    public static Map<String, Object> getHoursBefore() throws ParseException {
        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        Map<String, Object> map = new HashMap<>();
        Calendar date = Calendar.getInstance();
        date.setTime(new Date());
        map.put("endTime",date.getTime());
        date.set(Calendar.HOUR, date.get(Calendar.HOUR) - 23);
        map.put("startTime",date.getTime());
        String[] hours = new String[24];

        String format = dateFormat1.format(date.getTime());
        String[] split = format.split("-");

        int year = Integer.parseInt(split[0]);
        int month1 = Integer.parseInt(split[1]);
        int nowDay = Integer.parseInt(split[2]);
        Integer month = getMonth(year, month1);
        int nowHour = date.getTime().getHours();
        for (int i = 0; i < hours.length; i++) {
            int i3 = year;
            int i2 = month1;
            int i1 = nowDay;
            int i0=  nowHour++;
            if(i0==23){
                nowHour=0;
                i1=nowDay++;
                if (i1 == month) {
                    nowDay = 1;
                    i2 = month1++;
                    if (i2 == 12) {
                        month1 = 1;
                        i3 = year++;
                    }
                }
            }
            hours[i] = sdf.format(sdf.parse(i3 + "-" + i2 + "-" + i1+" "+i0));
        }

        map.put("list",hours);

        return map;
    }

    //获取前24小时(不包括当前小时)
    public static Map<String, Object> getHoursBeforeNotNow() throws ParseException {
        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        Map<String, Object> map = new HashMap<>();
        Calendar date2 = Calendar.getInstance();
        date2.setTime(new Date());
        date2.set(Calendar.HOUR, date2.get(Calendar.HOUR) - 1);
        date2.set(Calendar.MINUTE, 59);
        date2.set(Calendar.SECOND, 59);
        date2.set(Calendar.MILLISECOND, 999);
        map.put("endTime",date2.getTime());

        Calendar date = Calendar.getInstance();
        date.setTime(new Date());
        date.set(Calendar.HOUR, date.get(Calendar.HOUR) - 24);
        map.put("startTime",date.getTime());
        String[] hours = new String[24];

        String format = dateFormat1.format(date.getTime());
        String[] split = format.split("-");

        int year = Integer.parseInt(split[0]);
        int month1 = Integer.parseInt(split[1]);
        int nowDay = Integer.parseInt(split[2]);
        Integer month = getMonth(year, month1);
        int nowHour = date.getTime().getHours();
        for (int i = 0; i < hours.length; i++) {
            int i3 = year;
            int i2 = month1;
            int i1 = nowDay;
            int i0=  nowHour++;
            if(i0==23){
                nowHour=0;
                i1=nowDay++;
                if (i1 == month) {
                    nowDay = 1;
                    i2 = month1++;
                    if (i2 == 12) {
                        month1 = 1;
                        i3 = year++;
                    }
                }
            }
            hours[i] = sdf.format(sdf.parse(i3 + "-" + i2 + "-" + i1+" "+i0));
        }

        map.put("list",hours);

        return map;
    }

    //获取前十天日期
    public static Map<String, Object> getTenDaysBefore() throws ParseException {
        Map<String, Object> map = new HashMap<>();
        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        Calendar date = Calendar.getInstance();
        Date endDate = new Date();
        date.setTime(endDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) - 10);
        date.set(Calendar.HOUR_OF_DAY, 00);
        date.set(Calendar.MINUTE, 00);
        date.set(Calendar.SECOND, 00);
        date.set(Calendar.MILLISECOND, 000);
        Date startDate = date.getTime();
        String[] days = new String[10];
        String format = dateFormat1.format(startDate);
        String[] split = format.split("-");
        int year = Integer.parseInt(split[0]);
        int month1 = Integer.parseInt(split[1]);
        int nowDay = Integer.parseInt(split[2]);
        Integer month = getMonth(year, month1);
        for (int i = 0; i < days.length; i++) {
            int i3 = year;
            int i2 = month1;
            int i1 = nowDay++;
            if (i1 == month) {
                nowDay = 1;
                i2 = month1++;
                if (i2 == 12) {
                    month1 = 1;
                    i3 = year++;
                }
            }
            days[i] = dateFormat1.format(dateFormat1.parse(i3 + "-" + i2 + "-" + i1));
        }
        map.put("startTime",startDate);
        map.put("endTime",endDate);
        map.put("list",days);

        return map;
    }

    //获取前十天日期(包括今天)
    public static Map<String, Object> getTenDays() throws ParseException {
        Map<String, Object> map = new HashMap<>();
        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        Calendar date = Calendar.getInstance();
        Date endDate = new Date();
        date.setTime(endDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) - 9);
        date.set(Calendar.HOUR_OF_DAY, 00);
        date.set(Calendar.MINUTE, 00);
        date.set(Calendar.SECOND, 00);
        date.set(Calendar.MILLISECOND, 000);
        Date startDate = date.getTime();
        String[] days = new String[10];
        String format = dateFormat1.format(startDate);
        String[] split = format.split("-");
        int year = Integer.parseInt(split[0]);
        int month1 = Integer.parseInt(split[1]);
        int nowDay = Integer.parseInt(split[2]);
        Integer month = getMonth(year, month1);
        for (int i = 0; i < days.length; i++) {
            int i3 = year;
            int i2 = month1;
            int i1 = nowDay++;
            if (i1 == month) {
                nowDay = 1;
                i2 = month1++;
                if (i2 == 12) {
                    month1 = 1;
                    i3 = year++;
                }
            }
            days[i] = dateFormat1.format(dateFormat1.parse(i3 + "-" + i2 + "-" + i1));
        }
        map.put("startTime",startDate);
        map.put("endTime",endDate);
        map.put("list",days);

        return map;
    }

    //获取前十星期
    public static Map<String, Object> getTenWeeksBefore() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Object> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        Date sevenDay=null;
        for (int i = 0; i <10 ; i++) {
            if(i==0){
                sevenDay = getSevenDaysBefore(new Date());
                Map<String, String> weekDate = getWeekDate(sevenDay);
                list.add(weekDate.get("monday")+" ~ "+weekDate.get("sunday"));
                Calendar sc = Calendar.getInstance();
                sc.setTime(sdf.parse(weekDate.get("sunday")));
                sc.add(Calendar.DATE, +1);
                sc.add(Calendar.MILLISECOND, -1);
                map.put("endTime",sc.getTime() );
            }else{
                sevenDay = getSevenDaysBefore(sevenDay);
                Map<String, String> weekDate = getWeekDate(sevenDay);
                list.add(weekDate.get("monday")+" ~ "+weekDate.get("sunday"));
                map.put("startTime",sdf.parse(weekDate.get("monday")));
            }

        }
        map.put("list",list);
        return map;
    }

    //获取前12个月（包括本月）
    public static Map<String, Object> getTenMonthsBeforeWithNow() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Map<String, Object> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int month = c.get(Calendar.MONTH)+1;
        int year = c.get(Calendar.YEAR);
        List<String> lastYearList = new ArrayList<>();
        //month =month-1;//不包括本月
        for (int i = month; i >month-12 ; i--) {
            if(i<=0){
                String monthDate=(year-1)+"-"+(i+12);
                monthDate = sdf.format(sdf.parse(monthDate));
                list.add(monthDate);
                String lastMonthDate=(year-2)+"-"+(i+12);
                lastMonthDate = sdf.format(sdf.parse(lastMonthDate));
                lastYearList.add(lastMonthDate);
            }else {
                String monthDate=year+"-"+i;
                monthDate = sdf.format(sdf.parse(monthDate));
                list.add(monthDate);
                String lastMonthDate=(year-1)+"-"+i;
                lastMonthDate = sdf.format(sdf.parse(lastMonthDate));
                lastYearList.add(lastMonthDate);
            }
        }

        Calendar sc = Calendar.getInstance();
        sc.setTimeInMillis(c.getTimeInMillis());
        //int m = month;//上个月
        sc.set(Calendar.MONTH, month);//一月的值是0   0-11
        sc.set(Calendar.DAY_OF_MONTH, 1);
        sc.set(Calendar.HOUR_OF_DAY, 0);
        sc.set(Calendar.MINUTE, 0);
        sc.set(Calendar.SECOND, 0);
        sc.set(Calendar.MILLISECOND, 0);
        sc.add(Calendar.MILLISECOND, -1);
        map.put("endTime", sc.getTime());

        Calendar ec = Calendar.getInstance();
        ec.setTimeInMillis(c.getTimeInMillis());
        ec.set(Calendar.MONTH, month - 1);
        ec.set(Calendar.DAY_OF_MONTH, 1);
        ec.set(Calendar.HOUR_OF_DAY, 0);
        ec.set(Calendar.MINUTE, 0);
        ec.set(Calendar.SECOND, 0);
        ec.set(Calendar.MILLISECOND, 0);
        ec.add(Calendar.MONTH, -11);    //向前10个月，但是-9
        map.put("startTime", ec.getTime());//开始时间
        map.put("list",list);
        map.put("lastYearList",lastYearList);
        return map;
    }

    //获取前十个月(不包括本月)
    public static Map<String, Object> getTenMonthsBefore() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Map<String, Object> map = new HashMap<>();
        List<String> list = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int month = c.get(Calendar.MONTH)+1;
        int year = c.get(Calendar.YEAR);
        month =month-1;//不包括本月
        for (int i = month; i >month-10 ; i--) {
            if(i<=0){
                String monthDate=(year-1)+"-"+(i+12);
                monthDate = sdf.format(sdf.parse(monthDate));
                list.add(monthDate);
            }else {
                String monthDate=year+"-"+i;
                monthDate = sdf.format(sdf.parse(monthDate));
                list.add(monthDate);
            }
        }

        Calendar sc = Calendar.getInstance();
        sc.setTimeInMillis(c.getTimeInMillis());
        //int m = month;//上个月
        sc.set(Calendar.MONTH, month);//一月的值是0   0-11
        sc.set(Calendar.DAY_OF_MONTH, 1);
        sc.set(Calendar.HOUR_OF_DAY, 0);
        sc.set(Calendar.MINUTE, 0);
        sc.set(Calendar.SECOND, 0);
        sc.set(Calendar.MILLISECOND, 0);
        sc.add(Calendar.MILLISECOND, -1);
        map.put("endTime", sc.getTime());

        Calendar ec = Calendar.getInstance();
        ec.setTimeInMillis(c.getTimeInMillis());
        ec.set(Calendar.MONTH, month - 1);
        ec.set(Calendar.DAY_OF_MONTH, 1);
        ec.set(Calendar.HOUR_OF_DAY, 0);
        ec.set(Calendar.MINUTE, 0);
        ec.set(Calendar.SECOND, 0);
        ec.set(Calendar.MILLISECOND, 0);
        ec.add(Calendar.MONTH, -9);    //向前10个月，但是-9
        map.put("startTime", ec.getTime());//开始时间
        map.put("list",list);
        return map;
    }

    //获取前三季度
    public static Map<String, Object> getTreeQuarter() {
        Map<String, Object> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);               //年份
        int m = c.get(Calendar.MONTH) + 1;            //月份
        int s = (int) Math.ceil((m - 1) / 3) + 1;    //季度
        int em = (s - 1) * 3;                        //上一个季度最后一个月

        for (int i = s-1; i >s-4 ; i--) {
            if(i<=0){
                list.add((year-1)+"年第"+(i+4)+"季度");
            }else {
                list.add(year+"年第"+i+"季度");
            }
        }

        Calendar sc = Calendar.getInstance();
        sc.setTimeInMillis(c.getTimeInMillis());
        sc.set(Calendar.MONTH, em);//一月的值是0   0-11
        sc.set(Calendar.DAY_OF_MONTH, 1);
        sc.set(Calendar.HOUR_OF_DAY, 0);
        sc.set(Calendar.MINUTE, 0);
        sc.set(Calendar.SECOND, 0);
        sc.set(Calendar.MILLISECOND, 0);
        sc.add(Calendar.MILLISECOND, -1);
        map.put("endTime", sc.getTime());//结束时间


        Calendar ec = Calendar.getInstance();
        ec.setTimeInMillis(c.getTimeInMillis());
        ec.set(Calendar.MONTH, em - 1);
        ec.set(Calendar.DAY_OF_MONTH, 1);
        ec.set(Calendar.HOUR_OF_DAY, 0);
        ec.set(Calendar.MINUTE, 0);
        ec.set(Calendar.SECOND, 0);
        ec.set(Calendar.MILLISECOND, 0);
        ec.add(Calendar.MONTH, -8);    //向前9个月，但是-8
        map.put("startTime", ec.getTime());//开始时间
        map.put("list",list);

        return map;
    }

    //获取前七天的日期
    public static Date getSevenDaysBefore(Date assignDate){
        Calendar sc = Calendar.getInstance();
        sc.setTime(assignDate);
        sc.add(Calendar.DATE, -7);
        return sc.getTime();
    }

    //当前时间所在一周的周一和周日时间
    public static Map<String,String> getWeekDate(Date date) {
        Map<String,String> map = new HashMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.setFirstDayOfWeek(Calendar.MONDAY);// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
        if(dayWeek==1){
            dayWeek = 8;
        }
        //System.out.println("要计算日期为:" + sdf.format(cal.getTime())); // 输出要计算日期

        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - dayWeek);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        Date mondayDate = cal.getTime();
        String weekBegin = sdf.format(mondayDate);
        //System.out.println("所在周星期一的日期：" + weekBegin);


        cal.add(Calendar.DATE, 4 +cal.getFirstDayOfWeek());
        Date sundayDate = cal.getTime();
        String weekEnd = sdf.format(sundayDate);
        //System.out.println("所在周星期日的日期：" + weekEnd);

        map.put("monday", weekBegin);
        map.put("sunday", weekEnd);
        return map;
    }

    //获取当前月多少天
    public static Integer getMonth(int year, int month) {
        if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
            if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                return 31;
            } else if (month == 4 || month == 6 || month == 9 || month == 11) {
                return 30;
            } else if (month == 2) {
                return 29;
            } else {
                return 0;
            }
        } else {
            if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                return 31;
            } else if (month == 4 || month == 6 || month == 9 || month == 11) {
                return 30;
            } else if (month == 2) {
                return 28;
            } else {
                return 0;
            }
        }
    }

    //获取下个月一号凌晨时间
    public static Date getNextMonth(){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
