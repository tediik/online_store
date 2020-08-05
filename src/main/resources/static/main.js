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


                        // user.roles.forEach(element => console.log(element.authority));


                        $(tableBody).append($('<tr id="userRowId' + user.id + '">')
                            .append($("<td>").append(user.id))
                            .append($("<td>").append(user.email))
                            .append($("<td>").append(user.roles.map(role => role.name).join(" ")))

                            .append($("<td>").append("<button class='btn-info updateBtn '>Edit</button>").click(function (event) {
                                $('.update-example #updateId').val(user.id);
                                $('.update-example #updateEmail').val(user.email);
                                $('.update-example #updatePassword').val(user.password);
                                $('.update-example #updateModal').modal('show');
                            }))

                            .append($("<td>").append("<button class='btn-danger deleteBtn'>Delete</button>").click(function (event) {
                                $('.delete-example #deleteId').val(user.id);
                                $('.delete-example #deleteEmail').val(user.email);
                                $('.delete-example #deletePassword').val(user.password);
                                $('.delete-example #deleteModal').modal('show');
                            })));
                    })
                }
            });
    });



    $(function () {
        const tableBody = document.querySelector("#user-table > tbody");

        $('#addBtn').on('click', function () {
            var email = $('#addEmail').val();
            var password = $('#addPassword').val();
            var roles = $('#addRoles').val();
            var user = {email, password, roles};
            $.ajax(
                {
                    type: 'POST',
                    url: '/api/users/',
                    dataType: "json",
                    data: JSON.stringify(user),
                    contentType: "application/json; charset=utf-8",
                    success: function (data) {

                        console.log(data);

                        $('#table-body').append($('<tr id="userRowId' + data.id + '">')
                            .append($("<td>").append(data.id))
                            .append($("<td>").append(data.email))
                            .append($("<td>").append(data.roles.map(role => role.name).join(" ")))

                            .append($("<td>").append("<button class='btn-info updateBtn '>Edit</button>").click(function (event) {
                                $('.update-example #updateId').val(data.id);
                                $('.update-example #updateEmail').val(data.email);
                                $('.update-example #updatePassword').val(data.password);
                                $('.update-example #updateModal').modal('show');
                            }))

                            .append($("<td>").append("<button class='btn-danger deleteBtn'>Delete</button>").click(function (event) {
                                $('.delete-example #deleteId').val(data.id);
                                $('.delete-example #deleteEmail').val(data.email);
                                $('.delete-example #deletePassword').val(data.password);
                                $('.delete-example #deleteModal').modal('show');
                            })));


                    },
                    error: function (jqXhr, textStatus, errorThrown) {
                        console.log(errorThrown);
                    }
                });

        });
    });





});
