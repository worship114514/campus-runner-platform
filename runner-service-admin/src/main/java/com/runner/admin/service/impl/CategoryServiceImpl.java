package com.runner.admin.service.impl;

import com.runner.admin.mapper.CategoryMapper;
import com.runner.admin.service.CategoryService;
import com.runner.api.controller.BaseController;
import com.runner.exception.GraceException;
import com.runner.grace.result.ResponseStatusEnum;
import com.runner.pojo.Category;
import com.runner.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class CategoryServiceImpl extends BaseController implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    @Transactional
    public void createCategory(Category category) {
        int result = categoryMapper.insert(category);
        if (result != 1) {
            GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
        redis.del(REDIS_ALL_CATEGORY);
    }

    @Override
    @Transactional
    public void modifyCategory(Category category) {
        int result = categoryMapper.updateByPrimaryKey(category);
        if (result != 1) {
            GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
        redis.del(REDIS_ALL_CATEGORY);
    }

    @Override
    public boolean queryCatIsExist(String catName, String oldCatName) {
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name", catName);
        if (StringUtils.isNotBlank(oldCatName)) {
            criteria.andNotEqualTo("name", oldCatName);
        }
        List<Category> catList = categoryMapper.selectByExample(example);
        return catList != null && !catList.isEmpty();
    }

    @Override
    public List<Category> queryCategoryList() {
        return categoryMapper.selectAll();
    }

    @Override
    @Transactional
    public void deleteCategory(Integer id) {
        int result = categoryMapper.deleteByPrimaryKey(id);
        if (result != 1) {
            GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
        redis.del(REDIS_ALL_CATEGORY);
    }
}