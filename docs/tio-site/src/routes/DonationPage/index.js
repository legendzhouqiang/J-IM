import React from 'react';
import { Pagination } from 'antd';
import classnames from 'classnames';
import { connect } from 'dva';
import styles from './index.less';
import DonationItem from './DonationItem';
import { totalAmount, donationPage, selectDonateTitle } from '../../services/donation';
import logo from '../../assets/logo.jpg';

class DonationPage extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      pageNumber: 1,
      pageSize: 3,
      totalRow: 0,
      list: [],
      donateTitles: [{}, {}, {}],
      donateTitleIndex: 1
    };
    console.log("constructor");
    this.queryDonationPage(this.state.pageNumber, this.state.pageSize);
  }

  componentWillMount() {
    console.log("componentWillMount");

    setInterval( () => {
      // let yy = selectDonateTitle(this.state.donateTitleIndex++);
      // this.state.donateTitles = yy.data;
    }, 5000);

  }



  async queryDonationPage(pageNumber, pageSize) {
    const { data } = await donationPage(pageNumber, pageSize);

    this.setState(data);

    let xx = await totalAmount();
    this.state.totalAmount = xx.data;

    let yy = await selectDonateTitle(this.state.donateTitleIndex++);
    this.state.donateTitles = yy.data;


    this.setState(data);

    console.log(this.state.donateTitles[0].link);





  }

  render() {
    return (
      <div className={styles.container}>

        <div className={styles.top}>
          <div className={styles.total} >
            <div className={styles.totalTitle}>
              开源情怀，不以数计
            </div>
            <div className={styles.totalMonery}>
              已收到<span>{this.state.totalRow}</span>笔捐赠，合计<span>{this.state.totalAmount}</span>RMB
            </div>
          </div>
          <div className={styles.qrcode} >
            <img src="ali_300px-1.png" alt="" />
            <img src="wechat_300px-1.png" alt="" />
          </div>
        </div>
        <div className={styles.ranking} >
          <div className={classnames({ [styles.rankingItem]: true, [styles.one]: true })} >
            <a href={this.state.donateTitles[0].link} target="_blank"><div className={styles.icon} ><img src={this.state.donateTitles[0].avatar} style={{ width: '100%', height: '100%', borderRadius: '50%' }} /></div></a>
            <div className={styles.info} >
              <div className={styles.name}>{this.state.donateTitles[0].name}</div>
              <div className={styles.donation}>{this.state.donateTitles[0].text}</div>
            </div>
          </div>

          <div className={classnames({ [styles.rankingItem]: true, [styles.three]: true })} >
            <a href={this.state.donateTitles[1].link} target="_blank"><div className={styles.icon} ><img src={this.state.donateTitles[1].avatar} style={{ width: '100%', height: '100%', borderRadius: '50%' }} /></div></a>
            <div className={styles.info} >
              <div className={styles.name}>{this.state.donateTitles[1].name}</div>
              <div className={styles.donation}>{this.state.donateTitles[1].text}</div>
            </div>
          </div>

          <div className={classnames({ [styles.rankingItem]: true, [styles.two]: true })} >
            <a href={this.state.donateTitles[2].link} target="_blank"><div className={styles.icon} ><img src={this.state.donateTitles[2].avatar} style={{ width: '100%', height: '100%', borderRadius: '50%' }} /></div></a>
            <div className={styles.info} >
              <div className={styles.name}>{this.state.donateTitles[2].name}</div>
              <div className={styles.donation}>{this.state.donateTitles[2].text}</div>
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
