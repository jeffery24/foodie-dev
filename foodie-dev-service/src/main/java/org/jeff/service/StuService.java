package org.jeff.service;

import org.jeff.pojo.Stu;

public interface StuService {

    void saveStu();

    Stu getStuInfo(int id);

    void updateStu(int id);

    void deleteStu(int id);

    public void saveParent();
    public void saveChildren();
}
