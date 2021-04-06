package com.jm.online_store.config;

import com.jm.online_store.enums.DayOfWeekForStockSend;
import com.jm.online_store.exception.CategoriesNotFoundException;
import com.jm.online_store.exception.CharacteristicNotFoundException;
import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.model.Address;
import com.jm.online_store.model.BadWords;
import com.jm.online_store.model.Categories;
import com.jm.online_store.model.Characteristic;
import com.jm.online_store.model.Comment;
import com.jm.online_store.model.CommonSettings;
import com.jm.online_store.model.Customer;
import com.jm.online_store.model.Employee;
import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.News;
import com.jm.online_store.model.Order;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.Review;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.SentStock;
import com.jm.online_store.model.SharedNews;
import com.jm.online_store.model.SharedStock;
import com.jm.online_store.model.Stock;
import com.jm.online_store.model.SubBasket;
import com.jm.online_store.model.TaskSettings;
import com.jm.online_store.model.TemplatesMailingSettings;
import com.jm.online_store.model.Topic;
import com.jm.online_store.model.TopicsCategory;
import com.jm.online_store.service.interf.AddressService;
import com.jm.online_store.service.interf.BadWordsService;
import com.jm.online_store.service.interf.BasketService;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.CharacteristicService;
import com.jm.online_store.service.interf.CommentService;
import com.jm.online_store.service.interf.CommonSettingsService;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.FavouritesGroupService;
import com.jm.online_store.service.interf.NewsService;
import com.jm.online_store.service.interf.OrderService;
import com.jm.online_store.service.interf.ProductCharacteristicService;
import com.jm.online_store.service.interf.ProductInOrderService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.ReviewService;
import com.jm.online_store.service.interf.RoleService;
import com.jm.online_store.service.interf.SentStockService;
import com.jm.online_store.service.interf.SharedNewsService;
import com.jm.online_store.service.interf.SharedStockService;
import com.jm.online_store.service.interf.StockService;
import com.jm.online_store.service.interf.TaskSettingsService;
import com.jm.online_store.service.interf.TemplatesMailingSettingsService;
import com.jm.online_store.service.interf.TopicService;
import com.jm.online_store.service.interf.TopicsCategoryService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * класс первичного заполнения таблиц.
 * <p>
 * для первичного заполнения базы данных раскомментировать аннотацию
 * "@PostConstruct" и поменять значение  ключа "spring.jpa.hibernate.ddl-auto"
 * в файле "application.yml" с "update" на "create" или "create-drop".
 */
@Slf4j
@AllArgsConstructor
@Component
@Data
public class DataInitializer {

    private final UserService userService;
    private final RoleService roleService;
    private final CategoriesService categoriesService;
    private final ProductService productService;
    private final NewsService newsService;
    private final OrderService orderService;
    private final ProductInOrderService productInOrderService;
    private final BasketService basketService;
    private final StockService stockService;
    private final SharedStockService sharedStockService;
    private final AddressService addressService;
    private final SentStockService sentStockService;
    private final TaskSettingsService taskSettingsService;
    private final CommonSettingsService commonSettingsService;
    private final TopicService topicService;
    private final CommentService commentService;
    private final FavouritesGroupService favouritesGroupService;
    private final TopicsCategoryService topicsCategoryService;
    private final ReviewService reviewService;
    private final SharedNewsService sharedNewsService;
    private final CharacteristicService characteristicService;
    private final ProductCharacteristicService productCharacteristicService;
    private final BadWordsService badWordsService;
    private final TemplatesMailingSettingsService templatesMailingSettingsService;
    private final CustomerService customerService;

    @Autowired
    private Environment environment;

    // Задайте название магазина
    private final String STORE_NAME = "Online store";

    /**
     * Основной метод для заполнения базы данных.
     * Вызов методов добавлять в этод метод.
     * Следить за последовательностью вызова.
     */
    //@PostConstruct
    //раскомментировать аннотацию при первом запуске проекта для создания таблиц БД, потом закомментировать
    public void initDataBaseFilling() {
        roleInit();
        newsInit();
        characteristicsInit();
        productInit();
        categoryCharacteristicsInit();
        productCharacteristicsInit();
        ordersInit();
        stockInit();
        sharedStockInit();
        sharedNewsInit();
        addressInit(); 
//      sentStockInit();  // метод нужен только для тестирования рассылки акций
//      paginationNewsAndStocksInit();  // метод нужен для тестирования динамической пагинации
        taskSettingsInit();
        commonSettingsInit();
        feedbackTopicsInit();
        commentsInit();
        reviewsInit();
        badWordInit();
        templatesMailingSettingsInit();
    }

    /**
     * Метод конфигурирования и первичного заполнения таблиц:
     * ролей, юзеров и корзины.
     */
    private void roleInit() {
        Role adminRole = new Role("ROLE_ADMIN");
        Role customerRole = new Role("ROLE_CUSTOMER");
        Role managerRole = new Role("ROLE_MANAGER");
        Role serviceRole = new Role("ROLE_SERVICE");
        Role moderatorRole = new Role("ROLE_MODERATOR");
        Role supermoderatorRole = new Role("ROLE_SUPERMODERATOR");

        roleService.addRole(adminRole);
        roleService.addRole(customerRole);
        roleService.addRole(managerRole);
        roleService.addRole(serviceRole);
        roleService.addRole(moderatorRole);
        roleService.addRole(supermoderatorRole);

        Employee admin = new Employee("admin@mail.ru", "1");
        Employee manager = new Employee("manager@mail.ru", "1");
        Customer customer = new Customer("customer@mail.ru", "1");
        Employee service = new Employee("service@mail.ru", "1");
        Employee moderator1 = new Employee("moderator1@mail.ru", "1");
        Employee moderator2 = new Employee("moderator2@mail.ru", "2");
        Employee supermoderator = new Employee("supermoderator@mail.ru", "1");
        Customer deletedCustomer = new Customer("deleted@mail.ru", "1");
        deletedCustomer.setProfilePicture(StringUtils.cleanPath("deleted.jpg"));
        deletedCustomer.setFirstName("Deleted");
        deletedCustomer.setLastName("");

        Optional<Role> admnRole = roleService.findByName("ROLE_ADMIN");
        Optional<Role> custRole = roleService.findByName("ROLE_CUSTOMER");
        Optional<Role> managRole = roleService.findByName("ROLE_MANAGER");
        Optional<Role> servRole = roleService.findByName("ROLE_SERVICE");
        Optional<Role> modrRole = roleService.findByName("ROLE_MODERATOR");
        Optional<Role> supermodrRole = roleService.findByName("ROLE_SUPERMODERATOR");

        Set<Role> customerRoles = new HashSet<>();
        Set<Role> adminRoles = new HashSet<>();
        Set<Role> managerRoles = new HashSet<>();
        Set<Role> serviceRoles = new HashSet<>();
        Set<Role> moderatorRoles = new HashSet<>();
        Set<Role> supermoderatorRoles = new HashSet<>();

        customerRoles.add(custRole.get());
        adminRoles.add(admnRole.get());
        adminRoles.add(custRole.get());
        adminRoles.add(servRole.get());
        managerRoles.add(managRole.get());
        serviceRoles.add(servRole.get());
        moderatorRoles.add(modrRole.get());
        supermoderatorRoles.add(supermodrRole.get());

        manager.setRoles(managerRoles);
        admin.setRoles(adminRoles);
        customer.setRoles(customerRoles);
        deletedCustomer.setRoles(customerRoles);
        service.setRoles(serviceRoles);
        moderator1.setRoles(moderatorRoles);
        moderator2.setRoles(moderatorRoles);
        supermoderator.setRoles(supermoderatorRoles);

        userService.addUser(manager);
        userService.addUser(customer);
        userService.addUser(admin);
        userService.addUser(service);
        userService.addUser(moderator1);
        userService.addUser(moderator2);
        userService.addUser(supermoderator);
        userService.addUser(deletedCustomer);

        Product product_1 = new Product("apple", 100000D, 10, 0.1);
        Product product_2 = new Product("samsung", 80000D, 100, 0.9);
        Product product_3 = new Product("xiaomi", 30000D, 50, 0.5);

        productService.saveProduct(product_1);
        productService.saveProduct(product_2);
        productService.saveProduct(product_3);

        Set<Product> productSet = new HashSet<>();
        productSet.add(product_1);
        productSet.add(product_2);
        productSet.add(product_3);

        Customer customerU = customerService.findCustomerByEmail("customer@mail.ru");
        customerU.setFavouritesGoods(productSet);
        userService.updateUser(customerU);

        //Создание основного списка(Все товары) избранных товаров
        FavouritesGroup favouritesGroup = new FavouritesGroup();
        favouritesGroup.setName("Все товары");
        favouritesGroup.setProducts(productSet);
        favouritesGroup.setCustomer(customerU);
        favouritesGroupService.save(favouritesGroup);

        SubBasket subBasket_1 = new SubBasket();
        subBasket_1.setProduct(product_1);
        subBasket_1.setCount(1);
        basketService.addBasket(subBasket_1);
        SubBasket subBasket_2 = new SubBasket();
        subBasket_2.setProduct(product_3);
        subBasket_2.setCount(1);
        basketService.addBasket(subBasket_2);
        List<SubBasket> subBasketList = new ArrayList<>();
        subBasketList.add(subBasket_1);
        subBasketList.add(subBasket_2);
        customer.setUserBasket(subBasketList);
        userService.updateUser(customerU);

        Random random = new Random();
        for (int i = 1; i < 20; i++) {
            Customer customer1 = new Customer("customer" + i + "@mail.ru",
                    DayOfWeekForStockSend.values()[random.nextInt(6)],
                    String.valueOf(i));
            customer1.setRoles(customerRoles);

            userService.addUser(customer1);

            Customer customerU1 = customerService.findCustomerByEmail("customer" + i + "@mail.ru");
            customerU1.setFavouritesGoods(productSet);
            userService.updateUser(customerU1);

            FavouritesGroup favouritesGroup1 = new FavouritesGroup();
            favouritesGroup1.setName("Все товары");
            favouritesGroup1.setProducts(productSet);
            favouritesGroup1.setCustomer(customerU1);
            favouritesGroupService.save(favouritesGroup1);

            customer1.setUserBasket(subBasketList);
            userService.updateUser(customerU1);
        }
    }

