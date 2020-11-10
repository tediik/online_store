package com.jm.online_store.config;

import com.jm.online_store.enums.DayOfWeekForStockSend;
import com.jm.online_store.model.Address;
import com.jm.online_store.model.Categories;
import com.jm.online_store.model.Comment;
import com.jm.online_store.model.CommonSettings;
import com.jm.online_store.model.Customer;
import com.jm.online_store.model.Description;
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
import com.jm.online_store.model.Topic;
import com.jm.online_store.model.TopicsCategory;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.AddressService;
import com.jm.online_store.service.interf.BasketService;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.CommentService;
import com.jm.online_store.service.interf.CommonSettingsService;
import com.jm.online_store.service.interf.FavouritesGroupService;
import com.jm.online_store.service.interf.NewsService;
import com.jm.online_store.service.interf.OrderService;
import com.jm.online_store.service.interf.ProductInOrderService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.ReviewService;
import com.jm.online_store.service.interf.RoleService;
import com.jm.online_store.service.interf.SentStockService;
import com.jm.online_store.service.interf.SharedNewsService;
import com.jm.online_store.service.interf.SharedStockService;
import com.jm.online_store.service.interf.StockService;
import com.jm.online_store.service.interf.TaskSettingsService;
import com.jm.online_store.service.interf.TopicService;
import com.jm.online_store.service.interf.TopicsCategoryService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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

/**
 * класс первичного заполнения таблиц.
 * <p>
 * для первичного заполнения базы данных раскомментировать аннотацию
 * "@PostConstruct" и поменять значение  ключа "spring.jpa.hibernate.ddl-auto"
 * в файле "application.yml" с "update" на "create" или "create-drop".
 */
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


    /**
     * Основной метод для заполнения базы данных.
     * Вызов методов добавлять в этод метод.
     * Следить за последовательностью вызова.
     */
