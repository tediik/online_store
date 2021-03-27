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
import com.jm.online_store.repository.ProductCharacteristicRepository;
import com.jm.online_store.repository.ProductRepository;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
    private final ProductCharacteristicRepository productCharacteristicRepository;

    /**
     * Comparator для сравнения товаров по ценам
     */
    private final Comparator<Product> priceComparator = Comparator.comparing(Product::getPrice);

    /**
     * Возвращает сущность Filters{@link Filters} для отображения возможным фильтров на странице,
     * проверяет, какие данные есть у товаров данной категории, чтобы их отправить на фронт
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
            String characteristicName = characteristic.getCharacteristicName(); //на русском label Filter
            Set<CheckboxFilter.Label> label = characteristic.getProductCharacteristics()
                    .stream()
                    .filter(x -> categories.getProducts().contains(x.getProduct()))
                    .map(p -> new CheckboxFilter.Label(p.getValue()))
                    .collect(collectingAndThen(toCollection(()-> new TreeSet<>(Comparator.comparing
                                    (CheckboxFilter.Label::getLabel))), TreeSet::new));

            Filter filter = new Filter(FilterType.Checkbox, characteristicName, Transliteration.сyrillicToLatin(characteristicName), new CheckboxFilter(label));
            filterList.add(filter);
        }

        return new Filters(filterList);
    }

    /**
     * Список продуктов, соответствующих заданным фильтрам
     * @param category         - String - категория товара
     * @param price            List<Long> набор цен от ... до ...
     * @param brands           List<String> производители
     * @param color            List<String> цвета
     * @param RAM              List<String> оперативная память
     * @param storage          List<String> встроенная память
     * @param screenResolution List<String> разрешение экрана
     * @param OS               List<String> операционная система
     * @param bluetooth        List<String> версия bluetooth
     * @return List<ProductDto> {@link ProductDto} - список отфильтрованных товаров
     */
    @Override
    public List<ProductDto> filterProducts(String category,
                                           List<Long> price,
                                           List<String> brands,
                                           List<String> color,
                                           List<String> RAM,
                                           List<String> storage,
                                           List<String> screenResolution,
                                           List<String> OS,
                                           List<String> bluetooth) {
        List<Product> products = productService.findProductsByCategoryName(category);
//        if (price != null && !price.isEmpty()) {
//            products = products.stream()
//                    .filter(product -> product.getPrice() >= price.get(0))
//                    .filter(product -> product.getPrice() <= price.get(1))
//                    .collect(Collectors.toList());
//        }
//        if (brands != null && !brands.isEmpty()) {
//            products = products.stream()
//                    .filter(product -> brands.contains(product.getDescriptions().getProducer()))
//                    .collect(Collectors.toList());
//        }
//        if (color != null && !color.isEmpty()) {
//            products = products.stream()
//                    .filter(product -> color.contains(product.getDescriptions().getColor()))
//                    .collect(Collectors.toList());
//        }
//        if (RAM != null && !RAM.isEmpty()) {
//            List<Integer> intRam = new ArrayList<>();
//            for (String r : RAM) {
//                intRam.add(Integer.parseInt(r.substring(0, r.length() - 3)));
//            }
//            products = products.stream()
//                    .filter(product -> intRam.contains(product.getDescriptions().getRam()))
//                    .collect(Collectors.toList());
//        }
//        if (storage != null && !storage.isEmpty()) {
//            List<Integer> intStorage = new ArrayList<>();
//            for (String s : storage) {
//                intStorage.add(Integer.parseInt(s.substring(0, s.length() - 3)));
//            }
//            products = products.stream()
//                    .filter(product -> intStorage.contains(product.getDescriptions().getStorage()))
//                    .collect(Collectors.toList());
//        }
//        if (screenResolution != null && !screenResolution.isEmpty()) {
//            products = products.stream()
//                    .filter(product -> screenResolution.contains(product.getDescriptions().getScreenResolution()))
//                    .collect(Collectors.toList());
//        }
//        if (OS != null && !OS.isEmpty()) {
//            products = products.stream()
//                    .filter(product -> OS.contains(product.getDescriptions().getOS()))
//                    .collect(Collectors.toList());
//        }
//        if (bluetooth != null && !bluetooth.isEmpty()) {
//            products = products.stream()
//                    .filter(product -> bluetooth.contains(product.getDescriptions().getBluetoothVersion()))
//                    .collect(Collectors.toList());
//        }

        return products.stream().map(ProductDto::new).collect(Collectors.toList());
    }

}