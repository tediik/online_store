async function fillFavouritesGoods() {
    let response = await fetch("/customer/favouritesGoods");
    let content = await response.json();
    let favoriteGoodsJson = document.getElementById('favouritesGoodsList');
    let key
    $(favoriteGoodsJson).empty();
    for (key in content) {
        let product = `
        <tr class=${content[key].id} id=${content[key].id}>
    <td>${content[key].id}</td>
    <td><input class="select_product hidden" type="checkbox" value="" id="${content[key].id}" ></td>
    <td>${content[key].product}</td>
    <td>${content[key].price}</td>
    <td>${content[key].amount}</td>
    <td>
       <button class="btn btn-danger" onclick="deleteProductFromFavouritGoods(${content[key].id})">Удалить</button>
    </td>
    <td>
       <button class="btn btn-primary" onclick="addProductToBasket(${content[key].id})">Добавить в корзину</button>
    </td>
    <tr>
`;
        $(favoriteGoodsJson).append(product);
    }
}

async function deleteProductFromFavouritGoods(id) {
    let idGroup = $('.get-favourites-group-btn').attr("id");
    if (idGroup == 1) {
        await fetch("/customer/favouritesGoods", {
            method: "DELETE",
            body: id,
            headers: {"Content-Type": "application/json; charset=utf-8"}
        });
    } else {
        await fetch("/customer/deleteProductFromFavouritesGroup/" + idGroup, {
            method: "DELETE",
            body: id,
            headers: {"Content-Type": "application/json; charset=utf-8"}
        });
    }
    fillFavouritesProducts(idGroup);
}

/**
 * Добавление Товара в корзину
 * @param id
 * @returns {Promise<void>}
 */
async function addProductToBasket(id) {
    await fetch(`/api/basket/add/${id}`, {
        method: "PUT",
        headers: {"Content-Type": "application/json; charset=utf-8"}
    });
    fillBasket();
}

$(document).on("click", "#showBasket", function () {
    $('#v-pills-tab a[href="#basketGoods"]').tab('show')
});
// Новый функционал
/**
 * Обработка события нажатия на кнопку "Копировать"
 * Собираем данные и перемещаем продукты в нужный список
 */
$(document).on("click", ".btn-copy-product", function () {
    let listIdProducts = $(".select_product:checked").map(function () {
        return this.id;
    }).get();
    let idGroup = $(".check-favourites-group:checked").attr("id");
    if (idGroup) {
        let idOldGroup = $(".get-favourites-group-btn").attr("id");
        moveProductsFavouritesGroup(idGroup, idOldGroup, listIdProducts);
        toastr.success("Успешное перемещение!");
    } else {
        toastr.error("Выбирите список!");
    }
    defaultInterface();
    $('.close').trigger('click');
});
/**
 * Закрываем модулку
 */
$(document).on("click", ".modal-footer button", function (){
    defaultInterface();
    cancelNevGroup();
})
$(document).on("click", 'button[class="close"]', function (){
    defaultInterface();
    cancelNevGroup();
})

/**
 * Закрыть модалку выбора/создания списка избранных товаров
 */
function closeModalFavouritesList() {
    $("#exampleModal").removeClass("show");
    $("body").removeClass("modal-open");
    defaultBtnGroup ();
    defaultInterface();
    cancelNevGroup();
}
/**
 * Нажатие на чекбоксы для выбора списка в который будем перемещать продукты
 */
$(document).on("click", ".check-favourites-group", function () {
        let checkNewGroup = $(".check-favourites-group:checked").map(function () {
            return this.id;
        }).get();
        let checkOldGroup = $(".select_product:checked").map(function () {
            return this.id;
        }).get();
        if ((checkNewGroup.length > 0) && (checkOldGroup.length > 0)) {
            $(".btn-copy-product").attr("disabled", false);
        } else {
            $(".btn-copy-product").attr("disabled", true);
        }
    }
);

/**
 * Делаем стили по умолчанию(как будто только зашли в ИЗБРАННОЕ)
 */
