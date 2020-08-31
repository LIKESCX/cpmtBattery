package com.cpit.cpmt.biz.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


import javax.xml.bind.PropertyException;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

/**
 * Mybatis - 通用分页拦截器 适用于：MySQL Oracle MSSQL liangzhiyuan 2016-01-20
 */
@SuppressWarnings(value = { "unchecked" })
@Intercepts(@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
		RowBounds.class, ResultHandler.class }))
public class PageHelper implements Interceptor {
	private static final ThreadLocal<Page> localPage = new ThreadLocal<Page>();

	private static final List<ResultMapping> EMPTY_RESULTMAPPING = new ArrayList<ResultMapping>(0);

	private static String dialect = ""; // 数据库方言

	public static void startPage(int pageNum, int pageSize) {
		startPage(pageNum, pageSize, true);
	}

	public static void startPage(int pageNum, int pageSize, String order) {
		startPage(pageNum, pageSize, true, order);
	}

	public static void startPage(int pageNum, int pageSize, boolean count) {
		localPage.set(new Page(pageNum, pageSize, count ? Page.SQL_COUNT : Page.NO_SQL_COUNT));
	}

	public static void startPage(int pageNum, int pageSize, boolean count, String order) {
		localPage.set(new Page(pageNum, pageSize, count ? Page.SQL_COUNT : Page.NO_SQL_COUNT, order));
	}

	public static Page endPage() {
		Page page = localPage.get();
		localPage.remove();
		return page;
	}


	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		final Object[] args = invocation.getArgs();
		RowBounds rowBounds = (RowBounds) args[2];
		if (localPage.get() == null && rowBounds == RowBounds.DEFAULT) {
			return invocation.proceed();
		} else {

			args[2] = RowBounds.DEFAULT;
			MappedStatement ms = (MappedStatement) args[0];
			Object parameterObject = args[1];
			BoundSql boundSql = ms.getBoundSql(parameterObject);

			Page page = localPage.get();
			localPage.remove();

			if (page == null) {
				page = new Page(rowBounds);
			}
			MappedStatement qs = newMappedStatement(ms, new BoundSqlSqlSource(boundSql));
			args[0] = qs;
			MetaObject msObject = SystemMetaObject.forObject(qs);
			String sql = (String) msObject.getValue("sqlSource.boundSql.sql");
			if (page.getTotal() > Page.NO_SQL_COUNT) {
				msObject.setValue("sqlSource.boundSql.sql", getCountSql(sql));
				Object result = invocation.proceed();
				int totalCount = (Integer) ((List) result).get(0);
				page.setTotal(totalCount);
				int totalPage = totalCount / page.getPageSize() + ((totalCount % page.getPageSize() == 0) ? 0 : 1);
				page.setPages(totalPage);
				msObject.setValue("sqlSource.boundSql.sql", getPageSql(sql, page));
				msObject.setValue("resultMaps", ms.getResultMaps());
				result = invocation.proceed();
				page.addAll((List) result);
				return page;
			} else {
				msObject.setValue("sqlSource.boundSql.sql", getPageSql(sql, page));
				msObject.setValue("resultMaps", ms.getResultMaps());
				return invocation.proceed();
			}
		}
	}

	private String getCountSql(String sql) {
		StringBuilder pageSql = new StringBuilder(200);
		if ("mysql".equals(dialect) || "mssql".equals(dialect)) {
			pageSql.append("select count(0) from (" + sql + ") as tmp_count");
		} else if ("oracle".equals(dialect)) {
			pageSql.append("select count(0) from (" + sql + ")");
		}
		//System.out.println("获取总数sql:"+pageSql.toString());
		return pageSql.toString();
	}

	private String getPageSql(String sql, Page page) {
		StringBuilder pageSql = new StringBuilder(200);
		if ("mysql".equals(dialect)) {
			pageSql.append(sql);
			pageSql.append(" limit " + page.getStartRow() + "," + page.getPageSize());
		} else if ("oracle".equals(dialect)) {
			pageSql.append("select * from ( select temp.*, rownum row_id from ( ");
			pageSql.append(sql);
			pageSql.append(" ) temp where rownum <= ").append(page.getEndRow());
			pageSql.append(") where row_id > ").append(page.getStartRow());
		} /*
			 * else if("mssql".equals(dialect)){ pageSql.append(new
			 * SqlServer().convertToPageSql(sql, page.getStartRow(),
			 * page.getPageSize(),page.getOrderBy())); }
			 */
		// System.out.println("获取分页sql:"+pageSql.toString());
		return pageSql.toString();
	}

	private class BoundSqlSqlSource implements SqlSource {
		BoundSql boundSql;

		public BoundSqlSqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}

		public BoundSql getBoundSql(Object parameterObject) {
			return boundSql;
		}
	}

	private MappedStatement newMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
		MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId() + "_分页",
				newSqlSource, ms.getSqlCommandType());
		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
			StringBuffer keyProperties = new StringBuffer();
			for (String keyProperty : ms.getKeyProperties()) {
				keyProperties.append(keyProperty).append(",");
			}
			keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
			builder.keyProperty(keyProperties.toString());
		}
		builder.timeout(ms.getTimeout());
		builder.parameterMap(ms.getParameterMap());
		List<ResultMap> resultMaps = new ArrayList<ResultMap>();
		ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration(), ms.getId(), int.class, EMPTY_RESULTMAPPING)
				.build();
		resultMaps.add(resultMap);
		builder.resultMaps(resultMaps);
		builder.resultSetType(ms.getResultSetType());
		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());

		return builder.build();
	}

	@Override
	public Object plugin(Object target) {
		if (target instanceof Executor) {
			return Plugin.wrap(target, this);
		} else {
			return target;
		}
	}

	public void setProperties(Properties p) {
		dialect = p.getProperty("dialect");
		if (dialect != null && dialect.equals("")) {
			try {
				throw new PropertyException("dialect property is not found!");
			} catch (PropertyException e) {
			}
		}
	}

}
