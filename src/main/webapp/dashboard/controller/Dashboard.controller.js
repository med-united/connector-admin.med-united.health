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
          this.setDashboard();
        },

        setDashboard: function(){
             const self = this;
             const runtimeConfigModel = new sap.ui.model.odata.v2.ODataModel("../Data.svc",true);
             let url = document.URL;
             url = url.endsWith("#") ? url.substring(0, url.length-1) : url.substring(0, url.length);
             runtimeConfigModel.read("/RuntimeConfigs", {
               success: function (oData) {
                 const configs = oData.results;
                 const terminalData = {"Amount" : 0, "Inactive": 0};
                 const connectorData = {"Inactive": 0};
                 const date = new Date().toJSON();
                 const cardTypes = {"SMC_KT" : 0, "SMC_B" : 0, "HBA": 0, "EGK": 0};
                 const invalidCertCards = {"SMC_KT" : 0, "SMC_B" : 0, "HBA": 0, "EGK": 0};
                 const verifiedPinStatus = {"SMC_B" : 0, "HBA_CH": 0, "HBA_QES" : 0, "EGK": 0};
                 const verifiablePinStatus = {"SMC_B" : 0, "HBA_CH": 0, "HBA_QES" : 0, "EGK": 0};
                 const transportPinStatus = {"SMC_B" : 0, "HBA_CH": 0, "HBA_QES" : 0, "EGK": 0};
                 const blockedPinStatus = {"SMC_B" : 0, "HBA_CH": 0, "HBA_QES" : 0, "EGK": 0};
                 const emptyPinStatus = {"SMC_B" : 0, "HBA_CH": 0, "HBA_QES" : 0, "EGK": 0};
                 const disabledPinStatus = {"SMC_B" : 0, "HBA_CH": 0, "HBA_QES" : 0, "EGK": 0};
                 const errorPinStatus = {"SMC_B" : 0, "HBA_CH": 0, "HBA_QES" : 0, "EGK": 0};
                 const stats = {"VERIFIED" : verifiedPinStatus, "VERIFIABLE" : verifiablePinStatus, "TRANSPORT_PIN" : transportPinStatus, "BLOCKED" : blockedPinStatus, "EMPTY_PIN": emptyPinStatus, "DISABLED": disabledPinStatus, "ERROR" : errorPinStatus};
                 const numConfigs = configs.length;
                 let numResponses = 0;
                 let numCards = 0;
                 configs.forEach(function (config) {
                   let getCardsHeaders = {
                     "x-user-id": config.UserId,
                     "x-host": config.Url,
                     "x-mandant-id": config.MandantId,
                     "x-client-system-id": config.ClientSystemId,
                     "x-workplace-id": config.WorkplaceId,
                     "x-username": config.Username,
                     "x-password": config.Password,
                     "x-use-certificate-auth": config.UseCertificateAuth,
                     "x-client-certificate": config.ClientCertificate,
                     "x-client-certificate-password": config.ClientCertificatePassword,
                     "x-use-basic-auth": config.UseBasicAuth,
                     "x-basic-auth-username": config.BasicAuthUsername,
                     "x-basic-auth-password": config.BasicAuthPassword,
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
                          const terminals = data[0].cardTerminals.cardTerminal;
                          self.readTerminalData(terminals, terminalData);
                          const cards = data[1].cards.card;
                          numCards = numCards + cards.length;
                          self.readCertData(cards, cardTypes, invalidCertCards, date);
                          const pinStatus = data[3];
                          self.getStatus(pinStatus, stats);
                        }
                        else{
                           connectorData["Inactive"] = connectorData["Inactive"] + 1;
                        }
                        if(numResponses == numConfigs){
                           const oConnectors = data[2];
                           self.excludeColumns(stats);
                           self.writeConnectorData(oConnectors, numConfigs, url, terminalData, numCards, connectorData);
                           self.writeCardData(cardTypes, invalidCertCards, stats);
                        }
                     });
                   });
                 },
                 error: function (error) {
                    console.log(error);
                 },
               });
        },

        readCertData: function(cards, cardTypes, invalidCertCards, date){
          for(let k = 0; k < cards.length; k++){
           const cardType = cards[k].cardType;
           cardTypes[cardType] = cardTypes[cardType] + 1;
           invalidCertCards[cardType] = cards[k].certificateExpirationDate < date ? invalidCertCards[cardType] + 1 : invalidCertCards[cardType];
          }
        },

        readTerminalData: function(terminals, terminalData){
          for(let i = 0; i< terminals.length; i++){
            terminalData["Amount"] = terminalData["Amount"] + 1;
            terminalData["Inactive"] = terminals[i].connected ? terminalData["Inactive"] : terminalData["Inactive"] + 1
          }
        },

        getStatus: function(pinStatus, stats){
          for(let j = 0; j < pinStatus.length; j++){
             let type = pinStatus[j].cardType;
             if (type == "HBA"){
               type = pinStatus[j].pinType == "PIN.CH" ? "HBA_CH" : "HBA_QES";
             }
             if(pinStatus[j].status.includes("FEHLERCODE")){
                pinStatus[j].status = "ERROR";
             }
             if(type != "UNKNOWN"){
                stats[pinStatus[j].status][type] = stats[pinStatus[j].status][type] + 1;
             }
          }
        },

        excludeColumns: function(stats){
         let column = 3;
         const oTable = this.getView().byId("cardsTable");
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
        },

        writeConnectorData: function(oConnectors, numConfigs, url, terminalData, numCards, connectorData){
          let connectorContent = [];
          const oConnectorList = this.getView().byId("ConnectorListCard");
          connectorContent.push({
            "Name" : "Konnektoren",
            "Value": numConfigs,
            "Icon": url + "dashboard/images/Connector.png",
            "State" : connectorData["Inactive"] > 0 ? "Error" : "Success",
            "Info": connectorData["Inactive"] > 0 ? "Offline: " + connectorData["Inactive"] : this.translate("AllOnline")
          });
          connectorContent.push({
            "Name" : "Kartenterminals",
            "Value" : terminalData["Amount"],
            "Icon": url + "dashboard/images/CardTerminal.png",
            "State": terminalData["Inactive"] > 0 ? "Error" : (terminalData["Amount"] === 0) ? "Error" : "Success",
            "Info" : terminalData["Inactive"] > 0 ? "Offline: " + terminalData["Inactive"] : (terminalData["Amount"] === 0) ? this.translate("NotConnected") : this.translate("AllOnline")
          });
          connectorContent.push({
            "Name" : "Karten",
            "Value": numCards,
            "Icon": url + "dashboard/images/Card.png",
            "State": "None"
          });
          oConnectors["sap.card"].content.data.json = connectorContent;
          oConnectorList.setManifest(oConnectors);
         },

        writeCardData: function(cardTypes, invalidCertCards, stats){
          let cardContent = [];
          const oCardList = this.getView().getModel("CardsCard");
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
            "pinError" : cardTypes["EGK"] > 0 ? stats["ERROR"]["EGK"] : "-",
            "validity" : cardTypes["EGK"] > 0 ? invalidCertCards["EGK"] > 0 ? this.translate("Expired") + invalidCertCards["EGK"] : this.translate("AllValid") : "-",
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
            "pinError" : cardTypes["HBA"] > 0 ? stats["ERROR"]["HBA_CH"] : "-",
            "validity" : cardTypes["HBA"] > 0 ? invalidCertCards["HBA"] > 0 ? this.translate("Expired") + invalidCertCards["HBA"] : this.translate("AllValid") : "-",
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
            "pinError" : cardTypes["HBA"] > 0 ? stats["ERROR"]["HBA_QES"] : "-",
            "validity" : cardTypes["HBA"] > 0 ? invalidCertCards["HBA"] > 0 ? this.translate("Expired") + invalidCertCards["HBA"] : this.translate("AllValid") : "-",
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
            "pinError" : cardTypes["SMC_B"] > 0 ? stats["ERROR"]["SMC_B"] : "-",
            "validity" : cardTypes["SMC_B"] > 0 ? invalidCertCards["SMC_B"] > 0 ? this.translate("Expired") + invalidCertCards["SMC_B"] : this.translate("AllValid") : "-",
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
            "pinError" : "-",
            "validity" : cardTypes["SMC_KT"] > 0 ? invalidCertCards["SMC_KT"] > 0 ? this.translate("Expired") + invalidCertCards["SMC_KT"] : this.translate("AllValid") : "-",
            "validityState" : invalidCertCards["SMC_KT"] > 0 ? "Error" : "Success",
          });
          oCardList.setData(cardContent);
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
