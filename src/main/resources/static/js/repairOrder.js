$(document).ready(function () {
    /*Слушатель для добавления заказа на ремонт*/
    document.getElementById('buttonAddRepairOrder').addEventListener('click', addRepairOrder)
    /*Слушатель контролирующий нажатие на вкладки статусов заказа*/
    document.getElementById('navbarRepairOrders').addEventListener('click', checkUpperNavButtonRepairOrder)
    /*Слушатель проверяющий редактирование или удаление было нажато на заказе*/
    document.getElementById('repairOrdersTabContent').addEventListener('click', checkButtonEditOrDelete)
    /*Слушатель на вызов модального окна для редактирования заказа*/
    document.getElementById('editSave').addEventListener('click', updateRepairOrder)
    /*Слушатель на нажатие по вкладке Заказы*/
    document.getElementById('getRepairOrdersNav').addEventListener('click', function () {
        getAllRepairOrders()
    })
});

/**
 * Формирует заказ и присваивает уникальный номер
 * @returns Заказ на ремонт
 */
function formOrder() {
    let order = {
        fullNameClient: document.getElementById('fullNameClient').value,
        telephoneNumber: document.getElementById('telephoneNumber').value,
        nameDevice: document.getElementById('nameDevice').value,
        guarantee: document.getElementById('guaranteeCheckbox').checked,
        fullTextProblem: document.getElementById('fullTextProblem').value,
        orderNumber: ""
    }
    order.orderNumber += order.guarantee ? "Y" : "N";
    order.orderNumber += new Date().getUTCMilliseconds();
    order.orderNumber += order.telephoneNumber.slice(order.telephoneNumber.length - 2, order.telephoneNumber.length);
    return order;
}

/**
 * Функция добавления заявки на ремонт
 */
function addRepairOrder() {
    if (checkFieldsAddRepairOrder()) {
        let order = formOrder();
        fetch('/service/addRepairOrder', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-type': 'application/json; charset=UTF-8'
            },
            body: JSON.stringify(order)
        }).then(function (response) {
            if (response.status === 200) {
                toastr.success("Заявка успешно добавлена.");
                getWorkOrder(order);
                $('#fullTextProblem').summernote('code', '')
                document.getElementById('fullNameClient').value = ''
                document.getElementById('telephoneNumber').value = ''
                document.getElementById('nameDevice').value = ''
                document.getElementById('guaranteeCheckbox').checked = false
            } else {
                toastr.error("Заявка не была добавлена. Проверьте корректность ввода данных.", {timeOut: 3000});
            }
        })
    }
}

/**
 * Функция возвращает файл заказ-наряда
 * @param order заказ на ремонт
 */
function getWorkOrder(order) {
    fetch('/service/getWorkOrder', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-type': 'application/json; charset=UTF-8'
        },
        body: JSON.stringify(order)
    }).then(function (response) {
        if (response.status === 200) {
            response.blob().then(blob => {
                let url = window.URL.createObjectURL(blob);
                let file = document.createElement('a');
                file.href = url;
                file.download = `work_order_${order.orderNumber}.pdf`;
                file.click();
            })
        } else {
            alert("Заказа не найдено")
        }
    })
}

/**
 * Функция проверяет какая вкладка была нажата
 * @param event событие click
 */
function checkUpperNavButtonRepairOrder(event) {
    let tab = event.target.dataset.toggleId
    if (tab === 'allRepairOrders') {
        getAllRepairOrders()
    }
    if (tab === 'acceptedRepairOrders') {
        getAcceptedRepairOrders()
    }
    if (tab === 'diagnosticsRepairOrders') {
        getDiagnosticsRepairOrders()
    }
    if (tab === 'inWorkRepairOrders') {
        getIn_WorkRepairOrders()
    }
    if (tab === 'completeRepairOrders') {
        getCompleteRepairOrders()
    }
    if (tab === 'closedRepairOrders') {
        getArchiveRepairOrders()
    }
    if (tab === 'canceledRepairOrders') {
        getCanceledRepairOrders()
    }
}

/**
 * Функция, которая проверяет что мы нажали на заявке(редактировать или удалить)
 * @param event - событие click
 */
function checkButtonEditOrDelete(event) {
    let orderId = event.target.dataset.orderId
    if (event.target.dataset.toggleId === 'edit') {
        openEditModalWindow(orderId)
    }
    if (event.target.dataset.toggleId === 'delete') {
        deleteRepairOrder(orderId)
    }
}

/**
 * Функция получает все заказы на ремонт
 */
function getAllRepairOrders() {
    fetch('/service/getAllRepairOrder')
        .then((res) => res.json())
        .then((data) => {
            renderRepairOrder(data, 'all_repairOrders')
        })
}

/**
 * Функция получает все принятые заказы на ремонт
 */
function getAcceptedRepairOrders() {
    fetch('/service/getAcceptedRepairOrder')
        .then((res) => res.json())
        .then((data) => {
            renderRepairOrder(data, 'accepted_repairOrders')
        })
}

/**
 * Функция получает все заказы на ремонт, которые находятся на этапе диагностики
 */
