import React from 'react';
import { Pagination } from 'antd';
import classnames from 'classnames';
import { connect } from 'dva';
import styles from './index.less';
import DonationItem from './DonationItem';
import { donationPage } from '../../services/donation';

class DonationPage extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      pageNumber: 1,
      pageSize: 5,
      totalRow: 0,
      list: [],
    };
  }
  componentWillMount() {
    this.queryDonationPage(this.state.pageNumber, this.state.pageSize);
  }
  async queryDonationPage(page, pageSize) {
    const { data } = await donationPage(page, pageSize);
    this.setState(data);
  }

  render() {
    return (
      <div className={styles.container}>
        <div className={styles.top}>
          <div className={styles.total} >
            <div className={styles.totalTitle}>
              情怀捐赠
            </div>
            <div className={styles.totalMonery}>
              收到<span>2986</span>元捐赠
            </div>
          </div>
          <div className={styles.qrcode} >
            <img src="ali_300px-11.png" alt="" />
            <img src="wechat_300px-11.png" alt="" />
          </div>
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
          {
          this.state.list.map((item) => {
            return <DonationItem key={item.id} {...item} />;
          })
          }
          <Pagination
            style={{ textAlign: 'right', padding: '22px' }}
            showSizeChanger
            showQuickJumper
            current={this.state.pageNumber}
            pageSize={this.state.pageSize}
            total={this.state.totalRow}
            onChange={this.queryDonationPage.bind(this)}
            onShowSizeChange={this.queryDonationPage.bind(this)}
          />
        </div>
      </div>
    );
  }

}

DonationPage.propTypes = {};

export default connect()(DonationPage);
