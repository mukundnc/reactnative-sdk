import React from 'react';
import { AppRegistry, StyleSheet, Text, View, Button, Alert, NativeModules } from 'react-native';
import { BleManager } from 'react-native-ble-plx';

let controller = NativeModules.Controler;

export default class App extends React.Component {

  constructor(props){
    super(props);
    this.state = {
      showDevices: false,
      devices: []
    };
    this.manager = new BleManager();
  }

  componentWillMount() {
    const subscription = this.manager.onStateChange((state) => {
        if (state === 'PoweredOn') {
            this.scanAndConnect();
            subscription.remove();
        }
    }, true);
  }
  
  scanAndConnect() {
    var devices = new Array();
    this.manager.startDeviceScan(null, null, (error, device) => {
        if (error) {
            // Handle error (scanning will be stopped automatically)
            return
        }

        devices.push(device);
        this.setState({devices: devices});
        // Check if it is a device you are looking for based on advertisement data
        // or other criteria.
        // if (device.name === 'TI BLE Sensor Tag' || 
        //     device.name === 'SensorTag') {
            
        
            // Stop scanning as it's not necessary if you are scanning for one device.
            //this.manager.stopDeviceScan();

            // Proceed with connection.
        // }
    });
}
  
  onSearchDevices(){
    this.setState({showDevices:true});
    this.scanAndConnect();
  }

  onBack(){
    this.setState({showDevices:false});
  }

  showRootPage(){
    return (
      <View style={styles.container}>
        <Text>Open up App.js to start working on your app!</Text>
        <Text>Changes you make will automatically reload.</Text>
        <Text>Shake your phone to open the developer menu.</Text>
        <Button
          onPress={this.onSearchDevices.bind(this)}
          title="SearchDevices"
          color="#841584"
          accessibilityLabel="How can you do this to me"
        />
      </View>
    );
  }

  showBTDevices(){
    return (
      <View style={styles.container}>
        <Text>Devices1</Text>
        {this.state.devices.map((dev)=><Text>dev.name</Text>)}
        <Text>{controller.toString()}</Text>
        <Button
          onPress={this.onBack.bind(this)}
          title="Back"
          color="#841584"
          accessibilityLabel="How can you do this to me"
        />
      </View>
    );
  }

  render() {
    let html = '';
    if(!this.state.showDevices)
      html = this.showRootPage();
    else
      html = this.showBTDevices();
    return html;
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});

AppRegistry.registerComponent('App', () => App);