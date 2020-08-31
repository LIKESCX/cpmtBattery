package com.cpit.cpmt.biz.impl.exchange.operator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.common.SequenceId;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.exchange.operator.ExcOperFlowMapper;
import com.cpit.cpmt.biz.dao.exchange.operator.ExcParameterCtlMapper;
import com.cpit.cpmt.biz.dao.exchange.operator.ExcThirdAuthenticationMapper;
import com.cpit.cpmt.biz.dao.exchange.operator.ExcThirdInteractiveMapper;
import com.cpit.cpmt.biz.dao.exchange.operator.ExcThirdInterfaceMapper;
import com.cpit.cpmt.dto.exchange.operator.ExcOperFlow;
import com.cpit.cpmt.dto.exchange.operator.ExcParameterCtl;
import com.cpit.cpmt.dto.exchange.operator.ExcThirdAuthentication;
import com.cpit.cpmt.dto.exchange.operator.ExcThirdInteractive;
import com.cpit.cpmt.dto.exchange.operator.ExcThirdInterface;

@Service
public class InteractionMgmt {
	
	@Autowired
	private ExcOperFlowMapper excOperFlowMapper;
	@Autowired
	private ExcThirdAuthenticationMapper excThirdAuthenticationMapper;
	@Autowired
	private ExcThirdInteractiveMapper excThirdInteractiveMapper;
	@Autowired 
	private ExcThirdInterfaceMapper excThirdInterfaceMapper;
	@Autowired
	private ExcParameterCtlMapper excParameterCtlMapper;
	
	//流量控制===start
	public ExcOperFlow getOperFlowByOperId(String operatorId)  throws Exception{
		return excOperFlowMapper.selectByPrimaryKey(operatorId);
	}

	public void updateOperFlowByOperId(ExcOperFlow excOperFlow) throws Exception{
		excOperFlowMapper.updateByPrimaryKeySelective(excOperFlow);
	}

	public void addOperFlowByOperId(ExcOperFlow excOperFlow) throws Exception {
		excOperFlowMapper.insertSelective(excOperFlow);
	}
	
	public void delOperFlowByOperId(String operatorId) {
		excOperFlowMapper.deleteByPrimaryKey(operatorId);
	}
	
	public Page<ExcOperFlow> getOperFlowByParam(ExcOperFlow excOperFlow) {
		return excOperFlowMapper.getOperFlowByParam(excOperFlow);
	}
	//流量控制===end

	//接口适配===start
	public Page<ExcThirdAuthentication> getExcThirdAuthByParam(ExcThirdAuthentication excThirdAuthentication)  throws Exception{
		Page<ExcThirdAuthentication> selectByParam = excThirdAuthenticationMapper.selectByParam(excThirdAuthentication);
		return selectByParam;
	}
	//接口适配===end

	//交互权限===start
//	@Transactional
	public void addExcAuthentication(ExcThirdAuthentication excThirdAuthentication)  throws Exception{
//		ExcThirdInteractive excThirdInteractive = new ExcThirdInteractive();
		int authId = SequenceId.getInstance().getId("cpmtBizauthId");
		excThirdAuthentication.setAuthId(authId);
		excThirdAuthenticationMapper.insertSelective(excThirdAuthentication);	
//		List<Integer> interFaceList = excThirdAuthentication.getInterFaceList();
//		if(interFaceList != null && interFaceList.size() > 0) {
//			for (Integer interfaceId : interFaceList) {
//				int interactiveId = SequenceId.getInstance().getId("cpmtBizinteractiveId");
//				excThirdInteractive.setInteractiveId(interactiveId);
//				excThirdInteractive.setAuthId(excThirdAuthentication.getAuthId());
//				excThirdInteractive.setInterfaceId(interfaceId);
//				excThirdInteractive.setTransCycle(1);//频率开关
//				excThirdInteractive.setStatusCd((byte) 1);
//				excThirdInteractiveMapper.insertSelective(excThirdInteractive);
//			}
//		}
	}
	
//	@Transactional
	public void updateExcAuthentication(ExcThirdAuthentication excThirdAuthentication)  throws Exception{
//		ExcThirdInteractive excThirdInteractive = new ExcThirdInteractive();
		excThirdAuthenticationMapper.updateByPrimaryKeySelective(excThirdAuthentication);	
//		excThirdInteractiveMapper.deleteByAuthId(excThirdAuthentication.getAuthId());
//		List<Integer> interFaceList = excThirdAuthentication.getInterFaceList();
//		if(interFaceList != null && interFaceList.size() > 0) {
//			for (Integer interfaceId : interFaceList) {
//				int interactiveId = SequenceId.getInstance().getId("cpmtBizinteractiveId");
//				excThirdInteractive.setInteractiveId(interactiveId);
//				excThirdInteractive.setAuthId(excThirdAuthentication.getAuthId());
//				excThirdInteractive.setInterfaceId(interfaceId);
//				excThirdInteractive.setTransCycle(1);//频率开关
//				excThirdInteractive.setStatusCd((byte) 1);
//				excThirdInteractiveMapper.insertSelective(excThirdInteractive);
//			}
//		}
	}

