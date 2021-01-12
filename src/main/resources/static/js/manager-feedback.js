let managersId
$(document).ready(function () {
    toastr.options = {
        "positionClass": "toast-top-full-width"
    }

    /*Слушатель контролирующий нажатие на вкладки статусов обращений*/
    document.getElementById('navbarFeedbackManager').addEventListener('click', checkUpperNavButtonFeedback)
    /*проверяем какая кнопка была нажата на обращении*/
    document.getElementById('feedbackTabContent').addEventListener('click', checkButtonsOnFeedback)
    /*слушатель на нажатия вкладки Обратная связь слева на панели навигации*/
    document.getElementById('nav-link-feedbackManager').addEventListener('click', getInProgressFeedback)
})

/**
 * Функция проверяет какая вкладка нажата на панели навигации
 * @param event событие клик
 */
function checkUpperNavButtonFeedback(event) {
    let tab = event.target.dataset.toggleId
    if (tab === 'inProgressFeedback') {
        getInProgressFeedback()
    }
    if (tab === 'laterFeedback') {
        getLaterFeedback()
    }
    if (tab === 'resolvedFeedback') {
        getResolvedFeedback()
    }
}

/**
 * Функция запрашивает все обращения в статусе IN_PROGRESS
 * и в случае успеха, отправляет их в метод {@link renderMessages()}
 */
function getInProgressFeedback() {
    getManager();
    fetch('/api/feedback/inProgress')
        .then(function (response) {
            if (response.ok) {
                response.json().then((messages) => {
                    renderMessages(messages, 'in_progress_feedback')
                })
            } else {
                console.log('Загрузка обращений не удалась')
            }
        })
}

/**
 * Функция запрашивает все обращения в статусе LATER
 * и в случае успеха, отправляет их в метод {@link renderMessages()}
 */
function getLaterFeedback() {
    fetch('/api/feedback/later')
        .then(function (response) {
            if (response.ok) {
                response.json().then((messages) => {
                    renderMessages(messages, 'later_feedback')
                })
            } else {
                console.log('Загрузка обращений не удалась')
            }
        })
}

/**
 * Функция запрашивает все обращения в статусе RESOLVED
 * и в случае успеха, отправляет их в метод {@link renderMessages()}
 */
function getResolvedFeedback() {
    fetch('/api/feedback/resolved')
        .then(function (response) {
            if (response.ok) {
                response.json().then((messages) => {
                    renderMessages(messages, 'resolved_feedback')
                })
            } else {
                console.log('Загрузка обращений не удалась')
            }
        })
}

/**
 * Функция проверяет что было нажато на конкретном обращении
 * @param event сообытие клик
 */
function checkButtonsOnFeedback(event) {
    let id = event.target.dataset.feedbackId
    let showStatus = event.target.dataset.showstatusId

    let sendId = event.target.dataset.sendId
    let sendStatus = event.target.dataset.sendstatusId

    let timeId = event.target.dataset.timefId
    let timeStatus = event.target.dataset.timestatusId

    let laterId = event.target.dataset.laterId
    let laterStatus = event.target.dataset.laterstatusId

    let inworkId = event.target.dataset.inworkId
    let inworkStatus = event.target.dataset.inworkstatusId

    let delId = event.target.dataset.delfeedbackId

    if (event.target.dataset.toggleId === "show") {
        showAnswer(id, showStatus)
    }
    if (event.target.dataset.toggleId === "send") {
        sendAnswer(sendId, sendStatus)
    }
    if (event.target.dataset.toggleId === "time") {
        timeAnswer(timeId, timeStatus)
    }
    if (event.target.dataset.toggleId === "later") {
        laterAnswer(laterId, laterStatus)
    }
    if (event.target.dataset.toggleId === "inwork") {
        returnInWork(inworkId, inworkStatus)
    }
    if (event.target.dataset.toggleId === "delete") {
        deleteFeedback(delId)
    }
}

/**
 * Функция чистит поле с ответом, если ответа нет
 * @param id идентификатор обращения
 * @param showStatus статус обращения
 */
function showAnswer(id, showStatus) {
    let textAnswer = document.getElementById(`textAnswer-${id}`).value
    if (textAnswer === 'null') {
        document.getElementById(`textAnswer-${id}`).value = ''
    }
    if (showStatus === 'RESOLVED') {
        document.getElementById(`textAnswer-${id}`).setAttribute('readonly', 'readonly')
    }
}

/**
 * Функция получает фио менеджера ответившего на обращение
 */
