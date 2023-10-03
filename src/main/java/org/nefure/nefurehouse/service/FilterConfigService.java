package org.nefure.nefurehouse.service;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.nefure.nefurehouse.mapper.FilterConfigMapper;
import org.nefure.nefurehouse.model.entity.FilterConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author nefure
 * @date 2022/3/20 15:09
 */
@Slf4j
@Service
public class FilterConfigService {

    @Resource
    private FilterConfigMapper filterConfigMapper;

    /**
     * 指定驱动器下的文件名称, 根据过滤表达式判断是否会显示, 如果符合任意一条表达式, 则不显示, 反之则显示.
     * @param   driveId
     *          驱动器 ID
     * @param   fileName
     *          文件名
     * @return  是否显示
     */
    public boolean filterResultIsHidden(Long driveId, String fileName) {
        List<FilterConfig> filterConfigList = filterConfigMapper.findByDriveId(driveId);

        for (FilterConfig filterConfig : filterConfigList) {
            String expression = filterConfig.getExpression();
            if (StrUtil.isEmpty(expression)) {
                return false;
            }

            try {
                PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + expression);
                boolean match = pathMatcher.matches(Paths.get(fileName));
                if (match) {
                    return true;
                }
                log.debug("regex: {}, name {}, contains: false", expression, fileName);
            } catch (Exception e) {
                log.debug("regex: {}, name {}, parse error, skip expression", expression, fileName);
            }
        }

        return false;
    }

    public List<FilterConfig> getFiltersByDriveId(Long driveId) {
        return filterConfigMapper.findByDriveId(driveId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void replaceAllFilters(List<FilterConfig> filterConfigs, Integer driveId) {
        filterConfigMapper.deleteByDriveId(driveId);
        for (FilterConfig filterConfig : filterConfigs) {
            filterConfig.setDriveId(driveId);
            filterConfigMapper.save(filterConfig);
        }
    }
}
