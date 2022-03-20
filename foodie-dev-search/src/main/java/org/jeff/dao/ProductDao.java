package org.jeff.dao;

import org.jeff.pojo.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author jeff
 * @since 1.0.0
 */
public interface ProductDao extends ElasticsearchRepository<Product,Long> {

}
