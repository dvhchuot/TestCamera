import React, {Component} from 'react';
import {Text, View} from 'react-native';
import Proptypes from 'prop-types';
import Video from 'react-native-video';

class VideoS extends Component {
  constructor(props) {
    super(props);
    const param = props.navigation.getParam('param', {});
    this.state = {
      url: param.url,
    };
  }

  render() {
    const {url} = this.state;
    return <Video source={{uri: url}} style={{flex: 1}} repeat />;
  }
}

VideoS.propTypes = {};

export default VideoS;
