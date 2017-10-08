import React from 'react';
import { connect } from 'dva';
import styles from './index.less';
import CaseItem from './CaseItem';

const json = [
  {
    title: '某省移动公司CRM系统业务',
    readNum: 20000,
    updateTime: '2017-08-03 14:24:17',
    info: `上线的项目服务器用的是tio框架，作为server端与温控设备（客户端）进行socket通讯，客户端是客户那边的硬件
    设备，有自己的协议实现，所以没有用到tio，tio帮我实现了自动重连和心跳检测...`,
  },
  {
    title: '某省移动公司CRM系统业务',
    readNum: 20200,
    updateTime: '2017-08-03 14:24:17',
    info: `上线的项目服务器用的是tio框架，作为server端与温控设备（客户端）进行socket通讯，客户端是客户那边的硬件
    设备，有自己的协议实现，所以没有用到tio，tio帮我实现了自动重连和心跳检测...`,
    isProvide: true,
  },
  {
    title: '某省移动公司CRM系统业务',
    readNum: 10000,
    updateTime: '2017-08-03 14:24:17',
    info: '上线的项目服务器用的是tio框架，作为server端与温控设备（客户端）进行socket通讯',
  },
];
class CasePage extends React.Component {

  render() {
    return (
      <div className={styles.container}>
        <div className={styles.main}>
          {
            json.map((item) => {
              return <CaseItem {...item} />;
            })
          }
        </div>
      </div>
    );
  }

}

CasePage.propTypes = {};

export default connect()(CasePage);
