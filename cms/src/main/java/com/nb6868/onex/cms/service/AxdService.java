package com.nb6868.onex.cms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.cms.dao.AxdDao;
import com.nb6868.onex.cms.dto.AxdDTO;
import com.nb6868.onex.cms.entity.AxdEntity;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.Const;
import com.nb6868.onex.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 广告位
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class AxdService extends DtoService<AxdDao, AxdEntity, AxdDTO> {

    @Override
    public QueryWrapper<AxdEntity> getWrapper(String method, Map<String, Object> params){
        return new WrapperUtils<AxdEntity>(new QueryWrapper<>(), params)
                .eq("position", "position")
                .like("name", "name")
                .eq("tenantCode", "tenant_code")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    /**
     * 获取指定位置的广告列表
     * @param position 位置
     * @return 广告列表
     */
    public List<AxdEntity> listByPosition(String position) {
        return query().eq("position", position).orderByAsc("sort").list();
    }

    /**
     * 获取指定位置的广告
     * @param position 位置
     * @return 广告
     */
    public AxdEntity getByPosition(String position) {
        return query().eq("position", position).orderByAsc("sort").last(Const.LIMIT_ONE).one();
    }

}
