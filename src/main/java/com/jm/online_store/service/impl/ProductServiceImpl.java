package com.jm.online_store.service.impl;

import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Evaluation;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.ProductDto;
import com.jm.online_store.repository.ProductRepository;
import com.jm.online_store.service.interf.EvaluationService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.UserService;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final EvaluationService evaluationService;
    private final UserService userService;

    /**
     * метод получения списка товаров
     *
     * @return List<Product>
     */
    @Transactional
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    /**
     * метод поиска Product по иденификатору.
     *
     * @param productId идентификатор Product
     * @return Optional<Product>
     */
    @Override
    public Optional<Product> findProductById(Long productId) {
        return productRepository.findById(productId);
    }

    /**
     * метод поиска Product по наименованию.
     *
     * @param productName наименование Product
     * @return Optional<Product>
     */
    @Override
    public Optional<Product> findProductByName(String productName) {
        return productRepository.findByProduct(productName);
    }

    /**
     * метод обновления Product.
     *
     * @param product экземпляр класса Product
     * @return идентификатор обновленного Product
     */
    @Override
    public Long saveProduct(Product product) {
        Map<LocalDateTime, Double> map = product.getChangePriceHistory();
        map.put(LocalDateTime.now(), product.getPrice());
        product.setChangePriceHistory(map);
        Product savedProduct = productRepository.save(product);
        return savedProduct.getId();
    }

    /**
     * метод удаления Product.
     *
     * @param idProduct идентификатор Product
     */
    @Override
    public void deleteProduct(Long idProduct) {
        Product product = productRepository.getOne(idProduct);
        product.setDeleted(true);
        productRepository.save(product);
    }

    /**
     * метод восстановления удаленного Product.
     *
     * @param idProduct идентификатор Product
     */
    @Override
    public void restoreProduct(Long idProduct) {
        Product product = productRepository.getOne(idProduct);
        product.setDeleted(false);
        productRepository.save(product);
    }

    /**
     * Метод импортирует список товаров из сохраненного XML файла
     * Записывает товары в БД
     */
    @Override
    public void importFromXMLFile(String fileName) {

        try {
            // Создается построитель документа
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            // Создается дерево DOM документа из файла
            Document document = documentBuilder.parse("uploads/import/" + fileName);
            document.getDocumentElement().normalize();

            NodeList nList = document.getElementsByTagName("product");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    String productName = eElement.getElementsByTagName("productname").item(0).getTextContent();
                    String productPrice = eElement.getElementsByTagName("price").item(0).getTextContent();
                    String productAmount = eElement.getElementsByTagName("amount").item(0).getTextContent();
                    Product product = new Product(productName, Double.parseDouble(productPrice), Integer.parseInt(productAmount));
                    saveProduct(product);
                }
            }

        } catch (ParserConfigurationException e) {
            log.error("Ошибка конфигурации парсера");
            e.printStackTrace();
        } catch (SAXException e) {
            log.error("Ошибка XML синтаксиса");
            e.printStackTrace();
        } catch (IOException e) {
            log.error("Ошибка ввода/вывода");
            e.printStackTrace();
        }
    }

    /**
     * Метод импортирует список товаров из сохраненного CSV файла
     * Записывает товары в БД
     * Для правильного считывания используется кастомная MappingStrategy
     * чтобы не перегружать Products лишними аннотациями
     *
     * @param fileName имя скачанного файла
     */
    public void importFromCSVFile(String fileName) throws FileNotFoundException {

        try (
                Reader reader = Files.newBufferedReader(Paths.get("uploads/import/" + fileName));
        ) {

            ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();
            strategy.setType(Product.class);
            String[] memberFieldsToBindTo = {"product", "price", "amount"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            CsvToBean<Product> csvToBean = new CsvToBeanBuilder(reader)
                    .withMappingStrategy(strategy)
                    .withSkipLines(1)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            Iterator<Product> productIterator = csvToBean.iterator();

            while (productIterator.hasNext()) {
                Product product = productIterator.next();
                saveProduct(product);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод выбора из БД num первых продуктов
     *
     * @param num необходимое количество продуктов
     * @return список из num продуктов
     */
    public List<Product> findNumProducts(Integer num) {
        return productRepository.findNumProducts(num);
    }

    /**
     * метод получения коллекции по мониторингу изменения цены на Product.
     *
     * @param idProduct идентификатор Product
     * @return Map<LocalDateTime, Double> changePriceHistory
     */
    @Override
    public Map<LocalDateTime, Double> getProductPriceChange(Long idProduct) {
        Product product = productRepository.getOne(idProduct);
        return product.getChangePriceHistory();
    }

    /**
     * метод изменения рейтинга товара
     *
     * @param productId id товара
     * @param rating    оценка польователем товара
     * @param user      пользователь оценивший товар
     * @return double новый рейтинг
     * @throws UserNotFoundException,ProductNotFoundException
     */
    @Transactional
    @Override
    public double changeProductRating(Long productId, double rating, User user) {
        Optional<Evaluation> evaluation = evaluationService.getEvaluation(
                user,
                findProductById(productId).orElseThrow(ProductNotFoundException::new));
        if (evaluation.isPresent()) {
            evaluation.get().setRating(rating);
            evaluationService.addEvaluation(evaluation.get());
        } else {
            evaluationService.addEvaluation(new Evaluation(
                    rating,
                    userService.findById(user.getId()).orElseThrow(UserNotFoundException::new),
                    findProductById(productId).orElseThrow(ProductNotFoundException::new)
            ));
        }
        List<Evaluation> evaluations = evaluationService.getAllProductEvaluation(findProductById(productId)
                .orElseThrow(ProductNotFoundException::new));
        double newRating = evaluations.stream().mapToDouble(s -> s.getRating()).sum() / evaluations.size();
        Product product = findProductById(productId).orElseThrow(ProductNotFoundException::new);
        product.setRating(newRating);
        return newRating;
    }

    /**
     * метод формирующий DTO для передачи на страниу товара
     * @param productId
     * @param currentUser
     * @return Optional<ProductDto> для передачи на страницу товара
     * @throws {@link UserNotFoundException}
     */
    @Override
    public Optional<ProductDto> getProductDto(Long productId, User currentUser) {
        Optional<Product> product = findProductById(productId);
        if (currentUser != null) {
//            User userFromDB = userService.findById(currentUser.getId()).orElseThrow(UserNotFoundException::new);
            User userFromDB = userService.getCurrentLoggedInUser();
            log.debug("ProductServiceImpl.getProductDTO.userfromDB = " + userFromDB.getEmail());
            if (product.isPresent()) {
                Set<Product> productSet = userFromDB.getFavouritesGoods();
                Product presentProduct = product.get();
                ProductDto productDto = new ProductDto(
                        presentProduct.getId(),
                        presentProduct.getProduct(),
                        presentProduct.getPrice(),
                        presentProduct.getRating(),
                        presentProduct.getDescriptions(),
                        presentProduct.getProductType(),
                        productSet.contains(presentProduct)
                );
                return Optional.of(productDto);
            }
        } else {
            if (product.isPresent()) {
                Product presentProduct = product.get();
                ProductDto productDto = new ProductDto(
                        presentProduct.getId(),
                        presentProduct.getProduct(),
                        presentProduct.getPrice(),
                        presentProduct.getRating(),
                        presentProduct.getDescriptions(),
                        presentProduct.getProductType(),
                        false
                );
                return Optional.of(productDto);
            }
        }
        return Optional.empty();
    }
    /**
     * Method that finds search string in Product name.
     *
     * @param searchString - {@link String} search string
     * @return - list of {@link Product} with search result
     */
    @Override
    public List<Product> findProductsByNameContains(String searchString) {
        return productRepository.findProductByProductContains(searchString);
    }

    /**
     * Method that finds search string in Product description.
     *
     * @param searchString - {@link String} search string
     * @return - list of {@link Product} with search result
     */
    @Override
    public List<Product> findProductsByDescriptionContains(String searchString) {
        return productRepository.findProductByDescriptionsContains(searchString);
    }
}