$(document).ready(function () {
    getCurrentUser()
    /*Слушатель для кнопки смены email*/
    document.getElementById('buttonChangeMail').addEventListener('click', changeEmail)
    /*Слушатель для кнопки смены пароля*/
    document.getElementById('changePassword').addEventListener('click', changePassword)
    /*Слушатель для кнопки Сохранить(обновление профиля)*/
    document.getElementById('updateProfile').addEventListener("click", updateProfile)
});


/**
 * Функция смены email в профиле
 */
function changeEmail() {
    let newEmail = document.getElementById('new_mail').value
    fetch('/api/profile/changeEmail', {
        method: 'POST',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-type': 'application/json'
        },
        body: newEmail
    }).then(function (response) {
        if (response.ok) {
            toastr.success("Email успешно заменен.");
            $('#changeMailModal').modal('hide')
            document.location.href = "/logout";
        } else {
            toastr.error("Email занят.")
        }
    })
}

/**
 * Функция смены пароля в профиле
 */
function changePassword() {
    let oldPassword = document.getElementById('old_password').value
    let newPassword = document.getElementById('new_password').value
    fetch('/api/profile/changePassword', {
        method: 'POST',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-type': 'application/json'
        },
        body: JSON.stringify({
            oldPassword: oldPassword,
            newPassword: newPassword
        })
    }).then(function (response) {
        if (response.ok) {
            toastr.success("Пароль успешно изменен.");
            $('#changePasswordModal').modal('hide')
        } else {
            toastr.error("Некорректный пароль.")
        }
    })
}

/**
 * Функция для смены названия магазина
 */
$("#buttonNameStore").click(function () {
    let name = document.getElementById("nameStore").value
    $.ajax("/api/admin/editStoreName", {
        method: "put",
        data: {
            settingName: "store_name",
            textValue: name,
            status: false,
        },
        dataType: "text",
        success: function () {
            $('#changeStoreName').modal('hide')
        }
    })
})

/**
 * Функция получения текущего юзера и заполнение полей профиля
 */
function getCurrentUser() {
    fetch('/api/profile/currentUser')
        .then((res) => res.json())
        .then((currentUser) => {
            $('#id_update').val(currentUser.data.id);
            $('#password_update').val(currentUser.data.password);
            $('#first_name_update').val(currentUser.data.firstName);
            $('#last_name_input').val(currentUser.data.lastName);
            $('#email_input').val(currentUser.data.email);
            $("#date_birthday_input").val(currentUser.data.birthdayDate);
            $("#register_date").html(currentUser.data.registerDate);

            if (currentUser.data.userGender === "MAN") {
                $('#userGenderMan').prop('checked', true);
            } else if (currentUser.data.userGender === "WOMAN") {
                $('#userGenderWoman').prop('checked', true);
            } else {
                $('#userGenderNone').prop('checked', true);
            }
        })
}

/**
 * Функция обновления профиля
 */
function updateProfile() {
    let isChecked = ""
    if (document.getElementById('userGenderMan').checked) {
        isChecked = "MAN"
    } else if (document.getElementById('userGenderWoman').checked) {
        isChecked = "WOMAN"
    } else {
        isChecked = null
    }
    let userProfile = {
        id: $('#id_update').val(),
        password: $('#password_update').val(),
        firstName: $('#first_name_update').val(),
        lastName: $('#last_name_input').val(),
        email: $('#email_input').val(),
        birthdayDate: $('#date_birthday_input').val(),
        userGender: isChecked,
        registerDate: $('#register_date').val()
    }
    fetch('/api/profile/update', {
        method: 'PUT',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-type': 'application/json; charset=UTF-8'
        },
        body: JSON.stringify(userProfile)
    })
        .then(function (response) {
        if (response.status === 200) {
            getCurrentUser()
            toastr.success("Обновление профиля прошло успешно.");
        } else {
            toastr.error("Обновить профиль не удалось.");
        }
    })
}