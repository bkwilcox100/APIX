<script>
var resourceCollection = {};

function refreshPageData(){
	populateResourceTable();
}

$(document).ready(function(){
	refreshPageData();
	$("#RefreshCollection").click(function() {
		populateResourceTable();
	});
	attachOperationEvents();
	
	$("#expandAll").click(function() {
		$(".resourceContainer, .collectionContainer").collapsible("expand");
	});
	
	$("#collapseAll").click(function() {
		$(".resourceContainer, .collectionContainer").collapsible("collapse");
	});
	
	$("#expandAllCollections").click(function() {
		$(".collectionContainer").collapsible("expand");
	});
	
	$("#collapseAllCollections").click(function() {
		$(".collectionContainer").collapsible("collapse");
	});
	
	$("#expandAllResources").click(function() {
		$(".resourceContainer").collapsible("expand");
	});
	
	$("#collapseAllResources").click(function() {
		$(".resourceContainer").collapsible("collapse");
	});
	
});

function setResourceCollection(data){
	resourceCollection = data;
	$("#CollectionData").empty();
	displayCollection(resourceCollection, "#CollectionData", adminPageProperties[adminPageProperties.topLevelResourceType].resourceType);
	attachOperationEvents();
	$("#CollectionData").trigger("create");
}

function populateResourceTable(){
	if (adminPageProperties.forceDebugData){
		logDebug("Using Test Data!!");
		setResourceCollection(testdata);
	} else {
		sendAjaxRequest(
			adminWebProperties.restHostProtocol + "://"+ adminWebProperties.restHost + adminPageProperties[adminPageProperties.topLevelResourceType].collectionUrl + "?key=" + adminWebProperties.apiKey, 
			"GET", null, setResourceCollection);
	}
}

function generateCollectionView(parentElementSelector, resourceTypeName){
	var parentId = $(parentElementSelector).attr("data-parentResourceId");
	template = $("#CollectionTemplate").html();
	html = Mustache.to_html(template, {resourceTypeName: adminPageProperties[resourceTypeName].resourceType, parentId: parentId});
	$(parentElementSelector).append(html);
	return "Collection_" + adminPageProperties[resourceTypeName].resourceType + "_" + parentId;
}

function generateCreationModal(resourceTypeName, parentSelector, resourceChainIds){
	var parentId = $(parentSelector).attr("data-parentResourceId");
	
	var collectionUrl = Mustache.to_html(adminPageProperties[resourceTypeName].collectionUrl, resourceChainIds);
	var resourceUrl = Mustache.to_html(adminPageProperties[resourceTypeName].resourceUrl, resourceChainIds);
	
	// Add the modal and form to the page.
	template = $("#createResourceModalTemplate").html();
	html = Mustache.to_html(template, {resourceTypeName: adminPageProperties[resourceTypeName].resourceType, collectionUrl: collectionUrl, resourceUrl: resourceUrl, parentId: parentId});
	//$("#collectionOperations_" + adminPageProperties[resourceTypeName].resourceType + "_" + parentId).append(html);
	$(parentSelector).append(html);
	// add the form fields
	$.each(adminPageProperties[resourceTypeName].createProperties, function(index, createPropertyName) {
		template = $("#createFormInputTemplate").html();
		html = Mustache.to_html(template, {propertyName: createPropertyName, resourceTypeName: adminPageProperties[resourceTypeName].resourceType, parentId: parentId});
		$("#createResourceForm" + adminPageProperties[resourceTypeName].resourceType + "_" + parentId).prepend(html);
	});
}

function generateCollectionOperations(parentElementSelector, resourceTypeName, resourceChainIds){
	var parentId = $(parentElementSelector).attr("data-parentResourceId");
	template = $("#CollectionOperationsTemplate").html();
	html = Mustache.to_html(template, {resourceTypeName: adminPageProperties[resourceTypeName].resourceType, parentId: parentId});
	$(parentElementSelector).append(html);
	
	generateCreationModal(resourceTypeName, parentElementSelector, resourceChainIds);
}

