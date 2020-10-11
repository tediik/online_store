$(document).ready(function () {
    getFavouritesGroupInSelect();
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
    <td><input type="checkbox" name="${content[key].id}"/></td> 
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
    // $("#town :selected").remove() удалить выбранный елемент
    // $("#town option:selected").text();  получить текст выбранной категории

});

//Получаем список групп Избранного из БД и формируем "select"
async function getFavouritesGroupInSelect() {
    fetch(`/customer/favouritesGroup`).then(response => response.json()).then(fgroup => {
        for (let i = 0; i < fgroup.length; i++) {
            $('#favouritesGroup').append("<option id=" + fgroup[i].id + " value='" + toTranslit(fgroup[i].name) + "'>" + fgroup[i].name + " </option>");
        }
    })
};

//Передаем в БД новое имя скписка Избранного
async function addFavouritesGroupInBD(nameGroup) {
    const headers = {
        'Content-type': 'application/json; charset=UTF-8'
    };
    let favouritesGroup = {
        name: nameGroup
    };
    fetch(`/customer/favouritesGroup`, {
        method: 'POST',
        body: JSON.stringify(favouritesGroup),
        headers: headers
    }).then(response => {
        console.log(response.text());
    })
};

//Удаляем из БД имя скписка Избранного Кнопкой с id = delete-group-buton
async function deleteFavouritesGroupInBD(id) {
    fetch(`/customer/favouritesGroup/` + id, {
        method: 'DELETE'
    })
};

// Транслитерация текста text
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