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
              "傻"人
            </div>
            <div className={styles.totalMonery}>
              共收到<span>55</span>笔捐赠
            </div>
          </div>
          <div className={styles.qrcode} >
            <img src="ali_300px-1.png" alt="" />
            <img src="wechat_300px-1.png" alt="" />
          </div>
        </div>
        <div className={styles.ranking} >
          <div className={classnames({ [styles.rankingItem]: true, [styles.one]: true })} >
          <a href="https://gitee.com/sentsin/layui" target="_blank"><div className={styles.icon} /></a>
            <div className={styles.info} >
              <div className={styles.name}>贤心（layui作者）</div>
              <div className={styles.donation}>感谢对t-io的支持</div>
            </div>
          </div>
          
          <div className={classnames({ [styles.rankingItem]: true, [styles.three]: true })}>
          <a href="https://gitee.com/beimigame/beimi" target="_blank"><div className={styles.icon} /></a>
            <div className={styles.info} >
              <div className={styles.name}>贝密游戏&优客服作者</div>
              <div className={styles.donation}>感谢对t-io的支持</div>
            </div>
          </div>
          <div className={classnames({ [styles.rankingItem]: true, [styles.two]: true })} >
          <a href="https://gitee.com/loolly/hutool" target="_blank"><div className={styles.icon} /></a>
            <div className={styles.info} >
              <div className={styles.name}>路小磊(hutool作者)</div>
              <div className={styles.donation}>感谢对t-io的支持</div>
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
