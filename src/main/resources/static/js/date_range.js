$("#datepicker_add").ready(function(){
    $("#addStartDate").datepicker({
        minDate: 0,
        numberOfMonths: 1,
        onSelect: function(selected) {
            $("#addEndDate").datepicker("option","minDate", selected)
        }
    });
    $("#addEndDate").datepicker({
        numberOfMonths: 1,
        onSelect: function(selected) {
            $("#addStartDate").datepicker("option","maxDate", selected)
        }
    });
});

$("#datepicker_edit").ready(function(){
    $("#editStartDate").datepicker({
        minDate: 0,
        numberOfMonths: 1,
        onSelect: function(selected) {
            $("#editEndDate").datepicker("option","minDate", selected)
        }
    });
    $("#editEndDate").datepicker({
        numberOfMonths: 1,
        onSelect: function(selected) {
            $("#editStartDate").datepicker("option","maxDate", selected)
        }
    });
});