# Get Correct Card Handle Property when button was pressed in table

```
onItemPress: function(oEven) {
    let sCardHandle = oEvent.getSource().getBindingContext("Cards").getProperty("cardHandle");
    ...
}
```