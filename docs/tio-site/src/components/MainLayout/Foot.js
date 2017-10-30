import React from 'react';
import { Link } from 'dva/router';
import styles from './Foot.less';
import wechat from '../../assets/wechat.jpg';

function Foot() {
  return (
    <div className={styles.container}>
      <div className={styles.footer}>
        <div className={styles.guild}>
          <Link to="/404">开源项目</Link>
          <Link to="/404">学习步骤</Link>
          <Link to="/404">众说t-io</Link>
          <Link to="/404">授权协议</Link>
          <Link to="/donation">捐赠</Link>
        </div>
        <div className={styles.copyright}>
          <span
            style={{
              marginRight: '13px',
            }}
          >
            © 2017 t-io
          </span>浙ICP备17032976号
        </div>
        <div className={styles.wechat} >
          <img src={wechat} alt="" />
          <div className={styles.title} >
            t-io公众号
          </div>
        </div>
      </div>
    </div>
  );
}

Foot.propTypes = {};

export default Foot;
