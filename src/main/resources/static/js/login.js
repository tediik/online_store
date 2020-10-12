/**
 * Блокировка кнопки авторизации пользователя через facebook, так как на данном этапе наше приложение не настроено
 * для авторизации через facebook(не были получены идентификатор(client-id) и секрет приложения(client-secret),
 * удалить при необходимости.
 */
document.getElementById('noLinkFacebook').addEventListener('click', function(e) {
    e.preventDefault();
}, false);

/**
 * Метод делаеи запрос на рест контроллер при
 * нажатии на кнопку "Sign in with Odnoklassniki"
 */
document.getElementById('odnoklassnikiBtn').addEventListener('click',odnoklassnikiBtn)
function odnoklassnikiBtn() {
    fetch("/login/odnoklassniki").then(response => response.text()).
    then(url => document.location.href = url)
}

/**
 * Метод делаеи запрос на рест контроллер при
 * нажатии на кнопку "Sign in with VKontakte"
 */
document.getElementById('vkBtn').addEventListener('click',vkBtn)
function vkBtn() {
    fetch("/login/vkontakte").then(response => response.text()).
    then(url => document.location.href = url)
}

/**
 * Метод делаеи запрос на рест контроллер при
 * нажатии на кнопку "Sign in with Twitter"
 */
document.getElementById('twitterBtn').addEventListener('click',twitterBtn)
function twitterBtn() {
    //В данный момент не срабатывает, раскоментировать при необходимости!
    // fetch("/login/twitter").then(response => response.text())
    // then(url => document.location.href = url)
}

