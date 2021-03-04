let myHeaders = new Headers()
myHeaders.append('Content-type', 'application/json; charset=UTF-8')

let managerSalesApiUrl = "/api/manager/sales"

$(document).ready(function () {
    dateRangePickerForSalesReport()
})

/**
 * function setting up datepicker field
 */
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

/**
 * fetch GET request to server to receive list of sales for custom range date
 */
$('#managerSalesReportRange').on('apply.daterangepicker', function (ev, picker) {
    $('#salesTableBody').empty()
    let startDate = picker.startDate.format('YYYY-MM-DD')
    let endDate = picker.endDate.format('YYYY-MM-DD')
    fetch(managerSalesApiUrl + `?stringStartDate=${startDate}&stringEndDate=${endDate}`)
        .then(function (response) {
            if (response.status === 200) {
                response.json()
                    .then(sales => sales.data)
                    .then(sales => renderSalesTable(sales))
                showElementsOfReport()
            } else {
                popupWindow('#infoMessageDiv', 'За указанный период, продаж не найдено', 'error')
                hideElementsOfSalesReport()
            }
        })
    $('#exportCsvButton').attr('startDate', startDate).attr('endDate', endDate)
});

/**
 * fetch request to export to csv
 */
$('#exportCsvButton').on('click', function () {
    let StartDate = $('#exportCsvButton').attr('startDate')
    let EndDate = $('#exportCsvButton').attr('endDate')
    fetch(managerSalesApiUrl + `/exportCSV?stringStartDate=${StartDate}&stringEndDate=${EndDate}`)
        .then(function (response) {
            if (response.status === 200) {
                popupWindow('#infoMessageDiv', 'Данные успешно выгружены', 'success')
                response.blob().then(blob => {
                    let url = window.URL.createObjectURL(blob);
                    let file = document.createElement('a');
                    file.href = url;
                    file.download = `sales_report_${StartDate}-${EndDate}.csv`;
                    file.click();
                })
            } else {
                popupWindow('#infoMessageDiv', 'За указанный период, продаж не найдено', 'error')
            }
        })
});

/**
 * Function that renders sales report table
 * @param sales
 */
function renderSalesTable(sales) {
    let salesTableBody = $('#salesTableBody')
    salesTableBody.empty()
    for (let i = 0; i < sales.length; i++) {
        let row = `<tr id=tr-${sales[i].id}>
                        <th scope="row">${sales[i].orderNumber}</th>
                        <td>${sales[i].userEmail}</td>
                        <td>${sales[i].customerInitials}</td>
                        <td>${sales[i].purchaseDate}</td>
                        <td>${sales[i].quantity}</td>
                        <td>${sales[i].listOfProducts}</td>
                        <td>${sales[i].orderSummaryPrice}</td>
                    </tr>`
        salesTableBody.append(row)
    }
}

/**
 * function that shows success or error message
 * @param inputField - location where message appears
 * @param text - text of message
 * @param messageStatus - status success or error
 */
function popupWindow(inputField, text, messageStatus) {
    let successMessage = `<div class="alert text-center alert-success alert-dismissible" role="alert">
                            <strong>${text}</strong>
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span></button>
                       </div>`
    let alertMessage = `<div class="alert text-center alert-danger alert-dismissible" role="alert">
                            <strong>${text}</strong>
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span></button>
                       </div>`
    let message = ''

    if (messageStatus === "success") {
        message = successMessage
    } else if (messageStatus === "error") {
        message = alertMessage;
    }

    document.querySelector(`${inputField}`).innerHTML = message
    window.setTimeout(function () {
        $('.alert').alert('close');
    }, 3000)
}

/**
 * function hides elements of report
 */
function hideElementsOfSalesReport() {
    document.getElementById("salesReportTable").style.visibility = "hidden";
    document.getElementById("exportCsvButton").style.visibility = "hidden";
}

/**
 * function shows elements of report
 */
function showElementsOfReport() {
    document.getElementById("salesReportTable").style.visibility = "visible";
    document.getElementById("exportCsvButton").style.visibility = "visible";
}