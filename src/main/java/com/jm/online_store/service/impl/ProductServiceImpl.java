package com.jm.online_store.service.impl;

import com.jm.online_store.exception.EmailAlreadyExistsException;
import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Evaluation;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.ProductDto;
import com.jm.online_store.repository.ProductRepository;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.CommonSettingsService;
import com.jm.online_store.service.interf.EvaluationService;
import com.jm.online_store.service.interf.MailSenderService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.UserService;
import com.jm.online_store.util.ValidationUtils;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
    private final CommonSettingsService commonSettingsService;
    private final MailSenderService mailSenderService;
    private final CategoriesService categoriesService;

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
        List<Product> products = findAll();
        products.removeIf(Product::isDeleted);
        return products;
    }


    /**
     * Метод для создания XLSX файла из списка товаров по категории
     *
     * @param products товары
     * @param category нужная категория
     * @return Excel-документ
     */
    @Override
    public XSSFWorkbook createXlsxDoc(List<Product> products, String category){
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
     * метод обновления Product.
     *
     * @param product экземпляр класса Product
     * @return идентификатор обновленного Product
     */
    @Override
    public Long saveProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        return savedProduct.getId();
    }

    @Override
    public void saveAllProducts(List<Product> products) {
        for (Product product : products) {
            saveProduct(product);
        }
    }

    /**
     * Метод отправляющий сообщения пользователям, которые подписаны на уведомления
     * о снижении цены
     *
     * @param product  продукт
     * @param oldPrice старая цена продукта
     * @param newPrice новая цена продукта
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
            if (user.isPresent() && user.get().getFirstName() != null) {
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
                    String categoryId = eElement.getElementsByTagName("categoryid").item(0).getTextContent();
                    Product product = new Product(productName, Double.parseDouble(productPrice), Integer.parseInt(productAmount));
                    categoriesService.addToProduct(product,Long.parseLong(categoryId));
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
                    categoriesService.addToProduct(product,categoryId);
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
     * добавляет новые email в рассылку при измененеии цены на товар
     *
     * @param id    товара
     * @param email для рассылки
     * @return true если удалось добавить email, false если не удалось
     */
    @Override
    public boolean addNewSubscriber(Long id, String email) {
        if (!ValidationUtils.isValidEmail(email)) {
            return false;
        }
        Product product = findProductById(id).orElseThrow(ProductNotFoundException::new);
        Set<String> emails = product.getPriceChangeSubscribers();
        if (emails.contains(email)) {
            throw new EmailAlreadyExistsException();
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
}