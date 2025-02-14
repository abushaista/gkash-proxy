package com.kryptopos.kposproxy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gkash.gkashsoftpossdk.GkashSoftPOSSDK
import com.gkash.gkashsoftpossdk.GkashSoftPOSSDK.GkashStatusCallback
import com.gkash.gkashsoftpossdk.model.GkashSDKConfig
import com.gkash.gkashsoftpossdk.model.PaymentRequestDto
import com.gkash.gkashsoftpossdk.model.TransactionDetails
import com.kryptopos.kposproxy.ui.theme.KposproxyTheme
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {
    private lateinit var gkashSoftPOSSDK: GkashSoftPOSSDK
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KposproxyTheme {
                Column (
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoadingAnimation()
                }

            }
        }
        // ATTENTION: This was auto-generated to handle app links.
        val appLinkIntent: Intent = intent
        val appLinkAction: String? = appLinkIntent.action
        val appLinkData: Uri? = appLinkIntent.data
        val pType = intent.getStringExtra("paymentType")
        val sessionId = intent.getStringExtra("sessionId")
        val amount = intent.getIntExtra("amount", 0)
        gkashSoftPOSSDK = GkashSoftPOSSDK.getInstance(this@MainActivity)
        val gkashSDKConfig = GkashSDKConfig().setUsername("M161-TD-51316")
            .setPassword("Gkash@1234")
            .setTestingEnvironment(true)
        gkashSDKConfig.setLoadCertFromAsset(true);
        gkashSoftPOSSDK.init(gkashSDKConfig, object : GkashStatusCallback {
            override fun TransactionResult(details: TransactionDetails) {
                //TODO("Not yet implemented")
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://kryptopos-262315.web.app/?sessionId=${details.cartID}&status=${details.status}"))
                startActivity(browserIntent)
                finishAffinity()
            }

            override fun SocketStatus(connectivityCallback: GkashSoftPOSSDK.SocketConnectivityCallback?) {
                if(connectivityCallback == GkashSoftPOSSDK.SocketConnectivityCallback.ONLINE){
                    Log.d("ip address", gkashSoftPOSSDK.ipAddress)
                }
            }

            override fun TransactionEvent(transactionEventCallback: GkashSoftPOSSDK.TransactionEventCallback?) {
                TODO("Not yet implemented")
            }

            override fun QueryTransactionResult(details: TransactionDetails?) {
                TODO("Not yet implemented")
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://kryptopos-262315.web.app/?sessionId=${details?.cartID}&status=${details?.status}"))
                startActivity(browserIntent)
                finishAffinity()
            }
        })
        val requestDto = PaymentRequestDto()
        requestDto.amount = amount.toString()
        requestDto.paymentType = GkashSoftPOSSDK.PaymentType.valueOf(pType.toString())
        requestDto.referenceNo = sessionId;
        requestDto.isPreAuth = false
        requestDto.email = "YourEmail@email.com"
        requestDto.mobileNo = "0123456789"
        GlobalScope.launch(Dispatchers.Main){
            delay(100)
            try {
                gkashSoftPOSSDK.requestPayment(requestDto)
                Log.d("request payment","send to terminal")
            } catch (e: Exception){
                Log.e("request payment",e.message.toString())
            }
            delay(5000)
            try {
                gkashSoftPOSSDK.queryTransactionStatus(sessionId);
            } catch (e: Exception){
                Log.e("request payment",e.message.toString())
            }

        }

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KposproxyTheme {
        Greeting("Android")
    }
}