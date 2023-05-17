sap.ui.define(
  [
    "./AbstractMasterController",
    "sap/ui/model/json/JSONModel"
  ],
  function (AbstractMasterController, JSONModel) {
    "use strict";

    return AbstractMasterController.extend(
      "sap.f.ShellBarWithFlexibleColumnLayout.controller.Dashboard",
      {
        onInit: function () {

          this.oRouter = this.getOwnerComponent().getRouter();
          this._bDescendingSort = false;
          const oCardCard = new JSONModel();
          this.getView().setModel(oCardCard, "CardsCard")
          this.setCardList();
        },

        setCardList: function(){
             const runtimeConfigModel = new sap.ui.model.odata.v2.ODataModel("../Data.svc",true);
             const oConnectorList = this.getView().byId("ConnectorListCard");
             const oTable = this.getView().byId("cardsTable");
             const oCardList = this.getView().getModel("CardsCard");
             let url = document.URL;
             url = url.endsWith("#") ? url.substring(0, url.length-1) : url.substring(0, url.length);
             runtimeConfigModel.read("/RuntimeConfigs", {
               success: function (oData) {
                 const configs = oData.results;
                 let numCards = 0;
                 let numTerminals = 0;
                 let inactiveConnectors= 0;
                 let inactiveTerminals = 0;
                 let activeConnectors= 0;
                 let activeTerminals = 0;
                 const date = new Date().toJSON();
                 const cardTypes = {"SMC_KT" : 0, "SMC_B" : 0, "HBA": 0, "EGK": 0};
                 const invalidCertCards = {"SMC_KT" : 0, "SMC_B" : 0, "HBA": 0, "EGK": 0};
                 const verifiedPinStatus = {"SMC_B" : 0, "HBA_CH": 0, "HBA_QES" : 0, "EGK": 0};
                 const verifiablePinStatus = {"SMC_B" : 0, "HBA_CH": 0, "HBA_QES" : 0, "EGK": 0};
                 const transportPinStatus = {"SMC_B" : 0, "HBA_CH": 0, "HBA_QES" : 0, "EGK": 0};
                 const blockedPinStatus = {"SMC_B" : 0, "HBA_CH": 0, "HBA_QES" : 0, "EGK": 0};
                 const emptyPinStatus = {"SMC_B" : 0, "HBA_CH": 0, "HBA_QES" : 0, "EGK": 0};
                 const disabledPinStatus = {"SMC_B" : 0, "HBA_CH": 0, "HBA_QES" : 0, "EGK": 0};
                 const stats = {"VERIFIED" : verifiedPinStatus, "VERIFIABLE" : verifiablePinStatus, "TRANSPORT_PIN" : transportPinStatus, "BLOCKED" : blockedPinStatus, "EMPTY_PIN": emptyPinStatus, "DISABLED": disabledPinStatus};
                 const numConfigs = configs.length;
                 let numResponses = 0;
                 configs.forEach(function (config) {
                   let getCardsHeaders = {
                     "x-client-system-id": config.ClientSystemId,
                     "x-client-certificate": config.ClientCertificate,
                     "x-client-certificate-password": config.ClientCertificatePassword,
                     "x-mandant-id": config.MandantId,
                     "x-workplace-id": config.WorkplaceId,
                     "x-host": config.Url,
                     Accept: "application/json",
                   };
                   Promise.all([
                     fetch("connector/event/get-card-terminals", {headers : getCardsHeaders}),
                     fetch("connector/event/get-cards", {headers : getCardsHeaders}),
                     fetch("dashboard/resources/ConnectorList.json"),
                     fetch("connector/card/pinStatus", {headers : getCardsHeaders})
                   ]).then(function (responses) {
                     return Promise.all(responses.map(function (response) {
                       if(response.ok){
                          return response.json();
                       }
                     }));
                     }).then(function (data) {
                        numResponses++;
                        if(data[0]!=null){
                          let terminals = data[0].cardTerminals.cardTerminal;
                          numTerminals = numTerminals + terminals.length;
                          for(let i = 0; i< terminals.length; i++){
                             terminals[i].connected ? activeTerminals++ : inactiveTerminals++;
                          }
                          const cards = data[1].cards.card;
                          numCards = numCards + cards.length;
                          for(let k = 0; k < cards.length; k++){
                            const cardType = cards[k].cardType;
                            cardTypes[cardType] = cardTypes[cardType] + 1;
                            invalidCertCards[cardType] = cards[k].certificateExpirationDate < date ? invalidCertCards[cardType] + 1 : invalidCertCards[cardType];
                          }
                          activeConnectors++;
                          const pinStatus = data[3];
                          for(let j = 0; j < pinStatus.length; j++){
                                let type = pinStatus[j].cardType;
                                if (type == "HBA"){
                                    type = pinStatus[j].pinType == "PIN.CH" ? "HBA_CH" : "HBA_QES";
                                }
                                stats[pinStatus[j].status][type] = stats[pinStatus[j].status][type] + 1;
                          }
                          let column = 3;
                          for(let status in stats)
                          {
                            let sum = 0;
                            for(let typ in stats[status])
                            {
                                sum = sum + stats[status][typ];
                                stats[status][typ] = stats[status][typ] === 0 ? "-" : stats[status][typ];
                            }
                            if(sum === 0)
                             {
                                oTable.getColumns()[column].setVisible(false);
                             }
                            column++;
                          }
                        }
                        else{
                           inactiveConnectors++;
                        }
                        if(numResponses == numConfigs){
                           const connectorData = data[2];
                           let connectorContent = [];
                           let cardContent = [];
                           connectorContent.push({
                             "Name" : "Konnektoren",
                             "Value": configs.length,
                             "Icon": url + "dashboard/images/Connector.png",
                             "State" : inactiveConnectors > 0 ? "Error" : "Success",
                             "Info": inactiveConnectors > 0 ? "Offline: " + inactiveConnectors : "Alle Online"
                           });
                           connectorContent.push({
                             "Name" : "Kartenterminals",
                             "Value" : numTerminals,
                             "Icon": url + "dashboard/images/CardTerminal.png",
                             "State": inactiveTerminals > 0 ? "Error" : "Success",
                             "Info" : inactiveTerminals > 0 ? "Offline: " + inactiveTerminals : "Alle Online"
                           });
                           connectorContent.push({
                             "Name" : "Karten",
                             "Value": numCards,
                             "Icon": url + "dashboard/images/Card.png",
                             "State": "None"
                           });
                           cardContent.push({
                             "cardType": "EGK",
                             "Value": cardTypes["EGK"],
                             "pinType": "PIN.CH",
                             "pinVerified" : cardTypes["EGK"] > 0 ? stats["VERIFIED"]["EGK"] : "-",
                             "pinVerifiable" : cardTypes["EGK"] > 0 ? stats["VERIFIABLE"]["EGK"] : "-",
                             "pinTransport" : cardTypes["EGK"] > 0 ? stats["TRANSPORT_PIN"]["EGK"] : "-",
                             "pinBlocked" : cardTypes["EGK"] > 0 ? stats["BLOCKED"]["EGK"] : "-",
                             "pinDisabled" : cardTypes["EGK"] > 0 ? stats["DISABLED"]["EGK"] : "-",
                             "pinEmpty" : cardTypes["EGK"] > 0 ? stats["EMPTY_PIN"]["EGK"] : "-",
                             "validity" : cardTypes["EGK"] > 0 ? invalidCertCards["EGK"] > 0 ? "Abgelaufen: " + invalidCertCards["EGK"] : "Alle gültig" : "-",
                             "validityState" : invalidCertCards["EGK"] > 0 ? "Error" : "Success"
                           });
                           cardContent.push({
                             "cardType": "HBA",
                             "Value": cardTypes["HBA"],
                             "pinType": "PIN.CH",
                             "pinVerified" : cardTypes["HBA"] > 0 ? stats["VERIFIED"]["HBA_CH"] : "-",
                             "pinVerifiable" : cardTypes["HBA"] > 0 ? stats["VERIFIABLE"]["HBA_CH"] : "-",
                             "pinTransport" : cardTypes["HBA"] > 0 ? stats["TRANSPORT_PIN"]["HBA_CH"] : "-",
                             "pinBlocked" : cardTypes["HBA"] > 0 ? stats["BLOCKED"]["HBA_CH"] : "-",
                             "pinDisabled" : cardTypes["HBA"] > 0 ? stats["DISABLED"]["HBA_CH"] : "-",
                             "pinEmpty" : cardTypes["HBA"] > 0 ? stats["EMPTY_PIN"]["HBA_CH"] : "-",
                             "validity" : cardTypes["HBA"] > 0 ? invalidCertCards["HBA"] > 0 ? "Abgelaufen: " + invalidCertCards["HBA"] : "Alle gültig" : "-",
                             "validityState" : invalidCertCards["HBA"] > 0 ? "Error" : "Success"
                           });
                           cardContent.push({
                             "cardType": "HBA",
                             "Value": cardTypes["HBA"],
                             "pinType": "PIN.QES",
                             "pinVerified" : cardTypes["HBA"] > 0 ? stats["VERIFIED"]["HBA_QES"] : "-",
                             "pinVerifiable" : cardTypes["HBA"] > 0 ? stats["VERIFIABLE"]["HBA_QES"] : "-",
                             "pinTransport" : cardTypes["HBA"] > 0 ? stats["TRANSPORT_PIN"]["HBA_QES"] : "-",
                             "pinBlocked" : cardTypes["HBA"] > 0 ? stats["BLOCKED"]["HBA_QES"] : "-",
                             "pinDisabled" : cardTypes["HBA"] > 0 ? stats["DISABLED"]["HBA_QES"] : "-",
                             "pinEmpty" : cardTypes["HBA"] > 0 ? stats["EMPTY_PIN"]["HBA_QES"] : "-",
                             "validity" : cardTypes["HBA"] > 0 ? invalidCertCards["HBA"] > 0 ? "Abgelaufen: " + invalidCertCards["HBA"] : "Alle gültig" : "-",
                             "validityState" : invalidCertCards["HBA"] > 0 ? "Error" : "Success"
                           });
                           cardContent.push({
                             "cardType": "SMC-B",
                             "Value": cardTypes["SMC_B"],
                             "pinType": "PIN.SMC",
                             "pinVerified" : cardTypes["SMC_B"] > 0 ? stats["VERIFIED"]["SMC_B"] : "-",
                             "pinVerifiable" : cardTypes["SMC_B"] > 0 ? stats["VERIFIABLE"]["SMC_B"] : "-",
                             "pinTransport" : cardTypes["SMC_B"] > 0 ? stats["TRANSPORT_PIN"]["SMC_B"] : "-",
                             "pinBlocked" : cardTypes["SMC_B"] > 0 ? stats["BLOCKED"]["SMC_B"] : "-",
                             "pinDisabled" : cardTypes["SMC_B"] > 0 ? stats["DISABLED"]["SMC_B"] : "-",
                             "pinEmpty" : cardTypes["SMC_B"] > 0 ? stats["EMPTY_PIN"]["SMC_B"] : "-",
                             "validity" : cardTypes["SMC_B"] > 0 ? invalidCertCards["SMC_B"] > 0 ? "Abgelaufen: " + invalidCertCards["SMC_B"] : "Alle gültig" : "-",
                             "validityState" : invalidCertCards["SMC_B"] > 0 ? "Error" : "Success"
                           });
                           cardContent.push({
                             "cardType" : "SMC-KT",
                             "Value": cardTypes["SMC_KT"],
                             "pinType": "-",
                             "pinVerified" : "-",
                             "pinVerifiable" : "-",
                             "pinTransport" : "-",
                             "pinBlocked" : "-",
                             "pinDisabled" : "-",
                             "pinEmpty" : "-",
                             "validity" : cardTypes["SMC_KT"] > 0 ? invalidCertCards["SMC_KT"] > 0 ? "Abgelaufen: " + invalidCertCards["SMC_KT"] : "Alle gültig" : "-",
                             "validityState" : invalidCertCards["SMC_KT"] > 0 ? "Error" : "Success",
                           });
                           connectorData["sap.card"].content.data.json = connectorContent;
                           oConnectorList.setManifest(connectorData);
                           oCardList.setData(cardContent);
                        }
                     });
                   });
                 },
                 error: function (error) {
                    console.log(error);
                 },
               });
        },
        onBeforeRendering: function () {
          const oConnectorList = this.getView().byId("ConnectorListCard");
          oConnectorList.setManifest("./dashboard/resources/ConnectorList.json");
          this.onInit();
        },

        onRouteToMaster: function (oEvent) {
          this.oRouter.navTo("master");
        },
      }
    );
  }
);
