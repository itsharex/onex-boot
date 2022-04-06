package com.nb6868.onex.uc.service;

import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.JacksonUtils;
import com.nb6868.onex.uc.dao.UserParamsDao;
import com.nb6868.onex.uc.entity.TenantParamsEntity;
import com.nb6868.onex.uc.entity.UserParamsEntity;
import org.springframework.stereotype.Service;

/**
 * 用户参数
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class UserParamsService extends EntityService<UserParamsDao, UserParamsEntity> {

    /**
     * 根据参数编码，获取参数的value值
     *
     * @param userId 用户id
     * @param code 参数编码
     */
    public String getContent(Long userId, String code) {
        return query()
                .select("content")
                .eq("user_id", userId)
                .eq("code", code)
                .last(Const.LIMIT_ONE)
                .oneOpt()
                .map(UserParamsEntity::getContent)
                .orElse(null);
    }

    /**
     * 根据参数编码，获取value的Object对象
     *
     * @param code  参数编码
     * @param clazz Object对象
     */
    public <T> T getContentObject(Long userId, String code, Class<T> clazz) {
        return JacksonUtils.jsonToPojo(getContent(userId, code), clazz);
    }

}
