sap.ui.define(
  [
    "./AbstractMasterController",
    "../resources/libs/vega5",
    "../resources/libs/vega-embed5"
  ],
  function (
    AbstractMasterController,
    vega5js,
    vega5embedjs
  ) {
    "use strict";

    return AbstractMasterController.extend(
      "sap.f.ShellBarWithFlexibleColumnLayout.controller.Dashboard",
      {
        onInit: function () {
          this.oRouter = this.getOwnerComponent().getRouter();
          this._bDescendingSort = false;
        },
        onRouteToMaster: function (oEvent) {

          this.oRouter.navTo("master");
        },
      }
    );
  }
);
