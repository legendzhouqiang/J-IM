import React from 'react';
import styles from './DonationItem.less';

function DonationItem() {
  return (
    <div className={styles.item}>
      <div className={styles.icon} />
      <div className={styles.info} >
        <div>
          <span className={styles.name} >YY守护天使YY</span>
          <span className={styles.website} >(https://git.oschina.net/yyljlyy)</span>
        </div>
        <div className={styles.message}>
          ”框架写得太霸道了！怒赞。读得我心潮澎湃，久违的感觉。“
        </div>
      </div>
      <div className={styles.donation}>
        <span className={styles.payicon} />
        <span className={styles.monery} >
        188元
        </span>
      </div>
      <div className={styles.more}>
        <div className={styles.time} >
        2017-03-26 09:33:48
        </div>
        <div className={styles.tags} >
        标签
        </div>
      </div>
    </div>
  );
}

DonationItem.propTypes = {};

export default DonationItem;
