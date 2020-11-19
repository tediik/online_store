fetchCommentsAndRenderTable();
function renderCommentsTable(comments) {
    let table = $('#comments-table');
    table.empty()
        .append(`<tr>
                <th>ID</th>
                <th>Комментарий</th>
                <th>Опубликовать</th>
                <th>Удалить</th>
              </tr>`);
    for (let i = 0; i < comments.length; i++) {
        const comment = comments[i];
        let row = `
                <tr id="tr-${comment.id}">
                    <td>${comment.id}</td>
                    <td>${comment.content}</td>
                    <td>
                        <button data-comments-id="${comment.id}" type="button" class="btn btn-success edit-button">
                                 Опубликовать
                        </button>
                    </td>
                    <td>
                        <button data-comments-id="${comment.id}" type="button"  class="btn btn-danger action">
                                 Удалить
                        </button>
                    </td>
                </tr>
                `;
        table.append(row);
    }
}

function fetchCommentsAndRenderTable() {
    fetch("/api/moderator")
        .then(response => response.json())
        .then(comments => renderCommentsTable(comments))
}

