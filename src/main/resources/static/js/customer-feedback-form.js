/**
 * Global variables
 */
let myHeaders = new Headers()
myHeaders.append('Content-type', 'application/json; charset=UTF-8')
let feedBackTopicCategorySelect = $('#feedbackTopicCategorySelect')
let feedBackTopicSelect = $('#feedbackTopicNameSelect')

/**
 * event listeners
 */
document.getElementById('feedback-tab').addEventListener('click', fetchAndFillCategorySelect)
document.getElementById('feedbackSubmitButton').addEventListener('click', submitFeedbackForm)

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
                .append(`<option>${category.categoryName}</option>`))
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