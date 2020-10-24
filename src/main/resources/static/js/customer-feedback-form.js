/**
 * Global variables
 */
let myHeaders = new Headers()
myHeaders.append('Content-type', 'application/json; charset=UTF-8')
let feedBackTopicCategorySelect = $('#feedbackTopicCategorySelect')
let feedBackTopicSelect = $('#feedbackTopicNameSelect')

$(document).ready(function() {
    /**
     * event listeners
     */
    document.getElementById('feedback-tab').addEventListener('click', fetchAndFillCategorySelect)
    document.getElementById('feedbackSubmitButton').addEventListener('click', submitFeedbackForm)
    /*по нажатию на табу "Перечень обращений и ответов" достаем все обращения*/
    document.getElementById('answersFeedback-tab').addEventListener('click', getAllFeedbackCurrentCustomer)
    /*проверяем какая кнопка была нажата на обращении*/
    document.getElementById('messagesAndAnswersFeedback').addEventListener('click', checkButtonOnFeedbackCustomer)
})

/**
 * function that fetches categories list from db
 */
function fetchAndFillCategorySelect() {
    fetch("/api/feedback/categories")
        .then(function (response) {
            if (response.ok) {
                response.json().then(categories => fillCategorySelect(categories))
            }
        }).catch(error => console.log(error))

    function fillCategorySelect(categories) {
        feedBackTopicCategorySelect
            .empty()
            .append(`<option selected="selected" disabled>Выберите категорию</option>`)
        categories
            .forEach(category => feedBackTopicCategorySelect
                .append(`<option>${category}</option>`))
    }
    document.getElementById('feedbackTopicCategorySelect').addEventListener('change', fetchAndFillTopicSelect)
}

/**
 * function fetches and fills topic select
 */
function fetchAndFillTopicSelect() {
    fetch("/api/feedback/" + $('#feedbackTopicCategorySelect').val())
        .then(function (response) {
            if (response.ok) {
                response.json().then(topics => fillTopicSelect(topics))
            }
        })

    function fillTopicSelect(topics) {
        feedBackTopicSelect
            .empty()
            .append(`<option selected="selected" disabled>Выберите тему</option>`)
        topics.forEach(topic => feedBackTopicSelect.append(`<option value="${topic.id}">${topic.topicName}</option>`))
    }

    feedBackTopicSelect.removeAttr('disabled')
    document.getElementById('feedbackTopicNameSelect').addEventListener('change', enableFormFields)
}

/**
 * function enables feedback from fields
 */
function enableFormFields() {
    $('#messageFeedbackForm').removeAttr('disabled')
    $('#feedbackSubmitButton').removeAttr('disabled')
}

/**
 * function disables form fields
 */
function disableFormFields() {
    $('#messageFeedbackForm').attr('disabled', 'true')
    $('#feedbackSubmitButton').attr('disabled', 'true')
    $('#feedbackTopicNameSelect').attr('disabled', 'true')
}

/**
 * function submit feedback form
 */
function submitFeedbackForm() {
    let feedback = {
        message: $('#messageFeedbackForm').val(),
        topic: {
            id: $('#feedbackTopicNameSelect').val()
        },
    }
    if ($('#messageFeedbackForm').val() !== '') {
        fetch('/api/feedback/', {
            headers: myHeaders,
            method: 'POST',
            body: JSON.stringify(feedback)
        }).then(function (response) {
            if (response.ok) {
                toastr.success('Сообщение отправленно')
                document.getElementById('feedbackFrom').reset()
                disableFormFields()
            }
        })
    } else {
        toastr.error('Заполните все поля формы')
    }
}

function getAllFeedbackCurrentCustomer() {
    fetch('/api/feedback/messages')
        .then(function (response) {
            if (response.ok) {
                response.json().then((messages) => {
                    renderMessagesCurrentCustomer(messages)
                })
            } else {
                console.log('Ошибка запроса.')
            }
        })
}

function colorButtonAnswerFeedbackCustomer(answer) {
    if (answer === null) {
        return 'btn-light'
    } else {
        return 'btn-success'
    }
}

function checkExistAnswerFeedbackCustomer(answer) {
    if (answer === null) {
        return `disabled`
    } else {
        return `active`
    }
}

function colorStatusFeedbackCustomer(status) {
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

function colorDivFeedbackCustomer(status) {
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

function renderMessagesCurrentCustomer(data) {
    let viewMessagesCurrentCustomer = '';
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
        viewMessagesCurrentCustomer += `<div id="divCustomer-${messages.id}" class="alert ${colorDivFeedbackCustomer(messages.status)} mt-2">
                        <h5 class="font-weight-bold">№${messages.id} Категория: ${messages.topic.topicCategory}</h5>
                        <hr>  
                        <h6><u>Тема: ${messages.topic.topicName}</u></h6>
                        <span class="badge badge-pill ${colorStatusFeedbackCustomer(messages.status)} text-right">${messages.status}</span>
                        <br>  
                        <p>${messages.message}</p>
                        <div class="container-fluid row" id="divContainerCustomer">
                            <div class="text-left align-text-bottom col" id="divSpanCustomer">
                                <span id="postingDateRenderCustomer">Дата обращения: ${postDateFeedback}</span>
                            </div>
                            <div class="text-right col" id="divButtonsC">
                                <button type="button" data-toggle="modal" data-target="#deleteFeedbackModal" class="btn btn-danger" id="btn_delete_Feedback_Customer" 
                                data-toggle-id="cdelete" data-deletefeedback-id="${messages.id}" >Удалить</button>
                            </div>
                        </div>
                        <hr>
                        <div class="card">
                                    <div class="card-header" id="headingOneCustomer">
                                        <h5 class="mb-0">
                                            <button ${checkExistAnswerFeedbackCustomer(messages.answer)} type="button" 
                                            class="btn ${colorButtonAnswerFeedbackCustomer(messages.answer)}" id="btn_show_Answer_Customer" 
                                            data-toggle-id="cshow" data-cfeedback-id="${messages.id}"
                                            data-toggle="collapse" data-target="#collapseOneCustomer-${messages.id}" aria-expanded="true" 
                                            aria-controls="collapseOneCustomer-${messages.id}">Посмотреть ответ</button>
                                        </h5>
                                    </div>
                                <div id="collapseOneCustomer-${messages.id}" class="collapse" aria-labelledby="headingOneCustomer" data-parent="#divCustomer-${messages.id}">
                                    <div class="card-body" readonly>
                                        <p>${messages.answer}</p>
                                    </div>
                                </div>
                        </div>
                    </div>`
    });
    document.getElementById('messagesAndAnswersFeedback').innerHTML = viewMessagesCurrentCustomer;
}

function checkButtonOnFeedbackCustomer(event) {
    let delId = event.target.dataset.deletefeedbackId
    if (event.target.dataset.toggleId === "cshow") {

    }
    if (event.target.dataset.toggleId === "cdelete") {
        deleteFeedbackCustomer(delId)
    }
}

function deleteFeedbackCustomer(id) {
    if (confirm("Вы уверены что хотите удалить ообращение?")) {
        fetch(`/api/feedback/${id}`, {
            method: 'DELETE'
        }).then(function (response) {
            if (response.ok) {
                $('#divCustomer-' + id).remove()
                toastr.success("Обращение было удалено.");
            } else {
                toastr.error("Обращение не было удалено.");
            }
        })
    }
}