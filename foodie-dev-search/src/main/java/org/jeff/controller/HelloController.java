package org.jeff.controller;


import org.apache.commons.lang3.StringUtils;
import org.jeff.service.ItemsESService;
import org.jeff.util.JEFFJSONResult;
import org.jeff.util.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("items")
public class HelloController {

    @Autowired
    private ItemsESService itemsESService;

    @GetMapping("/hello")
    public Object hello() {

        return "Hello Elasticsearch~";
    }


    @GetMapping("/es/search")
    public JEFFJSONResult search(
            String keywords,
            String sort,
            Integer page,
            Integer pageSize) {

        if (StringUtils.isBlank(keywords)) {
            return JEFFJSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 20;
        }

        page --;

        PagedGridResult grid = itemsESService.searchItems(keywords,
                sort,
                page,
                pageSize);

        return JEFFJSONResult.ok(grid);
    }

}
