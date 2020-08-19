package com.jm.online_store.config;

import com.jm.online_store.model.News;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.NewsService;
import com.jm.online_store.service.interf.RoleService;
import com.jm.online_store.service.interf.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
public class DataInitializer {

    private final UserService userService;
    private final RoleService roleService;
    private final NewsService newsService;

    @Autowired
    public DataInitializer(UserService userService, RoleService roleService, NewsService newsService) {
        this.userService = userService;
        this.roleService = roleService;
        this.newsService = newsService;
    }

    /*@PostConstruct*/
    public void roleConstruct() {
        Role adminRole = new Role("ROLE_ADMIN");
        Role customerRole = new Role("ROLE_CUSTOMER");
        Role managerRole = new Role("ROLE_MANAGER");

        roleService.addRole(adminRole);
        roleService.addRole(customerRole);
        roleService.addRole(managerRole);

        User admin = new User("admin@mail.ru", "1");
        User manager = new User("manager@mail.ru", "1");
        User customer = new User("customer@mail.ru", "1");

        Optional<Role> admnRole = roleService.findByName("ROLE_ADMIN");
        Optional<Role> custRole = roleService.findByName("ROLE_CUSTOMER");
        Optional<Role> managRole = roleService.findByName("ROLE_MANAGER");

        Set<Role> customerRoles = new HashSet<>();
        Set<Role> adminRoles = new HashSet<>();
        Set<Role> managerRoles = new HashSet<>();

        customerRoles.add(custRole.get());
        adminRoles.add(admnRole.get());
        adminRoles.add(custRole.get());
        managerRoles.add(managRole.get());

        manager.setRoles(managerRoles);
        admin.setRoles(adminRoles);
        customer.setRoles(customerRoles);

        userService.addUser(manager);
        userService.addUser(customer);
        userService.addUser(admin);
    }