    /**
     * Метод первичного тестового заполнения новостей.
     */
    private void newsInit() {
        News firstNews = News.builder()
                .title("Акция от XP-Pen: Выиграй обучение в Skillbox!")
                .anons("Не пропустите розыгрыш потрясающих призов.")
                .fullText("<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " font-family: &quot;PT Sans&quot;, Arial, sans-serif;\"><b style=\"color: rgb(255, 0, 0);" +
                        " font-size: 1rem;\">Если вы любите создавать и повсюду ищите вдохновение, то следующая" +
                        " новость для вас!</b><br></p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left:" +
                        " 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial," +
                        " sans-serif;\">XP-Pen проводят акицию с невроятно крутым призовым фоном, вы можете выиграть" +
                        " один из сертификатов на годовое обучение 2D или 3D рисованию в Skillbox, а также фирменные" +
                        " сувениры от бренда.</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px;" +
                        " padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial," +
                        " sans-serif;\">Что нужно делать?</p><ul style=\"margin-right: 0px; margin-bottom: 0px;" +
                        " margin-left: 0px; padding: 0px; list-style-type: none; color: rgb(0, 0, 0); font-family:" +
                        " &quot;PT Sans&quot;, Arial, sans-serif;\"><li style=\"margin: 0px; padding: 0px;\">1.Купить" +
                        " в <b>Online-Shop</b> любой графический планшет или интерактивный дисплей XP-Pen с 15" +
                        " августа по 15 сентября 2020 года.</li><li style=\"margin: 0px; padding: 0px;\">2.Пришлите" +
                        " серийный номер изделия на эл. почту sales_ru@xp-pen.com</li>X – XP-Pen подведут итоги" +
                        " методом рандома, так что шанс есть у каждого!</li></ul><p style=\"margin-right: 0px;" +
                        " margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family:" +
                        " &quot;PT Sans&quot;, Arial, sans-serif;\">Вы только взгляните на эти призы!</p>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color:" +
                        " rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">1 сертификат на" +
                        " обучение в школе SkillBox по курсу «Профессия 2D-художник»</p><p style=\"margin-right:" +
                        " 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family:" +
                        " &quot;PT Sans&quot;, Arial, sans-serif;\">2 сертификата на обучение в школе SkillBox по" +
                        " курсу «Профессия 3D-художник»</p><p style=\"margin-right: 0px; margin-bottom: 1em;" +
                        " margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;," +
                        " Arial, sans-serif;\">5 наборов фирменных сувениров от XP-Pen (в набор входит рюкзачок" +
                        " XP-Pen, брелок с фирменным персонажем XP-Pen лисенком Фениксом и чехол для пера)</p>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color:" +
                        " rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">2 сертификата на 50%" +
                        " скидку на обучение в школе SkillBox по курсу «Профессия 2D-художник»</p>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color:" +
                        " rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">3 сертификата на" +
                        " 50% скидку на обучение в школе SkillBox по курсу «Профессия 3D-художник»</p>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">" +
                        "Online-shop желает всем удачи!</p>")
                .postingDate(LocalDate.now())
                .archived(true)
                .build();

        News secondNews = News.builder()
                .title("Акция от AORUS: Играй и смотри!")
                .anons("Купите монитор и получите целый год фильмов с ivi и вкусную пиццу в подарок.")
                .fullText("<h2 style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " font-family: &quot;PT Sans&quot;, Arial, sans-serif;\"><b style=\"\"><font color=\"#ff0000\">" +
                        "Хорошие новости в Online-Shop!</font></b></h2><p style=\"margin-right: 0px; margin-bottom:" +
                        " 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;," +
                        " Arial, sans-serif;\"><span style=\"background-color: rgb(0, 255, 0);\">Смотреть кино стало" +
                        " еще интереснее и вкуснее.</span> При покупке одного из мониторов AORUS вы получаете в" +
                        " подарок 12 месяцев подписки на ivi и промокод на 1200 рублей в Додо-пицца. Акция продлится" +
                        " с 10 по 31 августа.</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px;" +
                        " padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">" +
                        "<i style=\"font-size: 1rem;\">Приятных покупок в Online-Shop!</i></p><p style=\"margin-right:" +
                        " 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family:" +
                        " &quot;PT Sans&quot;, Arial, sans-serif;\"><i style=\"font-size: 1rem;\">23<br></i><br></p>")
                .postingDate(LocalDate.now().minusDays(5L))
                .archived(false)
                .build();

        News thirdNews = News.builder()
                .title("Сегодня стартует предзаказ на флагманские продукты Samsung!")
                .anons("Сделайте предзаказ и получите подарок.")
                .fullText("<h1><span style=\"font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">" +
                        "<font color=\"#0000ff\">Хорошие новости в Online-Shop!</font></span></h1><h1>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                        " font-size: 16px;\">Сегодня стартует предзаказ на новые флагманские продукты Samsung!<b></b>" +
                        "</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                        " font-size: 16px;\"><br></p></h1>")
                .postingDate(LocalDate.now().minusDays(13L))
                .archived(false)
                .build();

        News forthNews = News.builder()
                .title("Будь в плюсе вместе с нами!")
                .anons("Мы дарим дополнительный кэшбэк!")
                .fullText("<h1><span style=\"font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">" +
                        "<font color=\"#0000ff\">Хорошие новости в Online-Shop!</font></span></h1><h1>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                        " font-size: 16px;\">Кэшбэк 3% — стандартные начисления и 7% — за онлайн-оплату!<b></b>" +
                        "</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                        " font-size: 16px;\"><br></p></h1>")
                .postingDate(LocalDate.now().minusDays(10L))
                .archived(false)
                .build();

        News fifthNews = News.builder()
                .title("Старт продаж Honor30i")
                .anons("Только у нас эксклюзивный смартфон!")
                .fullText("<h1><span style=\"font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">" +
                        "<font color=\"#0000ff\">Хорошие новости в Online-Shop!</font></span></h1><h1>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                        " font-size: 16px;\">Супервыгода на HONOR 30i: получи кэшбэк 15% на свой Бонусный счёт<b></b>" +
                        "</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                        " font-size: 16px;\"><br></p></h1>")
                .postingDate(LocalDate.now().minusDays(1L))
                .archived(false)
                .build();

        News sixthNews = News.builder()
                .title("Отличная новость!")
                .anons(STORE_NAME + " открывает продлёнку на скидки!")
                .fullText("<h1><span style=\"font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">" +
                        "<font color=\"#0000ff\">Хорошие новости в Online-Shop!</font></span></h1><h1>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                        " font-size: 16px;\">Успей воспользоваться лучшим предложением, пока мы чистим стоки.<b></b>" +
                        "</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                        " font-size: 16px;\"><br></p></h1>")
                .postingDate(LocalDate.now().plusDays(30L))
                .archived(false)
                .build();

        News seventhNews = News.builder()
                .title("Как проверить ноутбук при покупке?")
                .anons("9 простых шагов")
                .fullText("<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " font-family: &quot;PT Sans&quot;, Arial, sans-serif;\"><b style=\"color: rgb(255, 0, 0);" +
                        " font-size: 1rem;\">Какой бы совершенной ни была сборка, всегда есть маленькая вероятность " +
                        "заводского брака. Как проверить работоспособность ноутбука при покупке — разбираемся вместе " +
                        "с " + STORE_NAME + ".</b><br></p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left:\"" +
                        " 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial,\" +\n" +
                        " sans-serif;\">1. Визуальный осмотр\n" +
                        "Покрутите ноутбук в руках. На его корпусе не должно быть царапин и потертостей. Любые " +
                        "повреждения говорят о том, что устройство хранилось и перевозилось в ненадлежащих условиях. " +
                        "Взгляните на петли экрана и панели корпуса рядом с ними. Если вы видите следы износа, " +
                        "портативный компьютер явно побывал в чьих-то руках. Обратите особое внимание на вентиляционные " +
                        "решетки сбоку или сзади. Если в этих местах материал изменил цвет или деформировался, " +
                        "устройство перегревалось ранее. Это важно для моделей с дискретной видеокартой.\n" +
                        "2. Проверка комплектации\n" +
                        "Если вы покупаете ноутбук в магазине, попросите продавца показать все аксессуары. " +
                        "Если устройство приобретено онлайн, распаковку нужно снимать на видео, обязательно фиксируя " +
                        "целостность пломб и наклеек. Все ноутбуки должны комплектоваться зарядным устройством, " +
                        "гарантийным талоном и инструкцией по эксплуатации. К отдельным компьютерам прилагаются диски " +
                        "и флешки с дополнительным программным обеспечением, а вместе с геймерскими девайсами — " +
                        "мышки, наушники или сумки.\n" +
                        "3. Общая проверка работоспособности\n" +
                        "Если на ноутбуке установлена операционная система, запустите его. Модели с винчестером " +
                        "полностью загружаются в течение 30–45 секунд, а с SSD — за 15–20 секунд. Если операционной " +
                        "системы нет, вам стоит воспользоваться загрузочным образом Windows> 10, заранее записанным " +
                        "на USB-флешку. А вот продукция Apple всегда поставляется с предустановленной ОС, поэтому " +
                        "дополнительные инструменты вам не понадобятся.\n" +
                        "Если флешки с загрузочным образом под рукой не оказалось, проверка значительно сокращается. " +
                        "Попросите запустить ноутбук. Убедитесь, что лампочки питания и работы накопителя загораются. " +
                        "Присмотритесь к экрану — на нем не должно быть полос, разноцветных квадратов и других " +
                        "дефектов. Если в устройстве есть оптический привод, выдвиньте лоток и проверьте, насколько " +
                        "легко он вставляется обратно.\n" +
                        "4. Производительность\n" +
                        "Запустите «тяжелое» приложение — для этих целей подойдет бесплатный графический редактор " +
                        "GIMP, утилита для монтажа видео Lightworks или медиаплеер с фильмом в формате 4K. Если " +
                        "ничего перечисленного под рукой нет, запустите онлайн-сервис Google Maps и перейдите в " +
                        "режим просмотра улиц. Даже при таких нагрузках ноутбук должен быстро реагировать на ваши " +
                        "команды — зависания и долгие фризы говорят о явных неисправностях. Если в устройстве более " +
                        "8 ГБ оперативной памяти, попробуйте запустить несколько приложений одновременно. Нажмите " +
                        "Ctrl+Alt+Del, откройте «диспетчер задач» и посмотрите, насколько загружены системные " +
                        "ресурсы. Если цифра достигает 100%, девайс уже работает на пределе своих возможностей.\n" +
                        "5. Экран\n" +
                        "Заранее подготовьте картинки с однотонной заливкой. Белая позволит проверить однородность " +
                        "подсветки, зеленая — выявить битые пиксели, а черная — обнаружить светлые пятна. Взгляните " +
                        "на углы обзора — у матриц IPS и VA они близки к 178°. Это означает, что картинка должна " +
                        "оставаться четкой независимо от вашего положения — допускается лишь небольшое снижение " +
                        "цветовой насыщенности. У матриц TN могут наблюдаться сильные искажения при изменении угла " +
                        "наклона экрана. Это допустимо и не считается дефектом.\n" +
                        "6. Разрядка батареи\n" +
                        "Запустите ноутбук и позвольте поработать около 10–15 минут. Без нагрузки аккумулятор " +
                        "должен разрядиться на 2–5% в зависимости от технических характеристик конкретной модели. " +
                        "Если цифра превышает 10%, это должно быть поводом для беспокойства. Подключите сетевой " +
                        "адаптер. Батарея зарядится до 100% в течение следующих 5–10 минут. Если цифра не меняется, " +
                        "система питания неисправна.\n" +
                        "7. Накопитель\n" +
                        "Запишите на флешку несколько файлов общим объемом около 1 ГБ. Попытайтесь скопировать " +
                        "их на внутренний жесткий диск и обратно. Если в устройстве установлен как винчестер, так " +
                        "и SSD, перекиньте файлы с одного накопителя на другой. Процесс копирования должен идти " +
                        "равномерно, без долгих пауз и прерываний.\n" +
                        "8. Клавиатура\n" +
                        "Откройте приложение «Блокнот» или другой текстовый редактор. Понажимайте на кнопки и " +
                        "проследите, как быстро устройство реагирует на ваши действия. Клавиши должны нажиматься " +
                        "легко и мгновенно возвращаться на место. Сильное сопротивление, залипание внизу, посторонние " +
                        "звуки и дребезг — повод для беспокойства. Обязательно включите подсветку, если она " +
                        "предусмотрена в конкретной модели.\n" +
                        "9. Коммуникации\n" +
                        "Проверьте порты USB и аудиоразъем. Для этого вам понадобится проводная мышка и гарнитура. " +
                        "В магазине также можно будет проверить интерфейсы HDMI и Ethernet для трансляции видео и " +
                        "подключения к Сети. Попытайтесь синхронизировать смартфон с ноутбуком по Bluetooth и " +
                        "Wi-Fi — во втором случае вам нужно будет выбрать функцию точки доступа в мобильном девайсе.\n" +
                        "Что в итоге\n" +
                        "Для проверки ноутбука с Windows или Mac OS своими силами понадобятся:\n" +
                        "• флешка с видео и картинками\n" +
                        "• мышка\n" +
                        "• наушники\n" +
                        "• портативная версия «тяжелой» программы\n" +
                        "Если ОС отсутствует, в список придется включить загрузочный образ.\n" +
                        "В " + STORE_NAME + " представлен огромный выбор ноутбуков. Также у нас вы найдете все " +
                        "необходимые аксессуары, чтобы проверить и опробовать в деле новый гаджет.</b><br></p>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">" +
                        "Online-shop желает всем удачи!</p>")
                .postingDate(LocalDate.now().plusDays(30L))
                .archived(false)
                .build();

        News eighthNews = News.builder()
                .title("Как выбрать телевизор")
                .anons("На что обращать вниание при покупке TV")
                .fullText("<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " font-family: &quot;PT Sans&quot;, Arial, sans-serif;\"><b style=\"color: rgb(255, 0, 0);" +
                        " font-size: 1rem;\">За свою почти вековую историю телевизор стал незаменимым " +
                        "многофункциональным устройством. Он магическим образом собирает у экрана всю семью, " +
                        "может считывать информацию с различных носителей и синхронизироваться со смартфоном, " +
                        "подключаться к интернету и управлять умным домом. " + STORE_NAME + " подскажет, на что " +
                        "обратить внимание при покупке телевизора.</b><br></p><p style=\"margin-right: 0px; " +
                        "margin-bottom: 1em; margin-left:\" +\n\" 0px; padding: 0px; color: rgb(0, 0, 0); font-family: " +
                        "&quot;PT Sans&quot;, Arial,\" sans-serif;\">" +
                        "1. Соотношение размера и разрешения — от этого зависит качество изображения. " +
                        "Для телевизоров с диагональю от 24 до 32 дюймов оптимальный формат картинки — " +
                        "Full HD (1920×1080), от 40 до 65 дюймов — UHD 4K (3840×2160).\n" +
                        "\n" +
                        "2. Wi-Fi и Smart TV. Если вы планируете использовать телевизор как основной " +
                        "мультимедийный центр, без «умных» функций не обойтись. Также выбирайте модели с " +
                        "подключением к интернету через сети Wi-Fi a/c. Они стабильнее и быстрее, чем предыдущие " +
                        "стандарты — например, Wi-Fi n.\n" +
                        "\n" +
                        "3. Операционная система. Основные программные платформы — Android TV (самый большой выбор " +
                        "приложений), Samsung Tizen (предельно простой интерфейс), LG WebOS (интеллектуальный " +
                        "подбор контента).\n" +
                        "\n" +
                        "4. Технология HDR. Расширенный динамический диапазон создает эффект глубины картинки " +
                        "и делает спецэффекты суперреалистичными.\n" +
                        "\n" +
                        "5. Дополнительные возможности:\n" +
                        "\n" +
                        "• поиск контента, переключение каналов и регулировка громкости голосовым управлением\n" +
                        "\n" +
                        "• синхронизация с умным домом (полный контроль над работой кондиционера, стиральной машины, " +
                        "теплого пола и видеодомофона) с помощью пульта от телевизора\n" +
                        "\n" +
                        "• экран на основе квантовых точек для расширения цветовой гаммы" +
                        "В " + STORE_NAME + " представлен огромный выбор телевизоров. Также у нас вы найдете все " +
                        "необходимые аксессуары, чтобы в полной мере наслаждаться просмотром телепередач.</b><br></p>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">" +
                        "Online-shop желает всем удачи!</p>")
                .postingDate(LocalDate.now().plusDays(30L))
                .archived(false)
                .build();

        News ninthNews = News.builder()
                .title("Выбор пароварки")
                .anons("Или технология умного приготовления пищи")
                .fullText("<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " font-family: &quot;PT Sans&quot;, Arial, sans-serif;\"><b style=\"color: rgb(255, 0, 0);" +
                        " font-size: 1rem;\">Самый полезный способ приготовления пищи — на пару. " +
                        "Продукты минимально теряют пищевую ценность, сохраняя микроэлементы и витамины. " +
                        "Для этого необходим особый прибор — пароварка.\n" +
                        STORE_NAME + " подскажет, на что обратить внимание при выборе.\n" +
                        "</b><br></p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left:\" +\n" +
                        " 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial,\" +\n" +
                        " sans-serif;\">Тип управления\n" +
                        "Механические пароварки\n" +
                        "Устройства такого типа управляются поворотным селектором. Это ограничивает количество " +
                        "доступных функций — остаются температура и время приготовления, но позволяет выбрать " +
                        "точные значения.<br>\n" +
                        "Электронные пароварки\n" +
                        "В них встроен дисплей для дополнительного контроля над процессом. Управление происходит " +
                        "при помощи кнопок или сенсорной панели. Есть дополнительные функции — отложенный старт и " +
                        "программы приготовления отдельных блюд — рыбы, птицы и даже выпечки.<br>\n" +
                        "Мощность\n" +
                        "От мощности зависит скорость превращения воды в пар и, соответственно, время, необходимое " +
                        "для приготовления пищи. Чем мощнее устройство, тем быстрее оно будет работать с большим " +
                        "числом отсеков. Большинство пароварок обладают мощностью до 1000 Вт — хватит для небольшой " +
                        "семьи из 2-4 человек. Для большой семьи, когда еда готовится сразу в 3-4 отсеках, стоит " +
                        "поискать пароварку мощнее: 1500-2000 Вт, а то и более 2000 Вт.<br>\n" +
                        "Объем\n" +
                        "Каждый прибор состоит из одного или нескольких лотков для продуктов, со своей емкостью. " +
                        "Чем больше лотков, тем больше будет суммарный объем пароварки. Как правило, в характеристиках " +
                        "указывается именно общая сумма — менее 6 литров, до 10 литров и даже более 10 литров. Чем " +
                        "больше надо готовить за один раз, тем более объемную пароварку стоит выбрать. Количество " +
                        "ярусов пропорционально объему продуктов, готовящихся одновременно. Есть пароварки с одним " +
                        "ярусом, двумя, тремя, пятью или даже больше.\n" +
                        "В пароварках с большим количеством чаш обращайте внимание на число поддонов для сока. Если " +
                        "он один, блюдо, готовящееся в нижней чаше, может собирать запахи с верхних. Если для каждой " +
                        "чаши предусмотрен свой поддон, этого не произойдет.<br>\n" +
                        "Материал чаши\n" +
                        "Пластиковые лотки\n" +
                        "Мнение, что они позволяют следить за процессом приготовления, не снимая крышку, — иллюзия. " +
                        "Прозрачные стенки достаточно быстро запотевают. У пластиковых чаш другое преимущество — " +
                        "они легче и гораздо быстрее отмываются. Но более хрупкие.\n" +
                        "Чаши из нержавеющей стали\n" +
                        "Такие устройства сложнее мыть, несмотря на антипригарное покрытие. Но они надежнее и лучше " +
                        "подходят для тушения мяса или птицы в собственном соку.\n" +
                        "Функция отложенного старта позволяет приготовить блюдо к определенному времени — закинули " +
                        "продукты с вечера, получили блюдо утром.<br>\n" +
                        "Дополнительные приспособления\n" +
                        "Некоторые пароварки поставляются со специальной чашей для приготовления риса — подходит " +
                        "и для других круп. Ее можно купить отдельно, главное — соответствие ее размера и лотка " +
                        "выбранной пароварки. Существуют пароварки с углублениями для варки яиц.\n" +
                        "У нас можно приобрести пароварку любого из представленных типов, в том числе купить в " +
                        "кредит или с использованием бонусных карт. В " + STORE_NAME + " представлен огромный выбор " +
                        "пароварок. Выбирайте с учетом представленных рекомендаций. </b><br></p>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">" +
                        "Online-shop желает всем удачи!</p>")
                .postingDate(LocalDate.now().plusDays(30L))
                .archived(false)
                .build();

        News tenthNews = News.builder()
                .title("Помощники в путешествии")
                .anons("Что взять с собой в поездку на автомобиле")
                .fullText("<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " font-family: &quot;PT Sans&quot;, Arial, sans-serif;\"><b style=\"color: rgb(255, 0, 0);" +
                        " font-size: 1rem;\">Свобода передвижения, неизведанные дали, неограниченный объем багажа — " +
                        "многие обожают путешествовать машиной! Однако легкомысленные сборы могут привести к " +
                        "сложностям в дороге. Поэтому первый шаг к идеальной автомобильной поездке — составить " +
                        "чек-лист необходимых вещей.\n" +
                        "\n" +
                        "Какие гаджеты взять с собой и как подготовиться к поездке — подскажет " + STORE_NAME + ".</b><br>" +
                        "</p><p style=\"margin-right: 0px; \" +\n" +
                        "margin-bottom: 1em; margin-left:\" +\n\" 0px; padding: 0px; color: rgb(0, 0, 0); " +
                        "font-family: \" +\n" +
                        "&quot;PT Sans&quot;, Arial,\" sans-serif;\">" +
                        "Путеводитель\n" +
                        "Бумажные карты — очередной проезд нужного поворота, лишние 50 километров по автобану и " +
                        "потраченное время на поиски верного пути. Есть вещи, сэкономив на которых придется " +
                        "заплатить слишком высокую цену. Поэтому заранее позаботьтесь о вашем проводнике — " +
                        "GPS-навигаторе, чтобы избежать пробки, ремонтные работы и не проглядеть дорожные знаки.\n" +
                        "Подручные инструменты\n" +
                        "Малейший ухаб на дороге может спровоцировать поломку машины или прокол колеса. Представьте: " +
                        "на улице темно, ближайшая цивилизация и СТО в 70 километрах, а связь предательски «не ловит». " +
                        "Никто не застрахован от такого случая и правильный набор инструментов в багажнике будет кстати.\n" +
                        "Заранее позаботьтесь о запасной шине и домкрате — так вы легко замените колесо. За пределами " +
                        "крупных городов сложно найти услуги по подкачке шин — электрический автомобильный насос в " +
                        "багажнике решит эту проблему. Также обзаведитесь базовым набором инструментов в виде " +
                        "буксировочного троса, разветвителя для прикуривателя, автомобильного инвертора, знака " +
                        "аварийной остановки, фонарика, ключей и отверток. Вы никогда не знаете, в какой ситуации " +
                        "они спасут вас, ваше время и нервы!\n" +
                        "Мобильные девайсы\n" +
                        "«Как банально» — возможно, подумаете вы. Однако мониторить прогноз погоды и симпатичные " +
                        "локации невозможно без смартфона. Не забывайте: «карманное турагентство» выполняет десяток " +
                        "дел одновременно, поэтому его батарея может разрядиться за несколько часов. Просто не " +
                        "забудьте Power Bank и USB-переходник: телефон не «умрет» в самый неподходящий момент! " +
                        "Чтобы запечатлеть новые места и потрясающие виды, возьмите палку для селфи или фотоаппарат, " +
                        "желательно со специальной сумкой. Фотографируйте сколько угодно — место найдется, особенно " +
                        "если будет дополнительная карта памяти или внешний жесткий диск.\n" +
                        "Планируете поездку с детьми? Без лишних слов: планшет с мультфильмами спасет вас.\n" +
                        "Развлечения и активный отдых\n" +
                        "Как развлечь ребенка в дороге помимо гаджетов? Помогут любимые игрушки и настольные игры. " +
                        "Главное, не забудьте, — в поездке с малышами детское автокресло — must-have.\n" +
                        "Мечтаете после длительного переезда размяться и насладиться живописными пейзажами? " +
                        "Мы точно знаем, как это совместить. Любители велоспорта, эта информация для вас! Все, что " +
                        "вам нужно — повесить велокрепление на крышу автомобиля.\n" +
                        "Вкусная и правильная пища\n" +
                        "Согласитесь, в дороге все вкуснее! Можно захватить лапшу быстрого приготовления и консервы. " +
                        "Однако после таких приемов пищи просьбы остановить автотранспорт участятся. Правильное " +
                        "питание — залог хорошего самочувствия. Продумайте рацион заранее и присмотрите портативный " +
                        "холодильник. В нем пару дней легко продержатся запеченная куриная грудка или индейка, а чуть " +
                        "дольше — вкусные домашние бутерброды с вяленым мясом и сыром. Для любителей чая пригодится " +
                        "термос или дорожный чайник. И конечно, кофе в дорогу в любимой термокружке!\n" +
                        "Уже решили, куда отправитесь на автомобиле? Тогда обязательно посмотрите, каких товаров в " +
                        "дорогу вам не хватает. Заходите в " + STORE_NAME + ", покупайте и путешествуйте с комфортом!</b><br></p>" +
                        "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                        " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">" +
                        "Online-shop желает всем удачи!</p>")
                .postingDate(LocalDate.now().plusDays(30L))
                .archived(false)
                .build();

        newsService.save(firstNews);
        newsService.save(secondNews);
        newsService.save(thirdNews);
        newsService.save(forthNews);
        newsService.save(fifthNews);
        newsService.save(sixthNews);
        newsService.save(seventhNews);
        newsService.save(eighthNews);
        newsService.save(ninthNews);
        newsService.save(tenthNews);
    }

