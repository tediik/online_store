$(document).ready(function () {
    userData();
    /*Слушатель для кнопки смены email modal*/
    $(document).delegate("#buttonChangeMail", "click", changeMail);
    /*Слушатель для кнопки смены пароля modal*/
    $(document).delegate("#submitNewPassword", "click", changePass);
    /*Слушатель для ввода пароля modal*/
    $(document).delegate("#new_password", "keyup", checkPassword);
});

/**
 * Method for customer's pass changing
 * @returns {boolean}
 */
function changePass() {
    fetch('/api/profile/changePassword', {
        method: 'POST',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-type': 'application/json'
        },
        body: JSON.stringify({
            oldPassword: $('#old_password').val(),
            newPassword: $('#new_password').val()
        })
    })
        .then(function (response) {
            if (response.ok) {
                $('#openChangePassModal').modal('hide')
                userData()
            } else {
                alert("Не удалось изменить пароль")
            }
        })
}

/**
 * Method for changing customer's mail
 */
function changeMail() {
    let newEmail = $('#new_mail').val();
    fetch('/api/profile/changeEmail', {
        method: 'POST',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-type': 'application/json'
        },
        body: newEmail
    })
        .then(function (response) {
            if (response.ok) {
                $('#openNewMailModal').hide()
                document.location.href = "/logout"
            } else {
                alert("Не удалось изменить адрес электронной почты")
            }
        })
}


//ToDo вроде есть класс ютил для проверки паролей у всех , можно прикручивать в контролллере или сервисе ... разобраться
/**
 * Method for checking that new customer's pass is match requirements
 */
function checkPassword() {
    regularExpression = new RegExp("(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9@#$%]).{8,}");
    newPassword = document.getElementById("new_password").value;
    if (regularExpression.test(newPassword)) {
        $("#submitNewPassword").attr('disabled', false);
    }
}

function userData() {
    fetch("/api/profile/currentUser")
        .then(response => response.json())
        .then((user) => {
            $('#id_update').val(user.data.id);
            $('#password_update').val(user.data.password);
            $('#first_name_update').val(user.data.firstName);
            $('#last_name_input').val(user.data.lastName);
            $('#email_input').val(user.data.email);
            $('#date_birthday_input').val(user.data.birthdayDate);
            $('#register_date').html(user.data.registerDate);
            if (user.data.userGender === null) {
                $('#userGenderNone').prop('checked', true);
            }
            if (user.userGender === "MAN") {
                $('#userGenderMan').prop('checked', true);
            }
            if (user.userGender === "WOMAN") {
                $('#userGenderWoman').prop('checked', true);
            }
        })
}

function updateUser() {
    let gender
    if (document.getElementById('userGenderMan').checked) {
        gender = "MAN"
    } else if (document.getElementById('userGenderWoman').checked) {
        gender = "WOMAN"
    } else {
        gender = null
    }

    let userForUpdate = {
        id: $("#id_update").val(),
        password: $('#password_update').val(),
        firstName: $('#first_name_update').val(),
        lastName: $('#last_name_input').val(),
        email: $('#email_input').val(),
        birthdayDate: $('#date_birthday_input').val(),
        userGender: gender,
        registerDate: $('#register_date').val()
    }
    fetch('/api/profile/update', {
        method: 'PUT',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-type': 'application/json; charset=UTF-8'
        },
        body: JSON.stringify(userForUpdate)
    })
        .then(() => userData())
        .catch(error => console.log("Не удалось сохранить изменения " + error))
}