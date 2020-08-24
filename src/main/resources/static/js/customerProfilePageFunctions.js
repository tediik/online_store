const stockMailingCheckbox = document.getElementById('#stockMailingCheckbox')
/**
 * Обработка чекбокса #stockMailingCheckbox
 * если галка стоит, то отображать dropdownlist
 * с выбором дня недели для рассылки акций
 * если галку убрать, то скрывать dropdownlist
 */
$('#stockMailingCheckbox').change(function(){
    if(this.checked!==true){
        $(".day-of-the-week-drop-list").addClass("d-none")
    }
    else{
        $(".day-of-the-week-drop-list").removeClass("d-none")
    }
});

$('#dayOfWeekDropList').change(function (){
    let selectedValue = $(this).val()
    console.log(selectedValue)
})