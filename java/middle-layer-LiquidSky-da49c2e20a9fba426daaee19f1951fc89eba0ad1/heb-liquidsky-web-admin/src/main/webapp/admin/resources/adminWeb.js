var adminWebProperties = {
		debugMode: true,
		confirmDelete: true,
		amountScrolled: 300,
		ajaxTimeout: 30000,
		restHost: "adminrest-dot-heb-javaapptest.appspot.com",
		restHostProtocol: "https",
		apiKey: "AIzaSyC18AzEVq7UOTlbWVLjJSKGTpdwcnBdnuk",
		dataType: 'jsonp',
		crossDomain: true,
		interfaces: {
			auditLog: {
				readCollection: "/adminrest/v1/auditlog"
			}
		},
		templateIds: {
			auditLogCollection: "AuditLogEntryTemplate",
		}
};

//  Please use this instead of console.log so that logging can be turned off easily.
function logDebug(message){
	if (undefined !== adminWebProperties.debugMode && adminWebProperties.debugMode) {
		console.log("Log Debug: " + message);
	}
}

$(document).on("mobileinit", function(){
	// jquery mobile likes to do ajax loads of pages, but that totally messes up our other JS, so this disables it. 
	$.mobile.ajaxEnabled = false;
});

$(document).on("pagecreate", function(){
	initScrollToTop();	
});

//Initial setup
$(document).ready(function(){
	// adds the back to top button.
	$('body').prepend('<a href="#" class="back-to-top">Back to Top</a>');
	loadAdminWebProperties();
})

//  Attach actions to DOM objects
$(document).ready(function(){
	$("#refreshAuditLogTableButton").click(function() {
		populateAuditLogTable();
	});
});


/*
 * 
 * General Utility functions for CRUD operations
 * 
 */

function deleteResourceCallback(data){
	//logDebug("deleteResourceCallback: " + data);
	if (typeof refreshPageData == "function") refreshPageData();
}

function deleteResource(resourceLocation, callbackFunction){
	var urlToHit = adminWebProperties.restHostProtocol + "://"+ adminWebProperties.restHost + resourceLocation + "?key=" + adminWebProperties.apiKey;
	sendAjaxRequest(urlToHit, "DELETE", null, callbackFunction);
}

function createResourceCallback(data){
	//logDebug("createResourceCallback: " + data);
	if (typeof refreshPageData == "function") refreshPageData();
	$.modal.close();
}

function createResource(createForm){
	var urlToHit = adminWebProperties.restHostProtocol + "://"+ adminWebProperties.restHost + createForm.attr("action") + "?key=" + adminWebProperties.apiKey;
	var jsonObject = {};
	var jsonArray = [];
	createForm.find("input").each(function(index) {
		jsonObject[$(this).attr("name")] = $(this).val();
	});
	jsonArray.push(jsonObject);
	//logDebug("Built object: " + JSON.stringify(jsonArray));
	sendAjaxRequest(urlToHit, "POST", JSON.stringify(jsonArray), createResourceCallback);
}

function updateResourceCallback(data){
	//logDebug("updateResourceCallback: " + data);
	if (typeof refreshPageData == "function") refreshPageData();
	$.modal.close();
}

function updateResource(updateForm){
	var urlToHit = adminWebProperties.restHostProtocol + "://"+ adminWebProperties.restHost + updateForm.attr("action") + "?key=" + adminWebProperties.apiKey;
	var jsonObject = {};
	updateForm.find("input").each(function(index) {
		jsonObject[$(this).attr("name")] = $(this).val();
	});
	//logDebug("Built object: " + JSON.stringify(jsonObject));
	sendAjaxRequest(urlToHit, "PUT", JSON.stringify(jsonObject), updateResourceCallback);
}

/*
 * ============================================================
 * Functions for edit pages.
 * ============================================================
 */

function attachOperationEvents(){
	$("[data-OperationButton]").off();
	$("[data-OperationButton").click(function() {
		event.preventDefault();
		switch ($(this).attr("data-OperationButton")){
			case "create":
				createResource($(this).parent())
				break;
			case "update":
				updateResource($(this).parent());
				break;
			case "delete":
				if (adminWebProperties.confirmDelete){
					if (confirm("Are you sure you want to delete "  + $(this).attr("data-resourceId") + "?")) {
						deleteResource($(this).attr("data-resourceLocation"), deleteResourceCallback);
					}
				} else {
					deleteResource($(this).attr("data-resourceLocation"), deleteResourceCallback);
				}
				break;
			default:
		}
		return false
	});
}


/*
 * Audit Log Functions
 */
function handleAuditLogCollection(arrayAuditLogEntries){
	if ($.isArray(arrayAuditLogEntries)){
		$("#AuditLogTable tbody").empty();
		$.each(arrayAuditLogEntries, function(index, AuditLogEntry) {
			//logDebug("arrayAuditLogEntries[" + index + "] " + JSON.stringify(AuditLogEntry));
			var template = $("#" + adminWebProperties.templateIds.auditLogCollection).html();
			var html = Mustache.to_html(template, AuditLogEntry);
			$("#AuditLogTable tbody").append(html);
		});
	} else {
		logDebug("arrayAuditLogEntries did not contain an array! arrayAuditLogEntries: " + arrayAuditLogEntries);
	}
}

function populateAuditLogTable(){
	sendAjaxRequest(
			adminWebProperties.restHostProtocol + "://"+ adminWebProperties.restHost + adminWebProperties.interfaces.auditLog.readCollection + "?key=" + adminWebProperties.apiKey, 
			"GET", null, handleAuditLogCollection);
}


/*
 * ============================================================
 * REST Interaction Functions
 * ============================================================
 */

