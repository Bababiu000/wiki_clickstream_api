package com.example.wiki_clickstream.service.impl;

import com.example.wiki_clickstream.entity.ClickstreamNode;
import com.example.wiki_clickstream.mapper.ClickstreamNodeMapper;
import com.example.wiki_clickstream.service.IClickstreamNodeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wiki_clickstream.vo.ClickstreamNodeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 作者
 * @since 2023-09-23
 */
@Service
public class ClickstreamNodeServiceImpl extends ServiceImpl<ClickstreamNodeMapper, ClickstreamNode> implements IClickstreamNodeService {

    @Resource
    private ClickstreamNodeMapper clickstreamNodeMapper;

    @Override
    public Map<String, Object> getDateRange() {

        Map<String, Object> dateRangeData = new HashMap<>();
        List<String> dateRanges = clickstreamNodeMapper.getDateRanges();

        List<String> years = new ArrayList<>();
        Map<Integer, List<Integer>> months = new HashMap<>();
        int maxYear = 0;
        int maxMonth = 0;

        for (String dateRange : dateRanges) {
            String[] parts = dateRange.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);

            if (!years.contains(parts[0])) {
                years.add(parts[0]);
            }

            if (!months.containsKey(year)) {
                months.put(year, new ArrayList<>());
            }
            months.get(year).add(month);

            if (year > maxYear || (year == maxYear && month > maxMonth)) {
                maxYear = year;
                maxMonth = month;
            }
        }

        // 对 years 列表进行排序
        Collections.sort(years);
        Collections.reverse(years);

        // 对 months 列表中的每个年份的月份列表进行排序
        for (List<Integer> monthList : months.values()) {
            Collections.sort(monthList);
        }

        dateRangeData.put("years", years);
        dateRangeData.put("months", months);
        dateRangeData.put("maxYear", maxYear);
        dateRangeData.put("maxMonth", maxMonth);

        return dateRangeData;
    }

    @Override
    public Map<String, Object> getList(String dateStr, Integer pageNum, Integer pageSize, String keyword) {
        LocalDate parsedDate = LocalDate.parse(dateStr.concat("-01"), DateTimeFormatter.ofPattern("yyyy-M-dd"));
        List<ClickstreamNode> clickstreamNodes = clickstreamNodeMapper.getList(parsedDate, (pageNum - 1) * pageSize, pageSize, keyword);

        List<ClickstreamNodeVo> list = new ArrayList<>();
        ClickstreamNodeVo currentCenter = null;

        for (ClickstreamNode clickstreamNode : clickstreamNodes) {
            if (currentCenter == null || !clickstreamNode.getDcDictIdx().equals(currentCenter.getDcDictIdx())) {
                currentCenter = new ClickstreamNodeVo();
                BeanUtils.copyProperties(clickstreamNode, currentCenter);
                currentCenter.setMembers(new ArrayList<>());
                list.add(currentCenter);
            } else {
                currentCenter.getMembers().add(clickstreamNode);
            }
        }

        Long total = clickstreamNodeMapper.getListTotal(parsedDate, keyword);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);

        return result;
    }

    public List<ClickstreamNode> getCenters(String dateStr) {
        LocalDate parsedDate = LocalDate.parse(dateStr.concat("-01"), DateTimeFormatter.ofPattern("yyyy-M-dd"));
        return clickstreamNodeMapper.getCenters(parsedDate);
    }

    @Override
    public List<ClickstreamNode> getDetail(String dateStr, Integer center) {
        LocalDate parsedDate = LocalDate.parse(dateStr.concat("-01"), DateTimeFormatter.ofPattern("yyyy-M-dd"));
        return clickstreamNodeMapper.getDetail(parsedDate, center);
    }
}