$(document).ready(function() {
    getAllFeedback()

    /*Слушатель контролирующий нажатие на вкладки статусов обращений*/
    document.getElementById('navbarFeedbackManager').addEventListener('click', checkUpperNavButtonFeedback)
    /*проверяем какая кнопка была нажата на обращении*/
    document.getElementById('feedbackTabContent').addEventListener('click', checkButtonsOnFeedback)
})

function checkUpperNavButtonFeedback(event) {
    let tab = event.target.dataset.toggleId
    if (tab === 'allFeedback') {
        getAllFeedback()
    }
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

function getAllFeedback() {
    fetch('/api/feedback/allMessages')
        .then(function (response) {
            if (response.ok) {
                response.json().then((messages) => {
                    renderMessages(messages, 'all_feedback')
                })
            } else {
                console.log('Что-то не ладное творится в наших чертогах.')
            }
        })
}

function getInProgressFeedback() {
    fetch('/api/feedback/inProgress')
        .then(function (response) {
            if (response.ok) {
                response.json().then((messages) => {
                    renderMessages(messages, 'in_progress_feedback')
                })
            } else {
                console.log('Что-то не ладное творится в наших чертогах.')
            }
        })
}

function getLaterFeedback() {
    fetch('/api/feedback/later')
        .then(function (response) {
            if (response.ok) {
                response.json().then((messages) => {
                    renderMessages(messages, 'later_feedback')
                })
            } else {
                console.log('Что-то не ладное творится в наших чертогах.')
            }
        })
}

function getResolvedFeedback() {
    fetch('/api/feedback/resolved')
        .then(function (response) {
            if (response.ok) {
                response.json().then((messages) => {
                    renderMessages(messages, 'resolved_feedback')
                })
            } else {
                console.log('Что-то не ладное творится в наших чертогах.')
            }
        })
}

function colorButtonAnswerFeedback(answer) {
    if (answer === null) {
        return 'btn-light'
    } else {
        return 'btn-success'
    }
}

function checkExistAnswerFeedback(answer) {
    if (answer === null) {
        return `disabled`
    } else {
        return `active`
    }
}

function colorStatusFeedback(status) {
    if (status === 'IN_PROGRESS') {
        return 'badge-primary'
    } else if (status === 'LATER') {
        return 'badge-warning'
    } else if (status === 'RESOLVED'){
        return 'badge-success'
    } else {
        return 'badge-danger'
    }
}

function colorDivFeedback(status) {
    if (status === 'IN_PROGRESS') {
        return 'alert-primary'
    } else if (status === 'LATER') {
        return 'alert-warning'
    } else if (status === 'RESOLVED'){
        return 'alert-success'
    } else {
        return 'alert-danger'
    }
}

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
                        <h5 class="font-weight-bold">№${messages.id} Категория: ${messages.topic.topicCategory}</h5>
                        <hr>  
                        <h6><u>Тема: ${messages.topic.topicName}</u></h6>  
                        <span class="badge badge-pill ${colorStatusFeedback(messages.status)} text-right">${messages.status}</span>
                        <br>  
                        <p>${messages.message}</p>
                        <div class="container-fluid row" id="divContainer">
                            <div class="text-left align-text-bottom col" id="divSpan">
                                <span id="postingDateRender">Дата обращения: ${postDateFeedback}</span>
                            </div>
                            <div class="text-right col" id="divButtons">
                                <button type="button" data-toggle="modal" data-target="#deleteFeedbackModal" class="btn btn-danger" id="btn_delete_Feedback" 
                                data-toggle-id="delete" data-feedback-id="${messages.id}">Удалить</button>
                            </div>
                        </div>
                        <hr>
                        <div class="card">
                                    <div class="card-header" id="headingOne">
                                        <h5 class="mb-0">
                                            <button ${checkExistAnswerFeedback(messages.answer)} type="button" 
                                            class="btn ${colorButtonAnswerFeedback(messages.answer)}" id="btn_show_Answer" 
                                            data-toggle-id="show" data-feedback-id="${messages.id}"
                                            data-toggle="collapse" data-target="#collapseOne-${messages.id}" aria-expanded="true" aria-controls="collapseOne">Написать ответ</button>
                                        </h5>
                                    </div>
                                
                                <div id="collapseOne-${messages.id}" class="collapse" aria-labelledby="headingOne" data-parent="#div-${messages.id}">
                                    <div class="card-body">
                                     <textarea class="container-fluid">${messages.answer}</textarea>
                                    </div>
                                </div>
                        </div>
                    </div>`
    });
    document.getElementById(elementId).innerHTML = viewMessages;
}

function checkButtonsOnFeedback(event) {
    let id = event.target.dataset.feedbackId
    if (event.target.dataset.toggleId === "show") {

    }
    if (event.target.dataset.toggleId === "delete") {
        deleteFeedback(id)
    }
}

function deleteFeedback(id) {
    if (confirm("Вы уверены что хотите удалить ообращение?")) {
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