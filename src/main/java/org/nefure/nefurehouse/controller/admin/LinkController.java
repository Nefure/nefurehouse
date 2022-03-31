package org.nefure.nefurehouse.controller.admin;

import org.nefure.nefurehouse.model.entity.ShortLinkConfig;
import org.nefure.nefurehouse.model.support.ResultData;
import org.nefure.nefurehouse.service.ShortLinkService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 连接管理接口
 * @author nefure
 * @date 2022/3/27 19:46
 */
@RequestMapping("/admin")
@RestController
public class LinkController {

    @Resource
    private ShortLinkService shortLinkService;

    /**
     * 对已有直链进行更新
     */
    @GetMapping("/api/short-link/key")
    public ResultData updateShortKey(Integer id, String newKey){
        ShortLinkConfig linkConfig = shortLinkService.getByKey(newKey);
        if (linkConfig != null){
            throw new RuntimeException("key已存在");
        }
        linkConfig = shortLinkService.getById(id);
        if(linkConfig == null){
            throw new RuntimeException("短链接不存在或已失效");
        }
        linkConfig.setKey(newKey);
        return ResultData.success();
    }

    @DeleteMapping("/api/short-link")
    public ResultData deleteLinks(@RequestParam("id[]") Integer[] id){
        shortLinkService.delete(id);
        return ResultData.success();
    }

    /**
     *
     * @param dateFrom 格式："2022-03-02"
     * @param dateTo 同上
     */
    @GetMapping("/link/list")
    public ResultData list(String key,
                           String url,
                           String dateFrom,
                           String dateTo,
                           Integer page,
                           Integer limit,
                           @RequestParam(required = false, defaultValue = "createDate") String orderBy,
                           @RequestParam(required = false, defaultValue = "desc") String orderDirection) {
        return ResultData.successData(shortLinkService.list(key,url,dateFrom,dateTo,page,limit,orderBy,orderDirection));
    }

    @GetMapping("/link/delete/{id}")
    public ResultData deleteLink(@PathVariable Integer id){
        shortLinkService.delete(id);
        return ResultData.success();
    }
}
