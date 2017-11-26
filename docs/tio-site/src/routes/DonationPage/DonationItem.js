import React from 'react';
import styles from './DonationItem.less';

function formatCoin(waytype) {
  switch (waytype) {
    case 1:
      { return '码'; }
    case 2:
      { return '微'; }
    case 3:
      { return '支'; }
    case 4:
      { return '红'; }
    default:
      { return '未'; }
  }
}

function DonationItem(props) {
  return (
    <div className={styles.item}>
      <div className={styles.icon} >
        <img alt src={props.avatar ? props.avatar : '/img/1.png'} style={{ width: '100%', height: '100%', borderRadius: '50%' }} />
      </div>
      <div className={styles.info} >
        <div>
          <span className={styles.name} >{props.name}</span>
          <span className={styles.website} >{props.url ? <a href={props.url} target="_blank"> {props.url} </a> : ''}</span>
        </div>
        <div className={styles.message}>
          {props.leavemsg ? `“${props.leavemsg}”` : <span>人好话不多，这位侠客什么都没留下</span>}
        </div>
      </div>
      <div className={styles.donation}>
        <span className={styles.payicon} title={props.way} >
          {formatCoin(props.waytype)}
        </span>
        <span className={styles.monery} >
          {props.amount}
        </span>
      </div>
      <div className={styles.more}>
        <div className={styles.time} >
          {props.time}
        </div>
        <div className={styles.tags} >
          {props.tag1 ? <span>{props.tag1}</span> : ''}
          {props.tag2 ? <span>{props.tag2}</span> : ''}
          {props.tag3 ? <span>{props.tag3}</span> : ''}
        </div>
      </div>
    </div>
  );
}

DonationItem.propTypes = {};

export default DonationItem;
