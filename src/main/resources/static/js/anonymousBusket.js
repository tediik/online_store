function prepareNumber(n) {
    let a, s = Number(n).toFixed(2);
    while (a = s.match(/\d(\d{3}[^\d])/)) s = s.replace(a[1], "&ensp;" + a[1]);
    return s;
}

$(document).ready(function (){
    fillBasket();
})

async function fillBasket() {
    let response = await fetch("/basketGoods");
    let content = await response.json();
    let basketGoodsJson = document.getElementById('busketTable');
    let countGoods = 0;
    let sumBasket = 0;
    let key;
    $(basketGoodsJson).empty();
    for (key in content) {
        countGoods += content[key].count;
        let product = `
        <tr class=${content[key].id} id=${content[key].id}>
            <td>
                ${content[key].product.product}
            </td>
            <td>
                цена: ${prepareNumber(content[key].product.price)} &#8381;
            </td>
            <td>
                <div class="amount">
                        <button id = "down" class = "btn btn-primary" onclick = "updateCountBasket(${content[key].id}, -1 )" > - </button>
                        <span>${content[key].count} шт.</span>
                        <button id = "up" class = "btn btn-primary" onclick = "updateCountBasket(${content[key].id}, 1 )" > + </button>
                </div>
            </td>
            <td>
                стоимость: ${prepareNumber(content[key].product.price * content[key].count)} &#8381;
            </td>
            <td>
                 <svg width="640" height="480" viewbox="0 0 640 480">
                   <path id="heart${content[key].id}" 
                   d="m219.28949,21.827393c-66.240005,0 -119.999954,53.76001 -119.999954,
                   120c0,134.755524 135.933151,170.08728 228.562454,303.308044c87.574219,
                   -132.403381 228.5625,-172.854584 228.5625,-303.308044c0,-66.23999
                    -53.759888,-120 -120,-120c-48.047913,0 -89.401611,28.370422 -108.5625,
                    69.1875c-19.160797,-40.817078 -60.514496,-69.1875 -108.5625,-69.1875z" 
                    onclick="addToFavouritsGoods(${content[key].product.id}, ${content[key].id})"/>
                </svg>  
            </td>
            <td>
                <button class="btn btn-primary" onclick="deleteBasket(${content[key].id})">Удалить</button>
            </td>
        <tr>
        `;
        fetch("api/products/" + `${content[key].product.id}`)
            .then(function (response) {
                response.json().then(function (data) {
                    if (data.favourite) {
                        $('#heart' + `${content[key].id}`).toggleClass("filled");
                    }
                })
            });
        sumBasket += content[key].product.price * content[key].count;
        $(basketGoodsJson).append(product);
    }
    sumBasket = prepareNumber(sumBasket);
    sumBasket += " &#8381;";
    $('#countBasketGoods').empty();
    $('#sumBasketGoods').empty();

    $('#countBasketGoods').append(countGoods + " шт.");
    $('#sumBasketGoods').append(sumBasket);

    $('#countInBasket').empty();
    $('#countInBasket').append(countGoods);
    //if basket is empty
    if (countGoods == 0) {
        let emptyBasket = `
            <tr align='center'>
                <td > Корзина сейчас пуста,</br>но вы можете перейти на главную страницу или в каталог товаров</td>
            </tr>
            <tr ><td ></td></tr>
            <tr align='center'>
                <td>
                <a class='btn btn-outline-primary mr-2' role='button' onclick='' href='/'>На главную страницу</a>
                <a class='btn btn-outline-danger ml-2' role='button' onclick='' href='#'>Перейдите в каталог</a>
                </td>
            </tr>
            `;
        let buttonBuy = `
            <button class="btn btn-lg btn-outline-primary btn-block" data-toggle="modal"
            data-target="#orderModalWindow" onclick="userAdresses()" disabled>Корзина пуста</button>
            `;
        $(basketGoodsJson).append(emptyBasket);
        $('#buttonBuyInBasket').empty().append(buttonBuy);
    }
}


async function deleteBasket(id) {
    await fetch("/customer/basketGoods", {
        method: "DELETE",
        body: id,
        headers: {"Content-Type": "application/json; charset=utf-8"}
    });
    await fillBasket();
}

async function updateCountBasket(id, count) {
    await fetch("/customer/basketGoods", {
        method: "PUT",
        body: JSON.stringify({
            id: id,
            count: count
        }),
        headers: {"Content-Type": "application/json; charset=utf-8"}
    }).then(function (response) {
        if(response.status === 400) {
            toastr.warning('Данный товар закончился', '', {timeOut: 1000})
        }
    });
    await fillBasket();
}

async function buildOrderFromBasket() {
    await fetch("/customer/basketGoods", {
        method: "POST",
        headers: {"Content-Type": "application/json; charset=utf-8"}
    });
    await fillBasket();
}

async function addToFavouritsGoods(id, heartId) {
    if ($("path").is('[class="filled"]')) {
        await fetch("/customer/favouritesGoods", {
            method: "DELETE",
            body: id,
            headers: {"Content-Type": "application/json; charset=utf-8"}
        }).then(function (response) {
            if (response.ok) {
                $('#heart' + heartId).toggleClass("filled");
                toastr.success("Товар успешно удалён из избранного");
            } else {
                toastr.error("Авторизуйтесь/зарегистрируйтесь");
            }
        });
    } else {
        await fetch("/customer/favouritesGoods", {
            method: "PUT",
            body: id,
            headers: {"Content-Type": "application/json; charset=utf-8"}
        }).then(function (response) {
            if (response.ok) {
                $('#heart' + heartId).toggleClass("filled");
                toastr.success("Товар успешно добавлен в избранное");
            } else {
                toastr.error("Авторизуйтесь/зарегистрируйтесь");
            }
        });
    }
}