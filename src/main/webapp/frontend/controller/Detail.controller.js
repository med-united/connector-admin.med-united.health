sap.ui.define([
	"./AbstractDetailController",
    "sap/ui/model/json/JSONModel"
], function (AbstractDetailController, JSONModel) {
	"use strict";

	return AbstractDetailController.extend("ap.f.ShellBarWithFlexibleColumnLayout.controller.Detail", {
		onInit: function() {
            AbstractDetailController.prototype.onInit.apply(this, arguments);

            const oCardsModel = new JSONModel();
            this.getView().setModel(oCardsModel, "Cards");

            const oCardTerminalsModel = new JSONModel();
            this.getView().setModel(oCardTerminalsModel, "CardTerminals");

            const oMetricsModel = new JSONModel();
            this.getView().setModel(oMetricsModel, "Metrics");

            const oCertificatesModel = new JSONModel();
            this.getView().setModel(oCertificatesModel, "Certificates");


            oCardsModel.attachRequestCompleted(function() {
                    console.log(oCardsModel.getData());
            });

            oCertificatesModel.attachRequestCompleted(function() {
                    console.log(oCertificatesModel.getData());
            });



        },
        _onMatched: function (oEvent) {
            AbstractDetailController.prototype._onMatched.apply(this, arguments);
            const sPath = "/RuntimeConfigs('"+this._entity+"')";
            const oRuntimeConfig = this.getView().getModel().getProperty(sPath);
            if(!oRuntimeConfig) {
                this.getView().getModel().read(sPath, {
                    success: (oResult) => {
                        this.reloadModels(oResult);
                    }
                });
            } else {
                this.reloadModels(oRuntimeConfig);
            }
            
        },
        reloadModels: function(oRuntimeConfig) {
            const mHeaders = {
                "x-client-system-id": oRuntimeConfig.ClientSystemId,
                "x-client-certificate": oRuntimeConfig.ClientCertificate,
                "x-client-certificate-password": oRuntimeConfig.ClientCertificatePassword,
                "x-sign-port": oRuntimeConfig.SignPort,
                "x-vzd-port": oRuntimeConfig.VzdPort,
                "x-mandant-id": oRuntimeConfig.MandantId,
                "x-workplace-id": oRuntimeConfig.WorkplaceId,
                "x-user-id": oRuntimeConfig.UserId,
                "x-host": oRuntimeConfig.Url
            };

            

            this.getView().getModel("Cards").loadData("connector/event/get-cards", {}, "true", "GET", false, true, mHeaders);

            this.getView().getModel("Certificates").loadData("connector/certificates/readCertificate", {}, "true", "GET", false, true, mHeaders);

            this.getView().getModel("CardTerminals").loadData("connector/event/get-card-terminals", {}, "true", "GET", false, true, mHeaders);



            let m;
            let ip;
            if(m = oRuntimeConfig.Url.match("\/\/([^\/:]+)(:\\d+)?\/")) {
                ip = m.group(1);
            } else {
                ip = oRuntimeConfig.Url;
            }
            fetch("connector/metrics/application", {
                headers: {"Accept": "application/json"}
            }).then((r) => r.json()).then((o) => {
                this.getView().getModel("Metrics").setData({
                    "connectorResponseTime": {
                        "count": o["connectorResponseTime_"+ip]["count"],
                        "min": o["connectorResponseTime_"+ip]["min"],
                        "max": o["connectorResponseTime_"+ip]["max"],
                        "mean": o["connectorResponseTime_"+ip]["mean"]
                    },
                    "currentlyConnectedCards": o["currentlyConnectedCards_"+ip]
                });
            });
            
        },
        getEntityName: function () {
			return "RuntimeConfig";
		}
	});
}, true);