// документация к jqxtree -
// https://www.jqwidgets.com/jquery-widgets-documentation/documentation/jqxtree/jquery-tree-getting-started.htm?search=
const API_CATEGORIES_URL = "/api/categories/"
let listOfAll
let productRestUrl = "/rest/products/allProducts"
let headers = new Headers()
headers.append('Content-type', 'application/json; charset=UTF-8')
document.getElementById('addBtn').addEventListener('click', handleAddBtn)
/**
 * Переменные для отдельной вкладки "Категории товаров
 */
let currentId;
let currentParentId;
let currentDepth;
let currentName;
let deletedName = "";
let hasProduct;

/**
 * Переменные для добавления характеристик
 */
let currentCategoryIdAdd;
let currentCategoryNameEdit;
let selectedCategory;
let characteristicsSelectedCategory;

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
    /**
     * Заполнение отдельной вкладки "Категории"
     */
    fillProductCategories().then();
    $('#addMainCategoryButton').click(function (event) {
        addNewMainCategory();
        event.preventDefault();
    });
    $('#jqxbutton').click(function (event) {
        prefillMagicModal();
        event.preventDefault();
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

//Метод для попробовать, убрать
function vizovRenderaHarakteristik() {
    let selectedCat = $('#jqxTreeHere').jqxTree('getSelectedItem');
    if (!selectedCat) {
        toastr.error("Категория не выбрана!")
        return false;
    } else {
        for (let z = 0; z < listOfAll.length; z++) {
            let currItem = listOfAll[z];
            if (selectedCat.label.localeCompare(currItem.text) === 0) {
                selectedCategory = currItem.id;
            }
        }
    }
    fetchCharacteristicsAndRenderFields(selectedCategory);
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
 * функция очистки формы характеристик нового продукта
 */
function clearCharacteristicForm() {
    $('#addCharacteristicForm')[0].remove();
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
                        fetchToAddCharacteristics($('#addProduct').val());
                        clearCharacteristicForm();
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
 * функция делает fetch запрос на рест контроллер, отправляя заполненные характеристики и имя товара
 */
function fetchToAddCharacteristics(addedProductName) {
    fetch("/manager/characteristics/addCharacteristics/" + addedProductName, {
        method: 'POST',
        headers: {'Content-Type': 'application/json;charset=utf-8'},
        body: JSON.stringify(createProductCharacteristicArray(characteristicsSelectedCategory))
    })
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
 * функция рендера полей характеристик(для выбранной категории)
 * @param characteristics
 */
function renderCharacteristicFields(characteristics) {
    characteristicsSelectedCategory = characteristics;
    let div = $('#addCharacteristicForm')
    for (let i = 0; i < characteristics.length; i++) {
        const characteristic = characteristics[i];
        let characteristicName = characteristic.characteristicName;
        let characteristicId = characteristic.id;
        div.append(`
                    <div id="add-${characteristicId}-form-group" class="form-group">
                    <label for="add${characteristicId}">${characteristicName}:</label>
                    <input type="text" id="add${characteristicId}" name="${characteristicName}"
                        placeholder="${characteristicName}"
                        class="form-control">
                    </div>
    
    `)
    }
}

/**
 * функция создания массива с характеристиками добавляемого объекта
 * @param characteristics
 */
function createProductCharacteristicArray(characteristics) {
    let arr = [];

    for (let i = 0; i < characteristics.length; i++) {
        const characteristic = characteristics[i];
        let productCharacteristics = {
            characteristicId: characteristic.id,
            value: $('#add' + characteristic.id).val(),
        }
        arr.push(productCharacteristics);
    }
    return arr;
}

/**
 * функция делает fetch запрос на рест контроллер, преобразует полученный объект в json
 * и передает функции рендера полей характеристик renderCharacteristicFields
 */
function fetchCharacteristicsAndRenderFields(categoryId) {
    fetch("/manager/characteristics/" + categoryId)
        .then(response => response.json())
        .then(characteristics => renderCharacteristicFields(characteristics))
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

/**
 *  Ниже методы, из файла managerProductCategoriesCRUD
 *  Для отдельной вкладка Категории товаров
 */


/**
 * Метод построение дерева категорий
 */
async function fillProductCategories() {
    $('jqxTree').empty();
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
    $('#jqxTree').jqxTree({
        source: source
    });
}

/**
 * Метод добавления корневой категории
 */
function addNewMainCategory() {
    let newMain = '#addNewMainCategory';
    let newDepth = 1;
    let newParentCategory = 0;
    postCategory(newMain, newDepth, newParentCategory)
}

/**
 * Метод добавления подкатегории
 */
function addNewSubCategory() {
    let newSub = '#addSubCategory';
    let subDepth = currentDepth + 1;
    let subParentCategory = currentId;
    postCategory(newSub, subDepth, subParentCategory);
}

/**
 * Fetch-запрос на добавление категории
 */
function postCategory(whatever, dep, pCat) {
    if ($(whatever).val() !== '' && checkIfNotExists(whatever)) {
        fetch(API_CATEGORIES_URL, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                category: $(whatever).val(),
                depth: dep,
                parentCategoryId: pCat
            })
        })
            .then(response => {
                if (response.status === 201) {
                    fillProductCategories().then();
                    $(whatever).val("");
                    toastr.success("Категория добавлена");
                }
            })
    } else {
        toastr.error('Такая категория уже есть, либо поле не заполнено!');
    }
}

/**
 * Запрос на обновление имени категории
 */
function updateCategory() {
    if ($('#editCategoryInput').val() !== '' && checkIfNotExists('#editCategoryInput')) {
        fetch(API_CATEGORIES_URL, {
            method: "PUT",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                id: currentId,
                category: $('#editCategoryInput').val(),
                depth: currentDepth,
                parentCategoryId: currentParentId
            })
        })
            .then(response => {
                if (response.status === 200) {
                    toastr.success('Категория обновлена');
                    fillProductCategories().then();
                    $('#editCategoryInput').val("");
                }
            })
    } else {
        toastr.error('Такая категория уже есть, либо поле не заполнено!');
    }
}

/**
 * Запрос на удаление категории
 */
function deleteSubCategory() {
    fetch(API_CATEGORIES_URL + currentId, {
        method: "DELETE"
    })
        .then(response => {
            if (response.status === 200) {
                toastr.success('Удалено');
                deletedName = currentName;
                fillProductCategories().then();
            }
        })
}

/**
 * Проверка "оригинальности" названия категории
 */
function checkIfNotExists(value) {
    for (let j = 0; j < listOfAll.length; j++) {
        if (listOfAll[j].text
            .toLowerCase()
            .localeCompare($(value)
                .val()
                .toLowerCase()) === 0) {
            return false;
        }
    }
    return true;
}

/**
 * Проверка наличия подкатегорий
 */
function checkForSubcategories(value) {
    for (let k = 0; k < listOfAll.length; k++) {
        if (listOfAll[k].parentId === value) {
            return true;
        }
    }
    return false;
}

/**
 * Предзаполнение модального окна magicModal
 */
function prefillMagicModal() {
    let name = $('#jqxTree').jqxTree('getSelectedItem');
    if (!name || (deletedName.localeCompare(name.label) === 0)) {
        $('#magicButtonModalLabel').empty().append('Hmmmm...');
        $('#magicButtonModalBody').empty().append('Looks like no selected category found!');
        $('#deleteSubCategoryButtonDiv').empty();
    } else {
        findByNameAndSetGlobalIds(name);
    }
}

/**
 * Заполнение глобальных переменных для magicModal
 */
function findByNameAndSetGlobalIds(name) {
    for (let x = 0; x < listOfAll.length; x++) {
        let thisItem = listOfAll[x];
        if (name.label.localeCompare(thisItem.text) === 0) {
            currentId = thisItem.id;
            currentParentId = thisItem.parentId;
            currentDepth = thisItem.depth;
            currentName = name.label;
            hasProduct = thisItem.hasProduct;
            fillMagicModal();
            break;
        }
    }
}

/**
 * Заполнение модального окна magicModal
 */
function fillMagicModal() {
    $('#magicButtonModalLabel').empty().append('Выберите действие для \"' + currentName + '\"');
    if (checkForSubcategories(currentId) || hasProduct) {
        $('#deleteSubCategoryButtonDiv').empty()
            .append(`    <button type="button" class="btn btn-outline-danger"
                     id="deleteSubCategoryButton" data-dismiss="modal" disabled>Категория не пуста -> удаление невозможно</button>`);
        ;
    } else {
        $('#deleteSubCategoryButtonDiv').empty()
            .append(`    <button type="button" class="btn btn-danger" onclick="deleteSubCategory()" data-dismiss="modal">Удалить категорию</button>`);
    }
    let body = `<table class="table m-0 p-0 " style="width: 100%;" >
                    <tbody>
                        <tr>
                            <td><label for="addSubCategory">Добавить подкатегорию:</label>
                                <input type="text" class="form-control m-0" name="subcategoryinput" id="addSubCategory"></td>
                            <td><button type="button" class="btn btn-primary mt-4" onclick="addNewSubCategory()"
                                        data-dismiss="modal">Добавить</button></td>
                        </tr>
                        <tr>
                        <td><label for="editCategoryInput">Введите новое название:</label>
                        <input type="text" class="form-control m-0" name="editsubcategoryinput" id="editCategoryInput" value="${currentName}"></td>
                        <td><button type="button" class="btn btn-primary mt-4" onclick="updateCategory()"
                                    data-dismiss="modal">Изменить</button></td>
                        </tr>
                    </tbody>
               </table>
    `;
    $('#magicButtonModalBody').empty().append(body);
}

/**
 *  Конец методов из файла managerProductCategorieCRUD
 *  Для отдельной вкладка Категории товаров
 */
