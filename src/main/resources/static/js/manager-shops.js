$(document).ready(getAllAddress());

//Users Table
function getAllAddress() {
    $("#table").empty();
    $.ajax({
        type: 'GET',
        url: '/api/manager/shops/allShops',
        timeout: 3000,
        success: function (data) {
            console.log(data);
            $.each(data, function (i, address) {
                $("#table").append($('<tr>').append(
                    $('<td>').text(address.id),
                    $('<td>').text(address.region),
                    $('<td>').text(address.city),
                    $('<td>').text(address.street),
                    $('<td>').text(address.building),
                    $('<td>').text(address.status),
                    $('<td>').append("<button type='button' data-toggle='modal' class='btn-info btn'" +
                        "data-target='#editUserModal' data-user-id='" + address.id + "'>Изменить</button>"),
                    $('<td>').append("<button type='button' data-toggle='modal' class='btn btn-danger'" +
                        "data-target='#deleteUserModal' data-user-id='" + address.id + "'>Удалить</button>")
                    )
                );
            });
        }
    });

    $("#buttonInputNewSubmit").on('click', (e) => {
        e.preventDefault();

        let newUser = {
            username: $("#usernameNew").val(),
            firstname: $("#firstnameNew").val(),
            lastname: $("#lastnameNew").val(),
            age: $("#ageNew").val(),
            email: $("#emailNew").val(),
            password: $("#passwordNew").val(),
            roles: $("#rolesNew").val()
        }

        $.ajax({
            url: "/admin",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(newUser)
        }),
            getUsers(),
            $('#AdminTabs a[href="#usersTable"]').tab('show'),
            location.reload();
    })

    $('[href="#v-pills-user"]').on('show.bs.tab', (e) => {
        $("#change-tabContent").hide(),
            getCurrent();
    })

    $('[href="#v-pills-admin"]').on('show.bs.tab', (e) => {
        location.reload();
    })

//Edit form
    $("#editUserModal").on('show.bs.modal', (e) => {
        let userId = $(e.relatedTarget).data("user-id");

        $.ajax({
            url: "/api/manager/shops/" + userId,
            type: "GET",
            dataType: "json"
        }).done((msg) => {

            let address = JSON.parse(JSON.stringify(msg));

            $("#idEdit").empty().val(address.id);
            $("#usernameEdit").empty().val(address.region);
            $("#firstnameEdit").empty().val(user.firstname);
            $("#lastnameEdit").empty().val(user.lastname);
            $("#ageEdit").empty().val(user.age);
            $("#emailEdit").empty().val(user.email);
            $("#passwordEdit").empty().val(user.password);

        });
    })

// Edit Modal
    $("#buttonEditSubmit").on('click', (e) => {
        e.preventDefault();

        let editAddress = {
            id: $("#idEdit").val(),
            username: $("#usernameEdit").val(),
            firstname: $("#firstnameEdit").val(),
            lastname: $("#lastnameEdit").val(),
            age: $("#ageEdit").val(),
            email: $("#emailEdit").val(),
            password: $("#passwordEdit").val(),
            roles: $("#rolesEdit").val()
        }

        $.ajax({
            url: "/api/manager/shops",
            type: "PUT",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(editAddress)
        }),

            $("#editUserModal").modal('hide'),
            location.reload();
        getUsers();
    })

// Delete Modal
    $("#deleteUserModal").on('show.bs.modal', (e) => {
        let addressId = $(e.relatedTarget).data("user-id");

        $.ajax({
            url: "/api/manager/shops/" + addressId,
            type: "GET",
            dataType: "json"
        }).done((msg) => {
            let address = JSON.parse(JSON.stringify(msg));

            $("#idDelete").empty().val(addressId);
            $("#regionDelete").empty().val(address.region);
            $("#cityDelete").empty().val(address.city);
            $("#streetDelete").empty().val(address.street);
            $("#buildingDelete").empty().val(address.building);

            $("#buttonDeleteSubmit").on('click', (e) => {
                e.preventDefault();

                $.ajax({
                    url: "/api/manager/shops/" + addressId,
                    type: "DELETE",
                    dataType: "text"
                }).done((deleteMsg) => {
                    $("#deleteUserModal").modal('hide');
                    location.reload();
                })
            })
        });
    })

// New User
    $('[href="#newUser"]').on('show.bs.tab', (e) => {
        $(() => {
            $("#usernameNew").empty().val("");
            $("#firstnameNew").empty().val("");
            $("#lastnameNew").empty().val("");
            $("#ageNew").empty().val("");
            $("#emailNew").empty().val("");
            $("#passwordNew").empty().val("");
            $("#rolesNew").empty().val("");
            $.each(allRoles, (i, role) => {
                $("#rolesNew").append(
                    $("<option>").text(role)
                )
            });
        })
    })

    $("#buttonInputNewSubmit").on('click', (e) => {
        e.preventDefault();

        let newUser = {
            username: $("#usernameNew").val(),
            firstname: $("#firstnameNew").val(),
            lastname: $("#lastnameNew").val(),
            age: $("#ageNew").val(),
            email: $("#emailNew").val(),
            password: $("#passwordNew").val(),
            roles: $("#rolesNew").val()
        }

        $.ajax({
            url: "/api/manager/shops",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(newUser)
        }),
            getUsers(),
            $('#AdminTabs a[href="#usersTable"]').tab('show'),
            location.reload();
    })

    $('[href="#v-pills-user"]').on('show.bs.tab', (e) => {
        $("#change-tabContent").hide(),
            getCurrent();
    })


    /**
     * Функция рендера модального окна Edit user
     * @param user пользователь из таблицы
     * @param allRoles все роли из бд
     */
    function editUserModalWindowRender(adress) {
        $('.modal-dialog').off("click").on("click", "#acceptButton", handleUserAcceptButtonFromModalWindow)
        $('#rolesSelectModal').empty()
        $('#idInputModal').val(user.id)
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
    }
    function compareRolesId(userRoles, roleNameToCheck) {
        for (let i = 0; i < userRoles.length; i++) {
            if (userRoles[i].name === roleNameToCheck) {
                return true
            }
        }
    }

    /**
     * Функция обраотки нажатия кнопки Edit в таблице пользователей
     * @param event
     */
    function handleEditUserButton(event) {
        const addressId = event.target.dataset["userId"]
        Promise.all([
            fetch( "api/manager/allShops/" + addressId, {headers: headers}),
            fetch(roleRestUrl, {headers: headers})
        ])
            .then(([response1, response2]) => Promise.all([response1.json(), response2.json()]))
            .then(([userToEdit, allRoles]) => editUserModalWindowRender(userToEdit, allRoles))
    }

// User
    function getCurrent() {
        $.ajax({
            url: "/getUser",
            type: "GET",
            dataType: "json"
        }).done((msg) => {
            let user = JSON.parse(JSON.stringify(msg));
            $("#current-user-table tbody").empty().append(
                $("<tr>").append(
                    $("<td>").text(user.id),
                    $("<td>").text(user.username),
                    $("<td>").text(user.firstname),
                    $("<td>").text(user.lastname),
                    $("<td>").text(user.age),
                    $("<td>").text(user.email),
                    $("<td>").text(user.roles)
                ));
        }).fail(() => {
            alert("Error Get All Users!")
        })
    }
}