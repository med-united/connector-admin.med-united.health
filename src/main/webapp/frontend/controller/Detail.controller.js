sap.ui.define([
	"./AbstractDetailController"
], function (AbstractDetailControlle) {
	"use strict";

	return AbstractDetailController.extend("ap.f.ShellBarWithFlexibleColumnLayout.controller.Detail", {
		formatter: Formatter,
		getEntityName: function () {
			return "RuntimeConfig";
		}
	});
}, true);