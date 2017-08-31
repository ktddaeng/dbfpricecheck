# Price Check
Price Check is an Android app that allows users to check the price of items by entering the barcode ID given an DBF inventory. Users in Admin Mode can view the order history of an item and tag items for restocking.

|Main Menu|Information Page|Restock Log|
|---|---|---|
|<img src="/screenshots/Screenshot_20170831-133841.png" width="250px" height="auto">|<img src="/screenshots/Screenshot_20170818-173250.png" width="250px" height="auto">|<img src="/screenshots/Screenshot_20170831-140956.png" width="250px" height="auto">|

## Motivation
This app was made as a management tool for a local gift shop & convenience store. The store owners use a very old FoxPro inventory management system, so marking items for restocking and checking the price of an item is a time consuming process.

## What's New
* Added a Restocking Log: Mark items as a favorite and put them on a log for restocking. The log can be exported for printing.

## Prerequisites
* Android SDK 25 (Minimum SDK 19)
* Android Build Tools v25.3.1
* Android Support Repository

## Getting Started
This project can be build using the "gradle build" feature in Android Studio.

### Syncing the Database
This app gets information from databases with a DBF extension. The columns must be simplified to Item ID, Description, then Retail Price. A second database file can be used to assemble a table for order history. Both files must be placed in the Downloads Directory for the app to find and parse. After placing the file in the appropriate location, press Sync Database and wait a few seconds for a message notifying that the database has been synced.

*Troubleshooting: If no loading Dialog appears, please restart the app.*

### Scanning Items
After selecting Search & Scan in the main menu, you can choose to enter in a barcode manually, or scan the item using your camera. This app uses the [ZXing Barcode Scanning Library](https://github.com/zxing/zxing). Users can then see a page containing the description and price of an item as stated in the inventory.

*Troubleshooting: If the item could not be found in the database by camera scanning or manual input, it could be because the item is registered with 10 digits of its ID rather than the full 12 digits. Try manually inputting the barcode ID again, this time without the first or last digits. And if that doesn't work, it could be because the inventory is out of date, or the item truly does not exist in the database.*

|Manual Input|Information Page|
|---|---|
|<img src="/screenshots/Screenshot_20170831-133851.png" width="250px" height="auto">|<img src="/screenshots/Screenshot_20170818-173250.png" width="250px" height="auto">|

## Admin Mode
Users can use Admin Mode through the context menu in the main menu, but only if they know the password. This mode is only to be used by those who manage the store inventory directly.

|The Context Menu with Admin Mode|Information Page (Regular)|Information Page (Admin)|
|---|---|---|
|<img src="/screenshots/Screenshot_20170831-133851.png" width="250px" height="auto">|<img src="/screenshots/Screenshot_20170818-173250.png" width="250px" height="auto">|<img src="/screenshots/Screenshot_20170831-134049.png" width="250px" height="auto">|

A user with Admin priveleges will notice a change in the Information Page after unlocking Admin Mode. Admins can view the order history of an item, usually seeing the last five shipments and unit prices. In the top right corner of the information page is a button that allows admins to mark items as favorites, meaning that the item will be saved onto the Restock Log.

### Restock Log (Admin Mode Restricted)
This page compiles the full list of items that an admin has favorited for restocking. This list is automatically ordered by Item ID for easy navigation. Clicking on an item on the list will bring up the item's information page, the same page as when such an item is scanned in. Items can be easily deleted off the list by swiping to the right, and the list can be refreshed by swiping down.

Admins can export the database using the export feature in the context menu in the top right corner. The app by default exports a CSV file directly to the designated Downloads folder of the Android device. Using [OpenCSV](http://opencsv.sourceforge.net/), the CSV file will be listed according to the last sorting query requested, and will be labeled with a timestamp to avoid accidental overwriting.

|Restock Log|
|---|
|<img src="/screenshots/Screenshot_20170831-140956.png" width="250px" height="auto">|