    /**
     * Метод первичного тестового заполнения товаров.
     */
    private void productInit() {

        Categories category1 = new Categories("Компьютеры", 0L, 1);
        Categories category2 = new Categories("Бытовая техника", 0L, 1);
        Categories category3 = new Categories("Смартфоны и гаджеты", 0L, 1);
        Categories category4 = new Categories("ТВ и развлечения", 0L, 1);
        Categories category5 = new Categories("Офис и сеть", 0L, 1);
        Categories category6 = new Categories("Аксессуары", 0L, 1);
        Categories category7 = new Categories("Автотовары", 0L, 1);
        Categories category8 = new Categories("Инструменты", 0L, 1);
        Categories category9 = new Categories("Ноутбуки", 1L, 2);
        Categories category10 = new Categories("Моноблоки", 1L, 2);
        Categories category11 = new Categories("Смартфоны", 3L, 2);
        Categories category12 = new Categories("Комплектующие", 1L, 2);
        Categories category13 = new Categories("Периферия", 1L, 2);
        Categories category14 = new Categories("Планшеты", 3L, 2);
        Categories category15 = new Categories("Электронные книги", 3L, 2);
        Categories category16 = new Categories("Аксессуары для гаджетов", 3L, 2);
        Categories category17 = new Categories("Телевизоры", 4L, 2);
        Categories category18 = new Categories("Игры", 4L, 2);
        Categories category19 = new Categories("Аудиотехника", 4L, 2);
        Categories category20 = new Categories("Оргтехника", 5L, 2);
        Categories category21 = new Categories("Роутеры и сетевое оборудование", 5L, 2);
        Categories category22 = new Categories("Техника для кухни", 2L, 2);
        Categories category23 = new Categories("Техника для уборки", 2L, 2);
        Categories category24 = new Categories("Стиральные и сушильные машины", 2L, 2);
        Categories category25 = new Categories("Климатическая техника", 2L, 2);
        Categories category26 = new Categories("С сенсорным экраном", 9L, 3);
        Categories category27 = new Categories("Игровые", 9L, 3);
        Categories category28 = new Categories("Недорогие", 9L, 3);
        Categories category29 = new Categories("Ультрабуки", 9L, 3);

        Product product1 = new Product("Asus-NX4567", 299.9, 15, 4.0, "Computer", false);
        Product product2 = new Product("ACER-543", 399.9, 10, 4.2, "Computer", false);
        Product product3 = new Product("Samsung-7893", 259.9, 20, 4.6, "Computer", false);

        Product product4 = new Product("NX-7893-PC-09878", 924.0, 3, 4.2, "Computer", false);
        Product product5 = new Product("ZX-7654-PC-1", 1223.9, 7, 4.7, "Computer", false);
        Product product6 = new Product("NY-2345-PC-453", 1223.9, 7, 4.7, "Computer", false);

        Product product7 = new Product("XIAOMI-Mi10", 599.9, 120, 4.9, "Cellphone", false);
        Product product8 = new Product("LG-2145", 439.5, 78, 3.9, "Cellphone", false);
        Product product9 = new Product("Apple-10", 1023.9, 74, 4.8, "Cellphone", false);

        Product product10 = new Product("Notebook 1", 99.9, 2, 0.0, "Computer", "some additional info here");
        Product product11 = new Product("Notebook 2", 99.9, 2, 0.0, "Computer", "some additional info here");
        Product product12 = new Product("Notebook 3", 99.9, 2, 0.0, "Computer", "some additional info here");

        Product product13 = new Product("Roomba 698", 299.9, 6, 4.3, "Cleaning", "Standard suction for an every day clean. Provides personalized cleaning suggestions.");
        Product product14 = new Product("Bosch BWD41720", 329.9, 8, 4.1, "Cleaning", "Моющий пылесос Bosch BWD41720 — надежное устройство, позволяющее поддерживать чистоту напольных покрытий любого типа.");
        Product product15 = new Product("Samsung SC4131", 69.9, 28, 4.6, "Cleaning", "Пылесос Samsung SC4131 используется для сухой уборки многокомнатных квартир и жилых домов.");

        Product product16 = new Product("Samsung WW60K40G00W", 549.9, 3, 4.8, "Washing", "Встраиваемая стиральная машина способна за один цикл постирать и высушить до 7 кг вещей.");
        Product product17 = new Product("Hotpoint-Ariston BI WDHG 75148 EU", 999.9, 2, 4.3, "Washing", "Автоматически определяется тип белья, расход воды и моющих средств. Устройство бережно относится к ткани и обеспечивает превосходный результат стирки.");
        Product product18 = new Product("Whirlpool TDLR 60111", 499.9, 6, 3.9, "Washing", "Позволяет бережно очищать от загрязнений одежду и текстильные изделия из хлопка, льна, синтетических волокон и деликатных тканей");

        Product product19 = new Product("Hotpoint-Ariston SPOWHA 409-K", 399.9, 2, 3.8, "Air_conditioner", "some additional info here");
        Product product20 = new Product("LG P09EP2", 529.9, 2, 4.1, "Air_conditioner", "some additional info here");
        Product product21 = new Product("LG Mega Plus P12EP1", 584.9, 2, 4.7, "Air_conditioner", "some additional info here");

        Product product22 = new Product("Apple-12", 3500.5, 20, 4.7, "Cellphone", false);

        category26.setProducts(Arrays.asList(product1, product2, product3));
        category28.setProducts(Arrays.asList(product4, product5, product6));
        category11.setProducts(Arrays.asList(product7, product8, product9, product22));
        category27.setProducts(Arrays.asList(product10, product11, product12));
        category23.setProducts(Arrays.asList(product13, product14, product15));
        category24.setProducts(Arrays.asList(product16, product17, product18));
        category25.setProducts(Arrays.asList(product19, product20, product21));

        categoriesService.saveAll(Arrays.asList(category1, category2, category3, category4, category5, category6, category7,
                category8, category9, category10, category11, category12, category13, category14, category15, category16,
                category17, category18, category19, category20, category21, category22, category23, category24, category25,
                category26, category27, category28, category29));
    }

