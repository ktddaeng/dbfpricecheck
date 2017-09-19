# Price Check
Price Check is an Android app that allows users to check the price of items by entering the barcode ID given an DBF inventory. Users in Admin Mode can view the order history of an item and tag items for restocking.

|Main Menu|Fetching an Item|Restock Log|
|---|---|---|
|<img src="/screenshots/Screenshot_20170918-212258.png" width="250px" height="auto">|<img src="/screenshots/ezgif-1-1b83deff8e.gif" width="240px" height="auto">|<img src="/screenshots/Screenshot_20170918-213648.png" width="250px" height="auto">|

## Motivation
This app was made as a management tool for a local gift shop & convenience store. The store owners use a very old FoxPro inventory management system, so marking items for restocking and checking the price of an item is a time consuming process.

## What's New
* 2.0.0.0 Bluetooth Scanner Support
* 2.1.0.0 Enhanced Restock Log to display most recent order (9/17/17)
* 3.0.0.0 New Item Feature Support (9/19/17)

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

A Bluetooth Scanner as HID Input can be used to scan items as well. By selecting Manual Search, the app will tell the user if it has detected the scanner. The app will react immediately to the scanner's input without requiring the user to select OK in the dialog.

*Troubleshooting: If the item could not be found in the database by camera scanning or manual input, it could be because the item is registered with 10 digits of its ID rather than the full 12 digits. Try manually inputting the barcode ID again, this time without the first or last digits. And if that doesn't work, it could be because the inventory is out of date, or the item truly does not exist in the database.*

|Manual Input|Information Page|
|---|---|
|<img src="/screenshots/Screenshot_20170831-133851.png" width="250px" height="auto">|<img src="/screenshots/Screenshot_20170818-173250.png" width="250px" height="auto">|<img src="/screenshots/Screenshot_20170918-212441.png" width="250px" height="auto"|

## Admin Mode
Users can use Admin Mode through the context menu in the main menu, but only if they know the password. This mode is only to be used by those who manage the store inventory directly.

|The Context Menu with Admin Mode|Information Page (Regular)|Information Page (Admin)|
|---|---|---|
|<img src="/screenshots/Screenshot_20170831-141020.png" width="250px" height="auto">|<img src="/screenshots/Screenshot_20170818-173250.png" width="250px" height="auto">|<img src="/screenshots/Screenshot_20170918-212441.png" width="250px" height="auto">|

A user with Admin privileges will notice a change in the Information Page after unlocking Admin Mode. Admins can view the order history of an item, usually seeing the last five shipments and unit prices. In the top right corner of the information page is a button that allows admins to mark items as favorites, meaning that the item will be saved onto the Restock Log.

### Restock Log (Admin Mode Restricted)
This page compiles the full list of items that an admin has favorited for restocking. This list is automatically ordered by Item ID for easy navigation. Clicking on an item on the list will bring up the item's information page, the same page as when such an item is scanned in. Items can be easily deleted off the list by swiping to the right, and the list can be refreshed by swiping down.

Admins can export the database using the export feature in the context menu in the top right corner. The app by default exports a CSV file directly to the designated Downloads folder of the Android device. Using [OpenCSV](http://opencsv.sourceforge.net/), the CSV file will be listed according to the last sorting query requested, and will be labeled with a timestamp to avoid accidental overwriting.

|Restock Log|Deleting a Restock Item|
|---|---|
|<img src="/screenshots/Screenshot_20170918-213648.png" width="250px" height="auto">|<img src="/screenshots/ezgif-1-0f9bb02d78.gif" width="240px" height="auto">|

### Items to Add (Admin Mode Restricted)
This recently added feature allows a user with admin priveleges to add items to the database. Because the app's database is purely local, items added to the database are added onto a separate table. When a new item is scanned, the user will be prompted to choose whether or not the item should be added (Figure 1). Then the user will be asked to fill in the remaining required description and price (FIgure 2).

|Figure 1|Figure 2|
|---|---|
|<img src="/screenshots/Screenshot_20170918-212242.png" width="250px" height="auto">|<img src="/screenshots/Screenshot_20170918-212246.png" width="250px" height="auto">|

The exclusive New Items list can be accessed through the "Items to Add" option in the main menu. Like the Restock Log, an item's information page can be accessed by touching the item, and an item can be deleted by swiping the item to the left. The information page will be colored red instead of green to denote that the item has not been registered in the main database. This table can also be exported into a CSV file using the menu in the top right corner. The created CSV file will be saved in the Downloads file and can be used to import into the DBF program of the user's choice.

|Items to Add List|Deleting a New Item|Item Information Page (New Item)|
|---|---|
|<img src="/screenshots/Screenshot_20170918-213648.png" width="250px" height="auto">|<img src="/screenshots/ezgif-1-255da2d71b.gif" width="250px" height="auto">|<img src="/screenshots/Screenshot_20170918-212202.png" width="250px" height="auto">|
