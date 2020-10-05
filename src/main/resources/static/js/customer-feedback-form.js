let myHeaders = new Headers()
myHeaders.append('Content-type', 'application/json; charset=UTF-8')
let feedBackTopicCategorySelect = $('#feedbackTopicCategorySelect')
let feedBackTopicSelect = $('#feedbackTopicNameSelect')

document.getElementById('feedback-tab').addEventListener('click', fetchAndFillCategorySelect)
document.getElementById('feedbackSubmitButton').addEventListener('click', submitFeedbackForm)

let phoneNumberInput = document.getElementById('phoneFeedbackFormInput')
let phoneMask = new Inputmask("9(999)999-99-99")
phoneMask.mask(phoneNumberInput)

$(document).ready(function (){

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
        })

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

    fetch('/api/feedback/userInfo').then(function (response){
        if (response.ok){
            response.json().then(user => {
                $('#firstNameFeedbackFormInput').val(user.firstName)
                $('#emailFeedbackFormInput').val(user.email)
            })
        } else {
            console.log('user not logged in')
        }
    })
}

/**
 * function enables feedback from fields
 */
function enableFormFields() {
    $('#messageFeedbackForm').removeAttr('disabled')
    $('#firstNameFeedbackFormInput').removeAttr('disabled')
    $('#emailFeedbackFormInput').removeAttr('disabled')
    $('#phoneFeedbackFormInput').removeAttr('disabled')
    $('#cityFeedbackFormSelect').removeAttr('disabled')
    $('#feedbackSubmitButton').removeAttr('disabled')
}

/**
 * function disables form fields
 */
function disableFormFields() {
    $('#messageFeedbackForm').attr('disabled', 'true')
    $('#firstNameFeedbackFormInput').attr('disabled', 'true')
    $('#emailFeedbackFormInput').attr('disabled', 'true')
    $('#phoneFeedbackFormInput').attr('disabled', 'true')
    $('#cityFeedbackFormSelect').attr('disabled', 'true')
    $('#feedbackSubmitButton').attr('disabled', 'true')
    $('#feedbackTopicNameSelect').attr('disabled', 'true')
}

/**
 * function submit feedback form
 */
function submitFeedbackForm() {
    let feedback = {
        email: $('#emailFeedbackFormInput').val(),
        firstName: $('#firstNameFeedbackFormInput').val(),
        message: $('#messageFeedbackForm').val(),
        phoneNumber: $('#phoneFeedbackFormInput').val(),
        topic: {
            id: $('#feedbackTopicNameSelect').val()
        },
    }
    if ($('#emailFeedbackFormInput') !== ''
        && $('#firstNameFeedbackFormInput').val() !== ''
        && $('#messageFeedbackForm').val() !== ''
        && $('#phoneFeedbackFormInput').val() !== '') {

        fetch('/api/feedback/', {
            headers: myHeaders,
            method: 'POST',
            body: JSON.stringify(feedback)
        }).then(function (response) {
            if (response.ok) {
                console.log('ok')
                toastr.success('Сообщение отправленно')
                document.getElementById('feedbackFrom').reset()
                disableFormFields()
            }
        })
    } else {
        toastr.error('Заполните все поля формы')
    }
}

function checkIfUserLoggedIn() {
    fetch()
}