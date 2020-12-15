/**
 * При загрузке страницы
 * @param day
 */

$(document).ready(fetchUsersAndRenderTable(onChangeDataInput()), dataPickerInitializer());


/**
 * функция запроса пользователей
 * @param day
 */
function fetchUsersAndRenderTable(day) {
    fetch("/api/manager/users/" + day, {
        method: 'GET',
        headers: {
            'Content-type': 'application/json; charset=UTF-8'
        }
    }).then(function (response) {
        if (response.ok) {
            response.json().then(users => renderUsersTable(users))
        } else {
            renderUsersTable(new Array)
        }
    })
}

/**
 * функция рендера таблицы пользователей
 * @param users
 */
function renderUsersTable(users) {
    let table = $('#users-table');
    table.empty();
    table.append(`<thead>
                    <tr>
        '               <th scope="col">ID</th>
        '               <th scope="col">Email</th>
        '               <th scope="col">Day</th>
        '               <th scope="col">Отменить подписку</th>
        '           </tr>
        '        </thead>`);
    if (users.length != 0) {
        for (let i = 0; i < users.length; i++) {
            const user = users[i];
            let row = `
                <tr id="${user.id}">
                    <td>${user.id}</td>
                    <td>${user.email}</td>            
                    <td>${user.dayOfWeekForStockSend}</td>
                    <td>
            <!-- Buttons of the right column of main table-->
                        <button data-user-id="${user.id}" type="button" class="btn btn-success delete-button" onclick="cancelSubscripption(this)">
                        Отменить
                        </button>
                    </td>
                  </tr>
                  `;
            table.append(row);
        }
    } else {
        table.append(`<tr>
                            <td colspan="4" align="center">
                                <strong style="color: #a94442">Нет пользователей для рассылки</strong>
                            </td>                       
                        </tr>`);
    }
}

/**
 * функция отмены подписки для пользователя
 * @param o
 */
function cancelSubscripption(element) {
    userId = $(element).closest('tr').find('td').eq(0).text();
    fetch("/api/manager/cancel/" + userId, {
        method: 'PUT',
        headers: {
            'Content-type': 'application/json; charset=UTF-8'
        }
    }).then(function (response) {
        if (response.ok) {
            handleSuccess("Подписка успешно отменена");
        } else {
            handleBad("Не удалось отменить подписку", "#alert-div");
        }
    })

    let row = document.getElementById("users-table")
        .querySelector(`tr[id='${userId}']`);
    row.parentElement.removeChild(row);
}

/**
 * функция изменения даты для отображения
 */
function onChangeDataInput() {
    let date = document.getElementById('date_range2').value;
    fetchUsersAndRenderTable(date);
}

/**
 * функция возвращает сегодняшний день
 */
// УДАЛИТЬ ПОСЛЕ ПРОВЕРКИ!!!
// function currentDay() {
//     let now = new Date();
//     return [7, 1, 2, 3, 4, 5, 6][now.getDay()];
// }

function fetchSentStocks(begin, end) {
    fetch("/api/manager/report?beginDate=" + begin + "&endDate=" + end, {
        method: 'GET',
        headers: {
            'Content-type': 'application/json; charset=UTF-8'
        }
    }).then(function (response) {
        if (response.ok) {
            response.json()
                .then(sentStocks => printChart(sentStocks));
        } else {
            handleBad("#chart-div");
            clearChart();
        }
    })
}

/**
 * Функция отрисовки графика
 */
function printChart(sentStocks) {
    let ctx = document.getElementById('myChart');
    Chart.defaults.global.defaultFontFamily = "Lato";
    Chart.defaults.global.defaultFontSize = 18;

    var speedData = {
        labels: Object.keys(sentStocks),
        datasets: [{
            label: "Количество отправленных акций по дням",
            data: Object.values(sentStocks),
            lineTension: 0,
            fill: false,
            borderColor: 'orange',
            backgroundColor: 'transparent',
            borderDash: [5, 5],
            pointBorderColor: 'orange',
            pointBackgroundColor: 'rgba(255,150,0,0.5)',
            pointRadius: 5,
            pointHoverRadius: 10,
            pointHitRadius: 30,
            pointBorderWidth: 2,
            pointStyle: 'rectRounded'
        }]
    };
    var chartOptions = {
        legend: {
            display: true,
            position: 'top',
            labels: {
                boxWidth: 80,
                fontColor: 'black'
            }
        }
    };
    var lineChart = new Chart(ctx, {
        type: 'line',
        data: speedData,
        options: chartOptions
    });
}

/**
 * функция перерисовки графика
 */
function rePrintChart(begin, end) {
    var dateBegin = new Date(begin);
    var dateEnd = new Date(end);
    fetchSentStocks(dateBegin.toISOString().substr(0, 10),
        dateEnd.toISOString().substr(0, 10));
}

/**
 * функция инициализация datepickera для выбора периода
 * построения графика
 */
function dataPickerInitializer() {
    $(function () {
        $('#date_range').datepicker({
            showButtonPanel: true,
            range: 'period', // режим - выбор периода
            numberOfMonths: 2,
            onSelect: function (dateText, inst, extensionRange) {
                // extensionRange - объект расширения
                $('#date_range').val(extensionRange.startDateText + ' - ' + extensionRange.endDateText);
            },
            onClose: function () {
                function isDonePressed() {
                    return ($('#ui-datepicker-div').html().indexOf('ui-datepicker-close ui-state-default ui-priority-primary ui-corner-all ui-state-hover') > -1);
                }

                if (isDonePressed()) {
                    var extensionRange = $('#date_range').datepicker('widget').data('datepickerExtensionRange');
                    rePrintChart(extensionRange.startDateText, extensionRange.endDateText);
                }
            }
        })
    })
    // $(function () {
    //     $('#date_range2').datepicker({
    //         showButtonPanel: true,
    //     })
    // })
}

/**
 * Сообщение об успешной отмене подписки на 5 сек
 * @param text - текст для вывода в алекрт
 */

function handleSuccess() {
    jQuery('#success-div').show();
    setTimeout(function () {
        jQuery('#success-div').hide();
    }, 5000);
}

/**
 * Сообщение о неудаче
 * @param text - текст для вывода в алекрт
 */
function handleBad(field) {
    jQuery(field).show();
    setTimeout(function () {
        jQuery(field).hide();
    }, 5000);
}

function clearChart() {
    $('#myChart').remove();
    $('#chartcontainer').append(`<canvas id="myChart"></canvas>`);
}