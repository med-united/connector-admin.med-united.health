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
          let graphJSON = {
            $schema: "https://vega.github.io/schema/vega/v5.json",
            title: "Gesteckte Karten aller verfügbaren Konnektoren",
            width: 1000,
            height: 300,
            padding: 5,

            data: [
              {
                name: "table",
                values: [],
                transform: [
                  {
                    type: "stack",
                    groupby: ["x"],
                    sort: { field: "c" },
                    field: "y",
                  },
                ],
              },
            ],

            scales: [
              {
                name: "x",
                type: "band",
                range: "width",
                domain: { data: "table", field: "x" },
                paddingInner: 0.1, // distance between stacks
                paddingOuter: 0.1,
              },
              {
                name: "y",
                type: "linear",
                range: "height",
                nice: 1, // x axis displaying a scale of 1
                zero: true,
                domain: { data: "table", field: "y1" },
                round: true,
              },
              {
                name: "color",
                type: "ordinal",
                range: ["#5899DA", "#E8743B", "#19A979", "#ED4A7B", "#945ECF"], // SAP Fiori for Web Design Guidelines
                domain: ["SMC_KT", "SMC_B", "HBA", "EGK", "KVK"],
              },
            ],

            axes: [
              { orient: "bottom", scale: "x", zindex: 1, title: "Konnektoren" },
              {
                orient: "left",
                scale: "y",
                zindex: 1,
                format: ".0f",
                title: "Gesteckte Karten",
              },
            ],

            marks: [
              {
                type: "rect",
                from: { data: "table" },
                encode: {
                  enter: {
                    x: { scale: "x", field: "x" },
                    width: { scale: "x", band: 1, offset: -1 },
                    y: { scale: "y", field: "y0" },
                    y2: { scale: "y", field: "y1" },
                    fill: { scale: "color", field: "c" },
                  },
                  update: {
                    fillOpacity: { value: 1 },
                  },
                  hover: {
                    fillOpacity: { value: 0.5 },
                  },
                },
              },
            ],
            legends: [
              {
                orient: "right",
                direction: "vertical",
                fill: "color",
                encode: {
                  labels: {
                    interactive: true,
                    update: {
                      fontSize: { value: 12 },
                      fill: { value: "black" },
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
          function attachGraphToElement() {
            vegaEmbed("#__data16", graphJSON)
              //.then(result => console.log(result))
              .catch(console.warn);
          }

          const runtimeConfigModel = new sap.ui.model.odata.v2.ODataModel(
            "../Data.svc",
            true
          );

          runtimeConfigModel.read("/RuntimeConfigs", {
            success: function (oData) {
              const configs = oData.results;
              const allContent = [];
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
                  response.json().then((data) => {
                    let cards = data.cards.card;
                    let content = cards.map((card) => ({
                      x: config.Url,
                      y: 1,
                      c: card.cardType,
                    }));
                    allContent.push(...content); // Accumulate data from this response
                    numResponses++;
                    if (numResponses === numConfigs) {
                      // Check if all responses have been received
                      graphJSON.data[0].values = allContent; // Update graph with all data
                      attachGraphToElement();
                    }
                  });
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
