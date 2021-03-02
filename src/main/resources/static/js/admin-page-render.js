let adminRestUrl = "/api/admin"
let roleRestUrl = "/api/roles"
let headers = new Headers()
headers.append('Content-type', 'application/json; charset=UTF-8')
document.getElementById('addBtn').addEventListener('click', handleAddBtn)

/*Слушатель для кнопки Подтвердить в Режиме техобслуживания*/
document.getElementById('maintenanceBtn').addEventListener('click', handleMaintenanceBtn)

addRolesOnNewUserForm()
addRolesOnMaintenanceMode()
fetchUsersAndRenderTable()
renderAcceptedRolesInMaintenance()

/**
 * Обработка события с выбором роли для фильтрации списка зарегистрированных пользователей по роли
 */
$('#filterRole').on("change", function () {
    var roleSelect = $('#filterRole').val();
    if (roleSelect !== 'default') {
        $.ajax({
            type: 'PUT',
            url: '/api/admin/' + roleSelect,
            success: function (filteredUsers) {
                renderUsersTable(filteredUsers)
            }
        });
    } else {
        fetchUsersAndRenderTable()
    }
});

/**
 * fetch запрос на roleRestUrl для получения всех ролей из бд
 *
 */
function addRolesOnNewUserForm() {
    fetch(roleRestUrl, {headers: headers}).then(response => response.json())
        .then(allRoles => renderRolesSelectOnNewUserForm(allRoles))
}

/**
 * рендерит <Select> c выбором ролей на странице добавления нового User'a
 * @param allRoles - принимается список всех ролей
 */
function renderRolesSelectOnNewUserForm(allRoles) {
    let selectRoles = $('#addRoles').empty()
    $.each(allRoles, function (i, role) {
        selectRoles.append(`<option value=${role.id}>${role.name}</option>>`)
    })
}

/**
 * Функция рендера модального окна Edit user
 * @param user пользователь из таблицы
 * @param allRoles все роли из бд
 */
function editUserModalWindowRender(user, allRoles) {
    console.log("Функция рендера модалки")
    $('.modal-dialog').off("click").on("click", "#acceptButton", handleUserAcceptButtonFromModalWindow)
    $('#rolesSelectModal').empty()
    $('#idInputModal').val(user.data.id)
    $('#acceptButton').text("Save changes").removeClass().toggleClass('btn btn-success edit-user')
    $('.modal-title').text("Edit user")
    $('#emailInputModal').val(user.data.email).prop('readonly', false)
    $('#firstNameInputModal').val(user.data.firstName).prop('readonly', false)
    $('#lastNameInputModal').val(user.data.lastName).prop('readonly', false)
    $('#passwordInputModal').val("").prop('readonly', false)
    $.each(allRoles, function (i, role) {
        if (compareRolesId(user.data.roles, role.name)) {
            $('#rolesSelectModal').append(`<option value=${role.id} selected="true">${role.name}</option>>`)
        } else {
            $('#rolesSelectModal').append(`<option value=${role.id}>${role.name}</option>>`)
        }
    })
    $('#flexSwitchCheckDefault').val(user.data.isAccountNonBlockedStatus).prop('checked', function () {
        return !user.data.isAccountNonBlockedStatus;
})
}

function compareRolesId(userRoles, roleNameToCheck) {
    for (let i = 0; i < userRoles.length; i++) {
        if (userRoles[i].name === roleNameToCheck) {
            return true
        }
    }
}

/**
 * Функция рендера модального окна Delete user
 * @param userToDelete
 */
function deleteUserModalWindowRender(userToDelete) {
    $('.modal-dialog').off("click").on("click", "#acceptButton", handleUserAcceptButtonFromModalWindow)
    $('#rolesSelectModal').empty()
    $('.modal-title').text("Delete user")
    $('#acceptButton').text("Delete").removeClass().toggleClass('btn btn-danger delete-user')
    $('#idInputModal').val(userToDelete.id)
    $('#emailInputModal').val(userToDelete.email).prop('readonly', true)
    $('#firstNameInputModal').val(userToDelete.firstName).prop('readonly', true)
    $('#lastNameInputModal').val(userToDelete.lastName).prop('readonly', true)
    $('#passwordInputModal').val("").prop('readonly', true)
    $.each(userToDelete.roles, function (i, role) {
        $('#rolesSelectModal').append(`<option value=${role.id} selected="true" disabled>${role.name}</option>>`)
    })

}

