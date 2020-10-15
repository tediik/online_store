document.getElementById('checkStatusRepairOrder').addEventListener("click", getRepairOrder)

function getRepairOrder() {
    let id = document.getElementById('idCheck').value;
    let telephoneNumber = document.getElementById('telCheck').value;
    fetch('http://localhost:9999/api/checkStatus', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-type': 'application/json; charset=UTF-8'
        },
        body: JSON.stringify({
              id: id,
              telephoneNumber: telephoneNumber})
    }).then(res => res.json()).then((data) => {

            console.log(data)
            toastr.success("Заявка успешно добавлена");

    })
}