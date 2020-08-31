package com.cpit.cpmt.biz.impl.exchange.basic;

import com.cpit.common.Dispatcher;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TestReceiveMsgProcess {


    //bmsInfo信息的：
/*
    {

        "OperatorID": "061402628",

            "Seq": "0001",

            "Sig": "2BF4D310F3F59B46CCB874270FE0CD00",

            "TimeStamp": "20191205145451",

            "Data": {

        "ConnectorID": "4402020030000017001",

                "Status": 3,

                "BmsInfo": {

            "MaxChargeCellVoltage": 220,

                    "Soc": 10,

                    "BMSCode": "FFFFFFDF08070211",

                    "TemptureH": 100,

                    "MaxChargeCurrent": 0,

                    "TemptureL": -12,

                    "MaxTemp": 100,

                    "RatedCapacity": 0,

                    "BMSVer": "1.17V",

                    "TotalCurrent": 0,

                    "VoltageL": 220,

                    "TatalVoltage": 0,

                    "VoltageH": 220,

                    "StartChargingTime":"2020-04-24 15:40:11",

                    "ChargingSessionMin":100

        }

    }

    }
*/


    volatile int errCount = 0;

    @Test
    public void send() throws Exception {
        long startTime = new Date().getTime();
        //ExecutorService pool = Executors.newFixedThreadPool(25);
        int coreSize = 500;
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(50);

        ExecutorService fixPool = Executors.newFixedThreadPool(10);

        RestTemplate restTemplate = new RestTemplate();
        Dispatcher dispatcher = new Dispatcher(restTemplate);
        int num = 5000;

        CountDownLatch countDownLatch = new CountDownLatch(num);
        String json = "{\"OperatorID\":\"061402628\",\"Seq\":\"0001\",\"Sig\":\"2BF4D310F3F59B46CCB874270FE0CD00\",\"TimeStamp\":\"20191205145451\",\"Data\":{\"ConnectorID\":\"4402020030000017001\",\"Status\":3,\"BmsInfo\":{\"MaxChargeCellVoltage\":220,\"Soc\":10,\"BMSCode\":\"FFFFFFDF08070211\",\"TemptureH\":100,\"MaxChargeCurrent\":0,\"TemptureL\":-12,\"MaxTemp\":100,\"RatedCapacity\":0,\"BMSVer\":\"1.17V\",\"TotalCurrent\":0,\"VoltageL\":220,\"TatalVoltage\":0,\"VoltageH\":220,\"StartChargingTime\":\"2020-04-24 15:40:11\",\"ChargingSessionMin\":100}}}";
        AtomicInteger errNum = new AtomicInteger(0);
        //
        pool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                fixPool.execute(new Runnable(){

                    @Override
                    public void run() {
                        String url = "http://localhost:28060/testShevcs/v1.0/notification_bmsInfo";
                        try {
                            String result = (String) dispatcher.doPost(url, String.class, json);
                            //System.out.println("===invoked result is:"+result);
                        } catch (Exception e) {
                            errNum.incrementAndGet();
                        }
                        countDownLatch.countDown();
                    }
                });
            }


        }, 0, 10, TimeUnit.MILLISECONDS);


        countDownLatch.await();

        System.out.println("===errCount" + errCount + " " + errNum.get() + " send" + (num - errCount) + ", pay " + (new Date().getTime() - startTime) + " milliseconds");
        //TimeUnit.SECONDS.sleep(30*2*1);

    }

}
