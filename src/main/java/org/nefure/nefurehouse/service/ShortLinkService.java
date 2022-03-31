package org.nefure.nefurehouse.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import org.nefure.nefurehouse.mapper.ShortLinkMapper;
import org.nefure.nefurehouse.model.entity.ShortLinkConfig;
import org.nefure.nefurehouse.model.support.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author nefure
 * @date 2022/3/20 20:07
 */
@Service
public class ShortLinkService {

    @Resource
    private ShortLinkMapper shortLinkDAO;

    public void save(ShortLinkConfig shortLinkConfig){
        shortLinkDAO.save(shortLinkConfig);
    }

    public ShortLinkConfig getByUrl(String url){
        return shortLinkDAO.findByUrl(url);
    }

    public ShortLinkConfig createShortLink(String url) {
        String randomKey;
        ShortLinkConfig shortLinkConfig;
        do {
            // 获取短链
            randomKey = RandomUtil.randomString(6);
            shortLinkConfig = getByUrl(randomKey);
        } while (shortLinkConfig != null);
        ShortLinkConfig rt = new ShortLinkConfig();
        rt.setKey(randomKey);
        rt.setUrl(url);
        save(rt);
        return rt;
    }

    public ShortLinkConfig getByKey(String key) {
        return shortLinkDAO.getByKey(key);
    }

    public ShortLinkConfig getById(Integer id) {
        return shortLinkDAO.getById(id);
    }

    public void delete(Integer... ids) {
        if(ids != null) {
            shortLinkDAO.delete(ids);
        }
    }

    public List<ShortLinkService> findAll() {
        return shortLinkDAO.findAll();
    }

    public Page<ShortLinkConfig> list(String key, String url, String dateFrom, String dateTo, Integer page, Integer limit,String orderBy, String order) {
        if(StrUtil.isNotEmpty(dateTo)){
            dateTo += " 23:59:59";
        }else {dateTo = null;}

        if(StrUtil.isNotEmpty(dateFrom)){
            dateFrom += " 00:00:00";
        }else {dateFrom = null;}

        if(StrUtil.isNotEmpty(orderBy)){
            try {
                orderBy = ShortLinkMapper.column.valueOf(orderBy.toUpperCase()).name;
            }catch (Exception ignored){}
        }else {orderBy = null;}

        long count = shortLinkDAO.count();
        Page<ShortLinkConfig> pageNeeded = new Page<>(count,page -1,limit);
        List<ShortLinkConfig> content = shortLinkDAO.search(key != null?key+"%":null, url != null?url+"%":null, dateFrom, dateTo, pageNeeded.getNumber(), pageNeeded.getSize(), orderBy, "desc".equalsIgnoreCase(order));
        pageNeeded.setContent(content);
        return pageNeeded;
    }
}
