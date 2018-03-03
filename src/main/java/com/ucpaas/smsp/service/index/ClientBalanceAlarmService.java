package com.ucpaas.smsp.service.index;

import java.util.Map;

import com.ucpaas.smsp.entity.message.ClientBalanceAlarm;

/**
 * @description 预付费客户余额预警信息
 * @author
 * @date 2017-07-21
 */
public interface ClientBalanceAlarmService {

	ClientBalanceAlarm getByClientId(String clientid);

	Map<String, Object> saveClientBalanceAlarm(ClientBalanceAlarm clientBalanceAlarm);
}
