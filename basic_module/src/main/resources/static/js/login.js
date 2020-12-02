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
 * Прослушиватель событий для кнопки "Sing in"
 */
document.getElementById('checkUserStatus').addEventListener('click', checkUserStatus)

/**
 * Функция, которая обрабатывает кнопку "Sing in" перед входом на странице логина
 */
function checkUserStatus() {
    let email = document.getElementById('email').value;
    let password = document.getElementById('password').value;
    let param = {
        "email": email,
        "password": password
    };
    fetch('/users/checkEmail', {
        method: 'POST',
        headers: myHeaders,
        body: JSON.stringify(param)
    }).then(function (response) {
        if (response.status === 200) {
            document.getElementById("formLogin").submit();
        } else {
            $('#askUserForRestore').modal('show')
        }
    })
}

/**
 * Прослушиватель событий для кнопки восстановления профиля
 */
document.getElementById('buttonRestore').addEventListener('click', functionRestore)

/**
 * Функция, которая обрабатывает кнопку восстановления профиля
 */
function functionRestore() {
    let email = document.getElementById('email').value;
    fetch('/users/restore', {
        method: 'PUT',
        headers: myHeaders,
        body: email
    }).then(function (res) {
        if (res.status === 200) {
            toastr.success(res, {timeOut: 5000});
            document.getElementById("formLogin").submit();
        } else {
            toastr.error(res, {timeOut: 5000});
            document.location.href = "/login";
        }
    })
}

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

