package org.jeff.dao;

import org.jeff.pojo.Items;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ItemsRepository extends ElasticsearchRepository<Items,Long> {

    //long deleteESUserByName(String name);
    //
    //List<Items> queryESUserByName(String name);

}