/**
 * Функция обработки нажатия кнопки Edit в таблице пользователей
 * @param event
 */
function handleEditUserButton(event) {
    const userId = event.target.dataset["userId"]
    Promise.all([
        fetch(adminRestUrl + "/users/" + userId, {headers: headers}),
        fetch(roleRestUrl, {headers: headers})
    ])
        .then(([response1, response2]) => Promise.all([response1.json(), response2.json()]))
        .then(([userToEdit, allRoles]) => editUserModalWindowRender(userToEdit, allRoles))
}

/**
 * Функция обработки нажатия кнопки Delete в таблице пользователей
 * @param event
 */
function handleDeleteUserButton(event) {
    const userId = event.target.dataset["userId"]
    fetch(adminRestUrl + "/users/" + userId, {headers: headers})
        .then(response => response.json())
        .then(userToDelete => deleteUserModalWindowRender(userToDelete))
}

/**
 * Функция делает активным таблицу с пользователями
 * и обновляет в ней данные
 */
function showAndRefreshHomeTab() {
    fetchUsersAndRenderTable()
    $('#nav-home').addClass('tab-pane fade active show')
    $('#nav-profile').removeClass('active show')
    $('#nav-profile-tab').removeClass('active')
    $('#nav-home-tab').addClass('active')
}

/**
 * Функция для очистки всех полей с формы
 * @param fieldIdForm - принимает id формы
 */
function clearFieldsForm(fieldIdForm) {
    document.getElementById(fieldIdForm).reset();
}

/**
 * функция обработки кнопки add на форме нового пользователя
 */
function handleAddBtn() {
    let user = {
        email: $('#addEmail').val(),
        password: $('#addPassword').val(),
        roles: getSelectValues(document.getElementById('addRoles'))
    }

    clearFieldsForm('addForm');
    addRolesOnNewUserForm();

    /**
     * обработка валидности полей формы, если поле пустое или невалидное, появляется предупреждение
     * и ставится фокус на это поле. Предупреждение автоматически закрывается через 5 сек
     * @param text - текст для вывода в алекрт
     * @param field - поле на каком установить фокус
     */
    function handleNotValidFormField(text, field) {
        $('#alert-div').empty().append(`
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
              <strong>${text}</strong>
              <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            `)
        $('#' + field).focus()

        window.setTimeout(function () {
            $('.alert').alert('close');
        }, 5000)
    }

    fetch(adminRestUrl, {
        method: 'POST',
        headers: {'Content-Type': 'application/json;charset=utf-8'},
        body: JSON.stringify(user)
    }).then(
        function (response) {
            let field;
            if (response.status !== 200) {
                response.text()
                    .then(
                        function (text) {
                            if (text === "notValidEmailError") {
                                field = "addEmail"
                                handleNotValidFormField("Вы ввели некоректный Email адрес!", field)
                            }
                            if (text === "duplicatedEmailError") {
                                field = "addEmail"
                                handleNotValidFormField("Такой email адрес уже существует", field)
                            }
                            if (text === "emptyPasswordError") {
                                field = "addPassword"
                                handleNotValidFormField("Заполните поле пароль", field)
                            }
                            if (text === "emptyRolesError") {
                                field = "addRoles"
                                handleNotValidFormField("Необходимо выбрать роль", field)
                            }
                            console.log(text)
                        })
            } else {
                response.text().then(function () {
                    showAndRefreshHomeTab();
                    clearFieldsForm('addForm');
                    addRolesOnNewUserForm();
                })
            }
        }
    )
}

/**
 * функция обработки нажатия кнопки accept в модальном окне
 * @param event
 */
