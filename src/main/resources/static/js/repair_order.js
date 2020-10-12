$(document).ready(function () {
    getAllRepairOrders()

    document.getElementById('buttonAddRepairOrder').addEventListener('click', addRepairOrder)
    document.getElementById('navbarRepairOrders').addEventListener('click', checkUpperNavButtonRepairOrder)
});

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
            'Content-type': 'application/json'
        },
        body: JSON.stringify(repairOrder)
    }).then(function (response) {
        if (response.status === 200) {

        } else {

        }
    })
}

/**
 * function checks which button on top nav bar was clicked
 * @param event
 */
function checkUpperNavButtonRepairOrder(event) {
    let tab = event.target.dataset.toggleId
    console.log(tab)
    if (tab === 'allRepairOrders') {
        getAllRepairOrders();
    }
    if (tab === 'acceptedRepairOrders') {
        /*initFetchNews('ALL', tab);*/
    }
    if (tab === 'diagnostics') {
        /*initFetchNews('UNPUBLISHED', tab);*/
    }
    if (tab === 'underRepair') {
        /*initFetchNews('ARCHIVED', tab);*/
    }
    if (tab === 'onIssue') {
        /*initFetchNews('ARCHIVED', tab);*/
    }
    if (tab === 'closedRepairOrders') {
        /* initFetchNews('ARCHIVED', tab);*/
    }
}


function getAllRepairOrders() {
    fetch('http://localhost:9999/service/getAllRepairOrder')
        .then((res) => res.json())
        .then((data) => {
            let allRepairOrder = '';
            data.forEach(function (repairOrder) {

                let modifiedDateRepairOrder = ''
                if (repairOrder.modifiedDate !== null) {
                    modifiedDateRepairOrder = `Дата последнего изменения: ` + moment(repairOrder.modifiedDate).format("yyyy-MM-DD")
                }

                allRepairOrder += `<div id="div-${repairOrder.id}" class="alert alert-info mt-2">
                        <h5 id="title">${repairOrder.fullNameClient}</h5>
                        <h5 id="anons">${repairOrder.telephoneNumber}</h5>
                        <p id="fullText">${repairOrder.fullTextProblem}</p>
                        <div class="container-fluid row" id="divContainer">
                            <div class="text-left align-text-bottom col" id="divSpan">
                                <span id="postingDate">Дата публикации: ${repairOrder.acceptanceDate}</span>
                                <br>
                                <span id="modifiedDate">${modifiedDateRepairOrder}</span>
                            </div>
                            <div class="text-right col" id="divButtons">
                                <button type="button" data-toggle="modal" class="btn btn-primary"  id="btn_edit_news" data-target="#editNewsModal" data-toggle-id="edit" data-news-id="${repairOrder.id}">Редактировать</button>
                                <button type="button" data-toggle="modal" class="btn btn-danger"  id="btn_delete_news" data-toggle-id="delete" data-news-id="${repairOrder.id}" >Удалить</button>
                            </div>
                        </div>
                    </div>`
            });
            document.getElementById('all_repairOrders').innerHTML = allRepairOrder;
        })
}