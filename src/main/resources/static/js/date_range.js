$("#datepicker_add").ready(function(){
    $("#startDate").datepicker({
        minDate: 0,
        numberOfMonths: 1,
        onSelect: function(selected) {
            $("#endDate").datepicker("option","minDate", selected)
        }
    });
    $("#endDate").datepicker({
        numberOfMonths: 1,
        onSelect: function(selected) {
            $("#startDate").datepicker("option","maxDate", selected)
        }
    });
});

$("#datepicker_edit").ready(function(){
    $("#startDate").datepicker({
        minDate: 0,
        numberOfMonths: 1,
        onSelect: function(selected) {
            $("#endDate").datepicker("option","minDate", selected)
        }
    });
    $("#endDate").datepicker({
        numberOfMonths: 1,
        onSelect: function(selected) {
            $("#startDate").datepicker("option","maxDate", selected)
        }
    });
});