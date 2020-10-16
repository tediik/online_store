$(document).ready(function () {
    /*Слушатель для ссылки удаления профиля сервисного работника*/
    document.getElementById('deleteProfileServiceWorker').addEventListener('click', deleteProfile)
    /*Слушатель для кнопки смены email сервисного работника*/
    document.getElementById('buttonChangeMailServiceWorker').addEventListener('click', changeEmail)
    /*Слушатель для кнопки смены пароля сервисного работника*/
    document.getElementById('changePasswordServiceWorker').addEventListener('click', changePassword)
});

/**
 * Функция удаления профиля работника сервиса
 * @param event событие click
 */
function deleteProfile(event) {
    let id = event.target.dataset.delId
    fetch(`/service/deleteProfile/${id}`, {
        method: 'DELETE',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-type': 'application/json'
        }
    }).then(function (response) {
        if (response.ok) {
            document.location.href = "/logout";
        } else {
            toastr.error('Ваш профиль не был удален.', {timeOut: 3000});
        }
    })
}

/**
 * Функция смены email в профиле работника сервиса
 */
function changeEmail() {
    let newEmail = document.getElementById('new_mail').value
    fetch('/service/changeEmail', {
        method: 'POST',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-type': 'application/json'
        },
        body: newEmail
    }).then(function (response) {
        if (response.ok) {
            toastr.success("Email успешно заменен.");
            $('#openNewMailModalServiceWorker').modal('hide')
            document.location.href = "/logout";
        } else {
            toastr.error("Email занят.")
        }
    })
}

/**
 * Функция смены пароля в профиле работника сервиса
 */
function changePassword() {
    let oldPassword = document.getElementById('old_password').value
    let newPassword = document.getElementById('new_password').value
    fetch('/service/changePassword', {
        method: 'POST',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-type': 'application/json'
        },
        body: JSON.stringify({
            oldPassword: oldPassword,
            newPassword: newPassword})
    }).then(function (response) {
        if (response.ok) {
            toastr.success("Пароль успешно изменен.");
            $('#openChangePassModalServiceWorker').modal('hide')
        } else {
            toastr.error("Некорректный пароль.")
        }
    })
}