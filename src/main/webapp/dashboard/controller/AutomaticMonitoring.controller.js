sap.ui.define(
  [
    "sap/ui/core/mvc/Controller",
    "sap/ui/model/json/JSONModel",
    "sap/ui/core/dnd/DragInfo",
    "sap/ui/core/dnd/DropInfo",
    "sap/f/dnd/GridDropInfo",
    "sap/ui/core/library",
  ],
  function (
    Controller,
    JSONModel,
    DragInfo,
    DropInfo,
    GridDropInfo,
    coreLibrary
  ) {
    "use strict";

    // shortcut for sap.ui.core.dnd.DropLayout
    var DropLayout = coreLibrary.dnd.DropLayout;

    // shortcut for sap.ui.core.dnd.DropPosition
    var DropPosition = coreLibrary.dnd.DropPosition;

    return Controller.extend(
      "sap.f.ShellBarWithFlexibleColumnLayout.controller.Detail",
      {
        onInit: function () {
          this.initData();
          this.attachDragAndDrop();
          this.byId("grid0").addStyleClass("forget");
        },

        initData: function () {
          this.byId("grid1").setModel(new JSONModel([]));

          this.byId("grid2").setModel(
            new JSONModel([
              {
                title: "Aktualisierung Konnektor",
                rows: 2,
                columns: 2,
                id: "actConn",
              },
              {
                title: "Aktualisierung Kartenterminals",
                rows: 2,
                columns: 2,
                id: "actKT",
              },
              {
                title: "\u00DCberpr\u00FCfung Onlinestatus TI",
                rows: 2,
                columns: 2,
                id: "tistat",
              },
            ])
          );
        },

        attachDragAndDrop: function () {
          var oList = this.byId("grid0");
          oList.addDragDropConfig(
            new DragInfo({
              sourceAggregation: "items",
            })
          );

          oList.addDragDropConfig(
            new DropInfo({
              targetAggregation: "items",
              dropPosition: DropPosition.Between,
              dropLayout: DropLayout.Vertical,
              dropIndicatorSize: this.onDropIndicatorSize.bind(this),
              drop: this.onDrop1.bind(this),
            })
          );

          var oGrid = this.byId("grid1");
          oGrid.addDragDropConfig(
            new DragInfo({
              sourceAggregation: "items",
            })
          );

          oGrid.addDragDropConfig(
            new GridDropInfo({
              targetAggregation: "items",
              dropPosition: DropPosition.Between,
              dropLayout: DropLayout.Horizontal,
              dropIndicatorSize: this.onDropIndicatorSize.bind(this),
              drop: this.onDrop1.bind(this),
            })
          );

          var oGrid2 = this.byId("grid2");
          oGrid2.addDragDropConfig(
            new DragInfo({
              sourceAggregation: "items",
            })
          );

          oGrid2.addDragDropConfig(
            new GridDropInfo({
              targetAggregation: "items",
              dropPosition: DropPosition.Between,
              dropLayout: DropLayout.Horizontal,
              dropIndicatorSize: this.onDropIndicatorSize.bind(this),
              drop: this.onDrop2.bind(this),
            })
          );
        },

        onDropIndicatorSize: function (oDraggedControl) {
          var oBindingContext = oDraggedControl.getBindingContext(),
            oData = oBindingContext
              .getModel()
              .getProperty(oBindingContext.getPath());

          if (oDraggedControl.isA("sap.m.StandardListItem")) {
            return {
              rows: oData.rows,
              columns: oData.columns,
            };
          }
        },

        onDrop1: function (oInfo) {
          var oDragged = oInfo.getParameter("draggedControl"),
            oDropped = oInfo.getParameter("droppedControl"),
            sInsertPosition = oInfo.getParameter("dropPosition"),
            oDragContainer = oDragged.getParent(),
            oDropContainer = oInfo.getSource().getParent(),
            oDragModel = oDragContainer.getModel(),
            oDropModel = oDropContainer.getModel(),
            oDragModelData = oDragModel.getData(),
            oDropModelData = oDropModel.getData(),
            iDragPosition = oDragContainer.indexOfItem(oDragged),
            droppedCardId = "",
            iDropPosition = oDropContainer.indexOfItem(oDropped);

          // remove the item
          var oItem = oDragModelData[iDragPosition];
          oDragModelData.splice(iDragPosition, 1);

          if (oDragModel === oDropModel && iDragPosition < iDropPosition) {
            iDropPosition--;
          }

          if (sInsertPosition === "After") {
            iDropPosition++;
          }

          // insert the control in target aggregation
          oDropModelData.splice(iDropPosition, 0, oItem);

          if (oDragModel !== oDropModel) {
            oDragModel.setData(oDragModelData);
            oDropModel.setData(oDropModelData);
          } else {
            oDropModel.setData(oDropModelData);
          }

          //console.log(oDropModel.getData()[0].id);
          droppedCardId =
            oDropModel.getData()[oDropModel.getData().length - 1].id + "_on";

          console.log(droppedCardId);

          this.byId("grid1").focusItem(iDropPosition);
        },

        onDrop2: function (oInfo) {
          var oDragged = oInfo.getParameter("draggedControl"),
            oDropped = oInfo.getParameter("droppedControl"),
            sInsertPosition = oInfo.getParameter("dropPosition"),
            oDragContainer = oDragged.getParent(),
            oDropContainer = oInfo.getSource().getParent(),
            oDragModel = oDragContainer.getModel(),
            oDropModel = oDropContainer.getModel(),
            oDragModelData = oDragModel.getData(),
            oDropModelData = oDropModel.getData(),
            iDragPosition = oDragContainer.indexOfItem(oDragged),
            droppedCardId = "",
            iDropPosition = oDropContainer.indexOfItem(oDropped);

          // remove the item
          var oItem = oDragModelData[iDragPosition];
          oDragModelData.splice(iDragPosition, 1);

          if (oDragModel === oDropModel && iDragPosition < iDropPosition) {
            iDropPosition--;
          }

          if (sInsertPosition === "After") {
            iDropPosition++;
          }

          // insert the control in target aggregation
          oDropModelData.splice(iDropPosition, 0, oItem);

          if (oDragModel !== oDropModel) {
            oDragModel.setData(oDragModelData);
            oDropModel.setData(oDropModelData);
          } else {
            oDropModel.setData(oDropModelData);
          }

          //console.log(oDropModel.getData()[0].id);
          droppedCardId =
            oDropModel.getData()[oDropModel.getData().length - 1].id + "_off";

          console.log(droppedCardId);

          this.byId("grid1").focusItem(iDropPosition);
        },
      }
    );
  }
);
