import React from 'react';
import {SafeAreaView, StyleSheet, Text, TouchableOpacity} from 'react-native';
import {
  AccountCallbackPayload,
  ArgyleLink,
  FormCallbackPayload,
  Language,
} from '@argyleio/argyle-plugin-react-native';

import {Colors} from 'react-native/Libraries/NewAppScreen';

export const LinkExample = (): JSX.Element => {
  const callbackFired = (key: string, response: any = {}) => {
    console.log('callback fired', key, response);
  };

  const start = () => {
    const config = {
      userToken: 'USER_TOKEN',
      sandbox: true,
      language: Language.EN,
      onAccountCreated: (response: AccountCallbackPayload) =>
        callbackFired('onAccountCreated', response),
      onAccountConnected: (response: AccountCallbackPayload) =>
        callbackFired('onAccountConnected', response),
      onAccountRemoved: (response: AccountCallbackPayload) =>
        callbackFired('onAccountRemoved', response),
      onAccountError: (response: AccountCallbackPayload) =>
        callbackFired('onAccountError', response),
      onDDSSuccess: (response: AccountCallbackPayload) =>
        callbackFired('onDDSSuccess', response),
      onDDSError: (response: AccountCallbackPayload) =>
        callbackFired('onDDSError', response),
      onClose: () => callbackFired('onClose'),
      onExitIntroClicked: () => callbackFired('onExitIntroClicked'),
      onError: (response: any) => callbackFired('onError', response),
      onTokenExpired: (updateToken: any) => {
        // updateToken('<token>')
        callbackFired('onTokenExpired', updateToken);
      },
      onUIEvent: (response: any) => callbackFired('onUIEvent', response),
      onFormSubmitted: (response: FormCallbackPayload) =>
        callbackFired('onFormSubmitted', response),
      onDocumentsSubmitted: (response: FormCallbackPayload) =>
        callbackFired('onDocumentsSubmitted', response),
    };

    ArgyleLink.start(config);
  };

  return (
    <SafeAreaView style={styles.background}>
      <Text style={styles.welcome}>📱️ Argyle Link SDK️</Text>
      <TouchableOpacity testID="start" onPress={start}>
        <Text style={styles.welcome}>Start</Text>
      </TouchableOpacity>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  background: {
    backgroundColor: Colors.lighter,
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
});