	public ExcThirdAuthentication getExcAuthenticationById(int authId)  throws Exception{
		return excThirdAuthenticationMapper.selectByPrimaryKey(authId);
	}

	public List<ExcThirdInterface> getAllInterfaceList()  throws Exception{
		return excThirdInterfaceMapper.getAllInterfaceList();
	}

	//交互权限===end
	
	//参数配置===start
	public Page<ExcParameterCtl> getExcParameterCtlByParam(ExcParameterCtl excParameterCtl)  throws Exception{
		return excParameterCtlMapper.selectByParam(excParameterCtl);
	}
	
	public void uptExcParameterCtl(ExcParameterCtl excParameterCtl)  throws Exception{
		excParameterCtlMapper.updateByPrimaryKeySelective(excParameterCtl);
	}
	
	public void addExcParameterCtl(ExcParameterCtl excParameterCtl) {
		int cpmtBizParameterCtlId = SequenceId.getInstance().getId("cpmtBizParameterCtlId");
		excParameterCtl.setId(cpmtBizParameterCtlId);
		excParameterCtlMapper.insertSelective(excParameterCtl);
	}
	public void delExcParameterCtl(int id) {
		excParameterCtlMapper.deleteByPrimaryKey(id);
	}
	//参数配置===end

	



	
	//新
	public Page<ExcThirdInteractive> getExcAutListByParam(ExcThirdInteractive excThirdInteractive) {
		Page<ExcThirdInteractive> selectByParam = excThirdInteractiveMapper.getExcAutListByParam(excThirdInteractive);
		return selectByParam;
	}

	public void addInteractive(ExcThirdInteractive excThirdInteractive) {
		int interactiveId = SequenceId.getInstance().getId("cpmtBizinteractiveId");
		int cpmtBizinterfaceId = SequenceId.getInstance().getId("cpmtBizinterfaceId");
		excThirdInteractive.setInteractiveId(interactiveId);
		excThirdInteractive.setInterfaceId(cpmtBizinterfaceId);
		excThirdInteractiveMapper.insertSelective(excThirdInteractive);
		
		ExcThirdInterface thirdInterface = new ExcThirdInterface();
		thirdInterface.setInterfaceId(cpmtBizinterfaceId);
		thirdInterface.setInterfaceDesc(excThirdInteractive.getInterfaceDesc());
		thirdInterface.setInterfaceUrl(excThirdInteractive.getInterfaceUrl());
		excThirdInterfaceMapper.insertSelective(thirdInterface);
	}

	public void uptInteractive(ExcThirdInteractive excThirdInteractive) {
		excThirdInteractiveMapper.updateByPrimaryKeySelective(excThirdInteractive);
	}

	public void delInteractive(int interactiveId) {
		excThirdInteractiveMapper.deleteByPrimaryKey(interactiveId);
	}

	public void delAuthentication(int authId) {
		excThirdAuthenticationMapper.deleteByPrimaryKey(authId);
	}

}