    /**
     * Метод первичного заполнения харакетристик категорий.
     */
    private void categoryCharacteristicsInit() {
        List<Characteristic> smartphones = new ArrayList<>();
        smartphones.add(characteristicService.findCharacteristicById(1L).orElseThrow(CharacteristicNotFoundException::new));
        smartphones.add(characteristicService.findCharacteristicById(2L).orElseThrow(CharacteristicNotFoundException::new));
        smartphones.add(characteristicService.findCharacteristicById(3L).orElseThrow(CharacteristicNotFoundException::new));
        smartphones.add(characteristicService.findCharacteristicById(4L).orElseThrow(CharacteristicNotFoundException::new));
        smartphones.add(characteristicService.findCharacteristicById(5L).orElseThrow(CharacteristicNotFoundException::new));
        smartphones.add(characteristicService.findCharacteristicById(6L).orElseThrow(CharacteristicNotFoundException::new));
        smartphones.add(characteristicService.findCharacteristicById(7L).orElseThrow(CharacteristicNotFoundException::new));
        smartphones.add(characteristicService.findCharacteristicById(8L).orElseThrow(CharacteristicNotFoundException::new));
        smartphones.add(characteristicService.findCharacteristicById(9L).orElseThrow(CharacteristicNotFoundException::new));
        smartphones.add(characteristicService.findCharacteristicById(10L).orElseThrow(CharacteristicNotFoundException::new));
        smartphones.add(characteristicService.findCharacteristicById(11L).orElseThrow(CharacteristicNotFoundException::new));
        smartphones.add(characteristicService.findCharacteristicById(12L).orElseThrow(CharacteristicNotFoundException::new));
        smartphones.add(characteristicService.findCharacteristicById(13L).orElseThrow(CharacteristicNotFoundException::new));
        smartphones.add(characteristicService.findCharacteristicById(14L).orElseThrow(CharacteristicNotFoundException::new));
        smartphones.add(characteristicService.findCharacteristicById(15L).orElseThrow(CharacteristicNotFoundException::new));
        smartphones.add(characteristicService.findCharacteristicById(16L).orElseThrow(CharacteristicNotFoundException::new));
        smartphones.add(characteristicService.findCharacteristicById(17L).orElseThrow(CharacteristicNotFoundException::new));
        smartphones.add(characteristicService.findCharacteristicById(18L).orElseThrow(CharacteristicNotFoundException::new));
        smartphones.add(characteristicService.findCharacteristicById(19L).orElseThrow(CharacteristicNotFoundException::new));

        List<Characteristic> laptops = new ArrayList<>();
        laptops.add(characteristicService.findCharacteristicById(1L).orElseThrow(CharacteristicNotFoundException::new));
        laptops.add(characteristicService.findCharacteristicById(2L).orElseThrow(CharacteristicNotFoundException::new));
        laptops.add(characteristicService.findCharacteristicById(3L).orElseThrow(CharacteristicNotFoundException::new));
        laptops.add(characteristicService.findCharacteristicById(4L).orElseThrow(CharacteristicNotFoundException::new));
        laptops.add(characteristicService.findCharacteristicById(5L).orElseThrow(CharacteristicNotFoundException::new));
        laptops.add(characteristicService.findCharacteristicById(9L).orElseThrow(CharacteristicNotFoundException::new));
        laptops.add(characteristicService.findCharacteristicById(10L).orElseThrow(CharacteristicNotFoundException::new));
        laptops.add(characteristicService.findCharacteristicById(11L).orElseThrow(CharacteristicNotFoundException::new));
        laptops.add(characteristicService.findCharacteristicById(14L).orElseThrow(CharacteristicNotFoundException::new));
        laptops.add(characteristicService.findCharacteristicById(15L).orElseThrow(CharacteristicNotFoundException::new));
        laptops.add(characteristicService.findCharacteristicById(16L).orElseThrow(CharacteristicNotFoundException::new));
        laptops.add(characteristicService.findCharacteristicById(17L).orElseThrow(CharacteristicNotFoundException::new));
        laptops.add(characteristicService.findCharacteristicById(18L).orElseThrow(CharacteristicNotFoundException::new));

        List<Characteristic> cleaners = new ArrayList<>();
        cleaners.add(characteristicService.findCharacteristicById(4L).orElseThrow(CharacteristicNotFoundException::new));
        cleaners.add(characteristicService.findCharacteristicById(11L).orElseThrow(CharacteristicNotFoundException::new));
        cleaners.add(characteristicService.findCharacteristicById(16L).orElseThrow(CharacteristicNotFoundException::new));
        cleaners.add(characteristicService.findCharacteristicById(19L).orElseThrow(CharacteristicNotFoundException::new));
        cleaners.add(characteristicService.findCharacteristicById(21L).orElseThrow(CharacteristicNotFoundException::new));
        cleaners.add(characteristicService.findCharacteristicById(22L).orElseThrow(CharacteristicNotFoundException::new));

        List<Characteristic> airConditions = new ArrayList<>();
        airConditions.add(characteristicService.findCharacteristicById(4L).orElseThrow(CharacteristicNotFoundException::new));
        airConditions.add(characteristicService.findCharacteristicById(11L).orElseThrow(CharacteristicNotFoundException::new));
        airConditions.add(characteristicService.findCharacteristicById(16L).orElseThrow(CharacteristicNotFoundException::new));
        airConditions.add(characteristicService.findCharacteristicById(19L).orElseThrow(CharacteristicNotFoundException::new));
        airConditions.add(characteristicService.findCharacteristicById(21L).orElseThrow(CharacteristicNotFoundException::new));
        airConditions.add(characteristicService.findCharacteristicById(22L).orElseThrow(CharacteristicNotFoundException::new));
        airConditions.add(characteristicService.findCharacteristicById(23L).orElseThrow(CharacteristicNotFoundException::new));

        List<Characteristic> washing = new ArrayList<>();
        washing.add(characteristicService.findCharacteristicById(4L).orElseThrow(CharacteristicNotFoundException::new));
        washing.add(characteristicService.findCharacteristicById(11L).orElseThrow(CharacteristicNotFoundException::new));
        washing.add(characteristicService.findCharacteristicById(16L).orElseThrow(CharacteristicNotFoundException::new));
        washing.add(characteristicService.findCharacteristicById(19L).orElseThrow(CharacteristicNotFoundException::new));
        washing.add(characteristicService.findCharacteristicById(22L).orElseThrow(CharacteristicNotFoundException::new));
        washing.add(characteristicService.findCharacteristicById(24L).orElseThrow(CharacteristicNotFoundException::new));
        washing.add(characteristicService.findCharacteristicById(25L).orElseThrow(CharacteristicNotFoundException::new));


        Categories categories1 = categoriesService.getCategoryById(11L).orElseThrow(CategoriesNotFoundException::new);
        categories1.setCharacteristics(smartphones);
        categoriesService.saveCategory(categories1);

        Categories categories2 = categoriesService.getCategoryById(14L).orElseThrow(CategoriesNotFoundException::new);
        categories2.setCharacteristics(smartphones);
        categoriesService.saveCategory(categories2);

        Categories categories3 = categoriesService.getCategoryById(28L).orElseThrow(CategoriesNotFoundException::new);
        categories3.setCharacteristics(laptops);
        categoriesService.saveCategory(categories3);

        Categories categories4 = categoriesService.getCategoryById(29L).orElseThrow(CategoriesNotFoundException::new);
        categories4.setCharacteristics(laptops);
        categoriesService.saveCategory(categories4);

        Categories categories5 = categoriesService.getCategoryById(27L).orElseThrow(CategoriesNotFoundException::new);
        categories5.setCharacteristics(laptops);
        categoriesService.saveCategory(categories5);

        Categories categories6 = categoriesService.getCategoryById(26L).orElseThrow(CategoriesNotFoundException::new);
        categories6.setCharacteristics(laptops);
        categoriesService.saveCategory(categories6);

        Categories categories7 = categoriesService.getCategoryById(10L).orElseThrow(CategoriesNotFoundException::new);
        categories7.setCharacteristics(laptops);
        categoriesService.saveCategory(categories7);

        Categories categories8 = categoriesService.getCategoryById(2L).orElseThrow(CategoriesNotFoundException::new);
        categories8.setCharacteristics(cleaners);
        categoriesService.saveCategory(categories8);

        Categories categories9 = categoriesService.getCategoryById(23L).orElseThrow(CategoriesNotFoundException::new);
        categories9.setCharacteristics(cleaners);
        categoriesService.saveCategory(categories9);

        Categories categories10 = categoriesService.getCategoryById(3L).orElseThrow(CategoriesNotFoundException::new);
        categories10.setCharacteristics(smartphones);
        categoriesService.saveCategory(categories10);

        Categories categories11 = categoriesService.getCategoryById(9L).orElseThrow(CategoriesNotFoundException::new);
        categories11.setCharacteristics(laptops);
        categoriesService.saveCategory(categories11);

        Categories categories12 = categoriesService.getCategoryById(2L).orElseThrow(CategoriesNotFoundException::new);
        categories12.setCharacteristics(airConditions);
        categoriesService.saveCategory(categories12);

        Categories categories13 = categoriesService.getCategoryById(25L).orElseThrow(CategoriesNotFoundException::new);
        categories13.setCharacteristics(airConditions);
        categoriesService.saveCategory(categories13);

        Categories categories14 = categoriesService.getCategoryById(24L).orElseThrow(CategoriesNotFoundException::new);
        categories14.setCharacteristics(washing);
        categoriesService.saveCategory(categories14);

        Categories categories15 = categoriesService.getCategoryById(2L).orElseThrow(CategoriesNotFoundException::new);
        categories15.setCharacteristics(washing);
        categoriesService.saveCategory(categories15);
    }

    /**
     * Метод первичного тестового заполнения заказов.
     */
    private void ordersInit() {
        Customer customer = customerService.findCustomerByEmail("customer@mail.ru");
        List<Long> productsIds = new ArrayList<>();
        List<Order> orders = new ArrayList<>();
        List<Long> ordersIds = new ArrayList<>();

        for (int i = 0; i <50; i++) {
            long id = 4 + (long) (Math.random() * 21);
            productsIds.add(id);
        }
        for (int i = 0; i < 15; i++) {
            orders.add(new Order(LocalDateTime.now().minusHours((long) (Math.random() * 24 * 30)), Order.Status.COMPLETED));
            orders.add(new Order(LocalDateTime.now().minusHours((long) (Math.random() * 24 * 30)), Order.Status.INCARTS));
            orders.add(new Order(LocalDateTime.now().minusHours((long) (Math.random() * 24 * 30)), Order.Status.CANCELED));
            orders.add(new Order(LocalDateTime.now(), Order.Status.COMPLETED));
        }
        for (Order order : orders) {
            ordersIds.add(orderService.addOrder(order));
        }
        for (Long ordersId : ordersIds) {
            productInOrderService.addToOrder(productsIds.get((int) (Math.random() * productsIds.size())), ordersId, 1 + (int) (Math.random() * 4));
        }
        customer.setOrders(Set.copyOf(orderService.findAll()));
        userService.updateUser(customer);

    }

