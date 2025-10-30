import React from 'react';
import {
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
  NativeModules,
  NativeEventEmitter,
  requireNativeComponent,
  InteractionManager,
} from 'react-native';
const {SudMGPPlugin} = NativeModules;
// Require the native component
const SudMGPPluginView = requireNativeComponent('SudMGPPluginView'); // Updated name
const eventEmitter = new NativeEventEmitter(SudMGPPlugin);

const USER_ID = '1234';
const APP_ID = '1461564080052506636';
const APP_KEY = '03pNxK2lEXsKiiwrBQ9GbH541Fk2Sfnc';

interface GameGetCodeListener {
  onSuccess(code: string): void;

  onFailed(): void;
}

async function getCode(
  userId: string,
  appId: string,
  listener: GameGetCodeListener,
): Promise<void> {
  // Note: The following code is for demonstration purposes only.
  // You should replace it with your actual implementation to retrieve the code.
  // For example, you might need to make a network request to your server to get the code.
  //

  const url = 'https://mgp-hello.sudden.ltd/login/v3';

  // Create the request payload
  const reqJsonObj = {
    user_id: userId,
  };
  try {
    // Make the fetch request
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(reqJsonObj),
    });
    // Check if the response is ok (status in the range 200-299)
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    // Parse the JSON response
    const dataJson = await response.json();
    const ret_code = dataJson.ret_code;
    const code = dataJson.data.code;
    // Check response code and handle accordingly
    if (ret_code === 0) {
      listener.onSuccess(code);
    } else {
      listener.onFailed();
    }
  } catch (error) {
    console.error('Error:', error);
    listener.onFailed();
  }
}

function App(): React.JSX.Element {
  // Subscribe to the event
  eventEmitter.addListener('onGetCode', message => {
    const {appId, userId} = message;
    console.log('rect-native: userId' + userId + ' appId:', appId);

    getCode(userId, appId, {
      onSuccess: code => {
        console.log('Success:', code);

        SudMGPPlugin.updateCode(
          JSON.stringify({
            code: code,
          }),
        );
      },
      onFailed: () => {
        SudMGPPlugin.updateCode(
          JSON.stringify({
            code: '',
          }),
        );
        console.error('Failed to retrieve code');
      },
    });
  });

  // game notify app state changed
  eventEmitter.addListener('onGameStateChanged', message => {
    const {state, dataJson} = message;
    console.log('rect-native: state' + state + ' dataJson:', dataJson);
  });

  const onLoadGameClick = async () => {
    console.log('Load Game', 'onLoadGameClick');

    getCode(USER_ID, APP_ID, {
      onSuccess: code => {
        console.log('Success:', code);

        handleLoadGame(code);
      },
      onFailed: () => {
        console.error('Failed to retrieve code');
      },
    });
  };
  // Function to handle Load Game button press
  const handleLoadGame = async (code: string) => {
    console.log('Load Game', 'Game is loading...');

    console.log('main thread');
    try {
      const sudGameLoadConfig = {
        appId: APP_ID, // Replace with actual app ID
        appKey: APP_KEY, // Replace with actual app key
        isTestEnv: true, // Set to true for testing, false for production
        gameId: '1461228410184400899', // Replace with actual game ID
        roomId: '1234', // Replace with actual room ID
        userId: USER_ID, // Replace with actual user ID
        language: 'en-US', // Set to desired language
        authorizationSecret: '', // Replace with actual secret if needed
        code: code, // same with onGetCode,Set the code you if you have already got it,if set it to null, it will call onGetCode to get the code
      };

      const gameRect = {
        left: 0,
        right: 0,
        top: 100,
        bottom: 200,
      };
      // setup game view rect
      SudMGPPlugin.configGameRect(JSON.stringify(gameRect));

      const response = SudMGPPlugin.loadGame(JSON.stringify(sudGameLoadConfig));
      console.log('Load Game', response);
    } catch (error) {
      console.error('Error loading game:', error);
    }

    // Add your game loading logic here
  };

  // Function to handle Close Game button press
  const onCloseGameClick = async () => {
    console.log('Close Game', 'Game is closing...');
    // Add your game closing logic here
    try {
      const response = await SudMGPPlugin.destroyGame();
      console.log('Close Game', response);
    } catch (error) {
      console.error('Error closing game:', error);
    }
  };

  const onJoinGameClick = async () => {
    // this just show how to use the game state change event, you can remove it if you don't need it
    sendGameStateChange(
      'app_common_self_in',
      JSON.stringify({
        isIn: true,
        seatIndex: -1,
        isSeatRandom: 1,
      }),
    );
  };

  const sendGameStateChange = async (state: string, dataJson: string) => {
    console.log('sendGameStateChange', 'Game is sending game state change...');
    try {
      const data = {
        state: state,
        dataJson: dataJson,
      };

      const response = await SudMGPPlugin.notifyStateChange(
        JSON.stringify(data),
      );
      console.log('sendGameStateChange', response);
    } catch (error) {
      console.error('Error sendGameStateChange:', error);
    }
  };

  return (
    <View style={styles.container}>
      <SudMGPPluginView style={styles.gameView} />
      <View style={styles.buttonContainer}>
        <TouchableOpacity style={styles.button} onPress={onLoadGameClick}>
          <Text style={styles.buttonText}>LoadGame</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.button} onPress={onCloseGameClick}>
          <Text style={styles.buttonText}>DestroyGame</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.button} onPress={onJoinGameClick}>
          <Text style={styles.buttonText}>TestJoin</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  webview: {
    width: '10%',
    height: '10%',
  },
  container: {
    flex: 1,
  },
  gameView: {
    flex: 1,
    backgroundColor: 'red',
  },
  buttonContainer: {
    position: 'absolute', // Position the buttons absolutely
    bottom: 20, // Space from the bottom
    left: 0,
    right: 0,
    flexDirection: 'row',
    justifyContent: 'space-around',
  },
  button: {
    backgroundColor: 'orange',
    padding: 15,
    borderRadius: 5,
    width: 'auto',
    alignItems: 'center',
  },
  buttonText: {
    color: 'white',
    fontSize: 16,
  },
});

export default App;
