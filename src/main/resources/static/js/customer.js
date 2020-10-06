
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
    /*await fetch("/customer/favouritesGoods", {
        method: "DELETE",
        body: id,
        headers: {"Content-Type": "application/json; charset=utf-8"}
    });
    await fillFavouritesGoods();*/
}

$(document).on("click", "#showBasket", function () {
    $('#v-pills-tab a[href="#basketGoods"]').tab('show')
});
