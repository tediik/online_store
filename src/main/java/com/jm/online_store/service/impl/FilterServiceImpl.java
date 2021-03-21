package com.jm.online_store.service.impl;

import com.jm.online_store.model.Description;
import com.jm.online_store.model.dto.ProductDto;
import com.jm.online_store.model.filter.CheckboxFilter;
import com.jm.online_store.model.filter.Filter;
import com.jm.online_store.model.filter.FilterData;
import com.jm.online_store.model.filter.FilterType;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.filter.Filters;
import com.jm.online_store.model.filter.RangeFilter;
import com.jm.online_store.service.interf.CharacteristicService;
import com.jm.online_store.service.interf.FilterService;
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

@Service
@AllArgsConstructor
@Transactional
public class FilterServiceImpl implements FilterService {
    private final ProductService productService;
    private final CharacteristicService characteristicService;

    private final Comparator<Product> priceComparator = Comparator.comparing(Product::getPrice);

    @Override
    public Filters getFilters(String category) {
        List<Filter> filterList = new ArrayList<>();

        String productsCategory = Transliteration.latinToCyrillic(category);
        List<Product> products = productService.findProductsByCategoryName(productsCategory);

        Set<CheckboxFilter.Label> brands = new TreeSet<>();
        Set<CheckboxFilter.Label> color = new TreeSet<>();
        Set<CheckboxFilter.Label> ram = new TreeSet<>();
        Set<CheckboxFilter.Label> storage = new TreeSet<>();
        Set<CheckboxFilter.Label> nfc = new TreeSet<>(Arrays.asList(new CheckboxFilter.Label("Есть"), new CheckboxFilter.Label("Нет")));
        Set<CheckboxFilter.Label> screenResolution = new TreeSet<>();
        Set<CheckboxFilter.Label> OS = new TreeSet<>();
        Set<CheckboxFilter.Label> bluetooth = new TreeSet<>();

        FilterData priceRangeFilterData = new RangeFilter(Math.round(Collections.min(products, priceComparator).getPrice()),
                Math.round(Collections.max(products, priceComparator).getPrice()));
        Filter priceRangeFilter = new Filter(FilterType.Range, "Цена", "price", priceRangeFilterData);
        filterList.add(priceRangeFilter);

        for (Product product : products) {
            Description description = product.getDescriptions();
            brands.add(new CheckboxFilter.Label(description.getProducer()));
            color.add(new CheckboxFilter.Label(description.getColor()));
            if (description.getRam() != 0) {
                ram.add(new CheckboxFilter.Label(description.getRam() + " МБ"));
            }
            if (description.getStorage() != 0) {
                storage.add(new CheckboxFilter.Label(description.getStorage() + " ГБ"));
            }
            if (description.getScreenResolution() != null) {
                screenResolution.add(new CheckboxFilter.Label(description.getScreenResolution()));
            }
            if (description.getOS() != null) {
                OS.add(new CheckboxFilter.Label(description.getOS()));
            }
            if (description.getBluetoothVersion() != null) {
                bluetooth.add(new CheckboxFilter.Label(description.getBluetoothVersion()));
            }
        }

        Filter brandCheckboxFilter = new Filter(FilterType.Checkbox, "Производитель", "brand", new CheckboxFilter(brands));

        filterList.add(brandCheckboxFilter);
        Filter colorCheckboxFilter = new Filter(FilterType.Checkbox, "Цвет", "color", new CheckboxFilter(color));
        filterList.add(colorCheckboxFilter);

        if (!ram.isEmpty()) {
            Filter ramCheckboxFilter = new Filter(FilterType.Checkbox, characteristicService.findCharacteristicById(5L).get().getCharacteristicName(), "RAM", new CheckboxFilter(ram));
            filterList.add(ramCheckboxFilter);
        }
        if (!storage.isEmpty()) {
            Filter storageCheckboxFilter = new Filter(FilterType.Checkbox, characteristicService.findCharacteristicById(6L).get().getCharacteristicName(), "storage", new CheckboxFilter(storage));
            filterList.add(storageCheckboxFilter);
        }
        if (!nfc.isEmpty()) {
            Filter nfcCheckboxFilter = new Filter(FilterType.Checkbox, characteristicService.findCharacteristicById(8L).get().getCharacteristicName(), "nfc", new CheckboxFilter(nfc));
            filterList.add(nfcCheckboxFilter);
        }
        if (!screenResolution.isEmpty()) {
            Filter screenResCheckboxFilter = new Filter(FilterType.Checkbox, characteristicService.findCharacteristicById(2L).get().getCharacteristicName(), "screenResolution", new CheckboxFilter(screenResolution));
            filterList.add(screenResCheckboxFilter);
        }
        if (!OS.isEmpty()) {
            Filter OSCheckboxFilter = new Filter(FilterType.Checkbox, characteristicService.findCharacteristicById(16L).get().getCharacteristicName(), "OS", new CheckboxFilter(OS));
            filterList.add(OSCheckboxFilter);
        }
        if (!bluetooth.isEmpty()) {
            Filter bluetoothCheckboxFilter = new Filter(FilterType.Checkbox, "Bluetooth", "bluetooth", new CheckboxFilter(bluetooth));
            filterList.add(bluetoothCheckboxFilter);
        }

        return new Filters(filterList);
    }

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
        if (price != null && !price.isEmpty()) {
            products = products.stream()
                    .filter(product -> product.getPrice() >= price.get(0))
                    .filter(product -> product.getPrice() <= price.get(1))
                    .collect(Collectors.toList());
        }
        if (brands != null && !brands.isEmpty()) {
            products = products.stream()
                    .filter(product -> brands.contains(product.getDescriptions().getProducer()))
                    .collect(Collectors.toList());
        }
        if (color != null && !color.isEmpty()) {
            products = products.stream()
                    .filter(product -> color.contains(product.getDescriptions().getColor()))
                    .collect(Collectors.toList());
        }
        if (RAM != null && !RAM.isEmpty()) {
            List<Integer> intRam = new ArrayList<>();
            for (String r : RAM) {
                intRam.add(Integer.parseInt(r.substring(0, r.length() - 3)));
            }
            products = products.stream()
                    .filter(product -> intRam.contains(product.getDescriptions().getRam()))
                    .collect(Collectors.toList());
        }
        if (storage != null && !storage.isEmpty()) {
            List<Integer> intStorage = new ArrayList<>();
            for (String s : storage) {
                intStorage.add(Integer.parseInt(s.substring(0, s.length() - 3)));
            }
            products = products.stream()
                    .filter(product -> intStorage.contains(product.getDescriptions().getStorage()))
                    .collect(Collectors.toList());
        }
        if (screenResolution != null && !screenResolution.isEmpty()) {
            products = products.stream()
                    .filter(product -> screenResolution.contains(product.getDescriptions().getScreenResolution()))
                    .collect(Collectors.toList());
        }
        if (OS != null && !OS.isEmpty()) {
            products = products.stream()
                    .filter(product -> OS.contains(product.getDescriptions().getOS()))
                    .collect(Collectors.toList());
        }
        if (bluetooth != null && !bluetooth.isEmpty()) {
            products = products.stream()
                    .filter(product -> bluetooth.contains(product.getDescriptions().getBluetoothVersion()))
                    .collect(Collectors.toList());
        }


        return products.stream().map(ProductDto::new).collect(Collectors.toList());
    }

}