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

          const oCardTerminalsModel = new JSONModel();
          this.getView().setModel(oCardTerminalsModel, "CardTerminals");

          const oMetricsModel = new JSONModel();
          this.getView().setModel(oMetricsModel, "Metrics");

          const oVerifyAllModel = new JSONModel();
          this.getView().setModel(oVerifyAllModel, "VerifyAll");

          const oPinStatus = new JSONModel();
          this.getView().setModel(oPinStatus, "PINStatus");

          const oCertSubjectModel = new JSONModel();
          this.getView().setModel(oCertSubjectModel, "CertSubject");

          this.getView().setModel(new JSONModel(), "ConnectorSDS");
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

        reloadModels: function (oRuntimeConfig) {

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
                  for (const element of arrayData) {
                    for (let q = 0; q < element.certInfos.length; q++) {
                      plainList.push({
                        cardHolderName: element.cardHolderName,
                        certRef: element.certInfos[q].certRef,
                        cardHandle: element.cardHandle,
                        serial:
                          element.certInfos[q].x509Data.x509IssuerSerial
                            .x509SerialNumber,
                        verify:
                          element.verifyResponses[q].verificationStatus
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
                const cardTerminals = this.getView().getModel("CardTerminals").getData().cardTerminals.cardTerminal;
                const ctId = card.ctId;
                var name = "Unbekannt";
                for (const cardTerminal of cardTerminals) {
                    if(ctId == cardTerminal.ctId){
                        name = cardTerminal.name;
                    }
                }
                card["cardTerminalSlotId"] =  name + ": " + card.slotId;
              }
              this.getView().getModel("Cards").setData(da);
            });

          fetch("connector/sds/config", { headers: mHeaders })
            .then((re) => re.json())
            .then((remoteConfig) => {
              this.getView().getModel("ConnectorSDS").setData(remoteConfig);
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
            "x-connector-brand": oRuntimeConfig.ConnectorBrand,
            "x-mandant-id": oRuntimeConfig.MandantId,
            "x-workplace-id": oRuntimeConfig.WorkplaceId,
            "x-user-id": oRuntimeConfig.UserId,
            "x-host": oRuntimeConfig.Url,
            "x-use-certificate-auth": oRuntimeConfig.UseCertificateAuth,
            "x-use-basic-auth": oRuntimeConfig.UseBasicAuth,
			"x-basic-auth-username": oRuntimeConfig.BasicAuthUsername,
			"x-basic-auth-password": oRuntimeConfig.BasicAuthPassword,
			};
        },

        _getConnectorType: async function () {
          const mHeaders = this._getHttpHeadersFromRuntimeConfig();

          let promise = new Promise((resolve) => {
            fetch("connector/sds/config", { headers: mHeaders })
              .then((remoteResponse) => remoteResponse.json())
              .then((remoteConfig) => {
                let productCode =
                  remoteConfig.productInformation.productIdentification
                    .productCode;
                let connectorBrand;
                if (productCode == "secu_kon") connectorBrand = "secunet";
                else if (productCode == "RKONN") connectorBrand = "rise";
                else if (productCode == "kocobox") connectorBrand = "kocobox";
                resolve(connectorBrand);
              });
          });

          return await promise;
        },

        pwdOnCancel: function (oEvent) {
          oEvent.getSource().getParent().close();
          oEvent.getSource().getParent().destroy();
        },

        restartConnector: function () {
          let oView = this.getView();
          const me = this;

          const sPath = "/RuntimeConfigs('" + this._entity + "')";
          const oRuntimeConfig = this.getView().getModel().getProperty(sPath);

          let username = oRuntimeConfig.Username;
          let password = oRuntimeConfig.Password;

          if (username && password) {
            this.pwdOnRestart();
          } else {
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
          }
        },

        pwdOnRestart: async function (oEvent) {
          try {
            const sPath = "/RuntimeConfigs('" + this._entity + "')";
            const oRuntimeConfig = this.getView().getModel().getProperty(sPath);
            const restartHeaders = {
              "x-client-certificate": oRuntimeConfig.ClientCertificate,
              "x-client-certificate-password": oRuntimeConfig.ClientCertificatePassword,
              "Content-Type": "application/json",
            };

            let username = oRuntimeConfig.Username;
            let password = oRuntimeConfig.Password;

            if (!username || !password) {
              username = this.byId("usernameInput").getValue();
              password = this.byId("passwordInput").getValue();
              oEvent.getSource().getParent().close();
              oEvent.getSource().getParent().destroy();
            }

            const requestBody = {
              username: username,
              password: password,
            };

            MessageToast.show(this.translate("restarting"));

            const connectorBrand = oRuntimeConfig.ConnectorBrand;

            let restartUrl =
              "connector/management/" +
              connectorBrand +
              "/restart?connectorUrl=" +
              oRuntimeConfig.Url;

            const response = await fetch(restartUrl, {
              headers: restartHeaders,
              method: "POST",
              body: JSON.stringify(requestBody),
            });

            if (!response.ok) {
              MessageToast.show("Fehler beim Neustart. Bitte Verbindung und Kennwort pr\u00FCfen");
            }
          } catch (error) {
            console.error(error);
          }
        },
      }
    );
  },
  true
);
