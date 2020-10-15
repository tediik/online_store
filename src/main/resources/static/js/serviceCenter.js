$(document).ready(function () {
    document.getElementById('deleteProfileServiceWorker').addEventListener('click', deleteProfile)
    document.getElementById('buttonChangeMailServiceWorker').addEventListener('click', changeEmail)

});

/**
 * Функция удаления профиля работника сервиса
 *
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
