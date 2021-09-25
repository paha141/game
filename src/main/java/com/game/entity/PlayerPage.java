package com.game.entity;

import com.game.controller.PlayerOrder;
import org.springframework.data.domain.Sort;

public class PlayerPage {
    private int pageNumber;
    private int pageSize;
    private Sort.Direction sortDirection;
    private String order;

    public PlayerPage() {
        pageNumber = 0;
        pageSize = 3;
        sortDirection = Sort.Direction.ASC;
        order = PlayerOrder.ID.getFieldName();
    }

    public PlayerPage(int pageNumber, int pageSize, String order) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.order = order;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Sort.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Sort.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
