package com.ucpaas.smsp.mapper.message;

import org.springframework.stereotype.Repository;

import com.ucpaas.smsp.entity.message.ClientBalanceAlarm;

/**
 * @description 预付费客户余额预警信息
 * @author
 * @date 2017-07-21
 */
@Repository
public interface ClientBalanceAlarmMapper {

	int insert(ClientBalanceAlarm model);

	int updateSelective(ClientBalanceAlarm model);

	ClientBalanceAlarm getById(Integer id);

	ClientBalanceAlarm getByClientId(String clientid);
}