function handleUserAcceptButtonFromModalWindow(event) {
    const user = {
        id: $('#idInputModal').val(),
        email: $('#emailInputModal').val(),
        firstName: $('#firstNameInputModal').val(),
        lastName: $('#lastNameInputModal').val(),
        password: $('#passwordInputModal').val(),
        roles: getSelectValues(document.getElementById("rolesSelectModal")),
        isAccountNonBlockedStatus: !document.getElementById('flexSwitchCheckDefault').checked
    };

    /**
     * обработка валидности полей формы, если поле пустое или невалидное, появляется предупреждение
     * и ставится фокус на это поле. Предупреждение автоматически закрывается через 5 сек
     * @param text - текст для вывода в алекрт
     * @param field - поле на каком установить фокус
     */
    function modalHandleNotValidFormField(text, field) {
        $('#alert-modal-div').empty().append(`
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
              <strong>${text}</strong>
              <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            `)
        $('#userModalWindow').scrollTop(0);
        $('#' + field).focus()

        window.setTimeout(function () {
            $('.alert').alert('close');
        }, 5000)
    }

    /**
     * Проверка кнопки delete или edit
     */
    if ($('#acceptButton').hasClass('delete-user')) {
        fetch(adminRestUrl + "/users/" + user.id, {headers: headers})
            .then(response => response.json())
            .then(userToDelete => {
                let hasCustomerRole = compareRolesId(userToDelete.roles, 'ROLE_CUSTOMER');
                if (hasCustomerRole === true) {
                    fetch("/api/customer/deleteProfile/" + user.id, {
                        headers: headers,
                        method: 'DELETE'
                    }).then(function (response) {
                        if (response.ok) {
                            fetchUsersAndRenderTable()
                            $('#userModalWindow').modal('hide')
                            toastr.success("Пользователь заблокирован");
                        } else {
                            modalHandleNotValidFormField("Не удается заблокировать пользователя")
                        }
                    })
                } else {
                    fetch(adminRestUrl + "/" + user.id, {
                        headers: headers,
                        method: 'DELETE'
                    }).then(function (response) {
                        if (response.ok) {
                            $('#tr-' + user.id).remove()
                            $('#userModalWindow').modal('hide')
                            toastr.success("Пользователь удален");
                        } else {
                            modalHandleNotValidFormField("Не удается удалить пользователя")
                        }
                    })
                }
            })
    } else {
        fetch(adminRestUrl, {
            method: 'PUT',
            headers: headers,
            body: JSON.stringify(user)
        }).then(function (response) {
            if (response.ok) {
                fetchUsersAndRenderTable()
                $('#userModalWindow').modal('hide')
            } else {
                response.text()
                    .then(
                        function (text) {
                            if (text === "notValidEmailError") {
                                field = "emailInputModal"
                                modalHandleNotValidFormField("Вы ввели некоректный Email адрес!", field)
                            }
                            if (text === "emptyRolesError") {
                                field = "rolesSelectModal"
                                modalHandleNotValidFormField("Необходимо выбрать роль", field)
                            }
                            if (text === "duplicatedEmailError") {
                                field = "addEmail"
                                modalHandleNotValidFormField("Такой email адрес уже существует", field)
                            }
                            console.log(text)
                        })
            }
        })
    }
}

/**
 * Функция возвращает массив выбранных ролей
 * @param select
 * @returns {[]}
 */
function getSelectValues(select) {
    const result = [];
    const options = select;
    let opt;

    for (let i = 0, iLen = options.length; i < iLen; i++) {
        opt = options[i];

        if (opt.selected) {
            result.push(opt.text);
        }
    }
    return result;
}

/**
 * функция рендера таблицы пользователей
 * @param users
 */
function renderUsersTable(users) {
    let table = $('#user-table')
    table.empty()
        .append(`<tr>
                <th>ID</th>
                <th>Email</th>
                <th>Roles</th>
                <th>Edit</th>
                <th>Delete</th>
              </tr>`)

    /**
     * Функция принимает объект roles,
     * Отдает String с именами ролей через разделенными "; "
     * @param userRoles
     * @returns {string}
     */
    function getUserRolesNames(userRoles) {
        if (!userRoles) {
             return '';
        }
        let rolesNames = '';
        for (let i = 0; i < userRoles.length; i++) {
            if (i === 0) {
                rolesNames = userRoles[i].name
            } else {
                rolesNames = rolesNames + '; ' + userRoles[i].name
            }
        }
        return rolesNames;
    }

    for (let i = 0; i < users.length; i++) {
        const user = users[i];
        let userRolesNames = getUserRolesNames(user.roles)
        let row = `
                <tr id="tr-${user.id}">
                    <td>${user.id}</td>
                    <td>${user.email}</td>
                    <td>${userRolesNames} </td>              
                    
                    <td>
            <!-- Buttons of the right column of main table-->
                        <button data-user-id="${user.id}" type="button" class="btn btn-success edit-button-user" data-toggle="modal" data-target="#userModalWindow">
                        Edit
                        </button>
                    </td>
                    <td>
                        <button data-user-id="${user.id}" type="button" class="btn btn-danger delete-button-user" data-toggle="modal" data-target="#userModalWindow">
                        Delete
                        </button>
                    </td>
                </tr>
                `;
        table.append(row)
    }
    $('.edit-button-user').click(handleEditUserButton)
    $('.delete-button-user').click(handleDeleteUserButton)
}

