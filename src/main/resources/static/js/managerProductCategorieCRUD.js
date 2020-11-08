// документация к jqxtree -
// https://www.jqwidgets.com/jquery-widgets-documentation/documentation/jqxtree/jquery-tree-getting-started.htm?search=
const API_URL = "/api/categories/";
const addSubCategoryButton = 'addSubCategoryButton';
let listOfAll;
let currentId;
let currentParentId;
let currentDepth;
let currentName;
let deletedName = "";
let hasProduct;

$(function () {
    fillProductCategories().then();
    $('#addMainCategoryButton').click(function (event) {
        addNewMainCategory();
        event.preventDefault();
    });
    $('#jqxbutton').click(function (event) {
        prefillMagicModal();
        event.preventDefault();
    });
})

// build hierarchical structure
async function fillProductCategories() {
    $('jqxTree').empty();
    listOfAll = await fetch(API_URL + "all").then(response => response.json());
    let source = [];
    let items = [];
    for (let i = 0; i < listOfAll.length; i++) {
        let item = listOfAll[i];
        let label = item.text;
        let thisParentId = item.parentId;
        let id = item.id;
        if (items[thisParentId]) {
            let tmpItem = { parentId: thisParentId, label: label, item: item };
            if (!items[thisParentId].items) {
                items[thisParentId].items = [];
            }
            items[thisParentId].items[items[thisParentId].items.length] = tmpItem;
            items[id] = tmpItem;
        }
        else {
            items[id] = { parentId: thisParentId, label: label, item: item };
            source[id] = items[id];
        }
    }
    $('#jqxTree').jqxTree({
        source: source
    });
}

/**
 * Запрос на добавление корневой категории
 */
function addNewMainCategory() {
    let newMain = '#addNewMainCategory';
    let newDepth = 1;
    let newParentCategory = 0;
    postCategory(newMain, newDepth, newParentCategory)
}

/**
 * Запрос на добавление подкатегории
 */
function addNewSubCategory() {
    let newSub = '#addSubCategory';
    let subDepth = currentDepth + 1;
    let subParentCategory = currentId;
    postCategory(newSub, subDepth, subParentCategory);
}

/**
 * Запрос на обновление имени категории
 */
function updateCategory() {
    if ($('#editCategoryInput').val() !== '' && checkIfNotExists('#editCategoryInput')) {
        fetch(API_URL, {
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
    fetch(API_URL + currentId, {
        method: "DELETE"
    })
        .then(response => {
            if (response.status === 200) {
                toastr.success('Удалено!');
                deletedName = currentName;
                fillProductCategories().then();
            }
        })
}

/**
 * Fetch-запрос на добавление категории
 */
function postCategory (whatever, dep, pCat) {
    if ($(whatever).val() !== '' && checkIfNotExists(whatever)) {
        fetch(API_URL, {
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
                }
            })
    } else {
        toastr.error('Такая категория уже есть, либо поле не заполнено!');
    }
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
                     id="deleteSubCategoryButton" data-dismiss="modal" disabled>Категория не пуста -> удаление невозможно</button>`);;
    } else {
        $('#deleteSubCategoryButtonDiv').empty()
        .append(`    <button type="button" class="btn btn-danger" onclick="deleteSubCategory()" data-dismiss="modal">Удалить категорию</button>`);
    }
    let body =`<table class="table m-0 p-0 " style="width: 100%;" >
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