function getDiagnosticsRepairOrders() {
    fetch('/service/getDiagnosticsRepairOrder')
        .then((res) => res.json())
        .then((data) => {
            renderRepairOrder(data, 'diagnostics_repairOrders')
        })
}

/**
 * Функция получает все заказы на ремонт, которые находятся на этапе ремонта
 */
function getIn_WorkRepairOrders() {
    fetch('/service/getIn_WorkRepairOrder')
        .then((res) => res.json())
        .then((data) => {
            renderRepairOrder(data, 'inWorkRepairOrders')
        })
}

/**
 * Функция получает все заказы на ремонт, ремонт которых выполнен
 */
function getCompleteRepairOrders() {
    fetch('/service/getCompleteRepairOrder')
        .then((res) => res.json())
        .then((data) => {
            renderRepairOrder(data, 'completeRepairOrders')
        })
}

/**
 * Функция получает все архивные заказы на ремонт
 */
function getArchiveRepairOrders() {
    fetch('/service/getArchiveRepairOrder')
        .then((res) => res.json())
        .then((data) => {
            renderRepairOrder(data, 'closedRepairOrders')
        })
}

/**
 * Функция получает все отмененные заказы на ремонт
 */
function getCanceledRepairOrders() {
    fetch('/service/getCanceledRepairOrder')
        .then((res) => res.json())
        .then((data) => {
            renderRepairOrder(data, 'canceledRepairOrders')
        })
}

/**
 * Функция проверяет заполнены ли поля в модальном окне
 * @returns {boolean}
 */
function checkFieldsModalRepairOrder() {
    if (document.getElementById('fullNameClientUpdate').value === '') {
        invalidModalField('Введите имя клиента!', document.getElementById('fullNameClientUpdate'))
        return false
    } else if (document.getElementById('telephoneNumberUpdate').value === '') {
        invalidModalField('Введите номер телефона!', document.getElementById('telephoneNumberUpdate'))
        return false
    } else if (document.getElementById('nameDeviceUpdate').value === '') {
        invalidModalField('Введите название устройства!', document.getElementById('nameDeviceUpdate'))
        return false
    }
    return true
}

/**
 * Функция проверяет заполнены ли поля в карточке добавления заявки на ремонт
 * @returns {boolean}
 */
function checkFieldsAddRepairOrder() {
    if (document.getElementById('fullNameClient').value === '') {
        invalidAddRepairOrderField('Введите имя клиента!', document.getElementById('fullNameClient'))
        return false
    } else if (document.getElementById('telephoneNumber').value === '') {
        invalidAddRepairOrderField('Введите номер телефона!', document.getElementById('telephoneNumber'))
        return false
    } else if (document.getElementById('nameDevice').value === '') {
        invalidAddRepairOrderField('Введите название устройства!', document.getElementById('nameDevice'))
        return false
    }
    return true
}

/**
 * Функция вызова и заполнения модального окна редактирования заказа на ремонт
 * @param repairOrderId - идентификатор Long id заказа на ремонт
 */
function openEditModalWindow(repairOrderId) {
    fetch(`/service/${repairOrderId}`)
        .then(function (response) {
            if (response.status === 200) {
                response.json().then(repairOrder => {
                    $('#idRepairOrderUpdate').val(repairOrder.id);
                    $('#fullNameClientUpdate').val(repairOrder.fullNameClient);
                    $('#telephoneNumberUpdate').val(repairOrder.telephoneNumber);
                    $('#nameDeviceUpdate').val(repairOrder.nameDevice);
                    $('#fullTextProblemUpdate').summernote('code', repairOrder.fullTextProblem);
                    $("#postingDateUpdate").val(moment(repairOrder.acceptanceDate).format("yyyy-MM-DD"));
                    $('#guaranteeCheckboxUpdate').prop('checked', repairOrder.guarantee);
                    $('#statusRepairOrderSelect').val(repairOrder.repairOrderType);
                })
            } else {
                console.log('RepairOrder not found')
            }
        })
}

/**
 * Функция редактирования заказа на ремонт
 */
function updateRepairOrder() {
    let repairOrder = {
        id: document.getElementById('idRepairOrderUpdate').value,
        fullNameClient: document.getElementById('fullNameClientUpdate').value,
        telephoneNumber: document.getElementById('telephoneNumberUpdate').value,
        nameDevice: document.getElementById('nameDeviceUpdate').value,
        guarantee: document.getElementById('guaranteeCheckboxUpdate').checked,
        repairOrderType: document.getElementById('statusRepairOrderSelect').value,
        fullTextProblem: document.getElementById('fullTextProblemUpdate').value,
        acceptanceDate: document.getElementById('postingDateUpdate').value,
    }
    if (checkFieldsModalRepairOrder()) {
        fetch('/service/updateRepairOrder', {
            method: 'PUT',
            headers: {
                'Accept': 'application/json, text/plain, */*',
                'Content-type': 'application/json; charset=UTF-8'
            },
            body: JSON.stringify(repairOrder)
        }).then(function (response) {
            if (response.status === 200) {
                $('#editRepairOrder').modal('hide')
                getAllRepairOrders()
                getAcceptedRepairOrders()
                getDiagnosticsRepairOrders()
                getIn_WorkRepairOrders()
                getCompleteRepairOrders()
                getArchiveRepairOrders()
                getCanceledRepairOrders()
                toastr.success("Обновление заявки прошло успешно.");
            } else {
                toastr.error("Обновить заявку не удалось. Проверьте корректность ввода данных.", {timeOut: 3000});
            }
        })
    }
}

