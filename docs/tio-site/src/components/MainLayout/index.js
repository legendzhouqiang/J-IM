import React from 'react';
import styles from './index.less';
import Head from './Head';
import Foot from './Foot';

function MainLayout(props) {
  return (
    <div className={styles.normal}>
      <Head />
      <div className={styles.main}>
        {props.children}
      </div>
      <Foot />
    </div>
  );
}

MainLayout.propTypes = {};

export default MainLayout;
