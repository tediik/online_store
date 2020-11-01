// документация к jqxtree -
// https://www.jqwidgets.com/jquery-widgets-documentation/documentation/jqxtree/jquery-tree-getting-started.htm?search=
const API_URL = "/api/categories/";
const addSubCategoryButton = 'addSubCategoryButton';
let listOfAll;
let idGlobal;
let parentidGlobal;
let depthGlobal;
let nameGlobal;
let hasProduct;

$(function () {
    fillProductCategories().then();

    $('#addMainCategoryButton').click(function (event) {
        addNewMainCategory();
        event.preventDefault();
    })

    $('#deleteSubCategoryButton').click(function (event) {
        alert("Точно удаляем?!");
        event.preventDefault();
    })

    $('#jqxbutton').click(function (event) {
        prefillMagicModal();
        event.preventDefault();
    })

    $('#' + addSubCategoryButton).click(function (event) {
        console.log("event checking")
        addNewSubCategory();
        event.preventDefault();
    })

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
        let parentid = item.parentid;
        let id = item.id;
        if (items[parentid]) {
            let tmpitem = { parentid: parentid, label: label, item: item };
            if (!items[parentid].items) {
                items[parentid].items = [];
            }
            items[parentid].items[items[parentid].items.length] = tmpitem;
            items[id] = tmpitem;
        }
        else {
            items[id] = { parentid: parentid, label: label, item: item };
            source[id] = items[id];
        }
    }
    $('#jqxTree').jqxTree({ source: source });
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
    console.log("new subcat adding")
    let newSub = '#addSubCategory';
    let subDepth = depthGlobal + 1;
    let subParentCategory = idGlobal;
    postCategory(newSub, subDepth, subParentCategory);
}

/**
 * Fetch-запрос на добавление категории
 */
function postCategory (url, dep, pCat) {
    if ($(url).val() !== '' && checkIfNotExists(url)) {
        fetch(API_URL, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                category: $(url).val(),
                depth: dep,
                parentCategoryId: pCat
            })
        })
            .then(response => {
                if (response.status === 201) {
                    fillProductCategories().then();
                    $(url).val("");
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
    for (let pos = 0; pos < listOfAll.length; pos++) {
        if (listOfAll[pos].text.localeCompare($(value).val()) === 0) {
            return false;
        }
    }
    return true;
}

/**
 * Проверка наличия подкатегорий
 */
function checkForSubcategories(value) {
    for (let subCat = 0; subCat < listOfAll.length; subCat++) {
        if (listOfAll[subCat].parentid === value) {return true;}
    }
    return false;
}

/**
 * Заполнение глобальных переменных для magicModal
 */
function findByNameAndSetGlobalIds(name) {
    for (let find = 0; find < listOfAll.length; find++) {
        let thisItem = listOfAll[find];
        if (name.label.localeCompare(thisItem.text) === 0) {
            idGlobal = thisItem.id;
            parentidGlobal = thisItem.parentid;
            depthGlobal = thisItem.depth;
            nameGlobal = name.label;
            hasProduct = thisItem.hasProduct;
            fillMagicModal();
            break;
        }
    }
}

/**
 * Предзаполнение модального окна magicModal
 */
function prefillMagicModal() {
    let name = $('#jqxTree').jqxTree('getSelectedItem');
    if (!name) {
        $('#magicButtonModalLabel').empty().append('Hmmmm...');
        $('#magicButtonModalBody').empty().append('Looks like no selected category found!');
        $('#deleteSubCategoryButtonDiv').empty();
    } else findByNameAndSetGlobalIds(name);
}

/**
 * Заполнение модального окна magicModal
 */
function fillMagicModal() {
    $('#magicButtonModalLabel').empty().append('Выберите действие для \"' + nameGlobal + '\"');
    if (checkForSubcategories(idGlobal) || hasProduct) {
        $('#deleteSubCategoryButtonDiv').empty()
        .append(`    <button type="button" class="btn btn-outline-danger"
                     id="deleteSubCategoryButton" data-dismiss="modal" disabled>Категория не пуста -> удаление невозможно</button>`);;
    } else {
        $('#deleteSubCategoryButtonDiv').empty()
        .append(`    <button type="button" class="btn btn-danger"
                     id="deleteSubCategoryButton" data-dismiss="modal">Удалить категорию</button>`);
    }
    let body =`<table class="table m-0 p-0 " style="width: 100%;" >
                    <tbody>
                        <tr>
                            <td><label for="addSubCategory">Добавить подкатегорию:</label>
                                <input type="text" class="form-control m-0" name="subcategoryinput" id="addSubCategory"></td>
                            <td><button type="button" class="btn btn-primary mt-4" onclick="addNewSubCategory()" id="${addSubCategoryButton}"
                                        data-dismiss="modal">Добавить</button></td>
                        </tr>
                        <tr>
                        <td><label for="editSubCategory">Введи новое название:</label>
                        <input type="text" class="form-control m-0" name="editsubcategoryinput" id="editSubCategory" value="${nameGlobal}"></td>
                        <td><button type="button" class="btn btn-primary mt-4" id="editCategoryButton"
                            data-dismiss="modal">Изменить</button></td>
                        </tr>
                    </tbody>
               </table>
    `;
    $('#magicButtonModalBody').empty().append(body);
}