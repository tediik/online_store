let url = "http://localhost:9999/api/admin"

fetchUsersAndRenderTable()

function renderUsersTable(users) {
    let table = $('#user-table')
    table.empty()
        .append(`<tr>
                <th>ID</th>
                <th>Email</th>
                <th>Roles</th>
                <th>Edit</th>
                <th>Delete</th>
              </tr>`)
    for (let i = 0; i < users.length; i++) {
        const user = users[i];
        let row = `
                <tr id="tr-${user.id}">
                    <td>${user.id}</td>
                    <td>${user.email}</td>
                    <td>${user.roles}</td>
                    <td>
            <!-- Buttons of the right column of main table-->
                        <button data-user-id="${user.id}" type="button" class="btn btn-success edit-button" data-toggle="modal" data-target="#userModalWindow">
                        Edit
                        </button>
                    </td>
                    <td>
                        <button data-user-id="${user.id}" type="button" class="btn btn-danger delete-button" data-toggle="modal" data-target="#userModalWindow">
                        Delete
                        </button>
                    </td>
                </tr>
                `;
        table.append(row)
    }
}



function fetchUsersAndRenderTable() {
    fetch(url + "/allUsers", {
        method: 'GET',
        headers: {
            'Content-type': 'application/json; charset=UTF-8'
        }

    }).then(response => response.json()).then(users => renderUsersTable(users))
}