package org.jeff.controller;

import org.jeff.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
public class StuFooController {

    @Autowired
    private StuService stuService;

    @GetMapping("/select")
    public Object select(int id) {
        return stuService.getStuInfo(id);
    }

    @PostMapping("/save")
    public Object save() {
        stuService.saveStu();
        return "OK";
    }

    @PostMapping("/update")
    public Object update(int id) {
        stuService.updateStu(id);
        return "OK";
    }

    @PostMapping("/delete")
    public Object delete(int id) {
        stuService.deleteStu(id);
        return "OK";
    }





}
