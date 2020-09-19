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
    let StartDate = picker.startDate.format('YYYY-MM-DD')
    let EndDate = picker.endDate.format('YYYY-MM-DD')
    fetch(managerSalesApiUrl + `?stringStartDate=${StartDate}&stringEndDate=${EndDate}`)
        .then(function (response){
            if (response.status === 200){
                response.json().then(sales => renderSalesTable(sales))
            } else {
                popupWindow('#infoMessageDiv', 'За указанный период, продаж не найдено', 'error')
            }
        })
    console.log(picker.startDate.format('YYYY-MM-DD'));
    console.log(picker.endDate.format('YYYY-MM-DD'));
});

/**
 * Function that renders sales report table
 * @param sales
 */
function renderSalesTable(sales){
    let salesTableBody = $('#salesTableBody')
    salesTableBody.empty()
    for (let i = 0; i < sales.length; i++){
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
 * function that sorts the table by header
 */
document.addEventListener('DOMContentLoaded', () => {
    const getSort = ({ target }) => {
        const order = (target.dataset.order = -(target.dataset.order || -1));
        const index = [...target.parentNode.cells].indexOf(target);
        const collator = new Intl.Collator(['en', 'ru'], { numeric: true });
        const comparator = (index, order) => (a, b) => order * collator.compare(
            a.children[index].innerHTML,
            b.children[index].innerHTML
        );

        for(const tBody of target.closest('table').tBodies)
            tBody.append(...[...tBody.rows].sort(comparator(index, order)));

        for(const cell of target.parentNode.cells)
            cell.classList.toggle('sorted', cell === target);
    };
    document.querySelectorAll('.table_sort thead').forEach(tableTH => tableTH.addEventListener('click', () => getSort(event)));
});

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