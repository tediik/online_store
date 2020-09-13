$(document).ready(function () {
    $(function () {
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
                console.log(formDataObject);
                $.ajax({
                    //Post comment
                    url: '/api/comments',
                    method: "POST",
                    data: JSON.stringify(formDataObject),
                    dataType: 'json',
                    cache: false,
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        var timeStamp = new Date(response.commentDate).toISOString().replace(/T/, " ").replace(/:00.000Z/, "").split('.')[0];

                        $('#showComments').append($(`<div class="media mb-4">
                            <div>
                                    <img id="profilePic" alt="UserPhoto" class="rounded-circle img-responsive mt-2"
                                         height="52" src="/uploads/images/${response.customer.profilePicture}" width="52"></div>
                                <div class="media-body" id='mediaBody${response.id}'>
                                    <h5 class="mt-0">${response.customer.email} commented on ${timeStamp}</h5>
                                    <div class="message">${response.content}</div>
                                   <button type="button" id='button${response.id}' class='btn btn-link reply'>Reply</button>
                                   <div class="replyDisplay" id='replyDisplayId${response.id}'> </div>
                                   <div class="commentBoxSpace" id='commentBoxSpace${response.id}'></div>
                               </div>`))

                        $('#commentForm').find('input:text').val('');
                    }

                })
            }
        });

        $(document).on('click', '.reply', function (e) {
            //Reply to this id
            var commentId = $(this).attr("id");
            commentId = commentId.replace(/\D/g, '');
            var commentBox = $(`
                                <div class="well">
                                    <h4>Leave a Comment:</h4>
                                        <div class="form-group">
                                            <textarea class="form-control" id="replyText" rows="3"></textarea>
                                        </div>
                                        <button type="button" id='submitReplyBtn' class="btn btn-primary">Submit</button>
                                </div>`)

            $('#commentBoxSpace' + commentId).html(commentBox);

            $('#submitReplyBtn').on('click', function (event) {
                if ($("#replyText").val().trim().length < 1) {
                    alert("Please Enter Text...");
                    return;
                }
                else {
                    var parentId = commentId;
                    event.preventDefault();
                    var content = $('#replyText').val();
                    var productComment = {parentId, content};
                    $.ajax({
                        //Post comment
                        url: '/api/comments',
                        method: "POST",
                        data: JSON.stringify(productComment),
                        dataType: 'json',
                        cache: false,
                        contentType: "application/json; charset=utf-8",
                        success: function (response) {
                            var timeStamp = new Date(response.commentDate).toISOString().replace(/T/, " ").replace(/:00.000Z/, "").split('.')[0];
                            commentBox.remove();
                            var replyDisplayId = $('#replyDisplayId' + commentId);

                            $(replyDisplayId).append($(`<div class="media mt-4">
                            <div>
                                    <img id="profilePic" alt="UserPhoto" class="rounded-circle img-responsive mt-2"
                                         height="52" src="/uploads/images/${response.customer.profilePicture}" width="52"></div>
                                        <div class="media-body">
                                            <h5 class="mt-0">${response.customer.username} commented on ${timeStamp}</h5>
                                            <div class="message">${response.content}</div>
                                        </div>
                                    </div>`).last());
                        }
                    });
                }

            });


        });
    });
});

function showComments() {
    $.ajax({
        //Get comment html code
        type: "GET",
        url: '/api/comments',
        dataType: "json",
        success: function (response) {
            $.each(response, function (i, comment) {
                var timeStamp = new Date(comment.commentDate).toISOString().replace(/T/, " ").replace(/:00.000Z/, "").split('.')[0];
                var profilePicture = (comment.customer.profilePicture);

                if (comment.parentId === null) {
                    $('#showComments').append($(`
                        <div class=\"media mb-4\"><div>
                        <img id="profilePic" alt="UserPhoto" class="rounded-circle img-responsive mt-2 height=52" src="/uploads/images/${profilePicture}" width=\"52"></div>
                        <div class="media-body" id='mediaBody" + ${comment.id} + "'>
                        <h5 class="mt-0">${comment.customer.email} commented on ${timeStamp}</h5>
                        <div class="message"> ${comment.content}  </div>
                        <button type='button' id='button${comment.id}' class='btn btn-link reply'>Reply</button>
                        <div class="replyDisplay" id='replyDisplayId${comment.id}'> </div>
                        <div class="commentBoxSpace" id='commentBoxSpace${comment.id}'></div>`))
                }

                var replyDisplayId = $('#replyDisplayId' + comment.parentId);
                if (comment.parentId !== null) {
                    $(replyDisplayId).append($(`<div class="media mt-4">
                                                <div>
                        <img id="profilePic" alt="UserPhoto" class="rounded-circle img-responsive mt-2 height=52" src="/uploads/images/${profilePicture}" width=\"52"></div>
                                            <div class="media-body"> 
                                                <h5 class="mt-0">${comment.customer.email} commented on ${timeStamp} </h5>
                                                <div class="message"> ${comment.content} </div>
                                                </div>`));
                }
            })
        }
    });
}