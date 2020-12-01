// документация к jqxtree -
// https://www.jqwidgets.com/jquery-widgets-documentation/documentation/jqxtree/jquery-tree-getting-started.htm?search=
const API_CATEGORIES_URL = "/api/categories/"
let listOfAll
let currentCategoryIdAdd;
let currentCategoryNameEdit;
let productRestUrl = "/rest/products/allProducts"
let headers = new Headers()
headers.append('Content-type', 'application/json; charset=UTF-8')
document.getElementById('addBtn').addEventListener('click', handleAddBtn)

$(function () {
    fillProductCategoriesIn('#jqxTreeHere')
        .then(() => {
            $('#jqxTreeHere').jqxTree('expandAll');
        })
        .then(() => { // поиск по категориям
            document.querySelector('#searchForCategories').oninput = function () {
                let val = this.value.trim().toLowerCase();
                let allItems = document.querySelectorAll('#jqxTreeHere li');
                if (val != '') {
                    allItems.forEach(function (element) {
                        if (element.innerText.toLowerCase().search(val) == -1) {
                            element.classList.add('hide');
                        } else {
                            element.classList.remove('hide');
                        }
                    })
                } else {
                    allItems.forEach(function (element) {
                        element.classList.remove('hide');
                    });
                }
            }
        });
});
/**
функция добавляет дерево выбора категорий на страницу менеджера в окно загрузки товаров из файла
 */
$(function () {
    fillProductCategoriesIn('#jqxTreeHere1')
        .then(() => {
            $('#jqxTreeHere1').jqxTree('expandAll');
        });
});

// build hierarchical structure
async function fillProductCategoriesIn(htmlId) {
    listOfAll = await fetch(API_CATEGORIES_URL + "all").then(response => response.json());
    let source = [];
    let items = [];
    for (let i = 0; i < listOfAll.length; i++) {
        let item = listOfAll[i];
        let label = item.text;
        let thisParentId = item.parentId;
        let id = item.id;
        if (items[thisParentId]) {
            let tmpItem = {parentId: thisParentId, label: label, item: item};
            if (!items[thisParentId].items) {
                items[thisParentId].items = [];
            }
            items[thisParentId].items[items[thisParentId].items.length] = tmpItem;
            items[id] = tmpItem;
        } else {
            items[id] = {parentId: thisParentId, label: label, item: item};
            source[id] = items[id];
        }
    }
    $(htmlId).jqxTree({
        source: source,
        height: "300px"
    });
}

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
function getAllProducts() { // не нашел, где используется эта функция
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
    $('#productCategoryValueModal').val(currentCategoryNameEdit);
    fillProductCategoriesIn('#jqxTreeModal').then();
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
        .then(([productToEdit]) => {
            fetch(API_CATEGORIES_URL + 'getOne/' + productId)
                .then(promiseResult => {
                    return promiseResult.text()
                })
                .then(responseResult => {
                    currentCategoryNameEdit = "" + responseResult;
                    editModalWindowRender(productToEdit)
                });
        });
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
    fetch("/rest/products/restoredeleted/" + productId, {
        headers: headers,
        method: 'POST'
    }).then(response => response.text())
        .then(restoreProduct => console.log('Product: ' + restoreProduct + ' was successfully restored'))
        .then(showTable => showAndRefreshHomeTab(showTable))
}

/**
 * функция делает активной таблицу с продуктами
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
 * функция делает активной таблицу без
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
    let productToAdd = {
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
     * проверяем, что выбранная категория продукта != null
     */
    let selectedCat = $('#jqxTreeHere').jqxTree('getSelectedItem');
    if (!selectedCat) {
        alert('Категория не выбрана!');
        return false;
    } else {
        for (let z = 0; z < listOfAll.length; z++) {
            let currItem = listOfAll[z];
            if (selectedCat.label.localeCompare(currItem.text) === 0) {
                currentCategoryIdAdd = currItem.id;
            }
        }
    }

    /**
     * проверяем, что наименование, цена продукта и количество заполнены
     */
    if (!productToAdd.product || !productToAdd.price || !productToAdd.amount) {

        if (!productToAdd.product) {
            let confirmName = document.getElementById('addProduct');

            confirmName.focus();
            toastr.error('Заполните поле наименования товара');
        }

        if (!productToAdd.price) {
            let confirmPrice = document.getElementById('addPrice');

            confirmPrice.focus();
            toastr.error('Заполните поле стоимости товара');
        }

        if (!productToAdd.amount) {
            let confirmAmount = document.getElementById('addAmount');

            confirmAmount.focus();
            toastr.error('Заполните поле количества товара');
        }
    }

    fetch("/rest/products/addProduct/" + currentCategoryIdAdd, {
        method: 'POST',
        headers: {'Content-Type': 'application/json;charset=utf-8'},
        body: JSON.stringify(productToAdd)
    })
        .then(function (response) {
                let field;
                if (response.status !== 200) {
                    response.text()
                        .then(
                            function (text) {

                                if (text === "duplicatedNameProductError") {
                                    toastr.error('Такое наименование уже существует');
                                }

                                console.log(text)
                            })
                } else {
                    response.text().then(function () {
                        $("#jqxTreeHere").jqxTree('selectItem', null);
                        if (document.getElementById("deletedCheckbox").checked) {
                            showAndRefreshNotDeleteHomeTab()
                        } else {
                            showAndRefreshHomeTab()
                        }
                        clearFormFields();
                        toastr.success('Товар успешно добавлен')
                    })
                }
            }
        )
}

