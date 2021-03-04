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
            newPassword: newPassword})
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
$("#buttonNameStore").click(function (){
    let name = document.getElementById("nameStore").value
    $.ajax("/api/admin/editStoreName", {
        method: "put",
        data: {
            settingName:"store_name",
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
        .then(response => response.json())
        .then(response => response.data)
        .then(currentUser => {
            $('#id_update').val(currentUser.id);
            $('#password_update').val(currentUser.password);
            $('#first_name_update').val(currentUser.firstName);
            $('#last_name_input').val(currentUser.lastName);
            $('#email_input').val(currentUser.email);
            $("#date_birthday_input").val(currentUser.birthdayDate);
            $("#register_date").html(currentUser.registerDate);

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
 * @param event событие click по кнопке Соохранить
 */
function updateProfile(event) {
    let date = event.target.dataset.postDate
    let isChecked = null
    if(document.getElementById('userGenderNone').checked) {
         isChecked = null
    }
    if(document.getElementById('userGenderMan').checked) {
         isChecked = "MAN"
    }
    if(document.getElementById('userGenderWoman').checked) {
         isChecked = "WOMAN"
    }
    let userProfile = {
        id: document.getElementById('id_update').value,
        password: document.getElementById('password_update').value,
        firstName: document.getElementById('first_name_update').value,
        lastName: document.getElementById('last_name_input').value,
        email: document.getElementById('email_input').value,
        birthdayDate: document.getElementById('date_birthday_input').value,
        userGender: isChecked,
        registerDate: date
    }
    fetch('/api/profile/update', {
        method: 'PUT',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-type': 'application/json; charset=UTF-8'
        },
        body: JSON.stringify(userProfile)
    }).then(function (response) {
        if (response.status === 200) {
            getCurrentUser()
            toastr.success("Обновление профиля прошло успешно.");
        } else {
            toastr.error("Обновить профиль не удалось.");
        }
    })
}