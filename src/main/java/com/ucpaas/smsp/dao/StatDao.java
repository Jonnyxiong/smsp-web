package com.ucpaas.smsp.dao;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

/**
 * ucpaas_message_statistics短信流水库
 */
@Repository
public class StatDao extends BaseDao {

	@Override
	@Resource(name = "stat_sqlSessionTemplate")
	protected void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

}
