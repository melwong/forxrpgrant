### Introduction
This repo contains the following:
* A simple demo site interface to demonstrate how someone who holds our credit line could make a remittance via our payment gateway - under `remittance` folder. This code is using MetaMask wallet because we were told my XRP grant team that we can submit code based on MetaMask as we don't have sufficient time to build a demo on XRP wallet. We only thought of submitting to your grant three days ago. 
* Point of Sale (PoS) Android app to allow registered merchant to use this app to accept remittance requests - under `android_pos` folder. This is useful in the future for people to easily walk to our merchant to send money home abroad. This is the common use case among migrant workers and we want to continue to faciliate this.

*NOTE: To learn more about our XRP development capability, please view our other XRP project -> https://github.com/melwong/ngxrp*

### Point of Sale (POS) Android App
This demo supports the MetaMask wallet mobile app. Here's a demo video - https://youtu.be/Ksj-Vgp1zRk?t=106

You may install the `Merchant.apk` file under `android_pos` folder on your Android phone (for Android version 4.5 and above). After installation, you'd need to enter your merchant details. But you'd need to sign up as a merchant before being able to use this app. But since this is a just a demo, the merchant signing-up process is not yet available. So, you may use the sample settings below:
*   Merchant name: Eastasia Remittance Service
*   Merchant ID: 52
*   Currency: USDC
*   API Key: 1234

After entering the settings, you'd be able to enter the amount you wish a customer to pay and the app will generate the corresponding QR code. Scan the QR with the MetaMask QR reader on another phone and it should redirect you to the MetaMask in-app browser where you can confirm the payment by making a signature. The signature is verified at the backend.

