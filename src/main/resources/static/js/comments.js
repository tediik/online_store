$(document).ready(function () {
    $(function () {
        showComments();
        $('#commentForm').on('submit', function (event) {

            event.preventDefault();
            var formData = $(this).serializeArray();
            var formDataObject = {};
            $.each(formData,
                function (i, v) {
                    formDataObject[v.name] = v.value;
                });
            console.log(formDataObject);
            $.ajax({
                //Post comment
                url: 'http://localhost:9999/api/comments',
                method: "POST",
                data: JSON.stringify(formDataObject),
                dataType: 'json',
                cache: false,
                contentType: "application/json; charset=utf-8",
                success: function (response) {

                    console.log(response)
                    $('#showComments').append($("<div class=\"media mb-4\">\n" +
                        "<div>\n" +
                        "        <img id=\"profilePic\" alt=\"UserPhoto\" class=\"rounded-circle img-responsive mt-2\"\n" +
                        "             height=\"52\" src=\"/uploads/images/" + response.customer.profilePicture + "\" width=\"52\"></div>\n" +
                        "    <div class=\"media-body\" id='mediaBody" + response.id + "'>\n" +
                        "        <h5 class=\"mt-0\"> " + response.customer.email + "</h5>\n" +
                        "        <div class=\"message\">" + response.content + "</div>\n" +
                        "        <button type='button' id='" + response.id + "'  class='btn btn-link reply'>Reply</button>\n" +
                        "    </div>\n" +
                        "</div>"))
                }
            })
        });

        $(document).on('click', '.reply', function () {
            //Reply to this id
            var commentId = $(this).attr("id");
            var mediaBody = $('#mediaBody' + commentId);
            var replyMessageBox = $("<div class=\"card my-4\">\n" +
                "    <h5 class=\"card-header\">Leave a Comment:</h5>\n" +
                "    <div class=\"card-body\">\n" +
                "        <form id=\"replyForm\">\n" +
                "            <div class=\"form-group\">\n" +
                "                <textarea name=\"content\" id=\"replyText\" class=\"form-control\" rows=\"3\"></textarea>\n" +
                "            </div>\n" +
                "           <button type=\"button\" id='submitReplyBtn' class=\"btn btn-primary\"></button> \n" +
                "        </form>\n" +
                "    </div>\n" +
                "</div>");

            $('#parent_id').val(commentId);
            $(this).after(replyMessageBox);

            $('#submitReplyBtn').on('click', function (event) {
                event.preventDefault();
                var parent_id = commentId;
                var content = $('#replyText').val();
                var productComment = {parent_id, content};
                console.log(productComment);
                $.ajax({
                    //Post comment
                    url: 'http://localhost:9999/api/comments',
                    method: "POST",
                    data: JSON.stringify(productComment),
                    dataType: 'json',
                    cache: false,
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        replyMessageBox.remove();
                        console.log(response);

                        $('#mediaBody' + commentId).append($("<div class=\"media mt-4\">\n" +
                            "<div>\n" +
                            "        <img id=\"profilePic\" alt=\"UserPhoto\" class=\"rounded-circle img-responsive mt-2\"\n" +
                            "             height=\"52\" src=\"/uploads/images/" + response.customer.profilePicture + "\" width=\"52\"></div>\n" +
                            "            <div class=\"media-body\">\n" +
                            "                <h5 class=\"mt-0\">" + response.customer.username + "</h5>\n" +
                            "                <div class=\"message\"> " + response.content + "</div>\n" +
                            "            </div>\n" +
                            "        </div>"));
                    }
                });
            });

        });
    });
});


function showComments() {
    $.ajax({
        //Get comment html code
        type: "GET",
        url: 'http://localhost:9999/api/comments',
        dataType: "json",
        success: function (response) {
            $.each(response, function (i, comment) {
                var date = new Date(comment.commentDate);
                console.log(date);
                var timeStamp = date.toISOString().replace(/T/, " ").replace(/:00.000Z/, "").split('.')[0];

                var profilePicture = (comment.customer.profilePicture);

                if (comment.parent_id === null) {
                    $('#showComments').append($("<div class=\"media mb-4\">\n" +
                        "    <div>\n" +
                        "<div>\n" +
                        "        <img id=\"profilePic\" alt=\"UserPhoto\" class=\"rounded-circle img-responsive mt-2\"\n" +
                        "             height=\"52\" src=\"/uploads/images/" + profilePicture + "\" width=\"52\"></div>\n" +
                        "    <div class=\"media-body\" id='mediaBody" + comment.id + "'>\n" +
                        "        <h5 class=\"mt-0\">" + comment.customer.email + "</h5>\n" +
                        "        <div class=\"message\">  " + comment.content + " </div>\n" +
                        "                        <div class=\"date\">" + timeStamp + "</div>\n" +

                        "        <button type='button' id='buttonId" + comment.id + "'class='btn btn-link reply'>Reply</button>"));
                }


                if (comment.parent_id !== null) {
                    $('#buttonId' + comment.parent_id).replaceWith($("  <div class=\"media mt-4\">\n" +
                        "                    <div>\n" +
                        "                        <img id=\"profilePic\" alt=\"UserPhoto\" class=\"rounded-circle img-responsive mt-2\" height=\"52\" src=\"http://placehold.it/50x50\" width=\"52\"></div>\n" +
                        "                    <div class=\"media-body\">\n" +
                        "                        <h5 class=\"mt-0\">" + comment.customer.email + "</h5>\n" +
                        "                        <div class=\"message\">  " + comment.content + " </div>\n" +
                        "                        <div class=\"date\">" + timeStamp + "</div>\n" +
                        "                    </div>\n" +
                        "                </div>\n"));
                }
            })
        }
    });
}





