package com.cpit.cpmt.biz.impl.exchange.process;

import com.cpit.cpmt.biz.config.RabbitCongfig;
import com.cpit.cpmt.biz.impl.exchange.basic.UnionStoreMgmt;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ConfigurableObjectInputStream;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.*;

@Service
public class ReportMsgProcess {
    private final static Logger logger = LoggerFactory.getLogger(ReportMsgProcess.class);

    private ExecutorService msgProThreadPool = null;

    @Autowired
    private UnionStoreMgmt unionStoreMgmt;

    @Autowired
    private RabbitMsgSender msgSender;


    {
        //msgProThreadPool = Executors.newFixedThreadPool(50);

        msgProThreadPool = new ThreadPoolExecutor(
                25, 50,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(100),
                Executors.defaultThreadFactory(),
                new RejectedExecutionHandler() {

                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                         if (!executor.isShutdown()) {
                            if (r instanceof Job) {
                                logger.debug("===thread pool size is "+executor.getQueue().size()+" finished:"+executor.getCompletedTaskCount()+" active:"+executor.getActiveCount());
                                BasicReportMsgInfo msgInfo = ((Job) r).getMsgInfo();
                                msgSender.send(msgInfo);
                            }
                        }
                    }
                });
    }


    @RabbitListener(queues = RabbitCongfig.EXC_QUEUE_NAME)
    public void reportMsgProc(Message msg) {
        Object _reportMsg = null;
        InputStream input = new ByteArrayInputStream(msg.getBody());
        ConfigurableObjectInputStream inputStream = null;
        try {
            inputStream = new ConfigurableObjectInputStream(input,
                    Thread.currentThread().getContextClassLoader());
            _reportMsg = inputStream.readObject();
        } catch (Exception e) {
            logger.info("RabbitMQ read error: " + e);
        }finally{
            try{
                inputStream.close();
            }catch(Exception ex){
            }
        }

        if (_reportMsg != null && _reportMsg instanceof BasicReportMsgInfo) {
            BasicReportMsgInfo reportMsg = (BasicReportMsgInfo) _reportMsg;
            msgProThreadPool.execute(new Job(reportMsg));
        }
    }

    class Job implements Runnable {

        private final BasicReportMsgInfo msgInfo;

        public Job(BasicReportMsgInfo msgInfo) {
            this.msgInfo = msgInfo;
        }

        @Override
        public void run() {
            unionStoreMgmt.storeDB(msgInfo);
        }

        public BasicReportMsgInfo getMsgInfo() {
            return this.msgInfo;
        }
    }
}
