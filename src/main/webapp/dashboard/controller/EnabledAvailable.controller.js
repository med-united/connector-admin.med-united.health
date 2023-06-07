sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/ui/model/json/JSONModel",
	"sap/ui/core/dnd/DragInfo",
	"sap/ui/core/dnd/DropInfo",
	"sap/f/dnd/GridDropInfo",
	"sap/ui/core/library"
], function (Controller, JSONModel, DragInfo, DropInfo, GridDropInfo, coreLibrary) {
	"use strict";

	// shortcut for sap.ui.core.dnd.DropLayout
	var DropLayout = coreLibrary.dnd.DropLayout;

	// shortcut for sap.ui.core.dnd.DropPosition
	var DropPosition = coreLibrary.dnd.DropPosition;

	return Controller.extend("sap.f.ShellBarWithFlexibleColumnLayout.controller.AbstractController", {

    })
});