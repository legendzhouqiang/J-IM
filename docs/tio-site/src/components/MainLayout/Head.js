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
          <a href="/doc/index.html" target='_blank'>文档</a>
          <a href="https://www.oschina.net/question/tag/t-io" target='_blank'>提问</a>
          <NavLink to="/donation" activeClassName={styles.active}>捐赠</NavLink>
        </div>
        
        <div className={styles.userInfo} >
          <Link to="/login" >登录</Link>
          <Link to="/register" >注册</Link>
        </div>
      </div>
    </div>
  );
}

Head.propTypes = {};

export default Head;
