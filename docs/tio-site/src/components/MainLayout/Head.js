import React from 'react';
import { NavLink, Link } from 'dva/router';
import styles from './Head.less';

import logo from '../../assets/logo.jpg';

function Head() {
  return (
    <div className={styles.main}>
      <div className={styles.header}>
        <img alt="" src={logo} className={styles.logo} />
        <div className={styles.topnav}>
          <NavLink to="/" exact activeClassName={styles.active}>首页</NavLink>
          <NavLink to="/case" activeClassName={styles.active}>案例</NavLink>
          <NavLink to="/doc" activeClassName={styles.active}>文档</NavLink>
          <NavLink to="/qa" activeClassName={styles.active}>提问</NavLink>
          <NavLink to="/donation" activeClassName={styles.active}>捐赠</NavLink>
        </div>
        
        
      </div>
    </div>
  );
}

Head.propTypes = {};

export default Head;
