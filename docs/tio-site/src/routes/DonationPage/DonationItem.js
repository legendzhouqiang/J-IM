import React from 'react';
import styles from './DonationItem.less';

function DonationItem(props) {
  return (
    <div className={styles.item}>
      <div className={styles.icon} />
      <div className={styles.info} >
        <div>
          <span className={styles.name} >{props.name}</span>
          <span className={styles.website} >{props.url ? <a href={props.url} target='_blank'>  {props.url}  </a> : ''}</span>
        </div>
        <div className={styles.message}>
          {props.leavemsg ? `“${props.leavemsg}”` : <span>人好话不多，这位侠客什么都没留下</span>}
        </div>
      </div>
      <div className={styles.donation}>
        <span className={styles.payicon} />
        <span className={styles.monery} >
          {props.amount}缘
        </span>
      </div>
      <div className={styles.more}>
        <div className={styles.time} >
          {props.time}
        </div>
        <div className={styles.tags} >
          <span>待完善</span>
        </div>
      </div>
    </div>
  );
}

DonationItem.propTypes = {};

export default DonationItem;
