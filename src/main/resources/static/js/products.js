let productRestUrl = "/rest/products/allProducts"
let categoryNow = "all";
let headers = new Headers()
headers.append('Content-type', 'application/json; charset=UTF-8')
document.getElementById('addBtn').addEventListener('click', handleAddBtn)

showAndRefreshNotDeleteHomeTab()

/**
 * Функция обработки действий чекбокса
 * @param check
 */
function toggle(check) {
    if (!check.checked) {
        showAndRefreshHomeTab()
    } else {
        showAndRefreshNotDeleteHomeTab()
    }
}

/**
 * fetch запрос на allProducts для получения всех продуктов из бд
 *
 */
function getAllProducts() {
    fetch(productRestUrl, {headers: headers}).then(response => response.json())
        .then(allProducts => renderProductsTable(allProducts))
}

/**
 * Функция рендера модального окна Edit product
 * @param product продукта из таблицы
 */
function editModalWindowRender(product) {
    $('.modal-dialog').off("click").on("click", "#acceptButton", handleAcceptButtonFromModalWindow)
    $('#idInputModal').val(product.id)
    $('#acceptButton').text("Save changes").removeClass().toggleClass('btn btn-success edit-product')
    $('.modal-title').text("Edit product")
    $('#productInputModal').val(product.product).prop('readonly', false)
    $('#productPriceInputModal').val(product.price).prop('readonly', false)
    $('#productAmountInputModal').val(product.amount).prop('readonly', false)
}

/**
 * Функция рендера модального окна Delete product
 * @param productToEdit
 */
function deleteModalWindowRender(productToEdit) {
    $('.modal-dialog').off("click").on("click", "#acceptButton", handleAcceptButtonFromModalWindow)
    $('.modal-title').text("Delete product")
    $('#acceptButton').text("Delete").removeClass().toggleClass('btn btn-danger delete-product')
    $('#idInputModal').val(productToEdit.id)
    $('#productInputModal').val(productToEdit.product).prop('readonly', true)
    $('#productPriceInputModal').val(productToEdit.price).prop('readonly', true)
    $('#productAmountInputModal').val(productToEdit.amount).prop('readonly', true)
}

/**
 * Функция обработки нажатия кнопки Edit в таблице продукта
 * @param event
 */
function handleEditButton(event) {
    const productId = event.target.dataset["productId"]
    Promise.all([
        fetch("/rest/products/" + productId, {headers: headers}),
    ])
        .then(([response]) => Promise.all([response.json()]))
        .then(([productToEdit]) => editModalWindowRender(productToEdit))
}

/**
 * Функция обработки нажатия кнопки Delete в таблице продуктов
 * @param productId
 */
function handleDeleteButton(productId) {
    fetch("/rest/products/" + productId, {headers: headers})
        .then(response => response.json())
        .then(productToDelete => deleteModalWindowRender(productToDelete))
}

/**
 * Функция обработки нажатия кнопки Restore в таблице продуктов
 * @param productId
 */
function handleRestoreButton(productId) {
    fetch("/rest/products/restoredeleted" + "/" + productId, {
        headers: headers,
        method: 'POST'
    }).then(response => response.text())
        .then(restoreProduct => console.log('User: ' + restoreProduct + ' was successfully restored'))
        .then(showTable => showAndRefreshHomeTab(showTable))
}

/**
 * функция делает активным таблицу с продуктами
 * и обновляет в ней данные
 */
function showAndRefreshHomeTab() {
    fetchProductsAndRenderTable()
    $('#nav-home').addClass('tab-pane fade active show')
    $('#nav-profile').removeClass('active show')
    $('#nav-profile-tab').removeClass('active')
    $('#nav-home-tab').addClass('active')
}

/**
 * функция делает активным таблицу без
 * удаленных продуктов
 */
function showAndRefreshNotDeleteHomeTab() {
    fetchProductsAndRenderNotDeleteTable()
    $('#nav-home').addClass('tab-pane fade active show')
    $('#nav-profile').removeClass('active show')
    $('#nav-profile-tab').removeClass('active')
    $('#nav-home-tab').addClass('active')
}

/**
 * функция обработки кнопки add на форме нового продукта
 */
