function prepareNumber(n) {
    let a, s = Number(n).toFixed(2);
    while (a = s.match(/\d(\d{3}[^\d])/)) s = s.replace(a[1], "&ensp;" + a[1]);
    return s;
}
function prepareDate(date){
    let s = new Date(date);
    return s.toUTCString();
}

function colorDiv(status) {
    if (status == "INCARTS") {
        return "alert-success";
    }
     return "alert-secondary";
}

async function addOrders(stringUrlForMapping, stringElentForAdd){

    let response = await fetch(stringUrlForMapping);
    let content = await response.json();
    let orderGoodsJson = document.getElementById(stringElentForAdd);
    let key;
    $(orderGoodsJson).empty();
    for (key in content) {
        let order = `
        <div class="alert ${colorDiv(content[key].status)} mt-2">
                                        <h4>Номер заказа: ${content[key].id}</h4>
                                        <h6>Статус:  ${content[key].status}</h6>
                                        <div class="row">
                                            <div class="col-6">
                                                <h5>Количество товаров : ${content[key].amount}</h5>
                                            </div>
                                            <div class="col-6">
                                                <h5>Сумма заказа: ${prepareNumber(content[key].orderPrice)} &#8381;</h5>
                                            </div>
                                            <div class="col-12"><h1></h1></div>
                                            <div class="col-6">
                                                <span >Время заказа: ${prepareDate(content[key].dateTime)}</span>
                                            </div>
                                            <div class="col-6">
                                                <button type="button" class="btn btn-primary" data-toggle="modal"
                                                        data-target="#orderModal" onclick="fillOrderById(${content[key].id})">подробнее
                                                </button>
                                            </div>
                                        </div>
                                    </div>
        `;
        $(orderGoodsJson).append(order);
    }
}

async function fillOrderById(id) {
    let response = await fetch("/api/customer/getOrderById", {
        method: "POST",
        body: id,
        headers: {"Content-Type": "application/json; charset=utf-8"}
    });
    let content = await response.json();
    console.log(content);
    let orderGoodsJson = document.getElementById("orderModalBody");
    let key;
    $('#exampleModalLabel').empty();
    $('#exampleModalLabel').append("Заказ: " + content.id);
    $(orderGoodsJson).empty();
    let head = `
     <div class="col-12">
        <h6>Дата заказа: ${prepareDate(content.dateTime)}</h6>
        </div>
        <div class="col-6">
        <h3>Общая стоимость:</h3>
        </div>
        <div class="col-6">
        <h3>${prepareNumber(content.orderPrice)}</h3>
        </div>
        </div>
    `;
    $(orderGoodsJson).append(head);
    for (key in content.productInOrders) {
        let order = `

        <div class="row alert alert-light mt-2">
        <div class="col-3">
        <h4>${content.productInOrders[key].product.product}</h4>
        </div>
        <div class="col-3">
        <h4>количество: ${content.productInOrders[key].amount}</h4>
        </div>
        <div class="col-3">
        <h4>цена: ${prepareNumber(content.productInOrders[key].buyPrice)}</h4>
        </div>
        <div class="col-3">
        <h4>сумма: ${prepareNumber(content.productInOrders[key].amount*content.productInOrders[key].buyPrice)}</h4>
        </div>
        `;
        $(orderGoodsJson).append(order);
    }
}

async function fillOrders() {
    await addOrders("/api/customer/getAllOrders", "allOrdersSet");
}

async function fillIncartsOrders() {
    await addOrders("/api/customer/getIncartsOrders", "inCartsOrders");
}

async function fillCompletedOrders() {
    await addOrders("/api/customer/getCompletedOrders", "completedOrders");
}

async function fillCanceledOrders() {
    await addOrders("/api/customer/getCanceledOrders", "canceledOrders");
}