function getManager() {
    fetch("/api/admin/authUser")
        .then((response) => {
            return response.json();
        })
        .then((data) => {
            managersId = data.id
        })
        .catch((error) => {
            console.log(error)
        })
}

/**
 * Функция добавляет ответ к обращению
 * @param id идентификатор обращения
 * @param sendStatus статус обращения
 */
function sendAnswer(id, sendStatus) {
    let textAnswer = document.getElementById(`textAnswer-${id}`).value
    if (sendStatus !== 'RESOLVED') {
        if (textAnswer !== 'null' && textAnswer !== '') {
            fetch('/api/feedback/addAnswer', {
                method: 'PUT',
                headers: {
                    'Accept': 'application/json, text/plain, */*',
                    'Content-type': 'application/json; charset=UTF-8'
                },
                body: JSON.stringify({
                    id: id,
                    answer: textAnswer,
                    managerId: managersId
                })
            }).then(function (response) {
                if (response.ok) {
                    toastr.success('Ответ отправлен')
                    getInProgressFeedback()
                    getLaterFeedback()
                    getResolvedFeedback()
                } else {
                    toastr.error('Ответ не отправлен')
                }
            })
        } else {
            toastr.error('Заполните поле ответа')
        }
    } else {
        toastr.error('Данное обращение закрыто')
    }
}

/**
 * Функция обновляет время ожидания обращения
 * @param id идентификатор обращения
 * @param laterStatus статус обращения
 */
function laterAnswer(id, laterStatus) {
    let time = document.getElementById(`laterTime-${id}`).value
    if (laterStatus !== 'RESOLVED') {
    fetch('/api/feedback/laterAnswer', {
        method: 'PUT',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-type': 'application/json; charset=UTF-8'
        },
        body: JSON.stringify({
            id: id,
            responseExpected: time
        })
    }).then(function (response) {
        if (response.ok) {
            toastr.success('Обращение отложено')
            getInProgressFeedback()
            getLaterFeedback()
        } else {
            toastr.error('Некорректные данные')
        }
    })
    } else {
        toastr.error('Данное обращение закрыто')
    }
}


function returnInWork(inworkId, inworkStatus) {
    if (inworkStatus !== 'RESOLVED') {
        fetch('/api/feedback/inWork', {
            method: 'PUT',
            headers: {
                'Accept': 'application/json, text/plain, */*',
                'Content-type': 'application/json; charset=UTF-8'
            },
            body: inworkId,
        }).then(function (response) {
            if (response.ok) {
                toastr.success('Обращение отправлено в работу')
                getLaterFeedback()
            } else {
                toastr.error('Некорректные данные')
            }
        })
    } else {
        toastr.error('Данное обращение закрыто')
    }
}

/**
 * Функция запрашивает время ожидания текущего обращения
 * @param id идентификатор обращения
 * @param timeStatus статус обращения
 */
function timeAnswer(id, timeStatus) {
    if (timeStatus === 'RESOLVED') {
        document.getElementById('laterTime-' + id).setAttribute('readonly', 'readonly')
    }
    fetch(`/api/feedback/timeAnswer/${id}`)
        .then(function (response) {
            if (response.ok) {
                response.json().then((data) => {
                    document.getElementById('laterTime-' + id).value = data
                })
            }
        })
}

/**
 * Функция удаляет обращение
 * @param id идентификатор обращения
 */
function deleteFeedback(id) {
    if (confirm("Вы уверены что хотите удалить обращение?")) {
        fetch(`/api/feedback/${id}`, {
            method: 'DELETE'
        }).then(function (response) {
            if (response.ok) {
                $('#div-' + id).remove()
                toastr.success("Обращение было удалено.");
            } else {
                toastr.error("Обращение не было удалено.");
            }
        })
    }
}

/**
 * Функция задает цвет кнопке ответа на обращение
 * @param answer - текст ответа на обращение
 * @returns {string} - возвращаем стиль для кнопки
 */
function colorButtonAnswerFeedback(answer) {
    if (answer === null) {
        return 'btn-info'
    } else {
        return 'btn-success'
    }
}

/**
 * Функция задает цвет статуса обращения
 * @param status - статус обращения
 * @returns {string} - возвращаем стиль для отображения статуса обращения
 */
function colorStatusFeedback(status) {
    if (status === 'IN_PROGRESS') {
        return 'badge-primary'
    } else if (status === 'LATER') {
        return 'badge-warning'
    } else if (status === 'RESOLVED') {
        return 'badge-success'
    } else {
        return 'badge-danger'
    }
}

