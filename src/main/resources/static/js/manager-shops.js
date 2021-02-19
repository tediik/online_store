
fetchAddressesRenderTable()

/**
 * Функция для отображения списках всех адресов магазинов 2.0
 * @param address
 */
function renderAddressTable(addresses){
    let table = $('#address-table')
    table.empty()
        .append(`<tr>
                <th>ID</th>
                <th>Регион</th>
                <th>Город</th>
                <th>Улица</th>
                <th>Дом</th>
                <th>Статус</th>
                <th>Изменить</th>
                <th>Удалить</th>
              </tr>`)

    for (let i = 0; i < address.length; i++) {
        const address = addresses[i];
        let row = `
                <tr id="tr-${address.id}">
                    <td>${address.id}</td>
                    <td>${address.region}</td>
                    <td>${address.city} </td>
                    <td>${address.street} </td>  
                    <td>${address.building} </td>
                    <td>${address.shop} </td>                                
                    
                    <td>
            <!-- Buttons of the right column of main table-->
                        <button data-user-id="${address.id}" type="button" class="btn btn-success edit-button-user" data-toggle="modal" data-target="#userModalWindow">
                        Edit
                        </button>
                    </td>
                    <td>
                        <button data-user-id="${address.id}" type="button" class="btn btn-danger delete-button-user" data-toggle="modal" data-target="#userModalWindow">
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
 * Функция для fetch запроса
 */
function fetchAddressesRenderTable() {
    fetch("/api/manager/shops", {
        method: 'GET'
    }).then(response => response.json()).then(addresses => renderAddressTable(addresses))
}

/*/!**
 * Функция для отображения списка всех магазинов
 *!/
function getAllAddress() {
    $("#table").empty();
    $.ajax({
        type: 'GET',
        url: '/api/manager/shops',
        timeout: 3000,
        success: function (data) {
            console.log(data);
            $.each(data, function (index, address) {
                $("#table").append($('<tr>').append(
                    $('<td>').text(address.id),
                    $('<td>').text(address.region),
                    $('<td>').text(address.city),
                    $('<td>').text(address.street),
                    $('<td>').text(address.building),
                    $('<td>').text(address.shop),
                    $('<td>').append("<button type='button' data-toggle='modal' class='btn-info btn'" +
                        "data-target='#editAddressModal' data-address-id='" + address.id + "'>Изменить</button>"),
                    $('<td>').append("<button type='button' data-toggle='modal' class='btn btn-danger'" +
                        "data-target='#deleteUserModal' data-address-id='" + address.id + "'>Удалить</button>")
                    )
                );
            });
        }
    });
}

/!**
 * Заполнение модального окна с изменением адреса
 *!/
$("#editAddressModal").on('show.bs.modal', (e) => {
    let addressId = $(e.relatedTarget).data("address-id");

    $.ajax({
        url: "/api/manager/shops/" + addressId,
        type: "GET",
        dataType: "json"
    }).done((msg) => {
        let address = JSON.parse(JSON.stringify(msg));

        $("#idEdit").empty().val(address.id);
        $("#regionEdit").empty().val(address.region);
        $("#cityEdit").empty().val(address.city);
        $("#streetEdit").empty().val(address.street);
        $("#buildingEdit").empty().val(address.building);
        $("#statusEdit").empty().val(address.shop);
    })
})

/!**
 * Сохранение изменений в модальном окне редактирования адреса
 *!/
$("#buttonEditSubmit").on('click', (e) => {
    e.preventDefault();

    let editAddress = {
        id: $("#idEdit").val(),
        region: $("#regionEdit").val(),
        city: $("#cityEdit").val(),
        street: $("#streetEdit").val(),
        building: $("#buildingEdit").val(),
        status: $("#statusEdit").val()
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
    getAllAddress();
})

/!**
 * Модальное окно удаления адреса
 *!/
$("#deleteUserModal").on('show.bs.modal', (e) => {
    let addressId = $(e.relatedTarget).data("address-id");

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
        $("#statusDelete").empty().val(address.shop);

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
})*/
