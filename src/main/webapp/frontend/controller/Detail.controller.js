sap.ui.define(
  ["./AbstractDetailController",
  "sap/ui/model/json/JSONModel",
  'sap/ui/core/Fragment',
  "../model/formatter"
  ],
  function (AbstractDetailController, JSONModel, Fragment, formatter) {
    "use strict";

    return AbstractDetailController.extend(
      "sap.f.ShellBarWithFlexibleColumnLayout.controller.Detail",
      {
 
        handlePopoverPress: function (oEvent) {
          var oButton = oEvent.getSource(),
            oView = this.getView();
    
          // create popover
          if (!this._pPopover) {
            this._pPopover = Fragment.load({
              id: oView.getId(),
              name: "sap.f.ShellBarWithFlexibleColumnLayout.view.CertPopover",
              controller: this
            }).then(function(oPopover) {
              oView.addDependent(oPopover);
              oPopover.bindElement("Cards>/ProductCollection/50");
              return oPopover;
            });
          }
          this._pPopover.then(function(oPopover) {
            oPopover.openBy(oButton);
          });
        },
    
        handlePopoverClosePress: function (oEvent) {
          this.getView().byId("certPopover").close();
        },

        formatter: formatter,

        onInit: function () {
          AbstractDetailController.prototype.onInit.apply(this, arguments);

          const oCardsModel = new JSONModel();
          this.getView().setModel(oCardsModel, "Cards");

          const oCardTerminalsModel = new JSONModel();
          this.getView().setModel(oCardTerminalsModel, "CardTerminals");

          const oMetricsModel = new JSONModel();
          this.getView().setModel(oMetricsModel, "Metrics");

          const oVerifyAllModel = new JSONModel();
          this.getView().setModel(oVerifyAllModel, "VerifyAll");


          const oPinStatus = new JSONModel();
          this.getView().setModel(oPinStatus, "PINStatus");

          const oProductInformationModel = new JSONModel();
          this.getView().setModel(oProductInformationModel, "ProductInformation")

        },
        onVerifyPinCh: function (oEvent) {
          const sPath = "/RuntimeConfigs('" + this._entity + "')";
          const oRuntimeConfig = this.getView().getModel().getProperty(sPath);
          const mHeaders = {
            "x-client-system-id": oRuntimeConfig.ClientSystemId,
            "x-client-certificate": oRuntimeConfig.ClientCertificate,
            "x-client-certificate-password":
              oRuntimeConfig.ClientCertificatePassword,
            "x-sign-port": oRuntimeConfig.SignPort,
            "x-vzd-port": oRuntimeConfig.VzdPort,
            "x-mandant-id": oRuntimeConfig.MandantId,
            "x-workplace-id": oRuntimeConfig.WorkplaceId,
            "x-user-id": oRuntimeConfig.UserId,
            "x-host": oRuntimeConfig.Url,
            Accept: "application/json",
          };

          let pinType = "PIN.CH";
          let cardHandle = oEvent
            .getSource()
            .getBindingContext("Cards")
            .getProperty("cardHandle");
          fetch(
            "connector/card/verifyPin?cardHandle=" +
              cardHandle +
              "&pinType=" +
              pinType,
            { headers: mHeaders }
          );
        },
        onVerifyPinSmc: function (oEvent) {
          const sPath = "/RuntimeConfigs('" + this._entity + "')";
          debugger;
          const oRuntimeConfig = this.getView().getModel().getProperty(sPath);
          const mHeaders = {
            "x-client-system-id": oRuntimeConfig.ClientSystemId,
            "x-client-certificate": oRuntimeConfig.ClientCertificate,
            "x-client-certificate-password":
              oRuntimeConfig.ClientCertificatePassword,
            "x-sign-port": oRuntimeConfig.SignPort,
            "x-vzd-port": oRuntimeConfig.VzdPort,
            "x-mandant-id": oRuntimeConfig.MandantId,
            "x-workplace-id": oRuntimeConfig.WorkplaceId,
            "x-user-id": oRuntimeConfig.UserId,
            "x-host": oRuntimeConfig.Url,
            Accept: "application/json",
          };

          let pinType = "PIN.SMC";
          let cardHandle = oEvent
            .getSource()
            .getBindingContext("Cards")
            .getProperty("cardHandle");
          fetch(
            "connector/card/verifyPin?cardHandle=" +
              cardHandle +
              "&pinType=" +
              pinType,
            { headers: mHeaders }
          );
        },
        onChangePinQes: function (oEvent) {
          const sPath = "/RuntimeConfigs('" + this._entity + "')";
          const oRuntimeConfig = this.getView().getModel().getProperty(sPath);
          const mHeaders = {
            "x-client-system-id": oRuntimeConfig.ClientSystemId,
            "x-client-certificate": oRuntimeConfig.ClientCertificate,
            "x-client-certificate-password":
              oRuntimeConfig.ClientCertificatePassword,
            "x-sign-port": oRuntimeConfig.SignPort,
            "x-vzd-port": oRuntimeConfig.VzdPort,
            "x-mandant-id": oRuntimeConfig.MandantId,
            "x-workplace-id": oRuntimeConfig.WorkplaceId,
            "x-user-id": oRuntimeConfig.UserId,
            "x-host": oRuntimeConfig.Url,
            Accept: "application/json",
          };

          let pinType = "PIN.QES";
          let cardHandle = oEvent
            .getSource()
            .getBindingContext("Cards")
            .getProperty("cardHandle");
          fetch(
            "connector/card/changePin?cardHandle=" +
              cardHandle +
              "&pinType=" +
              pinType,
            { headers: mHeaders }
          );
        },
        onChangePinCh: function (oEvent) {
          const sPath = "/RuntimeConfigs('" + this._entity + "')";
          const oRuntimeConfig = this.getView().getModel().getProperty(sPath);
          const mHeaders = {
            "x-client-system-id": oRuntimeConfig.ClientSystemId,
            "x-client-certificate": oRuntimeConfig.ClientCertificate,
            "x-client-certificate-password":
              oRuntimeConfig.ClientCertificatePassword,
            "x-sign-port": oRuntimeConfig.SignPort,
            "x-vzd-port": oRuntimeConfig.VzdPort,
            "x-mandant-id": oRuntimeConfig.MandantId,
            "x-workplace-id": oRuntimeConfig.WorkplaceId,
            "x-user-id": oRuntimeConfig.UserId,
            "x-host": oRuntimeConfig.Url,
            Accept: "application/json",
          };

          let pinType = "PIN.CH";
          let cardHandle = oEvent
            .getSource()
            .getBindingContext("Cards")
            .getProperty("cardHandle");
          fetch(
            "connector/card/changePin?cardHandle=" +
              cardHandle +
              "&pinType=" +
              pinType,
            { headers: mHeaders }
          );
        },
        _onMatched: function (oEvent) {
          AbstractDetailController.prototype._onMatched.apply(this, arguments);
          const sPath = "/RuntimeConfigs('" + this._entity + "')";
          const oRuntimeConfig = this.getView().getModel().getProperty(sPath);
          if (!oRuntimeConfig) {
            this.getView()
              .getModel()
              .read(sPath, {
                success: (oResult) => {
                  this.reloadModels(oResult);
                },
              });
          } else {
            this.reloadModels(oRuntimeConfig);
          }
        },
        reloadModels: function (oRuntimeConfig) {
          const mHeaders = {
            "x-client-system-id": oRuntimeConfig.ClientSystemId,
            "x-client-certificate": oRuntimeConfig.ClientCertificate,
            "x-client-certificate-password":
              oRuntimeConfig.ClientCertificatePassword,
            "x-sign-port": oRuntimeConfig.SignPort,
            "x-vzd-port": oRuntimeConfig.VzdPort,
            "x-mandant-id": oRuntimeConfig.MandantId,
            "x-workplace-id": oRuntimeConfig.WorkplaceId,
            "x-user-id": oRuntimeConfig.UserId,
            "x-host": oRuntimeConfig.Url,
            Accept: "application/json",
          };


          this.getView()
            .getModel("PINStatus")
            .loadData(
              "connector/card/pinStatus",
              {},
              "true",
              "GET",
              false,
              true,
              mHeaders
            );

          this.getView()
            .getModel("Cards")
            .loadData(
              "connector/event/get-cards",
              {},
              "true",
              "GET",
              false,
              true,
              mHeaders
            );

          this.getView()
            .getModel("CardTerminals")
            .loadData(
              "connector/event/get-card-terminals",
              {},
              "true",
              "GET",
              false,
              true,
              mHeaders
            );

          this.getView()
          .getModel("ProductInformation")
          .loadData(
            "connector/productTypeInformation/getVersion",
            {},
            "true",
            "GET",
            false,
            true,
            mHeaders
          );

          fetch("connector/certificate/verifyAll", { headers: mHeaders }).then(
            (response) =>
              response
                .json()
                .then((data) => ({
                  data: data,
                  status: response.status,
                }))
                .then((res) => {
                  let numberOfCards = res.data.length;
                  let arrayData = [];
                  for (let i = 0; i < numberOfCards; i++) {
                    if (!res.data[i].readCardCertificateResponse) continue;
                    arrayData.push({
                      cardHandle: res.data[i].cardInfoType.cardHandle,
                      cardHolderName: res.data[i].cardInfoType.cardHolderName,
                      certInfos:
                        res.data[i].readCardCertificateResponse.x509DataInfoList
                          .x509DataInfo,
                      verifyResponses: res.data[i].verifyCertificateResponse,
                    });
                  }

                  let plainList = [];
                  for (let j = 0; j < arrayData.length; j++) {
                    for (let q = 0; q < arrayData[j].certInfos.length; q++) {
                      plainList.push({
                        cardHolderName: arrayData[j].cardHolderName,
                        certRef: arrayData[j].certInfos[q].certRef,
                        cardHandle: arrayData[j].cardHandle,
                        serial:
                          arrayData[j].certInfos[q].x509Data.x509IssuerSerial
                            .x509SerialNumber,
                        verify:
                          arrayData[j].verifyResponses[q].verificationStatus
                            .verificationResult,
                      });
                    }
                  }

                  this.getView()
                    .getModel("VerifyAll")
                    .setData({
                      certificateCollection: { certificates: plainList },
                    });

                  let m;
                  let ip;
                  if ((m = oRuntimeConfig.Url.match("//([^/:]+)(:\\d+)?/"))) {
                    ip = m.group(1);
                  } else {
                    ip = oRuntimeConfig.Url;
                  }
                  fetch("connector/metrics/application", {
                    headers: { Accept: "application/json" },
                  })
                    .then((r) => r.json())
                    .then((o) => {
                      this.getView()
                        .getModel("Metrics")
                        .setData({
                          connectorResponseTime: {
                            count: o["connectorResponseTime_" + ip]["count"],
                            min: o["connectorResponseTime_" + ip]["min"],
                            max: o["connectorResponseTime_" + ip]["max"],
                            mean: o["connectorResponseTime_" + ip]["mean"],
                          },
                          currentlyConnectedCards:
                            o["currentlyConnectedCards_" + ip],
                        });
                    });
                })
          );
        },
        getEntityName: function () {
          return "RuntimeConfig";
        },
      }
    );
  },
  true
);
