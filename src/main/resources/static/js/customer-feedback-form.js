let feedBackTopicCategorySelect = $('#feedbackTopicCategorySelect')
let feedBackTopicSelect = $('#feedbackTopicNameSelect')

document.getElementById('feedback-tab').addEventListener('click', fetchAndFillCategorySelect)

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
        topics.forEach(topic => feedBackTopicSelect.append(`<option>${topic}</option>`))
    }
    feedBackTopicSelect.removeAttr('disabled')
    document.getElementById('feedbackTopicNameSelect').addEventListener('change', enableFeedbackForm)
    fetchCustomerInfoAndFillForm()
}

/**
 * function enables form fields
 */
function enableFeedbackForm(){
    $('#messageText').removeAttr('disabled')
    $('#firstNameFeedbackFormInput').removeAttr('disabled')
    $('#emailFeedbackFormInput').removeAttr('disabled')
    $('#phoneFeedbackFormInput').removeAttr('disabled')
}


function fetchCustomerInfoAndFillForm() {

}