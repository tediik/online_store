$(document).ready(function () {
    function showComments() {
        let productId = decodeURI(document.URL.substring(document.URL.lastIndexOf('/') + 1));
        $.ajax({
            //Get comment html code
            type: "GET",
            url: '/api/comments/' + productId,
            dataType: "json",
            success: function (response) {
                $.each(response, function (i, comment) {
                    if (comment.parentId === null) {
                        $('#showComments').append($(`
                        <div class="media mb-4">
                            <div>
                                <img id="profilePic" alt="UserPhoto" class="rounded-circle img-responsive mt-2 height=52" src="/uploads/images/${comment.userPhoto}" width="52">
                            </div>
                            <div class="media-body" id='mediaBody" + ${comment.id} + "'>
                                <h5 class="mt-0">${comment.userEmail} commented on ${comment.timeStamp}</h5>
                                <div class="message">${comment.content}</div>
                                <button type='button' id='button${comment.id}' class='btn btn-link reply'>Ответить</button>
                                <button type='button' id='button${comment.id}' class='btn btn-link report'>Пожаловаться</button>
                                <div class="reportCommentBoxSpace" id='reportCommentBoxSpace${comment.id}'></div>
                                <div class="replyDisplay" id='replyDisplayId${comment.id}'> </div>
                                <div class="commentBoxSpace" id='commentBoxSpace${comment.id}'></div>
                            </div>
                    `))
                    }

                    var replyDisplayId = $('#replyDisplayId' + comment.parentId);
                    if (comment.parentId !== null) {
                        $(replyDisplayId).append($(`
                            <div class="media mt-4">
                                <div>
                                    <img id="profilePic" alt="UserPhoto" class="rounded-circle img-responsive mt-2 height=52" 
                                    src="/uploads/images/${comment.userPhoto}" width="52">
                                </div>
                            <div class="media-body"> 
                                <h5 class="mt-0">${comment.userEmail} commented on ${comment.timeStamp} </h5>
                                <div class="message">${comment.content}</div>
                                <button type='button' id='button${comment.id}' class='btn btn-link report'>Пожаловаться</button>
                                <div class="reportCommentBoxSpace" id='reportCommentBoxSpace${comment.id}'></div>
                            </div>
                        `));
                    }
                })
            }
        });
    }

    $(function () {
        let productId = decodeURI(document.URL.substring(document.URL.lastIndexOf('/') + 1));

        showComments();
        $('#commentForm').on('submit', function (event) {
            event.preventDefault();
            if ($("#commentForm").find('input:text').val().trim().length < 1) {
                alert("Please Enter Text...");
                return;
            } else {

                var formData = $(this).serializeArray();
                var formDataObject = {};
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
                    success: function (response, textStatus, request) {
                        if (request.status === 201) {
                            let stopAlert = 'В вашем комментарии есть запрещенные слова (' + response + '). \nПожалуйста удалите их!'
                            toastr.error(stopAlert);
                            return;
                        }
                        var comment = response.comments[0];
                        $('#showComments').append($(`
                            <div class="media mb-4">
                                <div>
                                    <img id="profilePic" alt="UserPhoto" class="rounded-circle img-responsive mt-2"
                                    height="52" src="/uploads/images/${comment.userPhoto}" width="52">
                                </div>
                                <div class="media-body" id='mediaBody${response.id}'>
                                <h5 class="mt-0">${comment.userEmail} commented on ${comment.timeStamp}</h5>
                                <div class="message">${comment.content}</div>
                                <button type='button' id='button${comment.id}' class='btn btn-link reply'>Ответить</button>
                                <button type='button' id='button${comment.id}' class='btn btn-link report'>Пожаловаться</button>
                                <div class="replyDisplay" id='replyDisplayId${comment.id}'> </div>
                                <div class="commentBoxSpace" id='commentBoxSpace${comment.id}'></div>
                                <div class="reportCommentBoxSpace" id='reportCommentBoxSpace${comment.id}'></div>
                            </div>
                        `))

                        $('#commentForm').find('input:text').val('');
                    }
                })
            }
        });

        $(document).on('click', '.reply', function (e) {
            //Reply to this id
            var commentId = $(this).attr("id");

            commentId = commentId.replace(/\D/g, '');
            //Добавил проверку, анонимный пользователи или нет
            //От этого, показываем разные формы
            if ($("#sayYesComment").length) {
                var commentBox = $(`
                                <div class="well">
                                    <h4>Leave a Comment:</h4>
                                        <div class="form-group">
                                            <textarea class="form-control" id="replyText" rows="3"></textarea>
                                        </div>
                                     <button type="button" id='submitReplyBtn' class="btn btn-primary">Submit</button>
                                </div>`)
                $('#commentBoxSpace' + commentId).html(commentBox);
            } else {
                var commentNone = $(`
                                <div class="well">
                                    <h5 style="color: blue">Авторизуйтесь или зарегистрируйтесь, чтобы ответить на комментарий</h5>
                                </div>`)
                $('#commentBoxSpace' + commentId).html(commentNone);
            }
            $('#submitReplyBtn').on('click', function (event) {
                let productId = decodeURI(document.URL.substring(document.URL.lastIndexOf('/') + 1));

                if ($("#replyText").val().trim().length < 1) {
                    alert("Please Enter Text...");
                    return;
                } else {

                    var parentId = commentId;
                    event.preventDefault();
                    var content = $('#replyText').val();
                    var productComment = {parentId, content, productId};
                    $.ajax({
                        //Post comment
                        url: '/api/comments',
                        method: "POST",
                        data: JSON.stringify(productComment),
                        dataType: 'json',
                        cache: false,
                        contentType: "application/json; charset=utf-8",
                        success: function (response, textStatus, request) {
                            if (request.status === 201) {
                                let stopAlert = 'В вашем ответе есть запрещенные слова (' + response + '). \nПожалуйста удалите их!'
                                toastr.error(stopAlert);
                                return;
                            }
                            var comment = response.comments[0];
                            commentBox.remove();
                            var replyDisplayId = $('#replyDisplayId' + commentId);

                            $(replyDisplayId).append($(`
                                <div class="media mt-4">
                                    <div>
                                        <img id="profilePic" alt="UserPhoto" class="rounded-circle img-responsive mt-2"
                                        height="52" src="/uploads/images/${comment.userPhoto}" width="52">
                                    </div>
                                    <div class="media-body">
                                    <h5 class="mt-0">${comment.userEmail} commented on ${comment.timeStamp}</h5>
                                    <div class="message">${comment.content}</div>
                                    <button type='button' id='button${comment.id}' class='btn btn-link report'>Пожаловаться</button>
                                    <div class="reportCommentBoxSpace" id='reportCommentBoxSpace${comment.id}'></div>
                                </div>
                            </div>
                        `).last());
                        }
                    });
                }
            });
        });
    });
});

