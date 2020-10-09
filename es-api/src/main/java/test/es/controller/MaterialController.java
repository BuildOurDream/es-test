package test.es.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.es.entity.RespVo;
import test.es.service.VoteMaterialService;

/**
 * @Author: nobody
 * @Date: 2020-10-08 18:17
 * @Desc:
 */
@Api(tags = "素材")
@RestController
@RequestMapping("/material")
public class MaterialController {

    @Autowired
    private VoteMaterialService voteMaterialService;

    @ApiOperation("同步素材信息到ES")
    @PostMapping("/material")
    public RespVo syncMaterials(String index){
        voteMaterialService.syncMaterials(index);
        return RespVo.ok();
    }
}
