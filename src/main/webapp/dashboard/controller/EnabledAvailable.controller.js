sap.ui.define([
    "sap/ui/core/mvc/Controller"
    ], function (Controller) {
        "use strict";
    
        return Controller.extend("sap.f.ShellBarWithFlexibleColumnLayout.controller.AbstractController", {
            translate: function (sKey, aArgs, bIgnoreKeyFallback) {
                return (sKey)
                        ? this.getOwnerComponent().getModel("i18n").getResourceBundle().getText(sKey, aArgs, bIgnoreKeyFallback)
                        : '';
            }
        });
    }, true);
    