/**
 * Добавление товара из файла
 *
 * проверяем выбрана ли категория, если да , то добавляем товары и присваиваем им выбранную категорию,
 * если не выбрана то парсим файл в поиски категорий описанных в нём (позволяет добавлять много товаров разных категорий одним файлом)
 */
$('#inputFileSubmit').click(importProductsFromFile)

function importProductsFromFile() {
    let selectedCatForImport = $('#jqxTreeHere1').jqxTree('getSelectedItem');
    if (!selectedCatForImport) {
        let fileData = new FormData();
        fileData.append('file', $('#file')[0].files[0]);

        $.ajax({
            url: '/rest/products/uploadProductsFile/',
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

    } else {
        for (let z = 0; z < listOfAll.length; z++) {
            let currItem = listOfAll[z];
            if (selectedCatForImport.label.localeCompare(currItem.text) === 0) {
                currentCategoryIdAdd = currItem.id;
            }
        }

        let fileData = new FormData();
        fileData.append('file', $('#file')[0].files[0]);

        $.ajax({
            url: '/rest/products/uploadProductsFile/' + currentCategoryIdAdd,
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
        fetch("/rest/products/" + product.id, {
            headers: headers,
            method: 'DELETE'
        }).then(response => response.text())
            .then(deletedProduct => console.log('Product: ' + deletedProduct + ' was successfully deleted'))
            .then(showTable => {
                if (document.getElementById("deletedCheckbox").checked) {
                    showAndRefreshNotDeleteHomeTab(showTable)
                } else {
                    showAndRefreshHomeTab(showTable)
                }
            })
        $('#productModalWindow').modal('hide')
    } else {
        let newCategory = $('#jqxTreeModal').jqxTree('getSelectedItem');
        if (!newCategory || currentCategoryNameEdit.localeCompare(newCategory.label) === 0) {
            fetch("/rest/products/editProduct/", {
                method: 'PUT',
                headers: headers,
                body: JSON.stringify(product)
            }).then(function (response) {
                if (document.getElementById("deletedCheckbox").checked) {
                    fetchProductsAndRenderNotDeleteTable();
                } else {
                    fetchProductsAndRenderTable()
                }
                $('#productModalWindow').modal('hide')
            })
        } else {
            fetch("/rest/products/editProduct/" + getCatId(currentCategoryNameEdit)
                + "/" + getCatId(newCategory.label), {
                method: 'PUT',
                headers: headers,
                body: JSON.stringify(product)
            }).then(function (response) {
                if (document.getElementById("deletedCheckbox").checked) {
                    fetchProductsAndRenderNotDeleteTable();
                } else {
                    fetchProductsAndRenderTable()
                }
                $('#productModalWindow').modal('hide')
            })
        }
    }
}

function getCatId(category) {
    if (!category) {
        return -1;
    } else {
        for (let i = 0; i < listOfAll.length; i++) {
            let tempItem = listOfAll[i];
            if (category.localeCompare(tempItem.text) === 0) {
                return tempItem.id;
            }
        }
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
    let table = $('#products-table')
    table.empty()
        .append(`<tr>
                <th>ID</th>
                <th>Наименование товара</th>
                <th>Цена</th>
                <th>Количество</th>
                <th>Edit</th>
                <th>Delete</th>
              </tr>`)
    for (let i = 0; i < products.length; i++) {
        const product = products[i];
        let row = `
                <tr id="tr-${product.id}">
                    <td>${product.id}</td>
                    <td>${product.product}</td>
                    <td>${product.price}</td>
                    <td>${product.amount}</td>              
                    
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
    $('.edit-button').click(handleEditButton)
    $('.action').click(checkActionButton)
}

/**
 * функция делает fetch запрос на рест контроллер, преобразует полученный объект в json
 * и передает функции рендера таблицы renderProductsTable
 */
function fetchProductsAndRenderTable() {
    fetch("/rest/products/allProducts")
        .then(response => response.json())
        .then(products => renderProductsTable(products))
}

/**
 * функция делает fetch запрос на рест контроллер для получения неудаленных товаров,
 * преобразует полученный объект в json
 * и передает функции рендера таблицы renderProductsTable
 */
function fetchProductsAndRenderNotDeleteTable() {
    fetch("/rest/products/getNotDeleteProducts")
        .then(response => response.json())
        .then(products => renderProductsTable(products))
}