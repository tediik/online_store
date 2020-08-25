async function fillBusket() {
    let response = await fetch("/customer/busketGoods");
    let content = await response.json();
    let basketGoodsJson = document.getElementById('busketList');
    let countGoods = 0;
    let sumBasket = 0;
    let key
    $(basketGoodsJson).empty();
    for (key in content) {
        countGoods += content[key].count;
        let product = `
        <tr class=${content[key].id} id=${content[key].id}>
    <td>${content[key].product.product}</td>
    <td> цена: ${content[key].product.price} &#8381;</td>
    <td>
    <div class="amount">
\t<button id="down" class="btn btn-primary" onclick="downCountBasket(${content[key].id})">-</button>
    <span/>${content[key].count} </span>
\t<button id="up" class="btn btn-primary" onclick="upCountBasket(${content[key].id})">+</button>
</div>
    </td>
    <td>
    стоимость: ${content[key].product.price * content[key].count}
    </td>
    <td>
    <button class="btn btn-primary" onclick="deleteBasket(${content[key].id})">Удалить</button>
    </td>
    <tr>
`;
        sumBasket += content[key].product.price * content[key].count;
        $(basketGoodsJson).append(product);
    }
    sumBasket +=" &#8381;";
    $('#countBasketGoods').empty();
    $('#sumBasketGoods').empty();

    $('#countBasketGoods').append(countGoods);
    $('#sumBasketGoods').append(sumBasket);
}

async function deleteBasket(id) {
    await fetch("/customer/busketGoods", {
        method: "DELETE",
        body: id,
        headers: {"Content-Type": "application/json; charset=utf-8"}
    });
    await fillBusket();
}

async function upCountBasket(id) {
    await fetch("/customer/upBusketGoods", {
        method: "PUT",
        body: id,
        headers: {"Content-Type": "application/json; charset=utf-8"}
    });
    await fillBusket();
}

async function downCountBasket(id) {
    await fetch("/customer/downBusketGoods", {
        method: "PUT",
        body: id,
        headers: {"Content-Type": "application/json; charset=utf-8"}
    });
    await fillBusket();
}

async function buildOrderFromBasket() {
    await fetch("/customer/busketGoods", {
        method: "POST",
        headers: {"Content-Type": "application/json; charset=utf-8"}
    });
    await fillBusket();
}