const stockMailingCheckbox = document.getElementById('#stockMailingCheckbox')

$('#stockMailingCheckbox').change(function(){
    if(this.checked!==true){
        $(".day-of-the-week").addClass("d-none")
        console.log("hide")
    }
    else{
        $(".day-of-the-week").removeClass("d-none")
        console.log("show")
    }
});