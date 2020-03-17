import {createAppContainer} from 'react-navigation';
import {createStackNavigator} from 'react-navigation-stack';
import CapichiCameraView from './screen/Camera';
import Video from './screen/Video';

const Stack = createStackNavigator(
  {
    Camera: {
      screen: CapichiCameraView,
    },
    Video: {
      screen: Video,
    },
  },
  {
    headerMode: 'none',
  },
);

const Navigator = createAppContainer(Stack);

export default Navigator;
