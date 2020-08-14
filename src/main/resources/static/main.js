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

                        $(tableBody).append($('<tr id="userRowId' + user.id + '">')
                            .append($("<td>").append(user.id))
                            .append($("<td>").append(user.email))
                            .append($("<td>").append(user.authorities.map(role => role.authority).join(" ")))

                            .append($("<td>").append("<button class='btn-info updateBtn '>Edit</button>").click(function (event) {
                                $('.update-example #updateId').val(user.id);
                                $('.update-example #updateEmail').val(user.email);
                                //$('.update-example #updatePassword').val(user.password);
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
                            .append($("<td>").append(data.authorities.map(role => role.authority).join(" ")))

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
            $('.nav-tabs a[href="#nav-home"]').tab('show');
        });
    });

    $(function () {


        $('#updateSubmitBtn').on('click', function () {
            var id = $('#updateId').val();
            var email = $('#updateEmail').val();
            var password = $('#updatePassword').val();
            var roles = $('#updateRoles').val();

            var user = {id, email, password, roles};

            $.ajax({
                type: 'PUT',
                url: '/api/users/',
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(user),
                success: function (data) {

                    $(function updateFunc() {
                        $('#editBtn').on('click', function () {
                            $('.update-example #updateId').val(data.id);
                            $('.update-example #updateEmail').val(data.email);
                            $('.update-example #updatePassword').val(data.password);
                            $('.update-example #updateModal').modal('show');
                        });
                    });

                    $(function deleteFunc() {
                        $('#deleteBtn').on('click', function () {
                            $('.delete-example #deleteId').val(data.id);
                            $('.delete-example #deleteEmail').val(data.email);
                            $('.delete-example #deletePassword').val(data.password);
                            $('.delete-example #deleteModal').modal('show');
                        });
                    });

                    var new_row = `
                        <tr id="userRowId${data.id}">
                            <td>${data.id} </td>
                            <td>${data.email} </td>
                            <td>${data.authorities.map(role => role.authority).join(" , ")} </td>
                            <td> <button class='btn-info' id='editBtn'>Edit</button></td>
                            <td> <button class='btn-danger' id='deleteBtn'>Delete</button></td>
                        </tr>`;

                    $('#userRowId' + id).replaceWith(new_row);
                },
                error: function (jqXhr, textStatus, errorThrown) {
                    console.log(errorThrown);
                }
            });
        });
    });

    $(function () {
        $('#deleteSubmitBtn').on('click', function () {
            var $deleteId = $('#deleteId').val();
            $.ajax(
                {
                    type: 'DELETE',
                    url: '/api/users/' + $deleteId,
                    contentType: "application/json; charset=utf-8",
                    success: function () {
                        $('#userRowId' + $deleteId).remove();
                    },
                    error: function (jqXhr, textStatus, errorThrown) {
                        console.log(errorThrown);
                    }
                });

        })
    });


    $(function () {
        $.ajax(
            {
                type: "GET",
                url: '/api/roles',
                dataType: "json",
                success: function (roles) {
                    for (let i = 0; i < roles.length; i++) {

                        $("#updateRoles").append('<option value=' + roles[i].authority + '>' + roles[i].authority + '</option>');
                        $("#deleteRoles").append('<option value=' + roles[i].authority + '>' + roles[i].authority + '</option>');
                        $("#addRoles").append('<option value=' + roles[i].authority + '>' + roles[i].authority + '</option>');
                    }
                },
                error: function (jqXhr, textStatus, errorThrown) {
                    console.log(errorThrown);
                }

            });
    });


});