function defaultInterface(){
    $(".favouritesgroups").removeClass("selected");
    $("#addInOverGroup").removeClass("hidden");
    $("#escape").addClass("hidden");
 //   if ($('.ch_select_all').is(':checked')){
        $('.ch_select_all').prop('checked', false); // убираем галочку с "Выделить все"
        $(".select_product:checked").prop('checked', false); // убираем галочки с выбранных продуктов
  //  }
    $(".check-favourites-group").prop('checked', false); // убираем галочки с выбираемых списков
    $(".ch_select_all").addClass("hidden");
    $(".select_product ").addClass("hidden");
    $("#clear_group").removeClass("hidden");
    // $("#rename_group").removeClass("hidden");
    $("#archive_group").removeClass("hidden");
    $("#sel_new_group").addClass("hidden");
    $(".get-favourites-group-btn").attr("disabled", false);
}

/**
 * Достаем продукты заданного списка
 * @param id списка
 * @returns {Promise<void>}
 */
async function fillFavouritesProducts(id) {
    let response = await fetch("/customer/getProductFromFavouritesGroup/" + id);
    let content = await response.json();
    let favoriteGoodsJson = document.getElementById('favouritesGoodsList');
    let key
    $(favoriteGoodsJson).empty();
    for (key in content) {
        let product = `
        <tr class="product-in-group" id=${content[key].id}>
    <td>${content[key].id}</td>
    <td><input class="select_product hidden" type="checkbox" value="" id="${content[key].id}" ></td>
    <td>${content[key].product}</td>
    <td>${content[key].price}</td>
    <td>${content[key].amount}</td>
    <td>
       <button class="btn btn-danger" onclick="deleteProductFromFavouritGoods(${content[key].id})">Удалить</button>
    </td>
    <td>
       <button class="btn btn-primary" onclick="addProductToBasket(${content[key].id})">Добавить в корзину</button>
    </td>
    </tr>
`;
        $(favoriteGoodsJson).append(product);
    }
}

/**
 * Переход во вкладку "Избранное" - рисуем таблицу Списка "Все товары"
 */
$(document).on("click", "#favouritesGoods-tab", function () {
    let id = $('.dropdown-item:first').attr("id");
    fillFavouritesProducts(id);
});
/**
 * Нажымием на кнопку "Выбрать группу"
 */
$(document).on("click", "#sel_new_group", function () {
    $(".get-favourites-group-btn").attr("disabled", false);
    $(".btn-copy-product").attr("disabled", true);

});
/**
 * Обработчик кнопки "Добавить товары в другой список"
 */
$(document).on("click", "#addInOverGroup", function (event) {
    let activeAddInOverGroup = $(".favouritesgroups");
    if (!activeAddInOverGroup.hasClass("selected")) {
        activeAddInOverGroup.addClass("selected");
        $("#addInOverGroup").addClass("hidden");
        $("#escape").removeClass("hidden");
        $(".ch_select_all").removeClass("hidden");
        $(".select_product").removeClass("hidden");
        $("#clear_group").addClass("hidden");
        //$("#rename_group").addClass("hidden");
        $("#archive_group").addClass("hidden");
        $("#sel_new_group").removeClass("hidden");
        $(".get-favourites-group-btn").attr("disabled", true);
    }
});
/**
 * Обработчик кнопки Отмена (Появляется после нажатия на "Добавить товары в другой список")
 */
$(document).on("click", "#escape", function () {   // get-favourites-group-btn
    //let activeAddInOverGroup = $(".favouritesgroups");
    //if (activeAddInOverGroup.hasClass("selected")) {
        defaultInterface();
        // activeAddInOverGroup.removeClass("selected");
        // $("#addInOverGroup").removeClass("hidden");
        // $("#escape").addClass("hidden");
        // $(".ch_select_all").addClass("hidden");
        // $(".select_product ").addClass("hidden");
        // $("#clear_group").removeClass("hidden");
        // // $("#rename_group").removeClass("hidden");
        // $("#archive_group").removeClass("hidden");
        // $("#sel_new_group").addClass("hidden");
        // $(".get-favourites-group-btn").attr("disabled", false);
   // }
});
/**
 * Чекбокс "Выбрать все"
 */
$(document).on("click", "#ch_sel_all", function () {
    if ($(this).is(':checked')) {
        $('.select_product').prop('checked', true);
    } else {
        $('.select_product').prop('checked', false);
    }
});
/**
 * Нажимаем на выбранной группе для отображения списка товаров
 */
$(document).on("click", ".btn-group .dropdown-item", function () {
    $(".get-favourites-group-btn").text($(this).text());
    $(".get-favourites-group-btn").attr("id", this.id);
    fillFavouritesProducts(this.id);

});