    /**
     * Метод первичного тестового заполнения акций.
     */
    private void stockInit() {
        Stock firstStock = Stock.builder()
                .startDate(LocalDate.now().plusDays(2L))
                .endDate(LocalDate.now().plusDays(12L))
                .stockTitle("Команда " + STORE_NAME + " сообщает о начале акции – «Рассрочка или бонусы! HD-" +
                        " и UltraHD-телевизоры Samsung»")
                .stockImg("default.jpg")
                .stockText("<b style=\"font-weight: bold; color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;," +
                        " Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">" +
                        "Телевизоры Samsung&nbsp;</b><span style=\"color: rgb(51, 51, 51); font-family: &quot;PT" +
                        " Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; " +
                        "text-align: start;\">обеспечивают четкое, реалистичное изображение и точную цветопередачу." +
                        " Вы сможете разглядеть мельчайшие детали транслируемой картинки и насладиться объемным " +
                        "звучанием, которое можно настроить под любой контент. Технологии Smart TV, которыми " +
                        "обладают почти все модели из списка, делают из телевизора настоящий мультимедийный " +
                        "центр с доступом к различным фильмам, сериалам и передачам, мессенджерам и соцсетям." +
                        "</span><br style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, " +
                        "Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\"><span " +
                        "style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial, " +
                        "sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">Оформите " +
                        "беспроцентный кредит</span><span style=\"position: relative; font-size: 12px; " +
                        "line-height: normal; vertical-align: baseline; top: -0.5em; color: rgb(51, 51, 51);" +
                        " font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; letter-spacing: " +
                        "0.23px; text-align: start;\">1</span><span style=\"color: rgb(51, 51, 51); font-family:" +
                        " &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: " +
                        "0.23px; text-align: start;\">&nbsp;на&nbsp;</span><b style=\"font-weight: bold; color:" +
                        " rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; " +
                        "font-size: 18px; letter-spacing: 0.23px; text-align: start;\">телевизоры Samsung</b>" +
                        "<span style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, " +
                        "Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">&nbsp;" +
                        "из списка в любом магазине нашей сети или получите до 70 000 рублей на бонусную карту " +
                        "ProZaPass</span><span style=\"position: relative; font-size: 12px; line-height: normal;" +
                        " vertical-align: baseline; top: -0.5em; color: rgb(51, 51, 51); font-family: &quot;PT" +
                        " Sans&quot;, Helvetica, Arial, sans-serif; letter-spacing: 0.23px; text-align: start;\">" +
                        "2</span><span style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica," +
                        " Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">&nbsp;–" +
                        " выбор за вами!</span><br>")
                .published(true)
                .build();

        Stock secondStock = Stock.builder()
                .startDate(LocalDate.now().minusDays(5L))
                .endDate(LocalDate.now().plusDays(3L))
                .stockTitle("Команда " + STORE_NAME + " сообщает о начале акции – «Выгодный онлайн-шопинг с Visa!»")
                .stockImg("default.jpg")
                .stockText("<span style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, " +
                        "Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">С " +
                        "Visa за скидками далеко ходить не надо! Visa и DNS подготовили для вас выгодное " +
                        "предложение – оплачивая покупки онлайн картами Visa в период с 15 сентября по 15 " +
                        "ноября 2020 года вы получаете «Специальные цены» на выборочный ассортимент.</span>" +
                        "<br style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, " +
                        "Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">" +
                        "<br style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, " +
                        "Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">" +
                        "<b style=\"font-weight: bold; color: rgb(51, 51, 51); font-family: &quot;PT " +
                        "Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: " +
                        "0.23px; text-align: start;\">Что надо сделать:</b><br style=\"color: rgb(51, 51, 51);" +
                        " font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; " +
                        "letter-spacing: 0.23px; text-align: start;\"><br style=\"color: rgb(51, 51, 51); " +
                        "font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; " +
                        "letter-spacing: 0.23px; text-align: start;\"><span style=\"color: rgb(51, 51, 51); " +
                        "font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; " +
                        "letter-spacing: 0.23px; text-align: start;\">1. Выберите товар из списка, " +
                        "участвующих в акции.</span><br style=\"color: rgb(51, 51, 51); font-family: &quot;PT" +
                        " Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; " +
                        "text-align: start;\"><span style=\"color: rgb(51, 51, 51); font-family: &quot;PT " +
                        "Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; " +
                        "text-align: start;\">2. Добавьте выбранный товар в корзину.</span><br style=\"color: " +
                        "rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; " +
                        "font-size: 18px; letter-spacing: 0.23px; text-align: start;\"><span style=\"color: " +
                        "rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; " +
                        "font-size: 18px; letter-spacing: 0.23px; text-align: start;\">3. Нажмите «Получить " +
                        "скидку за оплату картой Visa» при оформлении заказа.</span><br style=\"color: " +
                        "rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif;" +
                        " font-size: 18px; letter-spacing: 0.23px; text-align: start;\"><span style=\"color:" +
                        " rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; " +
                        "font-size: 18px; letter-spacing: 0.23px; text-align: start;\">4. Выберите способ " +
                        "оплаты «Оплатить онлайн» – «Банковская карта».</span><br style=\"color: rgb(51, 51, 51);" +
                        " font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; " +
                        "letter-spacing: 0.23px; text-align: start;\"><br style=\"color: rgb(51, 51, 51); " +
                        "font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; " +
                        "letter-spacing: 0.23px; text-align: start;\"><b style=\"font-weight: bold; color: " +
                        "rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; " +
                        "font-size: 18px; letter-spacing: 0.23px; text-align: start;\">ВАЖНО!</b><span " +
                        "style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, " +
                        "Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">" +
                        "</span><br style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, " +
                        "Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: " +
                        "start;\"><br style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, " +
                        "Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: " +
                        "start;\"><span style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, " +
                        "Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: " +
                        "start;\">Скидка будет предоставлена только в том случае, если вы оплачиваете товар " +
                        "картой платежной системы Visa.</span><br style=\"color: rgb(51, 51, 51); font-family: " +
                        "&quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: " +
                        "0.23px; text-align: start;\"><span style=\"color: rgb(51, 51, 51); font-family: " +
                        "&quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: " +
                        "0.23px; text-align: start;\">Если в заказ будет добавлена: дополнительная гарантия, " +
                        "услуга или опциональный товар, то воспользоваться скидкой не возможно.</span><br " +
                        "style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial, " +
                        "sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">" +
                        "<span style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, " +
                        "Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; " +
                        "text-align: start;\">Юридические лица и индивидуальные предприниматели " +
                        "не вправе принимать участие в акции.</span><br style=\"color: rgb(51, 51, 51); " +
                        "font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px;" +
                        " letter-spacing: 0.23px; text-align: start;\">")
                .published(true)
                .build();

        Stock thirdStock = Stock.builder()
                .startDate(LocalDate.now().minusDays(20L))
                .endDate(LocalDate.now().minusDays(5L))
                .stockTitle("Команда DNS сообщает о начале акции – «Рассрочка или бонусы! Инверторные холодильники LG»")
                .stockImg("default.jpg")
                .stockText("<span style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial," +
                        " sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">Благодаря " +
                        "инверторным компрессорам</span><b style=\"font-weight: bold; color: rgb(51, 51, 51); " +
                        "font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; " +
                        "letter-spacing: 0.23px; text-align: start;\">&nbsp;холодильники LG&nbsp;</b><span " +
                        "style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial, " +
                        "sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">работают " +
                        "практически бесшумно. Технология No Frost позволит вам не тратить время и силы на " +
                        "разморозку и ограничит образование плесени и неприятных запахов внутри камер. " +
                        "Вместительные и стильные&nbsp;</span><b style=\"font-weight: bold; color: rgb(51, 51, 51); " +
                        "font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; " +
                        "letter-spacing: 0.23px; text-align: start;\">холодильники</b><span style=\"color: " +
                        "rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; " +
                        "font-size: 18px; letter-spacing: 0.23px; text-align: start;\">&nbsp;</span>" +
                        "<b style=\"font-weight: bold; color: rgb(51, 51, 51); font-family: &quot;PT " +
                        "Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: " +
                        "0.23px; text-align: start;\">LG</b><span style=\"color: rgb(51, 51, 51); " +
                        "font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: " +
                        "18px; letter-spacing: 0.23px; text-align: start;\">&nbsp;с классами энергопотребления " +
                        "A+ и A++ (в зависимости от модели)помогут сохранить ваши запасы свежими продолжительное " +
                        "время и украсят интерьер вашей кухни.</span><br style=\"color: rgb(51, 51, 51); " +
                        "font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; " +
                        "letter-spacing: 0.23px; text-align: start;\"><span style=\"color: rgb(51, 51, 51); " +
                        "font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; " +
                        "letter-spacing: 0.23px; text-align: start;\">Оформите беспроцентный кредит</span>" +
                        "<span style=\"position: relative; font-size: 12px; line-height: normal; vertical-align:" +
                        " baseline; top: -0.5em; color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, " +
                        "Helvetica, Arial, sans-serif; letter-spacing: 0.23px; text-align: start;\">1</span>" +
                        "<span style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica," +
                        " Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">" +
                        "&nbsp;на&nbsp;</span><b style=\"font-weight: bold; color: rgb(51, 51, 51); " +
                        "font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px;" +
                        " letter-spacing: 0.23px; text-align: start;\">инверторные</b><span style=\"color:" +
                        " rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif;" +
                        " font-size: 18px; letter-spacing: 0.23px; text-align: start;\">&nbsp;</span>" +
                        "<b style=\"font-weight: bold; color: rgb(51, 51, 51); font-family: &quot;PT " +
                        "Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; letter-spacing: " +
                        "0.23px; text-align: start;\">холодильники LG</b><span style=\"color: rgb(51, 51, 51);" +
                        " font-family: &quot;PT Sans&quot;, Helvetica, Arial, sans-serif; font-size: 18px; " +
                        "letter-spacing: 0.23px; text-align: start;\">&nbsp;из списка в любом магазине нашей" +
                        " сети или получите 10% от стоимости покупки на бонусную карту ProZaPass</span>" +
                        "<span style=\"position: relative; font-size: 12px; line-height: normal; vertical-align:" +
                        " baseline; top: -0.5em; color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, " +
                        "Helvetica, Arial, sans-serif; letter-spacing: 0.23px; text-align: start;\">2</span>" +
                        "<span style=\"color: rgb(51, 51, 51); font-family: &quot;PT Sans&quot;, Helvetica," +
                        " Arial, sans-serif; font-size: 18px; letter-spacing: 0.23px; text-align: start;\">" +
                        "&nbsp;– выбор за вами!</span>")
                .published(true)
                .build();

        Stock forthStock = Stock.builder()
                .startDate(LocalDate.now().minusDays(3L))
                .endDate(LocalDate.now().plusDays(10L))
                .stockTitle("«Рассрочка или бонусы!")
                .stockImg("default.jpg")
                .stockText("Стиральные машины Whirlpool помогут привести в порядок ваши вещи из различных тканей. " +
                        " Эта техника предлагает множество программ для деликатной и эффективной стирки и сушки." +
                        " Оформите беспроцентный кредит на бытовую технику Whirlpool или получите 10% от стоимости" +
                        " покупки на бонусную карту – выбор за вами!")
                .published(true)
                .build();

        Stock fifthStock = Stock.builder()
                .startDate(LocalDate.now().minusDays(10L))
                .endDate(LocalDate.now().plusDays(10L))
                .stockTitle("«3 года защиты за 990")
                .stockImg("default.jpg")
                .stockText("Самое время обновить компьютер! Выбери подходящую модель с Windows 10 и добавь" +
                        " надёжную защиту от вирусов Kaspersky Internet Security на три года всего за 990 рублей." +
                        " Закажи компьютер с Windows 10. Забери товар и получи промокод на Kaspersky Internet Security" +
                        " за 990 рублей на свой Email и в личный кабинет в течение трёх дней после получения заказа." +
                        " Обязательно используй Бонусную карту – её можно оформить прямо на сайте.")
                .published(false)
                .build();

        Stock sixthStock = Stock.builder()
                .startDate(LocalDate.now().minusDays(2L))
                .endDate(LocalDate.now().plusDays(30L))
                .stockTitle("«Требуй скидку!")
                .stockImg("default.jpg")
                .stockText("До 31 декабря требуй и получай скидку на смартфоны, телевизоры" +
                        " и бытовую технику из акционного списка." +
                        " Активируй свою скидку на странице товара." +
                        " Товары из акционного списка отмечены специальным знаком \"Требуй скидку!\" на сайте." +
                        " На товары из акционного перечня распространяются правила программы лояльности.")
                .published(false)
                .build();

        stockService.addStock(firstStock);
        stockService.addStock(secondStock);
        stockService.addStock(thirdStock);
        stockService.addStock(forthStock);
        stockService.addStock(fifthStock);
        stockService.addStock(sixthStock);
    }

    public void sharedStockInit() {
        String[] socialNetworkNames = {"facebook", "vk", "twitter"};
        List<Stock> stocks = stockService.findAll();
        List<Customer> customers = customerService.findAll();
        Long firstNumber = stocks.get(0).getId();
        Long lastNumber = stocks.get(stocks.size() - 1).getId();
        Random random = new Random();
        for (Stock stock : stocks) {
            for (Customer customer : customers) {
                long generatedLongForStock = firstNumber + (long) (Math.random() * (lastNumber - firstNumber));
                SharedStock sharedStock = SharedStock.builder()
                        .customer(customer)
                        .stock(stockService.findStockById(generatedLongForStock))
                        .socialNetworkName(socialNetworkNames[random.nextInt(socialNetworkNames.length)])
                        .build();
                sharedStockService.addSharedStock(sharedStock);
            }
        }
    }

    /**
     * Метод первичного заполнения новостей, которыми поделились
     */
    public void sharedNewsInit() {
        String[] socialNetworkNames = {"facebook", "vk", "twitter"};
        List<News> news = newsService.findAll();//10
        List<Customer> customers = customerService.findAll();//23
        Long firstNumber = news.get(0).getId();//
        Long lastNumber = news.get(news.size() - 1).getId();//10
        Random random = new Random();
        for (News oneNews : news) {
            for (Customer customer : customers) {
                long generatedLongForSNews = firstNumber + (long) (Math.random() * (lastNumber - firstNumber));
                SharedNews sharedNews = SharedNews.builder()
                        .customer(customer)
                        .news(newsService.findById(generatedLongForSNews))
                        .socialNetworkName(socialNetworkNames[random.nextInt(socialNetworkNames.length)])
                        .build();
                sharedNewsService.addSharedNews(sharedNews);
            }
        }
    }

    /**
     * Метод первичного заполнения акций, которые были отправлены пользователям
     */
    public void sentStockInit() {
        Random random = new Random();
        List<Stock> stocks = stockService.findAll();
        List<Customer> customers = customerService.findAll();
        for (int i = 0; i < 20; i++) {
            sentStockService.addSentStock(SentStock.builder().customer(customers.get(random.nextInt(customers.size())))
                    .stock(stocks.get(random.nextInt(stocks.size())))
                    .sentDate(LocalDate.now().plusDays(random.nextInt(8)))
                    .build());
        }
    }

