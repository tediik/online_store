/**
 * Когда html страница готова, во вкладку Подписки
 * добавляются товары, за изменением цены которых следит владелец ЛК
 */
$(document).ready(function () {
    getTrackableProducts();
})

/**
 * Метод делает запрос товаров,
 * на изменение цен которых подписан владелец ЛК
 */
function getTrackableProducts() {
    fetch("/api/customer/trackableProduct")
        .then(response => {
            if (response.status === 200) {
                response.json()
                    .then(trackableProduct => {
                        for (let product of trackableProduct) {
                            renderTrackingProducts(product);
                        }
                    })
            } else {
                renderInfoMessage();
            }
        })
}

/**
 * Метод вставляет отрисованные карточки отслеживемых товаров на фронт
 * @param trackableProduct товаров, за изменением цены которого следит владелец ЛК
 */
function renderTrackingProducts(trackableProduct) {
    let productsBody = document.querySelector('#trackableProduct');
    let insertBody = makeTrackableProductBody(trackableProduct);
    productsBody.insertAdjacentHTML("afterbegin", insertBody);
}

/**
 * Метод отрисовывает карточку товара
 * @param productsBody товар, который будет отрисован
 * @returns {string} карточка товара в html
 */
function makeTrackableProductBody(productsBody) {
    return `<div class="col mb-3" id="trackableProductCard${productsBody.id}">
                <div class="card bg-light h-100">
                    <div class="card-body">
                        <h3 class="card-title">
                            <a href="/products/${productsBody.id}">${productsBody.product}</a>
                        </h3>        
                        <h5 class="card-text">Стоимость ${productsBody.price} р.</h5>                                                  
                    </div>
                    <div class="card-footer">                    
                        <button type="button" class="btn btn-danger btn-sm float-right" onclick="stopTrackingProduct(${productsBody.id})">Отписаться</button>
                    </div>
                </div>                
            </div>`
}

/**
 * Метод вставляет информационное сообщение на фронт
 */
function renderInfoMessage() {
    let productsBody = document.querySelector('#trackableProduct');
    let insertBody = `<h5>Вы не отслеживаете изменения цен на наши товары</h5>`
    productsBody.insertAdjacentHTML("afterbegin", insertBody);
}

/**
 * Метод делает запрос на удаление подписки на товар,
 * на изменение цены которого подписан владелец ЛК
 * @param id идентификатор товара
 */
function stopTrackingProduct(id) {
    fetch("/api/customer/trackableProduct/" + id, {
        method: "DELETE",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            id: id
        })
    }).then(response => {
        if (response.status === 200) {
            response.json()
                .then(untraceableProduct => {
                    deleteProductBodyFromTrackableProducts(untraceableProduct);
                })
        }
    })
}

/**
 * Метод удаляет карточку товара с фронта
 * @param untraceableProduct id товара, на изменение цены которого владелец ЛК уже не подписан
 */
function deleteProductBodyFromTrackableProducts(untraceableProduct) {
    let cardsBody = document.querySelector(`#trackableProductCard${untraceableProduct}`);
    cardsBody.remove();

    if(!$("#trackableProduct").find(".col").length) {
        renderInfoMessage();
    }
}