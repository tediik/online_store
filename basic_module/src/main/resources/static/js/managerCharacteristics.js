let headers = new Headers()
headers.append('Content-type', 'application/json; charset=UTF-8')
document.getElementById('addBtn').addEventListener('click', handleAddBtn)

fetchCharacteristicsAndRenderTable()


/**
 * функция делает fetch запрос на рест контроллер,получает все характеристики, преобразует полученный объект в json
 * и передает функции рендера таблицы renderCharacteristicsTable
 */
function fetchCharacteristicsAndRenderTable() {
    fetch("/manager/characteristics/allCharacteristics")
        .then(response => response.json())
        .then(characteristics => renderCharacteristicsTable(characteristics))
}

/**
 * функция делает активной таблицу с харакетристиками
 * и обновляет в ней данные
 */
function showAndRefreshHomeTab() {
    fetchCharacteristicsAndRenderTable()
    $('#nav-characteristics').addClass('tab-pane fade active show')
    $('#nav-addCharacteristics').removeClass('active show')
    $('#nav-addCharacteristics-tab').removeClass('active')
    $('#nav-characteristics').addClass('active')
}

/**
 * функция рендера таблицы характеристик
 * @param characteristics
 */
function renderCharacteristicsTable(characteristics) {
    let table = $('#characteristic-table')
    table.empty()
        .append(`<tr class="d-flex">
                 <th class="col-1">ID<th/>
                 <th class="col-3">Наименование характеристики</th>
                 <th class="col-1">Изменить</th>
                 <th class="col-1">Удалить</th>
                 </tr>`)
    for (let i = 0; i < characteristics.length; i++) {
        const characteristic = characteristics[i];
        let row = `
                <tr class="d-flex" id="tr-${characteristic.id}">
                    <td class="col-sm-1">${characteristic.id}</td>
                    <td class="col-sm-3">${characteristic.characteristicName}</td>
                    <td class="col-sm-1">
            <!-- Buttons of the right column of main table-->
                        <button data-characteristic-id="${characteristic.id}" type="button" class="btn btn-success edit-button" data-toggle="modal" data-target="#characteristicModalWindow">
                        Изменить
                        </button>
                    </td>
                    <td class="col-sm-1">
                        <button data-characteristic-id="${characteristic.id}" type="button" class="btn btn-danger delete-button" data-toggle="modal" data-target="#characteristicModalWindow">
                        Удалить
                        </button>
                    </td>
                </tr>
        `;
        table.append(row)
    }
    $('.edit-button').click(handleEditButton)
    $('.delete-button').click(handleDeleteButton)
}

/**
 * Функция обраотки нажатия кнопки Edit в таблице характеристик
 * @param event
 */
function handleEditButton(event) {
    const characteristicId = event.target.dataset["characteristicId"]
    fetch("/manager/characteristic/" + characteristicId)
        .then(response => response.json())
        .then(characteristicToEdit => editModalWindowRender(characteristicToEdit))

}

/**
 * Функция обработки нажатия кнопки Delete в таблице характеристик
 * @param event
 */
function handleDeleteButton(event) {
    const characteristicId = event.target.dataset["characteristicId"]
    fetch("/manager/characteristic/" + characteristicId)
        .then(response => response.json())
        .then(characteristicToDelete => deleteModalWindowRender(characteristicToDelete))
}

/**
 * Функция рендера модального окна Edit characteristic
 * @param characteristicToEdit
 */
function editModalWindowRender(characteristicToEdit) {
    $('.modal-dialog').off("click").on("click", "#acceptButton", handleAcceptButtonFromModalWindow)
    $('.modal-title').text("Изменение характеристики")
    $('#idInputModal').val(characteristicToEdit.id)
    $('#acceptButton').text("Сохранить изменения").removeClass().toggleClass('btn btn-success edit-characteristic')
    $('#characteristicInputModal').val(characteristicToEdit.characteristicName).prop('readonly', false)

}

/**
 * Функция рендера модального окна Delete characteristic
 * @param characteristicToDelete
 */
function deleteModalWindowRender(characteristicToDelete) {
    $('.modal-dialog').off("click").on("click", "#acceptButton", handleAcceptButtonFromModalWindow)
    $('.modal-title').text("Удаление характеристики")
    $('#acceptButton').text("Удалить").removeClass().toggleClass('btn btn-danger delete-characteristic')
    $('#idInputModal').val(characteristicToDelete.id)
    $('#characteristicInputModal').val(characteristicToDelete.characteristicName).prop('readonly', true)
}

/**
 * функция обработки нажатия кнопки accept в модальном окне
 * @param event
 */
function handleAcceptButtonFromModalWindow(event) {
    const characteristic = {
        id: $('#idInputModal').val(),
        characteristicName: $('#characteristicInputModal').val(),
    };
    if (!characteristic.characteristicName) {
        let characteristicName = document.getElementById('characteristicInputModal');

        characteristicName.focus();
        toastr.error('Заполните поле наименования харакетристики');
    }
    /**
     * Проверка кнопки delete или edit
     */
    if ($('#acceptButton').hasClass('delete-characteristic')) {
        fetch("/manager/characteristics/" + characteristic.id, {
            method: 'DELETE'
        }).then(response => response.text())
            .then(deletedCharacteristic => console.log('Characteristic: ' + deletedCharacteristic + ' was successfully deleted'))
            .then($('#tr-' + characteristic.id).remove())
        $('#characteristicModalWindow').modal('hide')
        toastr.success("Характеристика успешно удалена")
    } else {
        fetch("/manager/characteristics", {
            method: 'PUT',
            headers: headers,
            body: JSON.stringify(characteristic)
        }).then(function (response) {
            if (response.ok) {
                fetchCharacteristicsAndRenderTable()
                $('#characteristicModalWindow').modal('hide')
                toastr.success("Характеристика успешно изменена")
            }
        })
    }
}

/**
 * функция обработки кнопки add на форме новой характеристики
 */
function handleAddBtn() {
    let characteristicToAdd = {
        characteristicName: $('#addCharacteristicName').val(),
    }

    /**
     * функция очистки полей формы новой харакетристики
     */
    function clearFormFields() {
        $('#addForm')[0].reset();
    }

    /**
     * проверяем, что наименование, цена продукта и количество заполнены
     */

    if (!characteristicToAdd.characteristicName) {
        let confirmName = document.getElementById('addCharacteristicName');

        confirmName.focus();
        toastr.error('Заполните поле наименования характеристкии');
    }

    fetch("/rest/characteristics/addCharacteristic", {
        method: 'POST',
        headers: {'Content-Type': 'application/json;charset=utf-8'},
        body: JSON.stringify(characteristicToAdd)
    })
        .then(function (response) {
                if (response.status !== 200) {
                    response.text()
                        .then(
                            function (text) {
                                toastr.error("Харакетристика не добавлена")

                                console.log(text)
                            })
                } else {
                    response.text().then(function () {
                        clearFormFields();
                        showAndRefreshHomeTab();
                        toastr.success('Харакетристика успешно добавлена')
                    })
                }
            }
        )
}

/**
 *  Функции для отдельной вкладки характеристики по категориям
 */