function handleAddBtn() {
    let product = {
        product: $('#addProduct').val(),
        price: $('#addPrice').val(),
        amount: $('#addAmount').val(),
    }

    /**
     * функция очистки полей формы нового продукта
     */
    function clearFormFields() {
        $('#addForm')[0].reset();
    }

    /**
     * обработка валидности полей формы, если поле пустое или невалидное, появляется предупреждение
     * и ставится фокус на это поле. Предупреждение автоматически закрывается через 5 сек
     * @param text - текст для вывода в алекрт
     * @param field - поле на каком установить фокус
     */
    function handleNotValidFormField(text, field) {
        $('#alert-div').empty().append(`
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
              <strong>${text}</strong>
              <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            `)
        $('#' + field).focus()

        window.setTimeout(function () {
            $('.alert').alert('close');
        }, 5000)
    }

    fetch("/rest/products/addProduct", {
        method: 'POST',
        headers: {'Content-Type': 'application/json;charset=utf-8'},
        body: JSON.stringify(product)
    }).then(
        function (response) {
            let field;
            if (response.status !== 200) {
                response.text()
                    .then(
                        function (text) {
                            if (text === "notValidNameProduct") {
                                field = "addEmail"
                                handleNotValidFormField("Вы ввели некоректное Наименование товара!", field)
                            }
                            if (text === "duplicatedNameProductError") {
                                field = "addEmail"
                                handleNotValidFormField("Такой наименование уже существует", field)
                            }
                            if (text === "emptyPriceError") {
                                field = "addPrice"
                                handleNotValidFormField("Заполните поле цены", field)
                            }
                            if (text === "amountError") {
                                field = "addAmount"
                                handleNotValidFormField("Необходимо выбрать количество", field)
                            }
                            console.log(text)
                        })
            } else {
                response.text().then(function () {
                    showAndRefreshHomeTab();
                    clearFormFields();
                })
            }
        }
    )
}

/**
 * Добавление товара из файла
 */

$('#inputFileSubmit').click(importProductsFromFile)

function importProductsFromFile() {

    let fileData = new FormData();
    fileData.append('file', $('#file')[0].files[0]);

    $.ajax({
        url: '/rest/products/uploadProductsFile',
        data: fileData,
        processData: false,
        contentType: false,
        type: 'POST',
        success: function (data) {
            showAndRefreshHomeTab()
            toastr.info('Импорт товаров завершен!', {timeOut: 5000})
        },
        error: function () {
            alert("Некорретный путь к файлу!")
        }
    });
}

/**
 * функция обработки нажатия кнопки accept в модальном окне
 * @param event
 */
function handleAcceptButtonFromModalWindow(event) {
    const product = {
        id: $('#idInputModal').val(),
        product: $('#productInputModal').val(),
        price: $('#productPriceInputModal').val(),
        amount: $('#productAmountInputModal').val(),
    };

    /**
     * Проверка кнопки delete или edit
     */
    if ($('#acceptButton').hasClass('delete-product')) {
        fetch("/rest/products" + "/" + product.id, {
            headers: headers,
            method: 'DELETE'
        }).then(response => response.text())
            .then(deletedProduct => console.log('User: ' + deletedProduct + ' was successfully deleted'))
            .then(showTable => showAndRefreshHomeTab(showTable))
        $('#productModalWindow').modal('hide')
    } else {
        fetch("/rest/products/editProduct/", {
            method: 'PUT',
            headers: headers,
            body: JSON.stringify(product)
        }).then(function (response) {
            fetchProductsAndRenderTable()
            $('#productModalWindow').modal('hide')
        })
    }
}

/**
 * функция проверки id button на кнопке
 * @param event
 */
function checkActionButton(event) {
    const productId = event.target.dataset["productId"]
    if (event.target.dataset.toggleId === 'delete') {
        handleDeleteButton(productId)
        $('')
    } else {
        handleRestoreButton(productId)
    }
}

/**
 * функция рендера таблицы продуктов
 * @param products
 */
function renderProductsTable(products) {
    let table = $('#productsTable')
    table.empty()
    for (let i = 0; i < products.length; i++) {
        const product = products[i];
        if (product.productType.category === categoryNow || categoryNow === "all") {
            let row = `
                <tr id="tr-${product.id}">
                    <td>${product.id}</td>
                    <td>${product.product}</td>
                    <td>${product.price}</td>
                    <td>${product.amount}</td>              
                    <td>${product.rating}</td>
                    <td>${product.productType.category}</td>
                    <td>
            <!-- Buttons of the right column of main table-->
                        <button data-product-id="${product.id}" type="button" class="btn btn-success edit-button" data-toggle="modal" data-target="#productModalWindow">
                                 Edit
                        </button>
                    </td>
                    <td>
                        <button data-product-id="${product.id}" type="button" id="action-button-${product.id}" class="btn action" data-target="#productModalWindow">
                                 Delete
                        </button>
                    </td>
                </tr>
                `;
            table.append(row)
            if (product.deleted === false) {
                $(`#action-button-${product.id}`).attr('data-toggle-id', 'delete')
                $(`#action-button-${product.id}`).attr('data-toggle', 'modal')
                $(`#action-button-${product.id}`).text("Delete").removeClass().toggleClass('btn btn-danger delete-product action')
            } else {
                $(`#action-button-${product.id}`).attr('data-toggle-id', 'restore')
                $(`#action-button-${product.id}`).text("Restore").removeClass().toggleClass('btn btn-info restore-product action')
                $('#tr-' + product.id).toggleClass('table-dark')
            }
        }
    }
    $('.edit-button').click(handleEditButton)
    $('.action').click(checkActionButton)
}