/**
 * функция делает fetch запрос на рест контроллер, преобразует полученный объект в json
 * и передает функции рендера таблицы renderUsersTable
 */
function fetchUsersAndRenderTable() {
    fetch(adminRestUrl + "/allUsers", {
        method: 'GET',
        headers: {
            'Content-type': 'application/json; charset=UTF-8'
        }
    }).then(response => response.json())
        .then(function(users) {
            let usersList = users.data;
            renderUsersTable(usersList);
        })
}

/**
 * fetch запрос на roleRestUrl для получения всех ролей из бд
 * и добавления их на страницу Режим Техобслуживания
 */
function addRolesOnMaintenanceMode() {
    fetch(roleRestUrl, {headers: headers}).then(response => response.json())
        .then(allRoles => renderRolesSelectOnMaintenanceMode(allRoles))
}

/**
 * рендерит <Select> c выбором ролей на странице Режим техобслуживания
 * @param allRoles - принимается список всех ролей
 */
function renderRolesSelectOnMaintenanceMode(allRoles) {
    let selectRoles = $('#rolesMode').empty()
    $.each(allRoles, function (i, role) {
        selectRoles.append(`<option value=${role.id}>${role.name}</option>>`)
    })
}

/**
 * функция обработки нажатия кнопки Подтвердить на странице Режим техобслуживания
 * функция делает fetch запрос с данным в базу данных common_setting, которые нужны
 * для фильтрации в MaintenanceFilter.java, затем выводит
 * toast, если техобслуживание включено и скрывыет, если выключено
 * @param event
 */
function handleMaintenanceBtn(event) {
    event.preventDefault();

    let roles = getSelectValues(document.getElementById('rolesMode')).toString()
    if (!roles.includes('ROLE_ADMIN')) {
        roles = roles.concat(',ROLE_ADMIN')
    }
    let urlOn = '/api/commonSettings'
    let text = $('#maintenance-mode').val()
    let commonSetting = {
        settingName: 'maintenance_mode',
        textValue: roles,
        status: text
    }
    fetch(urlOn, {
        method: 'PUT',
        headers: headers,
        body: JSON.stringify(commonSetting)
    }).then($('#maintenance-mode-access').empty()).then(renderAcceptedRolesInMaintenance)
    if (text === 'true') {
        $('.toast').toast('show')
    }
    if (text === 'false') {
        $('.toast').toast('hide')
    }
}

/**
 * функция заполняет "Кому открыт доступ" на страние Режим техобслуживания
 * выполняет fetch запрос на maintenance_mode для получения данных из дб настроек
 */
function renderAcceptedRolesInMaintenance() {
    let settingName = '/api/commonSettings/maintenance_mode'
    fetch(settingName, {
        method: 'GET',
        headers: headers
    }).then(response => response.json())
        .then(allRoles1 => f(allRoles1))

    function f(allRoles1) {
        let selectRoles = $('#maintenance-mode-access').empty()
        let selectMode = document.querySelector('#maintenance-mode').getElementsByTagName('option')
        let modeStatus = allRoles1.status
        let rolesArr = allRoles1.textValue.split(',')
        for (let i = 0; i < rolesArr.length; i++) {
            if (rolesArr[i])
                selectRoles.append(`<option value=${rolesArr[i]}>${rolesArr[i]}</option>>`)
        }

        if (modeStatus === false) {
            selectMode.namedItem('false').selected = true
        } else {
            selectMode.namedItem('true').selected = true
        }
    }
}
