package test.es.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import test.es.entity.RespVo;
import test.es.service.EsService;

import java.util.Map;

/**
 * @Author: nobody
 * @Date: 2020-10-05 13:41
 * @Desc:
 */
@Api(tags = "es api基本操作")
@RestController
@RequestMapping("/es")
public class EsController {

    @Autowired
    private EsService esService;

    @ApiOperation("创建索引")
    @PostMapping("/index")
    public RespVo createIndex(String index) {
        return RespVo.ok(esService.createIndex(index));
    }

    @ApiOperation("查找索引")
    @GetMapping("/index")
    public RespVo getIndex(String index) {
        return RespVo.ok(esService.checkIndexExistence(index));
    }

    @ApiOperation("删除索引")
    @DeleteMapping("/index")
    public RespVo deleteIndex(String index) {
        return RespVo.ok(esService.deleteIndex(index));
    }

    @ApiOperation("创建文档")
    @PutMapping("/doc/{index}")
    public RespVo createDoc(@PathVariable String index, @RequestBody Map<String, ?> object) {
        return RespVo.ok(esService.createDoc(index, object));
    }

    @ApiOperation("查找文档")
    @GetMapping("/doc")
    public RespVo getDoc(String index, String id, String[] includes) {
        return RespVo.ok(esService.getDoc(index, id, includes));
    }

    @ApiOperation("检查文档是否存在")
    @GetMapping("/doc/existence")
    public RespVo docExistence(String index, String id) {
        return RespVo.ok(esService.checkDocExistence(index, id));
    }

    @ApiOperation("编辑文档")
    @PostMapping("/doc")
    public RespVo editDoc(String index, String id, Object params) {
        return RespVo.ok(esService.updateDoc(index, id, params));
    }

    @ApiOperation("删除文档")
    @DeleteMapping("/doc")
    public RespVo dropDoc(String index, String docId) {
        return RespVo.ok(esService.deleteDoc(index, docId));
    }

    @ApiOperation("复制索引")
    @PostMapping("/reindex")
    public RespVo reindex(String destIndex, String... sourceIndices) {
        return RespVo.ok(esService.reindex(destIndex, sourceIndices));
    }

    @ApiOperation("根据查询条件编辑")
    @PostMapping("/updateByQuery")
    public RespVo updateByQuery(@RequestBody Map params, String... sourceIndices) {
        return RespVo.ok(esService.updateByQuery(params, sourceIndices));
    }

    @ApiOperation("根据查询条件删除")
    @PostMapping("/deleteByQuery")
    public RespVo deleteByQuery(String name, String... sourceIndices) {
        return RespVo.ok(esService.deleteByQuery(name, sourceIndices));
    }

    @ApiOperation("批量查询文档")
    @GetMapping("/multiGet/{index}")
    public RespVo mGetDocs(@PathVariable String index, String... ids) {
        return RespVo.ok(esService.multiGetDocs(index, ids));
    }

    @ApiOperation("搜索")
    @PostMapping("/search")
    public RespVo search(String filed, String value, String... index) {
        return RespVo.ok(esService.searchByConditions(filed, value, index));
    }

    @ApiOperation("搜索建议")
    @PostMapping("/searchSuggest")
    public RespVo searchSuggest(String filed, String value, String index) {
        return RespVo.ok(esService.searchSuggestions(index, filed, value));
    }

    @ApiOperation("聚合搜索")
    @PostMapping("/search/aggr")
    public RespVo searchAggr(String index) {
        return RespVo.ok(esService.searchAggrs(index));
    }
}
