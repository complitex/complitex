$(function(){
    $(".person-picker-search-form .person-picker-search-button").on("click", function(){
        var searchForm = $(this).closest(".person-picker-search-form");
        searchForm.find(".ui-autocomplete-input").data("close-menu", true).autocomplete("close");
    });
    
    $(".person-picker-search-form .ui-autocomplete-input")
        .on("keyup", function(event){
            var input = $(this);
            if(event.which == $.ui.keyCode.ENTER){
                input.closest(".person-picker-search-form").find(".person-picker-search-button").click();
            } else {
                //remove 'close-menu' data
                input.removeData("close-menu");
            }
        })
        .on("autocompleteselect", function(){
            var input = $(this);
            setTimeout(function(){
                var nextAutocompleter = input.closest("td").next("td").find(".ui-autocomplete-input");
                if(nextAutocompleter.size() == 1){
                    nextAutocompleter.focus();
                } else if(nextAutocompleter.size() == 0){
                    input.closest(".person-picker-search-form").find(".person-picker-search-button").click();
                }
            }, 200);
        })
        .on("autocompletesearch", closeMenuCheck)
        .on("autocompleteopen", closeMenuCheck);

        function closeMenuCheck(e){
            var closeMenu = $(this).data("close-menu");
            if(!!closeMenu){
                e.preventDefault();
            }
        }
});