/**
 * Функция удаления заказа
 * @param repairOrderId идентифактор заказа
 */
function deleteRepairOrder(repairOrderId) {
    let doDelete = confirm(`Вы уверены что хотите удалить заказ?`);
    if (doDelete) {
        fetch(`/service/${repairOrderId}`, {
            method: 'DELETE'
        }).then(function (response) {
            if (response.status === 200) {
                $('#div-' + repairOrderId).remove()
                toastr.success("Заявка была удалена.");
            } else {
                console.log('Orders not found')
                toastr.error("Заявка не была удалена.");
            }
        })
    }
}

/**
 * Функция задает фон заказа на ремонт взависимости от статуса
 * @param status статус заказа
 * @returns {string} фон заказа
 */
function colorDiv(status) {
    if (status === "ACCEPTED") {
        return "alert-primary";
    }
    if (status === "DIAGNOSTICS") {
        return "alert-info";
    }
    if (status === "IN_WORK") {
        return "alert-warning";
    }
    if (status === "COMPLETE") {
        return "alert-success";
    }
    if (status === "CANCELED") {
        return "alert-danger";
    }
    return "alert-secondary";
}

/**
 * Функция отрисовки заявок на ремонт
 * @param data - переданные заявки на ремонт
 * @param elementId указатель вкладки где происходит отрисовка
 */
function renderRepairOrder(data, elementId) {
    let viewRepairOrder = '';
    data.forEach(function (repairOrder) {
        let modifiedDateRepairOrder = ''
        if (repairOrder.modifiedDate !== null) {
            modifiedDateRepairOrder = `Дата последнего изменения: ` + moment(repairOrder.modifiedDate).format("yyyy-MM-DD")
        }

        viewRepairOrder += `<div id="div-${repairOrder.id}" class="alert ${colorDiv(repairOrder.repairOrderType)} mt-2">
                        <h5 id="numberOrderRender">№${repairOrder.id}</h5>  
                        <h6 id="fullNameClientRender">${repairOrder.fullNameClient}</h6>
                        <h6 id="telephoneNumberRender">${repairOrder.telephoneNumber}</h6>
                        <h6 id="nameDeviceRender">${repairOrder.nameDevice}</h6>
                        <h4 id="repairOrderTypeRender">${repairOrder.repairOrderType}</h4>
                        <p id="fullTextProblemRender">${repairOrder.fullTextProblem}</p>
                        <div class="container-fluid row" id="divContainer">
                            <div class="text-left align-text-bottom col" id="divSpan">
                                <span id="postingDateRender">Дата приема заказа: ${repairOrder.acceptanceDate}</span>
                                <br>
                                <span id="modifiedDateRender">${modifiedDateRepairOrder}</span>
                            </div>
                            <div class="text-right col" id="divButtons">
                                <button type="button" data-toggle="modal" class="btn btn-primary"  id="btn_edit_repairOrder" data-target="#editRepairOrder" data-toggle-id="edit" data-order-id="${repairOrder.id}">Редактировать</button>
                                <button type="button" data-toggle="modal" class="btn btn-danger"  id="btn_delete_repairOrder" data-toggle-id="delete" data-order-id="${repairOrder.id}" >Удалить</button>
                            </div>
                        </div>
                    </div>`
    });
    document.getElementById(elementId).innerHTML = viewRepairOrder;
}

/**
 * Функция выводящая предупреждение в случае если поля модального окна редактирования не заполнены
 * @param text текст сообщения
 * @param focusField поле для фокуса
 */
function invalidModalField(text, focusField) {
    document.getElementById('modal-alert-repairOrder')
        .innerHTML = `<div id="alert-repairOrder" class="alert alert-danger alert-dismissible fade show" role="alert">
                        <strong>${text}</strong>
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                        </button>
                      </div>`
    focusField.focus()
    window.setTimeout(function () {
        $('#alert-repairOrder').alert('close')
    }, 3000)
}

/**
 * Функция выводящая предупреждение когда поля при добавлении заказа не заполнены
 * @param text текст сообщения
 * @param focusField поле для фокуса
 */
function invalidAddRepairOrderField(text, focusField) {
    document.getElementById('modal-alert-addRepairOrder')
        .innerHTML = `<div id="alert-addRepairOrder" class="alert alert-danger alert-dismissible fade show" role="alert">
                        <strong>${text}</strong>
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                        </button>
                      </div>`
    focusField.focus()
    window.setTimeout(function () {
        $('#alert-addRepairOrder').alert('close')
    }, 3000)
}
