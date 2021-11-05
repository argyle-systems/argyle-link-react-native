import React, { useEffect } from 'react'
import { StyleSheet, Text, View, TouchableOpacity } from 'react-native'
import AsyncStorage from '@react-native-community/async-storage'
import ArgyleSdk from '@argyleio/argyle-plugin-react-native'

const API_HOST = 'https://api-sandbox.argyle.io/v1'
const LINK_KEY = 'YOUR_LINK_KEY' // get your key here: https://console.argyle.com/api-keys

const TOKEN_STORAGE_KEY = 'userKey'

const PAY_DISTRIBUTION_ON = false
const PD_CONFIG = 'YOUR_PD_CONFIG'

const storeUserToken = async token => {
  try {
    await AsyncStorage.setItem(TOKEN_STORAGE_KEY, token)
  } catch (e) {
    // saving error
    console.log('error saving value', e)
  }
}

const getSavedUserToken = async () => {
  try {
    const value = await AsyncStorage.getItem(TOKEN_STORAGE_KEY)
    return value
  } catch (e) {
    // error reading value
    console.log('error reading value', e)
    return null
  }
}

export const LinkExample = () => {
  useEffect(() => {
    const setup = async () => {
      const savedToken = await getSavedUserToken()
      const userToken = savedToken !== null ? savedToken : ''

      // Tip: always check if userToken is valid and generate a new one if needed before initialising Link
      // This way you won't need to handle it within onTokenExpired callback
      // https://argyle.com/docs/argyle-link/user-tokens
      ArgyleSdk.loginWith(LINK_KEY, API_HOST, userToken)
      // ArgyleSdk.customizationId('YOUR_CUSTOMIZATION_ID')

      // Tip: use this to define a subset of link items to use
      // ArgyleSdk.linkItems(["uber"])

      // Set this constant to true and define your PD config to try out Pay Distribution Update flow
      //  https://argyle.com/docs/pay-distributions-guide/link-integration
      if (PAY_DISTRIBUTION_ON) {
        ArgyleSdk.payDistributionConfig(PD_CONFIG)
        ArgyleSdk.payDistributionUpdateFlow(true)
        ArgyleSdk.payDistributionItemsOnly(true)
        ArgyleSdk.onPayDistributionSuccess(res => console.log('onPayDistributionSuccess', res))
        ArgyleSdk.onPayDistributionError(res => console.log('onPayDistributionError', res))
      }

      ArgyleSdk.onUserCreated(res => {
        console.log('onUserCreated', res)
        storeUserToken(res.token)
      })
      ArgyleSdk.onAccountCreated(res => console.log('onAccountCreated', res))
      ArgyleSdk.onAccountConnected(res => console.log('onAccountConnected', res))
      ArgyleSdk.onAccountUpdated(res => console.log('onAccountUpdated', res))
      ArgyleSdk.onAccountRemoved(res => console.log('onAccountRemoved', res))
      ArgyleSdk.onAccountError(res => console.log('onAccountError', res))
      ArgyleSdk.onError(error => console.log('onError', error))
      ArgyleSdk.onTokenExpired(res => {
        console.log('onTokenExpired', res)
        setTimeout(() => {
          // Simulated timeout before updating the token
          ArgyleSdk.updateToken('YOUR_NEW_TOKEN')
        }, 300)
      })
      ArgyleSdk.onClose(() => console.log('onClose'))
      ArgyleSdk.onUIEvent(res => console.log('onUIEvent', JSON.stringify(res, null, 2)))
    }

    setup()

    return () => {
      // any cleanup you may need to do goes here
    }
  }, [])

  const startNew = () => {
    ArgyleSdk.updateToken('')
    ArgyleSdk.start()
  }

  const startExisting = async () => {
    ArgyleSdk.start()
  }

  return (
    <View style={styles.container}>
      <Text style={styles.welcome}>📱️ Argyle Link SDK️</Text>
      <TouchableOpacity onPress={startNew}>
        <Text style={styles.welcome}>Start new</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={startExisting}>
        <Text style={styles.welcome}>Start existing</Text>
      </TouchableOpacity>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF'
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5
  }
})
