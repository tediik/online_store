const stockMailingCheckbox = document.getElementById('#stockMailingCheckbox')

// stockMailingCheckbox.addEventListener('click', handleStockMailingCheckbox)

function handleStockMailingCheckbox() {
    console.log("Нажал")
}

$('#stockMailingCheckbox').change(function(){
    if(this.checked!==true){
        $(".form-check-label").hide();
        console.log("hide")
    }
    else{
        $(".form-check-label").show();
        console.log("show")
    }
});