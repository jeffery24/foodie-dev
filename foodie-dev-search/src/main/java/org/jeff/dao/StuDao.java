package org.jeff.dao;

import org.jeff.pojo.Stu;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author jeff
 * @since 1.0.0
 */
public interface StuDao extends ElasticsearchRepository<Stu,Long> {

}