    /**
     * Метод инициализации новостей и акций в профиле менеджера для тестирования динамической пагинации.
     */
    public void paginationNewsAndStocksInit() {
        for (int i = 0; i < 50; i++) {
            News news = News.builder()
                    .title(i + " Сегодня стартует предзаказ на флагманские продукты Samsung!")
                    .anons("Сделайте предзаказ и получите подарок.")
                    .fullText("<h1><span style=\"font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">" +
                            "<font color=\"#0000ff\">Хорошие новости в Online-Shop!</font></span></h1><h1>" +
                            "<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                            " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                            " font-size: 16px;\">Сегодня стартует предзаказ на новые флагманские продукты Samsung!<b></b>" +
                            "</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px;" +
                            " color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;" +
                            " font-size: 16px;\"><br></p></h1>")
                    .postingDate(LocalDate.now().minusDays(Math.round(Math.random() * 20)))
                    .archived(false)
                    .build();
            newsService.save(news);
        }

        for (int i = 0; i < 50; i++) {
            Stock stock = Stock.builder()
                    .startDate(LocalDate.now().minusDays(20L))
                    .endDate(LocalDate.now().minusDays(5L))
                    .stockTitle("Скидки на игры ЕА!")
                    .stockText("В течение действия акции вы можете приобрести игры ЕА из списка по" +
                            " очень привлекательным ценам!" +
                            "Вы можете стать обладателем игр EA для Xbox One, Nintendo Switch и PS4" +
                            " в различных жанрах. Ощутите всю радость победы в хоккейном матче, станьте" +
                            " стремительным уличным автогонщиком, постройте дом мечты или очутитесь в" +
                            " фантастическом мире и примите участие в битве галактических масштабов!")
                    .build();
            stockService.addStock(stock);
        }
    }

    /**
     * Метод первичной инициалзации адресов, 2 адреса для магазина и 1 адрес прикрепляется к заказу
     */
    private void addressInit() {
        Address address1 = new Address("Татарстан", "Казань", "Революционная", "25","12", "420078", true);
        Address address2 = new Address("Московская область", "Москва", "Ленина", "126", "12","420078", true);
        Address address3 = new Address("Тамбовская область", "Тамбов", "Запорожская", "11","12", "420079", false);
        Address address4 = new Address("Тамбовская область", "Тамбов", "Запорожская", "12", "12","420080", false);
        addressService.addAddress(address1);
        addressService.addAddress(address2);
        addressService.addAddress(address3);
        addressService.addAddress(address4);

        Set<Address> userAddresses = new HashSet<>();
        userAddresses.add(addressService.findAddressById(3L).get());
        userAddresses.add(addressService.findAddressById(4L).get());
        Customer userToUpdate = customerService.findCustomerByEmail("customer@mail.ru");
        userToUpdate.setUserAddresses(userAddresses);
        userService.updateUser(userToUpdate);
    }

    /**
     * ini method for email stock distribution settings.
     * creates task for stock distribution with status not active
     * and current time
     */
    public void taskSettingsInit() {
        TaskSettings taskSettings = TaskSettings.builder()
                .taskName("stockMailDistribution")
                .active(false)
                .startTime(LocalTime.now().truncatedTo(ChronoUnit.MINUTES))
                .build();
        taskSettingsService.addNewTaskSetting(taskSettings);

        TaskSettings taskSettings2 = TaskSettings.builder()
                .taskName("dailyPriceCreate")
                .active(true)
                .startTime(LocalTime.now().truncatedTo(ChronoUnit.MINUTES))
                .build();
        taskSettingsService.addNewTaskSetting(taskSettings2);

        TaskSettings taskSettings3 = TaskSettings.builder()
                .taskName("deleteExpiredCustomersProfile")
                .active(true)
                .startTime(LocalTime.now().truncatedTo(ChronoUnit.MINUTES))
                .build();
        taskSettingsService.addNewTaskSetting(taskSettings3);

        TaskSettings taskSettings4 = TaskSettings.builder()
                .taskName("receiveEmailSubscribeConfirmation")
                .active(true)
                .startTime(LocalTime.now().truncatedTo(ChronoUnit.MINUTES))
                .build();
        taskSettingsService.addNewTaskSetting(taskSettings4);
    }

    /**
     * init method for email stock distribution template
     * creates template for email.
     */
    public void commonSettingsInit() {

        CommonSettings emailStockDistributionTemplate = CommonSettings.builder()
                .settingName("stock_email_distribution_template")
                .textValue("<p>Уважаемый @@user@@, спешим сообщить вам о новых Акциях!" +
                        "</p><p>@@stockList@@</p><p>С Уважением</p><p>" + environment.getProperty("production-url") + "</p>")
                .build();
        CommonSettings newCommentAnswerTemplate = CommonSettings.builder()
                .settingName("new_comment_answer_template")
                .textValue("<p>Уважаемый @@user@@, появился новый ответ на Ваш @@parentType@@ к товару @@product@@!" +
                        "</p><p>@@stockList@@</p><p>С Уважением</p><p>" + environment.getProperty("production-url") + "</p>")
                .build();
        CommonSettings priceChangeDistributionTemplate = CommonSettings.builder()
                .settingName("price_change_distribution_template")
                .textValue("<p>Уважаемый @@user@@, спешим сообщить вам о снижении цены</p>" +
                        "<p>Старая @@oldPrice@@ на @@product@@, новая @@newPrice@@</p>" +
                        "<p><a href=\"@@url@@\">Отписаться от рассылки на продукт</a></p>" +
                        "  <p><a href=\"@@url2@@\">Отписаться от всех рассылок</a></p>" +
                        "<p>С Уважением</p><p>" + environment.getProperty("production-url") + "</p>")
                .build();
        CommonSettings subscribeConfirmationTemplate = CommonSettings.builder()
                .settingName("subscribe_confirmation_template")
                .textValue("<p>Уважаемый @@user@@, для подтверждении подписки о снижении цены</p>" +
                        "<p>Пожалуйста, ответьте на это письмо с любым текстом</p>" +
                        "<p>С Уважением</p><p>" + environment.getProperty("production-url") + "</p>")
                .build();
        CommonSettings badWordsEnabled = CommonSettings.builder()
                .settingName("bad_words_enabled")
                .textValue("true")
                .build();
        CommonSettings maintenanceModeTemplate = CommonSettings.builder()
                .settingName("maintenance_mode")
                .textValue("ROLE_ADMIN")
                .status(false)
                .build();
        CommonSettings storeName = CommonSettings.builder()
                .settingName("store_name")
                .textValue(STORE_NAME)
                .status(false)
                .build();
        commonSettingsService.addSetting(emailStockDistributionTemplate);
        commonSettingsService.addSetting(priceChangeDistributionTemplate);
        commonSettingsService.addSetting(subscribeConfirmationTemplate);
        commonSettingsService.addSetting(badWordsEnabled);
        commonSettingsService.addSetting(maintenanceModeTemplate);
        commonSettingsService.addSetting(storeName);
        commonSettingsService.addSetting(newCommentAnswerTemplate);
    }

    /**
     * init method for distribution TemplatesMailingSettings templates
     * creates template for email.
     */
    public void templatesMailingSettingsInit() {

        TemplatesMailingSettings confirmationTokenToResetPasswordTemplate = TemplatesMailingSettings.builder()
                .settingName("send_confirmation_token_to_reset_password")
                .textValue("<p>Привет, @@user@@ Вы сделали запрос на сброс пароля , для подтверждения перейдите по ссылке:" +
                        "@@url@@" + "/restorepassword/@@confirmationToken@@</p>" +
                        "<p>С Уважением</p><p>" + environment.getProperty("production-url") + "</p>")
                .build();
        TemplatesMailingSettings regNewAccountTemplate = TemplatesMailingSettings.builder()
                .settingName("reg_new_account")
                .textValue("<p>Hello, @@userEmail@@!</p><p>" + " Welcome to " + STORE_NAME + ". Please, visit next link: @@url@@</p>" +
                        "<p>С Уважением</p><p>" +
                        "@@production-url@@" + "</p>")
                .build();
        TemplatesMailingSettings changeUsersPassTemplate = TemplatesMailingSettings.builder()
                .settingName("change_users_pass")
                .textValue("<p>Привет, @@user@@ Ваш пароль изменен</p>" +
                        "<p>С Уважением</p><p>" + environment.getProperty("production-url") + "</p>")
                .build();
        TemplatesMailingSettings changeUsersMailTemplate = TemplatesMailingSettings.builder()
                .settingName("change_users_mail")
                .textValue("<p>Здравствуйте, @@user@@" +
                        "Вы запросили изменение адреса электронной почты. Подтвердите, пожалуйста, по ссылке:" +
                        "@@url@@" + "/activatenewmail/@@confirmationToken@@</p>" +
                        "<p>С Уважением</p><p>" + environment.getProperty("production-url") + "</p>")
                .build();
        TemplatesMailingSettings activateUserTemplate = TemplatesMailingSettings.builder()
                .settingName("activate_user")
                .textValue("<p>Привет, @@user@@!</p><p>Вы зарегистрировались на сайте @@url@@. Пароль для входа в вашу учетную запись @@password@@, " +
                        "можете сменить его в личном кабинете.</p>" +
                        "<p>С Уважением,</p><p>" + environment.getProperty("production-url") + "</p>")
                .status(false)
                .build();
        TemplatesMailingSettings restorePasswordTemplate = TemplatesMailingSettings.builder()
                .settingName("restore_password")
                .textValue("<p>Привет, @@user@@ Вам Сгенерирован временный новый пароль @@newPassword@@</p>" +
                        "<p>С Уважением</p><p>" + environment.getProperty("production-url") + "</p>")
                .status(false)
                .build();
        templatesMailingSettingsService.addSetting(confirmationTokenToResetPasswordTemplate);
        templatesMailingSettingsService.addSetting(regNewAccountTemplate);
        templatesMailingSettingsService.addSetting(changeUsersPassTemplate);
        templatesMailingSettingsService.addSetting(changeUsersMailTemplate);
        templatesMailingSettingsService.addSetting(activateUserTemplate);
        templatesMailingSettingsService.addSetting(restorePasswordTemplate);
    }

    /**
     * Init method for topics
     */
    public void feedbackTopicsInit() {
        TopicsCategory topicsCategory1 = TopicsCategory.builder()
                .actual(true)
                .categoryName("Коммерческое предложение")
                .build();
        TopicsCategory topicsCategory2 = TopicsCategory.builder()
                .actual(true)
                .categoryName("Оценка работы")
                .build();
        TopicsCategory topicsCategory3 = TopicsCategory.builder()
                .actual(true)
                .categoryName("Ошибки и пожелания")
                .build();
        TopicsCategory topicsCategory4 = TopicsCategory.builder()
                .actual(false)
                .categoryName("Работа в компании")
                .build();

        topicsCategoryService.create(topicsCategory1);
        topicsCategoryService.create(topicsCategory2);
        topicsCategoryService.create(topicsCategory3);
        topicsCategoryService.create(topicsCategory4);


        Topic topic1 = Topic.builder()
                .topicName("По товарам")
                .topicsCategory(topicsCategory1)
                .build();
        Topic topic2 = Topic.builder()
                .topicName("По логистике")
                .topicsCategory(topicsCategory1)
                .build();
        Topic topic3 = Topic.builder()
                .topicName("По кредитам")
                .topicsCategory(topicsCategory1)
                .build();
        Topic topic4 = Topic.builder()
                .topicName("Магазин")
                .topicsCategory(topicsCategory2)
                .build();
        Topic topic5 = Topic.builder()
                .topicName("Доставка")
                .topicsCategory(topicsCategory2)
                .build();
        Topic topic6 = Topic.builder()
                .topicName("Сервисный центр")
                .topicsCategory(topicsCategory2)
                .build();
        Topic topic7 = Topic.builder()
                .topicName("Работа сайта")
                .topicsCategory(topicsCategory3)
                .build();
        Topic topic8 = Topic.builder()
                .topicName("Новости, акции")
                .topicsCategory(topicsCategory3)
                .build();
        Topic topic9 = Topic.builder()
                .topicName("Описание товара")
                .topicsCategory(topicsCategory3)
                .build();
        Topic topic10 = Topic.builder()
                .topicName("Резюме")
                .topicsCategory(topicsCategory4)
                .build();

        topicService.create(topic1);
        topicService.create(topic2);
        topicService.create(topic3);
        topicService.create(topic4);
        topicService.create(topic5);
        topicService.create(topic6);
        topicService.create(topic7);
        topicService.create(topic8);
        topicService.create(topic9);
        topicService.create(topic10);
    }

    /**
     * Init method for comments
     */
    public void commentsInit() {

        Comment comment1 = new Comment();
        Comment comment2 = new Comment();
        Comment comment3 = new Comment();

        comment1.setId(1L);
        comment2.setId(2L);
        comment3.setId(3L);
        comment1.setCommentDate(LocalDateTime.now());
        comment2.setCommentDate(LocalDateTime.now());
        comment3.setCommentDate(LocalDateTime.now());
        comment1.setCustomer(userService.findById(3L).stream().findFirst().orElse(null));
        comment2.setCustomer(userService.findById(3L).stream().findFirst().orElse(null));
        comment3.setCustomer(userService.findById(3L).stream().findFirst().orElse(null));
        comment1.setProductId(3L);
        comment2.setProductId(3L);
        comment3.setProductId(3L);
        comment1.setContent("Awesome comment. Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium " +
                "doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto " +
                "beatae vitae dicta sunt explicabo.");
        comment2.setContent("Just another one comment. At vero eos et accusamus et iusto odio dignissimos ducimus qui " +
                "blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint " +
                "occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est " +
                "laborum et dolorum fuga.");
        comment3.setContent("Awful comment. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus " +
                "saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae.");

        commentService.addCommentInit(comment1);
        commentService.addCommentInit(comment2);
        commentService.addCommentInit(comment3);
    }

