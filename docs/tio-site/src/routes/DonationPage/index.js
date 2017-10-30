import React from 'react';
// import { Pagination } from 'antd';
import classnames from 'classnames';
import { connect } from 'dva';
import styles from './index.less';
import DonationItem from './DonationItem';


class DonationPage extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      pageNumber: 1,
      pageSize: 10,
      totalRow: 100,
      list: [],
    };
  }
  componentWillMount() {

  }

  render() {
    return (
      <div className={styles.container}>
        <div className={styles.top}>
          头部
        </div>
        <div className={styles.ranking} >
          <div className={classnames({ [styles.rankingItem]: true, [styles.one]: true })} >
            <div className={styles.icon} />
            <div className={styles.info} >
              <div className={styles.name}>闲心</div>
              <div className={styles.donation}>总捐赠：768元</div>
            </div>
          </div>
          <div className={classnames({ [styles.rankingItem]: true, [styles.two]: true })} >
            <div className={styles.icon} />
            <div className={styles.info} >
              <div className={styles.name}>YY守护天使YY</div>
              <div className={styles.donation}>总捐赠：365元</div>
            </div>
          </div>
          <div className={classnames({ [styles.rankingItem]: true, [styles.three]: true })}>
            <div className={styles.icon} />
            <div className={styles.info} >
              <div className={styles.name}>贝密游戏</div>
              <div className={styles.donation}>总捐赠：200元</div>
            </div>
          </div>
        </div>
        <div className={styles.donationlist} >
          <DonationItem />
        </div>
      </div>
    );
  }

}

DonationPage.propTypes = {};

export default connect()(DonationPage);
