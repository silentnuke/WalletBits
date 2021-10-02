# Wallet app for Bits Interview

### Summary

This sample is written in Kotlin has 3 screens (Cards List, Add New Card, View Eisting Card) and uses the following Components:
 - ViewModel
 - LiveData
 - Data Binding
 - Navigation
 - Room
 - [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) for dependency injection
 - [Card Form from Bintree](https://github.com/braintree/android-card-form) for some basic card validation

### Testing

Have both UI and Unit tests.

UI tests use Hilt to provide their test versions.

This is done by creating a `CustomTestRunner` that uses an `Application` configured with Hilt. As
per the [Hilt testing documentation](https://developer.android.com/training/dependency-injection/hilt-android),
`@HiltAndroidTest` will automatically create the right Hilt components for tests.

## Screenshots

<p align="center">
  <img src="screenshots/device-2021-10-02-182324.png" width="250">
  <img src="screenshots/device-2021-10-02-181713.png" width="250">
</p>

<p align="center">
  <img src="screenshots/device-2021-10-02-182208.png" width="250">
</p>

<p align="center">
  <img src="screenshots/device-2021-10-02-182223.png" width="250">
  <img src="screenshots/device-2021-10-02-182257.png" width="250">
</p>