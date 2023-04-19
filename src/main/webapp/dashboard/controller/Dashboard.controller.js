sap.ui.define(
  [
    "./AbstractMasterController",
    "sap/ui/model/json/JSONModel",
    "sap/ui/integration/widgets/Card"
  ],
  function (AbstractMasterController, JSONModel, Card) {
    "use strict";

    return AbstractMasterController.extend(
      "sap.f.ShellBarWithFlexibleColumnLayout.controller.Dashboard",
      {
        onInit: function () {

          var ConnectorList = {
                              	"sap.app": {
                              		"id": "ConnectorList",
                              		"type": "card",
                              		"applicationVersion": {
                              		  "version": "1.0.0"
                              		}
                              	},
                              	"sap.card": {
                              		"type": "List",
                              		"header": {
                              			"title": "Konnektorübersicht",
                              			"icon": {
                              				"src": "sap-icon://phone"
                              			}
                              		},
                              		"content": {
                              			"data": { "json":
                              			    [
                              			    ]
                              			},
                              			"item": {
                              				"title": {
                              					"value": "{Name}"
                              				},
                              				"description": {
                              					"value": "{Value}"
                              				}
                              			}
                              		}
                              	}
                              };

          this.oRouter = this.getOwnerComponent().getRouter();
          this._bDescendingSort = false;
          var oCardContainer = this.getView().byId("dynamicPageId");

          const runtimeConfigModel = new sap.ui.model.odata.v2.ODataModel(
            "../Data.svc",
            true
          );

           function attachCard(){
             var oCard = new Card();
             oCard.setManifest(ConnectorList);
             oCardContainer.setContent(oCard);
           }

          runtimeConfigModel.read("/RuntimeConfigs", {
              success: function (oData) {
                const configs = oData.results;
                const content = [];
                let anz_Connectors = configs.length;
                let anz_Cards = 0;
                const numConfigs = configs.length;
                let numResponses = 0;
                configs.forEach(function (config) {
                   let getCardsHeaders = {
                      "x-client-system-id": config.ClientSystemId,
                      "x-client-certificate": config.ClientCertificate,
                       "x-client-certificate-password":
                              config.ClientCertificatePassword,
                            "x-mandant-id": config.MandantId,
                            "x-workplace-id": config.WorkplaceId,
                            "x-host": config.Url,
                            Accept: "application/json",
                          };
                          fetch("/connector/event/get-cards", {
                            headers: getCardsHeaders,
                          }).then((response) => {
                            numResponses++;
                            if (response.ok) {
                              response.json().then((data) => {
                                let cards = data.cards.card;
                                anz_Cards = anz_Cards + cards.length
                              });
                            }
                            if (numResponses === numConfigs) {
                              // Check if all responses have been received
                              content.push({
                                "Name" : "Anzahl Konnektoren",
                                "Value" : anz_Connectors
                              });
                              content.push({
                                "Name" : "Anzahl Karten",
                                "Value" : anz_Cards
                              });
                              ConnectorList["sap.card"].content.data.json = content;
                              attachCard();
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
          this.onInit();
        },

        onRouteToMaster: function (oEvent) {
          this.oRouter.navTo("master");
        },
      }
    );
  }
);
