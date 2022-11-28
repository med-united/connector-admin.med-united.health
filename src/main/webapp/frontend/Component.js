sap.ui.define([
	"sap/base/util/UriParameters",
	"sap/ui/core/UIComponent",
	"sap/ui/model/json/JSONModel",
	"sap/f/library",
	"sap/f/FlexibleColumnLayoutSemanticHelper"
], function (UriParameters, UIComponent, JSONModel, library, FlexibleColumnLayoutSemanticHelper) {
	"use strict";

	let LayoutType = library.LayoutType;

	let Component = UIComponent.extend("sap.f.ShellBarWithFlexibleColumnLayout.Component", {
		metadata: {
			manifest: "json"
		},

		init: function () {
			UIComponent.prototype.init.apply(this, arguments);

            // create the views based on the url/hash
            this.getRouter().initialize();
		},
	});
	return Component;
});
