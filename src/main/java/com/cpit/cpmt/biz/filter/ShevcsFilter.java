package com.cpit.cpmt.biz.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alibaba.fastjson.JSON;
import com.cpit.common.JsonUtil;
import com.cpit.cpmt.biz.impl.exchange.operator.AccessManageMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.OperatorInfoMgmt;
import com.cpit.cpmt.biz.utils.exchange.ValidateNullUtil;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.biz.utils.validate.Encrypt;
import com.cpit.cpmt.biz.utils.validate.ReturnCode;
import com.cpit.cpmt.dto.exchange.operator.AccessManage;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;


@Configuration
public class ShevcsFilter extends OncePerRequestFilter  {

	private final static Logger logger = LoggerFactory.getLogger(ShevcsFilter.class);

	@Autowired
	private DataSigCheck dataSigCheck;

    @Autowired
    private OperatorInfoMgmt operatorMgmt;
    
    @Autowired
    private AccessManageMgmt accessMgmt;


	@Value("${secret.key.sign}")
    private String signKey;


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String uri = request.getRequestURI();
		if(uri == null	|| !uri.startsWith("/shevcs/")){
			filterChain.doFilter(request, response);
			return;
	    }
		Map<String, Object> resMap = new LinkedHashMap<String, Object>();
		String body = getBody(request);
		if(!checkRequestBody(body,resMap)) {
			resMap.put("Data", "");
			response(response,resMap); //提示用户出错
			return;
		}
		logger.info("===received req="+uri+",operatorId="+resMap.get("operatorId"));
		//进入下一步业务处理
		body = (String)resMap.get("body");
		filterChain.doFilter(new MyRequestWrapper(request,body), response);
	}

	/**
	 * 
	 * @param request
	 * @return
	 * true : ok
	 * false: not ok
	 */
	private boolean checkRequestBody(String body,Map<String,Object> resMap) {
		String operatorId = null;
		String data = null;
		String timeStamp = null;
		String seq = null;
		String sig = null;
		Map<String,Object> contentMap = null;
		try {
			contentMap = JsonUtil.jsonToBean(body, Map.class);
			operatorId = (String)contentMap.get("OperatorID");
			data = (String)contentMap.get("Data");
			timeStamp = (String)contentMap.get("TimeStamp");
			seq = (String)contentMap.get("Seq");
			sig = (String)contentMap.get("Sig");
		} catch (Exception e) {
		}
		
		boolean paraNullValidate = ValidateNullUtil.requestParaValNull(operatorId, data, timeStamp, seq, sig);
		if(!paraNullValidate) {
			resMap.put("Ret", ReturnCode.CODE_4003);
			resMap.put("Msg", "In filter "+ReturnCode.MSG_4003);
			return false;
		}
		int status = checkStatus(operatorId);
		if(status == -1) {
			resMap.put("Ret", ReturnCode.CODE_4003);
			resMap.put("Msg", ReturnCode.MSG_4003_OpeartorId_Unregister);
			return false;
		}else if(status == OperatorInfoExtend.STATUS_CD_AUDIT_FAIL) {
			resMap.put("Ret", ReturnCode.CODE_4003);
			resMap.put("Msg", ReturnCode.MSG_4003_Operator_No_Check);
			return false;
		}else if(status == OperatorInfoExtend.STATUS_CD_DAISHENHE) {
			resMap.put("Ret", ReturnCode.CODE_4003);
			resMap.put("Msg", ReturnCode.MSG_4003_Operator_Wait_Check);
			return false;
		}else if(status == OperatorInfoExtend.STATUS_CD_TINGYUN) {
			resMap.put("Ret", ReturnCode.CODE_4003);
			resMap.put("Msg", ReturnCode.MSG_4003_Operator_Stop);
			return false;
		}
		
		int ifAccess = checkAccess(operatorId);
		if(ifAccess == AccessManage.IFACCESS_OFF) {
			resMap.put("Ret", ReturnCode.CODE_4003);
			resMap.put("Msg", ReturnCode.MSG_4003_Operator_Forbid_To_Access);
			return false;
			
		}
				
		String msg = operatorId+data+timeStamp+seq;
		String genSig = Encrypt.hmacMD5(signKey, msg);
		
		boolean sigCheck = sig.equals(genSig);
		if (!sigCheck) {
			logger.info(operatorId + " notification_stationInfo sig is wrong");
			resMap.put("Ret", ReturnCode.CODE_4001);
			resMap.put("Msg", ReturnCode.MSG_4001);
			return false;
		} 
		
		String decocedData = dataSigCheck.decodeContentData(data);
		if(decocedData == null) {
			logger.info(operatorId +  " data can not be decoded");
			resMap.put("Ret", ReturnCode.CODE_4003);
			resMap.put("Msg", ReturnCode.MSG_4003_Data_Error);
			return false;
		}
		
		try {
			contentMap.put("Data",JSON.parseObject(decocedData));//Data里放解码后的内容, 替换原Data
			body = JsonUtil.beanToJson(contentMap);
		} catch (Exception e) {
			resMap.put("Ret", ReturnCode.CODE_500);
			resMap.put("Msg", ReturnCode.MSG_500);
			return false;
		} 
		resMap.put("body", body);
		resMap.put("operatorId", operatorId); //用于记录日志

		return true;
		
	}
	
	
	private void response(ServletResponse response,Map<String,Object> resMap) {
		try {
			dataSigCheck.mkReturnMap(resMap);
			String json = JsonUtil.beanToJson(resMap);
			response.setCharacterEncoding("utf-8");
			response.setContentType(MediaType.APPLICATION_JSON_UTF8.toString());
			ServletOutputStream out = response.getOutputStream();
			out.write(json.getBytes("utf-8"));
			out.flush();
			out.close();
		} catch (Exception ex) {
			logger.error("response error",ex);
		}
	}
	
	private int checkStatus(String operatorId) {
		OperatorInfoExtend operator = operatorMgmt.getOperatorInfoById(operatorId);
		return operator == null ? -1 : operator.getStatusCd();
	}
	
	private int checkAccess(String operatorId) {
		AccessManage accessManage = accessMgmt.getAccessManageInfoById(operatorId);
		return accessManage == null ? 0 : accessManage.getIfAccess();
	}


	private String getBody(HttpServletRequest request) {
		StringBuffer buffer = new StringBuffer();
		try{
			ServletInputStream inputStream = request.getInputStream();
			byte[] bs = new byte[1024];
			int n = -1;
			while( (n = inputStream.read(bs,0, 1024)) != -1 ) {
				buffer.append(new String(bs,0,n,"utf-8"));
			}
		}catch(IOException ex) {
			logger.error("can not read request inputStream", ex);
		}
		return buffer.toString();
	}

	
}


class MyRequestWrapper extends HttpServletRequestWrapper {

    private String body;

    public MyRequestWrapper(HttpServletRequest request,String body) throws IOException {
        super(request);
        this.body = body;
    }
        


    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        final ByteArrayInputStream bais = new ByteArrayInputStream(body.getBytes("utf-8"));

        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };
    }

}
