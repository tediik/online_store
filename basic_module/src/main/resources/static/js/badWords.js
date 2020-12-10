let badWordsRestUrl = "/rest/bad-words/"
let headers = new Headers()
headers.append('Content-type', 'application/json; charset=UTF-8')
document.getElementById('addBtnWord').addEventListener('click', handleAddBtn)
document.getElementById('addBtnImport').addEventListener('click', handleImportBtn)

$(function () {
    getBadWords();
});

/**
 * Получаем стоп-слова и ренедерим список
 * @returns {Promise<void>}
 */
async function getBadWords() {
    renderEnabled()
    let url = badWordsRestUrl + "all";
    await fetch(url)
        .then(response => response.json())
        .then(allWords => renderBadWordsTable(allWords))
}

/**
 * Заполняем таблицу со стоп словами
 * @param wordsArray
 */
function renderBadWordsTable(wordsArray) {
    let table = $('#badWordsTable')
    table.empty()
        .append(`<tr>
                <th>ID</th>
                <th>Стоп-слово</th>
                <th>Статус</th>
                <th>Edit</th>
                <th>Delete</th>
              </tr>`)
    for (let i = 0; i < wordsArray.length; i++) {
        const word = wordsArray[i];
        let row = `
                <tr id="tr-${word.id}">
                    <td>${word.id}</td>
                    <td>${word.badword}</td>
                    <td>${word.status}</td>                
                    <td>
                        <button data-product-id="${word.id}" type="button" class="btn btn-success edit-button" data-toggle="modal" data-target="#wordModalWindow">Edit</button>
                    </td>
                    <td>
                        <button data-product-id="${word.id}" type="button" id="action-button-${word.id}" class="btn btn-danger delete-product action" data-target="#wordModalWindow" data-toggle="modal" data-toggle-id="delete">Delete</button>
                    </td>
                </tr>
                `;
        table.append(row)
    }
    $('.edit-button').click(handleEditButton)
    $('.action').click(checkActionButton)
}

/**
 * Функция подготовки обработки нажатия кнопки Delete в таблице стоп-слова
 * @param event
 */
function checkActionButton(event) {
    const wordId = event.target.dataset["productId"]
    handleDeleteButton(wordId)
}

/**
 * Функция обработки нажатия кнопки Edit в таблице стоп-слова
 * @param event
 */
function handleEditButton(event) {
    const wordId = event.target.dataset["productId"]
    fetch(badWordsRestUrl + wordId, {headers: headers})
        .then(response => response.json())
        .then(productToEdit => editModalWindowRender(productToEdit))
}

/**
 * Изменяем кнопку Edit
 * @param word
 */
function editModalWindowRender(word) {
    $('.modal-dialog').off("click").on("click", "#acceptButton", handleAcceptButtonFromModalWindow)
    $('.modal-title').text("Edit word")
    $('#acceptButton').text("Save changes").removeClass().toggleClass('btn btn-success edit-product')
    $('#idInputModal').val(word.id)
    $('#badwordInputModal').val(word.badword).prop('readonly', false)
    $('#statusInputModal').val(word.status).prop('readonly', false)
}

/**
 * Функция обработки нажатия кнопки Delete в таблице стоп-слова
 * @param event
 */
function handleDeleteButton(wordId) {
    fetch(badWordsRestUrl + wordId)
        .then(response => response.json())
        .then(productToDelete => deleteModalWindowRender(productToDelete))
}

/**
 * Изменяем кнопку Delete
 * @param word
 */
function deleteModalWindowRender(wordId) {
    $('.modal-dialog').off("click").on("click", "#acceptButton", handleAcceptButtonFromModalWindow)
    $('.modal-title').text("Delete product")
    $('#acceptButton').text("Delete").removeClass().toggleClass('btn btn-danger delete-product')
    $('#idInputModal').val(wordId.id)
    $('#badwordInputModal').val(wordId.badword).prop('readonly', true)
    $('#statusInputModal').val(wordId.status).prop('readonly', true)
}

/**
 * функция обработки нажатия кнопки accept в модальном окне
 * @param event
 */
function handleAcceptButtonFromModalWindow() {
    const word = {
        id: $('#idInputModal').val(),
        badword: $('#badwordInputModal').val().toLowerCase(),
        status: $('#statusInputModal').val(),
    };

    /**
     * Проверка кнопки delete или edit
     */
    if ($('#acceptButton').hasClass('delete-product')) {
        fetch(badWordsRestUrl + 'delete/' + word.id, {
            headers: headers,
            method: 'DELETE'
        }).then(response => response.text())
            .then(deletedWord => console.log('Word: ' + deletedWord + ' was successfully deleted'))
            .then(showTable => {
                showAndRefreshHomeTab(showTable)
            })
        $('#wordModalWindow').modal('hide')
    } else {
        fetch(badWordsRestUrl + 'update', {
            method: 'PUT',
            headers: headers,
            body: JSON.stringify(word)
        }).then(function (response) {
            $('#wordModalWindow').modal('hide')
            getBadWords();
        })
    }
}

