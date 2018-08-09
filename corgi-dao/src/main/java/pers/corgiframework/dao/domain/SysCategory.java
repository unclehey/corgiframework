package pers.corgiframework.dao.domain;

import java.time.LocalDateTime;

public class SysCategory {
    private Integer id;

    private String categoryName;

    private Integer categoryWeight;

    private Integer categoryType;

    private String categoryPic;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName == null ? null : categoryName.trim();
    }

    public Integer getCategoryWeight() {
        return categoryWeight;
    }

    public void setCategoryWeight(Integer categoryWeight) {
        this.categoryWeight = categoryWeight;
    }

    public Integer getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(Integer categoryType) {
        this.categoryType = categoryType;
    }

    public String getCategoryPic() {
        return categoryPic;
    }

    public void setCategoryPic(String categoryPic) {
        this.categoryPic = categoryPic == null ? null : categoryPic.trim();
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}