$(document).ready(takePriceChangeMonitor())
let maxDateValue;
let minDateValue;
let content;
let dataPoints = [];

async function takePriceChangeMonitor() {
    let response = await fetch("/manager/productChangeMonitor", {
        method: "POST",
        body: 1,
        headers: {"Content-Type": "application/json; charset=utf-8"}
    });

    content = await response.json();
    let key;

    for (key in content) {
        console.log(content[key]);
        dataPoints.push({
            x: new Date(key),
            y: content[key]
        });
    }
    dataPoints.sort(function (a, b) {
        return a.x - b.x
    });
    if (!maxDateValue) {
        maxDateValue = new Date(dataPoints[dataPoints.length - 1].x);
    }
    if (!minDateValue) {
        minDateValue = new Date(dataPoints[0].x);
    }
    fillPriceChange();
    $(function () {
        $('input[name="daterange"]').daterangepicker({
            opens: 'center',
            startDate: minDateValue,
            endDate: maxDateValue
        }, function (start, end) {
            minDateValue = new Date(start);
            maxDateValue = new Date(end);
            fillPriceChange();
        });
    });
}

async function fillPriceChange() {
    await $("#chartContainer").empty();

    let resultDataPoints = []
    for (key in dataPoints) {
        if (dataPoints[key].x >= minDateValue && dataPoints[key].x <= maxDateValue) {
            resultDataPoints.push(dataPoints[key]);
        }
    }

    var options = {
        animationEnabled: true,
        theme: "light2",
        title: {
            text: "Изменение цены на товар"
        },
        axisX: {
            valueFormatString: "DD MMM YYYY",
        },
        axisY: {
            title: "Руб.",
            titleFontSize: 24
        },
        data: [{
            type: "spline",
            yValueFormatString: "#,###.##",
            dataPoints: resultDataPoints
        }]
    };
    $("#chartContainer").CanvasJSChart(options);
}

