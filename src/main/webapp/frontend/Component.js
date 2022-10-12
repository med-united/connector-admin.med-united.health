sap.ui.define([
"sap/ui/core/UIComponent", "sap/ui/model/odata/v2/ODataModel",
"sap/f/FlexibleColumnLayoutSemanticHelper",],
function(UIComponent, ODataModel, FlexibleColumnLayoutSemanticHelper) {
	"use strict";
	return UIComponent.extend("frontend.Component", {

		metadata : {
			manifest: "json"
		},

		init : function() {
			// call the init function of the parent
			UIComponent.prototype.init.apply(this, arguments);
			// create the views based on the url/hash
			this.getRouter().initialize();

		},

		getHelper: function () {
            const oFCL = this.getRootControl().byId("fcl"),
            oParams = jQuery.sap.getUriParameters(),
            oSettings = {
                defaultTwoColumnLayoutType: sap.f.LayoutType.TwoColumnsMidExpanded,
                defaultThreeColumnLayoutType: sap.f.LayoutType.ThreeColumnsMidExpanded,
                mode: oParams.get("mode"),
                initialColumnsCount: oParams.get("initial"),
                maxColumnsCount: oParams.get("max")
            };
            return FlexibleColumnLayoutSemanticHelper.getInstanceFor(oFCL, oSettings);
        }
	});
});
