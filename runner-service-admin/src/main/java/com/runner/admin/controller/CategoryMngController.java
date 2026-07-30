package com.runner.admin.controller;

import com.runner.admin.service.CategoryService;
import com.runner.api.controller.BaseController;
import com.runner.grace.result.GraceJSONResult;
import com.runner.grace.result.ResponseStatusEnum;
import com.runner.pojo.Category;
import com.runner.pojo.bo.SaveCategoryBO;
import com.runner.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Api(tags = "分类管理")
@RestController
@RequestMapping("category")
public class CategoryMngController extends BaseController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation("新增/更新分类")
    @PostMapping("/save")
    public GraceJSONResult saveOrUpdateCategory(@Valid @RequestBody SaveCategoryBO saveCategoryBO,
                                                BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errorMap = getErrors(result);
            return GraceJSONResult.errorMap(errorMap);
        }

        Category category = new Category();
        BeanUtils.copyProperties(saveCategoryBO, category);

        if (saveCategoryBO.getId() == null || saveCategoryBO.getId() == 0) {
            boolean isExist = categoryService.queryCatIsExist(category.getName(), null);
            if (isExist) {
                return GraceJSONResult.errorCustom(ResponseStatusEnum.CATEGORY_EXIST_ERROR);
            }
            categoryService.createCategory(category);
        } else {
            boolean isExist = categoryService.queryCatIsExist(category.getName(), saveCategoryBO.getOldName());
            if (isExist) {
                return GraceJSONResult.errorCustom(ResponseStatusEnum.CATEGORY_EXIST_ERROR);
            }
            categoryService.modifyCategory(category);
        }

        redis.del(REDIS_ALL_CATEGORY);
        return GraceJSONResult.ok();
    }

    @ApiOperation("获取分类列表")
    @GetMapping("/list")
    public GraceJSONResult getCatList() {
        List<Category> categoryList = categoryService.queryCategoryList();
        return GraceJSONResult.ok(categoryList);
    }

    @ApiOperation("获取分类列表（带缓存）")
    @GetMapping("/cached")
    public GraceJSONResult getCats() {
        String allCatJson = redis.get(REDIS_ALL_CATEGORY);
        List<Category> categoryList;

        if (StringUtils.isBlank(allCatJson)) {
            categoryList = categoryService.queryCategoryList();
            redis.set(REDIS_ALL_CATEGORY, JsonUtils.objectToJson(categoryList));
        } else {
            categoryList = JsonUtils.jsonToList(allCatJson, Category.class);
        }

        return GraceJSONResult.ok(categoryList);
    }

    @ApiOperation("删除分类")
    @DeleteMapping("/delete")
    public GraceJSONResult deleteCategory(@RequestParam Integer id) {
        categoryService.deleteCategory(id);
        redis.del(REDIS_ALL_CATEGORY);
        return GraceJSONResult.ok();
    }
}