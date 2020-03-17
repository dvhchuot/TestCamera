//  Created by react-native-create-bridge

import React, {Component} from 'react';
import {requireNativeComponent} from 'react-native';

const CapichiCamera = requireNativeComponent(
  'CapichiCamera',
  CapichiCameraView,
);

export default class CapichiCameraView extends Component {
  onDone = e => {
    console.log('TCL: CapichiCameraView -> e', e.nativeEvent);
    const {url} = e.nativeEvent;
    const {navigation} = this.props;
    navigation.push('Video', {
      param: {
        url: `file://${url}`,
      },
    });
  };
  render() {
    return (
      <CapichiCamera
        {...this.props}
        style={{backgroundColor: 'red', flex: 1}}
        onDone={this.onDone}
      />
    );
  }
}

CapichiCameraView.propTypes = {
  // exampleProp: React.PropTypes.string,
};