/**
 * Перерисовываем обновленный список избранных товаров после переноса
 * продукта в другой список
 */
function defaultBtnGroup() {
    let id = $(".get-favourites-group-btn").attr("id");
    let text = $(".btn-group .dropdown-item[id='" + id + "']").text();
    $(".get-favourites-group-btn").text(text);
    fillFavouritesProducts(id);
}
/**
 * Кнопка "создать новый список"  в модалке работы со списками избранных
 */
$(document).on("click", ".add-group-new", function () {
    $(".polya-input").removeClass("hidden");
    $(".add-group-new").addClass("hidden");
    $(".new-group-checkbox").prop('checked', true);
    $(".new-group-checkbox").prop("disabled", true);
});
/**
 * Кнопка "Очистить список"
 */
$(document).on("click", "#clear_group", function () {
    let idGroup = $(".get-favourites-group-btn").attr("id");
    let listIdProducts = $(".product-in-group").map(function () {
        return this.id;
    }).get();
    if ((idGroup != undefined) && (listIdProducts.length != "")) {
        clearFavouritGroup(idGroup, listIdProducts);
    }

});

/**
 * Метод Очистки списка   "Очистить список"
 * @param idGroup
 * @param listIdProducts
 * @returns {Promise<void>}
 */
async function clearFavouritGroup(idGroup, listIdProducts) {
    const headers = {
        'Content-type': 'application/json; charset=UTF-8'
    };
    await fetch(`/customer/clearFavouritesGroup/` + idGroup, {
        method: 'DELETE',
        body: JSON.stringify(listIdProducts),
        headers: headers
    }).then(respons => {
        return respons;
    });
    defaultBtnGroup();
}
/**
 * Обработчик нажатия на кнопку EDIT в выборе списка избранного
 */
$(document).on("click", ".button-edit-fav-group", function () {
    let thisId = $(this).attr("id");
    $(".ok-cancel-edit-fav-group[id='" + thisId + "']").removeClass("hidden");
    $(this).addClass("hidden");
    $(".select-group-tr-input[id='" + thisId + "']").prop("disabled", false);
});
/**
 * Обработчик нажатия на кнопку DELETE в выборе списка избранного
 */
$(document).on("click", ".button-delete-fav-group", function () {
    let thisId = $(this).attr("id");
    let noDeleteGroup = $(".select-group-tr-input:first").val();
    if ($(".select-group-tr-input[id='" + thisId + "']").val() != noDeleteGroup) {
        deleteFavouritesGroupInBD(thisId);
        toastr.success("Cписок удален!");
        $(".select-group-tr[id='" + thisId + "']").detach();
        $(".dropdown-menu [id='" + thisId + "']").detach();

    } else {
        toastr.error("Удаление невозможно, основной список!");
    }
});
/**
 * Обработчик нажатия на кнопку V(Подтвердить редактирование) в выборе списка избранного
 */
$(document).on("click", ".edit-fav-group-ok", function () {
    let thisId = $(this).attr("id");
    let newNameFavouritesGroup = $(".select-group-tr-input[id='" + thisId + "']").val();
    $(".ok-cancel-edit-fav-group[id='" + thisId + "']").addClass("hidden");
    $(".button-edit-fav-group[id='" + thisId + "']").removeClass("hidden");
    updateFavouritesGroupInBD(newNameFavouritesGroup, thisId);
    //$(".select-group-tr-input[id='" + thisId + "']").val($(".dropdown-item[id='" + thisId + "']").text());
    $(".select-group-tr-input[id='" + thisId + "']").prop("disabled", true);
});
/**
 * Обработчик нажатия на кнопку Х(Отмена редактирования) в выборе списка избранного
 */
$(document).on("click", ".edit-fav-group-cancel", function () {
    let thisId = $(this).attr("id");
    $(".ok-cancel-edit-fav-group[id='" + thisId + "']").addClass("hidden");
    $(".button-edit-fav-group[id='" + thisId + "']").removeClass("hidden");
    $(".select-group-tr-input[id='" + thisId + "']").val($(".dropdown-item[id='" + thisId + "']").text());
    $(".select-group-tr-input[id='" + thisId + "']").prop("disabled", true);
});

