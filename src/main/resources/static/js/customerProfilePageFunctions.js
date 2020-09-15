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
