package com.kalix.schedule.plan.workreport.biz;

import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.api.persistence.JsonStatus;
import com.kalix.framework.core.impl.biz.ShiroGenericBizServiceImpl;
import com.kalix.framework.core.util.SerializeUtil;
import com.kalix.schedule.plan.departmentplan.api.dao.IDepartmentPlanBeanDao;
import com.kalix.schedule.plan.personalplan.api.dao.IPersonalPlanBeanDao;
import com.kalix.schedule.plan.workreport.api.biz.IWorkReportBeanService;
import com.kalix.schedule.plan.workreport.api.dao.IWorkReportBeanDao;
import com.kalix.schedule.plan.workreport.api.dao.IWorkReportPlanBeanDao;
import com.kalix.schedule.plan.workreport.entities.WorkReportBean;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @类描述： 
 * @创建人：  
 * @创建时间： 
 * @修改人：
 * @修改时间：
 * @修改备注：
 */
public class WorkReportBeanServiceImpl extends ShiroGenericBizServiceImpl<IWorkReportBeanDao, WorkReportBean> implements IWorkReportBeanService {
    private JsonStatus jsonStatus = new JsonStatus();
    private IWorkReportPlanBeanDao workreportplanBeanDao;
    private IPersonalPlanBeanDao personalplanBeanDao;
    private IDepartmentPlanBeanDao departmentplanBeanDao;
    public WorkReportBeanServiceImpl() {
        super.init(WorkReportBean.class.getName());
    }

    public void setWorkreportplanBeanDao(IWorkReportPlanBeanDao workreportplanBeanDao) {
        this.workreportplanBeanDao = workreportplanBeanDao;
    }

    public void setPersonalplanBeanDao(IPersonalPlanBeanDao personalplanBeanDao) {
        this.personalplanBeanDao = personalplanBeanDao;
    }

    public void setDepartmentplanBeanDao(IDepartmentPlanBeanDao departmentplanBeanDao) {
        this.departmentplanBeanDao = departmentplanBeanDao;
    }

    /**
     * 查找工作汇报关联的个人计划
     *
     * @param id
     * @return
     */
    @Deprecated
    @Override
    public JsonData getPersonalPlanByWorkReportId(long id) {
        List<Long> list = workreportplanBeanDao.findPlanByWorkReportId(id).stream()
                .filter(n -> n.getPersonalplanId() != 0)
                .map(n -> n.getPersonalplanId()).collect(Collectors.toList());

        return JsonData.toJsonData(personalplanBeanDao.findById(list));
    }

    /**
     * 查找工作汇报关联的部门计划
     *
     * @param id
     * @return
     */
    @Deprecated
    @Override
    public JsonData getDepartmentPlanByWorkReportId(long id) {
        List<Long> list = workreportplanBeanDao.findPlanByWorkReportId(id).stream()
                .filter(n -> n.getDepartmentplanId() != 0)
                .map(n -> n.getDepartmentplanId()).collect(Collectors.toList());

        return JsonData.toJsonData(departmentplanBeanDao.findById(list));
    }

    /**
     * 查询个人工作汇报
     *
     * @param page
     * @param limit
     * @param jsonStr
     * @return
     */
    @Override
    public JsonData getSelfEntityByQuery(Integer page, Integer limit, String jsonStr) {
        // 查询json串中添加，当前操作人员id
        Map<String, String> jsonMap = SerializeUtil.json2Map(jsonStr);
        jsonMap.put("userId", String.valueOf(this.getShiroService().getCurrentUserId()));

        return super.getAllEntityByQuery(page, limit, SerializeUtil.serializeJson(jsonMap));
    }

    /**
     * 查询全部工作汇报
     * @param page
     * @param limit
     * @param jsonStr
     * @return
     */
    @Override
    public JsonData getAllEntityByQuery(Integer page, Integer limit, String jsonStr) {
        Map<String, String> jsonMap = SerializeUtil.json2Map(jsonStr);
        // 不允许查询全部计划，所以在没有code情况下，添加一个不可能存在的code，保证查询不出数据
        if (jsonMap.get("orgCode") == null || "".equals(jsonMap.get("orgCode")))  {
            jsonMap.put("orgCode", "-1");
        }
        return super.getAllEntityByQuery(page, limit, SerializeUtil.serializeJson(jsonMap));
    }

    @Override
    public void beforeSaveEntity(WorkReportBean entity, JsonStatus status) {
        entity.setUserId(this.getShiroService().getCurrentUserId());
        entity.setUserName(this.getShiroService().getCurrentUserRealName());
        super.beforeSaveEntity(entity,status);
    }
}
