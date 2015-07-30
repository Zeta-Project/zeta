// jQuery extension for HTTP method PUT and DELETE
jQuery.each([ "put", "delete" ], function(i, method) {
	jQuery[method] = function(url, data, callback, type) {
		// shift arguments if data argument was omitted
		if (jQuery.isFunction(data)) {
			type = type || callback;
			callback = data;
			data = undefined;
		}

		return jQuery.ajax({
			url : url,
			type : method,
			dataType : type,
			data : data,
			success : callback
		});
	};
});