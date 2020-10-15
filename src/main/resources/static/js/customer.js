$(document).ready(function(){
    $('select').on('change', function () {   //'#favouritesGroup'
        var selectedValue = this.selectedOptions[0].value;
        var selectedText  = this.selectedOptions[0].text;
        fillNewTableProductsGroup();
    });
});
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
    <td><input type="checkbox" class="checkProductInGroup" name="${content[key].id}"/></td> 
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
    getFavouritesGroupInSelect();
}

async function deleteProductFromFavouritGoods(id) {
    await fetch("/customer/favouritesGoods", {
        method: "DELETE",
        body: id,
        headers: {"Content-Type": "application/json; charset=utf-8"}
    });
    await fillFavouritesGoods();
}

async function addProductToBasket(id) {
    await fetch(`/api/basket/add/${id}`, {
        method: "PUT",
        headers: {"Content-Type": "application/json; charset=utf-8"}
    });
    fillBusket();
}

$(document).on("click", "#showBasket", function () {
    $('#v-pills-tab a[href="#basketGoods"]').tab('show')
});
$(document).on("click", "#inic-product-buton", function () {
    inicStartGroup();
});
$(document).on("click", "#new-table-product-buton", function () {
    fillNewTableProductsGroup();
});

$(document).on("click", "#add-group-buton", function () {
    let nameGroup = prompt("Введите название группы \"Избранных товаров\" ");
    if (nameGroup) {
        $('#favouritesGroup').append("<option value='" + toTranslit(nameGroup) + "'>" + nameGroup + " </option>");
        addFavouritesGroupInBD(nameGroup);
    }
});

$(document).on("click", "#delete-group-buton", function () {
    let idGroup = $("#favouritesGroup option:selected").attr("id");
    if (idGroup) {
        deleteFavouritesGroupInBD(idGroup);
        $("#favouritesGroup :selected").remove()
    }
});

/**
 * Привязываем событие к CHECKBOX чтобы отметить избранные товары для отслеживания id
 */
$(document).on("click", ".checkProductInGroup", function () {
    if ($(this).is(':checked')) {
        $(this).attr('class', 'checkProductInGroup selected');
    } else {
        $(this).attr('class', 'checkProductInGroup');
    }
});
/**
 * Привязываем событие к кнопке перемещения товара в избранную группу товаров
 */
$(document).on("click", "#move-product-buton", function () {
    let idGroup = $("#favouritesGroup option:selected").attr("id");
    let idProduct = $(".checkProductInGroup:checked").map(function() {return this.name;}).get();
    if (idProduct != '') {
        console.log("Товар id=" + idProduct + " пеермещен в группу с id=" + idGroup);
        addProductInFavouritesGroupInBD(idProduct, idGroup);
    }
});

/**
 * Передаем в БД товары и список Избранного для добавления
 * @param idProduct
 * @param idGroup
 * @returns {Promise<void>}
 */
async function addProductInFavouritesGroupInBD(idProduct, idGroup) {
    const headers = {
        'Content-type': 'application/json; charset=UTF-8'
    };
    let idPidG = idProduct;
    idPidG.push(idGroup);
    console.log(idPidG);
    fetch(`/customer/addProductInFavouritesGroup`, {
        method: 'POST',
        body: JSON.stringify(idPidG),
        headers: headers
    }).then(response => {
        return response.text();
    }).then(data => {
        console.log(data);
    })
};

/**
 * Удаление продукта из одной группы при перемещении в другую
 * @param idProduct
 * @param idGroup
 * @returns {Promise<void>}
 */
async function deleteProductFromFavouritesGroupInBD(idProduct, idGroup) {
    const headers = {
        'Content-type': 'application/json; charset=UTF-8'
    };
    let idPidG = idProduct;
    idPidG.push(idGroup);
    console.log(idPidG);
    fetch(`/customer/deleteProductFromFavouritesGroup`, {
        method: 'DELETE',
        body: JSON.stringify(idPidG),
        headers: headers
    }).then(response => {
        console.log(response.text());
    })
};
/**
 * Получаем список групп Избранного из БД и формируем "select"
 * @returns {Promise<void>}
 */
async function getFavouritesGroupInSelect() {
    fetch(`/customer/favouritesGroup`).then(response => response.json()).then(fgroup => {
        for (let i = 0; i < fgroup.length; i++) {
            $('#favouritesGroup').append("<option id=" + fgroup[i].id + " value='" + toTranslit(fgroup[i].name) + "'>" + fgroup[i].name + " </option>");
        }
    })
};
/**
 * Передаем в БД новое имя списка Избранного
 * @param nameGroup
 * @returns {Promise<void>}
 */
async function addFavouritesGroupInBD(nameGroup) {
    let thisId = 0;
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
    }).then(response => {
        return response.json();
    }).then(idGroup => {
        thisId = idGroup;
    });
    $("select option[value=" + toTranslit(nameGroup) + "]").attr("id", thisId);
};
/**
 * Удаляем из БД имя скписка Избранного Кнопкой с id = delete-group-buton
 * @param id
 * @returns {Promise<void>}
 */
async function deleteFavouritesGroupInBD(id) {
    fetch(`/customer/favouritesGroup/` + id, {
        method: 'DELETE'
    })
};
/**
 * Транслитерация текста text
 * @param text
 * @returns {Node|void|string|*}
 */
function toTranslit(text) {
    return text.replace(/([а-яё])|([\s_-])|([^a-z\d])/gi,
        function (all, ch, space, words, i) {
            if (space || words) {
                return space ? '-' : '';
            }
            var code = ch.charCodeAt(0),
                index = code == 1025 || code == 1105 ? 0 :
                    code > 1071 ? code - 1071 : code - 1039,
                t = ['yo', 'a', 'b', 'v', 'g', 'd', 'e', 'zh',
                    'z', 'i', 'y', 'k', 'l', 'm', 'n', 'o', 'p',
                    'r', 's', 't', 'u', 'f', 'h', 'c', 'ch', 'sh',
                    'shch', '', 'y', '', 'e', 'yu', 'ya'
                ];
            return t[index];
        });
};

function inicStartGroup(){
    let idGroup = $("#favouritesGroup :first").attr("id");
    $(".checkProductInGroup").attr("idGroup", idGroup);
    let idProductInAll = $(".checkProductInGroup").map(function() {return this.name;}).get();
    if (idProductInAll != '') {
        console.log("Товар id=" + idProductInAll + " пеермещен в группу с id=" + idGroup);
        addProductInFavouritesGroupInBD(idProductInAll, idGroup);
    }
};

/**
 * Перерисовываем таблицу избранного в зависимости от селекта
 */
function fillNewTableProductsGroup(){
    let idGroup = $("#favouritesGroup option:selected").attr("id");
    fillFavouritesGroupProducts(idGroup);
};
async function fillFavouritesGroupProducts(idGroup) {
    let response = await fetch("/customer/getProductFromFavouritesGroup/" + idGroup);
    let content = await response.json();
    let favoriteGoodsJson = document.getElementById('favouritesGoodsList');
    let key
    $(favoriteGoodsJson).empty();
    for (key in content) {
        let product = `
        <tr class=${content[key].id} id=${content[key].id}>
    <td>${content[key].id}</td>
    <td><input type="checkbox" class="checkProductInGroup" name="${content[key].id}"/></td> 
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