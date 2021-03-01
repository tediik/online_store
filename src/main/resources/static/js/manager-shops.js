$(document).ready(renderAddressTable());

/**
 * Функция для рендера таблица адресов
 */
function renderAddressTable() {
    $("#table").empty();
    $.ajax({
        type: 'GET',
        url: '/api/manager/shops',
        success: function (data) {
            let info = data.data;
            $.each(info, function (i, address) {
                $("#table").append($('<tr>').append(
                    $('<td>').text(address.id),
                    $('<td>').text(address.region),
                    $('<td>').text(address.city),
                    $('<td>').text(address.street),
                    $('<td>').text(address.building),
                    $('<td>').append("<button type='button' data-toggle='modal' class='btn-info btn'" +
                        "data-target='#editAddressModal' data-user-id='" + address.id + "'>Изменить</button>"),
                    $('<td>').append("<button type='button' data-toggle='modal' class='btn btn-danger'" +
                        "data-target='#deleteAddressModal' data-user-id='" + address.id + "'>Удалить</button>")
                    )
                );
            });
        }
    });
}

/**
 * Заполнение модального окна редактирования
 */
$("#editAddressModal").on('show.bs.modal', (e) => {
    let userId = $(e.relatedTarget).data("user-id");

    $.ajax({
        url: "/api/manager/shops/" + userId,
        type: "GET",
        dataType: "json"
    }).done((msg) => {

        let address = msg.data;

        $("#idEdit").empty().val(address.id);
        $("#regionEdit").empty().val(address.region);
        $("#cityEdit").empty().val(address.city);
        $("#streetEdit").empty().val(address.street);
        $("#buildingEdit").empty().val(address.building);
    });
})

/**
 * Сохранение изменений в модальном окне
 */

$("#buttonEditSubmit").on('click', (e) => {
    e.preventDefault();

    let editAddress = {
        id: $("#idEdit").val(),
        region: $("#regionEdit").val(),
        city: $("#cityEdit").val(),
        street: $("#streetEdit").val(),
        building: $("#buildingEdit").val(),
    }

    $.ajax({
        url: "/api/manager/shops",
        type: "PUT",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: JSON.stringify(editAddress)
    }),

        $("#editAddressModal").modal('hide'),
        location.reload();
})

/**
 * Модальное окно для удаления адреса
 */
$("#deleteAddressModal").on('show.bs.modal', (e) => {
    let userId = $(e.relatedTarget).data("user-id");

    $.ajax({
        url: "/api/manager/shops/" + userId,
        type: "GET",
        dataType: "json"
    }).done((msg) => {
        let address = msg.data;

        $("#idDelete").empty().val(address.id);
        $("#regionDelete").empty().val(address.region);
        $("#cityDelete").empty().val(address.city);
        $("#streetDelete").empty().val(address.street);
        $("#buildingDelete").empty().val(address.building);

        $("#buttonDeleteSubmit").on('click', (e) => {
            e.preventDefault();

            $.ajax({
                url: "/api/manager/shops/" + userId,
                type: "DELETE",
                dataType: "text"
            }).done((deleteMsg) => {
                $("#deleteAddressModal").modal('hide');
                location.reload();
            })
        })
    });
})

/**
 * Добавление нового адреса
 */
$('[href="#newAddress"]').on('show.bs.tab', (e) => {
    $(() => {
        $("#regionNew").empty().val("");
        $("#cityNew").empty().val("");
        $("#streetNew").empty().val("");
        $("#buildingNew").empty().val("");
        $("#statusNew").empty().val("");
    })
})

$("#buttonInputNewSubmit").on('click', (e) => {
    e.preventDefault();

    let newAddress = {
        region: $("#regionNew").val(),
        city: $("#cityNew").val(),
        street: $("#streetNew").val(),
        building: $("#buildingNew").val(),
    }

    $.ajax({
        url: "/api/manager/shops",
        type: "POST",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: JSON.stringify(newAddress)
    }),
        renderAddressTable(),
        $('#AdminTabs a[href="#usersTable"]').tab('show'),
        location.reload();
})