/**
 * Обработка чекбокса #stockMailingCheckbox
 * если галка стоит, то отображать dropdownlist
 * с выбором дня недели для рассылки акций
 * если галку убрать, то скрывается dropdownlist
 * и удаляется значение
 */
function chekboxChanges(o) {
    if (o.checked != true) {
        $(".day-of-the-week-drop-list").addClass("d-none")
        $("#dayOfWeekDropList").val('')
    } else {
        $(".day-of-the-week-drop-list").removeClass("d-none")
    }
};

$(document).ready(function () {

    /**
     * Event listeners
     */
    document.getElementById('deleteProfileCustomer').addEventListener('click', deleteProfile)
});

/**
 * function delete profile customer
 * @param event
 */
function deleteProfile(event) {
    let id = event.target.dataset.delId
    fetch(`/customer/deleteProfile/${id}`, {
        method: 'DELETE',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-type': 'application/json'
        }
    }).then(function (response) {
        if (response.ok) {
            document.location.href = "/logout";
        } else {
            toastr.error('Ваш профиль не был удален.', {timeOut: 3000});
        }
    })
}