/**
 * Обновление отображения списка
 */
function showAndRefreshHomeTab() {
    getBadWords()
    $('#nav-home-badwords').addClass('tab-pane fade active show')
    $('#nav-profile-badwords').removeClass('active show')
    $('#nav-profile-tab-badwords').removeClass('active')
    $('#nav-badwords-import').removeClass('active show')
    $('#nav-badwords-import-tab').removeClass('active')
    $('#nav-home-tab-badwords').addClass('active')
}

/**
 * функция обработки кнопки add на форме нового стоп-слова
 */
function handleAddBtn() {
    let wordToAdd = {
        badword: $('#addBadWord').val().toLowerCase(),
        status: $('#addBadWordStatus').val(),
    }

    /**
     * функция очистки полей формы
     */
    function clearFormFields() {
        $('#addForm')[0].reset();
    }

    /**
     * проверяем, что все поля заполнены
     */
    if (!wordToAdd.badword || !wordToAdd.status) {

        if (!wordToAdd.badword) {
            let confirmName = document.getElementById('addBadWord');

            confirmName.focus();
            toastr.error('Заполните поле стоп-слово');
        }

        if (!wordToAdd.status) {
            let confirmPrice = document.getElementById('addBadWordStatus');

            confirmPrice.focus();
            toastr.error('Заполните поле статус');
        }

    } else {
        fetch(badWordsRestUrl + 'add', {
            method: 'POST',
            headers: {'Content-Type': 'application/json;charset=utf-8'},
            body: JSON.stringify(wordToAdd)
        })
            .then(function (response) {
                    let field;
                    if (response.status !== 200) {
                        response.text()
                            .then(
                                function (text) {
                                    if (text === "duplicatedWordName") {
                                        toastr.error('Такое слово уже существует');
                                    }
                                })
                    } else {
                        response.text().then(function () {
                            clearFormFields();
                            showAndRefreshHomeTab()
                            toastr.success('Стоп-слово успешно добавлено')
                        })
                    }
                }
            )
    }
}

/**
 * Проверяем включен фильтер, и переключаем чекбокс
 * в нужное положение
 */
function renderEnabled() {
    fetch('/rest/bad-words/status')
        .then((response) => {
            response.json().then((data) => {
                if (data.textValue === 'yes') {
                    $("#activateBadWords").attr('checked', true)
                }
                if (data.textValue === 'no') {
                    $("#activateBadWords").attr('checked', false)
                }
            })
        })
}

/**
 * Проверяем положение чек-бокса, и сохраняем настройки в базу
 * @param check
 */
function toggle(check) {
    if (check.checked) {
        fetch('/rest/bad-words/status', {
            method: 'POST',
            headers: {'Content-Type': 'application/json;charset=utf-8'},
            body: 'yes'
        })
    } else {
        fetch('/rest/bad-words/status', {
            method: 'POST',
            headers: {'Content-Type': 'application/json;charset=utf-8'},
            body: 'no'
        })
    }
    renderEnabled();
}

/**
 * функция обработки кнопки Импорт
 */
function handleImportBtn() {
    /*let wordToAdd = {
        text: $('#addBadWord').val().toLowerCase(),
    }*/
    let wordToAdd = $('#importWordText').val().toLowerCase();

    /**
     * функция очистки полей формы
     */
    function clearFormImport() {
        $('#importWord')[0].reset();
    }

    /**
     * проверяем, что все поля заполнены
     */
    if (!wordToAdd) {
        let confirmName = document.getElementById('importWordText');
        confirmName.focus();
        toastr.error('Заполните поле импорт');
    } else {
        fetch(badWordsRestUrl + 'import', {
            method: 'POST',
            headers: {'Content-Type': 'application/json;charset=utf-8'},
            body: wordToAdd //JSON.stringify(wordToAdd)
        })
            .then(function (response) {
                    let field;
                    if (response.status !== 200) {
                        toastr.error('Ошибка импорта стоп-слова успешно импортированны')
                    } else {
                        response.text().then(function () {
                            clearFormImport();
                            showAndRefreshHomeTab()
                            toastr.success('Стоп-слова успешно импортированны')
                        })
                    }
                }
            )
    }
}