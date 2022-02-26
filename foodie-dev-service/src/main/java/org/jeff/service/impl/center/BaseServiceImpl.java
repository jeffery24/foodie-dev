package org.jeff.service.impl.center;

import com.github.pagehelper.PageInfo;
import org.jeff.util.PagedGridResult;

import java.util.List;

public class BaseServiceImpl {

    public PagedGridResult setterPagedGrid(List<?> list, Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());
        return grid;
    }
}