    /**
     * Method fills the News table with initial data
     */
    @PostConstruct
    public void newsConstruct() {
        News firstNews = News.builder()
                .title("Акция от XP-Pen: Выигай обучение в Skillbox!")
                .anons("Не пропустите розыгрыш потрясающих призов.")
                .fullText("<p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; font-family: &quot;PT Sans&quot;, Arial, sans-serif;\"><b style=\"color: rgb(255, 0, 0); font-size: 1rem;\">Если вы любите создавать и повсюду ищите вдохновение, то следующая новость для вас!</b><br></p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">XP-Pen проводят акицию с невроятно крутым призовым фоном, вы можете выиграть один из сертификатов на годовое обучение 2D или 3D рисованию в Skillbox, а также фирменные сувениры от бренда.</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">Что нужно делать?</p><ul style=\"margin-right: 0px; margin-bottom: 0px; margin-left: 0px; padding: 0px; list-style-type: none; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\"><li style=\"margin: 0px; padding: 0px;\">1.Купить в <b>Online-Shop</b> любой графический планшет или интерактивный дисплей XP-Pen с 15 августа по 15 сентября 2020 года.</li><li style=\"margin: 0px; padding: 0px;\">2.Пришлите серийный номер изделия на эл. почту sales_ru@xp-pen.com</li><li style=\"margin: 0px; padding: 0px;\">3.Дождитесь 17 сентября 2020 года – XP-Pen подведут итоги методом рандома, так что шанс есть у каждого!</li></ul><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">Вы только взгляните на эти призы!</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">1 сертификат на обучение в школе SkillBox по курсу «Профессия 2D-художник»</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">2 сертификата на обучение в школе SkillBox по курсу «Профессия 3D-художник»</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">5 наборов фирменных сувениров от XP-Pen (в набор входит рюкзачок XP-Pen, брелок с фирменным персонажем XP-Pen лисенком Фениксом и чехол для пера)</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">2 сертификата на 50% скидку на обучение в школе SkillBox по курсу «Профессия 2D-художник»</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">3 сертификата на 50% скидку на обучение в школе SkillBox по курсу «Профессия 3D-художник»</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">Online-shop желает всем удачи!</p>")
                .postingDate(LocalDateTime.now())
                .build();

        News secondNews = News.builder()
                .title("Акция от AORUS: Играй и смотри!")
                .anons("Купите монитор и получите целый год фильмов с ivi и вкусную пиццу в подарок.")
                .fullText("<h2 style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; font-family: &quot;PT Sans&quot;, Arial, sans-serif;\"><b style=\"\"><font color=\"#ff0000\">Хорошие новости в Online-Shop!</font></b></h2><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\"><span style=\"background-color: rgb(0, 255, 0);\">Смотреть кино стало еще интереснее и вкуснее.</span> При покупке одного из мониторов AORUS вы получаете в подарок 12 месяцев подписки на ivi и промокод на 1200 рублей в Додо-пицца. Акция продлится с 10 по 31 августа.</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">Что нужно делать?</p><ul style=\"margin-right: 0px; margin-bottom: 0px; margin-left: 0px; padding: 0px; list-style-type: none; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\"><li style=\"margin: 0px; padding: 0px;\">1.Выберите в Online-Shop один из мониторов AORUS:&nbsp;<font color=\"#7c0596\"><span style=\"border-bottom: 1px solid rgba(124, 5, 150, 0.3); outline-color: initial; outline-width: initial; border-top-color: rgba(124, 5, 150, 0.3); border-right-color: rgba(124, 5, 150, 0.3); border-left-color: rgba(124, 5, 150, 0.3); cursor: pointer;\">AORUS FI27Q-P</span></font>,<font color=\"#7c0596\"><span style=\"border-bottom: 1px solid rgba(124, 5, 150, 0.3); outline-color: initial; outline-width: initial; border-top-color: rgba(124, 5, 150, 0.3); border-right-color: rgba(124, 5, 150, 0.3); border-left-color: rgba(124, 5, 150, 0.3); cursor: pointer;\">&nbsp;FI27Q</span></font>.&nbsp;<font color=\"#7c0596\"><span style=\"border-bottom: 1px solid rgba(124, 5, 150, 0.3); outline-color: initial; outline-width: initial; border-top-color: rgba(124, 5, 150, 0.3); border-right-color: rgba(124, 5, 150, 0.3); border-left-color: rgba(124, 5, 150, 0.3); cursor: pointer;\">KD25F</span></font>,&nbsp;<font color=\"#7c0596\"><span style=\"border-bottom: 1px solid rgba(124, 5, 150, 0.3); outline-color: initial; outline-width: initial; border-top-color: rgba(124, 5, 150, 0.3); border-right-color: rgba(124, 5, 150, 0.3); border-left-color: rgba(124, 5, 150, 0.3); cursor: pointer;\">CV27Q</span></font>,&nbsp;<font color=\"#7c0596\"><span style=\"border-bottom: 1px solid rgba(124, 5, 150, 0.3); outline-color: initial; outline-width: initial; border-top-color: rgba(124, 5, 150, 0.3); border-right-color: rgba(124, 5, 150, 0.3); border-left-color: rgba(124, 5, 150, 0.3); cursor: pointer;\">CV27F</span></font>.</li><li style=\"margin: 0px; padding: 0px;\">2.Зарегистрируйте покупку на официальном сайте: https://ru.aorus.com/event-detail.php?i=1613</li><li style=\"margin: 0px; padding: 0px;\">3.Получите промокод для ivi и Додо-пиццы.</li><li style=\"margin: 0px; padding: 0px;\">4.Соберитесь с друзьями и близкими за просмотром любимых фильмов.</li></ul><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\">Подробности акции: https://ru.aorus.com/event-detail.php?i=1613</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif;\"><i>Приятных покупок в Online-Shop!</i></p>")
                .postingDate(LocalDateTime.now().minusDays(10L))
                .build();

        News thirdNews = News.builder()
                .title("Сегодня стартует предзаказ на флагманские продукты Samsung!")
                .anons("Сделайте предзаказ и получите подарок.")
                .fullText("<h1><span style=\"font-family: &quot;PT Sans&quot;, Arial, sans-serif;\"><font color=\"#0000ff\">Хорошие новости в Ситилинк!</font></span></h1><h1><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif; font-size: 16px;\">Сегодня стартует предзаказ на новые флагманские продукты Samsung!<b></b></p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif; font-size: 16px;\"><b>Что это значит для вас?</b></p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif; font-size: 16px;\">Вы сможете оформить предзаказ с 5 по 20 августа и получить потрясающие подарки.</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif; font-size: 16px;\"><b>Какие продукты участвуют в предзаказе?</b></p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif; font-size: 16px;\">- Galaxy Note20, Note20&nbsp;Ultra</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif; font-size: 16px;\">- Galaxy Tab S7, S7+</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif; font-size: 16px;\">- Galaxy Watch 3</p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif; font-size: 16px;\"><b>Какие подарки вас ждут?</b></p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif; font-size: 16px;\"><span style=\"font-size: 18px;\">При покупке смартфона Galaxy Note20 вы получаете черные наушники Galaxy Buds+, а при покупке Galaxy Note20&nbsp;Ultra – бронзовые наушники Galaxy Buds Live.</span></p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif; font-size: 16px;\"><u>При покупке планшета Galaxy Tab S7 и S7+ в подарок стильный чехол-клавиатура.</u></p><p style=\"margin-right: 0px; margin-bottom: 1em; margin-left: 0px; padding: 0px; color: rgb(0, 0, 0); font-family: &quot;PT Sans&quot;, Arial, sans-serif; font-size: 16px;\"><i>При покупке часов Galaxy Watch 3 вам достается оригинальный кожаный ремешок.</i></p></h1>")
                .postingDate(LocalDateTime.now().minusDays(15L))
                .build();

        newsService.save(firstNews);
        newsService.save(secondNews);
        newsService.save(thirdNews);
    }
}