/**
 * функция делает fetch запрос на рест контроллер, преобразует полученный объект в json
 * и передает функции рендера таблицы renderProductsTable
 */
function fetchProductsAndRenderTable() {
    fetch("/rest/products/allProducts", {
        method: 'GET',
        headers: {
            'Content-type': 'application/json; charset=UTF-8'
        }
    }).then(response => response.json()).then(products => renderProductsTable(products))
}

/**
 * функция делает fetch запрос на рест контроллер для получения неудаленных товаров,
 * преобразует полученный объект в json
 * и передает функции рендера таблицы renderProductsTable
 */
function fetchProductsAndRenderNotDeleteTable() {
    fetch("/rest/products/getNotDeleteProducts", {
        method: 'GET',
        headers: {
            'Content-type': 'application/json; charset=UTF-8'
        }
    }).then(response => response.json()).then(products => renderProductsTable(products))
}

/**
 * функция сортировки в таблице
 */
document.addEventListener('DOMContentLoaded', () => {

    const getSort = ({target}) => {
        const order = (target.dataset.order = -(target.dataset.order || -1));
        const index = [...target.parentNode.cells].indexOf(target);
        const collator = new Intl.Collator(['en', 'ru'], {numeric: true});
        const comparator = (index, order) => (a, b) => order * collator.compare(
            a.children[index].innerHTML,
            b.children[index].innerHTML
        );

        for (const tBody of target.closest('table').tBodies)
            tBody.append(...[...tBody.rows].sort(comparator(index, order)));

        for (const cell of target.parentNode.cells)
            cell.classList.toggle('sorted', cell === target);
    };

    document.querySelectorAll('.table-sort thead').forEach(tableTH => tableTH.addEventListener('click', () => getSort(event)));

});

/**
 * функция получения категорий из БД
 */
function fetchCategories() {
    fetch("api/categories/all", {headers: headers}).then(response => response.json())
        .then(allCategories => renderCategoriesModal(allCategories))
}

/**
 * функция рендера модалки категорий
 */
function renderCategoriesModal(categories) {
    let radioList = $('#categoriesContainer')
    radioList.empty()
    radioList.append(`<form class= form-group-text-center>`)
    radioList.append(`
                     <div class="custom-control custom-radio">
                     <input value="all" type="radio" id="radio0"
                            name="radioCategory" class="custom-control-input">
                     <label class="custom-control-label" for="radio0">Все</label>
                     </div>
                        `)
    for (let i = 0; i < categories.length; i++) {
        let radiobox = `
                     <div class="custom-control custom-radio">
                     <input value="${categories[i].category}" type="radio" id="radio${categories[i].id}"
                            name="radioCategory" class="custom-control-input">
                     <label class="custom-control-label" for="radio${categories[i].id}">${categories[i].category}</label>
                     </div>
                        `;
        radioList.append(radiobox)
    }
    radioList.append(`</form>`)
}

function changeCategory() {
    let radio = document.getElementsByName('radioCategory');
    for (let i = 0; i < radio.length; i++) {
        if (radio[i].checked) {
            categoryNow = radio[i].value;
        }
    }
}

function changeProducts() {
    changeCategory();
    fetchProductsAndRenderTable();
}

function createReport() {
    changeCategory();
    fetch(`/manager/products/report?category=${categoryNow}`)
        .then(function (response) {
            if (response.status === 200) {
                response.blob().then(blob => {
                    let url = window.URL.createObjectURL(blob);
                    let file = document.createElement('a');
                    file.href = url;
                    file.download = `products_report_${categoryNow}.xlsx`;
                    file.click();
                    alert("Данные успешно выгружены")
                })
            } else {
                alert("Товаров не найдено")
            }
        })
}