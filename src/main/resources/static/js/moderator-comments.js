let stompClient = null;

connect();

function connect() {
    let socket = new SockJS('/reportComments');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        stompClient.subscribe('/table/report',function (allReports) {
            renderCommentsTable(JSON.parse(allReports.body));
        });
        sendMessage();
    })
}

function sendMessage() {
    stompClient.send("/app/report");
}

function renderCommentsTable(allReports) {
    let table = $('#comments-table');
    if (allReports.length === 0) {
        table.empty()
            .append(`
            <h4>На данный момент нет комментариев для проверки.</h4>
            `);
    } else {
        table.empty()
            .append(`
                    <tr>
                        <th>ID</th>
                        <th>Кто пожаловался</th>
                        <th>Комментарий</th>
                        <th>Причина жалобы</th>
                        <th>Комментарий к жалобе</th>
                        <th>Оставить</th>
                        <th>Удалить</th>
                    </tr>
                    `);
        for (let i = 0; i < allReports.length; i++) {
            const report = allReports[i];
            let row = `
                <tr>
                    <td>${report.reportId}</td>
                    <td>${report.reportCustomerEmail}</td>
                    <td>${report.reportedComment}</td>
                    <td>${report.reportReason}</td>
                    <td>${report.reasonComment}</td>
                    <td>
                        <button id="${report.reportId}" type="button" class="btn btn-success leave-button">
                                 Оставить
                        </button>
                    </td>
                    <td>
                        <button id="${report.commentId}" type="button"  class="btn btn-danger delete-button">
                                 Удалить
                        </button>
                    </td>
                </tr>
                `;
            table.append(row);
        }
    }
    $('.leave-button').on('click', function () {
        let reportId = $(this).attr("id");
        $.ajax({
            url: '/api/moderator/leave/' + reportId,
            method: "DELETE",
            dataType: 'text',
            success: function () {
                toastr.info("Комментарий оставлен");
                sendMessage();
            }
        });
    });
    $('.delete-button').on('click', function () {
        let commentId = $(this).attr("id");
        $.ajax({
            url: '/api/moderator/delete/' + commentId,
            method: "DELETE",
            dataType: 'text',
            success: function () {
                toastr.info("Комментарий удален");
                sendMessage();
            }
        })
    });

    fetch("/api/moderator/statistic").then(
        res => {
            res.json().then(
                data => {
                    let statistic = "";
                    data.forEach((s) => {

                        let firstName = (s.moderator.firstName == null) ? `` : s.moderator.firstName;
                        let lastName = (s.moderator.lastName == null) ? `` : s.moderator.lastName;

                        statistic += `<tr>`;
                        statistic += `<th>` + firstName + ` ` + lastName + `</th>`;
                        statistic += `<th>` + s.moderator.email + `</th>`;
                        statistic += `<th>` + s.approvedCount + `</th>`;
                        statistic += `<th>` + s.dismissedCount + `</th>`;
                        statistic += `<th>` + (s.approvedCount + s.dismissedCount) + `</th>`;
                        statistic += `<th>` + s.lastActivityDate + `</th></tr>`;
                        }
                    )
                    document.getElementById("listStatistic").innerHTML = statistic;
                }
            )
        }
    )

}



