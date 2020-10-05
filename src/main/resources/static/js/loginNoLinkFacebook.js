/**
 * Блокировка кнопки авторизации пользователя через facebook, так как на данном этапе наше приложение не настроено
 * для авторизации через facebook(не были получены идентификатор(client-id) и секрет приложения(client-secret),
 * удалить при необходимости.
 */
document.getElementById('noLinkFacebook').addEventListener('click', function(e) {
    e.preventDefault();
}, false);