package org.jeff.service;


import org.jeff.util.PagedGridResult;

public interface ItemsESService {

    public PagedGridResult searchItems(String keywords,
                                       String sort,
                                       Integer page,
                                       Integer pageSize);

}
