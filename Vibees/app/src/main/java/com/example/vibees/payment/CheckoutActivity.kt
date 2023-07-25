package com.example.vibees.payment

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vibees.Api.VibeesApi
import com.example.vibees.MainActivity
import com.example.vibees.Models.Party
import com.example.vibees.Models.PaymentMetaData
import com.example.vibees.Models.ResponseMessage
import com.example.vibees.Models.Transaction
import com.example.vibees.screens.bottombar.BottomBar
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult

class CheckoutActivity : AppCompatActivity() {
    lateinit var paymentSheet: PaymentSheet
    lateinit var customerConfig: PaymentSheet.CustomerConfiguration
    lateinit var paymentIntentClientSecret: String
    val vibeesApi = VibeesApi()
    // Successful request
    val successfn: (PaymentMetaData) -> Unit = { response ->
        Log.d("TAG", "success")
        paymentIntentClientSecret = response.paymentIntent
        customerConfig = PaymentSheet.CustomerConfiguration(
            response.customer,
            response.ephemeralKey
        )
        val publishableKey = response.publishableKey
        PaymentConfiguration.init(this, publishableKey)
        presentPaymentSheet()
    }

    // failed request
    val failurefn: (Throwable) -> Unit = { t ->
        Log.d("TAG", "FAILURE")
        Log.d("TAG", t.printStackTrace().toString())
        showToast("Something went wrong! Coundn't register")
    }

    // Successful request
    val attend: (ResponseMessage) -> Unit = { response ->
        Log.d("TAG", "${response.message}")
        showToast("Successfully Completed")
    }


    var userId = ""
    var userName = ""
    var partyId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        val amount: Double = intent.getDoubleExtra("entryFee", 0.0) * 100
        userId = intent.getStringExtra("userID")!!
        Log.d(ContentValues.TAG, "UserID: ${userId}")
        partyId = intent.getStringExtra("partyID")!!
        userName = intent.getStringExtra("userName")!!
        Log.d(ContentValues.TAG, "UserName: ${userName}")
        if (amount <= 0.5) {
            val callResponse = vibeesApi.registerUserForParty(attend, failurefn, partyId)
            navigateToMainActivity()
        }
        else {
            val transaction = Transaction(amount.toInt())
            val response = vibeesApi.getPaymentInfo(transaction, successfn, failurefn)
        }
    }

    fun presentPaymentSheet() {
        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret,
            PaymentSheet.Configuration(
                merchantDisplayName = "My merchant name",
                customer = customerConfig,
                defaultBillingDetails = PaymentSheet.BillingDetails(
                    address = PaymentSheet.Address(country = "Canada")
                ),
                // Set `allowsDelayedPaymentMethods` to true if your business
                // can handle payment methods that complete payment after a delay, like SEPA Debit and Sofort.
                allowsDelayedPaymentMethods = false
            )
        )
    }

    fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when(paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                Log.d("TAG", "CANCELLED")
                showToast("Transaction Cancelled")
            }
            is PaymentSheetResult.Failed -> {
                Log.d("TAG","Error: ${paymentSheetResult.error}")
                showToast("Transaction Failed")
            }
            is PaymentSheetResult.Completed -> {
                // Display for example, an order confirmation screen
                Log.d("TAG", "TRANSACTION COMPLETED")
                showToast("Transaction Completed")

                // user will attend party
                val callResponse = vibeesApi.registerUserForParty(attend, failurefn, partyId)
            }
        }

        navigateToMainActivity()
    }

    fun navigateToMainActivity() {
        val intentNew = Intent(this, MainActivity::class.java)
        intentNew.putExtra("navigateToHome", true)
        intentNew.putExtra("userID", userId)
        intentNew.putExtra("userName", userName)

        startActivity(intentNew)
        finish()
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }
}