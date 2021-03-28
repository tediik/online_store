package com.jm.online_store.service.impl;

import com.jm.online_store.model.Categories;
import com.jm.online_store.model.Characteristic;
import com.jm.online_store.model.ProductCharacteristic;
import com.jm.online_store.model.dto.ProductDto;
import com.jm.online_store.model.filter.CheckboxFilter;
import com.jm.online_store.model.filter.Filter;
import com.jm.online_store.model.filter.FilterData;
import com.jm.online_store.model.filter.FilterType;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.filter.Filters;
import com.jm.online_store.model.filter.RangeFilter;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.CharacteristicService;
import com.jm.online_store.service.interf.FilterService;
import com.jm.online_store.service.interf.ProductCharacteristicService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.util.Transliteration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * сервис для реализации логики фильтров товаров
 */
@Service
@AllArgsConstructor
@Transactional
public class FilterServiceImpl implements FilterService {
    private final ProductService productService;
    private final CharacteristicService characteristicService;
    private final CategoriesService categoriesService;
    private final ProductCharacteristicService productCharacteristicService;

    /**
     * Comparator для сравнения товаров по ценам
     */
    private final Comparator<Product> priceComparator = Comparator.comparing(Product::getPrice);

    /**
     * Возвращает сущность Filters{@link Filters} для отображения возможных фильтров на странице,
     * проверяет, какие характеристики {@link Characteristic} есть у товаров данной категории, чтобы их отправить на фронт
     *
     * @param category - String - категория товара
     * @return {@link Filters} набор фильтров с данными для отображения
     */
    @Override
    public Filters getFilters(String category) {
        List<Filter> filterList = new ArrayList<>();

        Categories categories = categoriesService.getCategoryByCategoryName(Transliteration.latinToCyrillic(category)).get();
        List<Characteristic> characteristics = categories.getCharacteristics();

        FilterData priceRangeFilterData = new RangeFilter(Math.round(Collections.min(categories.getProducts(), priceComparator).getPrice()),
                Math.round(Collections.max(categories.getProducts(), priceComparator).getPrice()));
        Filter priceRangeFilter = new Filter(FilterType.Range, "Цена", "price", priceRangeFilterData);
        filterList.add(priceRangeFilter);

        for (Characteristic characteristic : characteristics) {
            String characteristicName = characteristic.getCharacteristicName();
            Set<CheckboxFilter.Label> label = characteristic.getProductCharacteristics()
                    .stream()
                    .filter(x -> categories.getProducts().contains(x.getProduct()))
                    .map(p -> new CheckboxFilter.Label(p.getValue()))
                    .collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing
                            (CheckboxFilter.Label::getLabel))), TreeSet::new));

            Filter filter = new Filter(FilterType.Checkbox, characteristicName, Transliteration.сyrillicToLatin(characteristicName), new CheckboxFilter(label));
            filterList.add(filter);
        }

        return new Filters(filterList);
    }

    /**
     *
     * @param category String - название категории
     * @param labels - Map<String, String> ключ - название характеристики, значение - заданное значение для фильтрации
     * @return List<ProductDto> - список продуктов по заданным характеристикам.
     */
    @Override
    public List<ProductDto> getProductsByFilter(String category, Map<String, String> labels) {
        Categories categories = categoriesService.getCategoryByCategoryName(category).get();
        List<Product> products = new ArrayList<>();

        for (Map.Entry<String, String> label : labels.entrySet()) {
            if (label.getKey().equals("price")) {
                String[] prices = label.getValue().replaceAll("\\s+", "").split(",");
                products = productService.findProductsByCategoriesAndPriceBetween(categories, Double.parseDouble(prices[0]), Double.parseDouble(prices[1]));
            } else {
                Characteristic characteristic = characteristicService.findByCharacteristicName(Transliteration.latinToCyrillic(label.getKey())).get();
                if (!products.isEmpty()) {
                    products = products.stream()
                            .filter(product -> product.getProductCharacteristics()
                                    .contains(productCharacteristicService
                                            .findProductCharacteristicsByValueAndCharacteristicAndProduct(label.getValue(), characteristic, product)))
                            .collect(Collectors.toList());
                } else {
                    products.addAll(productCharacteristicService.findProductCharacteristicsByValueAndAndCharacteristic(label.getValue(), characteristic)
                            .stream()
                            .map(ProductCharacteristic::getProduct)
                            .collect(Collectors.toList()));
                }
            }
        }

        return products.stream().map(ProductDto::new).collect(Collectors.toList());
    }

}