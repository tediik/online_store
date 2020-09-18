let myHeaders = new Headers()
myHeaders.append('Content-type', 'application/json; charset=UTF-8')

let managerSalesApiUrl = "/manager/api/sharedStock"

$(document).ready(function () {
    dateRangePickerForSalesReport()
})

// function dateRangePickerForSalesReport(){
//     $('input[name="dateRange"]').daterangepicker({
//         opens: 'left'
//     }, function(start, end, label) {
//         console.log("A new date selection was made: " + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD'));
//     });
// }

function dateRangePickerForSalesReport() {
    let start = moment().subtract(29, 'days');
    let end = moment();

    function cb(start, end) {
        $('#managerSalesReportRange span').html(start.format('MMMM D, YYYY') + ' - ' + end.format('MMMM D, YYYY'));
    }

    $('#managerSalesReportRange').daterangepicker({
        startDate: start,
        endDate: end,
        ranges: {
            'Today': [moment(), moment()],
            'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
            'Last 7 Days': [moment().subtract(6, 'days'), moment()],
            'Last 30 Days': [moment().subtract(29, 'days'), moment()],
            'This Month': [moment().startOf('month'), moment().endOf('month')],
            'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
        }
    }, cb);

    cb(start, end);
}

$('#managerSalesReportRange').on('apply.daterangepicker', function(ev, picker) {
    fetch()
    console.log(picker.startDate.format('YYYY-MM-DD'));
    console.log(picker.endDate.format('YYYY-MM-DD'));
});