package com.cpit.cpmt.biz.config.db;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(-1)  // 该切面应当先于 @Transactional 执行
@Component
public class DynamicDataSourceAspect {
	private final static Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

	
	@Value("${ds.if.dynamic}")
	String ifDynamicDS;
	
	private final String[] QUERY_PREFIX = { 
		"select","get","find","query","quickGet"
	};

	
//	@Pointcut("@annotation(com.cpit.common.db.Read)")
//    public void readPointcut() {
//    }
// 
//    @Before("readPointcut()")
//    public void read() {
//    	switchDataSource("read");
//    }
//
//	@After("readPointcut()")
//	public void readAfter() {
//		restoreDataSource();
//	}

	@Pointcut("execution( * com.cpit.cpmt.biz.dao..*.*(..))")
	public void daoAspect() {
	}
    
	@Before("daoAspect()")
	public void beforeDao(JoinPoint point) {
		if("off".equals(ifDynamicDS)) {
			return;
		}
		boolean isQueryMethod = isQueryMethod(point.getSignature().getName());
		if (isQueryMethod) {
			switchDataSource("read");
		}
	}

	@After("daoAspect()")
	public void afterDao(JoinPoint point) {
		if("off".equals(ifDynamicDS)) {
			return;
		}
		restoreDataSource();
	}
	    
    //===============================private method
    private void switchDataSource(String key) {
        if (!DynamicDataSourceContextHolder.containDataSourceKey(key)) {
            logger.debug("======>DataSource [{}] doesn't exist, use default DataSource [{}] " + key);
        } else {
            // 切换数据源
            DynamicDataSourceContextHolder.setDataSourceKey(key);
            logger.debug("======>Switch DataSource to " + DynamicDataSourceContextHolder.getDataSourceKey());
         }   	
    }
    
    private void restoreDataSource() {
        // 将数据源置为默认数据源
        DynamicDataSourceContextHolder.clearDataSourceKey();
    }


	private boolean isQueryMethod(String methodName) {
		for (String prefix : QUERY_PREFIX) {
			if (methodName.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}
}
