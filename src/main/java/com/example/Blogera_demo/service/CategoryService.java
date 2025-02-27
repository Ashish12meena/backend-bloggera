package com.example.Blogera_demo.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Blogera_demo.dto.GetCategories;
import com.example.Blogera_demo.model.Category;
import com.example.Blogera_demo.repository.CategoryRepository;
import com.example.Blogera_demo.serviceInterface.CategoryInterface;

@Service
public class CategoryService implements CategoryInterface{
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<GetCategories> getCategories() {

        
        List<Category> listoFCategories = categoryRepository.findAll();
        List<GetCategories> getCategoriesList = listoFCategories.parallelStream().map(list -> {
            GetCategories getCategories = new GetCategories();
            getCategories.setId(list.getId());
            getCategories.setName(list.getName());
            // getCategoriesList.add(getCategories);
            return getCategories;
        }).toList();

        return getCategoriesList;
    }
}
