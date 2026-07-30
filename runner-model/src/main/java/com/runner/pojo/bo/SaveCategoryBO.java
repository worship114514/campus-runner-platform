package com.runner.pojo.bo;

import javax.validation.constraints.NotBlank;

public class SaveCategoryBO {

    private Integer id;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private String oldName;

    @NotBlank(message = "标签颜色不能为空")
    private String tagColor;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getTagColor() {
        return tagColor;
    }

    public void setTagColor(String tagColor) {
        this.tagColor = tagColor;
    }
}