function generateResourceView(resource, resourceTypeName, parentElementSelector, resourceChainIds){
	var resourceId = resource[adminPageProperties[resourceTypeName].idProperty];
	resourceChainIds[adminPageProperties[resourceTypeName].idProperty] = resourceId;
	
	var collectionUrl = Mustache.to_html(adminPageProperties[resourceTypeName].collectionUrl, resourceChainIds);
	var resourceUrl = Mustache.to_html(adminPageProperties[resourceTypeName].resourceUrl, resourceChainIds);

	// Add the resource View
	template = $("#ResourceViewTemplate").html();
	html = Mustache.to_html(template, {resourceTypeName: resourceTypeName, resourceId: resourceId});
	$(parentElementSelector).append(html);
	var resourceViewSelector = "#resource_" + resourceTypeName + "_" + resourceId;
	
	// Add operations buttons.
	template = $("#ResourceOperationsTemplate").html();
	html = Mustache.to_html(template, {resourceId: resourceId, collectionUrl: collectionUrl, resourceUrl: resourceUrl});
	$(resourceViewSelector + " > .resourceViewOperations").append(html);

	// add update form fields
	$.each(adminPageProperties[resourceTypeName].updateProperties, function(index, updatePropertyName) {
		template = $("#updateFormInputTemplate").html();
		html = Mustache.to_html(template, {resourceId: resourceId, propertyName: updatePropertyName, propertyValue: resource[updatePropertyName]});
		$("#updateResourceForm_" + resourceId).prepend(html);
	});
	
	// Add each data element containing the properties.
	$.each(adminPageProperties[resourceTypeName].propertyOrder, function(index, attrName) {
        var attrValue = resource[attrName];
        if (!($.isArray(attrValue))){
        	template = $("#ResourcePropertyTemplate").html();
    		html = Mustache.to_html(template, {propertyName: attrName, propertyValue: attrValue});
    		$(resourceViewSelector + " > ul").append(html);
        }
    });
	
	// add child collections
	$.each(adminPageProperties[resourceTypeName].childCollections, function(index, attrName) {
		var attrValue = resource[attrName];
		template = $("#ChildCollectionPropertyTemplate").html();
		html = Mustache.to_html(template, {collectionType: adminPageProperties[attrName].resourceType, parentId: resourceId, colCount: adminPageProperties[resourceTypeName].propertyOrder.length + 1});
		$(resourceViewSelector).append(html);
		
		displayCollection(attrValue, "#collection_" + adminPageProperties[attrName].resourceType + "_" + resourceId, attrName, resourceChainIds);
	});
}

/*
 * This function generates a collection table.  It also is called recursively to generate tables for child collections. 
 */
function displayCollection(arrayResources, parentElement, resourceTypeName, resourceChainIds){
	// this holds all of the ids for all parents of the resource and is used as a filter for generating the resource and collection paths.
	if (undefined === resourceChainIds) {
		resourceChainIds = {};
	}

	if ($.isArray(arrayResources)){
		var collectionViewId = generateCollectionView(parentElement, resourceTypeName);
		generateCollectionOperations("#" + collectionViewId, resourceTypeName, resourceChainIds);
		
		$.each(arrayResources, function(index, resource) {
			generateResourceView(resource, resourceTypeName, "#" + collectionViewId, resourceChainIds);
		});
	}
}

</script>


<ul id="ParentCollectionOperations">
	<li>
		<a href="#" id="RefreshCollection" class="ui-btn ui-shadow ui-corner-all ui-btn-icon-left ui-icon-refresh">Refresh</a>
	</li>
	
	<li>
		<a href="#" id="expandAll" class="ui-btn ui-shadow ui-corner-all ui-btn-icon-left ui-icon-plus">Expand All</a>
	</li>
	<li>
		<a href="#" id="collapseAll" class="ui-btn ui-shadow ui-corner-all ui-btn-icon-left ui-icon-minus">Collapse All</a>
	</li>
	
	<li>
		<a href="#" id="expandAllCollections" class="ui-btn ui-shadow ui-corner-all ui-btn-icon-left ui-icon-plus">Expand All Collections</a>
	</li>
	<li>
		<a href="#" id="collapseAllCollections" class="ui-btn ui-shadow ui-corner-all ui-btn-icon-left ui-icon-minus">Collapse All Collections</a>
	</li>
	
	<li>
		<a href="#" id="expandAllResources" class="ui-btn ui-shadow ui-corner-all ui-btn-icon-left ui-icon-plus">Expand All Resources</a>
	</li>
	<li>
		<a href="#" id="collapseAllResources" class="ui-btn ui-shadow ui-corner-all ui-btn-icon-left ui-icon-minus">Collapse All Resources</a>
	</li>

