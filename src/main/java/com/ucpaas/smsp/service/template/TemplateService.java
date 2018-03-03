package com.ucpaas.smsp.service.template;

import java.util.Map;

import org.springframework.scheduling.annotation.Async;

import com.ucpaas.smsp.common.entity.ResultVO;
import com.ucpaas.smsp.model.PageContainer;
import com.ucpaas.smsp.model.Template;

public interface TemplateService {
	
	PageContainer queryList(Map<Object, Object> params) throws Exception;

    PageContainer queryTemporaryList(Map<Object, Object> queryParams) throws Exception;

    /**
     * 查询模版
     * @param queryParams
     * @return
     * @throws Exception
     */
    Template query(Map<Object, Object> queryParams) throws Exception;

    Template queryTemporary(Map<Object, Object> queryParams) throws Exception;

    ResultVO save(Template template);

    ResultVO update(Template template) throws Exception;
    
    /**
     * @Description 删除短信模板
     * @param templateId
     * @return
     * @author wangwei
     * @date 2017年3月6日 下午3:57:08
     */
    ResultVO deleteTemplate(int templateId);
    
}
