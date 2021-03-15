    package com.jm.online_store.controller.rest.manager;

    import com.jm.online_store.enums.ExceptionEnums;
    import com.jm.online_store.enums.ResponseOperation;
    import com.jm.online_store.exception.constants.ExceptionConstants;
    import com.jm.online_store.model.Product;
    import com.jm.online_store.model.dto.ProductDto;
    import com.jm.online_store.model.dto.ResponseDto;
    import com.jm.online_store.service.interf.CategoriesService;
    import com.jm.online_store.service.interf.ProductService;
    import io.swagger.annotations.Api;
    import io.swagger.annotations.ApiOperation;
    import io.swagger.annotations.ApiResponse;
    import io.swagger.annotations.ApiResponses;
    import io.swagger.annotations.Authorization;
    import lombok.AllArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.apache.commons.io.FilenameUtils;
    import org.apache.commons.lang3.StringUtils;
    import org.apache.poi.xssf.usermodel.XSSFWorkbook;
    import org.springframework.core.io.FileSystemResource;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.DeleteMapping;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.PutMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.web.bind.annotation.RestController;
    import org.springframework.web.multipart.MultipartFile;
    import javax.servlet.http.HttpServletResponse;
    import java.io.BufferedOutputStream;
    import java.io.FileNotFoundException;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.Comparator;
    import java.util.List;
    import java.util.Objects;
    import java.util.stream.Collectors;

    /**
     * Контроллер для работы с добавлением/изменением товаров
     */
    @RestController
    @AllArgsConstructor
    @Slf4j
    @Api(description = "Rest controller for add/edit products")
    @RequestMapping("/api/product")
    public class ProductsRestController {
        private final ProductService productService;
        private final CategoriesService categoriesService;


        /**
         * Метод обрабатывает загрузку файла с товарами на сервер
         * Вызывает соответствующий сервисный метод в зависимости от типа файла(CSV или XML)
         * @param file файл с данными
         * @return ResponseEntity<String> с кодом ответа
         */
        @PostMapping(value = "/uploadFile/{id}")
        @ApiOperation(value = "Method handles uploading a file with products to the server. " +
                "Calls the appropriate service method depending on the file type(CSV or XML)",
                authorizations = { @Authorization(value = "jwtToken") })
        @ApiResponses(value = {
                @ApiResponse(code = 200, message = "Successful downloaded"),
                @ApiResponse(code = 404, message = "File hasn't been found"),
                @ApiResponse(code = 500, message = "Internal server errors"),
        })
        public ResponseEntity<ResponseDto<String>> handleFileUpload(@RequestParam("file") MultipartFile file, @PathVariable Long id) throws FileNotFoundException {
            writeFile(file);
            if (Objects.equals(FilenameUtils.getExtension(file.getOriginalFilename()), "xml")) {
                productService.importFromXMLFile(file.getOriginalFilename(), id);
            } else if (Objects.equals(FilenameUtils.getExtension(file.getOriginalFilename()),"csv")) {
                productService.importFromCSVFile(file.getOriginalFilename(), id);
            } else  {
                return ResponseEntity.ok(new ResponseDto<>(true, ResponseOperation.FAILED.getMessage()));
            }
            return new ResponseEntity<>(new ResponseDto<>(true, ResponseOperation.SUCCESS.getMessage()), HttpStatus.CREATED);
        }

        @PostMapping(value = "/uploadFile")
        @ApiOperation(value = "Method handles uploading a file with products to the server. " +
                "Calls the appropriate service method depending on the file type(CSV or XML)",
                authorizations = { @Authorization(value = "jwtToken") })
        @ApiResponses(value = {
                @ApiResponse(code = 200, message = "Successfully downloaded"),
                @ApiResponse(code = 404, message = "File hasn't been found"),
                @ApiResponse(code = 500, message = "Internal server errors"),
        })
        public ResponseEntity<ResponseDto<String>> handleFileUpload(@RequestParam("file") MultipartFile file) throws FileNotFoundException {
            writeFile(file);
            if (Objects.equals(FilenameUtils.getExtension(file.getOriginalFilename()), "xml")) {
                productService.importFromXMLFile(file.getOriginalFilename());
            } else if (Objects.equals(FilenameUtils.getExtension(file.getOriginalFilename()),"csv")) {
                productService.importFromCSVFile(file.getOriginalFilename());
            } else {
                return ResponseEntity.ok(new ResponseDto<>(true, ResponseOperation.FAILED.getMessage()));
            }
            return ResponseEntity.ok(new ResponseDto<>(true, ResponseOperation.SUCCESS.name(), ResponseOperation.NO_ERROR.getMessage()));
        }

        /**
         * Метод для записи загруженного файла
         * @param file файл для записи
         */
        private void writeFile(@RequestParam("file") MultipartFile file) {
            try {
                byte[] bytes = file.getBytes();
                if (bytes.length == 0)
                    throw new RuntimeException("File is empty");
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream("uploads/import/" + file.getOriginalFilename()));
                stream.write(bytes);
                stream.close();
            } catch (Exception e) {
                log.error("Ошибка сохранения файла");
                e.printStackTrace();
            }
            log.debug("тип файла" + FilenameUtils.getExtension(file.getOriginalFilename()));
        }

        /**
         * Метод выводит список всех товаров
         * или пустой список
         * @return List<ProductDto> возвращает список товаров
         */
        @GetMapping(value = "/getAll")
        @ApiOperation(value = "get all products",
                authorizations = { @Authorization(value = "jwtToken") })
        @ApiResponses(value = {
                @ApiResponse(code = 200, message = "Products have been found"),
                @ApiResponse(code = 200, message = "Products haven't been found. Returns empty list"),
        })
        public ResponseEntity<ResponseDto<List<ProductDto>>> findAll() {
            List<ProductDto> returnValue = new ArrayList<>();
            productService.findAll().forEach(p -> returnValue.add(new ProductDto(p)));
            return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
        }

        /**
         * Метод возвращает список неудаленных товаров
         * или пустой список
         * @return List<ProductDto> возвращает список товаров
         */
        @GetMapping(value = "/getNotDeletedProducts")
        @ApiOperation(value = "get list of all undeleted products",
                authorizations = { @Authorization(value = "jwtToken") })
        @ApiResponses(value = {
                @ApiResponse(code = 200, message = "Products have been found"),
                @ApiResponse(code = 200, message = "Products haven't been found. Returns empty list"),
        })
        public ResponseEntity<ResponseDto<List<ProductDto>>> getNotDeleteProducts() {
            List<ProductDto> returnValue = new ArrayList<>();
            productService.getNotDeleteProducts().forEach(p -> returnValue.add(new ProductDto(p)));
            return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
        }

        /**
         * Метод, ищет товар по id
         * @param productId идентификатор товара
         * @return <ProductDto> возвращает товар
         */
        @GetMapping(value = "/manager/{id}")
        @ApiOperation(value = "Find product by ID",
                authorizations = { @Authorization(value = "jwtToken") })
        @ApiResponses(value = {
                @ApiResponse(code = 200, message = "Product has been found"),
                @ApiResponse(code = 404, message = "Product hasn't been found"),
        })
        public ResponseEntity<ResponseDto<ProductDto>> findProductById(@PathVariable("id") Long productId) {
            Product gotBack = productService.getProductById(productId);
            ProductDto returnValue = new ProductDto(gotBack);
            return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
        }

        /**
         * Метод добавляет товар
         * @param productReq товар для добавления
         * @return ResponseEntity<ProductDto> Возвращает добавленный товар с кодом ответа
         */
        @PostMapping(value = "/add/{id}")
        @ApiOperation(value = "Add product",
                authorizations = { @Authorization(value = "jwtToken") })
        @ApiResponse(code = 400, message = "Product has empty name or product with this name is already exists ")
        @ApiResponses(value = {
                @ApiResponse(code = 200, message = "Product has been added"),
        })
        public ResponseEntity<ResponseDto<ProductDto>> addProduct(@RequestBody ProductDto productReq, @PathVariable Long id) {
            if (!StringUtils.isNoneBlank(productReq.getProduct())) {
                log.debug("EmptyProductName");
                return new ResponseEntity("EmptyProductName", HttpStatus.BAD_REQUEST);
            }
            if (productService.existsProductByProduct(productReq.getProduct())) {
                log.debug("Product with name: {} already exists", productReq.getProduct());
                return new ResponseEntity("duplicatedNameProductError", HttpStatus.BAD_REQUEST);
            }
            Product product = new Product();
            product.setId(productReq.getId());
            product.setProduct(productReq.getProduct());
            product.setPrice(productReq.getPrice());
            product.setRating(productReq.getRating());
            product.setProductType(productReq.getProductType());
            product.setProductPictureNames(product.getProductPictureNames());
            product.setDeleted(false);
            Product gotBack = productService.saveProduct(product);
            categoriesService.addToProduct(product, id);
            ProductDto returnValue = new ProductDto(gotBack);
            return new ResponseEntity<>(new ResponseDto<>(true, returnValue), HttpStatus.CREATED);
        }

        /**
         * Редактирует товар
         * @param productReq товар для редактирования
         * @return <Long> Возвращает отредактированный товар с кодом ответа
         */
        @PutMapping("/edit")
        @ApiOperation(value = "Method to edit product",
                authorizations = { @Authorization(value = "jwtToken") })
        @ApiResponses(value = {
                @ApiResponse(code = 200, message = "Product has been updated"),
                @ApiResponse(code = 404, message = "Product hasn't been found"),
        })
        public ResponseEntity<ResponseDto<ProductDto>> editProduct(@RequestBody ProductDto productReq) {
            Product product = new Product();
            product.setId(productReq.getId());
            product.setProduct(productReq.getProduct());
            product.setPrice(productReq.getPrice());
            product.setRating(productReq.getRating());
            product.setProductType(productReq.getProductType());
            product.setProductPictureNames(product.getProductPictureNames());
            product.setDeleted(false);
            Product gotBack = productService.editProduct(product);
            ProductDto returnValue = new ProductDto(gotBack);
            return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
        }

        /**
         * Редактирует товар и его категорию
         * @param product товар для редактирования
         * @return строку с описанием результата операции
         */
        @PutMapping("/edit/{idOld}/{idNew}")
        @ApiOperation(value = "Method for edit product and his category",
                authorizations = { @Authorization(value = "jwtToken") })
        @ApiResponses(value = {
                @ApiResponse(code = 200, message = "Product has been updated"),
                @ApiResponse(code = 404, message = "Category hasn't been found"),
        })
        public ResponseEntity<ResponseDto<String>> editProductAndCategory(@RequestBody Product product,
                                                              @PathVariable Long idOld,
                                                              @PathVariable Long idNew) {
            productService.editProduct(product);
            if (idOld == -1) {
                categoriesService.addToProduct(product, idNew);
            } else {
                categoriesService.removeFromProduct(product, idOld);
                categoriesService.addToProduct(product, idNew);
            }
            return ResponseEntity.ok(
                    new ResponseDto<>(true, ResponseOperation.HAS_BEEN_UPDATED.getMessage(), ResponseOperation.NO_ERROR.getMessage()));
        }

        /**
         * Метод удаления товара по идентификатору
         * @param id идентификатор товара
         * @return идентификатор удаленной сущности
         */
        @DeleteMapping(value = "/{id}")
        @ApiOperation(value = "Delete product by ID",
                authorizations = { @Authorization(value = "jwtToken") })
        @ApiResponses(value = {
                @ApiResponse(code = 200, message = "Product has been deleted"),
                @ApiResponse(code = 404, message = "Product hasn't been found"),
        })
        public ResponseEntity<ResponseDto<Long>> deleteProductById(@PathVariable("id") Long id) {
            productService.deleteProduct(id);
            return ResponseEntity.ok(new ResponseDto<>(true, id));
        }

        /**
         * Метод восстановления удаленного товара по идентификатору
         * @param id идентификатор товара
         * @return идентификатор удаленной сущности
         */
        @PostMapping(value = "/restoredeleted/{id}")
        @ApiOperation(value = "Restore product by ID",
                authorizations = { @Authorization(value = "jwtToken") })
        @ApiResponses(value = {
                @ApiResponse(code = 200, message = "Product has been restored"),
                @ApiResponse(code = 404, message = "Product hasn't been found"),
        })
        public ResponseEntity<ResponseDto<Long>> restoreProductById(@PathVariable("id") Long id) {
            productService.restoreProduct(id);
            return ResponseEntity.ok(new ResponseDto<>(true, id));
        }

        /**
         * Метод выбора продукта по категории
         * или пустой список
         * @param categoryName - id выбранной категории
         * @return List<ProductDto> отредактированный лист продуктов
         */
        @GetMapping(value = "/{categoryName}")
        @ApiOperation(value = "Choosing product by ID",
                authorizations = { @Authorization(value = "jwtToken") })
        @ApiResponses(value = {
                @ApiResponse(code = 200, message = "Products have been found"),
                @ApiResponse(code = 200, message = "Products haven't been found. Returns empty list"),
                @ApiResponse(code = 404, message = "Category hasn't been found")
        })
        public ResponseEntity<ResponseDto<List<ProductDto>>> filterByCategory(@PathVariable String categoryName) {
            List<ProductDto> returnValue = new ArrayList<>();
            productService.findProductsByCategoryName(categoryName).forEach(p -> returnValue.add(new ProductDto(p)));
            return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
        }

        /**
         * Метод выбора продукта по категории и сортирующий по возрастанию
         * @param categoryName - id выбранной категории
         * @return List<Product> отредактированный лист продуктов
         */

        @GetMapping(value = "/sort/{categoryName}/{orderSelect}")
        @ApiOperation(value = "Choosing product by ID and sorted by ASC",
                authorizations = { @Authorization(value = "jwtToken") })
        @ApiResponses(value = {
                @ApiResponse(code = 200, message = "Products have been found"),
                @ApiResponse(code = 200, message = "Products haven't been found. Returns empty list"),
                @ApiResponse(code = 404, message = "Category hasn't been found")
        })
        public ResponseEntity<ResponseDto<List<ProductDto>>> filterByCategoryAndSort(@PathVariable String categoryName,
                                                     @PathVariable String orderSelect) {
            List<ProductDto> returnValue = new ArrayList<>();
            if (StringUtils.isNoneBlank(categoryName) && categoryName.equals("default")) {
                if (StringUtils.isNoneBlank(categoryName) && orderSelect.equals("ascOrder")) {
                    productService.findAllOrderByRatingAsc().forEach(p -> returnValue.add(new ProductDto(p)));
                    return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
                }
                if (StringUtils.isNoneBlank(categoryName) && orderSelect.equals("descOrder")) {
                    productService.findAllOrderByRatingDesc().forEach(p -> returnValue.add(new ProductDto(p)));
                    return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
                }
                else {
                    return new ResponseEntity<>(new ResponseDto<>(false, ExceptionEnums.CATEGORIES.getText() + ExceptionConstants.NOT_FOUND), HttpStatus.BAD_REQUEST);
                }
            }
            if (StringUtils.isNoneBlank(categoryName) && orderSelect.equals("ascOrder")) {
                List<Product> products = productService.findProductsByCategoryName(categoryName).stream()
                        .sorted(Comparator.comparing(Product::getRating))
                        .collect(Collectors.toList());
                products.forEach(p -> returnValue.add(new ProductDto(p)));
                return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
            } else {
                List<Product> products = productService.findProductsByCategoryName(categoryName).stream()
                        .sorted((p1, p2) -> p2.getRating().compareTo(p1.getRating()))
                        .collect(Collectors.toList());
                products.forEach(p -> returnValue.add(new ProductDto(p)));
                return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
            }
        }

        /**
         * Метод выбора продукта по категории и сортирующий по убыванию
         * или пустой список
         * @param categoryName - id выбранной категории
         * @return List<Product> отредактированный лист продуктов
         */
        @GetMapping(value = "/descOrder/{categoryName}")
        @ApiOperation(value = "Choosing product by ID and sorted by DESC",
                authorizations = { @Authorization(value = "jwtToken") })
        @ApiResponses(value = {
                @ApiResponse(code = 200, message = "Products have been found"),
                @ApiResponse(code = 200, message = "Products haven't been found. Returns empty list"),
                @ApiResponse(code = 404, message = "Category hasn't been found")
        })
        public ResponseEntity<ResponseDto<List<ProductDto>>> filterByCategoryInDescOrder(@PathVariable String categoryName) {
            List<ProductDto> returnValue = new ArrayList<>();
            if (categoryName.equals("default")) {
                productService.findAllOrderByRatingDesc().forEach(p -> returnValue.add(new ProductDto(p)));
                return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
            }
            List<Product> products = productService.findProductsByCategoryName(categoryName).stream()
                    .sorted((p1, p2) -> p2.getRating().compareTo(p1.getRating()))
                    .collect(Collectors.toList());
            products.forEach(p -> returnValue.add(new ProductDto(p)));
            return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
        }


        /**
         * Метод, который формирует файл с товарами нужной категории и передаёт обратно на страницу
         * @param categoryName нужная категория товаров
         * @param response     запрос для возврата информации
         * @return запрос с файлом xlsx
         */
        @GetMapping("/report/{categoryName}/{number}/{orderSelect}")
        @ApiOperation(value = "Generate file with products of the category and return it to page",
                authorizations = { @Authorization(value = "jwtToken") })
        @ApiResponses(value = {
                @ApiResponse(code = 200, message = "Products have been found"),
                @ApiResponse(code = 200, message = "Products haven't been found. Returns empty list"),
                @ApiResponse(code = 404, message = "Could not found category and/or order"),
                @ApiResponse(code = 404, message = "Category hasn't been found")
        })
        public ResponseEntity<ResponseDto<FileSystemResource>> getProductsReportAndExportToXlsx(@PathVariable String categoryName,
                                                                                   @PathVariable Long number,
                                                                                   @PathVariable String orderSelect,
                                                                                   HttpServletResponse response) {
            try {
                response.setContentType("text/html; charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                List<Product> products;
                if (StringUtils.isNoneBlank(categoryName) && categoryName.equals("default")) {
                    if (StringUtils.isNoneBlank(orderSelect) && orderSelect.equals("descOrder")) {
                        products = productService.findAllOrderByRatingDesc().stream().limit(number).collect(Collectors.toList());
                    } else {
                        products = productService.findAllOrderByRatingAsc().stream().limit(number).collect(Collectors.toList());
                    }
                } else {
                    if (StringUtils.isNoneBlank(orderSelect) && orderSelect.equals("descOrder")) {
                        products = productService.findProductsByCategoryName(categoryName).stream()
                                .sorted((p1, p2) -> p2.getRating().compareTo(p1.getRating())).limit(number)
                                .collect(Collectors.toList());
                    } else {
                        products = productService.findProductsByCategoryName(categoryName).stream()
                                .sorted(Comparator.comparing(Product::getRating)).limit(number)
                                .collect(Collectors.toList());
                    }
                }
                response.setHeader("Size", String.valueOf(products.size()));
                productService.createXlsxDoc(products, categoryName).write(response.getOutputStream());
                return ResponseEntity.ok(new ResponseDto(true, ResponseOperation.SUCCESS, ResponseOperation.NO_ERROR.getMessage()));
            } catch (NullPointerException | IOException e) {
                return new ResponseEntity<>(new ResponseDto<>(false, ExceptionEnums.PRODUCTS.getText() + ExceptionConstants.NOT_FOUND), HttpStatus.NOT_FOUND);
            }
        }
    }
