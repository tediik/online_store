package com.jm.online_store.service.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Categories;
import com.jm.online_store.model.ConfirmationToken;
import com.jm.online_store.model.Evaluation;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.ProductDto;
import com.jm.online_store.repository.ConfirmationTokenRepository;
import com.jm.online_store.repository.ProductRepository;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.CommonSettingsService;
import com.jm.online_store.service.interf.EvaluationService;
import com.jm.online_store.service.interf.MailSenderService;
import com.jm.online_store.service.interf.ProductCharacteristicService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.UserService;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.mail.MessagingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final EvaluationService evaluationService;
    private final UserService userService;
    private final CommonSettingsService commonSettingsService;
    private final MailSenderService mailSenderService;
    private final CategoriesService categoriesService;
    private final ProductCharacteristicService productCharacteristicService;
    private final ConfirmationTokenRepository confirmTokenRepository;

    @Value("${spring.server.url}")
    private String urlActivate;

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
     * Метод для получения списка неудаленных товаров
     */
    @Override
    public List<Product> getNotDeleteProducts() {
        return productRepository.findProductsByDelete(false);
    }

    /**
     * метод для получения списка Product по имени категории.
     *
     * @param categoryName идентификатор Product
     * @return List<Product>
     */
    @Override
    public List<Product> findProductsByCategoryName(String categoryName) {
        Categories category = categoriesService.getCategoryByCategoryName(categoryName).get();
        return category.getProducts();
    }


    /**
     * Метод для создания XLSX файла из списка товаров по категории
     *
     * @param products товары
     * @param category нужная категория
     * @return Excel-документ
     */
    @Override
    public XSSFWorkbook createXlsxDoc(List<Product> products, String category) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Products report");
        int rowCount = 0;

        XSSFRow row = sheet.createRow(rowCount++);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("ID");
        cell = row.createCell(1);
        cell.setCellValue("Название");
        cell = row.createCell(2);
        cell.setCellValue("Цена");
        cell = row.createCell(3);
        cell.setCellValue("Количество");
        cell = row.createCell(4);
        cell.setCellValue("Рейтинг");
        cell = row.createCell(5);
        cell.setCellValue("Категория");

        for (Product aProduct : products) {
            row = sheet.createRow(rowCount++);

            cell = row.createCell(0);
            cell.setCellValue(aProduct.getId());

            cell = row.createCell(1);
            cell.setCellValue(aProduct.getProduct());

            cell = row.createCell(2);
            cell.setCellValue(aProduct.getPrice());

            cell = row.createCell(3);
            cell.setCellValue(aProduct.getAmount());

            cell = row.createCell(4);
            cell.setCellValue(aProduct.getRating());

            cell = row.createCell(5);
            cell.setCellValue(category);
        }
        return workbook;
    }

    /**
     * метод поиска Product по идентификатору.
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
     * Метод получения списка всех продуктов по возрастанию рейтинга
     *
     * @return List<Product>
     */
    @Override
    public List<Product> findAllOrderByRatingAsc() {
        return productRepository.findAllOrderByRatingAsc();
    }

    /**
     * Метод получения списка всех продуктов по убыванию рейтинга
     *
     * @return List<Product>
     */
    @Override
    public List<Product> findAllOrderByRatingDesc() {
        return productRepository.findAllOrderByRatingDesc();
    }

    /**
     * метод обновления Product.
     *
     * @param product экземпляр класса Product
     * @return идентификатор обновленного Product
     */
    @Override
    public Long saveProduct(Product product) {
        if (product.getRating() == null) {
            product.setRating(0d);
        }
        Product savedProduct = productRepository.save(product);
        return savedProduct.getId();
    }

    @Override
    public void saveAllProducts(List<Product> products) {
        productRepository.saveAll(products);
    }

    /**
     * Метод отправляющий сообщения пользователям, которые подписаны на уведомления
     * о снижении цены. Для зарегистрированных пользователей письма отпровляются только при получении
     * согласия юзера на таковые рассылки (таблица Users, значение confirm_receive_email - CONFIRMED)
     * рассылка для незарегистрированных юзеров отключена, чтобы не спамить
     * @param product  продукт
     * @param oldPrice старая цена продукта
     * @param newPrice новая цена продукта
     */
    public void sendNewPrice(Product product, double oldPrice, double newPrice) {
        Product productToSend = findProductById(product.getId()).get();
        Long productId = productToSend.getId();
        Set<String> emails = productToSend.getPriceChangeSubscribers();
        String templateBody = commonSettingsService
                .getSettingByName("price_change_distribution_template")
                .getTextValue();
        String messageBody;
        for (String email : emails) {
            Optional<User> user = userService.findByEmail(email);
            if (user.isPresent() && user.get().getConfirmReceiveEmail().toString().equals("CONFIRMED")) { //рассылка для незарегистрированных юзеров отключена.
                ConfirmationToken confirmationToken = confirmTokenRepository.findByUserEmail(email);
                if (user.get().getFirstName() != null) {
                    messageBody = templateBody.replaceAll("@@user@@", user.get().getFirstName());
                } else {
                    messageBody = templateBody.replaceAll("@@user@@", "Покупатель");
                }
                messageBody = messageBody.replaceAll("@@oldPrice@@", String.valueOf(oldPrice));
                messageBody = messageBody.replaceAll("@@newPrice@@", String.valueOf(newPrice));
                messageBody = messageBody.replaceAll("@@product@@", product.getProduct());
                messageBody = messageBody.replaceAll("@@idProduct@@", Long.toString(product.getId()));
                messageBody = messageBody.replaceAll("@@url@@", urlActivate  + "/cancelMailing/" + confirmationToken.getConfirmationToken()+ "/" + productId );

                try {
                    mailSenderService.sendHtmlMessage(email, "Снижена цена на товар!", messageBody, "Price change");
                } catch (MessagingException e) {
                    log.debug("Can not send mail about price changes to product {} to {}", product.getProduct(), email);
                }
            }
        }
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
     * Метод находит кол-во определенного продукта в БД Product
     *
     * @param idProduct идентификатор Product
     * @return количество данного продукта в БД Product
     */
    @Override
    public int findProductAmount(Long idProduct) {
        Product product = productRepository.getOne(idProduct);
        return product.getAmount();

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
     * парсит категории из файла
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
                    String categoryName = eElement.getElementsByTagName("categoryname").item(0).getTextContent();
                    Product product = new Product(productName, Double.parseDouble(productPrice), Integer.parseInt(productAmount));
                    categoriesService.addToProduct(product, categoryName);

                    String characteristicName;
                    String characteristicValue;
                    if (eElement.getElementsByTagName("characteristicname").getLength() != 0
                            && eElement.getElementsByTagName("characteristicvalue").getLength() != 0) {
                        characteristicName = eElement.getElementsByTagName("characteristicname").item(0).getTextContent();
                        characteristicValue = eElement.getElementsByTagName("characteristicvalue").item(0).getTextContent();

                        List<String> listNames = Arrays.asList(characteristicName.split(","));
                        List<String> listValues = Arrays.asList(characteristicValue.split(","));
                        Map<String, String> map = new HashMap<>();

                        for (int i = 0; i < listValues.size(); i++) {
                            map.put(listNames.get(i), listValues.get(i));
                        }
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            productCharacteristicService.addProductCharacteristic(findProductByName(productName).orElseThrow(ProductNotFoundException::new).getId(),
                                    entry.getKey(), entry.getValue());
                        }
                    }
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
     * Метод импортирует список товаров из сохраненного XML файла
     * Записывает товары в БД
     * категорию получает из окна загрузки файла в кабинете менеджера
     */
    @Override
    public void importFromXMLFile(String fileName, Long categoryId) {

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
                    categoriesService.addToProduct(product, categoryId);

                    String characteristicName;
                    String characteristicValue;
                    if (eElement.getElementsByTagName("characteristicname").getLength() != 0
                            && eElement.getElementsByTagName("characteristicvalue").getLength() != 0) {
                        characteristicName = eElement.getElementsByTagName("characteristicname").item(0).getTextContent();
                        characteristicValue = eElement.getElementsByTagName("characteristicvalue").item(0).getTextContent();

                        List<String> listNames = Arrays.asList(characteristicName.split(","));
                        List<String> listValues = Arrays.asList(characteristicValue.split(","));
                        Map<String, String> map = new HashMap<>();

                        for (int i = 0; i < listValues.size(); i++) {
                            map.put(listNames.get(i), listValues.get(i));
                        }
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            productCharacteristicService.addProductCharacteristic(findProductByName(productName).orElseThrow(ProductNotFoundException::new).getId(),
                                    entry.getKey(), entry.getValue());
                        }
                    }
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

            productRepository.saveAll(csvToBean);

        } catch (IOException e) {
            e.printStackTrace();
            log.warn(e.toString());
        }
    }

    /**
     * Метод импортирует список товаров из сохраненного CSV файла
     * Записывает товары в БД
     * Для правильного считывания используется кастомная MappingStrategy
     * чтобы не перегружать Products лишними аннотациями
     *
     * @param fileName   имя скачанного файла
     * @param categoryId категория , полученная из окна загрузки файла в кабинете менеджера
     */
    public void importFromCSVFile(String fileName, Long categoryId) throws FileNotFoundException {

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

            for (Product product : csvToBean) {
                categoriesService.addToProduct(product, categoryId);
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.warn(e.toString());
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
     * метод удаления Product из таблицы рассылки изменения цены.
     *
     * @param email почта пользователя
     * @param idProduct идентификатор Product
     */
    @Transactional
    @Override
    public void deleteProductPriceChangeById(String email,Long idProduct) {
        productRepository.deletePriceChangeSubscriber(email,idProduct);
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
     * метод формирующий DTO для передачи на страницу товара
     *
     * @param productId
     * @param currentUser
     * @return Optional<ProductDto> для передачи на страницу товара
     * @throws {@link UserNotFoundException}
     */
    @Override
    public Optional<ProductDto> getProductDto(Long productId, User currentUser) {
        Optional<Product> product = findProductById(productId);
        if (currentUser != null) {
            User userFromDB = userService.findById(currentUser.getId()).orElseThrow(UserNotFoundException::new);
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

    /**
     * Метод для добавления нового email в рассылку при изменении цены на товар
     * Помимо этого направляет юзеру письмо с просьбой подтвердить получение рассылки.
     * Без этого согласия получать письма об изменении цен он не будет. Письмо отправляется при каждом нажатии
     * на "Подписаться", пока не будет получено согласие. При этом в базу для рассылки он будет заноситься.
     * @param body тело запроса
     * @return true если удалось добавить email, false если такой email уже есть
     */
    @Override
    public boolean addNewSubscriber(ObjectNode body) {
        String email;
        if (body.has("email")) {
            email = body.get("email").asText();
        } else {
            email = userService.getCurrentLoggedInUser().getEmail();
        }
        if (userService.findByEmail(email).isPresent() && !userService.findByEmail(email).get()
                .getConfirmReceiveEmail().toString().equals("CONFIRMED")) {
            userService.sendConfirmationSubscribeLetter(email);
        }
        Product product = findProductById(body.get("id").asLong()).orElseThrow(ProductNotFoundException::new);
        Set<String> emails = product.getPriceChangeSubscribers();
        if (emails.contains(email)) {
            return false;
        } else {
            emails.add(email);
            product.setPriceChangeSubscribers(emails);
            saveProduct(product);
            return true;
        }
    }

    /**
     * Метод для редактирования информации о товаре
     *
     * @param product изменённый товар
     * @return id изменённого товара
     */
    @Override
    public Long editProduct(Product product) {
        Map<LocalDateTime, Double> map = findProductById(product.getId())
                .orElseThrow(ProductNotFoundException::new)
                .getChangePriceHistory();
        map.put(LocalDateTime.now(), product.getPrice());
        product.setChangePriceHistory(map);
        double oldPrice = findProductById(product.getId()).get().getPrice();
        double newPrice = product.getPrice();
        if (newPrice < oldPrice) {
            sendNewPrice(product, oldPrice, newPrice);
        }
        product.setPriceChangeSubscribers(findProductById(product.getId())
                .orElseThrow(ProductNotFoundException::new)
                .getPriceChangeSubscribers());
        return saveProduct(product);
    }

    /**
     * Метод проверяет существование товара в БД.
     *
     * @param productName - поле по которому проверяем товар
     * @return false -  Если такой товар не был найден.
     * true -   Если такой товар существует.
     */
    @Override
    @Transactional
    public boolean existsProductByProduct(String productName) {
        return productRepository.existsProductByProduct(productName);
    }

    /**
     * Метод для поиска товаров, на изменения цен которых
     * подписан авторизованный пользователь по email
     *
     * @return List<Product> список товаров
     */
    @Override
    public List<Product> findTrackableProductsByLoggedInUser() {
        return productRepository.findProductByPriceChangeSubscribersEquals(userService.getCurrentLoggedInUser().getEmail());
    }

    /**
     * Метод для удаления подписки залогиненного пользователя на изменение цены товара
     *
     * @param productId уникальный идентификатор товара
     */
    @Transactional
    @Override
    public void deleteProductFromTrackedForLoggedInUser(long productId) {
        productRepository.deletePriceChangeSubscriber(userService.getCurrentLoggedInUser().getEmail(), productId);
    }
}