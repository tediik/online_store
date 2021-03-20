package com.jm.online_store.service.impl;

import com.jm.online_store.model.filter.CheckboxFilter;
import com.jm.online_store.model.filter.Filter;
import com.jm.online_store.model.filter.FilterData;
import com.jm.online_store.model.filter.FilterType;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.filter.Filters;
import com.jm.online_store.model.filter.RangeFilter;
import com.jm.online_store.repository.DescriptionRepository;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Transactional
public class FilterServiceImpl implements FilterService {
    private final ProductService productService;
    private final CharacteristicService characteristicService;

    private final String rangeFilter = FilterType.RANGE.getMessage();
    private final String checkboxFilter = FilterType.CHECKBOX.getMessage();

    private final Comparator<Product> priceComparator = Comparator.comparing(Product::getPrice);

    @Override
    public Filters getSmartphoneFilters(String category) {
        List<Filter> filterList = new ArrayList<>();

        String productsCategory = Transliteration.latinToCyrillic(category);
        List<Product> products = productService.findProductsByCategoryName(productsCategory);
        Set<String> brands = new HashSet<>();
        Set<String> ram = new HashSet<>();
        Set<String> storage = new HashSet<>();
        Set<String> screenResolution = new HashSet<>();
        Set<String> OS = new HashSet<>();

        FilterData priceRangeFilterData = new RangeFilter(Math.round(Collections.min(products, priceComparator).getPrice()),
                Math.round(Collections.max(products, priceComparator).getPrice()));
        Filter priceRangeFilter = new Filter(rangeFilter, "Цена", "price", priceRangeFilterData);

        for (Product p : products) {
            brands.add(p.getDescriptions().getProducer());
            ram.add(p.getDescriptions().getRam() + " МБ");
            storage.add(p.getDescriptions().getStorage() + " ГБ");
            screenResolution.add(p.getDescriptions().getScreenResolution());
            OS.add(p.getDescriptions().getOS());
        }
        FilterData brandCheckboxFilterData = new CheckboxFilter(brands);
        FilterData ramCheckboxFilterData = new CheckboxFilter(ram);
        FilterData storageCheckboxFilterData = new CheckboxFilter(storage);
        FilterData nfcCheckboxFilterData = new CheckboxFilter(Set.of("Да", "Нет"));
        FilterData screenResCheckboxFilterData = new CheckboxFilter(screenResolution);
        FilterData OSCheckboxFilterData = new CheckboxFilter(OS);

        Filter brandCheckboxFilter = new Filter(checkboxFilter, "Производитель", "brand", brandCheckboxFilterData);
        Filter ramCheckboxFilter = new Filter(checkboxFilter, characteristicService.findCharacteristicById(5L).get().getCharacteristicName(), "RAM", ramCheckboxFilterData);
        Filter storageCheckboxFilter = new Filter(checkboxFilter, characteristicService.findCharacteristicById(6L).get().getCharacteristicName(), "storage", storageCheckboxFilterData);
        Filter nfcCheckboxFilter = new Filter(checkboxFilter, characteristicService.findCharacteristicById(8L).get().getCharacteristicName(), "nfc", nfcCheckboxFilterData);
        Filter screenResCheckboxFilter = new Filter(checkboxFilter, characteristicService.findCharacteristicById(2L).get().getCharacteristicName(), "screenResolution", screenResCheckboxFilterData);
        Filter OSCheckboxFilter = new Filter(checkboxFilter, characteristicService.findCharacteristicById(16L).get().getCharacteristicName(), "OS", OSCheckboxFilterData);

        filterList.add(priceRangeFilter);
        filterList.add(brandCheckboxFilter);
        filterList.add(ramCheckboxFilter);
        filterList.add(storageCheckboxFilter);
        filterList.add(nfcCheckboxFilter);
        filterList.add(screenResCheckboxFilter);
        filterList.add(OSCheckboxFilter);
        return new Filters(filterList);
    }

}