    /**
     * Init method for review
     */
    public void reviewsInit() {

        Review review1 = new Review();
        Review review2 = new Review();
        Review review3 = new Review();

        review1.setId(1L);
        review2.setId(2L);
        review3.setId(3L);
        review1.setReviewDate(LocalDateTime.now());
        review2.setReviewDate(LocalDateTime.now());
        review3.setReviewDate(LocalDateTime.now());
        review1.setCustomer(userService.findById(3L).stream().findFirst().orElse(null));
        review2.setCustomer(userService.findById(3L).stream().findFirst().orElse(null));
        review3.setCustomer(userService.findById(3L).stream().findFirst().orElse(null));
        review1.setProductId(1L);
        review2.setProductId(1L);
        review3.setProductId(1L);
        review1.setContent("Пожалуй это лучший компьютер который я когда либо видел и держал в руках, надеюсь он сослужит" +
                " мне хорошую службу.\n" +
                "Перед покупкой смотрел на конфигурацию с i9 и 5500m, в итоге по опыту прошлых поколений сделал выбор в " +
                "пользу i7 и 5300m и не прогадал. В моих задачах производительность идентичная MacBook с i9 но более " +
                "старшая модель при высоких нагрузках нагревается и сбрасывает частоты что у данной модели не обнаружено," +
                " и в итоге по сравнению с ноутбуком товарища c i9 5500m Xcode работает у меня стабильней, и " +
                "производительность на одно ядро выше, и работает он куда тише.");
        review2.setContent("По производительности большой разницы для меня нет.Проект собирается плюс минус так же. " +
                "Только теперь температура во время сборки доходит максимум до 65 градусов вместо 80-85.Ноутбук стал " +
                "немного больше по габаритам, но в сумку от пятнашки вошел.Звук вроде хороший, качество экрана особо " +
                "без изменений. Не понятно достают ли клавиши до экрана когда ноутбук лежит в сумке, вроде отметин " +
                "пока на нем нет. На старом доставали и царапали покрытие экрана. Можно выкинуть внешнюю клавиатуру. " +
                "Мышь теперь то же не нужна.");
        review3.setContent("Для меня важно было найти баланс между решением рабочих задач (разработка на Java), " +
                "любовью к потреблению контента (YouTube, PornoHub, Горячие мамочки) и периодическими поездками. " +
                "Производительная начинка вкупе с большой диагональю экрана и автономностью работы у этой прошки " +
                "полностью меня устроили. Надеюсь, Apple докажет и в этот раз свою состоятельность, и менять мак в связи" +
                "с не актульностью придется не раньше 2025)");

        reviewService.addReviewInit(review1);
        reviewService.addReviewInit(review2);
        reviewService.addReviewInit(review3);
    }

    public void characteristicsInit() {
        List<Characteristic> characteristicList = new ArrayList<>();

        characteristicList.add(new Characteristic("Диагональ экрана"));
        characteristicList.add(new Characteristic("Разрешение экрана"));
        characteristicList.add(new Characteristic("Модель процессора"));
        characteristicList.add(new Characteristic("Год выпуска"));
        characteristicList.add(new Characteristic("Объем оперативной памяти"));
        characteristicList.add(new Characteristic("Объем встроенной памяти"));
        characteristicList.add(new Characteristic("Количество камер"));
        characteristicList.add(new Characteristic("NFC"));
        characteristicList.add(new Characteristic("GPS"));
        characteristicList.add(new Characteristic("Емкость аккумулятора"));
        characteristicList.add(new Characteristic("Вес"));
        characteristicList.add(new Characteristic("Тип экрана"));
        characteristicList.add(new Characteristic("Частота обновления дисплея"));
        characteristicList.add(new Characteristic("Сенсорный экран"));
        characteristicList.add(new Characteristic("Операционная система"));
        characteristicList.add(new Characteristic("Производитель"));
        characteristicList.add(new Characteristic("Wi-Fi"));
        characteristicList.add(new Characteristic("Версия bluetooth"));
        characteristicList.add(new Characteristic("Цвет"));
        characteristicList.add(new Characteristic("Размеры"));
        characteristicList.add(new Characteristic("Потребляемая мощность"));
        characteristicList.add(new Characteristic("Уровень шума"));
        characteristicList.add(new Characteristic("Мощность"));
        characteristicList.add(new Characteristic("Максимальный вес загрузки"));
        characteristicList.add(new Characteristic("Количество программ"));

        characteristicList.forEach(characteristicService::saveCharacteristic);

    }

