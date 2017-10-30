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
          <NavLink to="/a" activeClassName={styles.active}>应用</NavLink>
          <NavLink to="/b" activeClassName={styles.active}>生态</NavLink>
          <NavLink to="/c" activeClassName={styles.active}>贡献</NavLink>
          <NavLink to="/d" activeClassName={styles.active}>提问</NavLink>
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
