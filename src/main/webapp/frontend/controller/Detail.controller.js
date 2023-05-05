sap.ui.define(
  ["./AbstractDetailController",
  "sap/ui/model/json/JSONModel",
  "../utils/formatter"
  ],
  function (AbstractDetailController, JSONModel, formatter) {
    "use strict";

    return AbstractDetailController.extend(
      "ap.f.ShellBarWithFlexibleColumnLayout.controller.Detail",
      {
      formatter: formatter,
        onInit: function () {
          AbstractDetailController.prototype.onInit.apply(this, arguments);

          let certData = null;

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
        removeCardHolderBoxBorders: function () {
          const nodeList = document.querySelectorAll(".availableCertsClass td");
          if(nodeList.length > 0) {
            let contentLength = [];
            for(let i=0;i<this.certData.length;i++) contentLength.push(this.certData[i].certInfos.length);
            let loopCount = 0;
            let aux = 0;
            let ct2 = 0;
            for(let i=1;i<nodeList.length;) {
                aux = contentLength[ct2];
                if (loopCount > aux) ct2++;
                if(loopCount % aux != 0) nodeList[i].style.borderTop = 0;
                i+=8;
                loopCount++
            }
          }
        },
        removeAliasBoxBorders: function () {
          const nodeList = document.querySelectorAll(".availableCertsClass td");
          if(nodeList.length > 0) {
            let contentLength = [];
            for(let i=0;i<this.certData.length;i++) contentLength.push(this.certData[i].certInfos.length);
            let loopCount = 0;
            let aux = 0;
            let ct2 = 0;
            for(let i=3;i<nodeList.length;) {
                aux = contentLength[ct2];
                if (loopCount > aux) ct2++;
                if(loopCount % aux != 0) nodeList[i].style.borderTop = 0;
                i+=8;
                loopCount++
            }
          }
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
                  this.certData = arrayData;

                  let plainList = [];
                  for (let j = 0; j < arrayData.length; j++) {
                    for (let q = 0; q < arrayData[j].certInfos.length; q++) {
                      plainList.push({
                        cardHolderName: (q == 0) ? arrayData[j].cardHolderName : "",
                        certRef: arrayData[j].certInfos[q].certRef,
                        cardHandle: (q == 0) ? arrayData[j].cardHandle : "",
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
                    .then((r) => {
                                 r.json();
                                 this.removeCardHolderBoxBorders();
                                 this.removeAliasBoxBorders();
                    })
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
