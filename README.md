### Introduction
This repo contains the following:
* A simple NFT demo site used to create a transaction on crypto card - under `nft` folder. 
* Avalanche Point of Sale (PoS) Android app to allow registered merchant to use this app to accept payments via our crypto card - under `android_pos` folder.

NOTE: The demo above only supports MetaMask wallet for now.

### Simple NFT Demo Site
This demo supports the MetaMask wallet Chrome extension.

For this demo purpose, we allow any wallet address to transact although in the actual system, only approved cardholders' wallets are accepted.

### Avalanche Point of Sale (POS) Android App
This demo supports the MetaMask wallet mobile app.

You may install the `Merchant.apk` file under `android_pos` folder on your Android phone (for Android version 4.5 and above). After installation, you'd need to enter your merchant details. But you'd need to sign up as a merchant before being able to use this app. But since this is a just a demo, the merchant signing-up process is not yet available. So, you may use the sample settings below:
*   Merchant name: MyPizza
*   Merchant ID: 52
*   Currency: USDC
*   API Key: 1234

After entering the settings, you'd be able to enter the amount you wish a customer to pay and the app will generate the corresponding QR code. Scan the QR with the MetaMask QR reader on another phone and it should redirect you to the MetaMask in-app browser where you can confirm the payment by making a signature. The signature is verified at the backend.



 