/**
 * Функция задает цвет самой карточки обращения
 * @param status - статус обращения
 * @returns {string} - стиль карточки обращения
 */
function colorDivFeedback(status) {
    if (status === 'IN_PROGRESS') {
        return 'alert-primary'
    } else if (status === 'LATER') {
        return 'alert-warning'
    } else if (status === 'RESOLVED') {
        return 'alert-success'
    } else {
        return 'alert-danger'
    }
}

function getCategoryName(category) {
    if (typeof category === "object") {
        return category.categoryName
    } else {
        return category
    }
}

/**
 * Функция отрисовки обращений
 * @param data - массив обращений
 * @param elementId идентификатор области для заполнения обращениями
 */
function renderMessages(data, elementId) {
    let viewMessages = '';
    let options = {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'long',
        timezone: 'UTC',
        hour: 'numeric',
        minute: 'numeric',
        second: 'numeric'
    };
    data.forEach(function (messages) {
        let postDateFeedback = new Date(messages.feedbackPostDate).toLocaleString('ru', options)
        viewMessages += `<div id="div-${messages.id}" class="alert ${colorDivFeedback(messages.status)} mt-2">
                        <h5 class="font-weight-bold">№${messages.id} Категория: ${getCategoryName(messages.topic.topicsCategory)}</h5>
                        <hr>  
                        <h6><u>Тема: ${messages.topic.topicName}</u></h6>  
                        <span class="badge badge-pill ${colorStatusFeedback(messages.status)} text-right">${messages.status}</span>
                        <br>  
                        <p>${messages.message}</p>
                        <div class="container-fluid row" id="divContainer-${messages.id}">
                            <div class="text-left align-text-bottom col" id="divSpan-${messages.id}">
                                <span id="postingDateRender">Дата обращения: ${postDateFeedback}</span>
                                <span id="lastNameRender">Логин:<mark>${messages.user.username}</mark>,</span>
                                <span id="NameRender"><em>Имя: <mark>${messages.user.firstName}</mark>,</em></span> 
                                <span id="lastNameRender"><em>Фамилия: <mark>${messages.user.lastName}</mark></em></span>
                            </div>
                            <div class="text-right col" id="divButtons-${messages.id}">
                                <button type="button" class="btn btn-danger" id="btn_delete_Feedback" 
                                data-toggle-id="delete" data-delfeedback-id="${messages.id}">Удалить</button>
                            </div>
                        </div>
                        <hr>
                        <div class="card" id="cardManager-${messages.id}">
                                    <div class="card-header" id="card-header-${messages.id}">
                                        <h5 class="mb-0">
                                            <button type="button" 
                                            class="btn ${colorButtonAnswerFeedback(messages.answer)}" id="btn_show_Answer" 
                                            data-toggle-id="show" data-feedback-id="${messages.id}" data-showstatus-id="${messages.status}"
                                            data-toggle="collapse" data-target="#collapseOne-${messages.id}" aria-expanded="true" aria-controls="collapseOne">Написать ответ</button>
                                            
                                            <button data-timef-id="${messages.id}" data-timestatus-id="${messages.status}" data-toggle-id="time" class="btn btn-warning"
                                            data-toggle="collapse" data-target="#collapseTwo-${messages.id}" aria-expanded="true" aria-controls="collapseTwo">
                                            Выбрать время ответа</button>
                                        </h5>
                                    </div>
                                <div id="collapseOne-${messages.id}" class="collapse" data-parent="#div-${messages.id}">
                                    <div class="card-body" id="card-body1-${messages.id}">
                                     <textarea id="textAnswer-${messages.id}" class="container-fluid">${messages.answer}</textarea>
                                     <button data-send-id="${messages.id}" data-sendstatus-id="${messages.status}" data-toggle-id="send" class="btn btn-success">Отправить ответ</button>
                                    </div>
                                </div>
                                <div id="collapseTwo-${messages.id}" class="collapse" data-parent="#div-${messages.id}">
                                    <div class="card-body" id="card-body2-${messages.id}">
                                        <input id="laterTime-${messages.id}" class="form-control" type="datetime-local">
                                        <br>
                                        <button data-later-id="${messages.id}" data-laterstatus-id="${messages.status}" data-toggle-id="later" class="btn btn-success">Отложить обращение</button>
                                        <button data-inwork-id="${messages.id}" data-inworkstatus-id="${messages.status}" data-toggle-id="inwork" class="btn btn-primary">Вернуть в работу</button>
                                    </div>
                                </div>
                        </div>
                    </div>`
    });
    document.getElementById(elementId).innerHTML = viewMessages;
}