
    $(document).ready(function() {
    $('#fullText').summernote({
        lang:'ru-RU',
        height:300,
        focus:true,
        placeholder:'Введите текст новости',
        toolbar:[
            //[groupname,[list buttons]]
            ['insert',['picture','link','video','table']],
            ['style',['bold','italic','underline']],
            ['font', ['strikethrough', 'superscript', 'subscript']],
            ['fontsize', ['fontsize','fontname']],
            ['color', ['color']],
            ['para', ['ul', 'ol', 'paragraph','style']],
            ['height', ['height','codeview']],

        ],
        fontNames:['Arial','Times New Roman','Verdana'],
        disableDragAndDrop:true,
    });
});