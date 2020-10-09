package test.es.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.es.entity.VoteMaterial;
import test.es.mapper.VoteMaterialMapper;

import java.util.List;

/**
 * @Author: nobody
 * @Date: 2020-10-08 18:14
 * @Desc:
 */
@Service
public class VoteMaterialService extends ServiceImpl<VoteMaterialMapper, VoteMaterial> {

    @Autowired
    private EsService esService;

    /**
     * 逐条处理
     * @param index
     */
    /*public void syncMaterials(String index) {
        List<VoteMaterial> list = list();
        for (VoteMaterial voteMaterial : list) {
            esService.updateDoc(index, voteMaterial.getId().toString(), BeanUtil.beanToMap(voteMaterial));
        }
    }*/

    /**
     * 批量写入 有则更新 没有则创建
     * @param index
     */
    public void syncMaterials(String index) {
        List<VoteMaterial> list = list();
        if (list.isEmpty()) return;
        BulkRequest bulkRequest = new BulkRequest(index);
        for (VoteMaterial voteMaterial : list) {
            UpdateRequest updateRequest = new UpdateRequest()
                    .id(voteMaterial.getId().toString())
                    .doc(BeanUtil.beanToMap(voteMaterial))
                    .fetchSource(false)
                    .docAsUpsert(true)
                    .retryOnConflict(10);
            bulkRequest.add(updateRequest);
            if (bulkRequest.numberOfActions() >= 5) {
                esService.asyncBulkOp(bulkRequest);
                bulkRequest = new BulkRequest(index);
            }
        }
        esService.asyncBulkOp(bulkRequest);
    }
}