</ul>

<div id="CollectionData" data-parentResourceId="topResourceCollection">
	Collection Data Displays Here
</div>




<%--
=====================================
Mustache Templates
=====================================
 --%>

<template id="CollectionTemplate">
	<div id="Collection_{{resourceTypeName}}_{{parentId}}" class="collectionContainer" 
		data-role="collapsible" 
		data-collapsed="false"
		data-resourceTypeName="{{resourceTypeName}}" 
		data-parentResourceId="{{parentId}}">
		
		<h4>{{resourceTypeName}}</h4>
	</div>
</template>

<template id="CollectionOperationsTemplate">
	<div>
		<a href="#createResourceModal_{{resourceTypeName}}_{{parentId}}" 
			rel="modal:open" 
			class="ui-btn ui-shadow ui-corner-all ui-btn-inline ui-btn-icon-left ui-icon-edit">
			
			Create {{resourceTypeName}}
		</a>
	</div>
</template>

<template id="ResourceViewTemplate">
	<div id="resource_{{resourceTypeName}}_{{resourceId}}" class="resourceContainer" data-role="collapsible" data-collapsed="false">
		<h4>{{resourceId}}</h4>
		<span class="resourceViewOperations"></span>
		<ul id="properties_{{resourceTypeName}}_{{resourceId}}">
		</ul>
	</div>
</template>

<template id="ResourcePropertyTemplate">
	<li>
		<span class="propertyName"> {{propertyName}} :</span> <span class="propertyValue"> {{propertyValue}} </span>
	</li>
</template>

<template id="ChildCollectionPropertyTemplate">
	<span class="subCollectionName"> {{collectionType}} </span> 
	<span id="collection_{{collectionType}}_{{parentId}}" class="subCollectionValue" data-parentResourceId="{{parentId}}">
	</span>
</template>

<template id="ResourceOperationsTemplate">
	<div class="resourceOperations">
		<a href="#updateResourceModal_{{resourceId}}" rel="modal:open" class="ui-btn ui-shadow ui-corner-all ui-btn-inline ui-btn-icon-left ui-icon-edit">
			Update
		</a>
		
		<a href="#" data-OperationButton="delete" data-parentResourceId="{{resourceId}}" data-resourceLocation="{{resourceUrl}}" class="ui-btn ui-shadow ui-corner-all ui-btn-inline ui-btn-icon-left ui-icon-delete">
			Delete
		</a>
		
		<div id="updateResourceModal_{{resourceId}}" style="display:none;">
			<p>resourceId: {{resourceId}}
			<form id="updateResourceForm_{{resourceId}}" name="updateResource" action="{{resourceUrl}}">
				<button id="updateResourceButton_{{resourceId}}" data-OperationButton="update"> Update {{resourceId}} Resource</button>
			</form>
		</div>
	</td>
</template>

<template id="updateFormInputTemplate">
	<label for="{{propertyName}}UpdateResource_{{resourceId}}"> {{propertyName}} </label>
	<input id="{{propertyName}}UpdateResource_{{resourceId}}" name="{{propertyName}}" value="{{propertyValue}}"/><br/>
</template>

<template id="createResourceModalTemplate">
	<div id="createResourceModal_{{resourceTypeName}}_{{parentId}}" style="display:none;">
		<p>Create new {{resourceTypeName}} </p>
		<form id="createResourceForm{{resourceTypeName}}_{{parentId}}" name="createResource{{resourceTypeName}}" action="{{collectionUrl}}">
			
			<button id="createResource{{resourceTypeName}}_{{parentId}}Button" data-OperationButton="create"> Create {{resourceTypeName}} Resource</button>
		</form>
	</div>
</template>

<template id="createFormInputTemplate">
	<label for="{{propertyName}}_{{parentId}}CreateResourceInput"> {{propertyName}} </label>
	<input id="{{propertyName}}_{{parentId}}CreateResourceInput" name="{{propertyName}}" value="Sample{{propertyName}}"/><br/>
</template>
