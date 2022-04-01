import React, {Component} from 'react';
import Locations from './components/locations'

class App extends Component {
  state = {
    locations: []
  }

  componentDidMount() {
    fetch('/location/driver')
        .then(res => res.json())
        .then((data) => {
          this.setState({ locations: data })
        })
        .catch(console.log)
  }

  render () {
    return (
        <Locations locations={this.state.locations} />
    );
  }
}

export default App;
