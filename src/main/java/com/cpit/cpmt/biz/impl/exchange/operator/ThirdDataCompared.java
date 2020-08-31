package com.cpit.cpmt.biz.impl.exchange.operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.Date;

@RefreshScope
@Service
public class ThirdDataCompared {
    private final static Logger logger = LoggerFactory.getLogger(StationInfoMgmt.class);

    @Value("${ds.driver}")
    private String JDBC_DRIVER;

    @Value("${ds.url}")
    private String DB_URL;

    @Value("${ds.username}")
    private String USER;

    @Value("${ds.password}")
    private String PASS;

    //图形
    public List<Map<String, Object>> getComareResult(List<String> sqlList, String beginTime, String endTime) {
        List<Map<String, Object>> list = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        try{
            try {
                // 注册 JDBC 驱动
                Class.forName(JDBC_DRIVER);

                // 打开链接，连接数据库
                conn = DriverManager.getConnection(DB_URL,USER,PASS);
                stmt = conn.createStatement();
            } catch (ClassNotFoundException e) {
                logger.error("getConnection error:"+e);
            }

            try {
                //建临时表
                String creat_table_sql=
                        "CREATE TEMPORARY TABLE `connector_power_info` (`id` int(10) NOT NULL,`station_id` varchar(30),`equipment_id` varchar(30),`connector_id` varchar(30),`operator_id` varchar(10),`charge_electricity` double(10,1),`power` double(10,1),`current` double(10,1),`voltage` double(10,1),`temperature` double(10,1),`in_time` datetime,PRIMARY KEY (`id`), KEY `in_time` (`in_time`),KEY `station_id` (`station_id`) ,KEY `operator_id` (`operator_id`) , KEY `equipment_id` (`equipment_id`), KEY `connector_id` (`connector_id`) )";
                stmt.execute(creat_table_sql);
            } catch (SQLException e) {
                logger.error("create table sql error:"+e);
            }

            //插入数据
            //String insert_sql="insert into `tmp_table` (`name`,`value`) values ('梨花',1); ";
            for (String insert_sql : sqlList) {
                stmt.executeUpdate(insert_sql);
            }
            ResultSet rs = null;

            try {
                //关联查询结果
                String sql;
                sql = "select  a.in_time,\n" +
                        "abc.charge_electricity,bac.charge_electricity,abc.power,bac.power,abc.current,bac.current,\n" +
                        "abc.voltage,bac.voltage,abc.temperature,bac.temperature\n" +
                        "from \n" +
                        "(select station_id,equipment_id,connector_id,operator_id,in_time from connector_power_info\n" +
                        " union \n" +
                        "SELECT station_id,equipment_id,connector_id,operator_id,in_time from third_connector_charge where in_time>='"+beginTime+"' and in_time<='"+endTime+"') a \n" +
                        "LEFT JOIN connector_power_info abc on a.in_time=abc.in_time and a.connector_id=abc.connector_id and a.operator_id=abc.operator_id and a.station_id=abc.station_id and a.equipment_id=abc.equipment_id \n" +
                        "LEFT JOIN third_connector_charge bac on a.in_time=bac.in_time and a.connector_id=bac.connector_id and a.operator_id=bac.operator_id and a.station_id=bac.station_id and a.equipment_id=bac.equipment_id  ORDER BY a.in_time DESC \n ";
                rs = stmt.executeQuery(sql);
            } catch (SQLException e) {
                logger.error("linkedSelect error:"+e);
            }
            // 展开结果集数据库
            while(rs.next()){
                Map<String, Object> map = new HashMap<>();
                // 通过字段检索
//                map.put("operator",rs.getString(1));
//                map.put("station",rs.getString(2));
//                map.put("equipment",rs.getString(3));
//                map.put("connector",rs.getString(4));
                map.put("date", rs.getTimestamp(1));
                map.put("chargeElectricity3",rs.getDouble(2));//充电量
                map.put("chargeElectricity",rs.getDouble(3));
                map.put("power3",rs.getDouble(4));//功率
                map.put("power",rs.getDouble(5));
                map.put("current3",rs.getDouble(6));//电流
                map.put("current",rs.getDouble(7));
                map.put("voltage3",rs.getDouble(8));//电压
                map.put("voltage",rs.getDouble(9));
                map.put("temperature3",rs.getDouble(10));//温度
                map.put("temperature",rs.getDouble(11));

                list.add(map);
            }

            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
        }catch(Exception e){
            logger.error("connect db error:"+e);
        }finally{
            // 关闭资源
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }// 什么都不做
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                logger.error("connect close error:"+se);
            }
        }

        return list;
    }


    //分页
    public List<Map<String, Object>> getComareResultPage(List<String> sqlList, String beginTime, String endTime,int pageNumber,int pageSize) {
        List<Map<String, Object>> list = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        try{
            try {
                // 注册 JDBC 驱动
                Class.forName(JDBC_DRIVER);

                // 打开链接，连接数据库
                conn = DriverManager.getConnection(DB_URL,USER,PASS);
                stmt = conn.createStatement();
            } catch (ClassNotFoundException e) {
                logger.error("getConnection error:"+e);
            }

            try {
                //建临时表
                String creat_table_sql=
                        "CREATE TEMPORARY TABLE `connector_power_info` (`id` int(10) NOT NULL,`station_id` varchar(30),`equipment_id` varchar(30),`connector_id` varchar(30),`operator_id` varchar(10),`charge_electricity` double(10,1),`power` double(10,1),`current` double(10,1),`voltage` double(10,1),`temperature` double(10,1),`in_time` datetime,PRIMARY KEY (`id`), KEY `in_time` (`in_time`))";
                stmt.execute(creat_table_sql);
            } catch (SQLException e) {
                logger.error("create table sql error:"+e);
            }

            //插入数据
            //String insert_sql="insert into `tmp_table` (`name`,`value`) values ('梨花',1); ";
            for (String insert_sql : sqlList) {
                stmt.executeUpdate(insert_sql);
            }
            ResultSet rs = null;

            try {
                //关联查询结果
                int index=(pageNumber-1)*pageSize;//分页页码索引
                String sql;
                sql = "select operator.operator_name,station.station_name,equipment.equipment_name,connector.connector_name, a.in_time,\n" +
                        "abc.charge_electricity,bac.charge_electricity,abc.power,bac.power,abc.current,bac.current,\n" +
                        "abc.voltage,bac.voltage,abc.temperature,bac.temperature\n" +
                        "from \n" +
                        "(select station_id,equipment_id,connector_id,operator_id,in_time from connector_power_info\n" +
                        " union \n" +
                        "SELECT station_id,equipment_id,connector_id,operator_id,in_time from third_connector_charge where in_time>='"+beginTime+"' and in_time<='"+endTime+"') a \n" +
                        "LEFT JOIN exc_operator_info operator on  operator.operator_id=a.operator_id\n" +
                        "LEFT JOIN exc_station_info station on station.station_id=a.station_id and station.operator_id=a.operator_id\n" +
                        "LEFT JOIN exc_equipment_info equipment on a.equipment_id=equipment.equipment_id and a.operator_id=equipment.operator_id\n" +
                        "LEFT JOIN exc_connector_info connector on a.connector_id=connector.connector_id and a.operator_id=connector.operator_id\n" +
                        "LEFT JOIN connector_power_info abc on a.in_time=abc.in_time and a.connector_id=abc.connector_id and a.operator_id=abc.operator_id and a.station_id=abc.station_id and a.equipment_id=abc.equipment_id \n" +
                        "LEFT JOIN third_connector_charge bac on a.in_time=bac.in_time and a.connector_id=bac.connector_id and a.operator_id=bac.operator_id and a.station_id=bac.station_id and a.equipment_id=bac.equipment_id ORDER BY a.in_time DESC limit "+index+","+pageSize;
                rs = stmt.executeQuery(sql);
            } catch (SQLException e) {
                logger.error("linkedSelect error:"+e);
            }
            // 展开结果集数据库
            while(rs.next()){
                Map<String, Object> map = new HashMap<>();
                // 通过字段检索
                map.put("operator",rs.getString(1));
                map.put("station",rs.getString(2));
                map.put("equipment",rs.getString(3));
                map.put("connector",rs.getString(4));
                map.put("date", rs.getTimestamp(5));
                map.put("chargeElectricity3",rs.getDouble(6));//充电量
                map.put("chargeElectricity",rs.getDouble(7));
                map.put("power3",rs.getDouble(8));//功率
                map.put("power",rs.getDouble(9));
                map.put("current3",rs.getDouble(10));//电流
                map.put("current",rs.getDouble(11));
                map.put("voltage3",rs.getDouble(12));//电压
                map.put("voltage",rs.getDouble(13));
                map.put("temperature3",rs.getDouble(14));//温度
                map.put("temperature",rs.getDouble(15));

                list.add(map);
            }

            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
        }catch(Exception e){
            logger.error("connect db error:"+e);
        }finally{
            // 关闭资源
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }// 什么都不做
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                logger.error("connect close error:"+se);
            }
        }

        return list;
    }

}
