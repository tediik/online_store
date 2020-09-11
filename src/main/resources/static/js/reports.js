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
$( ".form-control" ).change(function() {
    var date = new Date (document.getElementById('date_for_table').value);
    fetchUsersAndRenderTable([7, 1, 2, 3, 4, 5, 6][date.getDay()]);
    document.getElementById('date_for_table').blur();
});
/**
 * функция возвращает сегодняшний день
 */
function currentDay() {
    let now = new Date();
    return [7, 1, 2, 3, 4, 5, 6][now.getDay()];
}

