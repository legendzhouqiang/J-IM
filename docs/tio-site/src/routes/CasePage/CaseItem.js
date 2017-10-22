import React from 'react';
import { Button } from 'antd';
import styles from './CaseItem.less';

function CaseItem(props) {
  const clickHandle = () => {
    window.open(props.caseurl);
  };
  return (
    <div className={styles.item}>
      <img src={props.caseimg} alt="" className={styles.img} />
      <div className={styles.main} >
        <div className={styles.title} >
          {props.casename}
        </div>
        <div className={styles.gen} >
          <span style={{ marginRight: '100px' }} >{props.personqq}</span> <span>{props.provideddate}</span>
        </div>
        <div className={styles.info} >
          {props.caseintro}
        </div>
      </div>
      <div className={styles.btn} >
        {
          props.caseurl ? <Button size="large" type="primary" ghost onClick={clickHandle} >去看看</Button> : <Button size="large" disabled ghost >未提供地址</Button>
        }
      </div>
    </div>
  );
}

CaseItem.propTypes = {};

export default CaseItem;
