function prepareNumber(n) {
    let a, s = Number(n).toFixed(2);
    while (a = s.match(/\d(\d{3}[^\d])/)) s = s.replace(a[1], "&ensp;" + a[1]);
    return s;
}

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
                <button class="btn btn-primary" onclick="deleteBasket(${content[key].id})">Удалить</button>
            </td>
        <tr>
        `;
        sumBasket += content[key].product.price * content[key].count;
        $(basketGoodsJson).append(product);
    }
    sumBasket = prepareNumber(sumBasket);
    sumBasket +=" &#8381;";
    $('#countBasketGoods').empty();
    $('#sumBasketGoods').empty();

    $('#countBasketGoods').append(countGoods + "шт.");
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

async function updateCountBasket(id, count) {
    await fetch("/customer/busketGoods", {
        method: "PUT",
        body: JSON.stringify({
            id: id,
            count: count
        }),
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
