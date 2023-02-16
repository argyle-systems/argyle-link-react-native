import React from 'react'
import { SafeAreaView, StyleSheet, Text, TouchableOpacity } from 'react-native'
import { ArgyleLink } from '@argyleio/argyle-plugin-react-native'

import { Colors } from 'react-native/Libraries/NewAppScreen'

export const LinkExample = (): JSX.Element => {
  const callbackFired = (key: string, response: any = {}) => {
    console.log('callback fired', key, response)
  }

  const start = () => {
    const config = {
      linkKey: 'LINK_KEY',
      userToken: 'USER_TOKEN',
      sandbox: true,
      onAccountCreated: response => callbackFired('onAccountCreated', response),
      onAccountConnected: response => callbackFired('onAccountConnected', response),
      onAccountRemoved: response => callbackFired('onAccountRemoved', response),
      onAccountError: response => callbackFired('onAccountError', response),
      onDDSSuccess: response => callbackFired('onDDSSuccess', response),
      onDDSError: response => callbackFired('onDDSError', response),
      onClose: () => callbackFired('onClose'),
      onExitIntroClicked: () => callbackFired('onExitIntroClicked'),
      onError: response => callbackFired('onError', response),
      onTokenExpired: updateToken => {
        // updateToken('<token>')
        callbackFired('onTokenExpired', updateToken)
      },
      onUIEvent: response => callbackFired('onUIEvent', response),
      onFormSubmitted: response => callbackFired('onFormSubmitted', response),
      onDocumentsSubmitted: response => callbackFired('onDocumentsSubmitted', response)
    }

    ArgyleLink.start(config)
  }

  return (
    <SafeAreaView style={styles.background}>
      <Text style={styles.welcome}>📱️ Argyle Link React Native SDK️</Text>
      <TouchableOpacity testID="start" onPress={start}>
        <Text style={styles.welcome}>Start</Text>
      </TouchableOpacity>
    </SafeAreaView>
  )
}

const styles = StyleSheet.create({
  background: {
    backgroundColor: Colors.lighter
  },
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
  },
  callbackContainer: {
    width: '100%',
    backgroundColor: 'white',
    padding: 20,
    height: 300
  },
  callbacks: {
    textAlign: 'left',
    color: '#333333',
    marginTop: 5
  }
})
