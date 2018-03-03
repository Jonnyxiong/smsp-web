package com.ucpaas.smsp.service.index;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ucpaas.smsp.entity.message.ClientBalanceAlarm;
import com.ucpaas.smsp.mapper.message.ClientBalanceAlarmMapper;
import com.ucpaas.smsp.util.JsonUtils;

/**
 * @description 预付费客户余额预警信息
 * @author
 * @date 2017-07-21
 */
@Service
public class ClientBalanceAlarmServiceImpl implements ClientBalanceAlarmService {

	private static final Logger logger = LoggerFactory.getLogger(ClientBalanceAlarmServiceImpl.class);

	@Autowired
	private ClientBalanceAlarmMapper clientBalanceAlarmMapper;

	@Override
	public ClientBalanceAlarm getByClientId(String clientid) {
		return clientBalanceAlarmMapper.getByClientId(clientid);
	}

	@Override
	@Transactional
	public Map<String, Object> saveClientBalanceAlarm(ClientBalanceAlarm clientBalanceAlarm) {
		Map<String, Object> result = new HashMap<>();
		if (clientBalanceAlarm == null) {
			result.put("code", 500);
			result.put("msg", "参数不能为空！");
			return result;
		}

		if (StringUtils.isBlank(clientBalanceAlarm.getClientid())) {
			result.put("code", 500);
			result.put("msg", "客户ID不能为空！");
			return result;
		}

		if (clientBalanceAlarm.getClientid().length() > 6) {
			result.put("code", 500);
			result.put("msg", "客户ID错误！");
			return result;
		}

		if (StringUtils.isBlank(clientBalanceAlarm.getAlarmPhone())) {
			result.put("code", 500);
			result.put("msg", "手机号码不能为空！");
			return result;
		}

		if (clientBalanceAlarm.getAlarmPhone().length() > 1000) {
			result.put("code", 500);
			result.put("msg", "手机号码长度不能超过1000个字符！");
			return result;
		}

		if (clientBalanceAlarm.getAlarmNumber() == null) {
			result.put("code", 500);
			result.put("msg", "账户短信余额限制不能为空！");
			return result;
		}

		if (clientBalanceAlarm.getAlarmNumber() > 1000 * 1000 * 100) {
			result.put("code", 500);
			result.put("msg", "账户短信余额限制最大为1亿条！");
			return result;
		}

		Calendar cal = Calendar.getInstance();

		// 查询到就插入，查询不到就更新
		ClientBalanceAlarm alarm = clientBalanceAlarmMapper.getByClientId(clientBalanceAlarm.getClientid());
		if (alarm == null) {
			alarm = new ClientBalanceAlarm();
			alarm.setClientid(clientBalanceAlarm.getClientid());
			alarm.setAlarmPhone(clientBalanceAlarm.getAlarmPhone());
			alarm.setAlarmNumber(clientBalanceAlarm.getAlarmNumber());
			alarm.setReminderNumber(1);
			alarm.setCreateTime(cal.getTime());
			alarm.setUpdateTime(cal.getTime());
			alarm.setResetTime(cal.getTime());
			logger.debug("插入客户的余额预警信息: 客户{}, 信息", alarm.getClientid(), JsonUtils.toJson(alarm));

			int count = clientBalanceAlarmMapper.insert(alarm);
			if (count > 0) {
				result.put("code", 0);
				result.put("msg", "设置成功");
				return result;
			}
		} else {
			alarm.setAlarmPhone(clientBalanceAlarm.getAlarmPhone());
			alarm.setAlarmNumber(clientBalanceAlarm.getAlarmNumber());
			alarm.setReminderNumber(1);
			alarm.setUpdateTime(cal.getTime());
			logger.debug("更新客户的余额预警信息: 客户{}, 信息", alarm.getClientid(), JsonUtils.toJson(alarm));

			int count = clientBalanceAlarmMapper.updateSelective(alarm);
			if (count > 0) {
				result.put("code", 0);
				result.put("msg", "设置成功");
				return result;
			}
		}

		result.put("code", 500);
		result.put("msg", "设置失败！");
		return result;
	}
}
