package org.jeff.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author jeff
 * @since 1.0.0
 */
@Document(indexName = "stu", shards = 3, replicas = 1)
public class Stu {

    @Id
    private Long id;

    @Field(store = true,type = FieldType.Text,analyzer = "ik_max_word")
    private String name;

    @Field(store = true,type = FieldType.Integer)
    private Integer age;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
