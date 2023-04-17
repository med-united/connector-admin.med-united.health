sap.ui.define(
  [
    "./AbstractMasterController",
    "sap/ui/model/json/JSONModel",
    "../resources/libs/vega5",
    "../resources/libs/vega-embed5",
  ],
  function (AbstractMasterController) {
    "use strict";

    return AbstractMasterController.extend(
      "sap.f.ShellBarWithFlexibleColumnLayout.controller.Dashboard",
      {
        onInit: function () {
          let StackCard = {
             "$schema": "https://vega.github.io/schema/vega/v5.json",
             "title": "Kartentypen aller verfügbaren Konnektoren",
             "width": 500,
             "height": 300,
             "padding": 5,

             "data": [
             {
                "name": "table",
                "values": [],
                "transform": [
                {
                   "type": "stack",
                   "groupby": ["x"],
                   "sort": {"field": "c"},
                   "field": "y"
                }
                ]
             }
             ],

             "scales": [
               {
                "name": "x",
                "type": "band",
                "range": "width",
                "domain": {"data": "table", "field": "x"},
                "paddingInner": 0.1, // distance between stacks
                "paddingOuter": 0.1
             },
             {
             "name": "y",
             "type": "linear",
             "range": "height",
             "nice": 1, // x axis displaying a scale of 1
             "zero": true,
             "domain": {"data": "table", "field": "y1"},
             "round": true
             },
             {
               "name": "color",
               "type": "ordinal",
               "range": ["#5899DA", "#E8743B", "#19A979", "#ED4A7B", "#945ECF"], // SAP Fiori for Web Design Guidelines
               "domain": ["SMC_KT", "SMC_B", "HBA", "EGK", "KVK"],
             }
             ],

             "axes": [
                  {"orient": "bottom", "scale": "x", "zindex": 1, "title":"Konnektoren"},
                  {"orient": "left", "scale": "y", "zindex": 1, "format": ".0f", "title":"Gesteckte Karten", "values": [1, 2, 3, 4,5,6,7,8,9]}
             ],

             "marks": [
                {
                   "type": "rect",
                   "from": {"data": "table"},
                   "encode": {
                      "enter": {
                        "x": {"scale": "x", "field": "x"},
                        "width": {"scale": "x", "band": 1, "offset": -1},
                        "y": {"scale": "y", "field": "y0"},
                        "y2": {"scale": "y", "field": "y1"},
                        "fill": {"scale": "color", "field": "c"}
                      },
                      "update": {
                          "fillOpacity": {"value": 1}
                      },
                      "hover": {
                          "fillOpacity": {"value": 0.5}
                      }
                    }
                }
                ],
                "legends": [
                    {
                      "orient": "right",
                      "direction": "vertical",
                      "fill": "color",
                      "encode":{
                        "labels":{
                           "interactive": true,
                             "update":{
                                "fontSize": {"value": 12},
                                "fill": {"value": "black"},
                             },
                           },
                        },
                    },
                ],
             };
             let DonutCard =
                {
                   "$schema": "https://vega.github.io/schema/vega/v5.json",
                   "width": 300,
                   "height": 300,
                   "padding": 5,
                   "title": "Verteilung der Kartentypen",

                   "data": [
                      {
                        "name": "table",
                        "values": [],
                        "transform": [
                           {"type": "formula", "expr": "datum.id + ': ' + datum.field", "as": "tooltip"},
                           {
                             "type": "pie",
                             "field": "field",
                             "startAngle": 0,
                             "endAngle": 6.29,
                              "sort": true
                           }
                        ]
                      }
                   ],

                   "scales": [
                       {
                         "name": "color",
                         "type": "ordinal",
                         "range": ["#5899DA", "#E8743B", "#19A979", "#ED4A7B", "#945ECF"], // SAP Fiori for Web Design Guidelines
                         "domain": ["SMC_KT", "SMC_B", "HBA", "EGK", "KVK"],
                       }
                   ],

                   "marks": [
                       {
                          "type": "arc",
                          "from": {"data": "table"},
                          "encode": {
                            "enter": {
                               "fill": {"scale": "color", "field": "id"},
                               "x": {"signal": "width / 2"},
                               "y": {"signal": "height / 2"},
                               "startAngle": {"field": "startAngle"},
                               "endAngle": {"field": "endAngle"},
                               "innerRadius": {"value": 60},
                               "outerRadius": {"signal": "width / 2"},
                               "cornerRadius": {"value": 0},
                               "tooltip": {"field": "tooltip"}
                            },
                          },
                       },
                   ],
                };

                let DonutCert =
                   {
                     "$schema": "https://vega.github.io/schema/vega/v5.json",
                     "width": 300,
                     "height": 300,
                     "padding": 5,
                     "title": "Validität aller Zertifikate",

                     "data": [
                         {
                           "name": "table",
                           "values": [],
                           "transform": [
                            {"type": "formula", "expr": "datum.id + ': ' + datum.field", "as": "tooltip"},
                            {
                               "type": "pie",
                               "field": "field",
                               "startAngle": 0,
                               "endAngle": 6.29,
                               "sort": true
                            }
                           ]
                         }
                     ],

                     "scales": [
                        {
                           "name": "color",
                           "type": "ordinal",
                           "domain": ["INVALID", "VALID"],
                           "range": ["#dc0d0e", "#3fa45b"]
                        }
                     ],

                     "marks": [
                        {
                          "type": "arc",
                          "from": {"data": "table"},
                          "encode": {
                              "enter": {
                              "fill": {"scale": "color", "field": "id"},
                              "x": {"signal": "width / 2"},
                              "y": {"signal": "height / 2"},
                              "startAngle": {"field": "startAngle"},
                              "endAngle": {"field": "endAngle"},
                              "innerRadius": {"value": 60},
                              "outerRadius": {"signal": "width / 2"},
                              "cornerRadius": {"value": 0},
                              "tooltip": {"field": "tooltip"}
                              },
                           },
                          },
                        ],
                   };

                   let StackCert = {
                        "$schema": "https://vega.github.io/schema/vega/v5.json",
                        "title": "Validität der Zertifikate aller verfübaren Konnektoren",
                        "width": 500,
                        "height": 300,
                        "padding": 5,

                         "data": [
                            {
                              "name": "table",
                              "values": [],
                              "transform": [
                              {
                                 "type": "stack",
                                 "groupby": ["x"],
                                 "sort": {"field": "c"},
                                 "field": "y"
                              }
                              ]
                            }
                         ],

                         "scales": [
                            {
                              "name": "x",
                              "type": "band",
                              "range": "width",
                              "domain": {"data": "table", "field": "x"},
                              "paddingInner": 0.1, // distance between stacks
                              "paddingOuter": 0.1
                            },
                            {
                              "name": "y",
                              "type": "linear",
                              "range": "height",
                              "nice": 1, // x axis displaying a scale of 1
                              "zero": true,
                              "domain": {"data": "table", "field": "y1"},
                              "round": true
                            },
                            {
                              "name": "color",
                              "type": "ordinal",
                              "range": ["#dc0d0e", "#3fa45b"], // SAP Fiori for Web Design Guidelines
                              "domain": ["INVALID", "VALID"],
                            }
                         ],

                         "axes": [
                             {"orient": "bottom", "scale": "x", "zindex": 1, "title":"Konnektoren"},
                             {"orient": "left", "scale": "y", "zindex": 1, "format": ".0f", "title":"Zertifikate", "values": [1, 2, 3, 4,5,6,7,8,9]}
                         ],

                         "marks": [
                              {
                                 "type": "rect",
                                 "from": {"data": "table"},
                                 "encode": {
                                    "enter": {
                                        "x": {"scale": "x", "field": "x"},
                                        "width": {"scale": "x", "band": 1, "offset": -1},
                                        "y": {"scale": "y", "field": "y0"},
                                        "y2": {"scale": "y", "field": "y1"},
                                        "fill": {"scale": "color", "field": "c"}
                                    },
                                    "update": {
                                        "fillOpacity": {"value": 1}
                                    },
                                    "hover": {
                                        "fillOpacity": {"value": 0.5}
                                    }
                                 }
                              }
                         ],
                         "legends": [
                            {
                               "orient": "right",
                               "direction": "vertical",
                               "fill": "color",
                               "encode":{
                                  "labels":{
                                     "interactive": true,
                                     "update":{
                                         "fontSize": {"value": 12},
                                         "fill": {"value": "black"},
                                     },
                                  },
                               },
                            },
                         ],
                   };
          this.oRouter = this.getOwnerComponent().getRouter();
          this._bDescendingSort = false;

          // attaching "manually" to a div __data12. We need a better way to do this
          // ideally this should be loaded into a panel or a card
          function attachGraphToTopLeft() {
              vegaEmbed("#__data13", DonutCard)
              .catch(console.warn);
          }

          function attachGraphToTopRight() {
              vegaEmbed("#__data14", StackCard)
              .catch(console.warn);
          }

          function attachGraphToButtomLeft() {
              vegaEmbed("#__data15", DonutCert)
              .catch(console.warn);
          }

          function attachGraphToButtomRight() {
              vegaEmbed("#__data16", StackCert)
              .catch(console.warn);
          }

          const runtimeConfigModel = new sap.ui.model.odata.v2.ODataModel(
            "../Data.svc",
            true
          );

          runtimeConfigModel.read("/RuntimeConfigs", {
            success: function (oData) {
              const configs = oData.results;
              const StackContent = [];
              const DonutContent = [];
              let anz_SMC_KT = 0;
              let anz_SMC_B = 0;
              let anz_EGK = 0;
              let anz_HBA = 0;
              let anz_KVK = 0;
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
                      for(let i = 0; i < cards.length; i++){
                          if(cards[i].cardType == "SMC_KT"){
                              anz_SMC_KT++;
                          }
                          else if(cards[i].cardType == "SMC_B"){
                              anz_SMC_B++;
                          }
                          else if(cards[i].cardType == "EGK"){
                              anz_EGK++;
                          }
                          else if(cards[i].cardType == "KVK"){
                              anz_KVK++;
                          }
                          else if(cards[i].cardType == "HBA"){
                              anz_HBA++;
                          }
                      }
                      let content = cards.map((card) => ({
                        x: config.Url,
                        y: 1,
                        c: card.cardType,
                      }));
                      StackContent.push(...content);
                    });
                  }
                  if (numResponses === numConfigs) {
                    // Check if all responses have been received
                    DonutContent.push({
                        "id" : "SMC_KT",
                        "field": anz_SMC_KT
                    });
                    DonutContent.push({
                        "id" : "SMC_B",
                        "field": anz_SMC_B
                    });
                    DonutContent.push({
                        "id" : "HBA",
                        "field" : anz_HBA
                    });
                    DonutContent.push({
                        "id" : "EGK",
                        "field" : anz_EGK
                    });
                    DonutContent.push({
                        "id" : "KVK",
                        "field" : anz_KVK
                    });
                    StackCard.data[0].values = StackContent;
                    DonutCard.data[0].values = DonutContent;
                    attachGraphToTopLeft();
                    attachGraphToTopRight();
                  }
                });
              });
            },
            error: function (error) {
              console.log(error);
            },
          });
          runtimeConfigModel.read("/RuntimeConfigs", {
             success: function (oData) {
               const configs = oData.results;
               const CertContent = [];
               const DonutCertContent = [];
               let anz_valid = 0;
               let anz_invalid = 0;
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
                  fetch("/connector/certificate/verifyAll", {
                      headers: getCardsHeaders,
                      }).then((response) => {
                        numResponses++;
                        if (response.ok) {
                          response.json().then((data) => {
                             for(let j = 0; j < data.length; j++){
                                    let cardCerts = data[j].verifyCertificateResponse;
                                    if(cardCerts.length > 0){
                                        for(let q = 0; q < cardCerts.length; q++){
                                            let validity = cardCerts[q].verificationStatus.verificationResult;
                                            if(validity == "VALID"){
                                                CertContent.push({
                                                    "x": config.Url,
                                                    "y": 1,
                                                    "c" : validity
                                                });
                                                anz_valid++;
                                            }
                                            else if(validity == "INVALID"){
                                                CertContent.push({
                                                    "x": config.Url,
                                                    "y":1,
                                                    "c":validity
                                                });
                                                anz_invalid++;
                                            }
                                        }
                                    }
                                }
                              });
                            }
                            if (numResponses === numConfigs) {
                              // Check if all responses have been received
                              DonutCertContent.push({
                                  "id": "VALID",
                                  "field": anz_valid
                              });
                              DonutCertContent.push({
                                  "id": "INVALID",
                                  "field": anz_invalid
                              });
                              StackCert.data[0].values = CertContent;
                              DonutCert.data[0].values = DonutCertContent;
                              attachGraphToButtomLeft();
                              attachGraphToButtomRight();
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
