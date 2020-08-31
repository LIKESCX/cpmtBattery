package com.cpit.cpmt.biz.impl.monitor;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.common.SequenceId;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.monitor.AnnounceDao;
import com.cpit.cpmt.biz.dao.monitor.StationRunningDao;
import com.cpit.cpmt.biz.impl.message.MessageMgmt;
import com.cpit.cpmt.dto.message.ExcMessage;
import com.cpit.cpmt.dto.monitor.Announce;
import com.cpit.cpmt.dto.monitor.StationRunning;

@Service
public class StationRunningMgmt {
	private final static String PREFIX_ID = "ma"; 

	
	@Autowired
	private StationRunningDao stationRunningDao;
	
	@Autowired
	private AnnounceDao announceDao;

	@Autowired
	private MessageMgmt msgMgmt;

	
	//获取充电次数
	public Page<StationRunning> getCharge(Map condition){
		return stationRunningDao.getCharge(condition);
	}
	
	//获取告警次数
	public Page<StationRunning> getAlarm(Map condition){
		return stationRunningDao.getAlarm(condition);
	}

	@Transactional
	public void addAnnounce(Announce announce){
		String announceId = SequenceId.getInstance().getId("monAnnounceId", PREFIX_ID);
		if(announceId == null) {
			throw new RuntimeException("can not get sequenceId");
		}
		announce.setId(announceId);
		announceDao.insertSelective(announce);
	}
	
	@Transactional
	public void updateAnnounce(Announce announce){
		announceDao.updateByPrimaryKeySelective(announce);
	}
	
	@Transactional
	public void deleteAnnounce(String announceId){
		announceDao.deleteByPrimaryKey(announceId);
	}
	
	private final static String NOTIFY = "#A的#B存在#C，请开展自查字纠，举一反三，及时消除隐患。";
	@Transactional
	public void sendAnnounce(String announceId){
		Announce announce = getAnnounce(announceId);
		
		ExcMessage msg = new ExcMessage();
		msg.setSmsType(ExcMessage.TYPE_STATION_ANNOUNCE);
		msg.setPhoneNumber(announce.getStation().getStationTel());
		String content = NOTIFY;
		content = content.replace("#A", announce.getStation().getOperatorInfo().getOperatorName());
		content = content.replace("#B", announce.getStation().getStationName());
		String problem = "";
		if(announce.getQuestion() != null && announce.getQuestion().trim().length() != 0) {
			problem += announce.getQuestion()+"问题或";//
		}
		if(announce.getEvent() != null && announce.getEvent().trim().length() != 0) {
			problem += announce.getQuestion()+"重大运行事故";//
		}		
		
		if(problem.isEmpty()) {
			return; //not send
		}

		content = content.replace("#C", problem);

		msg.setSubContent(content);
		msgMgmt.sendMessage(msg); //发短信通知

		announce.setNotifyStatus(Announce.NOTIFY_STATUS_SENT);
		updateAnnounce(announce); //修改通知状态
	}

    public Announce getAnnounce(String announceId){
     	return announceDao.selectByPrimaryKey(announceId);
    }	
	
    
    public Page<Announce> selectAnnounceByCondition(Announce condition){
    	return announceDao.selectByCondition(condition);
    }
    
    public static void main(String[] args) {
		String content = NOTIFY;
		content = content.replace("#A", "你好");
		System.out.println(content);

    }

}
