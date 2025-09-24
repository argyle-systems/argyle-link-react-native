import React, {useState} from 'react';
import Clipboard from '@react-native-clipboard/clipboard';
import {
  SafeAreaView,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
} from 'react-native';
import {AccountCallbackPayload, ArgyleLink, FormCallbackPayload} from '@argyleio/argyle-plugin-react-native';

const parseJsonConfig = (jsonConfig: any) => {
  const parsed = JSON.parse(jsonConfig);
  return Object.entries(parsed).reduce(
    (result, [key, value]) => ({
      ...result,
      [key]: value,
    }),
    {},
  );
};

function TestConfiguratorApp(): JSX.Element {
  const [jsonConfig, setJsonConfig] = useState('');
  const [callbackLog, setCallbackLog] = useState('Callback Log');

  const callbackFired = (key: string, response: any = {}) => {
    console.log('callback fired', key, response);
    setCallbackLog(
      callbackLog => callbackLog + '\n' + key + '\n' + JSON.stringify(response),
    );
  };

  const start = () => {
    const parsedConfig = parseJsonConfig(jsonConfig);
    const config = {
      sandbox: true,
      ...parsedConfig,
      onCantFindItemClicked: parsedConfig.onCantFindItemClicked
        ? (response: string) => callbackFired('onCantFindItemClicked', response)
        : undefined,
      onAccountCreated: (response: AccountCallbackPayload) => callbackFired('onAccountCreated', response),
      onAccountConnected: (response: AccountCallbackPayload) =>
        callbackFired('onAccountConnected', response),
      onAccountRemoved: (response: AccountCallbackPayload) => callbackFired('onAccountRemoved', response),
      onAccountError: (response: AccountCallbackPayload) => callbackFired('onAccountError', response),
      onDDSSuccess: (response: AccountCallbackPayload) => callbackFired('onDDSSuccess', response),
      onDDSError: (response: AccountCallbackPayload) => callbackFired('onDDSError', response),
      onClose: () => callbackFired('onClose'),
      onExitIntroClicked: () => callbackFired('onExitIntroClicked'),
      onError: (response: any) => callbackFired('onError', response),
      onTokenExpired: (updateToken: any) => {
        // updateToken('<token>')
        callbackFired('onTokenExpired', updateToken);
      },
      onUIEvent: (response: any) => callbackFired('onUIEvent', response),
      onFormSubmitted: (response: FormCallbackPayload) => callbackFired('onFormSubmitted', response),
      onDocumentsSubmitted: (response: FormCallbackPayload) =>
        callbackFired('onDocumentsSubmitted', response),
    };

    ArgyleLink.start(config);
  };

  const clear = () => {
    setJsonConfig('');
  };

  const paste = async () => {
    const text = await Clipboard.getString();
    setJsonConfig(text);
  };

  return (
    <SafeAreaView style={styles.background}>
      <Text style={styles.welcome}>📱️ Argyle Link SDK️</Text>
      <TextInput
        multiline
        testID="json-input"
        onChangeText={text => setJsonConfig(text)}
        value={jsonConfig}
        style={{backgroundColor: 'teal', width: '100%', height: 200}}
      />
      <TouchableOpacity testID="start" onPress={start}>
        <Text style={styles.welcome}>Start</Text>
      </TouchableOpacity>
      <TouchableOpacity testID="clear" onPress={clear}>
        <Text style={styles.welcome}>Clear</Text>
      </TouchableOpacity>
      <TouchableOpacity testID="paste" onPress={paste}>
        <Text style={styles.welcome}>Paste from clipboard</Text>
      </TouchableOpacity>
      <ScrollView style={styles.callbackContainer}>
        <Text style={styles.callbacks} testID="callback-log">
          {callbackLog}
        </Text>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  background: {},
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
  callbackContainer: {
    width: '100%',
    backgroundColor: 'white',
    padding: 20,
    height: 300,
  },
  callbacks: {
    textAlign: 'left',
    color: '#333333',
    marginTop: 5,
  },
});

export default TestConfiguratorApp;
