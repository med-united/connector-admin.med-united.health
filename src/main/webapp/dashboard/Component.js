sap.ui.define([
  "sap/base/util/UriParameters",
  "sap/ui/core/UIComponent",
  "sap/ui/model/json/JSONModel",
  "sap/f/library",
  "sap/f/FlexibleColumnLayoutSemanticHelper"
], function (UriParameters, UIComponent, JSONModel, library,
    FlexibleColumnLayoutSemanticHelper) {
  "use strict";

  let LayoutType = library.LayoutType;

  let Component = UIComponent.extend(
      "sap.f.ShellBarWithFlexibleColumnLayout.Component", {
        metadata: {
          manifest: "json"
        },

        init: function () {
          UIComponent.prototype.init.apply(this, arguments);

          // create the views based on the url/hash
          this.getRouter().initialize();

          let oModel = new JSONModel();
          this.setModel(oModel, "Layout");
        },

        getHelper: function () {
          let oFCL = this.getRootControl().byId("fcl"),
              oParams = UriParameters.fromQuery(location.search),
              oSettings = {
                defaultTwoColumnLayoutType: LayoutType.TwoColumnsMidExpanded,
                defaultThreeColumnLayoutType: LayoutType.ThreeColumnsMidExpanded,
                mode: oParams.get("mode"),
                maxColumnsCount: oParams.get("max")
              };

          return FlexibleColumnLayoutSemanticHelper.getInstanceFor(oFCL,
              oSettings);
        }
      });
  return Component;
});
