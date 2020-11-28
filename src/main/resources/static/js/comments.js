document.addEventListener('DOMContentLoaded', () => {

    let commentsCache;
    let currentUserEmail;
    let productId = decodeURI(document.URL.substring(document.URL.lastIndexOf('/') + 1));
    let allCommentsBlock = document.getElementById('showComments');

    getLoggedInUser().then(showComments);

    async function getLoggedInUser() {
        try {
            await $.ajax({
                //Get current logged in user
                type: "GET",
                url: '/authority/currentUser',
                dataType: "json",
                success: function (response) {
                    currentUserEmail = response.email;
                }
            });
        } catch (e) {
            console.log(e)
        }
    }

    function showComments() {
        let productId = decodeURI(document.URL.substring(document.URL.lastIndexOf('/') + 1));

        $('#showComments').append($(`
            <div class="container">
               <div>
                    <h6>Сортировать комментарии: </h6>
                    <form id="buttonsView" class="btn-group btn-group-toggle" data-toggle="buttons">
                        <label class="btn btn-secondary active">
                             <input type="radio" name="commentsView" id="CommentsNewFirst" autocomplete="off" value="CommentsNewFirst" checked> Новые сначала
                        </label>
                        <label class="btn btn-secondary">
                             <input type="radio" name="commentsView" id="CommentsOldFirst" autocomplete="off" value="CommentsOldFirst"> Старые сначала
                        </label>
                    </form>
               </div>
            </div>
        `))

        $('#CommentsNewFirst,#CommentsOldFirst').on('change', function () {
            commentsCache.reverse(); //переворачивает массив комментариев
            showOrRefreshComments();
        })

        showOrRefreshComments();
    }

    /*Загружает комменты из БД или commentCache.*/
    async function showOrRefreshComments() {

        if (commentsCache == null) {
            await $.ajax({
                //Get comment html code
                type: "GET",
                url: '/api/comments/' + productId,
                dataType: "json",
                success: function (response) {
                    console.log(response);
                    commentsCache = response;
                    if (document.getElementById('CommentsNewFirst').checked) {
                        commentsCache.reverse();
                    }
                }
            });
        }

        if (document.getElementById('commentsList')) {
            document.getElementById('commentsList').remove();
        }

        $('#showComments').append($(`<div class="container-fluid mt-4" id="commentsList"></div>`));

        /* Отображение сперва основных комментариев (не reply) */
        $.each(commentsCache, function (i, comment) {
            if (comment.parentId === null) {
                $('#commentsList').append($(`
                        <div class="media mb-4" id="media${comment.id}"><div>
                        <img id="profilePic" alt="UserPhoto" class="rounded-circle img-responsive mt-2 height=52" src="/uploads/images/${comment.userPhoto}" width="52"></div>
                        <div class="media-body" id='mediaBody${comment.id}'>
                        <h5 class="mt-0">${comment.userEmail} commented on ${comment.timeStamp}</h5>
                        <div class="message" id="commentContent${comment.id}"> ${comment.content}  </div>
                        <button type='button' id='replyButton${comment.id}' class='btn btn-link reply'>Reply</button>
                        <button type='button' id='editButton${comment.id}' class='btn btn-link edit'>Edit</button>
                        <button type='button' id='deleteButton${comment.id}' class='btn btn-link delete'>Delete</button>
                        <div class="replyDisplay" id='replyDisplayId${comment.id}'> </div>
                        <div class="commentBoxSpace" id='commentBoxSpace${comment.id}'></div>
                `))
                deleteAndEditButtonsView(comment)
                replyButtonsView(comment);
            }
        });

        /* Отображение reply комментариев */
        commentsCache.forEach((comment, i) => {
            let replyDisplayId = $('#replyDisplayId' + comment.parentId);
            if (comment.parentId !== null) {
                $(replyDisplayId).append($(`<div class="media mt-4">
                    <div>
                    <img id="profilePic" alt="UserPhoto" class="rounded-circle img-responsive mt-2 height=52" src="/uploads/images/${comment.userPhoto}" width="52"></div>
                    <div class="media-body" id='mediaBody${comment.id}'>
                    <h5 class="mt-0">${comment.userEmail} commented on ${comment.timeStamp} </h5>
                    <div class="message" id="commentContent${comment.id}"> ${comment.content} </div>
                    <button type='button' id='editButton${comment.id}' class='btn btn-link edit'>Edit</button>
                    <button type='button' id='deleteButton${comment.id}' class='btn btn-link delete'>Delete</button>
                     </div>
                `));
                deleteAndEditButtonsView(comment);
            }
        });

        /*Показывает кнопки Edit, Delete только владельцу комментария*/
        function deleteAndEditButtonsView(commentData) {
            let btnEdit = document.getElementById("editButton" + commentData.id);
            let btnDelete = document.getElementById("deleteButton" + commentData.id);
            if (currentUserEmail === commentData.userEmail) {
                btnEdit.hidden = false;
                btnDelete.hidden = false;
            } else {
                btnEdit.hidden = true;
                btnDelete.hidden = true;
            }
        }

        /*Показывает кнопку Reply только залогиненным пользователям*/
        function replyButtonsView(commentData) {
            let btnReply = document.getElementById("replyButton" + commentData.id);
            if (currentUserEmail == null) {
                btnReply.hidden = true;
            } else {
                btnReply.hidden = false;
            }
        }
    }


    /* При нажатии на "Отправить" ответ на комментарий */

    function replySubmit(replyCommentId) {
        let content = document.getElementById(replyCommentId).parentElement.querySelector('textarea').value;
        console.log(content);
        if ($("#replyText").val().trim().length < 1) {
            alert("Please Enter Text...");
        } else {
            let parentId = replyCommentId.replace(/\D/g, '');
            event.preventDefault();
            let productComment = {parentId, content, productId};
            $.ajax({
                //Post comment
                url: '/api/comments',
                method: "POST",
                data: JSON.stringify(productComment),
                dataType: 'json',
                cache: false,
                contentType: "application/json; charset=utf-8",
                success: function (response) {
                    let comment = response.comments[0];
                    let commentBox = document.getElementById('commentBoxSpace' + parentId).firstElementChild;
                    console.log(commentBox);
                    commentBox.remove();
                    let replyDisplayId = $('#replyDisplayId' + parentId);

                    $(replyDisplayId).append($(`
                                <div class="media mt-4">
                                    <div>
                                        <img id="profilePic" alt="UserPhoto" class="rounded-circle img-responsive mt-2"
                                        height="52" src="/uploads/images/${comment.userPhoto}" width="52">
                                    </div>
                                    <div class="media-body" id="mediaBody${comment.id}">
                                    <h5 class="mt-0">${comment.userEmail} commented on ${comment.timeStamp}</h5>
                                    <div class="message" id="commentContent${comment.id}">${comment.content}</div>
                                    <button type='button' id='editButton${comment.id}' class='btn btn-link edit'>Edit</button>
                                    <button type='button' id='deleteButton${comment.id}' class='btn btn-link delete'>Delete</button>
                                </div>
                            </div>
                    `).last());

                    commentsCache.push(response.comments[0]);
                }
            })
        }
    }

    /* При нажатии на "Отправить" новый комментарий */

    $('#commentForm').on('submit', function (event) {
        event.preventDefault();
        if ($("#commentForm").find('input:text').val().trim().length < 1) {
            alert("Please Enter Text...");
        } else {
            let formData = $(this).serializeArray();
            let formDataObject = {};
            $.each(formData,
                function (i, v) {
                    formDataObject[v.name] = v.value;
                });
            formDataObject["productId"] = productId;
            $.ajax({
                //Post comment
                url: '/api/comments',
                method: "POST",
                data: JSON.stringify(formDataObject),
                dataType: 'json',
                cache: false,
                contentType: "application/json; charset=utf-8",
                success: function (response) {
                    /*
                                        var comment = response.comments[0];
                                        $('#showComments').append($(`
                                            <div class="media mb-4">
                                                <div>
                                                    <img id="profilePic" alt="UserPhoto" class="rounded-circle img-responsive mt-2"
                                                    height="52" src="/uploads/images/${comment.userPhoto}" width="52">
                                                </div>
                                                <div class="media-body" id='mediaBody${comment.id}'>
                                                <h5 class="mt-0">${comment.userEmail} commented on ${comment.timeStamp}</h5>
                                                <div class="message" id="commentContent${comment.id}"> ${comment.content}  </div>
                                                <button type='button' id='replyButton${comment.id}' class='btn btn-link reply'>Reply</button>
                                                <button type='button' id='editButton${comment.id}' class='btn btn-link edit'>Edit</button>
                                                <button type='button' id='deleteButton${comment.id}' class='btn btn-link delete'>Delete</button>
                                                <div class="replyDisplay" id='replyDisplayId${comment.id}'> </div>
                                                <div class="commentBoxSpace" id='commentBoxSpace${comment.id}'></div>
                                            </div>
                                        `))*/

                    $('#commentForm').find('input:text').val('');
                    commentsCache = null;
                    showOrRefreshComments();
                }

            })
        }
    });


    /* При нажатии на "reply" */
    function replyOnAComment(replyCommentId) {
        replyCommentId = replyCommentId.replace(/\D/g, '');


        let commentBox = $(`
                                <div class="well">
                                    <h4>Leave a Comment:</h4>
                                        <div class="form-group">
                                            <textarea class="form-control" id="replyText" rows="3"></textarea>
                                        </div>
                                     <button type="button"  id='submitReplyBtn${replyCommentId}' class="btn btn-primary">Ответить</button>
                                     <button type="button"  id='cancelReplyBtn${replyCommentId}' class="btn btn-secondary">Отмена</button>
                                </div>`)

        $('#commentBoxSpace' + replyCommentId).html(commentBox);
    }


    /* При нажатии на "edit" */
    function editComment(commentId) {
        commentId = commentId.replace(/\D/g, '');
        let commentText = document.getElementById('commentContent' + commentId).textContent.trim();

        let editCommentBox = document.createElement("div");
        editCommentBox.className = 'well';
        editCommentBox.setAttribute("id", `commentEditing${commentId}`);
        editCommentBox.innerHTML = `
                                    <h4>Editing Comment:</h4>
                                        <div class="form-group">
                                            <textarea class="form-control" id="editText${commentId}" rows="3">${commentText}</textarea>
                                        </div>
                                     <button type="button" id='submitEditBtn${commentId}' class="btn btn-primary">Изменить</button>
                                     <button type="button" id='cancelEditBtn${commentId}' class="btn btn-secondary">Отмена</button>
        `;


        let commentContent = document.getElementById('commentContent' + commentId);
        document.getElementById(`mediaBody${commentId}`).insertBefore(editCommentBox, commentContent);

        $(commentContent).hide();
        if (document.getElementById('replyButton' + commentId)) { // кнопки reply нет у комментариев, которые сами являются reply (с parentId).
            $('#replyButton' + commentId).hide();
        }
        $('#editButton' + commentId).hide();
        $('#deleteButton' + commentId).hide();

    }

    /* При нажатии на "Изменить" при редактировании комментария*/
    async function submitEdit(commentId) {
        let editBlock = document.getElementById(commentId).parentElement;

        commentId = commentId.replace(/\D/g, '');
        let editContent = $("#editText" + commentId).val().trim();
        console.log(editContent);

        if (editContent.length < 1) {
            alert("Please Enter Text...");
        } else {
            let dataObject = {};
            dataObject["id"] = commentId;
            dataObject["content"] = editContent;

            await (async () => {
                const rawResponse = await fetch('/api/comments', {
                    method: 'PUT',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(dataObject)
                })
                    .then(function (response) {

                        let commentContent = document.getElementById('commentContent' + commentId.replace(/\D/g, ''));
                        commentContent.textContent = editContent;
                        console.log(commentContent);

                        $(commentContent).show();
                        editBlock.remove();
                        if (document.getElementById('replyButton' + commentId.replace(/\D/g, ''))) {
                            $('#replyButton' + commentId.replace(/\D/g, '')).show();
                        }
                        $('#editButton' + commentId.replace(/\D/g, '')).show();
                        $('#deleteButton' + commentId.replace(/\D/g, '')).show();

                        commentsCache.forEach((item, i) => {
                            if (item.id == commentId) {
                                item.content = editContent;
                            }
                        });
                        console.log(commentsCache);
                    })
                    .catch(err => console.log(err));
            })();

        }
    }


    /* При нажатии на "delete" */

    function deleteComment(commentId) {
        commentId = commentId.replace(/\D/g, '');
        console.log('Номер коммента' + commentId);

        if(document.getElementById('deleteCommentModal')) {
            document.getElementById('deleteCommentModal').remove();
        }

        let deleteCommentModal = document.createElement("div");
        deleteCommentModal.className = 'modal fade';
        deleteCommentModal.setAttribute("id", `deleteCommentModal`);
        deleteCommentModal.setAttribute("tabindex", `-1`);
        deleteCommentModal.setAttribute("role", `dialog`);
        deleteCommentModal.setAttribute("aria-labelledby", `myModalLabel`);
        deleteCommentModal.setAttribute("aria-hidden", `true`);

        deleteCommentModal.innerHTML = `
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal"
                                    aria-hidden="true">&times;</button>
                        </div>
                        <div class="modal-body">
                            <h4>Вы действительно хотите удалить этот комментарий?</h4>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" id="btnDeleteCommentYes${commentId}" >Да</button>
                            <button type="button" class="btn btn-secondary" id="btnDeleteCommentNo" data-dismiss="modal">Нет</button>
                        </div>
                    </div>
                </div>
        `;

        allCommentsBlock.append(deleteCommentModal);
        let id = $('#deleteCommentModal').data('id');
        $('#deleteCommentModal').data('id', commentId).modal('show');

    }

    document.addEventListener('click', (event) => {
        if (event.target && event.target.textContent === 'Да' && event.target.id.includes('btnDeleteCommentYes')) {
            $('#deleteCommentModal').modal('hide');
            let commentId = event.target.id.replace(/\D/g, '');

            console.log(commentId);
            console.log(event.target.id);

            let replyDisplay = document.getElementById('replyDisplayId' + commentId);
            console.log(replyDisplay);
            if (replyDisplay && replyDisplay.childElementCount > 0) {
                console.log("comment has replies");

                let dataObject = {};
                dataObject["id"] = commentId;
                dataObject["customer_id"] = "null";
                dataObject["content"] = "-------Комментарий был удалён-------";

                (async () => {
                    const rawResponse = await fetch('/api/comments', {
                        method: 'PUT',
                        headers: {
                            'Accept': 'application/json',
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(dataObject)
                    })
                        .then(function (response) {
                            commentsCache.forEach((item, i) => {

                                if (item.id == commentId) {
                                    item.content = "------Комментарий был удалён-------";
                                                        // ОТСУТСТВУЮТ КНОПКИ EDIT DELETE - HIDDEN!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                }
                            });
                            console.log(commentsCache);
                            document.getElementById('commentContent' + commentId).textContent = "------Комментарий был удалён-------";
                        })
                        .catch(err => console.log(err));
                })();

            } else {
                console.log("comment doesn't have any replies");

                (async () => {
                    const rawResponse = await fetch('/api/comments/' + commentId, {
                        method: 'DELETE',
                    })
                        .then(function () {

                            document.getElementById('mediaBody' + commentId).parentElement.remove();
                            commentsCache.forEach((item, i) => {
                                if (item.id == commentId) {
                                    commentsCache.splice(i, 1);
                                }
                            })
                            console.log(commentsCache);

                        })
                        .catch(err => console.error(err + "Ошибка"));
                })();
            }
        }
    });

        /* Обработчики событий на все кнопки существующих комментариев (кроме модального Delete) */

    allCommentsBlock.addEventListener('click', (event) => {
        if (event.target) {
            let commentId = event.target.id;
            let commentBtnName = event.target.textContent;

            if (commentBtnName === 'Reply') {
                console.log("pressed Reply on " + commentId)
                replyOnAComment(commentId);
            } else if (commentBtnName === 'Edit') {
                console.log("pressed Edit");
                editComment(commentId);
            } else if (commentBtnName === 'Изменить') {
                console.log("pressed Изменить");
                submitEdit(commentId);
            } else if (commentBtnName === 'Delete') {
                console.log("pressed Delete")
                deleteComment(commentId);
            } else if (commentBtnName === 'Отмена') {
                if (commentId.includes('cancelReplyBtn')) {
                    $('#commentBoxSpace' + commentId.replace(/\D/g, '')).html("");
                } else if (commentId.includes('cancelEditBtn')) {
                    let commentContent = document.getElementById('commentContent' + commentId.replace(/\D/g, ''));
                    let editBlock = document.getElementById(commentId).parentElement;
                    $(commentContent).show();
                    editBlock.remove();
                    if (document.getElementById('replyButton' + commentId.replace(/\D/g, ''))) {
                        $('#replyButton' + commentId.replace(/\D/g, '')).show();
                    }
                    $('#editButton' + commentId.replace(/\D/g, '')).show();
                    $('#deleteButton' + commentId.replace(/\D/g, '')).show();
                }
            } else if (commentBtnName === 'Ответить' && commentId.includes('submitReplyBtn')) {
                console.log("pressed submitReplyBtn" + commentId)
                replySubmit(commentId);
            }
        }
    });


});


/*
$(document).ready(function () {

    let commentsCache;
    let currentUserEmail;

    /!* Срабатывает при загрузке страницы: загружает авторизованного юзера и передает его email в переменную currentUserEmail *!/

    async function getLoggedInUser() {
        try {
            await $.ajax({
                //Get current logged in user
                type: "GET",
                url: '/authority/currentUser',
                dataType: "json",
                success: function (response) {
                    currentUserEmail = response.email;
                }
            });
        } catch (e) {
            console.log(e)
        }
    }

    /!* Срабатывает при загрузке страницы: отоброжает кнопки сортировки *!/

    function showComments() {
        let productId = decodeURI(document.URL.substring(document.URL.lastIndexOf('/') + 1));

        $('#showComments').append($(`
            <div class="container">
               <div>
                    <h6>Сортировать комментарии: </h6>
                    <form id="buttonsView" class="btn-group btn-group-toggle" data-toggle="buttons">
                        <label class="btn btn-secondary active">
                             <input type="radio" name="commentsView" id="CommentsNewFirst" autocomplete="off" value="CommentsNewFirst" checked> Новые сначала
                        </label>
                        <label class="btn btn-secondary">
                             <input type="radio" name="commentsView" id="CommentsOldFirst" autocomplete="off" value="CommentsOldFirst"> Старые сначала
                        </label>
                    </form>
               </div>
            </div>
        `))

        $('#CommentsNewFirst,#CommentsOldFirst').on('change', function () {
            commentsCache.reverse(); //переворачивает массив комментариев
            showOrRefreshComments();
        })

        showOrRefreshComments();
    }

    /!*Загружает комменты из БД или commentCache.*!/

    async function showOrRefreshComments() {

        if (commentsCache == null) {
            await $.ajax({
                //Get comment html code
                type: "GET",
                url: '/api/comments/' + productId,
                dataType: "json",
                success: function (response) {
                    console.log(response);
                    commentsCache = response;
                    if (document.getElementById('CommentsOldFirst').checked) {
                        commentsCache.reverse();
                    }
                }
            });
        }

        if (document.getElementById('commentsList')) {
            document.getElementById('commentsList').remove();
        }

        $('#showComments').append($(`<div class="container-fluid mt-4" id="commentsList"></div>`));

        /!* Отображение сперва основных комментариев (не reply) *!/
        $.each(commentsCache, function (i, comment) {
            if (comment.parentId === null) {
                $('#commentsList').append($(`
                        <div class="media mb-4" id="media${comment.id}"><div>
                        <img id="profilePic" alt="UserPhoto" class="rounded-circle img-responsive mt-2 height=52" src="/uploads/images/${comment.userPhoto}" width="52"></div>
                        <div class="media-body" id='mediaBody${comment.id}'>
                        <h5 class="mt-0">${comment.userEmail} commented on ${comment.timeStamp}</h5>
                        <div class="message" id="commentContent${comment.id}"> ${comment.content}  </div>
                        <button type='button' id='replyButton${comment.id}' class='btn btn-link reply'>Reply</button>
                        <button type='button' id='editButton${comment.id}' class='btn btn-link edit'>Edit</button>
                        <button type='button' id='deleteButton${comment.id}' class='btn btn-link delete'>Delete</button>
                        <div class="replyDisplay" id='replyDisplayId${comment.id}'> </div>
                        <div class="commentBoxSpace" id='commentBoxSpace${comment.id}'></div>
                `))
                deleteAndEditButtonsView(comment)
                replyButtonsView(comment);
            }
        });

        /!* Отображение reply комментариев *!/
        $.each(commentsCache, function (i, comment) {
            let replyDisplayId = $('#replyDisplayId' + comment.parentId);
            if (comment.parentId !== null) {
                $(replyDisplayId).append($(`<div class="media mt-4">
                    <div>
                    <img id="profilePic" alt="UserPhoto" class="rounded-circle img-responsive mt-2 height=52" src="/uploads/images/${comment.userPhoto}" width="52"></div>
                    <div class="media-body" id='mediaBody${comment.id}'>
                    <h5 class="mt-0">${comment.userEmail} commented on ${comment.timeStamp} </h5>
                    <div class="message" id="commentContent${comment.id}"> ${comment.content} </div>
                    <button type='button' id='editButton${comment.id}' class='btn btn-link edit'>Edit</button>
                    <button type='button' id='deleteButton${comment.id}' class='btn btn-link delete'>Delete</button>
                     </div>
                `));
                deleteAndEditButtonsView(comment);
            }
        });

        /!*Показывает кнопки Edit, Delete только владельцу комментария*!/
        function deleteAndEditButtonsView(commentData) {
            let btnEdit = document.getElementById("editButton" + commentData.id);
            let btnDelete = document.getElementById("deleteButton" + commentData.id);
            if (currentUserEmail === commentData.userEmail) {
                btnEdit.hidden = false;
                btnDelete.hidden = false;
            } else {
                btnEdit.hidden = true;
                btnDelete.hidden = true;
            }
        }

        /!*Показывает кнопку Reply только залогиненным пользователям*!/
        function replyButtonsView(commentData) {
            let btnReply = document.getElementById("replyButton" + commentData.id);
            if (currentUserEmail == null) {
                btnReply.hidden = true;
            } else {
                btnReply.hidden = false;
            }
        }
    }

    $(function () {
        let productId = decodeURI(document.URL.substring(document.URL.lastIndexOf('/') + 1));

        getLoggedInUser().then(showComments);

        $('#commentForm').on('submit', function (event) {
            event.preventDefault();
            if ($("#commentForm").find('input:text').val().trim().length < 1) {
                alert("Please Enter Text...");
                return;
            } else {
                let formData = $(this).serializeArray();
                let formDataObject = {};
                $.each(formData,
                    function (i, v) {
                        formDataObject[v.name] = v.value;
                    });
                formDataObject["productId"] = productId;
                $.ajax({
                    //Post comment
                    url: '/api/comments',
                    method: "POST",
                    data: JSON.stringify(formDataObject),
                    dataType: 'json',
                    cache: false,
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        $('#commentForm').find('input:text').val('');
                        commentsCache = null;
                        showOrRefreshComments();
                    }
                })
            }
        });

        /!* При нажатии на "reply" *!/
        $(document).on('click', '.reply', function (e) {
            //Reply to this id
            let commentId = $(this).attr("id");

            commentId = commentId.replace(/\D/g, '');
            let commentBox = $(`
                                <div class="well">
                                    <h4>Leave a Comment:</h4>
                                        <div class="form-group">
                                            <textarea class="form-control" id="replyText" rows="3"></textarea>
                                        </div>
                                     <button type="button" id='submitReplyBtn' class="btn btn-primary">Submit</button>
                                </div>`)

            $('#commentBoxSpace' + commentId).html(commentBox);

            $('#submitReplyBtn').on('click', function (event) {
                let productId = decodeURI(document.URL.substring(document.URL.lastIndexOf('/') + 1));

                if ($("#replyText").val().trim().length < 1) {
                    alert("Please Enter Text...");
                    return;
                } else {

                    let parentId = commentId;
                    event.preventDefault();
                    let content = $('#replyText').val();
                    let productComment = {parentId, content, productId};
                    $.ajax({
                        //Post comment
                        url: '/api/comments',
                        method: "POST",
                        data: JSON.stringify(productComment),
                        dataType: 'json',
                        cache: false,
                        contentType: "application/json; charset=utf-8",
                        success: function (response) {
                            commentsCache = null;
                            showOrRefreshComments();
                        }
                    });
                }
            });
        });

        /!* При нажатии на "delete" *!/
        $(document).on('click', '.delete', function (e) {
            e.preventDefault();
            let commentId = $(this).attr("id").toString().substring(12); // получает id косментария (только цифру)

            $('#showComments').append($(`
            <div class="modal fade" id="deleteCommentModal" tabindex="-1" role="dialog"
                 aria-labelledby="myModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal"
                                    aria-hidden="true">&times;</button>
                        </div>
                        <div class="modal-body">
                            <h4>Вы действительно хотите удалить этот комментарий?</h4>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" id="btnDeleteCommentYes" href="#">Да</button>
                            <button type="button" class="btn btn-secondary" id="btnDeleteCommentNo" data-dismiss="modal">Нет</button>
                        </div>
                    </div>
                </div>
            </div>
            `));

            $('#deleteCommentModal').data('id', commentId).modal('show');

            $('#btnDeleteCommentYes').click(async function () {
                let id = $('#deleteCommentModal').data('id');

                await (async () => {
                    const rawResponse = await fetch('/api/comments/' + id, {
                        method: 'DELETE',
                    })
                        .then(function () {
                            commentsCache = null;
                            showOrRefreshComments();
                        })
                        .catch(err => console.error(err + "Ошибка"));
                })();
                $('#deleteCommentModal').modal('hide');
            });
        });

        /!* При нажатии на "edit" *!/
        $(document).on('click', '.edit', function (e) {
            let commentId = $(this).attr("id");
            commentId = commentId.replace(/\D/g, '');

            let commentText = document.getElementById('commentContent' + commentId).innerHTML.trim();

            let editCommentBox = $(`
                                <div class="well" id="commentEditing">
                                    <h4>Editing Comment:</h4>
                                        <div class="form-group">
                                            <textarea class="form-control" id="editText" rows="3">${commentText}</textarea>
                                        </div>
                                     <button type="button" id='submitEditBtn' class="btn btn-primary">Изменить</button>
                                     <button type="button" id='cancelEditBtn' class="btn btn-secondary">Отмена</button>
                                </div>`
            );



            if (document.getElementById('replyButton' + commentId)) { // кнопки reply нет у комментариев, которые сами являются reply (с parentId).
                document.getElementById('replyButton' + commentId).hidden = true
            }
            document.getElementById('editButton' + commentId).hidden = true;
            document.getElementById('deleteButton' + commentId).hidden = true;

            $('#commentContent' + commentId).html(editCommentBox);

            /!* При нажатии на "Отмена" *!/
            $('#cancelEditBtn').click(function () {
                showOrRefreshComments();
            });

            /!* При нажатии на "Изменить" *!/
            $('#submitEditBtn').click(async function () {
                e.preventDefault();
                let idComment = $(e.target.parentNode).attr("id");
                idComment = idComment.replace(/\D/g, '');
                let editContent = $("#editText").val().trim();

                if (editContent.length < 1) {
                    alert("Please Enter Text...");
                } else {
                    let dataObject = {};
                    dataObject["id"] = idComment;
                    dataObject["content"] = editContent;

                    await (async () => {
                        const rawResponse = await fetch('/api/comments', {
                            method: 'PUT',
                            headers: {
                                'Accept': 'application/json',
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify(dataObject)
                        })
                            .then(function (response) {
                                commentsCache = null;
                                showOrRefreshComments();
                            })
                            .catch(err => console.log(err));
                    })();

                }
            });

        });
    });

});

*/
