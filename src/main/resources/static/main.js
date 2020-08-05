$(document).ready(function () {
    const tableBody = document.querySelector("#user-table > tbody");


    $(function () {
        $.ajax(
            {
                type: "GET",
                url: '/api/users/',
                dataType: "json",
                success: function (users) {
                    $.each(users, function (i, user) {


                        user.roles.forEach(element => console.log(element.authority));


                        $(tableBody).append($('<tr id="userRowId' + user.id + '">')
                            .append($("<td>").append(user.id))
                            .append($("<td>").append(user.email))
                            .append($("<td>").append(user.roles.map(role => role.name).join(" ")))
                            .append($("<td>").append("<button class='btn-info updateBtn '>Edit</button>").click(function (event) {

                                $('.update-example #updateId').val(user.id);
                                $('.update-example #updateEmail').val(user.username);
                                $('.update-example #updatePassword').val(user.password);
                                $('.update-example #updateModal').modal('show');
                            }))

                            .append($("<td>").append("<button class='btn-danger deleteBtn'>Delete</button>").click(function (event) {

                                $('.delete-example #deleteId').val(user.id);
                                $('.delete-example #deleteEmail').val(user.username);
                                $('.delete-example #deletePassword').val(user.password);
                                $('.delete-example #deleteModal').modal('show');
                            })));
                    })
                }
            });
    });
});
