package com.ucpaas.smsp.dao;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

/**
 * ucpaas_message_record短信流水库
 */
@Repository
public class RecordDao extends BaseDao {

	@Override
	@Resource(name = "record_sqlSessionTemplate")
	protected void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

}
