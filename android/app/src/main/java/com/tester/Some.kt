package com.tester

import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.argyle.Argyle
import com.argyle.ArgyleErrorType
import com.argyle.ArgyleConfig

    class A {
        fun helloWorld(act : AppCompatActivity) {

            val argyle = Argyle.instance
            val pluginKey = "646dc138-5942-4eb6-a9ca-dd01b6d57ae9"
            val apiHost = "https://api-sandbox.develop.argyle.io/v1/"

            val config = ArgyleConfig.Builder()
                    //.dataPartners(arrayOf("uber", "deliv"))
                    .loginWith(pluginKey, apiHost)
                    .setCallbackListener(object : Argyle.ArgyleResultListener {
                        override fun onTokenExpired(handler: (String) -> Unit) {
                            val token = "token"
                            handler(token)
                        }

                        override fun onAccountConnected(accountId: String, workerId: String) {
                            Log.d("Result", "onAccountConnected: accountId: $accountId workerId: $workerId")
                        }

                        override fun onAccountRemoved(accountId: String, workerId: String) {
                            Log.d("Result", "onAccountRemoved: accountId: $accountId workerId: $workerId")
                        }

                        override fun onError(error: ArgyleErrorType) {
                            super.onError(error)
                            Log.d("Result", "onError: error: $error")
                        }

                        override fun onWorkerCreated(workerToken: String, workerId: String) {
                            Log.d("Result", "onWorkerCreated:  workerId: $workerId workerToken: $workerToken")
                        }
                    })

                    .build()
                argyle.init(config)
            Log.d("Kotlin", "Hello World!")
            Argyle.instance.startSDK(act)
        }
    }

