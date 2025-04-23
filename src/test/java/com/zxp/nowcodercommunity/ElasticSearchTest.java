package com.zxp.nowcodercommunity;


import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.zxp.nowcodercommunity.mapper.DiscussPostMapper;
import com.zxp.nowcodercommunity.mapper.eslaticsearch.DiscussPostRepository;
import com.zxp.nowcodercommunity.pojo.DiscussPost;
import com.zxp.nowcodercommunity.service.DiscussPostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.aot.hint.TypeReference.listOf;

@SpringBootTest
public class ElasticSearchTest {

    // 存储帖子的测试

    // mapper
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    // service测试
    @Autowired
    private DiscussPostService discussPostService;

    // 存储测试
    @Autowired
    private DiscussPostRepository postRepository;

    // Template
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     *  save方法测试
     */
    @Test
    public void testSave() {
        postRepository.save(discussPostMapper.findPostById(241)); // 一条条的save
        postRepository.save(discussPostMapper.findPostById(242));

    }

    /**
     *  saveAll
     */
    @Test
    public void testsaveAll() {
        // saveall需要的参数是一个可迭代的类型
        postRepository.saveAll(discussPostMapper.findPostsById(101, 0, 100, 0));
        postRepository.saveAll(discussPostMapper.findPostsById(102, 0, 100, 0));
        postRepository.saveAll(discussPostMapper.findPostsById(103, 0, 100, 0));
        postRepository.saveAll(discussPostMapper.findPostsById(104, 0, 100, 0));
    }

    @Test
    public void testUpdate() {
        // 如果想要修改一个数据，先修改数据库里面的内容，然后再保存即可

        DiscussPost post = discussPostMapper.findPostById(241);
        post.setContent("这是一个关于elasticsearch的测试");
        // 然后就可以成功更新了
        postRepository.save(post);
    }

    /**
     *  删除操作
     */
    @Test
    public void delete() {
        // postRepository.deleteById(241); // 根据id删除
        // 删除所有的
        postRepository.deleteAll();
    }

    /**
     * 搜索
     */
    @Test
    public void search() {
        // title
        HighlightField highlightTitle = new HighlightField("title", HighlightFieldParameters.builder().withPreTags("<em>").withPostTags("</em>").build());
        // content字段
        HighlightField highlightContent = new HighlightField("content", HighlightFieldParameters.builder().withPreTags("<em>").withPostTags("</em>").build());
        // 把这个规则添加到List里面
        List<HighlightField> highlightFieldList = new ArrayList<>();
        highlightFieldList.add(highlightTitle);
        highlightFieldList.add(highlightContent);
        // 构建highLight
        Highlight highlight = new Highlight(highlightFieldList);
        // 构建HighlightQuery
        HighlightQuery highlightQuery = new HighlightQuery(highlight, DiscussPost.class);

        // 构建搜索条件
        NativeQueryBuilder nativeQueryBuilder = new NativeQueryBuilder()
                .withQuery(q -> q.multiMatch(
                        m -> m.query("互联网寒冬")
                                .fields("title", "content") // 去title和content里面匹配
                ))
                // 根据类型进行排名
                .withSort(s -> s.field(f -> f.field("type").order(SortOrder.Desc)))
                // 根据得分
                .withSort(s -> s.field(f -> f.field("score").order(SortOrder.Desc)))
                // 根据创建时间
                .withSort(s -> s.field(f -> f.field("createTime").order(SortOrder.Desc)))
                // 分页
                .withPageable(PageRequest.of(0, 10))
                // 突出显示
                .withHighlightQuery(highlightQuery);

        NativeQuery nativeQuery = new NativeQuery(nativeQueryBuilder);

        // 使用esTemplate查询
        SearchHits<DiscussPost> search = elasticsearchTemplate.search(nativeQuery, DiscussPost.class);

        // 逐行打印
        search.forEach(System.out::println);



    }
}
