package org.nefure.nefurehouse.controller.home;

import cn.hutool.core.util.URLUtil;
import org.nefure.nefurehouse.model.entity.ShortLinkConfig;
import org.nefure.nefurehouse.service.ShortLinkService;
import org.nefure.nefurehouse.service.SystemConfigService;
import org.nefure.nefurehouse.util.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * 短链接重定向
 * @author nefure
 * @date 2022/3/31 11:48
 */
@Controller
public class ShortLinkController {

    @Resource
    private ShortLinkService shortLinkService;

    @Resource
    private SystemConfigService systemConfigService;

    @RequestMapping("/s/{key}")
    public String parseShortLink(@PathVariable String key){
        ShortLinkConfig shortLinkConfig = shortLinkService.getByKey(key);
        if (shortLinkConfig == null){
            throw new RuntimeException("此短链接不存在或已失效");
        }

        String domain = systemConfigService.getSystemConfig().getDomain();
        String url = URLUtil.encode(StringUtils.removeDuplicateSeparator(domain+shortLinkConfig.getUrl()));

        return "redirect:"+url;
    }
}
