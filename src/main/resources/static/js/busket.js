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
                   <path id="heart${content[key].id}" d="m219.28949,21.827393c-66.240005,0 -119.999954,53.76001 -119.999954,120c0,134.755524 135.933151,170.08728 228.562454,303.308044c87.574219,-132.403381 228.5625,-172.854584 228.5625,-303.308044c0,-66.23999 -53.759888,-120 -120,-120c-48.047913,0 -89.401611,28.370422 -108.5625,69.1875c-19.160797,-40.817078 -60.514496,-69.1875 -108.5625,-69.1875z" onclick="addToFavouritsGoods(${content[key].product.id}, ${content[key].id})"/>
                </svg>  
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
    sumBasket += " &#8381;";
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

async function addToFavouritsGoods(id, heartId) {
    await fetch("/customer/favouritesGoods", {
        method: "PUT",
        body: id,
        headers: {"Content-Type": "application/json; charset=utf-8"}
    });
    let l = 'heart' + heartId;
    let b = '#' + l;
    $(b).toggleClass('filled');

}
