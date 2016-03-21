if( jQuery ) {
	jQuery.noConflict();
	if (Prototype.BrowserFeatures.ElementExtensions) {
	    var disablePrototypeJS = function (method, pluginsToDisable) {
	            var handler = function (event) {
	                event.target[method] = undefined;
	                setTimeout(function () {
	                    delete event.target[method];
	                }, 0);
	            };
	            pluginsToDisable.each(function (plugin) { 
	                jQuery(window).on(method + '.bs.' + plugin, handler);
	            });
	        },
	        pluginsToDisable = ['collapse', 'dropdown', 'modal', 'tooltip', 'popover'];
	    disablePrototypeJS('show', pluginsToDisable);
	    disablePrototypeJS('hide', pluginsToDisable);
	}
	jQuery(document).ready(function ($) {
	    $('.bs-example-tooltips').children().each(function () {
	        $(this).tooltip();
	    });
	    $('.bs-example-popovers').children().each(function () {
	            $(this).popover();
	    });
	});
}