package com.nb6868.onex.sys.service;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.sys.SysConst;
import com.nb6868.onex.sys.dao.CalendarDao;
import com.nb6868.onex.sys.dto.CalendarDTO;
import com.nb6868.onex.sys.entity.CalendarEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 万年历
 *
 * @author Charles zhangchaoxu@gmail.comc
 */
@Service
public class CalendarService extends DtoService<CalendarDao, CalendarEntity, CalendarDTO> {

    /**
     * 是否工作日
     */
    public boolean isWorkday(String day) {
        CalendarEntity calendarEntity = getOneByColumn("day_date", day);
        AssertUtils.isNull(calendarEntity, "日期超出数据库内数据范围:" + day);
        return calendarEntity.getType() == SysConst.CalenderTypeEnum.WORKDAY.getCode() || calendarEntity.getType() == SysConst.CalenderTypeEnum.HOLIDAY_EXCHANGE.getCode();
    }

    /**
     * 获取时间区间内某类型的数据
     *
     * @param startDay 开始日期
     * @param endDay   结束日期
     * @return 日期数组
     */
    public List<Date> getDayList(Integer type, String startDay, String endDay) {
        return CollStreamUtil.toList(lambdaQuery().select(CalendarEntity::getDayDate)
                .between(CalendarEntity::getDayDate, startDay, endDay)
                .eq(type != null, CalendarEntity::getType, type)
                .orderByAsc(CalendarEntity::getDayDate)
                .list(), CalendarEntity::getDayDate);
    }

    /**
     * 获取时间区间内某类型的数量
     *
     * @param startDay 开始日期
     * @param endDay   结束日期
     * @return 日期天数
     */
    public Long getDayCount(Integer type, String startDay, String endDay) {
        return lambdaQuery().select(CalendarEntity::getDayDate)
                .between(CalendarEntity::getDayDate, startDay, endDay)
                .eq(type != null, CalendarEntity::getType, type)
                .orderByAsc(CalendarEntity::getDayDate)
                .count();
    }

    /**
     * 用接口同步
     * http://timor.tech/api/holiday/
     */
    public boolean syncWithApi(Date startDay, Date endDay) {
        // 日期列表
        while (DateUtil.compare(startDay, endDay) <= 0) {
            // 调用接口,用jsoup,而不是RestTemplate是由于可能会被拦截403
            try {
                // 直接接口读取可能会被403,所以用jsoup模拟
                String res = HttpUtil.get("http://timor.tech/api/holiday/info/" + DateUtil.format(startDay, "yyyy-MM-dd"));
                JSONObject jsonResult = JSONUtil.parseObj(res);
                JSONObject jsonType = jsonResult.getJSONObject("type");
                CalendarEntity calendar = new CalendarEntity();
                calendar.setWeek(jsonType.getInt("week"));
                // 节假日类型，分别表示 工作日、周末、节日、调休
                calendar.setType(jsonType.getInt("type"));
                JSONObject workholiday = jsonResult.getJSONObject("holiday");
                if (workholiday == null) {
                    // 如果不是节假日，holiday字段为null。
                    calendar.setWage(1);
                } else {
                    calendar.setWage(workholiday.getInt("wage"));
                    calendar.setHolidayName(workholiday.getStr("name"));
                }
                calendar.setDayDate(startDay);
                save(calendar);
            } catch (Exception e) {
                break;
            }
            startDay = DateUtil.offsetDay(startDay, 1);
        }
        return true;
    }

}
