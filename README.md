# How to run
## 1. Install android studio at - https://developer.android.com/studio
## 2. Ensure backend server is running. See - https://github.com/aarushth/InventoryManagerBackend
## 3. Make sure your phone has developer options and USB debugging enabled - https://developer.android.com/studio/debug/dev-options
## 4. open up cmd and run 'ipconfig'
## 5. copy the IPv4 address that shows up
## 6. Open the project up in android studio and open up the LoginActivity.kt file at '/app/kotlin+java/com.leopardseal.inventorymanager/'
## 7. in the 'handleSignIn' function, find client.get("http://192.168.68.77:8080/signIn"), and change the ip adress to the one you copied. Make sure you don't delete the :8080/signIn at the endand the http:// at the start
## 8. Plug your phone into your pc/laptop and its name should appear at the top of android studio. Click run to run the app.
## 9. When you try to log in it should say 'user not found in system' as your email isn't in the server yet. You can add it into the InventoryManagerBackend project in the Controller class's constructor the same way my email is added. Make sure to rerun the server once changes are made.
