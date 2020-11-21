fetchCommentsAndRenderTable();

function renderCommentsTable(reports) {
    let table = $('#comments-table');
    if (reports.length === 0) {
        table.empty()
            .append(`
            <h4>На данный момент нет комментариев для проверки.</h4>
            `);
    } else {
        table.empty()
            .append(`<tr>
                <th>ID</th>
                <th>Комментарий</th>
                <th>Причина жалобы</th>
                <th>Комментарий к жалобе</th>
                <th>Оставить</th>
                <th>Удалить</th>
              </tr>`);
        for (let i = 0; i < reports.length; i++) {
            const report = reports[i];
            let row = `
                <tr>
                    <td>${report.reportId}</td>
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
            success: function () {
                toastr.info("Комментарий оставлен");
                fetchCommentsAndRenderTable();
            }
        });
        fetchCommentsAndRenderTable();
    });
    $('.delete-button').on('click', function () {
        let commentId = $(this).attr("id");
        $.ajax({
            url: '/api/moderator/delete/' + commentId,
            method: "DELETE",
            success: function () {
                toastr.info("Комментарий удален");
                fetchCommentsAndRenderTable();
            }
        })
    });
}

function fetchCommentsAndRenderTable() {
    fetch("/api/moderator")
        .then(response => response.json())
        .then(reports => renderCommentsTable(reports))
}


