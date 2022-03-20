package org.jeff.test;

import org.jeff.pojo.Stu;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringDataEsIndexTest {

    //
    @Autowired
    private ElasticsearchRestTemplate esTemplate;

    @Test
    public void createIndex() {
        //创建索引，系统初始化会自动创建索引
        System.out.println(" 创建索引");
    }

    @Test
    public void deleteIndex() {
        //创建索引，系统初始化会自动创建索引
        boolean flg = esTemplate.deleteIndex(Stu.class);
        System.out.println(" 删除索引 = " + flg);
    }


}
