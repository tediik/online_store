const API_URL = "/api/categories/";
let listOfAll;
let idGlobal;
let parentidGlobal;
let depthGlobal;

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

$('#jqxbutton').click(function () {
    let name = $('#jqxTree').jqxTree('getSelectedItem');
    findByNameAndSetGlobalIds(name);
});

/**
 * Запрос на добавление корневой категории
 */
function addNewMainCategory() {
    if ($('#addNewMainCategory').val() !== '' && checkIfNotExists()) {
        fetch(API_URL, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                category: $('#addNewMainCategory').val(),
                depth: 1,
                parentCategoryId: 0
            })
        })
            .then(response => {
                if (response.status === 201) {
                    fillProductCategories().then();
                    $('#addNewMainCategory').val("");
                }
            })
    } else {
        toastr.error('Такая категория уже есть, либо поле не заполнено!');
    }
}

function checkIfNotExists() {
    for (let pos = 0; pos < listOfAll.length; pos++) {
        if (listOfAll[pos].text.localeCompare($('#addNewMainCategory').val()) === 0) {
            return false;
        }
    }
    return true;
}

function findByNameAndSetGlobalIds(name) {
    for (let find = 0; find < listOfAll.length; find++) {
        let thisItem = listOfAll[find];
        if (name.label.localeCompare(thisItem.text) === 0) {
            idGlobal = thisItem.id;
            parentidGlobal = thisItem.parentid;
            depthGlobal = thisItem.depth;
            break;
        }
    }
}