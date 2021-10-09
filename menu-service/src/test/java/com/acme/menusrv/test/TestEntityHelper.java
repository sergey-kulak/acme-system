package com.acme.menusrv.test;

import com.acme.menusrv.dish.Dish;
import com.acme.menusrv.dish.repository.DishRepository;
import com.acme.menusrv.menu.Category;
import com.acme.menusrv.menu.repository.CategoryRepository;
import com.acme.testcommons.RandomTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TestEntityHelper {
    @Autowired
    private DishRepository dishRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public Mono<Dish> createDish(UUID companyId, UUID ppId) {
        Dish dish = new Dish();
        dish.setId(UUID.randomUUID());
        dish.setCompanyId(companyId);
        dish.setPublicPointId(ppId);
        dish.setName(RandomTestUtils.randomString("dish"));
        dish.setDescription(RandomTestUtils.randomString("delicious"));
        dish.setPrimaryImage(RandomTestUtils.randomString("primary"));
        dish.setImages(List.of(RandomTestUtils.randomString("secondary")));
        dish.setComposition(RandomTestUtils.randomString("composition"));
        dish.setTags(List.of(RandomTestUtils.randomString("tag")));
        dish.setPrice(new BigDecimal("5.55"));

        return dishRepository.save(dish);
    }

    public Mono<Category> createCategory(UUID companyId, UUID ppId) {
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setCompanyId(companyId);
        category.setPublicPointId(ppId);
        category.setName(RandomTestUtils.randomString("cat"));
        category.setPosition(1);
        category.setDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY));
        category.setStartTime(LocalTime.of(9, 0));
        category.setEndTime(LocalTime.of(23, 0));
        category.setDishIds(List.of(UUID.randomUUID()));
        return categoryRepository.save(category);
    }
}
