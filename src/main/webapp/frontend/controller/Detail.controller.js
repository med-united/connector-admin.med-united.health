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

            const oVerifyAllModel = new JSONModel();
            this.getView().setModel(oVerifyAllModel, "VerifyAll");

            const oCertificatesModel = new JSONModel();
            this.getView().setModel(oCertificatesModel, "Certificates");


            oCardsModel.attachRequestCompleted(function() {
                    console.log(oCardsModel.getData());
            });

            oVerifyAllModel.attachRequestCompleted(function() {
                   // console.log(oCertificatesModel.getData());
                   // console.log(oMetricsModel.getData());
                   // console.log(oVerifyAllModel.getData());
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
                "x-host": oRuntimeConfig.Url,
                "Accept": "application/json"
            };

            
            this.getView().getModel("VerifyAll").loadData("connector/certificate/verifyAll", {}, "true", "GET", false, true, mHeaders);

            this.getView().getModel("Cards").loadData("connector/event/get-cards", {}, "true", "GET", false, true, mHeaders);

            this.getView().getModel("CardTerminals").loadData("connector/event/get-card-terminals", {}, "true", "GET", false, true, mHeaders);

            fetch("connector/certificate/verifyAll", { headers: mHeaders })
            .then(response =>
                response.json().then(data => ({
                    data: data,
                    status: response.status
                })
            ).then(res => {
                let numberOfCards = res.data.length;
                let contentsToShow = [];
                for(let i=0;i<numberOfCards;i++){
                    contentsToShow.push(
                    {
                    "cardHandle" : res.data[i].cardInfoType.cardHandle,
                    "number":5
                    } );
                };
                this.getView().getModel("VerifyAll").setData({
                   theArr: {ct:contentsToShow}
                });
                console.log(this.getView().getModel("VerifyAll").getData());
                console.log(res.status, res.data, res.data.length)
            }));


        },
        getEntityName: function () {
			return "RuntimeConfig";
		}
	});
}, true);