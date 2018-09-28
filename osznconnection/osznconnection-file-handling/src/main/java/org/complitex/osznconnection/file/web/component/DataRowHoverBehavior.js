$(function(){
    (function(){
        $("table tr.data-row").bind('click', function(event){
            var row = $(this).closest("tr.data-row");

            if($(event.target).hasClass('data-row-link')){
                row.addClass("data-row-hover");
            }else{
                if (row.hasClass("data-row-hover")) {
                    row.removeClass("data-row-hover");
                } else {
                    row.addClass("data-row-hover");
                }
            }
        });
    })();
});