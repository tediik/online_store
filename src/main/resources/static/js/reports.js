/**
 * При загрузке страницы
 * @param day
 */
$(document).ready (fetchUsersAndRenderTable(currentDay()));

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
    }).then(response => response.json()).then(users => renderUsersTable(users))
}
/**
 * функция рендера таблицы пользователей
 * @param users
 */
function renderUsersTable(users) {
    let table = $('#users-table');
    table.empty();
    table.append('<thead>\n' +
    '                        <tr>\n' +
    '                            <th scope="col" >ID</th>\n' +
    '                            <th scope="col" >Email</th>\n' +
    '                            <th scope="col" >Day</th>\n' +
    '                            <th scope="col" >Отменить подписку</th>\n' +
    '                        </tr>\n' +
    '                        </thead>');
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
  }
/**
 * функция отмены подписки для пользователя
 * @param o
 */
  function cancelSubscripption(o) {
      userId = $(o).closest('tr').find('td').eq(0).text();
      fetch("/api/manager/cancel/"+userId, {
          method: 'GET',
          headers: {
              'Content-type': 'application/json; charset=UTF-8'
          }
      }).then(function (response) {
          if(response.ok) {
              handleSucces("Подписка успешно отменена")
          }
          else {
              console.log("bad")
          }
      })
      var table = document.getElementById("users-table");
      var selector = "tr[id='"+userId+"']";
      var row = table.querySelector(selector);
      row.parentElement.removeChild(row);
  }

/**
 * функция изменения даты для отображения
 */
function onChangeDataInput() {
    var date = new Date (document.getElementById('date_range2').value);
    fetchUsersAndRenderTable([7, 1, 2, 3, 4, 5, 6][date.getDay()]);
}
/**
 * функция возвращает сегодняшний день
 */
function currentDay() {
    let now = new Date();
    return [7, 1, 2, 3, 4, 5, 6][now.getDay()];
}
function fetchSentStocks(begin, end) {
    fetch("/api/manager/report?param1="+begin+"&param2="+end, {
        method: 'GET',
        headers: {
            'Content-type': 'application/json; charset=UTF-8'
        }
    }).then(response => response.json()).then(sentStocks=> printChart(sentStocks));
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
            label: "Количество отправленных акция по дням",
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
    fetchSentStocks(dateBegin.toISOString().substr(0,10),
                    dateEnd.toISOString().substr(0,10));
}
/**
 * функция инициализация datepickera для выбора периода
 * построения графика
 */
$(function() {
    $('#date_range').datepicker({
        showButtonPanel: true,
        range: 'period', // режим - выбор периода
        numberOfMonths: 2,
        onSelect: function(dateText, inst, extensionRange) {
            // extensionRange - объект расширения
            $('#date_range').val(extensionRange.startDateText + ' - ' + extensionRange.endDateText);
        },
        onClose: function() {
            function isDonePressed(){
                return ($('#ui-datepicker-div').html().indexOf('ui-datepicker-close ui-state-default ui-priority-primary ui-corner-all ui-state-hover') > -1);
            }
            if (isDonePressed()){
                var extensionRange = $('#date_range').datepicker('widget').data('datepickerExtensionRange');
                rePrintChart(extensionRange.startDateText,extensionRange.endDateText);
            }
        }
    })
});
/**
 * функция инициализации 2 datepickera
 */
$(function() {
    $('#date_range2').datepicker({
        showButtonPanel: true,
    })
});
/**
 * Сообщение об успешной отмене подписки
 * @param text - текст для вывода в алекрт
 */
function handleSucces(text) {
    $('#success-div').empty().append(`
            <div class="alert alert-success alert-dismissible fade show" role="alert">
              <strong>${text}</strong>
              <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            `)

    window.setTimeout(function () {
        $('.alert').alert('close');
    }, 5000)
}




