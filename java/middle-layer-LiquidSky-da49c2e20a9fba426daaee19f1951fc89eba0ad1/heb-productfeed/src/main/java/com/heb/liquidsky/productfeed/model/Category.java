
package com.heb.liquidsky.productfeed.model;

public class Category {

    private Long categoryId;
    private Boolean isDefaultParent;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Boolean getIsDefaultParent() {
        return isDefaultParent;
    }

    public void setIsDefaultParent(Boolean isDefaultParent) {
        this.isDefaultParent = isDefaultParent;
    }

}