//     @PostConstruct
     //раскомментировать аннотацию при первом запуске проекта для создания таблиц БД, потом закомментировать
    public void initDataBaseFilling() {
        roleInit();
        newsInit();
        productInit();
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

        roleService.addRole(adminRole);
        roleService.addRole(customerRole);
        roleService.addRole(managerRole);
        roleService.addRole(serviceRole);

        User admin = new User("admin@mail.ru", "1");
        User manager = new User("manager@mail.ru", "1");
        Customer customer = new Customer("customer@mail.ru", "1");
        User service = new User("service@mail.ru", "1");

        Optional<Role> admnRole = roleService.findByName("ROLE_ADMIN");
        Optional<Role> custRole = roleService.findByName("ROLE_CUSTOMER");
        Optional<Role> managRole = roleService.findByName("ROLE_MANAGER");
        Optional<Role> servRole = roleService.findByName("ROLE_SERVICE");

        Set<Role> customerRoles = new HashSet<>();
        Set<Role> adminRoles = new HashSet<>();
        Set<Role> managerRoles = new HashSet<>();
        Set<Role> serviceRoles = new HashSet<>();

        customerRoles.add(custRole.get());
        adminRoles.add(admnRole.get());
        adminRoles.add(custRole.get());
        adminRoles.add(servRole.get());
        managerRoles.add(managRole.get());
        serviceRoles.add(servRole.get());

        manager.setRoles(managerRoles);
        admin.setRoles(adminRoles);
        customer.setRoles(customerRoles);
        service.setRoles(serviceRoles);

        userService.addUser(manager);
        userService.addUser(customer);
        userService.addUser(admin);
        userService.addUser(service);

        Random random = new Random();
        for (int i = 1; i < 20; i++) {
            Customer customer1 = new Customer("customer" + i + "@mail.ru",
                    DayOfWeekForStockSend.values()[random.nextInt(6)],
                    String.valueOf(i));
            customer1.setRoles(customerRoles);
            userService.addUser(customer1);
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
                .anons("Online_store открывает продлёнку на скидки!")
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
                        "с online_store.</b><br></p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left:\"" +
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
                        "В online_store представлен огромный выбор ноутбуков. Также у нас вы найдете все " +
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
                        "подключаться к интернету и управлять умным домом. Online_store подскажет, на что " +
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
                        "В online_store представлен огромный выбор телевизоров. Также у нас вы найдете все " +
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
                        "Online_store подскажет, на что обратить внимание при выборе.\n" +
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
                        "кредит или с использованием бонусных карт. В online_store представлен огромный выбор " +
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
                        "Какие гаджеты взять с собой и как подготовиться к поездке — подскажет online_store.</b><br>" +
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
                        "дорогу вам не хватает. Заходите в online_store, покупайте и путешествуйте с комфортом!</b><br></p>" +
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
     * Метод первичного тестового заполнения товаров, избранного и корзины.
     */
    private void productInit() {

        Categories category1 = new Categories("Ноутбуки", "Компьютеры");
        Categories category2 = new Categories("Компьютеры", "Компьютеры");
        Categories category3 = new Categories("Смартфоны", "Смартфоны и гаджеты");
        Categories category4 = new Categories("Комплектующие", "Компьютеры");
        Categories category5 = new Categories("Периферия", "Компьютеры");
        Categories category6 = new Categories("Планшеты", "Смартфоны и гаджеты");
        Categories category7 = new Categories("Электронные книги", "Смартфоны и гаджеты");
        Categories category8 = new Categories("Аксессуары", "Смартфоны и гаджеты");
        Categories category9 = new Categories("Телевизоры", "ТВ и развлечения");
        Categories category10 = new Categories("Игры", "ТВ и развлечения");
        Categories category11 = new Categories("Аудиотехника", "ТВ и развлечения");
        Categories category12 = new Categories("Оргтехника", "Офис и сеть");
        Categories category13 = new Categories("Роутеры и сетевое оборудование", "Офис и сеть");
        Categories category14 = new Categories("Техника для кухни", "Бытовая техника");
        Categories category15 = new Categories("Техника для уборки", "Бытовая техника");
        Categories category16 = new Categories("Стиральные и сушильные машины", "Бытовая техника");
        Categories category17 = new Categories("Климатическая техника", "Бытовая техника");

        Product product1 = new Product("Asus-NX4567", 299.9, 15, 4.0, category1);
        Product product2 = new Product("ACER-543", 399.9, 10, 4.2, category1);
        Product product3 = new Product("Samsung-7893", 259.9, 20, 4.6, category1);

        Product product4 = new Product("NX-7893-PC-09878", 924.0, 3, 4.2, category2);
        Product product5 = new Product("ZX-7654-PC-1", 1223.9, 7, 4.7, category2);
        Product product6 = new Product("NY-2345-PC-453", 1223.9, 7, 4.7, category2);

        Product product7 = new Product("XIAOMI-Mi10", 599.9, 120, 4.9, category3);
        Product product8 = new Product("LG-2145", 439.5, 78, 3.9, category3);
        Product product9 = new Product("Apple-10", 1023.9, 74, 4.8, category3);

        Product product10 = new Product("Roomba 698", 299.9, 6, 4.3, category15);
        Product product11 = new Product("Bosch BWD41720", 329.9, 8, 4.1, category15);
        Product product12 = new Product("Samsung SC4131", 69.9, 28, 4.6, category15);

        Product product13 = new Product("Samsung WW60K40G00W", 549.9, 3, 4.8, category16);
        Product product14 = new Product("Hotpoint-Ariston BI WDHG 75148 EU", 999.9, 2, 4.3, category16);
        Product product15 = new Product("Whirlpool TDLR 60111", 499.9, 6, 3.9, category16);

        Product product16 = new Product("Hotpoint-Ariston SPOWHA 409-K", 399.9, 2, 3.8, category17);
        Product product17 = new Product("LG P09EP2", 529.9, 2, 4.1, category17);
        Product product18 = new Product("LG Mega Plus P12EP1", 584.9, 2, 4.7, category17);

        Description description1 = new Description("12344232", "ASUS", 2, "500x36x250", "black", 1.3, "Оснащенный 15.6-дюймовым экраном ноутбук ASUS TUF Gaming FX505DT-AL087 – игровой портативный компьютер, который ничто не помешает вам использовать и в роли универсального домашнего компьютера.");
        Description description2 = new Description("23464223", "ACER", 1, "654x38x245", "yellow", 2.1, "some additional info here");
        Description description3 = new Description("99966732", "Samsung", 3, "550x27x368", "white", 1.1, "some additional info here");
        Description description4 = new Description("33311432NXU", "ATop corp.", 3, "698x785x368", "black", 3.1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris id condimentum tortor. Aliquam tristique tempus ipsum id laoreet. Pellentesque ligula lectus, finibus eget auctor pellentesque, molestie ac elit. Fusce in maximus leo. Morbi maximus vel enim");
        Description description5 = new Description("33211678NXU", "ATop corp.", 3, "690x765x322", "black", 3.5, "some additional info here");
        Description description6 = new Description("333367653Rh", "Rhino corp.", 3, "612x678x315", "orange", 2.8, "some additional info here");
        Description description7 = new Description("X54355543455", "Xiaomi", 1, "115x56x13", "grey", 0.115, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris id condimentum tortor. Aliquam tristique tempus ipsum id laoreet. Pellentesque ligula lectus, finibus eget auctor pellentesque, molestie ac elit. Fusce in maximus leo. Morbi maximus vel enim", 512, 512, "1920x960", true, "5.0");
        Description description8 = new Description("L55411165632", "LG", 2, "110x48x19", "black", 0.198, "some additional info here", 1024, 256, "1920x960", false, "4.0");
        Description description9 = new Description("A88563902273", "Apple corp.", 1, "112x55x8", "black", 0.176, "some additional info here", 2048, 128, "1024x480", true, "5.0");
        Description description10 = new Description("XYZ270011101230600001", "iRobot", 2, "300x75x300", "silver", 3.0, "Standard suction for an every day clean. Provides personalized cleaning suggestions.");
        Description description11 = new Description("CFE867594316856743201", "Bosch", 1, "360x350x490", "violet", 10.9, "Моющий пылесос Bosch BWD41720 — надежное устройство, позволяющее поддерживать чистоту напольных покрытий любого типа.");
        Description description12 = new Description("08UV8NEM703511M", "Samsung", 1, "365x230x275", "blue", 3.8, "Пылесос Samsung SC4131 используется для сухой уборки многокомнатных квартир и жилых домов.");
        Description description13 = new Description("X54355543455", "Samsung", 2, "850x600x450", "white", 54.0, "Позволяет бережно очищать от загрязнений одежду и текстильные изделия из хлопка, льна, синтетических волокон и деликатных тканей");
        Description description14 = new Description("A886UW16575632", "Whirlpool Corp.", 1, "815x595x540", "white", 65.0, "Встраиваемая стиральная машина способна за один цикл постирать и высушить до 7 кг вещей", 1024, 256, "1920x960", false, "4.0");
        Description description15 = new Description("A88563902273", "Whirlpool Corp.", 1, "900x420x600", "white", 49.0, "Автоматически определяется тип белья, расход воды и моющих средств. Устройство бережно относится к ткани и обеспечивает превосходный результат стирки.");
        Description description16 = new Description("AHP4388843455", "Whirlpool Corp.", 1, "270x835x210", "white", 6.5, "Кондиционер Hotpoint-Ariston SPOWHA 409-K используется для создания благоприятного микроклимата в помещениях площадью 27 м²");
        Description description17 = new Description("L856XZ11564632", "LG", 1, "265x756x184", "white", 7.4, "Кондиционер LG P09EP2 используется для установки оптимальной температуры в помещении дома или офиса площадью 20 м²");
        Description description18 = new Description("L014ZZ10018974", "LG", 1, "302x837x189; 483x717x230", "white", 8.7, "Модель LG Mega Plus P12EP1 будет оптимальна для установки в помещении площадью 35 м²");

        product1.setDescriptions(description1);
        product2.setDescriptions(description2);
        product3.setDescriptions(description3);
        product4.setDescriptions(description4);
        product5.setDescriptions(description5);
        product6.setDescriptions(description6);
        product7.setDescriptions(description7);
        product8.setDescriptions(description8);
        product9.setDescriptions(description9);
        product10.setDescriptions(description10);
        product11.setDescriptions(description11);
        product12.setDescriptions(description12);
        product13.setDescriptions(description13);
        product14.setDescriptions(description14);
        product15.setDescriptions(description15);
        product16.setDescriptions(description16);
        product17.setDescriptions(description17);
        product18.setDescriptions(description18);

        category1.setProducts(Arrays.asList(product1, product2, product3));
        category2.setProducts(Arrays.asList(product4, product5, product6));
        category3.setProducts(Arrays.asList(product7, product8, product9));
        category15.setProducts(Arrays.asList(product10, product11, product12));
        category16.setProducts(Arrays.asList(product13, product14, product15));
        category17.setProducts(Arrays.asList(product16, product17, product18));

        categoriesService.saveAll(Arrays.asList(category1, category2, category3,
                category4, category5, category6, category7, category8, category9, category10, category11,
                category12, category13, category14, category15, category16, category17));

        productService.saveAllProducts(Arrays.asList(product1, product2, product3, product4, product5,
                product6, product7, product8, product9, product10, product11, product12, product13, product14,
                product15, product16, product17, product18));

        Set<Product> productSet = new HashSet<>();
        productSet.add(product1);
        productSet.add(product2);
        productSet.add(product3);

        User customer = userService.findByEmail("customer@mail.ru").get();
        customer.setFavouritesGoods(productSet);
        userService.updateUser(customer);

        //Создание основного списка(Все товары) избранных товаров
        FavouritesGroup favouritesGroup = new FavouritesGroup();
        favouritesGroup.setName("Все товары");
        favouritesGroup.setProducts(productSet);
        favouritesGroup.setUser(customer);
        favouritesGroupService.save(favouritesGroup);

        SubBasket subBasket_1 = new SubBasket();
        subBasket_1.setProduct(product1);
        subBasket_1.setCount(1);
        basketService.addBasket(subBasket_1);
        SubBasket subBasket_2 = new SubBasket();
        subBasket_2.setProduct(product3);
        subBasket_2.setCount(1);
        basketService.addBasket(subBasket_2);
        List<SubBasket> subBasketList = new ArrayList<>();
        subBasketList.add(subBasket_1);
        subBasketList.add(subBasket_2);
        customer.setUserBasket(subBasketList);
        userService.updateUser(customer);
    }

    /**
     * Метод первичного тестового заполнения заказов.
     */
    private void ordersInit() {
        User customer = userService.findByEmail("customer@mail.ru").get();

        List<Long> productsIds = new ArrayList<>();
        productsIds.add(productService.findProductByName("NX-7893-PC-09878").get().getId());
        productsIds.add(productService.findProductByName("Asus-NX4567").get().getId());
        productsIds.add(productService.findProductByName("ACER-543").get().getId());
        productsIds.add(productService.findProductByName("XIAOMI-Mi10").get().getId());
        productsIds.add(productService.findProductByName("LG-2145").get().getId());
        productsIds.add(productService.findProductByName("Apple-10").get().getId());
        productsIds.add(productService.findProductByName("Roomba 698").get().getId());
        productsIds.add(productService.findProductByName("Bosch BWD41720").get().getId());
        productsIds.add(productService.findProductByName("Hotpoint-Ariston BI WDHG 75148 EU").get().getId());
        productsIds.add(productService.findProductByName("LG Mega Plus P12EP1").get().getId());
        productsIds.add(productService.findProductByName("Hotpoint-Ariston SPOWHA 409-K").get().getId());

        List<Order> orders = new ArrayList<>();
        orders.add(new Order(LocalDateTime.of(2019, 12, 31, 22, 10), Order.Status.COMPLETED));
        orders.add(new Order(LocalDateTime.of(2020, 1, 23, 13, 37), Order.Status.COMPLETED));
        orders.add(new Order(LocalDateTime.of(2020, 3, 10, 16, 51), Order.Status.INCARTS));
        orders.add(new Order(LocalDateTime.of(2020, 6, 13, 15, 3), Order.Status.CANCELED));
        orders.add(new Order(LocalDateTime.of(2020, 7, 18, 16, 18), Order.Status.COMPLETED));
        orders.add(new Order(LocalDateTime.of(2020, 7, 24, 11, 9), Order.Status.CANCELED));
        orders.add(new Order(LocalDateTime.of(2020, 8, 3, 15, 43), Order.Status.COMPLETED));
        orders.add(new Order(LocalDateTime.of(2020, 8, 18, 17, 33), Order.Status.CANCELED));
        orders.add(new Order(LocalDateTime.of(2020, 9, 16, 10, 21), Order.Status.INCARTS));
        orders.add(new Order(LocalDateTime.now(), Order.Status.INCARTS));

        List<Long> ordersIds = new ArrayList<>();
        for (Order order : orders) {
            ordersIds.add(orderService.addOrder(order));
        }

        productInOrderService.addToOrder(productsIds.get(0), ordersIds.get(0), 1);
        productInOrderService.addToOrder(productsIds.get(1), ordersIds.get(0), 2);

        productInOrderService.addToOrder(productsIds.get(2), ordersIds.get(1), 1);

        productInOrderService.addToOrder(productsIds.get(4), ordersIds.get(2), 2);

        productInOrderService.addToOrder(productsIds.get(3), ordersIds.get(3), 1);
        productInOrderService.addToOrder(productsIds.get(4), ordersIds.get(3), 2);
        productInOrderService.addToOrder(productsIds.get(5), ordersIds.get(3), 3);

        productInOrderService.addToOrder(productsIds.get(5), ordersIds.get(4), 3);
        productInOrderService.addToOrder(productsIds.get(6), ordersIds.get(5), 1);
        productInOrderService.addToOrder(productsIds.get(6), ordersIds.get(6), 4);
        productInOrderService.addToOrder(productsIds.get(7), ordersIds.get(7), 1);
        productInOrderService.addToOrder(productsIds.get(8), ordersIds.get(8), 1);
        productInOrderService.addToOrder(productsIds.get(9), ordersIds.get(9), 2);
        productInOrderService.addToOrder(productsIds.get(10), ordersIds.get(9), 1);
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
                .stockTitle("Команда Online-Store сообщает о начале акции – «Рассрочка или бонусы! HD-" +
                        " и UltraHD-телевизоры Samsung»")
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
                .stockTitle("Команда Online-Store сообщает о начале акции – «Выгодный онлайн-шопинг с Visa!»")
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
        List<User> users = userService.findAll();
        Long firstNumber = stocks.get(0).getId();
        Long lastNumber = stocks.get(stocks.size() - 1).getId();
        Random random = new Random();
        for (Stock stock : stocks) {
            for (User user : users) {
                long generatedLongForStock = firstNumber + (long) (Math.random() * (lastNumber - firstNumber));
                SharedStock sharedStock = SharedStock.builder()
                        .user(user)
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
        List<User> users = userService.findAll();//23
        Long firstNumber = news.get(0).getId();//
        Long lastNumber = news.get(news.size() - 1).getId();//10
        Random random = new Random();
        for (News oneNews : news) {
            for (User user : users) {
                long generatedLongForSNews = firstNumber + (long) (Math.random() * (lastNumber - firstNumber));
                SharedNews sharedNews = SharedNews.builder()
                        .user(user)
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
        List<User> users = userService.findAll();

        for (int i = 0; i < 20; i++) {
            sentStockService.addSentStock(SentStock.builder().user(users.get(random.nextInt(users.size())))
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
        Address address1 = new Address("420077", "Татарстан", "Казань", "Революционная", "25", true);
        Address address2 = new Address("420078", "Московская область", "Москва", "Ленина", "126", true);
        Address address3 = new Address("420079", "Тамбовская область", "Тамбов", "Запорожская", "11", false);
        Address address4 = new Address("420080", "Тамбовская область", "Тамбов", "Запорожская", "12", false);
        addressService.addAddress(address1);
        addressService.addAddress(address2);
        addressService.addAddress(address3);
        addressService.addAddress(address4);

        Set<Address> userAddresses = new HashSet<>();
        userAddresses.add(addressService.findAddressById(3L).get());
        userAddresses.add(addressService.findAddressById(4L).get());
        User userToUpdate = userService.findByEmail("customer@mail.ru").get();
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
    }

    /**
     * init method for email stock distribution template
     * creates template for email.
     */
    public void commonSettingsInit() {
        CommonSettings emailStockDistributionTemplate = CommonSettings.builder()
                .settingName("stock_email_distribution_template")
                .textValue("<p>Уважаемый @@user@@, спешим сообщить вам о новых Акциях!" +
                        "</p><p>@@stockList@@</p><p>С Уважением</p><p>Online-store.ru</p>")
                .build();
        CommonSettings priceChangeDistributionTemplate = CommonSettings.builder()
                .settingName("price_change_distribution_template")
                .textValue("<p>Уважаемый @@user@@, спешим сообщить вам о снижении цены</p>" +
                        "<p>Старая @@oldPrice@@ на @@product@@, новая @@newPrice@@</p>" +
                        "<p>С Уважением</p><p>Online-store.ru</p>")
                .build();
        commonSettingsService.addSetting(emailStockDistributionTemplate);
        commonSettingsService.addSetting(priceChangeDistributionTemplate);
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
}
