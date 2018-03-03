package com.ucpaas.smsp.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.mybatis.spring.SqlSessionTemplate;

import com.ucpaas.smsp.model.PageContainer;

public abstract class BaseDao {
	
	protected SqlSessionTemplate sqlSessionTemplate;

	/**
	 * 设置SqlSessionTemplate
	 * 
	 * @param sqlSessionTemplate
	 */
	protected abstract void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate);

	/**
	 * 查询单个对象
	 * 
	 * @param sqlId
	 * @return
	 */
	public <T> T selectOne(String sqlId) {
		return sqlSessionTemplate.selectOne(sqlId);
	}

	/**
	 * 查询单个对象
	 * 
	 * @param sqlId
	 * @param sqlParams
	 * @return
	 */
	public <T> T selectOne(String sqlId, Object sqlParams) {
		return sqlSessionTemplate.selectOne(sqlId, sqlParams);
	}

	/**
	 * 查询list
	 * 
	 * @param sqlId
	 * @return
	 */
	public <E> List<E> selectList(String sqlId) {
		return sqlSessionTemplate.selectList(sqlId);
	}

	/**
	 * 查询list
	 * 
	 * @param sqlId
	 * @param sqlParams
	 * @return
	 */
	public <E> List<E> selectList(String sqlId, Object sqlParams) {
		return sqlSessionTemplate.selectList(sqlId, sqlParams);
	}
	
	/**
	 * 查询List
	 * 
	 * @param queryStr
	 * @param sqlParams
	 * @return
	 */
	public List<Map<String, Object>> getSearchList(String queryStr, Object sqlParams) {
		List<Map<String, Object>> list = sqlSessionTemplate.selectList(queryStr, sqlParams);
		return list;
	}

	/**
	 * 计算列表总数
	 * 
	 * @param countStr
	 * @param sqlParams
	 * @return
	 */
	public int getSearchSize(String countStr, Object sqlParams) {
		int totalCount = 0;
		List<Map<String, Object>> list = sqlSessionTemplate.selectList(countStr, sqlParams);
		if (list.size() > 0) {
			totalCount = Integer.parseInt(list.get(0).get("totalCount").toString());
		}
		return totalCount;
	}

	/**
	 * 插入
	 * 
	 * @param sqlId
	 * @return
	 */
	public int insert(String sqlId) {
		return sqlSessionTemplate.insert(sqlId);
	}

	/**
	 * 插入
	 * 
	 * @param sqlId
	 * @param sqlParams
	 * @return
	 */
	public int insert(String sqlId, Object sqlParams) {
		return sqlSessionTemplate.insert(sqlId, sqlParams);
	}

	/**
	 * 更新
	 * 
	 * @param sqlId
	 * @return
	 */
	public int update(String sqlId) {
		return sqlSessionTemplate.update(sqlId);
	}

	/**
	 * 更新
	 * 
	 * @param sqlId
	 * @param sqlParams
	 * @return
	 */
	public int update(String sqlId, Object sqlParams) {
		return sqlSessionTemplate.update(sqlId, sqlParams);
	}

	/**
	 * 删除
	 * 
	 * @param sqlId
	 * @return
	 */
	public int delete(String sqlId) {
		return sqlSessionTemplate.delete(sqlId);
	}

	/**
	 * 删除
	 * 
	 * @param sqlId
	 * @param sqlParams
	 * @return
	 */
	public int delete(String sqlId, Object sqlParams) {
		return sqlSessionTemplate.delete(sqlId, sqlParams);
	}
	
	/**
	 * 分页查询
	 * 
	 * @param queryStr
	 * @param countStr
	 * @param sqlParams
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PageContainer getSearchPage(String queryStr, String countStr, Map sqlParams) {
		PageContainer p = new PageContainer();
		int totalCount = getSearchSize(countStr, sqlParams);
		p.setTotalCount(totalCount);
		if (totalCount > 0) {
			String pageRowCountS = sqlParams.get("pageRowCount") != null ? sqlParams.get("pageRowCount").toString()
					: null;
			if (NumberUtils.isDigits(pageRowCountS)) {
				int pageRowCount = Integer.parseInt(pageRowCountS);
				if (pageRowCount > 0) {
					p.setPageRowCount(pageRowCount);
				}
			}
			int totalPage = totalCount / p.getPageRowCount() + (totalCount % p.getPageRowCount() == 0 ? 0 : 1);
			p.setTotalPage(totalPage);

			String currentPageS = sqlParams.get("currentPage") != null ? sqlParams.get("currentPage").toString() : null;
			if (NumberUtils.isDigits(currentPageS)) {
				int currentPage = Integer.parseInt(currentPageS);
				if (currentPage > 0 && currentPage <= totalPage) {
					p.setCurrentPage(currentPage);
				}
			}

			int startRow = (p.getCurrentPage() - 1) * p.getPageRowCount(); // 分页开始行号
			int rows = p.getPageRowCount();// 分页返回行数
			sqlParams.put("limit", "LIMIT " + startRow + ", " + rows);

			List<Map<String, Object>> list = getSearchList(queryStr, sqlParams);
			for (Map<String, Object> map : list) {
				map.put("rownum", ++startRow);
			}
			p.setList(list);
		}
		
		//暂时处理（解决数据为0，不统一的问题）
		if(totalCount == 0){
			String pageRowCountS = sqlParams.get("pageRowCount") != null ? sqlParams.get("pageRowCount").toString()
					: null;
			if (NumberUtils.isDigits(pageRowCountS)) {
				int pageRowCount = Integer.parseInt(pageRowCountS);
				if (pageRowCount > 0) {
					p.setPageRowCount(pageRowCount);
				}
			}
		}
		
		return p;
	}

}