    private void productCharacteristicsInit() {
        long productId = productService.findProductByName("XIAOMI-Mi10").orElseThrow(ProductNotFoundException::new).getId();

        productCharacteristicService.addProductCharacteristic(productId, characteristicService.findByCharacteristicName("Диагональ экрана").orElseThrow(CharacteristicNotFoundException::new).getId(), "6.67");
        productCharacteristicService.addProductCharacteristic(productId, characteristicService.findByCharacteristicName("Разрешение экрана").orElseThrow(CharacteristicNotFoundException::new).getId(), "2340x1080");
        productCharacteristicService.addProductCharacteristic(productId, characteristicService.findByCharacteristicName("Модель процессора").orElseThrow(CharacteristicNotFoundException::new).getId(), "Qualcomm Snapdragon 865");
        productCharacteristicService.addProductCharacteristic(productId, characteristicService.findByCharacteristicName("Год выпуска").orElseThrow(CharacteristicNotFoundException::new).getId(), "2020");
        productCharacteristicService.addProductCharacteristic(productId, characteristicService.findByCharacteristicName("Объем оперативной памяти").orElseThrow(CharacteristicNotFoundException::new).getId(), "8");
        productCharacteristicService.addProductCharacteristic(productId, characteristicService.findByCharacteristicName("Объем встроенной памяти").orElseThrow(CharacteristicNotFoundException::new).getId(), "256");
        productCharacteristicService.addProductCharacteristic(productId, characteristicService.findByCharacteristicName("Количество камер").orElseThrow(CharacteristicNotFoundException::new).getId(), "4");
        productCharacteristicService.addProductCharacteristic(productId, characteristicService.findByCharacteristicName("NFC").orElseThrow(CharacteristicNotFoundException::new).getId(), "Есть");
        productCharacteristicService.addProductCharacteristic(productId, characteristicService.findByCharacteristicName("GPS").orElseThrow(CharacteristicNotFoundException::new).getId(), "Есть");
        productCharacteristicService.addProductCharacteristic(productId, characteristicService.findByCharacteristicName("Емкость аккумулятора").orElseThrow(CharacteristicNotFoundException::new).getId(), "4780");
        productCharacteristicService.addProductCharacteristic(productId, characteristicService.findByCharacteristicName("Вес").orElseThrow(CharacteristicNotFoundException::new).getId(), "0.208");
        productCharacteristicService.addProductCharacteristic(productId, characteristicService.findByCharacteristicName("Тип экрана").orElseThrow(CharacteristicNotFoundException::new).getId(), "IPS");
        productCharacteristicService.addProductCharacteristic(productId, characteristicService.findByCharacteristicName("Частота обновления дисплея").orElseThrow(CharacteristicNotFoundException::new).getId(), "144");
        productCharacteristicService.addProductCharacteristic(productId, characteristicService.findByCharacteristicName("Сенсорный экран").orElseThrow(CharacteristicNotFoundException::new).getId(), "Да");
        productCharacteristicService.addProductCharacteristic(productId, characteristicService.findByCharacteristicName("Операционная система").orElseThrow(CharacteristicNotFoundException::new).getId(), "Android");
        productCharacteristicService.addProductCharacteristic(productId, characteristicService.findByCharacteristicName("Производитель").orElseThrow(CharacteristicNotFoundException::new).getId(), "Xiaomi");
        productCharacteristicService.addProductCharacteristic(productId, characteristicService.findByCharacteristicName("Wi-Fi").orElseThrow(CharacteristicNotFoundException::new).getId(), "Есть");
        productCharacteristicService.addProductCharacteristic(productId, characteristicService.findByCharacteristicName("Версия bluetooth").orElseThrow(CharacteristicNotFoundException::new).getId(), "5.1");
        productCharacteristicService.addProductCharacteristic(productId, characteristicService.findByCharacteristicName("Цвет").orElseThrow(CharacteristicNotFoundException::new).getId(), "Black");

        long lgId = productService.findProductByName("LG-2145").orElseThrow(ProductNotFoundException::new).getId();

        productCharacteristicService.addProductCharacteristic(lgId, characteristicService.findByCharacteristicName("Диагональ экрана").orElseThrow(CharacteristicNotFoundException::new).getId(), "6.0");
        productCharacteristicService.addProductCharacteristic(lgId, characteristicService.findByCharacteristicName("Разрешение экрана").orElseThrow(CharacteristicNotFoundException::new).getId(), "1920x960");
        productCharacteristicService.addProductCharacteristic(lgId, characteristicService.findByCharacteristicName("Модель процессора").orElseThrow(CharacteristicNotFoundException::new).getId(), "Qualcomm Snapdragon 835");
        productCharacteristicService.addProductCharacteristic(lgId, characteristicService.findByCharacteristicName("Год выпуска").orElseThrow(CharacteristicNotFoundException::new).getId(), "2019");
        productCharacteristicService.addProductCharacteristic(lgId, characteristicService.findByCharacteristicName("Объем оперативной памяти").orElseThrow(CharacteristicNotFoundException::new).getId(), "4");
        productCharacteristicService.addProductCharacteristic(lgId, characteristicService.findByCharacteristicName("Объем встроенной памяти").orElseThrow(CharacteristicNotFoundException::new).getId(), "128");
        productCharacteristicService.addProductCharacteristic(lgId, characteristicService.findByCharacteristicName("Количество камер").orElseThrow(CharacteristicNotFoundException::new).getId(), "1");
        productCharacteristicService.addProductCharacteristic(lgId, characteristicService.findByCharacteristicName("NFC").orElseThrow(CharacteristicNotFoundException::new).getId(), "Нет");
        productCharacteristicService.addProductCharacteristic(lgId, characteristicService.findByCharacteristicName("GPS").orElseThrow(CharacteristicNotFoundException::new).getId(), "Есть");
        productCharacteristicService.addProductCharacteristic(lgId, characteristicService.findByCharacteristicName("Емкость аккумулятора").orElseThrow(CharacteristicNotFoundException::new).getId(), "2540");
        productCharacteristicService.addProductCharacteristic(lgId, characteristicService.findByCharacteristicName("Вес").orElseThrow(CharacteristicNotFoundException::new).getId(), "0.198");
        productCharacteristicService.addProductCharacteristic(lgId, characteristicService.findByCharacteristicName("Тип экрана").orElseThrow(CharacteristicNotFoundException::new).getId(), "IPS");
        productCharacteristicService.addProductCharacteristic(lgId, characteristicService.findByCharacteristicName("Частота обновления дисплея").orElseThrow(CharacteristicNotFoundException::new).getId(), "150");
        productCharacteristicService.addProductCharacteristic(lgId, characteristicService.findByCharacteristicName("Сенсорный экран").orElseThrow(CharacteristicNotFoundException::new).getId(), "Да");
        productCharacteristicService.addProductCharacteristic(lgId, characteristicService.findByCharacteristicName("Операционная система").orElseThrow(CharacteristicNotFoundException::new).getId(), "Android");
        productCharacteristicService.addProductCharacteristic(lgId, characteristicService.findByCharacteristicName("Производитель").orElseThrow(CharacteristicNotFoundException::new).getId(), "LG");
        productCharacteristicService.addProductCharacteristic(lgId, characteristicService.findByCharacteristicName("Wi-Fi").orElseThrow(CharacteristicNotFoundException::new).getId(), "Есть");
        productCharacteristicService.addProductCharacteristic(lgId, characteristicService.findByCharacteristicName("Версия bluetooth").orElseThrow(CharacteristicNotFoundException::new).getId(), "4.0");
        productCharacteristicService.addProductCharacteristic(lgId, characteristicService.findByCharacteristicName("Цвет").orElseThrow(CharacteristicNotFoundException::new).getId(), "Black");

        long appleId = productService.findProductByName("Apple-10").orElseThrow(ProductNotFoundException::new).getId();

        productCharacteristicService.addProductCharacteristic(appleId, characteristicService.findByCharacteristicName("Диагональ экрана").orElseThrow(CharacteristicNotFoundException::new).getId(), "5.8");
        productCharacteristicService.addProductCharacteristic(appleId, characteristicService.findByCharacteristicName("Разрешение экрана").orElseThrow(CharacteristicNotFoundException::new).getId(), "2436x1125");
        productCharacteristicService.addProductCharacteristic(appleId, characteristicService.findByCharacteristicName("Модель процессора").orElseThrow(CharacteristicNotFoundException::new).getId(), "Apple A11 Bionic");
        productCharacteristicService.addProductCharacteristic(appleId, characteristicService.findByCharacteristicName("Год выпуска").orElseThrow(CharacteristicNotFoundException::new).getId(), "2017");
        productCharacteristicService.addProductCharacteristic(appleId, characteristicService.findByCharacteristicName("Объем оперативной памяти").orElseThrow(CharacteristicNotFoundException::new).getId(), "3");
        productCharacteristicService.addProductCharacteristic(appleId, characteristicService.findByCharacteristicName("Объем встроенной памяти").orElseThrow(CharacteristicNotFoundException::new).getId(), "256");
        productCharacteristicService.addProductCharacteristic(appleId, characteristicService.findByCharacteristicName("Количество камер").orElseThrow(CharacteristicNotFoundException::new).getId(), "2");
        productCharacteristicService.addProductCharacteristic(appleId, characteristicService.findByCharacteristicName("NFC").orElseThrow(CharacteristicNotFoundException::new).getId(), "Есть");
        productCharacteristicService.addProductCharacteristic(appleId, characteristicService.findByCharacteristicName("GPS").orElseThrow(CharacteristicNotFoundException::new).getId(), "Нет");
        productCharacteristicService.addProductCharacteristic(appleId, characteristicService.findByCharacteristicName("Емкость аккумулятора").orElseThrow(CharacteristicNotFoundException::new).getId(), "2700");
        productCharacteristicService.addProductCharacteristic(appleId, characteristicService.findByCharacteristicName("Вес").orElseThrow(CharacteristicNotFoundException::new).getId(), "0.174");
        productCharacteristicService.addProductCharacteristic(appleId, characteristicService.findByCharacteristicName("Тип экрана").orElseThrow(CharacteristicNotFoundException::new).getId(), "OLED");
        productCharacteristicService.addProductCharacteristic(appleId, characteristicService.findByCharacteristicName("Частота обновления дисплея").orElseThrow(CharacteristicNotFoundException::new).getId(), "137");
        productCharacteristicService.addProductCharacteristic(appleId, characteristicService.findByCharacteristicName("Сенсорный экран").orElseThrow(CharacteristicNotFoundException::new).getId(), "Да");
        productCharacteristicService.addProductCharacteristic(appleId, characteristicService.findByCharacteristicName("Операционная система").orElseThrow(CharacteristicNotFoundException::new).getId(), "iOS");
        productCharacteristicService.addProductCharacteristic(appleId, characteristicService.findByCharacteristicName("Производитель").orElseThrow(CharacteristicNotFoundException::new).getId(), "Apple");
        productCharacteristicService.addProductCharacteristic(appleId, characteristicService.findByCharacteristicName("Wi-Fi").orElseThrow(CharacteristicNotFoundException::new).getId(), "Есть");
        productCharacteristicService.addProductCharacteristic(appleId, characteristicService.findByCharacteristicName("Версия bluetooth").orElseThrow(CharacteristicNotFoundException::new).getId(), "5.0");
        productCharacteristicService.addProductCharacteristic(appleId, characteristicService.findByCharacteristicName("Цвет").orElseThrow(CharacteristicNotFoundException::new).getId(), "White");

        long apple2Id = productService.findProductByName("Apple-12").orElseThrow(ProductNotFoundException::new).getId();

        productCharacteristicService.addProductCharacteristic(apple2Id, characteristicService.findByCharacteristicName("Диагональ экрана").orElseThrow(CharacteristicNotFoundException::new).getId(), "6.1");
        productCharacteristicService.addProductCharacteristic(apple2Id, characteristicService.findByCharacteristicName("Разрешение экрана").orElseThrow(CharacteristicNotFoundException::new).getId(), "2532x1170");
        productCharacteristicService.addProductCharacteristic(apple2Id, characteristicService.findByCharacteristicName("Модель процессора").orElseThrow(CharacteristicNotFoundException::new).getId(), "Apple A14 Bionic");
        productCharacteristicService.addProductCharacteristic(apple2Id, characteristicService.findByCharacteristicName("Год выпуска").orElseThrow(CharacteristicNotFoundException::new).getId(), "2020");
        productCharacteristicService.addProductCharacteristic(apple2Id, characteristicService.findByCharacteristicName("Объем оперативной памяти").orElseThrow(CharacteristicNotFoundException::new).getId(), "4");
        productCharacteristicService.addProductCharacteristic(apple2Id, characteristicService.findByCharacteristicName("Объем встроенной памяти").orElseThrow(CharacteristicNotFoundException::new).getId(), "128");
        productCharacteristicService.addProductCharacteristic(apple2Id, characteristicService.findByCharacteristicName("Количество камер").orElseThrow(CharacteristicNotFoundException::new).getId(), "2");
        productCharacteristicService.addProductCharacteristic(apple2Id, characteristicService.findByCharacteristicName("NFC").orElseThrow(CharacteristicNotFoundException::new).getId(), "Есть");
        productCharacteristicService.addProductCharacteristic(apple2Id, characteristicService.findByCharacteristicName("GPS").orElseThrow(CharacteristicNotFoundException::new).getId(), "Есть");
        productCharacteristicService.addProductCharacteristic(apple2Id, characteristicService.findByCharacteristicName("Емкость аккумулятора").orElseThrow(CharacteristicNotFoundException::new).getId(), "2815");
        productCharacteristicService.addProductCharacteristic(apple2Id, characteristicService.findByCharacteristicName("Вес").orElseThrow(CharacteristicNotFoundException::new).getId(), "0.164");
        productCharacteristicService.addProductCharacteristic(apple2Id, characteristicService.findByCharacteristicName("Тип экрана").orElseThrow(CharacteristicNotFoundException::new).getId(), "OLED");
        productCharacteristicService.addProductCharacteristic(apple2Id, characteristicService.findByCharacteristicName("Частота обновления дисплея").orElseThrow(CharacteristicNotFoundException::new).getId(), "143");
        productCharacteristicService.addProductCharacteristic(apple2Id, characteristicService.findByCharacteristicName("Сенсорный экран").orElseThrow(CharacteristicNotFoundException::new).getId(), "Да");
        productCharacteristicService.addProductCharacteristic(apple2Id, characteristicService.findByCharacteristicName("Операционная система").orElseThrow(CharacteristicNotFoundException::new).getId(), "iOS");
        productCharacteristicService.addProductCharacteristic(apple2Id, characteristicService.findByCharacteristicName("Производитель").orElseThrow(CharacteristicNotFoundException::new).getId(), "Apple");
        productCharacteristicService.addProductCharacteristic(apple2Id, characteristicService.findByCharacteristicName("Wi-Fi").orElseThrow(CharacteristicNotFoundException::new).getId(), "Есть");
        productCharacteristicService.addProductCharacteristic(apple2Id, characteristicService.findByCharacteristicName("Версия bluetooth").orElseThrow(CharacteristicNotFoundException::new).getId(), "5.0");
        productCharacteristicService.addProductCharacteristic(apple2Id, characteristicService.findByCharacteristicName("Цвет").orElseThrow(CharacteristicNotFoundException::new).getId(), "Red");

        long samsungId = productService.findProductByName("Samsung SC4131").orElseThrow(ProductNotFoundException::new).getId();

        productCharacteristicService.addProductCharacteristic(samsungId, characteristicService.findByCharacteristicName("Год выпуска").orElseThrow(CharacteristicNotFoundException::new).getId(), "2019");
        productCharacteristicService.addProductCharacteristic(samsungId, characteristicService.findByCharacteristicName("Вес").orElseThrow(CharacteristicNotFoundException::new).getId(), "3.760");
        productCharacteristicService.addProductCharacteristic(samsungId, characteristicService.findByCharacteristicName("Производитель").orElseThrow(CharacteristicNotFoundException::new).getId(), "Samsung");
        productCharacteristicService.addProductCharacteristic(samsungId, characteristicService.findByCharacteristicName("Цвет").orElseThrow(CharacteristicNotFoundException::new).getId(), "Blue");
        productCharacteristicService.addProductCharacteristic(samsungId, characteristicService.findByCharacteristicName("Потребляемая мощность").orElseThrow(CharacteristicNotFoundException::new).getId(), "1600");
        productCharacteristicService.addProductCharacteristic(samsungId, characteristicService.findByCharacteristicName("Уровень шума").orElseThrow(CharacteristicNotFoundException::new).getId(), "74");

        long airCondId = productService.findProductByName("LG P09EP2").orElseThrow(ProductNotFoundException::new).getId();

        productCharacteristicService.addProductCharacteristic(airCondId, characteristicService.findByCharacteristicName("Год выпуска").orElseThrow(CharacteristicNotFoundException::new).getId(), "2017");
        productCharacteristicService.addProductCharacteristic(airCondId, characteristicService.findByCharacteristicName("Вес").orElseThrow(CharacteristicNotFoundException::new).getId(), "26.0");
        productCharacteristicService.addProductCharacteristic(airCondId, characteristicService.findByCharacteristicName("Производитель").orElseThrow(CharacteristicNotFoundException::new).getId(), "LG");
        productCharacteristicService.addProductCharacteristic(airCondId, characteristicService.findByCharacteristicName("Цвет").orElseThrow(CharacteristicNotFoundException::new).getId(), "White");
        productCharacteristicService.addProductCharacteristic(airCondId, characteristicService.findByCharacteristicName("Потребляемая мощность").orElseThrow(CharacteristicNotFoundException::new).getId(), "800");
        productCharacteristicService.addProductCharacteristic(airCondId, characteristicService.findByCharacteristicName("Уровень шума").orElseThrow(CharacteristicNotFoundException::new).getId(), "30");
        productCharacteristicService.addProductCharacteristic(airCondId, characteristicService.findByCharacteristicName("Мощность").orElseThrow(CharacteristicNotFoundException::new).getId(), "74");

        long laptopId = productService.findProductByName("ACER-543").orElseThrow(ProductNotFoundException::new).getId();

        productCharacteristicService.addProductCharacteristic(laptopId, characteristicService.findByCharacteristicName("Диагональ экрана").orElseThrow(CharacteristicNotFoundException::new).getId(), "15.6");
        productCharacteristicService.addProductCharacteristic(laptopId, characteristicService.findByCharacteristicName("Разрешение экрана").orElseThrow(CharacteristicNotFoundException::new).getId(), "1366x768");
        productCharacteristicService.addProductCharacteristic(laptopId, characteristicService.findByCharacteristicName("Модель процессора").orElseThrow(CharacteristicNotFoundException::new).getId(), "Turion II");
        productCharacteristicService.addProductCharacteristic(laptopId, characteristicService.findByCharacteristicName("Год выпуска").orElseThrow(CharacteristicNotFoundException::new).getId(), "2016");
        productCharacteristicService.addProductCharacteristic(laptopId, characteristicService.findByCharacteristicName("Объем оперативной памяти").orElseThrow(CharacteristicNotFoundException::new).getId(), "3");
        productCharacteristicService.addProductCharacteristic(laptopId, characteristicService.findByCharacteristicName("GPS").orElseThrow(CharacteristicNotFoundException::new).getId(), "Нет");
        productCharacteristicService.addProductCharacteristic(laptopId, characteristicService.findByCharacteristicName("Емкость аккумулятора").orElseThrow(CharacteristicNotFoundException::new).getId(), "4400");
        productCharacteristicService.addProductCharacteristic(laptopId, characteristicService.findByCharacteristicName("Вес").orElseThrow(CharacteristicNotFoundException::new).getId(), "2.6");
        productCharacteristicService.addProductCharacteristic(laptopId, characteristicService.findByCharacteristicName("Сенсорный экран").orElseThrow(CharacteristicNotFoundException::new).getId(), "Нет");
        productCharacteristicService.addProductCharacteristic(laptopId, characteristicService.findByCharacteristicName("Операционная система").orElseThrow(CharacteristicNotFoundException::new).getId(), "Windows");
        productCharacteristicService.addProductCharacteristic(laptopId, characteristicService.findByCharacteristicName("Производитель").orElseThrow(CharacteristicNotFoundException::new).getId(), "Acer");
        productCharacteristicService.addProductCharacteristic(laptopId, characteristicService.findByCharacteristicName("Wi-Fi").orElseThrow(CharacteristicNotFoundException::new).getId(), "Есть");
        productCharacteristicService.addProductCharacteristic(laptopId, characteristicService.findByCharacteristicName("Версия bluetooth").orElseThrow(CharacteristicNotFoundException::new).getId(), "3.0");
        productCharacteristicService.addProductCharacteristic(laptopId, characteristicService.findByCharacteristicName("Цвет").orElseThrow(CharacteristicNotFoundException::new).getId(), "Black");

        long washingId = productService.findProductByName("Whirlpool TDLR 60111").orElseThrow(ProductNotFoundException::new).getId();

        productCharacteristicService.addProductCharacteristic(washingId, characteristicService.findByCharacteristicName("Год выпуска").orElseThrow(CharacteristicNotFoundException::new).getId(), "2016");
        productCharacteristicService.addProductCharacteristic(washingId, characteristicService.findByCharacteristicName("Вес").orElseThrow(CharacteristicNotFoundException::new).getId(), "62.0");
        productCharacteristicService.addProductCharacteristic(washingId, characteristicService.findByCharacteristicName("Производитель").orElseThrow(CharacteristicNotFoundException::new).getId(), "Whirlpool");
        productCharacteristicService.addProductCharacteristic(washingId, characteristicService.findByCharacteristicName("Цвет").orElseThrow(CharacteristicNotFoundException::new).getId(), "White");
        productCharacteristicService.addProductCharacteristic(washingId, characteristicService.findByCharacteristicName("Уровень шума").orElseThrow(CharacteristicNotFoundException::new).getId(), "70");
        productCharacteristicService.addProductCharacteristic(washingId, characteristicService.findByCharacteristicName("Максимальный вес загрузки").orElseThrow(CharacteristicNotFoundException::new).getId(), "74");
        productCharacteristicService.addProductCharacteristic(washingId, characteristicService.findByCharacteristicName("Количество программ").orElseThrow(CharacteristicNotFoundException::new).getId(), "9");


    }

    /**
     * Инициализация тестовых данных для BadWords
     * импортируется из файла в проекте: src/main/resources/uploads/import/badword_for_import.txt
     * если еще не импортированы, можно поставить аннотацию "@PostConstruct" перед методом
     * @throws java.io.FileNotFoundException если файл не найден
     */
    private void badWordInit() {
        //Стартовый набор Стоп-Слов
        //Полный набор ипортируйте в Настройках
        String separator = File.separator;
        String path = "src" + separator + "main" + separator + "resources" + separator + "uploads" + separator + "import" + separator + "badword_for_import.txt";
        String[] startBadWord = {"хрен","фиг","плохой","говно","ерунда","бляха","екарный","дерьмо"}; //если файл отсутствует, пусть хоть эти будут
        try {
            startBadWord = Files.lines(Paths.get(path)).collect(Collectors.joining("")).split(", ");
        } catch (IOException e) {
            log.error("файл плохих слов badword_for_import.txt не найден и не загружен в базу");
        }

        for (String textToSave:startBadWord) {
            if (badWordsService.existsBadWordByName(textToSave)) continue;
            BadWords badWordsToSave = new BadWords(textToSave, true);
            badWordsService.saveWord(badWordsToSave);
        }
    }
}