/**
 * Кнопочка подтверждения создания нового списка  "V"
 */
$(document).on("click", ".add-group-input-ok", function () {
    let newGroupName = $(".new-group-input").val();
    let isPresent = $(".select-group-tr-input[value='" + newGroupName + "']").length;
    if (!isPresent) {
        addFavouritesGroupInBD(newGroupName);
        toastr.success("Cписок успешно создан!");
    } else {
        toastr.error("Такой список уже существует!");
        $(".polya-input").addClass("hidden");
        $(".add-group-new").removeClass("hidden");
        $(".new-group-checkbox").prop('checked', false);
        $(".new-group-checkbox").prop("disabled", false);
    }
})
/**
 * Кнопочка отмены от создания нового списка  "Х"
 */
$(document).on("click", ".add-group-input-cancel", function () {
    cancelNevGroup();
});
/**
 * Функция к кнопке отмены от создания нового списка
 */
function cancelNevGroup(){
    $(".polya-input").addClass("hidden");
    $(".add-group-new").removeClass("hidden");
    $(".new-group-checkbox").prop('checked', false);
    $(".new-group-checkbox").prop("disabled", false);
}

/**
 * Обновление название списка в БД
 * @param nameGroup   Новое название списка
 * @returns {Promise<void>}
 */
async function updateFavouritesGroupInBD(nameGroup, idGroup) {
    let id = idGroup;
    const headers = {
        'Content-type': 'application/json; charset=UTF-8'
    };
    await fetch(`/customer/favouritesGroup/` + id, {
        method: 'PUT',
        body: nameGroup,
        headers: headers
    }).then(respons => {
        return respons;
    });
};

/**
 * Функция перемещения товаров из одного списка ИЗБРАННОГО в другой
 * @param idNewGroup     id списка В который перемещаем
 * @param idOldGroup     id списка ИЗ которого перемещаем
 * @param idProducts     Массив товаров для перемещения
 * @returns {Promise<void>}
 */
async function moveProductsFavouritesGroup(idNewGroup, idOldGroup, idProducts) {
    const headers = {
        'Content-type': 'application/json; charset=UTF-8'
    };

    await fetch(`/customer/deleteProductFromFavouritesGroup/` + idNewGroup + `/` + idOldGroup, {
        method: 'PUT',
        body: JSON.stringify(idProducts),
        headers: headers
    }).then(respons => {
        return respons;
    });
    defaultBtnGroup();
};

/**
 * Добавляем название нового списка в БД
 * @param nameGroup   Название новой группы
 * @returns {Promise<void>}
 */
async function addFavouritesGroupInBD(nameGroup) {
    const headers = {
        'Content-type': 'application/json; charset=UTF-8'
    };
    let favouritesGroup = {
        name: nameGroup
    };
    await fetch(`/customer/favouritesGroup`, {
        method: 'POST',
        body: JSON.stringify(favouritesGroup),
        headers: headers
    }).then(respons => respons.json())
        .then(data => {
            $('.dropdown-menu').append("<a class='dropdown-item' href='#' id=" + data.id + ">" + data.name + "</a>");
            let kusok = `<tr class="select-group-tr mt-3" id="${data.id}">
                <td>
                    <input id="${data.id}" type="checkbox" class="check-favourites-group">
                        <label class="form-check-label" for="${data.id}">
                            <input id="${data.id}" type="text" value="${data.name}" class="select-group-tr-input" disabled="">
                        </label>
                </td>
                <td>
                    <div class="ok-cancel-edit-fav-group hidden" id="${data.id}">
                        <span class="edit-fav-group-ok" id="${data.id}">V</span>
                        <span class="edit-fav-group-cancel" id="${data.id}">X</span>
                    </div>
                    <button type="button" class="button-edit-fav-group btn btn-secondary" id="${data.id}">EDIT
                    </button>
                    <button type="button" class="button-delete-fav-group btn btn-danger" id="${data.id}">DELETE
                    </button>
                </td>
            </tr>
            `;
            $('.select_group').append(kusok);
        });
};

/**
 * Удаляем из БД имя скписка Избранного Кнопкой DELETE
 * @param id
 * @returns {Promise<void>}
 */
async function deleteFavouritesGroupInBD(id) {
    fetch(`/customer/favouritesGroup/` + id, {
        method: 'DELETE'
    })
};