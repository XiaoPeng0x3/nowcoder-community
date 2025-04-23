package com.zxp.nowcodercommunity.mapper.eslaticsearch;

import com.zxp.nowcodercommunity.pojo.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> { // 泛型所需的类型是建立关系的实体和实体的主键类型

}
