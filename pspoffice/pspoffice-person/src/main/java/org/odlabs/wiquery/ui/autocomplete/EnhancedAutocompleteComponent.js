/**
 *
 * @author Artem
 */

(function($){
    $.widget("custom.enhanced_address_autocomplete", $.ui.autocomplete, {

        _renderMenu: function(ul, items) {
            var self = this;
            $.each( items, function(index, item) {
                self._renderItem(ul, item);
            });
            
            var $openCreationDialogButton = self.element.siblings("input[data-open-create-dialog]:hidden");
            if($openCreationDialogButton.size() == 1){
                var openCreationDialogButton = $openCreationDialogButton.first();
                var button = $("<input type='button'/>")
                .attr("value", openCreationDialogButton.attr("value"))
                .addClass("btnMiddle")
                .css({
                    "font-size" : "12px",
                    "margin-top": "10px"
                })
                .click(function(){
                    openCreationDialogButton.click();
                });
                var container = $("<div>").append(button);
                ul.append(container);
            }
        }
    });
})(jQuery)