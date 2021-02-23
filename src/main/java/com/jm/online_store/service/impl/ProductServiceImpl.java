package com.jm.online_store.service.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jm.online_store.enums.ExceptionEnums;
import com.jm.online_store.exception.CustomerNotFoundException;
import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.exception.constants.ExceptionConstants;
import com.jm.online_store.model.Categories;
import com.jm.online_store.model.Customer;
import com.jm.online_store.model.Evaluation;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.ProductDto;
import com.jm.online_store.repository.ProductRepository;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.CommonSettingsService;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.EvaluationService;
import com.jm.online_store.service.interf.MailSenderService;
import com.jm.online_store.service.interf.ProductCharacteristicService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.UserService;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.mail.MessagingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
@AllArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private static final String loadPictureFrom = ".." + File.separator + "uploads" +
            File.separator + "images" + File.separator + "products" + File.separator;

    private final ProductRepository productRepository;
    private final EvaluationService evaluationService;
    private final UserService userService;
    private final CommonSettingsService commonSettingsService;
    private final MailSenderService mailSenderService;
    private final CategoriesService categoriesService;
    private final ProductCharacteristicService productCharacteristicService;
    private final CustomerService customerService;

    /**
     * Получение списка товаров
     * @return List<Product> - список товаров
     */
    @Transactional
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    /**
     * Получение списка неудаленных товаров
     * @return List<Product> - список неудаленных товаров
     */
    @Override
    public List<Product> getNotDeleteProducts() {
        return productRepository.findProductsByDelete(false);
    }

    /**
     * Получение списка товаров по имени категории.
     * @param categoryName - название категории товара
     * @return List<Product> - список товаров
     */
    @Override
    public List<Product> findProductsByCategoryName(String categoryName) {
        Categories category = categoriesService.getCategoryByCategoryName(categoryName).get();
        return category.getProducts();
    }


    /**
     * Создание XLSX-файла из списка товаров по категории
     * @param products - список товаров
     * @param category - нужная категория
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
     * Поиск товара по его идентификатору.
     * @param productId идентификатор товара.
     * @return Optional<Product> - товар
     */
    @Override
    public Optional<Product> findProductById(Long productId) {
        return productRepository.findById(productId);
    }

    @Override
    public Product getProductById(Long productId) {
        return productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
    }

    /**
     * Поиск товара по его наименованию.
     * @param productName наименование товара.
     * @return Optional<Product> - товар
     */
    @Override
    public Optional<Product> findProductByName(String productName) {
        return productRepository.findByProduct(productName);
    }

    /**
     * Получение списка всех товаров по возрастанию рейтинга.
     * @return List<Product> - список товаров, отсортированный по возрастанию рейтинга.
     */
    @Override
    public List<Product> findAllOrderByRatingAsc() {
        return productRepository.findAllOrderByRatingAsc();
    }

    /**
     * Получение списка всех товаров по убыванию рейтинга.
     * @return List<Product> - список товаров, отсортированный по убыванию рейтинга.
     */
    @Override
    public List<Product> findAllOrderByRatingDesc() {
        return productRepository.findAllOrderByRatingDesc();
    }

    /**
     * Обновление товара.
     * @param product экземпляр класса {@link Product}
     * @return идентификатор обновленного товара.
     */
    @Override
    public Long saveProduct(Product product) {

        if (product.getRating() == null) {
            product.setRating(0d);
        }
        if (product.getProductPictureName().isEmpty()) {
            product.setProductPictureName(loadPictureFrom + "defaultPictureProduct.jpg");
        } else {
            product.setProductPictureName(product.getProductPictureName());
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
     * о снижении цены. Для зарегистрированных пользователей письма отправляются только при получении
     * согласия пользователя на такие рассылки (таблица Users, значение confirm_receive_email - CONFIRMED)
     * рассылка для незарегистрированных пользователей отключена (чтобы не спамить).
     *
     * @param product товар
     * @param oldPrice старая цена товара.
     * @param newPrice новая цена товара.
     */
    public void sendNewPrice(Product product, double oldPrice, double newPrice) {
        Product productToSend = findProductById(product.getId()).get();
        Set<String> emails = productToSend.getPriceChangeSubscribers();
        String templateBody = commonSettingsService
                .getSettingByName("price_change_distribution_template")
                .getTextValue();
        String messageBody;
        for (String email : emails) {
            Optional<User> user = userService.findByEmail(email);
            if (user.isPresent() && user.get().getConfirmReceiveEmail().toString().equals("CONFIRMED")) { //рассылка для незарегистрированных юзеров отключена.
                if (user.get().getFirstName() != null) {
                    messageBody = templateBody.replaceAll("@@user@@", user.get().getFirstName());
                } else {
                    messageBody = templateBody.replaceAll("@@user@@", "Покупатель");
                }
                messageBody = messageBody.replaceAll("@@oldPrice@@", String.valueOf(oldPrice));
                messageBody = messageBody.replaceAll("@@newPrice@@", String.valueOf(newPrice));
                messageBody = messageBody.replaceAll("@@product@@", product.getProduct());
                try {
                    mailSenderService.sendHtmlMessage(email, "Снижена цена на товар!", messageBody, "Price change");
                } catch (MessagingException e) {
                    log.debug("Can not send mail about price changes to product {} to {}", product.getProduct(), email);
                }
            }
        }
    }

    /**
     * Удаление товара.
     * @param idProduct идентификатор товара.
     */
    @Override
    public void deleteProduct(Long idProduct) {
        Product product = productRepository.getOne(idProduct);
        product.setDeleted(true);
        productRepository.save(product);
    }

    /**
     * Возвращает кол-во определенного товара в БД.
     * @param idProduct идентификатор товара.
     * @return количество данного товара в БД.
     */
    @Override
    public int findProductAmount(Long idProduct) {
        Product product = productRepository.getOne(idProduct);
        return product.getAmount();
    }

    /**
     * Восстановление удаленного товара.
     * @param idProduct идентификатор товара.
     */
    @Override
    public void restoreProduct(Long idProduct) {
        Product product = productRepository.getOne(idProduct);
        product.setDeleted(false);
        productRepository.save(product);
    }

    /**
     * Импорт списка товаров из XML-файла.
     * Записывает товары в БД.
     * Парсит категории из файла.
     * @param fileName имя файла.
     */
    @Override
    public void importFromXMLFile(String fileName) {
//        try {
//            // Создается построитель документа
//            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//
//            // Создается дерево DOM документа из файла
//            Document document = documentBuilder.parse("uploads/import/" + fileName);
//            document.getDocumentElement().normalize();
//
//            NodeList nList = document.getElementsByTagName("product");
//
//            for (int temp = 0; temp < nList.getLength(); temp++) {
//
//                Node nNode = nList.item(temp);
//                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
//
//                    Element eElement = (Element) nNode;
//
//                    String productName = eElement.getElementsByTagName("productname").item(0).getTextContent();
//                    String productPrice = eElement.getElementsByTagName("price").item(0).getTextContent();
//                    String productAmount = eElement.getElementsByTagName("amount").item(0).getTextContent();
//                    String categoryName = eElement.getElementsByTagName("categoryname").item(0).getTextContent();
//                    Product product = new Product(productName, Double.parseDouble(productPrice), Integer.parseInt(productAmount));
//                    categoriesService.addToProduct(product, categoryName);
//
//                    String characteristicName;
//                    String characteristicValue;
//                    if (eElement.getElementsByTagName("characteristicname").getLength() != 0
//                            && eElement.getElementsByTagName("characteristicvalue").getLength() != 0) {
//                        characteristicName = eElement.getElementsByTagName("characteristicname").item(0).getTextContent();
//                        characteristicValue = eElement.getElementsByTagName("characteristicvalue").item(0).getTextContent();
//
//                        List<String> listNames = Arrays.asList(characteristicName.split(","));
//                        List<String> listValues = Arrays.asList(characteristicValue.split(","));
//                        Map<String, String> map = new HashMap<>();
//
//                        for (int i = 0; i < listValues.size(); i++) {
//                            map.put(listNames.get(i), listValues.get(i));
//                        }
//                        for (Map.Entry<String, String> entry : map.entrySet()) {
//                            productCharacteristicService.addProductCharacteristic(findProductByName(productName).orElseThrow(ProductNotFoundException::new).getId(),
//                                    entry.getKey(), entry.getValue());
//                        }
//                    }
//                }
//            }
//
//        } catch (ParserConfigurationException e) {
//            log.error("Ошибка конфигурации парсера");
//            e.printStackTrace();
//        } catch (SAXException e) {
//            log.error("Ошибка XML синтаксиса");
//            e.printStackTrace();
//        } catch (IOException e) {
//            log.error("Ошибка ввода/вывода");
//            e.printStackTrace();
//        }
    }

    /**
     * Импорт списка товаров из XML-файла.
     * Записывает товары в БД.
     * Категорию получает из окна загрузки файла в кабинете менеджера.
     * @param fileName имя файла.
     * @param categoryId идентификатор категории товара.
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
     * Импорт списка товаров из CSV-файла.
     * Записывает товары в БД.
     * Для правильного считывания используется кастомная MappingStrategy, 
     * чтобы не перегружать Products лишними аннотациями.
     *
     * @param fileName имя файла
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
     * Импорт списка товаров из CSV-файла.
     * Записывает товары в БД.
     * Для правильного считывания используется кастомная MappingStrategy, 
     * чтобы не перегружать Products лишними аннотациями.
     *
     * @param fileName имя файла.
     * @param categoryId категория, полученная из окна загрузки файла в кабинете менеджера.
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
     * Выбирает из БД num первых товаров.
     * @param num необходимое количество товаров.
     * @return список из запрошенного кол-ва товаров.
     */
    public List<Product> findNumProducts(Integer num) {
        return productRepository.findNumProducts(num);
    }

    /**
     * Получение коллекции по мониторингу изменения цены на товар.
     * @param idProduct идентификатор товара.
     * @return Map<LocalDateTime, Double> changePriceHistory
     */
    @Override
    public Map<LocalDateTime, Double> getProductPriceChange(Long idProduct) {
        Product product = productRepository.getOne(idProduct);
        return product.getChangePriceHistory();
    }

    /**
     * Изменение рейтинга товара.
     * @param productId идентификатор товара.
     * @param rating оценка пользователем товара.
     * @param user пользователь, оценивший товар.
     * @return double новый рейтинг.
     * @throws UserNotFoundException, ProductNotFoundException
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
     * метод формирующий DTO для передачи на страницу товара.
     * @param productId идентификатор товара.
     * @param currentUser текущий пользователь.
     * @return Optional<ProductDto> - DTO для передачи на страницу товара.
     * @throws {@link UserNotFoundException}
     */
    @Override
    public Optional<ProductDto> getProductDto(Long productId, User currentUser) {
        Optional<Product> product = findProductById(productId);
        if (currentUser != null) {
            Customer customer = customerService.findById(currentUser.getId()).orElseThrow(()
                    -> new CustomerNotFoundException(ExceptionEnums.CUSTOMER.getText() + ExceptionConstants.NOT_FOUND));
            if (product.isPresent()) {
                Set<Product> productSet = customer.getFavouritesGoods();
                Product presentProduct = product.get();
                ProductDto productDto = new ProductDto(
                        presentProduct.getId(),
                        presentProduct.getProduct(),
                        presentProduct.getPrice(),
                        presentProduct.getRating(),
                        presentProduct.getDescriptions(),
                        presentProduct.getProductType(),
                        presentProduct.getProductPictureName(),
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
                        presentProduct.getProductPictureName(),
                        false
                );
                return Optional.of(productDto);
            }
        }
        return Optional.empty();
    }

    /**
     * Method that finds search string in Product name.
     * @param searchString - {@link String} search string
     * @return - list of {@link Product} with search result
     */
    @Override
    public List<Product> findProductsByNameContains(String searchString) {
        return productRepository.findProductByProductContains(searchString);
    }

    /**
     * Method that finds search string in Product description.
     * @param searchString - {@link String} search string
     * @return - list of {@link Product} with search result
     */
    @Override
    public List<Product> findProductsByDescriptionContains(String searchString) {
        return productRepository.findProductByDescriptionsContains(searchString);
    }

    /**
     * Добавление нового email в рассылку при изменении цены на товар.
     * Помимо этого направляет пользователю письмо с просьбой подтвердить получение рассылки.
     * Без этого согласия получать письма об изменении цен он не будет. Письмо отправляется при каждом нажатии
     * на "Подписаться", пока не будет получено согласие. При этом в базу для рассылки он будет заноситься.
     *
     * @param body тело запроса
     * @return true если удалось добавить email, false если такой email уже есть.
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
     * Редактирование информации о товаре.
     * @param product изменённый товар.
     * @return идентификатор изменённого товара.
     */
    @Override
    public Long editProduct(Product product) {

        if (product.getProductPictureName().isEmpty()) {
            product.setProductPictureName("defaultProductImage.jpg");
        } else {
            product.setProductPictureName(product.getProductPictureName());
        }

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
     * Проверяет существование товара в БД.
     * @param productName - название товара.
     * @return false - если такой товар не был найден, 
     * true - если такой товар существует.
     */
    @Override
    @Transactional
    public boolean existsProductByProduct(String productName) {
        return productRepository.existsProductByProduct(productName);
    }

    /**
     * Поиск товаров, на изменения цен которых
     * подписан авторизованный пользователь по email.
     * @return List<Product> список товаров.
     */
    @Override
    public List<Product> findTrackableProductsByLoggedInUser() {
        return productRepository.findProductByPriceChangeSubscribersEquals(userService.getCurrentLoggedInUser().getEmail());
    }

    /**
     * Удаление подписки залогиненного пользователя на изменение цены товара.
     * @param productId уникальный идентификатор товара.
     */
    @Transactional
    @Override
    public void deleteProductFromTrackedForLoggedInUser(long productId) {
        productRepository.deletePriceChangeSubscriber(userService.getCurrentLoggedInUser().getEmail(), productId);
    }

    @Override
    public void importFromXMLFile(MultipartFile file) {
        writeFile(file);
        try {
            // Создается построитель документа
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Создается дерево DOM документа из файла
            Document document = documentBuilder.parse("C:\\Users\\Dalan\\Desktop\\dev\\online_store\\uploads\\import\\" + file.getOriginalFilename());
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

    @Override
    public void importFromCSVFile(MultipartFile multipartFile) throws FileNotFoundException {

    }



    private void writeFile(@RequestParam("file") MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            if (bytes.length == 0)
                throw new Exception("File is empty");
            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream("C:\\Users\\Dalan\\Desktop\\dev\\online_store\\uploads\\import\\" + file.getOriginalFilename()));
            stream.write(bytes);
            stream.close();
        } catch (Exception e) {
            log.error("Ошибка сохранения файла");
            e.printStackTrace();
        }
        log.debug("тип файла" + FilenameUtils.getExtension(file.getOriginalFilename()));
    }
}
