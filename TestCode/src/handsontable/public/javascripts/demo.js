var container = document.getElementById('test-hot');

var data;

var selectedRange;

var ssDefaultSettings = {
    minRows: 200,
    minCols:40
}

var hot = new Handsontable(container, {
    startRows:200,
    startCols:40,
    // width:600,
    // height:500,
    rowHeaders: true,
    colHeaders: true,
    contextMenu: true,
    outsideClickDeselects:false,
    manualColumnResize: true,
    manualRowResize: true,
    afterScrollVertically:
    function(){
        console.log(' you are scrolling');
        // hot.alter('insert_row');
    },
    afterScrollHorizontally:
    function(){
        // hot.alter('insert_col');
    }
});

$("#newSheet").click(function(event) {
    hot.clear();
})

$("#loadData").click(function(event) {
    event.stopPropagation();
    $.get('http://localhost:3000/users', function(data) {
        // console.log(data);
        var obj = jQuery.parseJSON(data);
        hot.loadData(obj);
        hot.updateSettings(ssDefaultSettings);

        console.log(obj);
    });

});

$('#createTable').click(function(event) {
    var tableData = hot.getSelected();

    createTable(tableData);

    hot.render();

});

$('#importTable').click(function(event) {
    $.get('http://localhost:3000/tables', function(data) {
        // console.log(data)
       var obj = JSON.parse(data);
       // hot.updateSettings(
       //     jQuery.extend(
       //         {
       //             data: obj.values
       //         },
       //         ssDefaultSettings
       //     )
       // )
       //  console.log(obj);

        [row, col, row2 , col2] = hot.getSelected();
        importTable(row,col,obj);

    });
});



var editSchema = function(item){
    item.click(function(event){
        console.log("your are editing schema");
        alert("test message");
    })
};

var createTable = function(selectedRange){
    [r,c,r2,c2] = selectedRange;

    console.log(hot.getData(r,c,r2,c2));
    var firstRowIsField = true;
    if (firstRowIsField === true){
        console.log(hot.getData(r,c,r2,c2));
        for (j=c;j<=c2;j++){
            hot.setCellMeta(r, j, 'type', 'dropdown' );
            hot.setCellMeta(r, j, 'source', ['ChangeType','ChangeData']);
            hot.setCellMeta(r, j, 'className', 'table-header');
            hot.setCellMeta(r, j, 'trimDropdown', 'false' );

        }
        for (j=c;j<=c2;j++){
            for (i=r+1;i<=r2;i++) {
                hot.setCellMeta(i, j, 'className', 'table-body');
            }
        }
    }else {
    };
    //pop up a window
    // checkbox-use the first row as field name
    // if unclick that checkbox, show a small panel
    // for users to specify the field name. The selected range
    // will be moved down a row.
}

var importTable = function(row, col, data){
    var keys = data[0];
    var types = data[1];
    var values = data[2];

console.log(types);
    var c2 = col + keys.length;
    var r2 = row + values.length;

    var header = [];
    var body = [];
    for(j=col; j<c2;j++ ){
        header.push([row, j, '<button>'+keys[j-col]+'</button>']);
        hot.setCellMeta(row, j, 'renderer', 'html' );
        // hot.setCellMeta(row, j, 'source', ['ChangeType','ChangeData']);
        hot.setCellMeta(row, j, 'className', 'table-header');
    };

    hot.setDataAtCell(header);

    $('.table-header').click(function(event){
       alert("Editing panel");
    });

    for (j=col; j<c2;j++){
        for(i=row+1;i<=r2;i++){
            hot.setCellMeta(i,j,'className', 'table-body');
            body.push([i,j,values[i-row-1][j-col]]);
        }
    }

    hot.setDataAtCell(body);
}


var setPropAtRange = function(r,c,r2,c2, prop, value) {
    for (j=c;j<=c2;j++){
        for (i=r;i<=r2;i++) {
            hot.setCellMeta(i, j, prop, value);

        }
    }
    hot.render();

}

$('#highlight').click(function(event){
    console.log("clicked highlight");
    [r,c,r2,c2] = hot.getSelected();
    console.log([r,c,r2,c2]);

    setPropAtRange(r,c,r2,c2,'className','highlighted');
})

//
// var data2 = [
//     ["1","Ted Right","A1"],
//     ["1","Ted Right","A2"],
//     ["2","Frank Honest","B"],
//     ["3","Joan Well","C"],
//     ["4","Gail Polite","D1"],
//     ["4","Gail Polite","D2"],
//     ["5","Michael Fair","E"],
// ];
//
// var moreData = [
//     ["6","Ted2Right","F"],
//     ["7","Frank2Honest","G1"],
//     ["7","Frank2Honest","G2"],
//     ["8","Joan2Well","H"],
//     ["9","Gail2Polite","I1"],
//     ["9","Gail2Polite","I2"],
//     ["9","Gail2Polite","I3"],
//     ["10","Michael2Fair","J"],
// ];
//
//
//
// $(window).scroll(function () {
//     if($(document).height() <= $(window).scrollTop() + $(window).height()) {
//         appendHOT2();
//     }
// });
//
// $("#infiniteLoad").click(function(event) {
//    hot.loadData(data2);
// });
//
// function appendHOT2() {
//     var hotData = hot.handsontable('getData');
//     Array.prototype.push.apply(hotData, moreData);
//     hot.handsontable('loadData', hotData);
//
// }