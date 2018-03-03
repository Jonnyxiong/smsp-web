package com.ucpaas.smsp.dao;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

/**
 * ucpaas_message主库的dao类
 */
@Repository
public class MasterDao extends BaseDao {

	@Override
	@Resource(name = "master_sqlSessionTemplate")
	protected void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

}
