//  Created by react-native-create-bridge

import React, {Component} from 'react';
import {requireNativeComponent} from 'react-native';

const CapichiCamera = requireNativeComponent(
  'CapichiCamera',
  CapichiCameraView,
);

export default class CapichiCameraView extends Component {
  render() {
    return (
      <CapichiCamera
        {...this.props}
        style={{backgroundColor: 'red', flex: 1}}
        onDone={e => console.log('done', e.nativeEvent)}
      />
    );
  }
}

CapichiCameraView.propTypes = {
  // exampleProp: React.PropTypes.string,
};
