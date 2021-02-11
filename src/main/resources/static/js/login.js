/**
 * Блокировка кнопки авторизации пользователя через facebook, так как на данном этапе наше приложение не настроено
 * для авторизации через facebook(не были получены идентификатор(client-id) и секрет приложения(client-secret),
 * удалить при необходимости.
 */
document.getElementById('noLinkFacebook').addEventListener('click', function(e) {
    e.preventDefault();
}, false);

let myHeaders = new Headers();
myHeaders.append('Content-type', 'application/json; charset=UTF-8')

/**
 * Прослушиватель событий для ссылки "Восстановить"
 * алерта выпадающегоесли пытается залогниться кастомер который удалил свой профиль
 * но 30 дней еще не прошло
 */
document.getElementById('linkForOpenRestoreModal').addEventListener('click', showModalForRestore)

/**
 * функиця показывает модалку для восстановления профиля
 */
function showModalForRestore() {
    $('#askUserForRestore').modal('show')
}

/**
 * Прослушиватель событий для кнопки восстановления профиля
 */
document.getElementById('buttonRestore').addEventListener('click', functionRestore)

/**
 * Функция, которая обрабатывает кнопку восстановления профиля
 */
function functionRestore() {
    let email = document.getElementById('emailForRestore').value;
    let password = document.getElementById('passwordForRestore').value;
    let data = {email:email,password:password}
    fetch('/api/allUsers/restore', {
        method: 'PUT',
        headers: myHeaders,
        body: JSON.stringify(data)
    }).then(function (res) {
        if (res.status === 200) {
            alert("Ваш аккаунт был успешно восстановлен")
            document.location.href = "/login";
        } else {
            alert("Ваш аккаунт не восстановлен. Проверьте введеннные учетные данные")
        }
    })
}

/**
 * Метод делаеи запрос на рест контроллер при
 * нажатии на кнопку "Sign in with Odnoklassniki"
 */
document.getElementById('odnoklassnikiBtn').addEventListener('click',odnoklassnikiBtn)
function odnoklassnikiBtn() {
    fetch("/api/login/odnoklassniki").then(response => response.text()).
    then(url => document.location.href = url)
}

/**
 * Метод делаеи запрос на рест контроллер при
 * нажатии на кнопку "Sign in with VKontakte"
 */
document.getElementById('vkBtn').addEventListener('click',vkBtn)
function vkBtn() {
    fetch("/api/login/vkontakte").then(response => response.text()).
    then(url => document.location.href = url)
}

/**
 * Метод делаеи запрос на рест контроллер при
 * нажатии на кнопку "Sign in with Twitter"
 */
document.getElementById('twitterBtn').addEventListener('click',twitterBtn)
function twitterBtn() {
    //В данный момент не срабатывает, раскоментировать при необходимости!
    // fetch("/api/login/twitter").then(response => response.text())
    // then(url => document.location.href = url)
}

