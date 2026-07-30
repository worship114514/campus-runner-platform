package com.runner.admin.service;

import com.runner.pojo.Category;

import java.util.List;

public interface CategoryService {
    void createCategory(Category category);
    void modifyCategory(Category category);
    boolean queryCatIsExist(String catName, String oldCatName);
    List<Category> queryCategoryList();
    void deleteCategory(Integer id);
}