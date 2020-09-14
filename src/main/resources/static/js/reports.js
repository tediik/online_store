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
              console.log("succes")
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
    let ctx = document.getElementById('myChart').getContext('2d');
    let chart = new Chart(ctx, {
        type: 'line',
        data: {
            // Точки графиков
            labels: Object.keys(sentStocks),
            // График
            datasets: [{
                label: 'График рассылки',
                borderColor: 'rgb(255, 99, 132)',
                data: Object.values(sentStocks)
            }]
        },
        options: {}
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




