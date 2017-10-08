import React from 'react';
import { Button } from 'antd';
import styles from './CaseItem.less';

function CaseItem(props) {
  return (
    <div className={styles.item}>
      <img src={props.imgUrl} alt="" className={styles.img} />
      <div className={styles.main} >
        <div className={styles.title} >
          {props.title}
        </div>
        <div className={styles.gen} >
          <span style={{ marginRight: '100px' }} >{props.readNum}</span> <span>{props.updateTime}</span>
        </div>
        <div className={styles.info} >
          {props.info}
        </div>
      </div>
      <div className={styles.btn} >
        {
          props.isProvide ? <Button size="large" type="primary" ghost >去看看</Button> : <Button size="large" disabled ghost >未提供地址</Button>
        }
      </div>
    </div>
  );
}

CaseItem.propTypes = {};

export default CaseItem;
