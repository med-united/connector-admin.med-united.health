sap.ui.define(
  [
    "./AbstractDetailController",
    "sap/ui/model/json/JSONModel",
    "sap/m/MessageToast",
    "sap/ui/core/Fragment",
    "sap/ui/core/util/File",
    "../utils/formatter",
  ],
  function (
    AbstractDetailController,
    JSONModel,
    MessageToast,
    Fragment,
    File,
    formatter
  ) {
    "use strict";

    return AbstractDetailController.extend(
      "sap.f.ShellBarWithFlexibleColumnLayout.controller.Detail",
      {
        formatter: formatter,

        onInit: function () {
          AbstractDetailController.prototype.onInit.apply(this, arguments);

          let certData = null;

          const oCardsModel = new JSONModel();

          oCardsModel.attachRequestSent(() => {
            this.byId("cardTable").setBusy(true);
          });
          oCardsModel.attachRequestCompleted(() => {
            this.byId("cardTable").setBusy(false);
          });
          oCardsModel.attachRequestFailed(() => {
            MessageToast.show("Konnte keine Karten laden");
          });

          this.getView().setModel(oCardsModel, "Cards");

          const oProductIDModel = new JSONModel();
          this.getView().setModel(oProductIDModel, "ProductID");

          const oCardTerminalsModel = new JSONModel();
          this.getView().setModel(oCardTerminalsModel, "CardTerminals");

          const oMetricsModel = new JSONModel();
          this.getView().setModel(oMetricsModel, "Metrics");

          const oVerifyAllModel = new JSONModel();
          this.getView().setModel(oVerifyAllModel, "VerifyAll");

          const oPinStatus = new JSONModel();
          this.getView().setModel(oPinStatus, "PINStatus");

          const oProductInformationModel = new JSONModel();
          this.getView().setModel(
            oProductInformationModel,
            "ProductInformation"
          );

          const oCertSubjectModel = new JSONModel();
          this.getView().setModel(oCertSubjectModel, "CertSubject");
        },

        onVerifyPinCh: function (oEvent) {
          const mHeaders = this._getHttpHeadersFromRuntimeConfig();

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
          const mHeaders = this._getHttpHeadersFromRuntimeConfig();

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

        removeSeparatorLines: function (startBox) {
          const nodeList = document.querySelectorAll(".availableCertsClass td");
          if (nodeList.length > 0) {
            let contentLength = [];
            for (let i = 0; i < this.certData.length; i++)
              contentLength.push(this.certData[i].certInfos.length);
            let loopCount = 0;
            let aux = 0;
            let ct2 = 0;
            for (let i = startBox; i < nodeList.length; ) {
              aux = contentLength[ct2];
              if (loopCount > aux) ct2++;
              if (loopCount % aux != 0) nodeList[i].style.borderTop = 0;
              i += 8;
              loopCount++;
            }
          }
        },

        onChangePinQes: function (oEvent) {
          const mHeaders = this._getHttpHeadersFromRuntimeConfig();

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
          const mHeaders = this._getHttpHeadersFromRuntimeConfig();

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

        onChangePinSmc: function (oEvent) {
          const mHeaders = this._getHttpHeadersFromRuntimeConfig();

          let pinType = "PIN.SMC";
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

        pwdOnCancel: function (oEvent) {
          oEvent.getSource().getParent().close();
          oEvent.getSource().getParent().destroy();
        },

        pwdOnRestart: function (oEvent) {
          const sPath = "/RuntimeConfigs('" + this._entity + "')";
          const oRuntimeConfig = this.getView().getModel().getProperty(sPath);
          const restartHeaders = {
            "x-client-certificate": oRuntimeConfig.ClientCertificate,
            "x-client-certificate-password":
              oRuntimeConfig.ClientCertificatePassword,
            "Content-Type": "application/json",
          };
          const username = this.byId("usernameInput").getValue();
          const password = this.byId("passwordInput").getValue();
          const requestBody = {
            username: username,
            password: password,
          };
          fetch(
            "connector/management/secunet/restart?connectorUrl=" +
              oRuntimeConfig.Url +
              "&managementPort=8500",
            {
              headers: restartHeaders,
              method: "POST",
              body: JSON.stringify(requestBody),
            }
          );
          oEvent.getSource().getParent().close();
          MessageToast.show(this.translate("restarting"));
          oEvent.getSource().getParent().destroy();
        },

        restartConnector: function () {
          let oView = this.getView();
          const me = this;

          if (!this.byId("RestartPasswordDialog")) {
            Fragment.load({
              id: oView.getId(),
              name: "sap.f.ShellBarWithFlexibleColumnLayout.view.RestartPasswordDialog",
              controller: this,
            }).then(function (oDialog) {
              me.onAfterCreateOpenDialog({ dialog: oDialog });
              oView.addDependent(oDialog);
              me._openCreateDialog(oDialog);
            });
          } else {
            this._openCreateDialog(this.byId("restartPasswordDialog"));
          }
        },

        reloadModels: function (oRuntimeConfig) {
          this.handleFullScreen();

          const mHeaders = this._getHttpHeadersFromRuntimeConfig();

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
                        cardHolderName:
                          q == 0 ? arrayData[j].cardHolderName : "",
                        certRef: arrayData[j].certInfos[q].certRef,
                        cardHandle: arrayData[j].cardHandle,
                        cardHandle: q == 0 ? arrayData[j].cardHandle : "",
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
                    })
                    .then((q) => {
                      this.removeSeparatorLines(1);
                      this.removeSeparatorLines(2);
                    });
                })
          );
          fetch("connector/event/get-cards", { headers: mHeaders })
            .then((re) => re.json())
            .then((da) => {
              const cards = da.cards.card;
              for (const card of cards) {
                card["option"] =
                  card.cardType == "SMC_KT" || card.cardType == "KVK"
                    ? false
                    : true;
                card["vPIN.CH"] = card.cardType == "HBA" ? true : false;
                card["vPIN.SMC"] = card.cardType == "SMC_B" ? true : false;
                card["cPIN.CH"] =
                  card.cardType == "EGK" || card.cardType == "HBA"
                    ? true
                    : false;
                card["cPIN.QES"] = card.cardType == "HBA" ? true : false;
                card["cPIN.SMC"] = card.cardType == "SMC_B" ? true : false;
              }
              this.getView().getModel("Cards").setData(da);
            });

            fetch("/connector/productSpecifications/getConnectorSpecifications", { headers: mHeaders }).then((re) => re.json()).then((remoteProductData) => {
              this.getView().getModel("ProductID").setData(remoteProductData);
            });
        },

        getEntityName: function () {
          return "RuntimeConfig";
        },

        handlePopoverPress: function (oEvent) {
          let oControl = oEvent.getSource(),
            oView = this.getView();

          if (!this._pPopover) {
            this._pPopover = Fragment.load({
              id: oView.getId(),
              name: "sap.f.ShellBarWithFlexibleColumnLayout.view.CertPopover",
              controller: this,
            }).then(function (oPopover) {
              oView.addDependent(oPopover);
              return oPopover;
            });
          }
          this._pPopover.then(
            function (oPopover) {
              const oHeaders = this._getHttpHeadersFromRuntimeConfig();
              const sCertRef = oControl
                .getBindingContext("VerifyAll")
                .getProperty("certRef");
              const sCardHandle = oControl
                .getBindingContext("VerifyAll")
                .getProperty("cardHandle");
              oView
                .getModel("CertSubject")
                .loadData(
                  "connector/certificate/" + sCertRef + "/" + sCardHandle,
                  {},
                  "true",
                  "GET",
                  false,
                  true,
                  oHeaders
                );
              oPopover.openBy(oControl);
            }.bind(this)
          );
        },

        handlePopoverClosePress: function (oEvent) {
          this.getView().byId("certPopover").close();
        },

        translateTextWithPrefix: function (prefix, certFieldName) {
          return this.getOwnerComponent()
            .getModel("i18n")
            .getResourceBundle()
            .getText(prefix + certFieldName);
        },

        onDownloadSDS: function (oEvent) {
          const oHeaders = this._getHttpHeadersFromRuntimeConfig();
          fetch("connector/sds/file", {
            headers: oHeaders,
          })
            .then((response) => response.blob())
            .then((xBlob) =>
              File.save(
                xBlob,
                "Connector",
                "sds",
                "application/octet-stream",
                null,
                false
              )
            );
        },

        _getRuntimeConfigPath: function () {
          return "/RuntimeConfigs('" + this._entity + "')";
        },

        _getHttpHeadersFromRuntimeConfig: function () {
          const sPath = this._getRuntimeConfigPath();
          const oRuntimeConfig = this.getView().getModel().getProperty(sPath);
          return {
            Accept: "application/json",
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
          };
        },
      }
    );
  },
  true
);
