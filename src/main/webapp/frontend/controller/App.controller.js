sap.ui.define([
   "sap/ui/core/mvc/Controller"
], function (Controller) {
   "use strict";
   return Controller.extend("frontend.controller.App", {

        onListItemPress: function (oEvent) {
                    var oNextUIState = this.getOwnerComponent().getHelper().getNextUIState(1),
                        entityPath = oEvent.getSource().getBindingContext().getPath(),
                        entity = entityPath.split("/").slice(-1).pop();

                    var oParams = {layout: oNextUIState.layout};
                    oParams[this.getEntityName().toLowerCase()] = entity;
                    this.oRouter.navTo(this.getEntityName().toLowerCase() + "-detail", oParams);
                },

   });
});