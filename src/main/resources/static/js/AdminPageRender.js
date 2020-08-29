let adminRestUrl = "http://localhost:9999/api/admin"
let roleRestUrl = "http://localhost:9999/api/roles"
let headers = new Headers()
headers.append('Content-type', 'application/json; charset=UTF-8')

fetchUsersAndRenderTable()

/**
 * Функция рендера модального окна Edit user
 * @param user пользователь из таблицы
 * @param allRoles все роли из бд
 */
function editModalWindowRender(user, allRoles) {
    $('.modal-dialog').off("click").on("click", "#acceptButton", handleAcceptButtonFromModalWindow)
    $('#idInputModal').val(user.id)
    $('#rolesSelectModal').empty()
    $('#acceptButton').text("Save changes").removeClass().toggleClass('btn btn-success edit-user')
    $('.modal-title').text("Edit user")
    $('#emailInputModal').val(user.email).prop('readonly', false)
    $('#firstNameInputModal').val(user.firstName).prop('readonly', false)
    $('#lastNameInputModal').val(user.lastName).prop('readonly', false)
    $('#passwordInputModal').val("").prop('readonly', false)
    $.each(allRoles, function (i, role) {
        if (compareRolesId(user.roles, role.name)) {
            $('#rolesSelectModal').append(`<option value=${role.id} selected="true">${role.name}</option>>`)
        } else {
            $('#rolesSelectModal').append(`<option value=${role.id}>${role.name}</option>>`)
        }
    })

    function compareRolesId(userRoles, roleNameToCheck) {
        for (let i = 0; i < userRoles.length; i++) {
            if (userRoles[i].name === roleNameToCheck) {
                return true
            }
        }
    }
}

/**
 * Функция обраотки нажатия кнопки Edit в таблице пользователей
 * @param event
 */
function handleEditButton(event) {
    const userId = event.target.dataset["userId"]
    Promise.all([
        fetch(adminRestUrl + "/users/" + userId, {headers: headers}),
        fetch(roleRestUrl, {headers: headers})
    ])
        .then(([response1, response2]) => Promise.all([response1.json(), response2.json()]))
        .then(([userToEdit, allRoles]) => editModalWindowRender(userToEdit, allRoles))

}

/**
 * Функция рендера модального окна Delete user
 * @param userToDelete
 */
function deleteModalWindowRender(userToDelete) {
    $('.modal-dialog').off("click").on("click", "#acceptButton", handleAcceptButtonFromModalWindow)
    $('#rolesSelectModal').empty()
    $('.modal-title').text("Delete user")
    $('#acceptButton').text("Delete").removeClass().toggleClass('btn btn-danger delete-user')
    $('#idInputModal').val(userToDelete.id)
    $('#emailInputModal').val(userToDelete.email).prop('readonly', true)
    $('#firstNameInputModal').val(userToDelete.firstName).prop('readonly', true)
    $('#lastNameInputModal').val(userToDelete.lastName).prop('readonly', true)
    $('#passwordInputModal').val("").prop('readonly', true)
    $.each(userToDelete.roles, function (i, role) {
        $('#rolesSelectModal').append(`<option disabled>${role.name}</option>>`)
    })
}

/**
 * Функция обработки нажатия кнопки Delete в таблице пользователей
 * @param event
 */
function handleDeleteButton(event) {
const userId = event.target.dataset["userId"]
    fetch(adminRestUrl + "/users/" + userId, {headers: headers})
        .then(response => response.json())
        .then(userToDelete => deleteModalWindowRender(userToDelete))
}

function handleAcceptButtonFromModalWindow(event) {
    const user = {
        id: $('#idInputModal').val(),
        email: $('#emailInputModal').val(),
        firstName: $('#firstNameInputModal').val(),
        lastName: $('#lastNameInputModal').val(),
        password: $('#passwordInputModal').val(),
        roles: getSelectValues(document.getElementById("rolesSelectModal")),
    };

    /**
     * Проверка кнопки delete или edit
     */
    if ($('#acceptButton').hasClass('delete-user')) {
        fetch(adminRestUrl + "/" + user.id, {
            headers: headers,
            method: 'DELETE'
        }).then(response => response.text())
            .then(deletedUser => console.log('User: ' + deletedUser + ' was successfully deleted'))
            .then($('#tr-' + user.id).remove())
        $('#userModalWindow').modal('hide')
    } else {
        fetch(adminRestUrl, {
            method: 'PUT',
            headers: headers,
            body: JSON.stringify(user)
        }).then(response => response.json())
            .then(editedUser => console.log('User: ' + editedUser + ' was modifies'))
            .then(() => $('#tr-' + user.id).remove())
        $('#userModalWindow').modal('hide')
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
    table.on('click', '.edit-button', handleEditButton)
    table.on('click', '.delete-button', handleDeleteButton)

    /**
     * Функция принимает объект roles,
     * Отдает String с именами ролей через разделенными "; "
     * @param userRoles
     * @returns {string}
     */
    function getUserRolesNames(userRoles) {
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
                        <button data-user-id="${user.id}" type="button" class="btn btn-success edit-button" data-toggle="modal" data-target="#userModalWindow">
                        Edit
                        </button>
                    </td>
                    <td>
                        <button data-user-id="${user.id}" type="button" class="btn btn-danger delete-button" data-toggle="modal" data-target="#userModalWindow">
                        Delete
                        </button>
                    </td>
                </tr>
                `;
        table.append(row)
    }
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
    }).then(response => response.json()).then(users => renderUsersTable(users))
}

//TODO доделать
function renderNewUserForm() {
    let newUserForm = $('#new-user-form')
    newUserForm.empty().append(`
        <div class="panel-body">
        <div id="add-email-form-group" class="form-group">
            <label for="addEmail">Email:</label>
            <input type="text" id="addEmail" name="email" placeholder="E-mail" class="form-control">
        </div>
        <div class="form-group">
            <label for="addPassword">Password:</label>
            <input type="password" id="addPassword" name="password" placeholder="Password" class="form-control">
        </div>
        <div class="form-group">
            <label>Role</label>

            <select id="addRoles" name="roles" class="form-control" multiple="multiple">

            </select>
        </div>
        <button id="addBtn" type="button" class=" btn btn-success btn-md btn">Add</button>
    </div>
        `)
}