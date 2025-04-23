package com.zxp.nowcodercommunity.service.impl;

import co.elastic.clients.elasticsearch._types.SortOrder;
import com.zxp.nowcodercommunity.mapper.eslaticsearch.DiscussPostRepository;
import com.zxp.nowcodercommunity.pojo.DiscussPost;
import com.zxp.nowcodercommunity.service.ElasticsearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {
    // 操作数据的接口
    private final DiscussPostRepository postRepository;
    // 进行各种功能实现的接口
    private final ElasticsearchTemplate elasticsearchTemplate;
    public ElasticsearchServiceImpl(DiscussPostRepository postRepository, ElasticsearchTemplate elasticsearchTemplate) {
        this.postRepository = postRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    /**
     * 将新添加的帖子添加到es
     * @param post
     */
    @Override
    public void saveDiscussPost(DiscussPost post) {
        postRepository.save(post);
    }

    /**
     * 删除操作
     */
    @Override
    public void deleteDiscussPost(int id) {
        postRepository.deleteById(id);
    }

    /**
     * 搜索方法
     */
    @Override
    public Page<DiscussPost> searchDiscussPost(String keyword, int currentPage, int pageSize) {
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
                        m -> m.query(keyword)
                                .fields("title", "content") // 去title和content里面匹配
                ))
                // 根据类型进行排名
                .withSort(s -> s.field(f -> f.field("type").order(SortOrder.Desc)))
                // 根据得分
                .withSort(s -> s.field(f -> f.field("score").order(SortOrder.Desc)))
                // 根据创建时间
                .withSort(s -> s.field(f -> f.field("createTime").order(SortOrder.Desc)))
                // 分页
                .withPageable(PageRequest.of(currentPage, pageSize))
                // 突出显示
                .withHighlightQuery(highlightQuery);

        NativeQuery nativeQuery = new NativeQuery(nativeQueryBuilder);

        // 使用esTemplate查询
        SearchHits<DiscussPost> searchHints = elasticsearchTemplate.search(nativeQuery, DiscussPost.class);

        // 构造数据
        List<DiscussPost> discussPosts = searchHints
                .stream()
                .map(search -> {
                    // 得到content
                    DiscussPost discussPost = search.getContent();
                    // 得到title
                    List<String> title = search.getHighlightField("title");
                    // 得到content
                    List<String> content = search.getHighlightField("content");
                    if (!title.isEmpty()) {
                        discussPost.setTitle(title.get(0));
                    }
                    if (!content.isEmpty()) {
                        discussPost.setContent(content.get(0));
                    }
                    return discussPost;
                }).toList();

        return new PageImpl<>(discussPosts, PageRequest.of(currentPage, pageSize), searchHints.getTotalHits());
    }
}
