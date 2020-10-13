$(document).ready(function () {
    getAllRepairOrders()

    /*Слушатель для добавления заказа на ремонт*/
    document.getElementById('buttonAddRepairOrder').addEventListener('click', addRepairOrder)
    /*Слушатель на навбар статусов заказа*/
    document.getElementById('navbarRepairOrders').addEventListener('click', checkUpperNavButtonRepairOrder)
    /*Слушатель для проверки редактирование или удаление были нажаты на заказе*/
    document.getElementById('repairOrdersTabContent').addEventListener('click', checkButtonEditOrDelete)
    /*Слушатель на вызов модального окна для редактирования*/
    document.getElementById('editSave').addEventListener('click', updateRepairOrder)
});

/**
 * Функция добавления заявки на ремонт
 */
function addRepairOrder() {
    let repairOrder = {
        fullNameClient: document.getElementById('fullNameClient').value,
        telephoneNumber: document.getElementById('telephoneNumber').value,
        nameDevice: document.getElementById('nameDevice').value,
        guarantee: document.getElementById('guaranteeCheckbox').checked,
        fullTextProblem: document.getElementById('fullTextProblem').value
    }
    fetch('http://localhost:9999/service/addRepairOrder', {
        method: 'POST',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-type': 'application/json; charset=UTF-8'
        },
        body: JSON.stringify(repairOrder)
    }).then(function (response) {
        if (response.status === 200) {

        } else {

        }
    })
}

/**
 * Функция проверяет какая Таба была нажата
 * @param event событие click
 */
function checkUpperNavButtonRepairOrder(event) {
    let tab = event.target.dataset.toggleId
    if (tab === 'allRepairOrders') {
        getAllRepairOrders();
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
}

/**
 * Функция, которая проверяет что мы нажали на заявке(редактировать или удалить)
 * @param event - событие click
 */
function checkButtonEditOrDelete(event) {
    let orderId = event.target.dataset.orderId
    console.log(orderId)
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
    fetch('http://localhost:9999/service/getAllRepairOrder')
        .then((res) => res.json())
        .then((data) => {
            renderRepairOrder(data, 'all_repairOrders')
        })
}

/**
 * Функция получает все принятые заказы на ремонт
 */
function getAcceptedRepairOrders() {
    fetch('http://localhost:9999/service/getAcceptedRepairOrder')
        .then((res) => res.json())
        .then((data) => {
            renderRepairOrder(data, 'accepted_repairOrders')
        })
}

/**
 * Функция получает все заказы на ремонт, которые находятся на этапе диагностики
 */
function getDiagnosticsRepairOrders() {
    fetch('http://localhost:9999/service/getDiagnosticsRepairOrder')
        .then((res) => res.json())
        .then((data) => {
            renderRepairOrder(data, 'diagnostics_repairOrders')
        })
}

/**
 * Функция получает все заказы на ремонт, которые находятся на этапе ремонта
 */
function getIn_WorkRepairOrders() {
    fetch('http://localhost:9999/service/getIn_WorkRepairOrder')
        .then((res) => res.json())
        .then((data) => {
            renderRepairOrder(data, 'inWorkRepairOrders')
        })
}

/**
 * Функция получает все заказы на ремонт, ремонт которых выполнен
 */
function getCompleteRepairOrders() {
    fetch('http://localhost:9999/service/getCompleteRepairOrder')
        .then((res) => res.json())
        .then((data) => {
            renderRepairOrder(data, 'completeRepairOrders')
        })
}

/**
 * Функция получает все архивные заказы на ремонт
 */
function getArchiveRepairOrders() {
    fetch('http://localhost:9999/service/getArchiveRepairOrder')
        .then((res) => res.json())
        .then((data) => {
            renderRepairOrder(data, 'closedRepairOrders')
        })
}

/**
 * Функция вызова и заполнения модального окна редактирования заказа на ремонт
 * @param repairOrderId - идентификатор Long id заказа на ремонт
 */
function openEditModalWindow(repairOrderId) {
    fetch(`http://localhost:9999/service/${repairOrderId}`)
        .then(function (response) {
            if (response.status === 200) {
                //renderSelect()
                response.json().then(repairOrder => {
                    $('#idRepairOrderUpdate').val(repairOrder.id);
                    $('#fullNameClientUpdate').val(repairOrder.fullNameClient);
                    $('#telephoneNumberUpdate').val(repairOrder.telephoneNumber);
                    $('#nameDeviceUpdate').val(repairOrder.nameDevice);
                    $('#fullTextProblemUpdate').summernote('code', repairOrder.fullTextProblem);
                    $("#postingDateUpdate").val(moment(repairOrder.acceptanceDate).format("yyyy-MM-DD"));
                    $('#guaranteeCheckboxUpdate').prop('checked', repairOrder.guarantee);
                    //$('#statusRepairOrderSelect').prop('selected', repairOrder.repairOrderType);
                    document.getElementById('statusRepairOrderSelect').value = repairOrder.repairOrderType;
                    console.log(repairOrder.repairOrderType);
                })
            } else {
                console.log('RepairOrder not found')
            }
        })
}

/**
 * Функция обновления заказа на ремонт
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
    fetch('http://localhost:9999/service/updateRepairOrder', {
        method: 'PUT',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-type': 'application/json; charset=UTF-8'
        },
        body: JSON.stringify(repairOrder)
    }).then(function (response) {
        if (response.status === 200) {
            console.log("все ок")
            $('#editRepairOrder').modal('hide')
        } else {
            console.log("не ок")
        }
    })
}

/*function renderSelect() {
    fetch('http://localhost:9999/service/getAllRepairOrderType')
        .then((res) => res.json())
        .then((data) => {
            let output = '';
            data.forEach(function (repairOrderType) {
                output += `
                   <option id="select-${repairOrderType}">${repairOrderType}</option>
          `;
            });
            document.getElementById('statusRepairOrderSelect').innerHTML = output;
        })
}*/
/**
 * Функция удаления заказа
 *
 * @param repairOrderId идентифактор заказа
 */
function deleteRepairOrder(repairOrderId) {
    let doDelete = confirm(`Вы уверены что хотите удалить заказ?`);
    if (doDelete) {
        fetch(`http://localhost:9999/service/${repairOrderId}`, {
            method: 'DELETE'
        }).then(function (response) {
            if (response.status === 200) {
                $('#div-' + repairOrderId).remove()
            } else {
                console.log('Orders not found')
            }
        })
    }
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

        viewRepairOrder += `<div id="div-${repairOrder.id}" class="alert alert-info mt-2">
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