//Sends an ajax request
function sendAjaxRequest(urlToHit, httpMethod, dataObject, successCallback) {
	//var contentTypeToUse = "application/x-www-form-urlencoded; charset=UTF-8";
	var contentTypeToUse = "application/json";

	$.ajax({
		url : urlToHit,
		cache : false,
		type : httpMethod,
		dataType : "json",
		data : dataObject,
		contentType : contentTypeToUse,
		timeout: adminWebProperties.ajaxTimeout,
		success : function(data) {
			if (typeof successCallback == "function") successCallback(data); else logDebug("callback function for sendAjaxRequest was not valid");
			//logDebug(data);
			initScrollToTop();
		},
		fail : function(data) {
			logDebug("the Ajax Request failed. data: " + data);
			if ($("#CollectionData").length > 0) {
				$("#CollectionData").html("An Error has occurred <br/>" + data.responseText);
			} else {
				alert("the Ajax Request failed.");
			}
		},
		error : function(data) {
			logDebug("the Ajax Request failed. data: " + data);
			if ($("#CollectionData").length > 0) {
				$("#CollectionData").html("An Error has occurred <br/>" + data.responseText);
			} else {
				alert("an ajax error occurred");
			}
		},
		beforeSend : function(data) {
			logDebug("Request sent to : \n" + urlToHit
					+ "\n via httpMethod: " + httpMethod
					+ "\n Waiting for response...");
			$.mobile.loading( "show", {
				text: "Loading...",
				textVisible: true,
				textonly: false
			});
		},
		complete : function(data) {
			logDebug("Request Complete");
			$.mobile.loading("hide");
		}
	});
}

/*
 * ============================================================
 * General Utility Functions
 * ============================================================
 */


function scrollToTopHandler() {
	if ( $(window).scrollTop() > adminWebProperties.amountScrolled ) {
		$('a.back-to-top').fadeIn('slow');
	} else {
		$('a.back-to-top').fadeOut('slow');
	}
}

function initScrollToTop(){
	// these bits add the scroll to top button and control the functionality
	// TODO: jquery mobile is messing this up and unbinding the event.  fix this later. for now it is just removing and re adding when an ajax call completes.
	logDebug("initScrollToTop(); fired")
	$(window).off("scroll", window, scrollToTopHandler);
	$(window).on("scroll", window, scrollToTopHandler);
	$('a.back-to-top').click(function() {
	$('html, body').animate({scrollTop: 0}, 300);
		return false;
	});
}
//Adds functionality for a Copy to Clipboard button.
function copyToClipboard(elem) {
	  // create hidden text element, if it doesn't already exist
    var targetId = "_hiddenCopyText_";
    var isInput = elem.tagName === "INPUT" || elem.tagName === "TEXTAREA";
    var origSelectionStart, origSelectionEnd;
    if (isInput) {
        // can just use the original source element for the selection and copy
        target = elem;
        origSelectionStart = elem.selectionStart;
        origSelectionEnd = elem.selectionEnd;
    } else {
        // must use a temporary form element for the selection and copy
        target = document.getElementById(targetId);
        if (!target) {
            var target = document.createElement("textarea");
            target.style.position = "absolute";
            target.style.left = "-9999px";
            target.style.top = "0";
            target.id = targetId;
            document.body.appendChild(target);
        }
        target.textContent = elem.textContent;
    }
    // select the content
    var currentFocus = document.activeElement;
    target.focus();
    target.setSelectionRange(0, target.value.length);
    
    // copy the selection
    var succeed;
    try {
    	  succeed = document.execCommand("copy");
    } catch(e) {
        succeed = false;
    }
    // restore original focus
    if (currentFocus && typeof currentFocus.focus === "function") {
        currentFocus.focus();
    }
    
    if (isInput) {
        // restore prior selection
        elem.setSelectionRange(origSelectionStart, origSelectionEnd);
    } else {
        // clear temporary content
        target.textContent = "";
    }
    return succeed;
}

//updates any input with the attribute data-injectApiKey="true"
function updateApiKeyInputs(newKey){
	$("input[data-injectApiKey='true']").each(function(index) {
		$(this).val(newKey);
	});
}

// Gets saved properties from a cookie and overrides some properties in adminWebProperties 
function loadAdminWebProperties(){
	var jsonObject = savedAdminWebProperties();
	adminWebProperties.apiKey = jsonObject.apiKey;
	adminWebProperties.restHost = jsonObject.host;
	adminWebProperties.restHostProtocol = jsonObject.protocol;
}

/*
 * Used to read or write saved settings for overriding adminWebProperties default values
 * Takes a Json Object that contains settings to save
 * If no Json Object is passed, then the saved settings are returned.
 * If there are no saved settings, then it saves the default settings and returns those. 
 */
function savedAdminWebProperties(newAdminWebProperties) {
	if (undefined !== newAdminWebProperties) {
		// set the AdminWebProperties because a new one was passed
		logDebug("newAdminWebProperties: " + JSON.stringify(newAdminWebProperties));
		Cookies.set('AdminWebProperties', JSON.stringify(newAdminWebProperties), {
			expires : 90,
			path : '/'
		});
		return newAdminWebProperties;
	} else {
		var cookieAdminWebProperties = Cookies.get('AdminWebProperties');
		if (undefined !== cookieAdminWebProperties) {
			logDebug("cookieAdminWebProperties: " + cookieAdminWebProperties);
			return JSON.parse(cookieAdminWebProperties);
		} else {
			// nothing was found, so set with the defaults and return that
			var jsonObject = {};
			jsonObject.apiKey = adminWebProperties.apiKey;
			jsonObject.host = adminWebProperties.restHost;
			jsonObject.protocol = adminWebProperties.restHostProtocol;
			return savedAdminWebProperties(jsonObject);
		}
	}
}

