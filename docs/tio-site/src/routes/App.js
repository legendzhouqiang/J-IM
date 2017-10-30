import React from 'react';
import { Route, Switch } from 'dva/router';
import styles from './App.less';
import IndexPage from './IndexPage';
import CasePage from './CasePage/index';
import MainLayout from './../components/MainLayout';
import DonationPage from './DonationPage/index';

class App extends React.Component {
  render() {
    return (
      <div className={styles.container}>
        <MainLayout>
          <Switch>
            <Route path="/" exact component={IndexPage} />
            <Route path="/case" exact component={CasePage} />
            <Route path="/donation" exact component={DonationPage} />
          </Switch>
        </MainLayout>
      </div>
    );
  }
}

export default App;
